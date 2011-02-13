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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.log4j.Logger;


import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Annotation;
import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Data;
import uk.ac.ebi.bioinvindex.model.Identifiable;
import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.model.Protocol;
import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.processing.DataNode;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.processing.Processing;
import uk.ac.ebi.bioinvindex.model.processing.ProtocolApplication;
import uk.ac.ebi.bioinvindex.model.term.AnnotationType;
import uk.ac.ebi.bioinvindex.model.term.AssayTechnology;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.ContactRole;
import uk.ac.ebi.bioinvindex.model.term.DataType;
import uk.ac.ebi.bioinvindex.model.term.Design;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.model.term.MaterialRole;
import uk.ac.ebi.bioinvindex.model.term.Measurement;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.model.term.Property;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;
import uk.ac.ebi.bioinvindex.model.term.ProtocolComponent;
import uk.ac.ebi.bioinvindex.model.term.ProtocolType;
import uk.ac.ebi.bioinvindex.model.term.PublicationStatus;
import uk.ac.ebi.bioinvindex.model.term.Unit;
import uk.ac.ebi.bioinvindex.model.term.UnitValue;
import uk.ac.ebi.bioinvindex.model.xref.AssayTypeDataLocation;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.model.xref.Xref;
import uk.ac.ebi.bioinvindex.unloading.bioproperties.CharacteristicValueUnloader;
import uk.ac.ebi.bioinvindex.unloading.bioproperties.FactorValueUnloader;
import uk.ac.ebi.bioinvindex.unloading.bioproperties.PropertyValueUnloader;
import uk.ac.ebi.bioinvindex.unloading.bioproperties.ProtocolParameterUnloader;
import uk.ac.ebi.bioinvindex.unloading.bioproperties.UnitUnloader;
import uk.ac.ebi.bioinvindex.unloading.bioproperties.UnitValueUnloader;
import uk.ac.ebi.bioinvindex.unloading.freetextterms.AnnotationTypeUnloader;
import uk.ac.ebi.bioinvindex.unloading.freetextterms.DesignUnloader;
import uk.ac.ebi.bioinvindex.unloading.freetextterms.ProtocolComponentUnloader;
import uk.ac.ebi.bioinvindex.unloading.ontologyterms.AssayMeasurmentUnloader;
import uk.ac.ebi.bioinvindex.unloading.ontologyterms.AssayTechnologyUnloader;
import uk.ac.ebi.bioinvindex.unloading.ontologyterms.ContactRoleUnloader;
import uk.ac.ebi.bioinvindex.unloading.ontologyterms.DataTypeUnloader;
import uk.ac.ebi.bioinvindex.unloading.ontologyterms.MaterialRoleUnloader;
import uk.ac.ebi.bioinvindex.unloading.ontologyterms.OntologyEntryUnloader;
import uk.ac.ebi.bioinvindex.unloading.ontologyterms.OntologyTermUnloader;
import uk.ac.ebi.bioinvindex.unloading.ontologyterms.ProtocolTypeUnloader;
import uk.ac.ebi.bioinvindex.unloading.ontologyterms.PublicationStatusUnloader;
import uk.ac.ebi.bioinvindex.unloading.pipeline.DataNodeUnloader;
import uk.ac.ebi.bioinvindex.unloading.pipeline.DataUnloader;
import uk.ac.ebi.bioinvindex.unloading.pipeline.MaterialNodeUnloader;
import uk.ac.ebi.bioinvindex.unloading.pipeline.MaterialUnloader;
import uk.ac.ebi.bioinvindex.unloading.pipeline.ProcessingUnloader;
import uk.ac.ebi.bioinvindex.unloading.pipeline.ProtocolApplicationUnloader;
import uk.ac.ebi.bioinvindex.unloading.bioproperties.PropertyTypeUnloader;

/**
 * The unloader manager. This is the entry point to be used to start the unloading of a set of model objects.
 * It keeps the list of objects scheduled for unloading and decides the {@link AbstractUnloader} to be used
 * for doing the unloading operations.  
 * 
 * @author brandizi
 * <b>date</b>: Nov 5, 2009
 *
 */
public class UnloadManager
{
	/**
	 * What is to be downloaded, on a per-class basis.
	 */
	private Map<Class<Identifiable>, Map<Long, ? extends Identifiable>> deletionQueue = 
		new HashMap<Class<Identifiable>, Map<Long, ? extends Identifiable>> ();

