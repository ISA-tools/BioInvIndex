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

import java.util.List;

/**
 * Miscellanea utilities about lists. 
 * 
 * @author brandizi
 * <b>date</b>: Dec 13, 2009
 *
 */
public class ListUtils
{
	/**
	 * Sets the element i in list, independently on the list size. That is: if i < list.size() calls list.set (i, v), 
	 * otherwise adds i - list.size() null elements to list and then adds v as the last element, which will be the i element. 
	 * 
	 */
	public static <T> T set ( List<T> list, int i, T v ) 
	{
		int sz = list.size ();
		if ( i == sz ) {
			list.add ( v );
			return null;
		}
		else if ( i > sz ) 
		{
			for ( int j = sz; j < i; j++ ) { 
				assert ( list.size () == j ): "Internal error: loop invariant in ListUtils.set() violated!";
			  // Proof: base is trivial. Recursion: from list.sz == j, you increase sz with add and j with incr. step 
				list.add ( null );
			}
			assert ( list.size () == i ): "Internal error: loop-end assertion in ListUtils.set() violated!";
			// Proof: trivial, from the loop invariant + the end condition
			
			list.add ( v );
			return null;
		}
		else
			return list.set ( i, v );
	}
	
	/**
	 * Gets the i element of list, independently on list.size(). That is: i < list.size () ? list.get ( i ) : null 
	 * 
	 */
	public static <T> T get ( List<T> list, int i ) {
		return i < list.size () ? list.get ( i ) : null;
	} 

	/**
	 * Adds the element i in list, as in {@link List#add(int, Object)}, but independently on list.size(). That is: calls 
	 * list.add (i, v) if i <= list.size() (and shifts all elements from i upward as usually), calls 
	 * {@link #set(List, int, Object) set ( list, i, v)} otherwise (i.e.: adds null element up to i-1 and v as last 
	 * element).  
	 *  
	 */
	public static <T> void add ( List<T> list, int i, T v )
	{
		if ( i <= list.size () )
			list.add ( i, v );
		else
			set ( list, i, v );
	} 

}
