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

import org.apache.commons.collections.CollectionUtils;
import org.dbunit.operation.DatabaseOperation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.term.Characteristic;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.Unit;
import uk.ac.ebi.bioinvindex.model.term.UnitValue;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.persistence.FreeTextTermPersistenceTest.OESelector;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;

import static java.lang.System.out;
import java.sql.Timestamp;
import java.util.Collection;


public class PropertyValuePersistenceTest extends TransactionalDBUnitEJB3DAOTest
{

	private CharacteristicValuePersister persister;


	public PropertyValuePersistenceTest() throws Exception {
		super();
	}

	@Before
	public void initPersister () 
	{
		// Needs to be instantiated here, so that the internal cache for the ontology terms is cleared
		// before every test.
		//
		persister = new CharacteristicValuePersister ( 
			DaoFactory.getInstance ( entityManager ), new Timestamp ( System.currentTimeMillis () ) 
		);
	}

	
	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_persistence.xml";
	}


	@Test
	public void testPersistValue () throws Exception
	{

		out.println ( "\n\n\n _______________ PropertyValuePersistanceTest, Basic Test ____________________________\n" );

		Characteristic prop = new Characteristic ( "Foo Property", 0 );

			// New OE
			ReferenceSource source = new ReferenceSource ( "My fancy test ontology 2" );
			source.setAcc ( "bii:tests:MY-SRC-2" );
			OntologyTerm oe1 = new OntologyTerm ( "biionto:101", "test OE", source );
			prop.addOntologyTerm ( oe1 );

			// Existing OE
			source = new ReferenceSource ( "TEST ONTOLOGY" );
			source.setAcc ( "BII-1" );
			OntologyTerm oe2 = new OntologyTerm ( "OBI-EO1", "organism", source );
			prop.addOntologyTerm ( oe2 );

			// The Value
			CharacteristicValue value = new CharacteristicValue ( "Foo Value", prop );

			// Let's persist
			CharacteristicValue valueNew =  persister.persist ( value );
			transaction.commit ();


			assertNotNull ( "Uh! Oh! Persister returns null!", valueNew );
			assertTrue ( "Ouch! Cannot find the persisted value in the DB!", entityManager.contains(valueNew) );
			assertTrue ( "Argh! The persisted value and the original one should be the same!", value == valueNew );
			assertNotNull ( "Burp! The persisted value should have an ID!", valueNew.getId() );


			Characteristic typeNew = valueNew.getType ();
			assertNotNull ( "Urp! Saved Value has no type!", typeNew );
			assertNotNull ( "Urp! Type of Saved Value should have an ID!", typeNew.getId () );
			assertEquals ( "Urp! Type of Saved Value has wrong value", "Foo Property", typeNew.getValue () );


			Collection<OntologyTerm> oes = typeNew.getOntologyTerms ();
			assertNotNull ( "Oh no! The persisted value does not return OEs!", oes );
			assertEquals ( "Oh no! The persisted value does not return correct no. of OEs !", 2, oes.size() );

			OntologyTerm oeDB1 = (OntologyTerm) CollectionUtils.find ( oes, new OESelector ( oe1 ) );
			assertNotNull ( "Urp! The term " + oe1 + "was not persisted with the value!", oeDB1 );
			assertNotNull ( "The term: " + oeDB1 + "should have an ID!", oeDB1 );

			OntologyTerm oeDB2 = (OntologyTerm) CollectionUtils.find ( oes, new OESelector ( oe2 ) );
			assertNotNull ( "Urp! The term " + oe2 + "was not persisted with the value!", oeDB2 );
			assertEquals ( "Wrong retrieved ID for the term : " + oeDB2 + "!", new Long ( 501 ), oeDB2.getId () );


			out.println ( "Saved Value:" + valueNew );


		out.println ( "\n _______________ /end: PropertyValuePersistanceTest, Basic Test ____________________________\n\n" );
	}


	@Test
	public void testPersistValueAndUnit () throws Exception
	{
		out.println ( "\n\n\n ______________________ Value & Unit Test _______________________________\n" );

		Characteristic prop = new Characteristic ( "Foo Property", 0 );

			// The Value
			CharacteristicValue value = new CharacteristicValue ( "10", prop );

			// The Unit
			Unit unit = new Unit ( "My Length Type" );
			UnitValue uval = new UnitValue ( "my-len-unit", unit );
			ReferenceSource usrc = new ReferenceSource ( "TEST ONTOLOGY" );
			usrc.setAcc ( "BII-1" );
			OntologyTerm uterm = new OntologyTerm ( "bii:mylen", "mylen", usrc );
			uval.addOntologyTerm ( uterm );
			value.setUnit ( uval );

			// Let's persist
			CharacteristicValue valueNew = persister.persist ( value );
			transaction.commit ();


			assertNotNull ( "Uh! Oh! Persister returns null!", valueNew );
			assertTrue ( "Ouch! Cannot find the persisted value in the DB!", entityManager.contains ( valueNew ) );
			assertTrue ( "Argh! The persisted value and the original one should be the same!", value == valueNew );
			assertNotNull ( "Burp! The persisted value should have an ID!", valueNew.getId() );


			Characteristic typeNew = valueNew.getType ();
			assertNotNull ( "Urp! Saved Value has no type!", typeNew );
			assertNotNull ( "Urp! Type of Saved Value should have an ID!", typeNew.getId () );
			assertEquals ( "Urp! Type of Saved Value has wrong value", "Foo Property", typeNew.getValue () );

			UnitValue uvalNew = valueNew.getUnit ();
			assertNotNull ( "Ouch! No Unit saved!", uvalNew );
			assertNotNull ( "Ouch! Retrieved Unit should have an ID!", uvalNew.getId () );
			assertEquals ( "Ouch! Wrong Unit Value!", "my-len-unit", uvalNew.getValue () );

			Unit utypeNew = uvalNew.getType ();
			assertNotNull ( "Ouch! No Unit Type saved!", utypeNew );
			assertNotNull ( "Ouch! Retrieved Unit should have an ID!", utypeNew.getId () );
			assertEquals ( "Ouch! Wrong Unit Type Value!", "My Length Type", utypeNew.getValue () );

			Collection<OntologyTerm> utermNew = uvalNew.getOntologyTerms ();
			assertNotNull ( "Arg! No term annotation found for the unit!", utermNew );
			assertEquals ( "Arg! Cannot find the single term annotation for the unit", 1, utermNew.size () );
			assertEquals ( "Wrong OE annotation retrieved for unit value", "mylen", utermNew.iterator ().next ().getName () );


			out.println ( "Saved Value:" + valueNew );


		out.println ( "\n __________________________ /end: Value & Unit Test ____________________________\n\n" );
	}

}