	/**
	 * Which unloader is to be used for which class. This is a cache used by {@link #getUnloader(Class)}
	 */
	private Map<Class<Identifiable>, AbstractUnloader<?>> unloaders 
		= new HashMap<Class<Identifiable>, AbstractUnloader<?>> ();
	
	private DaoFactory daoFactory;
	
	private final Timestamp submissionTs;
	private Timestamp additionalTs = null;
	private boolean _alreadyInvoked = false;
	
	private Set<String> messages = new HashSet<String> ();
	
	protected final static Logger log = Logger.getLogger ( UnloadManager.class );

	/**
	 * Tells which unloaders should be used for working with which type.
	 * see {@link UnloadManager#unloaderClasses}.
	 * 
	 */
	public static class UnloaderMapping<T extends Identifiable>
	{
		public final Class<T> targetClass;
		public final Class<? super AbstractUnloader<T>> unloaderClass;

		public UnloaderMapping ( Class<T> targetClass, Class<? super AbstractUnloader<T>> unloaderClass ) 
		{
			this.targetClass = targetClass;
			this.unloaderClass = unloaderClass;
		}
	}
	
	/**
	 * Tells which unloaders should be used for working with which type. The list is specified in order of
	 * specificity (less specific classes first), so that the most specific type gets its specific unloader, 
	 * or it gets a more general unloader if nothing else is available. 
	 */
	@SuppressWarnings("unchecked")
	private static UnloaderMapping<?> unloaderClasses [] = new UnloaderMapping<?> []
	{
		new UnloaderMapping ( Study.class, StudyUnloader.class ),
		new UnloaderMapping ( Investigation.class, InvestigationUnloader.class ),
		new UnloaderMapping ( Publication.class, PublicationUnloader.class ),
		new UnloaderMapping ( PublicationStatus.class, PublicationStatusUnloader.class ),
		new UnloaderMapping ( Contact.class, ContactUnloader.class ),
		new UnloaderMapping ( ContactRole.class, ContactRoleUnloader.class ),
		new UnloaderMapping ( Protocol.class, ProtocolUnloader.class ),
		new UnloaderMapping ( Annotation.class, AnnotationUnloader.class ),
		new UnloaderMapping ( Design.class, DesignUnloader.class ),
		new UnloaderMapping ( AnnotationType.class, AnnotationTypeUnloader.class ),
		new UnloaderMapping ( ProtocolType.class, ProtocolTypeUnloader.class ),
		new UnloaderMapping ( Parameter.class, ProtocolParameterUnloader.class ),
		new UnloaderMapping ( ProtocolComponent.class, ProtocolComponentUnloader.class ),
		new UnloaderMapping ( Assay.class, AssayUnloader.class ),
		new UnloaderMapping ( Measurement.class, AssayMeasurmentUnloader.class ),
		new UnloaderMapping ( AssayTechnology.class, AssayTechnologyUnloader.class ),
		new UnloaderMapping ( AssayResult.class, AssayResultUnloader.class ),
		new UnloaderMapping ( Data.class, DataUnloader.class ),
		new UnloaderMapping ( Material.class, MaterialUnloader.class ),
		new UnloaderMapping ( Processing.class, ProcessingUnloader.class ),
		new UnloaderMapping ( ProtocolApplication.class, ProtocolApplicationUnloader.class ),
		new UnloaderMapping ( MaterialNode.class, MaterialNodeUnloader.class ),
		new UnloaderMapping ( DataNode.class, DataNodeUnloader.class ),
		new UnloaderMapping ( DataType.class, DataTypeUnloader.class ),
		new UnloaderMapping ( MaterialRole.class, MaterialRoleUnloader.class ),
		new UnloaderMapping ( CharacteristicValue.class, CharacteristicValueUnloader.class ),
		new UnloaderMapping ( FactorValue.class, FactorValueUnloader.class ),
		new UnloaderMapping ( PropertyValue.class, PropertyValueUnloader.class ),
		new UnloaderMapping ( UnitValue.class, UnitValueUnloader.class ),
		new UnloaderMapping ( Unit.class, UnitUnloader.class ),
		new UnloaderMapping ( Property.class, PropertyTypeUnloader.class ),
		new UnloaderMapping ( AssayTypeDataLocation.class, DataLocationUnloader.class ),
		new UnloaderMapping ( ReferenceSource.class, ReferenceSourceUnloader.class ),
		new UnloaderMapping ( OntologyTerm.class, OntologyTermUnloader.class ),
		new UnloaderMapping ( OntologyEntry.class, OntologyEntryUnloader.class ),
		new UnloaderMapping ( Xref.class, XrefUnloader.class )
	}; 
	
