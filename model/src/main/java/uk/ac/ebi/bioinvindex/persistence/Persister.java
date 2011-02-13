package uk.ac.ebi.bioinvindex.persistence;

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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.Logger;

import uk.ac.ebi.bioinvindex.dao.IdentifiableDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Accessible;
import uk.ac.ebi.bioinvindex.model.Identifiable;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.unloading.AbstractUnloader;
import uk.ac.ebi.utils.reflection.ReflectionUtils;

/**
 * The Persister class. Does the persistence work. Essentially, checks whether the object to be persisted already exists
 * in the DB (for instance, by using the accession), in case it's not, persists the related objects and finally persists
 * the new object itself. When an object is persisted, its submission timestamp is saved, which allows to associated the 
 * object to the submission it was creaded for.<p/>
 * 
 * <p>See also {@link AbstractUnloader}. The way the unloading is realized is absolutely similar to how the persistence works.</p>  
 *  
 * <p><b>date</b>: Aug 25, 2008</p>
 * @author brandizi
 *
 * @param <T> The type of object to be persisted
 * 
 */
public abstract class Persister<T extends Identifiable>
{
	/**
	 * <p>The light persistence option. If this property is set to "true", the perister skips the persistence of
	 * the pipeline. This feature was introduced because such operation is still terribly slow and really unfeasible
	 * for large data sets. Not storing the pipeline implies that you cannot change the database and have the changes
	 * reflected by the study exporter. It also implies that the latter doesn't work at all. That is: BE CAREFUL in 
	 * using it.</p> 
	 * 
	 * <p>The property can be set in the file config/config.properties</p> 
	 * 
	 */
	public static final String LIGHT_PERSISTENCE_PROPERTY = "bioinvindex.biidb.persistence.light-mode";
	
	private static Boolean isLightPersistenceFlag = null;
	
	/**
	 * See {@link #LIGHT_PERSISTENCE_PROPERTY}.
	 */
	public static boolean isLightPersistence ()
	{
		if ( isLightPersistenceFlag != null ) return isLightPersistenceFlag;
		return isLightPersistenceFlag = "true".equals ( System.getProperty ( LIGHT_PERSISTENCE_PROPERTY, null ) );
	}
	
	/** 
	 * We log some messages (e.g.: "#X deleted" with this priority), you may want to change it for important objects
	 * and leave it low for others. 
	 *  
	 */
	protected Priority logLevel = Level.TRACE;
	
	/**
	 * Available in all the persister for managing the DB operations
	 */
	protected DaoFactory daoFactory;
	/**
	 * Available in all the persister for managing the DB operations
	 */
	protected IdentifiableDAO<? super T> dao;

	private final Timestamp submissionTs;
	
	private final Map<String, T> cache = new HashMap<String, T> (); 
	private Class<T> persistedClass = null; 
	
	protected static final Logger log = Logger.getLogger ( Persister.class );
	
	public Persister ( DaoFactory daoFactory, Timestamp submissionTs ) {
		super ();
		this.daoFactory = daoFactory;
		this.dao = daoFactory.getIdentifiableDAO ( getPersistedClass () );
		this.submissionTs = submissionTs;
	}

	/** 
	 * Does the persistence job. It should return the parameter in case the object is new and a new instance in the DB 
	 * is created. A corresponding object retrieved from the DB should be returned in case the object already ecists in the
	 * DB.<p/>
	 * 
	 * You should not need to change this method for implementing your own persistence, {@link #preProcess(Identifiable)}
	 * should be the method to change instead.<p/>
	 * 
	 * The default version works like that:<p/> 
	 * <ul>
	 *   <li>if the parameter is null =&gt; returns null</li>
	 *   <li>if object.getId() != null =&gt; returns the object (we assume it is in the DB)</li>
	 *   <li>if {@link #cachedLookup(Identifiable) cachedLookup(object)} != null =&gt; returns the object found in the DB</li>
	 *   <li>otherwise object is assumed to be new and persisted as such, hence we call {@link #preProcess(Identifiable) preProcess(object)}; 
	 * 			 {@link #save(Identifiable) save(object)}; {@link #postProcess(Identifiable)}
	 * </ul>
	 * 
	 * <b>WARNING</b>: in general, the parameter is changed in several ways (when it is assumed to be new and returned by the method).
	 * As a minimum, properties like ID or accession are filled. Other changes are possible (for instance: related objects are replaced
	 * with the equivalent instance stored in the DB.
	 * 
	 */
	public T persist ( T object ) 
	{
		// We don't save nulls... 
		if ( object == null ) {
			log.trace ( "persister of class " + getPersistedClass ().getSimpleName () + ", persisting object is null, returning" );
			return null;
		}
		
		Long id = object.getId ();

		String objStr = null;
		if ( log.isTraceEnabled () || log.isEnabledFor ( logLevel ) ) 
		{ 
			objStr = "<" + object.getClass ().getSimpleName () + ", ";
			if ( object instanceof Accessible )
				objStr += ((Accessible) object).getAcc ();
			else if ( object instanceof OntologyEntry )
				objStr += ((OntologyEntry) object).getAcc ();
			else 
				objStr += object;
			objStr += ">";
		}
		
		
		// It was already saved in some other call
		if ( id != null ) 
		{
			if ( log.isTraceEnabled () )
				log.trace ( 
					"persister, not persisting " + objStr + ", has already an ID, should be already in the DB" 
			);
			return object;
		}
		
		// If it is already in the DB, returns it
		T objectDB = cachedLookup ( object );
		if ( objectDB != null ) {
			if ( log.isTraceEnabled () ) 
				log.trace ( "persister, not persisting " + objStr + ", is already in the DB" );
			return objectDB;
		}
		
		// save and do pre- and post-
		if ( log.isTraceEnabled () ) log.trace ( "persister, start pre-processing " + objStr );
		preProcess ( object );
		object.setSubmissionTs ( getSubmissionTs () );
		if ( log.isTraceEnabled () ) log.trace ( "persister, start saving " + objStr );
		save ( object );
		if ( log.isTraceEnabled () ) log.trace ( "persister, start post-processing " + objStr );
		postProcess ( object );

		if ( log.isEnabledFor ( logLevel ) ) 
			log.log ( logLevel, "object:" + objStr + " persisted." );

		return object;
	}
	
	
	/** 
	 * Pre-process a new object which has to be persisted. This is called by {@link #persist(Identifiable)} once it
	 * has established its parameter object is to be actually persisted. By default does nothing.
	 * 
	 */
	protected void preProcess ( T object ) {
	}
	
