/*
 * __________
 * CREDITS
 * __________
 *
 * Team page: http://isatab.sf.net/
 * - Marco Brandizi (software engineer: ISAvalidator, ISAconverter, BII data management utility, BII model)
 * - Eamonn Maguire (software engineer: ISAcreator, ISAcreator configurator, ISAvalidator, ISAconverter,  BII data management utility, BII web)
 * - Nataliya Sklyar (software engineer: BII web application, BII model,  BII data management utility)
 * - Philippe Rocca-Serra (technical coordinator: user requirements and standards compliance for ISA software, ISA-tab format specification, BII model, ISAcreator wizard, ontology)
 * - Susanna-Assunta Sansone (coordinator: ISA infrastructure design, standards compliance, ISA-tab format specification, BII model, funds raising)
 *
 * Contributors:
 * - Manon Delahaye (ISA team trainee:  BII web services)
 * - Richard Evans (ISA team trainee: rISAtab)
 *
 *
 * ______________________
 * Contacts and Feedback:
 * ______________________
 *
 * Project overview: http://isatab.sourceforge.net/
 *
 * To follow general discussion: isatab-devel@list.sourceforge.net
 * To contact the developers: isatools@googlegroups.com
 *
 * To report bugs: http://sourceforge.net/tracker/?group_id=215183&atid=1032649
 * To request enhancements:  http://sourceforge.net/tracker/?group_id=215183&atid=1032652
 *
 *
 * __________
 * License:
 * __________
 *
 * This work is licenced under the Creative Commons Attribution-Share Alike 2.0 UK: England & Wales License. To view a copy of this licence, visit http://creativecommons.org/licenses/by-sa/2.0/uk/ or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA.
 *
 * __________
 * Sponsors
 * __________
 * This work has been funded mainly by the EU Carcinogenomics (http://www.carcinogenomics.eu) [PL 037712] and in part by the
 * EU NuGO [NoE 503630](http://www.nugo.org/everyone) projects and in part by EMBL-EBI.
 */
 
package uk.ac.ebi.bioinvindex.unloading;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import uk.ac.ebi.bioinvindex.dao.IdentifiableDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Accessible;
import uk.ac.ebi.bioinvindex.model.Identifiable;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.utils.reflection.ReflectionUtils;

/**
 * The unloaders are components used by the {@link UnloadManager}. Basically every unloader manages its own object type 
 * and knows how to schedule an object for deletion (i.e.: it decides which other depending objects to delete or if the 
 * object should be kept because of dependants) and later manages the deletion operation.  
 * 
 * @author brandizi
 * <b>date</b>: Nov 5, 2009
 * 
 */
public abstract class AbstractUnloader<T extends Identifiable>
{
	/** 
	 * We log some messages (e.g.: "#X deleted" with this priority), you may want to change it for important objects
	 * and leave it low for others. 
	 *  
	 */
	protected Priority logLevel = Level.TRACE;

	private Class<T> _unloadedClass;

	/**
	 * Available in all the persister for managing the DB operations
	 */
	protected IdentifiableDAO<? super T> dao;
	
	/**
	 * Unloaders work together with the manager.
	 */
	protected final UnloadManager unloadManager;

	protected final static Logger log = Logger.getLogger ( AbstractUnloader.class );

	/**
	 * tells all the associations of a class that have to be considered during deletion. The class is used in 
	 * {@link AbstractUnloader#getReferringAssociations()}. 
	 *  
	 * @author brandizi
	 * <b>date</b>: Nov 5, 2009
	 *
	 */
	public static class Association
	{
		public final Class<? extends Identifiable> dependingType;
		public final String dependingAssociation;

		public <T extends Identifiable> Association ( Class<T> dependingType, String dependingAssociation ) {
			this.dependingType = dependingType;
			this.dependingAssociation = dependingAssociation;
		}
	}
	
	/**
	 * The unloader works together with an {@link UnloadManager}.
	 * 
	 */
	public AbstractUnloader ( UnloadManager unloadManager )
	{
		this.unloadManager = unloadManager;
		dao = unloadManager.getDaoFactory ().getIdentifiableDAO ( getUnloadedClass () );
	}
	