	/**
	 * A particular instance of the unloader manager works with a daoFactory and with objects having a given timestamp, 
	 * i.e.: created by a particular submission.  
	 * 
	 * @param daoFactory
	 * @param submissionTs WARNING: if it's null all the objects passed to the unloader are removed, with no check on the timestamp
	 */
	public UnloadManager ( DaoFactory daoFactory, Timestamp submissionTs )
	{
		this.daoFactory = daoFactory;
		this.submissionTs = submissionTs;
	}
	
	/**
	 * Gets the unloader that has to be used for working with the parameter type. Uses the classes mapped in 
	 * {@value #unloaderClasses}. 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> AbstractUnloader<T> getUnloader ( Class<T> type ) 
	{
		try
		{
			AbstractUnloader unloader = unloaders.get ( type );
			if ( unloader != null ) return unloader;
	
			for ( UnloaderMapping<?> unloaderMapping: unloaderClasses )
			{
				if ( !unloaderMapping.targetClass.isAssignableFrom ( type ) ) continue;
				log.trace ( 
					"Instantiating the " + unloaderMapping.unloaderClass.getSimpleName () 
					+ " for the type " + unloaderMapping.targetClass.getSimpleName ()
				);
				unloader = (AbstractUnloader<T>) ConstructorUtils.invokeConstructor ( unloaderMapping.unloaderClass, this );
				unloaders.put ( (Class<Identifiable>) type, unloader );
				return unloader;
			}
			// TODO: proper exception, I don't know why BIIException has been moved in services
			throw new  RuntimeException( "Missing unloader for the type " + type.getCanonicalName () );
		}
		catch ( Exception ex ) {
			throw new RuntimeException ( "Problem with the Unloading system: " + ex.getMessage (), ex );
		}
	}
	/**
	 * Simply queues/schedules the object for deletion.
	 *   
	 * @return true if the object was actually added to {@link #deletionQueue} for deletion.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Identifiable> boolean queueSimple ( Class<? super T> type, T object  )
	{
		if ( object == null ) return false;
		Long id = object.getId ();
		if ( id == null ) return false;
		final Timestamp ts = object.getSubmissionTs ();
		
		if ( !( submissionTs == null 
						| ( submissionTs.equals ( ts ) || additionalTs != null && additionalTs.equals ( ts ) ) 
					)
		)
			return false;

		Map<Long, T> deletedIds = (HashMap<Long, T>) deletionQueue.get ( type );
		if ( deletedIds == null ) {
			deletedIds = new HashMap<Long, T> ();
			deletionQueue.put ( (Class<Identifiable>) type, deletedIds );
		}
		boolean result;
		if ( deletedIds.containsKey ( id ) )
			result = false;
		else {
			deletedIds.put ( object.getId(), object );
			result = true;
		}
		if ( log.isTraceEnabled () )
			log.trace ( "queing the deletion of <" + type.getSimpleName () + ", " + object.getId () + ">, result is: " + result );
		return result;
	}

	/**
	 * A wrapper of {@link #queueSimple(Class, Identifiable) queueSimple ( object.getClass(), object )} 
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Identifiable> boolean queueSimple ( T object )
	{
		return queueSimple ( (Class<T>) object.getClass (), object );
	}
	
	
	
	/**
	 * Gets the unloader for the parameter type and invokes its {@link AbstractUnloader#queue(Identifiable)} method.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> boolean queue ( Class<? super T> type, T object ) 
	{
		if ( object == null ) return false;
		Long id = object.getId ();
		if ( id == null ) return false;

		AbstractUnloader<T> unloader = getUnloader ( (Class<T>) type );
		return unloader.queue ( object );
	}

	/**
	 * A wrapper of {@link #queue(Class, Identifiable)}, with type = object.getClass(). 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> boolean queue ( T object ) 
	{
		if ( object == null || object.getId () == null ) return false;
		return queue ( (Class<T>) object.getClass (), object );
	}
	
	/**
	 * Gets the unloader for the parameter type and invokes its method {@link AbstractUnloader#queueAll(Collection)}. 
	 *  
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> boolean queueAll ( Class<? super T> type, Collection<T> objects )
	{
		if ( objects == null || objects.isEmpty () ) return false;
		AbstractUnloader<T> unloader = getUnloader ( (Class<T>) type );
		return unloader.queueAll ( objects );
	} 

	/**
	 * A wrapper of {@link #queueAll(Class, Collection)}. with class = object.getClass(). 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> boolean queueAll ( Collection<T> objects ) {
		if ( objects == null || objects.isEmpty () ) return false;
		for ( T object: objects )
			if ( object != null ) return queueAll ( (Class<T>) object.getClass (), objects );
		return false;
	}

	/**
	 * Gets the unloader for the parameter type and invokes its method {@link AbstractUnloader#queueAllByTs()}.
	 * 
	 * @return true if some object was actually added into {@link #deletionQueue}.
	 * 
	 */
	public <T extends Identifiable> boolean queueAllByTs ( Class<T> type )
	{
		AbstractUnloader<T> unloader = getUnloader ( type );
		return unloader.queueAllByTs ();
	} 
	
