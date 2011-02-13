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
import org.apache.commons.collections.Predicate;
import org.dbunit.operation.DatabaseOperation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Protocol;
import uk.ac.ebi.bioinvindex.model.term.FreeTextTerm;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.model.term.ProtocolComponent;
import uk.ac.ebi.bioinvindex.model.term.ProtocolType;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;

import static java.lang.System.out;
import java.sql.Timestamp;
import java.util.Collection;


public class ProtocolPersistenceTest extends TransactionalDBUnitEJB3DAOTest
{

	private ProtocolPersister persister;

	/** Compares by ID */
	private static class FreeTextSelector implements Predicate
	{
		private FreeTextTerm term;

		public FreeTextSelector ( FreeTextTerm term ) {
			this.term = term;
		}

		public boolean evaluate ( Object object ) {
			return term.getId ().equals ( ((FreeTextTerm) object).getId() );
		}

	}



	public ProtocolPersistenceTest() throws Exception {
		super();
	}
 
	@Before
	public void initPersister () 
	{
		// Needs to be instantiated here, so that the internal cache for the ontology terms is cleared
		// before every test.
		//
		persister = new ProtocolPersister ( DaoFactory.getInstance(entityManager), new Timestamp ( System.currentTimeMillis () )  );
	}

	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_persistence.xml";
	}


	@Test
	public void testPersistNew () throws Exception
	{

		out.println ( "\n\n\n _______________ ProtocolPersistanceTest, Testing new Protocol  ____________________________\n" );

		ReferenceSource source = new ReferenceSource ( "My fancy test ontology 2" );
		source.setAcc ( "bii:tests:MY-SRC-2" );
		ProtocolType type = new ProtocolType ( "proto:type:999", "My Test Proto Type", source );

		Protocol protocol = new Protocol ( "A-NEW-PROTO-99", type );
		protocol.setAcc ( protocol.getName () );
		
		transaction.commit ();

		// Let's attach few parameters
		//
		Parameter param1 = new Parameter ( "Time Before Killing the Operator", 0 );
			ReferenceSource sourceOE1 = new ReferenceSource ( "My fancy test ontology 3" );
			sourceOE1.setAcc ( "bii:tests:MY-SRC-3" );
			OntologyTerm oe1 = new OntologyTerm ( "biionto:101", "test OE", source );
		param1.addOntologyTerm ( oe1 );
		protocol.addParameter ( param1 );

		Parameter param2 = new Parameter ( "Time To get rid of the corpse", 1 );
			ReferenceSource sourceOE2 = new ReferenceSource ( "101 Ontology" );
			sourceOE2.setAcc ( "BII-101" );
			OntologyTerm oe2 = new OntologyTerm ( "test:101", "Time", sourceOE2 );
		param2.addOntologyTerm ( oe2 );
		protocol.addParameter ( param2 );


		// Let's attach few components
		//
		ProtocolComponent comp1 = new ProtocolComponent ( "Chain saw" );
			ReferenceSource sourceOE3 = new ReferenceSource ( "My fancy test ontology 3" );
			sourceOE3.setAcc ( "bii:tests:MY-SRC-33" );
			OntologyTerm oe3 = new OntologyTerm ( "biionto:303", "test OE", source );
		comp1.addOntologyTerm ( oe3 );
		protocol.addComponent ( comp1 );

		ProtocolComponent comp2 = new ProtocolComponent ( "Chain saw" );
		comp2.addOntologyTerm ( oe2 );
		protocol.addComponent ( comp2 );



		transaction.begin ();
		Protocol protocolDB = persister.persist ( protocol );
		transaction.commit ();

		assertNotNull ( "Uh! No object returned by the persister!", protocolDB );
		assertEquals ( "Gosh! The persister should return the same persisted object!", protocol, protocolDB );
		assertNotNull ( "Argh! The persisted protocol should have a non null ID", protocol.getId () );
		assertTrue ( "Uhm... The returned object apparently is not in the DB", entityManager.contains ( protocolDB ));
		assertNotNull ( "The New Protocol should have a non null accession!", protocolDB.getAcc () );

		// Type
		//
		ProtocolType typeDB = protocolDB.getType ();
		assertNotNull ( "Urp! No type persisted for the protocol!", protocolDB );
		assertNotNull ( "Ouch! The persisted protocol type should have a non null ID", type.getId () );
		assertTrue ( "Ouch! The protocol type is not in the DB!", entityManager.contains ( typeDB ) );
		assertEquals ( "Argh! The persisted type should be == the initial one", type, typeDB );


		ReferenceSource typeSrcDB = typeDB.getSource ();
		assertNotNull ( "Argh! Persisted protocol type has not a source!", typeSrcDB );
		assertTrue ( "Ouch! The protocol type is not in the DB!", entityManager.contains ( typeDB ) );
		assertEquals ( "Urp! Persisted protocol type has bad source", source.getName (), typeSrcDB.getName () );


		// Parameters
		//
		Collection<Parameter> params = protocolDB.getParameters ();
		assertNotNull ( "Oh no! No parameter from the persisted protocol!", params );
		assertEquals ( "Oh no! The persisted protocol does not return correct no. of params !", 2, params.size() );

		// Param 1
		FreeTextTerm param1DB = (FreeTextTerm) CollectionUtils.find ( params, new FreeTextSelector ( param1 ) );
		assertNotNull ( "Urp! The param " + param1 + " was not persisted with the protocol!", param1DB );
		assertNotNull ( "The term: " + param1DB + "should have an ID!", param1DB );

			Collection<OntologyTerm> oes1 = param1DB.getOntologyTerms ();
			assertNotNull ( "Oh no! The persisted parameter" + param1DB + " does not return OEs!", oes1 );
			assertEquals ( "Oh no! The persisted parameter does not return correct no. of OEs !", 1, oes1.size() );

		// Param 2
		FreeTextTerm param2DB = (FreeTextTerm) CollectionUtils.find ( params, new FreeTextSelector ( param2 ) );
		assertNotNull ( "Urp! The param " + param2 + " was not persisted with the protocol!", param2DB );
		assertNotNull ( "The term: " + param2DB + "should have an ID!", param2DB.getId () );

			Collection<OntologyTerm> oes2 = param2DB.getOntologyTerms ();
			assertNotNull ( "Oh no! The persisted parameter" + param2DB + " does not return OEs!", oes2 );
			assertEquals ( "Oh no! The persisted parameter" + param2DB + " does not return correct no. of OEs !", 1, oes2.size() );
			OntologyTerm oeDB2 = oes2.iterator ().next ();
			assertEquals ( "Urp! The term " + oe2 + "was not persisted with the Parameter!", oe2.getAcc (), oeDB2.getAcc() );
			assertEquals ( "Wrong retrieved ID for the term : " + oeDB2 + "!", new Long ( 507 ), oeDB2.getId () );



		// Components
		//
		Collection<ProtocolComponent> comps = protocolDB.getComponents ();
		assertNotNull ( "Oh no! No component from the persisted protocol!", comps );
		assertEquals ( "Oh no! The persisted protocol does not return correct no. of components!", 2, comps.size() );

		// Component 1
		FreeTextTerm comp1DB = (FreeTextTerm) CollectionUtils.find ( comps, new FreeTextSelector ( comp1 ) );
		assertNotNull ( "Urp! The component " + comp1 + " was not persisted with the protocol!", comp1DB );
		assertNotNull ( "The term: " + param1DB + "should have an ID!", comp1DB );

			Collection<OntologyTerm> coes1 = comp1DB.getOntologyTerms ();
			assertNotNull ( "Oh no! The persisted parameter" + comp1DB + " does not return OEs!", coes1 );
			assertEquals ( "Oh no! The persisted component does not return correct no. of OEs !", 1, coes1.size() );

		// Component 2
		FreeTextTerm comp2DB = (FreeTextTerm) CollectionUtils.find ( comps, new FreeTextSelector ( comp2 ) );
		assertNotNull ( "Urp! The param " + param2 + "was not persisted with the protocol!", comp2DB );
		assertNotNull ( "The term: " + comp2DB + "should have an ID!", comp2DB.getId () );

			Collection<OntologyTerm> coes2 = param2DB.getOntologyTerms ();
			assertNotNull ( "Oh no! The persisted parameter" + comp2DB + " does not return OEs!", coes2 );
			assertEquals ( "Oh no! The persisted parameter" + comp2DB + " does not return correct no. of OEs !", 1, coes2.size() );
			OntologyTerm coeDB2 = coes2.iterator ().next ();
			assertEquals ( "Urp! The term " + oe2 + "was not persisted with the Component!", oe2.getAcc (), oeDB2.getAcc () );
			assertEquals ( "Wrong retrieved ID for the component term : " + coeDB2 + "!", new Long ( 507 ), coeDB2.getId () );


		out.println ( "\n _______________ /end: ProtocolPersistanceTest, Testing new Protocol ____________________________\n\n" );

	}



	@Test
	public void testPersistExisting () throws Exception
	{

		out.println ( "\n\n\n _______________ ProtocolPersistanceTest, Testing existing Protocol ____________________________\n" );

		Protocol protocol = new Protocol ( null, null );
		protocol.setAcc ( "bii:proto:999" );

		Protocol protocolDB = persister.persist ( protocol );
		transaction.commit ();


		assertNotNull ( "Uh! No object returned by the persister!", protocolDB );
		assertTrue ( "Gosh! The persister should return a different existing protocol!", protocol != protocolDB );
		assertEquals ( "Argh! Bad ID for the persisted protocol", new Long ( -10 ), protocolDB.getId () );
		assertTrue ( "Uhm... The returned object apparently is not in the DB", entityManager.contains ( protocolDB ));


		// Type
		ProtocolType typeDB = protocolDB.getType ();
		assertNotNull ( "Urp! No type persisted for the protocol!", protocolDB );
		assertEquals ( "Ouch! Bad ID for the persisted Protocol Type", new Long ( 506 ), typeDB.getId () );
		assertTrue ( "Ouch! The protocol type is not in the DB!", entityManager.contains ( typeDB ) );



		// Parameters
		//
		Collection<Parameter> params = protocolDB.getParameters ();
		assertNotNull ( "Oh no! No param types from the persisted protocol!", params );
		assertEquals ( "Oh no! The persisted protocol does not return correct no. of params !", 2, params.size() );

		// Param 1
		Parameter param1 = new Parameter ( null, 0 );
		param1.setId ( new Long ( -1 ) );
		FreeTextTerm param1DB = (FreeTextTerm) CollectionUtils.find ( params, new FreeTextSelector ( param1 ) );
		assertNotNull ( "Urp! The existing protocol should have parameter " + param1 + "!", param1DB );

			Collection<OntologyTerm> oes1 = param1DB.getOntologyTerms ();
			assertNotNull ( "Oh no! The persisted parameter" + param1DB + " does not return OEs!", oes1 );
			assertEquals ( "Oh no! The persisted FreeTextTerm does not return correct no. of OEs !", 1, oes1.size() );
			OntologyTerm oeDB1 = oes1.iterator ().next ();
			assertEquals ( "Wrong retrieved ID for the term : " + oeDB1 + "!", new Long ( -2 ), oeDB1.getId () );



		// Param 2
		Parameter param2 = new Parameter ( null, 0 );
		param2.setId ( new Long ( -2 ) );
		FreeTextTerm param2DB = (FreeTextTerm) CollectionUtils.find ( params, new FreeTextSelector ( param2 ) );
		assertNotNull ( "Urp! The param " + param2 + "was not persisted with the protocol!", param2DB );

			Collection<OntologyTerm> oes2 = param2DB.getOntologyTerms ();
			assertNotNull ( "Oh no! The persisted parameter" + param2DB + " does not return OEs!", oes2 );
			assertEquals ( "Oh no! The persisted parameter" + param2DB + " does not return correct no. of OEs !", 1, oes2.size() );
			OntologyTerm oeDB2 = oes2.iterator ().next ();
			assertEquals ( "Wrong retrieved ID for the term : " + oeDB2 + "!", new Long ( 507 ), oeDB2.getId () );

		out.println ( "\n _______________ /end: ProtocolPersistanceTest, Testing existing Protocol ____________________________\n\n" );

	}

}
