package uk.ac.ebi.bioinvindex.model;


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

import static org.junit.Assert.*;

import org.junit.Test;
import static java.lang.System.out;

import uk.ac.ebi.bioinvindex.model.processing.ProtocolApplication;
import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.model.term.ParameterValue;
import uk.ac.ebi.bioinvindex.model.term.ProtocolType;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;

public class ProtoAppEquivalenceTest
{
	@Test
	public void testEquivalence ()
	{
		Protocol p1 = new Protocol ( "Foo Proto", new ProtocolType ( "test", "test Proto",
			new ReferenceSource ( "foo source" ) ) );
		Parameter par1 = new Parameter ( "Param 1", 0 );
		p1.addParameter ( par1 );

		ProtocolApplication pa1 = new ProtocolApplication ( p1 );
		ParameterValue pval1 = new ParameterValue ( par1 );
		pval1.setValue ( "v1" );
		pa1.addParameterValue ( pval1 );

		ProtocolApplication pa2 = new ProtocolApplication ( p1 );
		ParameterValue pval2 = new ParameterValue ( par1 );
		pval2.setValue ( "v1" );
		pa2.addParameterValue ( pval2 );

		assertEquals ( "Different hashcodes for the same parameters!", pval1.hashCode (), pval2.hashCode () );
		assertEquals ( "Different hashcodes for the same protocol apps!", pa1.hashCode (), pa2.hashCode () );
		assertTrue ( "equals false for the same parameters!", pval1.equals ( pval2 ) );
		assertTrue ( "equals false for the same protocol apps!", pa1.equals ( pa2 ) );
	}
}