	/**
	 * Simply removes the object from {@link #deletionQueue}, used by the unloaders.
	 * 
	 * @return true if the object was actually in {@link #deletionQueue} and was actually removed from there.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Identifiable> boolean unqueueSimple ( Class<? super T> type, T object  )
	{
		if ( object == null || object.getId () == null ) return false;
		if ( !submissionTs.equals ( object.getSubmissionTs () ) ) return false;

		Set<T> deletedIds = (Set<T>) deletionQueue.get ( type );
		if ( deletedIds == null ) return false; 
		boolean result = deletedIds.remove ( object );
		if ( log.isTraceEnabled () )
			log.trace ( "canceling the deletion of <" + type.getSimpleName () + ", " + object.getId () + ">, result is: " + result );
		return result;
	}

	/**
	 * Simply removes the object from {@link #deletionQueue}, used by the unloaders. This is a wrapper of 
	 * {@link #unqueueSimple(Class, Identifiable)}, with class = object.getClass(). 
	 *  
	 * @return true if the object was actually in {@link #deletionQueue} and was actually removed from there.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Identifiable> boolean unqueueSimple ( T object )
	{
		return unqueueSimple ( (Class<T>) object.getClass (), object );
	}
	
	
	
	/**
	 * Removes an object from the deletion scheduling, i.e.: retracts the decision to delete the object.
	 * This method version allows to explicitly specify the specific class under which the object is seen 
	 * in {@link #deletionQueue}.
	 * 
	 * @return true if the object was actually in {@link #deletionQueue} and was actually removed from there.
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> boolean unqueue ( Class<? super T> type, T object ) 
	{
		if ( object == null ) return false;
		Long id = object.getId ();
		if ( id == null ) return false;

		AbstractUnloader<T> unloader = getUnloader ( (Class<T>) type );
		return unloader.unqueue ( object );
	}

	/**
	 * Removes an object from the deletion scheduling, i.e.: retracts the decision to delete the object.
	 * This is a wrapper of {@link #unqueue(Class, Identifiable)}, with class = object.getClass().
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> boolean unqueue ( T object ) 
	{
		if ( object == null || object.getId () == null ) return false;
		return unqueue ( (Class<T>) object.getClass (), object );
	}
	
	
	/**
	 * Removes all objects of a certain type from the deletion scheduling.
	 * @return true if some removal from {@link #deletionQueue} has actually occurred.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> boolean unqueueAll ( Class<? super T> type, Collection<T> objects )
	{
		if ( objects == null || objects.isEmpty () ) return false;
		AbstractUnloader<T> unloader = getUnloader ( (Class<T>) type );
		return unloader.unqueueAll ( objects );
	} 

	/**
	 * Deletes the parameter objects from the scheduled deletion.
	 * 
	 * @return true if some removal from {@link #deletionQueue} has actually occurred.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> boolean unqueueAll ( Collection<T> objects ) {
		if ( objects == null || objects.isEmpty () ) return false;
		for ( T object: objects )
			if ( object != null ) return queueAll ( (Class<T>) object.getClass (), objects );
		return false;
	}
	
	
	/**
	 * Gets objects of a certain type queued/scheduled for deletion.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends Identifiable> Map<Long, T> getDeletedObjectsByType ( Class<T> type )
	{
		Map<Long, T> delObjs = (Map<Long, T>) deletionQueue.get ( type );
		return delObjs;
	}
	
	
	/**
	 * Performs the deletion process, after all objects to be deleted are queued/scheduled. The right order, which is 
	 * compatible with referentail integirty constraints, is chosen here.
	 * 
	 * WARNING: for this version, this method can be called <b>only once</b>. If you want to restart queueing and deleting 
	 * objects, you need to create another instance of this class.
	 */
	public void delete ()
	{		
		if ( _alreadyInvoked ) 
			throw new IllegalStateException ( 
				"Internal Error: The UnloaderManager can be invoked only once, reinstantiate it for performing another unloading"
		);
		
		deleteType ( Publication.class );
		deleteType ( Contact.class );
		deleteType ( PublicationStatus.class );
		deleteType ( ContactRole.class );
		deleteType ( AssayResult.class );
		deleteType ( Processing.class );
		deleteType ( PropertyValue.class );
		deleteType ( ProtocolApplication.class );
		deleteType ( FactorValue.class );
		deleteType ( CharacteristicValue.class );
		deleteType ( Assay.class );
		deleteType ( Material.class );
		deleteType ( Data.class );
		deleteType ( MaterialNode.class );
		deleteType ( DataNode.class );
		deleteType ( DataType.class );
		deleteType ( MaterialRole.class );
		deleteType ( Measurement.class );
		deleteType ( AssayTechnology.class );
		deleteType ( Study.class );
		deleteType ( Protocol.class );
		deleteType ( Parameter.class );
		deleteType ( ProtocolComponent.class );
		deleteType ( ProtocolType.class );
		deleteType ( Investigation.class );
		deleteType ( Design.class );
		deleteType ( UnitValue.class );
		deleteType ( Unit.class );
		deleteType ( Property.class );
		deleteType ( OntologyTerm.class );
		deleteType ( OntologyEntry.class );
		deleteType ( AssayTypeDataLocation.class );
		deleteType ( ReferenceSource.class ); 
		deleteType ( Annotation.class );
		deleteType ( AnnotationType.class ); 
		// Do it again, so that annotation/xref's sources are removed too
		deleteType ( OntologyTerm.class );
		deleteType ( ReferenceSource.class ); 
		deleteType ( Xref.class );
		// Do it again, so that xref's sources are removed too
		deleteType ( ReferenceSource.class ); 
		_alreadyInvoked = true;
	}
	