	/**
	 * Post-process a new object just after it has been persisted. It is called by {@link #persist(Identifiable)}, after
	 * it has persisted a new object. By default does nothing.
	 *
	 */
	protected void postProcess ( T object ) {
	}
	
	/**
	 * Saves a new object in the DB, usually by means of the DAO obtained from the {@link #daoFactory}.
	 * if {@link #getCacheKey(Identifiable) getCacheKey ( object )} is null, the internal cache is also 
	 * updated with the object.
	 * 
	 */
	protected Long save ( T object ) 
	{
		Long id = dao.save ( object );
		String key = getCacheKey ( object );
		if ( key != null ) cache.put ( key, object );
		return id;
	}

	/** 
	 * This method should search the object in the database and return an existing object if it exists, null
	 * must be returned otherwise. 
	 * 
	 * By default here we always return null, i.e.: default behavior is always to add objects.
	 * 
	 * This method should not be used directly, it is automatically called by {@link #cachedLookup(Identifiable)}. 
	 */
	protected T lookup ( T object ) {
		return null;
	}

	/**
	 * Returns a String key to be used to cache the object in an internal cache of type {@link Map Map&lt;String, T&gt;}. 
	 * This stores those objects in the DB having the same key returned by this method. This way, objects will be cached 
	 * during a persistence session, without searching them in the DB every time {@link #lookup(Identifiable)} is invoked.
	 * 
	 * This method only makes sense for an object if {@link #lookup(Identifiable) lookup ( object )} returns a non-null 
	 * value.
	 *  
	 */
	protected String getCacheKey ( T object ) {
		return null;
	}
	
	/**
	 * Search an object that may already possibly exist in the internal cache (which is populated from the DB) or in the DB
	 * (updates the cache in that case). It works this way: 
	 * 
	 * <pre>
	 * if ( {@link #getCacheKey(Identifiable) getCacheKey ( object )} == null 
	 *   return {@link #lookup(Identifiable) lookup ( object )}
	 * if an object with the key above is in the cache
	 *   return that object
	 * if an object is returned by lookup ( object ) (i.e. by the DB)
	 *   save the object in the internal cache and return it
	 * return null otherwise
	 * </pre>
	 * 
	 * This method is automatically invoked by {@link #persist(Identifiable)}. if you need to lookup an object
	 * in the DB, you should prefer the invocation of this method to {@link #lookup(Identifiable)}. 
	 * Instead, you should not need to reimplement this method for the specific way the object equivalence is 
	 * used to search the DB. {@link #lookup(Identifiable)} is the method to override in such a case.
	 *  
	 */
	protected T cachedLookup ( T object ) 
	{
		String key = getCacheKey ( object );
		log.trace ( "cachedLookup(): key is '" + key + "'" );
		
		if ( key == null ) return lookup ( object );
		
		T cachedObj = cache.get ( key );
		if ( cachedObj != null ) {
			log.trace ( "cachedLookup(): returning cached object" );
			return cachedObj;
		}
		
		log.trace ( "cachedLookup(): searching object in the DB" );
		T dbObj = lookup ( object );
		if ( dbObj != null ) {
			log.trace ( "cachedLookup(): caching and returning object in the DB, #" + dbObj.getId () );
			cache.put ( key, dbObj );
		}
		else
			log.trace ( "cachedLookup(): object is new, returning null" );
		return dbObj;
	}
	
	
	/** 
	 * The class corresponding to ObjectType, extracted by means of reflection.
	 * 
	 * WARNING: This method won't work if the current class still has a generic which overrides T. Declare the class
	 * abstract in such a case and use an anonymous class.
	 * 
	 */
	protected Class<T> getPersistedClass () 
	{
		if ( persistedClass != null ) return persistedClass;
		
		persistedClass = ReflectionUtils.getTypeArgument ( Persister.class, this.getClass(), 0 );
		if ( persistedClass == null ) 
			throw new PersistenceException ( "getPersistedClass() returns null for " + this.getClass () );
		return persistedClass;

	}


	/**
	 * The submission time-stamp, which will be used throughout the current submission. 
	 * This should be set once per submission, and propagated by every persister which uses other persisters. 
	 * 
	 * WARNING: This method won't work if the current class still has a generic which overrides T. Declare the class
	 * abstract in such a case and use an anonymous class for clearing the generic.
   * 
	 */
	public Timestamp getSubmissionTs () {
		return submissionTs;
	}
	

}
