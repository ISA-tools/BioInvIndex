package uk.ac.ebi.bioinvindex.utils;

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

import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.model.Protocol;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.processing.MaterialProcessing;
import uk.ac.ebi.bioinvindex.model.processing.Processing;
import uk.ac.ebi.bioinvindex.model.processing.ProtocolApplication;
import uk.ac.ebi.bioinvindex.model.term.Characteristic;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.MaterialRole;
import uk.ac.ebi.bioinvindex.model.term.ProtocolType;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;

public class CascadedPropertiesTest
{
	private int nodeCounter = 0;

	private Processing<?, ?> addStep ( Study study, MaterialProcessing previousStep, Material material, Protocol protocol )
	{

		MaterialNode mnode = new MaterialNode ( study );
		mnode.setAcc ( "MN:" + nodeCounter++ );
		mnode.setMaterial ( material );

		if ( previousStep != null )
			previousStep.addOutputNode ( mnode );

		if ( protocol == null )
			return null;

		MaterialProcessing step = new MaterialProcessing ( study );
		ProtocolApplication protoApp = new ProtocolApplication ( protocol );
		step.addProtocolApplication ( protoApp );
		step.addInputNode ( mnode );

		return step;
	}


	@Test
	@Ignore
	public void testCascadedProperties ()
	{
		/*
		 * m1 ---- m2 ---- m3-----m3.1
		 *                   \----m3.2
		 */


		Material m = new Material ( "test 1", new MaterialRole ( "foo", "Foo Material Type", new ReferenceSource ( "Foo Ref" ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 1", new Characteristic ( "Type 1", 0 ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 2", new Characteristic ( "Type 2", 1 ) ) );
		Protocol p = new Protocol ( "Foo Proto", new ProtocolType ( "proto:1", "Protocol 1", new ReferenceSource ( "Foo Ref" )  ) );
		MaterialProcessing step = (MaterialProcessing) addStep ( null, null, m, p );

		m = new Material ( "test 2", new MaterialRole ( "foo", "Foo Material Type", new ReferenceSource ( "Foo Ref" ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 2", new Characteristic ( "Type 2", 2 ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 3", new Characteristic ( "Type 4", 3 ) ) );
		p = new Protocol ( "Foo Proto 1", new ProtocolType ( "proto:2", "Protocol 2", new ReferenceSource ( "Foo Ref" )  ) );
		step = (MaterialProcessing) addStep ( null, step, m, p );

		m = new Material ( "test 3", new MaterialRole ( "foo 1", "Foo Material Type 1", new ReferenceSource ( "Foo Ref" ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 2", new Characteristic ( "Type 2", 4 ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 3", new Characteristic ( "Type 4", 5 ) ) );
		MaterialProcessing step1 = (MaterialProcessing) addStep ( null, step, m, p );
		MaterialProcessing step2 = (MaterialProcessing) addStep ( null, step, m, p );

		m = new Material ( "test 3.1", new MaterialRole ( "foo 1", "Foo Material Type 1", new ReferenceSource ( "Foo Ref" ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 2.1", new Characteristic ( "Type 7", 6 ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 3.1", new Characteristic ( "Type 8", 7 ) ) );
		addStep ( null, step1, m, null );

		m = new Material ( "test 3.2", new MaterialRole ( "foo 1", "Foo Material Type 1", new ReferenceSource ( "Foo Ref" ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 2.2", new Characteristic ( "Type 8", 8 ) ) );
		m.addCharacteristicValue ( new CharacteristicValue ( "value 3.2", new Characteristic ( "Type 9", 9 ) ) );
		addStep ( null, step2, m, null );

		// ToDo: fix test to use AssayResult

//		Assay assay = new Assay ();
//		assay.setMaterial ( m );


//		Collection<CharacteristicValue> characteristics = assay.findPipelineCharacteristicValues ();

//		out.println ( "\n\n\n***************" );
//		for ( CharacteristicValue chr: characteristics ) {
//			out.println ( chr );
//		}


//		assertEquals ( "Arg! Bad no of characteristic values returned!", 8, characteristics.size () );
//		assertNotNull ( "Ops! 'value 2' was not found",
//			CollectionUtils.find ( characteristics, new Predicate () {
//				public boolean evaluate ( Object object ) {
//					return "value 2".equals ( ( (CharacteristicValue) object ).getValue () );
//				}
//			})
//		);
//
//		out.println ( "\n\nCharacteristics found:" );
//		for ( CharacteristicValue v: characteristics )
//			out.println ( "  " + v );
//
//
//		characteristics = AssayResult.filterRepeatedPropertyValues ( characteristics );
//		assertEquals ( "Arg! Bad no of filtered values!", 5, characteristics.size () );
//
//		out.println ( "\n\nFiltered Characteristics:" );
//		for ( CharacteristicValue v: characteristics )
//			out.println ( "  " + v );

	}

}
