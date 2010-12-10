package uk.ac.ebi.utils.reflection;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import static uk.ac.ebi.utils.reflection.ReflectionUtils.getTypeArguments;

import java.util.Collection;
import java.util.List;

/**
 * Jan 16, 2008
 * @author brandizi
 *
 */
public class ReflectionUtilsTest
{
	
	private abstract class A<T1, T2 extends Collection<T3>, T3> {}
	private class B extends A<Integer, List<String>, String> {}
	private class C<T3> extends A<Integer, List<T3>, T3> {}
	private class D extends C<Integer> {}

	@Test
	public void testGetTypeArguments () {
		List<Class<?>> args = getTypeArguments ( A.class, B.class );
		assertNotNull ( "getTypeArguments() with null result! :-(", args );
		assertEquals ( "getTypeArguments() with wrong result (size)! :-(", 3, args.size () );

		Class<?> t0 = args.get ( 0 );
		assertNotNull ( "getTypeArguments() with wrong result (arg 0 is null)!", t0 );
		assertTrue ( "getTypeArguments() with wrong result (arg 0)! :-(", Integer.class.isAssignableFrom ( t0 ) );
		
		Class<?> t1 = args.get ( 1 );
		assertNotNull ( "getTypeArguments() with wrong result (arg 1 is null)!", t1 );
		assertTrue ( "getTypeArguments() with wrong result (arg 1)! :-(",  List.class.isAssignableFrom ( t1 ) );
		
	}


	
	@Test
	public void testGetTypeArguments1 () {
		List<Class<?>> args = getTypeArguments ( A.class, D.class );
		assertNotNull ( "getTypeArguments1() with null result! :-(", args );
		assertEquals ( "getTypeArguments1() with wrong result (size)! :-(", 3, args.size () );

		Class<?> t2 = args.get ( 2 );
		assertNotNull ( "getTypeArguments1() with wrong result (arg 2 is null)!", t2 );
		assertTrue ( "getTypeArguments1() with wrong result (arg 2)! :-(", Integer.class.isAssignableFrom ( t2 ) );
	}

	
	@Test
	public void testGetTypeArguments2 () {
		List<Class<?>> args = getTypeArguments ( A.class, C.class );
		assertNotNull ( "getTypeArguments2() with null result! :-(", args );
		assertEquals ( "getTypeArguments2() with wrong result (size)! :-(", 3, args.size () );

		Class<?> t2 = args.get ( 2 );
		assertNull ( "getTypeArguments1() with wrong result (arg 2 should be null)!", t2 );
	}
	

}
