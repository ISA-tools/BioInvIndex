package uk.ac.ebi.utils.collections;

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

import java.util.Collection;
import java.util.Iterator;

/**
 * Allows to type-cast the collection of type T1 into the type T (T extends T1), which is not permitted by the 
 * Java generics. The class is simply the adapter pattern wrapping the converted collection. 
 * 
 * date: Jul 29, 2008
 * @author brandizi
 *
 * @param <T> the target type for the collection. 
 * 
 */
public class TypeCastCollection<T> implements Collection<T>
{
	protected final Collection<? super T> base;

	
	private class TypeCastIterator implements Iterator<T>
	{
		private final Iterator<? super T> baseItr;
		
		public TypeCastIterator ( Iterator<? super T> baseIterator ) {
			this.baseItr = baseIterator;
		}

		public boolean hasNext () {
			return baseItr.hasNext ();
		}

		@SuppressWarnings ( "unchecked" )
		public T next () {
			return (T) baseItr.next ();
		}

		public void remove () {
			baseItr.remove ();
		}
	}
	
	
	
	
	public TypeCastCollection ( Collection<? super T> base ) {
		this.base = base;
	}

	public boolean add ( T o ) {
		return base.add ( o );
	}

	public boolean addAll ( Collection<? extends T> c ) 
	{
		boolean result = true;
		for ( T o: c )
			result &= base.add ( o );
		return result;
	}

	public void clear () {
		base.clear ();
	}

	public boolean contains ( Object o ) {
		return base.contains ( o );
	}

	public boolean containsAll ( Collection<?> c ) {
		return base.containsAll ( c );
	}

	public boolean isEmpty () {
		return base.isEmpty ();
	}

	public Iterator<T> iterator () {
		return new TypeCastIterator ( base.iterator () );
	}

	public boolean remove ( Object o ) {
		return base.remove ( o );
	}

	public boolean removeAll ( Collection<?> c ) {
		return base.removeAll ( c );
	}

	public boolean retainAll ( Collection<?> c ) {
		return base.retainAll ( c );
	}

	public int size () {
		return base.size ();
	}

	public Object[] toArray () {
		return base.toArray ();
	}

	public <TA> TA[] toArray ( TA[] a ) {
		return base.toArray ( a );
	}

}