	/**
	 * Deletes all instances of a given type, provided that it is one of the types registered in {@link #deletionQueue}.
	 * This is used by {@link #delete()}. 
	 * 
	 */
	private <T extends Identifiable> void deleteType ( Class<T> type )
	{
		AbstractUnloader<T> unloader = this.getUnloader ( type );
		unloader.deleteAll ();
	}
	
	/**
	 * The DAO factory is passed upon initialization and used to interface the DB, as usually
	 */
	public DaoFactory getDaoFactory () {
		return daoFactory;
	}

	/**
	 * The unloader is thought to delete all the objects related to a given study and the submission that created
	 * that study. This is set during the manager initialization 
	 * 
	 */
	public Timestamp getSubmissionTs () {
		return submissionTs;
	}

	/**
	 * Collects warnings and other messages, which can be printed at the end of the unloading. 
	 * Messages that are exatly the same are never duplicated (the internal structure is a set)
	 *  
	 * @param message
	 */
	public void addMessage ( String message )
	{
		messages.add ( message );
	}

	/**
	 * Tells all the messages issued during the unloading, without dupes.
	 */
	public Set<String> getMessages () {
		return Collections.unmodifiableSet ( messages );
	}

	/**
	 * When we're deleting an investigation that has no further study attached to it, the investigation 
	 * must be deleted anyway, so we need to specify its addtional timestamp to be considered by 
	 * {@link AbstractUnloader#delete(Identifiable)}. 
	 * 
	 * TODO: well, it's a quite ugly patch... this TS thing is to be reviewed.
	 * 
	 * @param ts
	 */
	void setAdditionalTs ( Timestamp ts ) {
		this.additionalTs = ts;
	}
}
