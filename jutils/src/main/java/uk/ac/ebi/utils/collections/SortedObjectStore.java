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
 
package uk.ac.ebi.utils.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;


/**
 * Like {@link ObjectStore}, but with types and keys kept sorted. 
 *  
 * @author brandizi
 * <b>date</b>: Mar 3, 2010
 * 
 */
public class SortedObjectStore<T, K, V> extends ObjectStore<T, K, V>
{
	private SortedMap<T, SortedMap<K, V>> types; 
	protected int size = 0;

	private final Comparator<K> keyComparator;
	
	public SortedObjectStore () {
		this ( null, null );
	}


	public SortedObjectStore ( Comparator<T> typeComparator, Comparator<K> keyComparator ) 
	{
		types = new TreeMap<T, SortedMap<K,V>> ( typeComparator );
		this.keyComparator = keyComparator;
	}

	protected static final Logger log = Logger.getLogger ( SortedObjectStore.class );

	/**
	 * Stores an object, identified by a type and an identifier
	 * If value is null deletes the entry.
	 *
	 */
	public void put ( T type, K id, V value )
	{
		SortedMap<K, V> idmap = this.types.get ( type );

		if ( idmap == null ) {
			idmap = new TreeMap<K, V> ( keyComparator );
			types.put ( type, idmap );
		}

		if ( value == null ) {
			if ( idmap.containsKey ( id ) ) {
				idmap.remove ( id );
				if ( size > 0 ) size--;
			}
		}
		else {
			if ( !idmap.containsKey ( id ) ) size++;
			idmap.put ( id, value );
		}

//		log.trace ( String.format (
//			"ObjectStore, storing no. %d: <%s, %s, %s>\n\n", size, type, id, value
//		));
	}
	
	@Override
	public void remove ( T type ) 
	{
		Map<K, V> idmap = this.types.get ( type );
		if ( idmap == null ) return; 
		
		size -= idmap.size ();
		idmap.clear ();
	}


	/**
	 * Gets an object identifies by type and id. Returns null in case the entry is
	 * empty.
	 *
	 */
	public V get ( T type, K id )
	{
		Map<K, V> idmap =  this.types.get ( type );
		if ( idmap == null ) return null;
		return idmap.get ( id );
	}


	public int size () {
		return size;
	}


	/** All the types, sorted, in the store */
	public Set<T> types () {
		return types.keySet ();
	}

	/** All the identifiers, sorted, of objects belonging to a given type */
	public Set<K> typeKeys ( T type ) {
		Map<K, V> idmap = this.types.get ( type );
		if ( idmap == null ) return null;
		return idmap.keySet ();
	}


	/** All the values of a certain type. Never returns null. */
	public Collection<V> values ( T type ) 
	{
		Map<K,V> idmap = this.types.get ( type );
		if ( idmap == null ) return Collections.emptySet ();
		return Collections.unmodifiableCollection ( idmap.values () );
	}
	

	
	/**
	 * Prints full contents of the object store.
	 *
	 */
	public String toStringVerbose ()
	{
		String result = "";

		for ( T type : this.types () )
			for (  K key : this.typeKeys ( type ) )
				result += String.format ( "<%s, %s>:\n%s\n", type, key, this.get ( type, key ) );
		return result;
	}

	
}