	/**
	 * This method is supposed to use the {@link UnloadManager} (e.g.: queueSimple()) to schedule the deletion of the 
	 * parameter object and other objects that depends on the parameter and need to be deleted on cascade. 
	 * 
	 */
	public boolean queue ( T object ) {
		return unloadManager.queueSimple ( getUnloadedClass (), object );
	}

	/**
	 * Calls {@link #queue(Identifiable)} for all parameter objects. 
	 * 
	 */
	public boolean queueAll ( Collection<T> objects ) 
	{
		if ( objects == null || objects.isEmpty () ) return false;
		boolean result = false;

		for ( T object: objects ) result |= queue ( object );
		return result;
	}

	/**
	 * Calls {@link #queueAll(Collection)} with all objects having the time stamp recorded in {@link #unloadManager}
	 */
	public boolean queueAllByTs () {
		return queueAll ( (List<T>) dao.getBySubmissionTs ( unloadManager.getSubmissionTs () ) );
	} 

	/**
	 * Deletes an object by using its ID. This makes the unloader to search for the object in the DB and 
	 * call {@link #queue(Identifiable)}. This is necessary because the real job is done in that method. 
	 * 
	 *
	 */
	@SuppressWarnings("unchecked")
	public boolean queueById ( Long id ) 
	{
		T object = (T) dao.getById ( id );
		
		if ( object == null ) {
			unloadManager.addMessage ( "object #" + id + "not found, no unloading done for this accession." );
			return false;
		}
		return queue ( object );
	}
	
	/**
	 * This method is supposed to use the {@link UnloadManager} (e.g.: unqueueSimple()) to cancel the deletion of the 
	 * parameter object and other objects that depends on the parameter and need to be deleted/undeleted on cascade. 
	 * 
	 * WARNING: This method can only work if the deletion has not been performed yet. TODO: we need a check for this 
	 * condition.
	 *  
	 */
	public boolean unqueue ( T object ) {
		return unloadManager.unqueueSimple ( getUnloadedClass (), object );
	}

	/**
	 * Calls {@link #unqueue(Identifiable)} for all parameter objects. 
	 * 
	 */
	public boolean unqueueAll ( Collection<T> objects ) 
	{
		if ( objects == null || objects.isEmpty () ) return false;
		boolean result = false;

		for ( T object: objects ) result |= unqueue ( object );
		return result;
	}
	
	/**
	 * Unqueues objects and related objects because the deletion of current object has been canceled. 
	 * This may happen {@link #hasNoReferringEntities(Long)} returns false. 
	 * 
	 */
	protected void cancelDelete ( T object ) {
	}
	
