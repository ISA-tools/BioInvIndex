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

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.dao.StudyDAO;
import uk.ac.ebi.bioinvindex.dao.UserDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Protocol;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.term.ContactRole;
import uk.ac.ebi.bioinvindex.model.term.Design;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.model.term.ProtocolType;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;


public class StudyPersistenceTest extends TransactionalDBUnitEJB3DAOTest
{
	private StudyPersister persister;


	public StudyPersistenceTest() throws Exception {
		super();
		persister = new StudyPersister ( DaoFactory.getInstance ( entityManager ), new Timestamp ( System.currentTimeMillis () ) );
	}

	@Before
	public void initPersister ()
	{
		// Needs to be instantiated here, so that the internal cache for the ontology terms is cleared
		// before every test.
		//
		persister = new StudyPersister ( DaoFactory.getInstance ( entityManager ), new Timestamp ( System.currentTimeMillis () ) );
	}

	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_persistence.xml";
	}

	@Test
	public void testPersistBasic () throws Exception
	{

		out.println ( "\n\n\n _______________  StudyPersistanceTest, Testing new Study  ____________________________\n" );

		Study study = new Study ( "My super-scientific experiment" );

			Design design = new Design ( "Whip the operator to see its performance improves" );
			ReferenceSource designSource = new ReferenceSource ( "TEST ONTOLOGY" );
			designSource.setAcc ( "BII-1" );
			OntologyTerm oe = new OntologyTerm ( "biionto:OperatorPerturbationDesign", "Operator Perturbation Design", designSource );
			design.addOntologyTerm ( oe );
			study.addDesign ( design );

			design = new Design ( "Whip the PI too if nothing changes" );
			study.addDesign ( design );


			// Protocol 1 (does not exist)
			ReferenceSource source = new ReferenceSource ( "My fancy test ontology 2" );
			source.setAcc ( "bii:tests:MY-SRC-2" );
			ProtocolType type = new ProtocolType ( "proto:type:999", "My Test Proto Type", source ) ;

			Protocol protocol1 = new Protocol ( "My New Protocol", type );
			protocol1.setAcc ( "A-NEW-PROTO-99" );

			// Let's attach some parameters
			Parameter param1 = new Parameter ( "Time Before Killing the Operator", 0 );
				ReferenceSource sourceOE1 = new ReferenceSource ( "My fancy test ontology 3" );
				sourceOE1.setAcc ( "bii:tests:MY-SRC-3" );
				OntologyTerm oe1 = new OntologyTerm ( "biionto:101", "test OE", source );
			param1.addOntologyTerm ( oe1 );
			protocol1.addParameter ( param1 );

			Parameter param2 = new Parameter ( "Time To get rid of the corpse", 1 );
				ReferenceSource sourceOE2 = new ReferenceSource ( "101 Ontology" );
				sourceOE2.setAcc ( "BII-101" );
				OntologyTerm oe2 = new OntologyTerm ( "test:101", "Time", sourceOE2 );
			param2.addOntologyTerm ( oe2 );
			protocol1.addParameter ( param2 );

			study.addProtocol ( protocol1 );


			// Protocol 2 (exists)
			Protocol protocol2 = new Protocol ( null, null );
			protocol2.setAcc ( "bii:proto:999" );
			study.addProtocol ( protocol2 );

			// Contacts
			Contact contact = new Contact ( "Mr", null, "Foo", "someone@somewhere.net" );
				ReferenceSource roleSrc = new ReferenceSource ( null ); roleSrc.setAcc (  "BII-1" );
				ContactRole contactRole = new ContactRole ( "bii:fooContactRole", "My Contact Role", roleSrc );
			  contact.addRole ( contactRole );
				contactRole = new ContactRole ( "bii:fooContactRole1", "My Contact Role 1", roleSrc );
			  contact.addRole ( contactRole );

			study.addContact ( contact );

			// TODO: publications, needs role persistence


		// Let's go with the persistence job!
		//

		Study studyDB = persister.persist ( study );
		transaction.commit ();

		assertNotNull ( "Oh! No study returned by the persister!", studyDB );
		assertEquals ( "Ouch! The study rerturned by the persister should be the original one!", study, studyDB );
		assertTrue ( "Argh! Cannot find the persisted study in the DB!", entityManager.contains ( studyDB ) );
		assertNotNull ( "Urp! The study should have an ID", studyDB.getId () );


		Collection<Design> designsDB = studyDB.getDesigns ();
		assertNotNull (  "Ops! The retuned study has no a design!", designsDB );
		assertEquals ( "Oh no! Bad no. of designs persisted!", 2, designsDB.size () );
		for ( Design designDB: designsDB )
			assertNotNull ( "Urp! The study design should have an ID", designDB.getId () );

		final String dval = "Whip the operator to see its performance improves";
		Design designDB = (Design) CollectionUtils.find ( designsDB, new Predicate () {
			public boolean evaluate ( Object object ) {
				return dval.equals ( ((Design) object ).getValue () );
			}
		});
		assertNotNull ( "Arg! I cannot find a design which shoulb have been persisted ('" + dval + "')", designDB );
		Collection<OntologyTerm> oesDesign = designDB.getOntologyTerms ();
		assertNotNull ( "Oh no! The persisted parameter" + designDB + " does not return OEs!", oesDesign );
		assertEquals ( "Oh no! The persisted FreeTextTerm does not return correct no. of OEs !", 1, oesDesign.size() );
		OntologyTerm oeDB1 = oesDesign.iterator ().next ();
		assertEquals ( "Wrong retrieved ID for the term : " + oeDB1 + "!", new Long ( -3 ), oeDB1.getId () );
		assertEquals ( "Oh no! The retrieved study design's OE has bad accession!", "biionto:OperatorPerturbationDesign", oeDB1.getAcc () );

		final String dval1 = "Whip the PI too if nothing changes";
		designDB = (Design) CollectionUtils.find ( designsDB, new Predicate () {
			public boolean evaluate ( Object object ) {
				return dval1.equals ( ((Design) object ).getValue () );
			}
		});
		assertNotNull ( "Arg! I cannot find a design which shoulb have been persisted ('" + dval1 + "')", designDB );


		// Protocols
		//
		Collection<Protocol> protocols = study.getProtocols ();
		assertNotNull ( "Ugh! No protocols from the study!", protocols );
		assertEquals ( "Urgh! Wrong no. of protocols returned for the study!", 2, protocols.size () );

		// Protocol 1
		//
		Protocol protocol1DB = (Protocol) CollectionUtils.find ( protocols,
			new Predicate () {
				public boolean evaluate ( Object object ) {
					return "A-NEW-PROTO-99".equals ( ((Protocol) object).getAcc () );
				}
		});
		assertNotNull ( "Ough! Cannot find the protocol A-NEW-PROTO-99 in the study!", protocol1DB );

		// Parameters
		//
		Collection<Parameter> params1 = protocol1DB.getParameters ();
		assertNotNull ( "Oh no! No param types from the persisted protocol!", params1 );
		assertEquals ( "Oh no! The persisted protocol does not return correct no. of params !", 2, params1.size() );

		// Protocol 2
		//
		Protocol protocol2DB = (Protocol) CollectionUtils.find ( protocols,
			new Predicate () {
				public boolean evaluate ( Object object ) {
					return "bii:proto:999".equals ( ((Protocol) object).getAcc () );
				}
		});
		assertNotNull ( "Ough! Cannot find the protocol bii:proto:999 in the study!", protocol2DB );

		// Params
		//
		Collection<Parameter> params2 = protocol2DB.getParameters ();
		assertNotNull ( "Oh no! No param types from the persisted protocol!", params2 );
		assertEquals ( "Oh no! The persisted protocol does not return correct no. of params !", 2, params2.size() );

		// Contact
		Collection<Contact> contactsDB = studyDB.getContacts ();
		assertEquals ( "Arg! Wrong no. of contacts persisted!", 1, contactsDB.size () );
		Contact contactDB = contactsDB.iterator ().next ();
		assertNotNull ( "Oh no! Persisted contact has not an ID!", contactDB.getId () );
		assertEquals ( "Oh no! Persisted contact has wrong email", "someone@somewhere.net", contactDB.getEmail () );
		Collection<ContactRole> rolesDB = contactDB.getRoles ();
		assertEquals (  "Argh! Bad no. of contact roles persisted", 2, rolesDB.size ());
		Iterator<ContactRole> rolesItr = rolesDB.iterator ();
		ContactRole roleDB = rolesItr.next ();
		Long id = roleDB.getId ();
		assertTrue ( "Arg! Wrong role #0 for the persisted contact", id != null );
		ReferenceSource roleSrcDB = roleDB.getSource ();
		assertEquals ( "Arg! Wrong role source for the persisted contact", (long) 100, roleSrcDB.getId ().longValue()   );
		roleDB = rolesItr.next ();
		Long id1 = roleDB.getId ();
		assertNotNull ( "Arg! Wrong role #1 for the persisted contact", id1 != null || ( id == -7 || id1 == -7 ) );
		roleSrcDB = roleDB.getSource ();
		assertEquals ( "Arg! Wrong role source for the persisted contact", (long) 100, roleSrcDB.getId ().longValue()   );

		out.println ( "\n\nPersisted study: " + studyDB );

		out.println ( "\n\n\n _______________  /end: StudyPersistanceTest, Testing new Study  ____________________________\n" );

	}


	@Test
	public void testPersistBasicWithUsers() throws Exception {

		Study study = new Study("My super-scientific experiment with users");
        study.setAcc("ssewu-1");

		UserDAO userDAO = DaoFactory.getInstance(entityManager).getUserDAO();
		User user1 = userDAO.getByUsername("test_user");
		assertNotNull(user1);

		study.addUser(user1);

		Study studyDB = persister.persist(study);
		assertNotNull(studyDB);

        System.out.println("___CHECKING USER COUNT: " + studyDB.getUsers().size());

		assertEquals(1, studyDB.getUsers().size());

		StudyDAO studyDAO = DaoFactory.getInstance(entityManager).getStudyDAO();

        List<String> accessions = studyDAO.getPublicStudyAccs();

        for(String accession : accessions) {
            System.out.println("\t ACCESSION FOR AVAILABLE STUDIES IS: " + accession);
        }

        Study testStudy = studyDAO.getByAcc(studyDB.getAcc());
		assertNotNull(testStudy);


        System.out.println("___CHECKING USER COUNT AGAIN ON TEST STUDY: " + studyDB.getUsers().size());
		assertEquals(1, testStudy.getUsers().size());

		transaction.commit();
	}

	@Test
	public void testPersistStudyAndInvestigation () throws Exception
	{

		out.println ( "\n\n\n _______________  StudyPersistanceTest, Testing Study/Investigation  _____________________\n" );

		Study study = new Study ( "My super-scientific experiment" );
		study.setDescription ( "A new fantastic study used for tests" );

			Design design = new Design ( "Whip the operator to see its performance improves" );
			ReferenceSource designSource = new ReferenceSource ( null );
			designSource.setAcc ( "BII-1" );
			OntologyTerm oe = new OntologyTerm ( "biionto:OperatorPerturbationDesign", null, designSource );
			design.addOntologyTerm ( oe );
			study.addDesign ( design );

			Investigation investigation1 = new Investigation ( "The New Test Investigation" );
				investigation1.setDescription ( "The Investigation used for testing" );
			study.addInvestigation ( investigation1 );

			Investigation investigation2 = new Investigation ( "" );
				investigation2.setAcc ( "bii:test:inv:01" );
			study.addInvestigation ( investigation2 );


			Study studyDB = persister.persist ( study );
			transaction.commit ();


			assertNotNull ( "Arg! No study returned by the persister!", studyDB );
			assertEquals ( "Ouch! The study retuned by the persister should be == the original one", studyDB, study );
			assertNotNull ( "Ops! null ID for the persisted study", studyDB.getId() );
			assertTrue ( "Oh! The study is not in the DB!", entityManager.contains ( studyDB ) );

			Collection<Investigation> investigationsDB = studyDB.getInvestigations ();
			assertEquals ( "Gosh! Wrong # of investigations associated to the persisted study!", 2, investigationsDB.size () );

			final String title1 = investigation1.getTitle ();
			Investigation investigation1DB = (Investigation) CollectionUtils.find ( investigationsDB, new Predicate () {
				public boolean evaluate ( Object object ) {
					return title1.equals ( ((Investigation) object).getTitle () );
				}
			});
			assertNotNull ( "Urp! The investigation \"" + title1 + "\" was not persisted!", investigation1DB  );
			assertNotNull ( "Argh! The investigation \"" + title1 + "\" should have an ID", investigation1DB.getId () );
			assertNotNull ( "Urp! The investigation \"" + title1 + "\" should have an accession", investigation1DB.getAcc () );
			assertTrue ( "Oh! The investigation \"" + title1 + "\" is not in the DB!", entityManager.contains ( investigation1DB ) );
			assertTrue ( "Urgh! The investigation " + title1 + "doesn't contain the persisted study!", investigation1DB.getStudies ().contains ( studyDB ) );

			final String acc2 = investigation2.getAcc ();
			Investigation investigation2DB = (Investigation) CollectionUtils.find ( investigationsDB, new Predicate () {
				public boolean evaluate ( Object object ) {
					return acc2.equals ( ((Investigation) object).getAcc () );
				}
			});
			assertNotNull ( "Urp! The investigation \"" + acc2 + "\" was not persisted!", investigation2DB  );
			assertNotNull ( "Argh! The investigation \"" + acc2 + "\" should have an ID", investigation2DB.getId () );
			assertNotNull ( "Urp! The investigation \"" + acc2 + "\" should have an accession", investigation2DB.getAcc () );
			assertTrue ( "Oh! The investigation \"" + acc2 + "\" is not in the DB!", entityManager.contains ( investigation2DB ) );
			assertTrue ( "Urgh! The investigation " + acc2 + "doesn't contain the persisted study!", investigation2DB.getStudies ().contains ( studyDB ) );


			out.println ( "\n\nPersisted study: " + studyDB );

			out.println ( "\n\nAnd its investigations:" );
			for ( Investigation investigation: studyDB.getInvestigations () )
				out.println ( investigation );



		out.println ( "\n\n\n _______________  StudyPersistanceTest, Testing Study/Investigation  _____________________\n" );

	}


}