	public boolean deleteAll ()
	{
		Map<Long, T> deletedObjs = unloadManager.getDeletedObjectsByType ( getUnloadedClass () );
		if ( deletedObjs == null ) return false;
		
		boolean result = false;
		for ( Long id: deletedObjs.keySet () )
		{
			T object = deletedObjs.get ( id );
			if ( object != null && this.delete ( object ) ) {
				deletedObjs.put ( id, null );
				result = true;
			}
		}
		return result;
	}

	
	/**
	 * Actually performs the unloading operation, usually by using an appropriate DAO for the object, usually taken from the
	 * unloader's {@link DaoFactory}. The exact workflow is like this: 
	 * 
	 * <ol>
	 * <li>Objects are queued via {@link UnloadManager}</li>
	 * <li>{@link UnloadManager#delete()} is invoked and this decides in which order to call {@link #deleteAll()} for the 
	 *     different unloaders available for the different types to be deleted</li>
	 * <li>this method here is called and...</li>
	 * <li>The deletion actually occurs if {@link #hasNoReferringEntities(Identifiable)} returns true. In this case
	 *     returns true if, the deletion of the object via its DAO successes<li>
	 * <li>otherwise {@link #cancelDelete(Identifiable)} is invoked and false is returned at the end
	 * </ol>
	 * 
	 * @return true if it actually deleted the object
	 * 
	 */
	public boolean delete ( T object ) 
	{
		if ( object == null ) return false; 
		Long id = object.getId ();
		if ( id == null ) return false;
		
		boolean result = false;

		String objStr = null;
		if ( log.isTraceEnabled () || log.isEnabledFor ( logLevel ) ) 
		{ 
			objStr = "<" + object.getClass ().getSimpleName () + ", ";
			if ( object instanceof Accessible )
				objStr += ((Accessible) object).getAcc () + ", " + id;
			else if ( object instanceof OntologyEntry )
				objStr += ((OntologyEntry) object).getAcc () + ", " + id;
			else 
				objStr += id.toString ();
			objStr += ">";
		}
		

		if ( log.isTraceEnabled () )
			log.trace ( "entering the deletion of " + objStr );
			
		if ( hasNoReferringEntities ( id ) ) 
		{ 
			// Doing it by ID prevents "Removing a detached instance" and possibly "already gone" problems
			dao.deleteById ( id );
			result = dao.getById ( id ) == null;
		}
		else
			cancelDelete ( object );
		
		log.trace ( "deletion of " + objStr + " result: " + result );
		
		if ( result && log.isEnabledFor ( logLevel ) )
			log.log ( logLevel, "deletion of " + objStr + " completed" );
		
		return result;
	}



	/** 
	 * The class corresponding to T, extracted by means of reflection.
	 * 
	 * WARNING: This method won't work if the current class still has a generic which overrides T. Declare the class
	 * abstract in such a case and use an anonymous class for clearing the generic.
	 *   
	 */
	public Class<T> getUnloadedClass ()
	{
		if ( _unloadedClass != null ) return _unloadedClass;
		
		_unloadedClass = ReflectionUtils.getTypeArgument ( AbstractUnloader.class, this.getClass(), 0 );
		if ( _unloadedClass == null ) 
			throw new PersistenceException ( "getUnloadedClass() returns null for " + this.getClass ().getSimpleName () );
		return _unloadedClass;
	}
	
	
	/**
	 * Tells all the relevant types and their associations that might point to the object type managed by an unloader. 
	 * These associations have to be taken into account when performing the actual deletion, via {@link #delete(Identifiable)}.
	 *  
	 */
	protected Association[] getReferringAssociations () { 
		return null; 
	} 


	/**
	 * Tells if an object has some referring associations. The {@link #delete(Identifiable)} method will issue a 
	 * {@link #cancelDelete(Identifiable)} and will cancel the deletion if this method returns false. 
	 * By default, it checks all the possible dependencies that results from {@link #getReferringAssociations()}.
	 * 
	 */
	protected boolean hasNoReferringEntities ( Long id ) 
	{
		Association[] referringAssociations = getReferringAssociations ();
		if ( referringAssociations == null ) return true;
		         
		for ( Association ass: referringAssociations ) 
		{
			String ent = ass.dependingType.getCanonicalName (), rel = ass.dependingAssociation;
			
			// Join is required for collections, e.rel.id won't work,
			// See here: https://forum.hibernate.org/viewtopic.php?p=2359010
			String hql = 
				"select e.id from " + ent + " e left join e." + rel + " child where child.id = " + id;
			
			if ( log.isTraceEnabled () )
				log.trace ( 
					"hasNoReferringEntities, for <" + getUnloadedClass ().getSimpleName () + ", " + id + ">, running:\n   "	
					+ hql 
			);
			
			Query qry = unloadManager.getDaoFactory ().getEntityManager ().createQuery( hql );
			boolean result = qry.getResultList ().isEmpty ();

			log.trace ( "query returns " + result );
			if ( !result ) return false;
		}
		return true;
	}

	/**
	 * A wrapper that uses object.getId(). It assumes the object is not null. 
	 */
	protected boolean hasNoReferringEntities ( T object ) {
		return hasNoReferringEntities ( object.getId () );
	} 

	
}
