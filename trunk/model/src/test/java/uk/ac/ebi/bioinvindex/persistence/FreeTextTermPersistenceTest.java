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
import org.apache.commons.lang.StringUtils;
import org.dbunit.operation.DatabaseOperation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.term.Design;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;

import static java.lang.System.out;
import java.sql.Timestamp;
import java.util.Collection;


public class FreeTextTermPersistenceTest extends TransactionalDBUnitEJB3DAOTest
{

	private FreeTextTermPersister<Design> persister;

	static class OESelector implements Predicate
	{
		private OntologyTerm oe;

		public OESelector ( OntologyTerm oe ) {
			this.oe = oe;
		}

		public boolean evaluate ( Object object ) {
			if ( oe == null ) return object == null;
			if ( !( object instanceof OntologyTerm ) ) return false;
			OntologyTerm oe1 = (OntologyTerm) object;
			if ( !StringUtils.equals ( oe.getAcc (), oe1.getAcc () ) ) return false;
			ReferenceSource src = oe.getSource (), src1 = oe1.getSource ();
			if ( src == null ) return src1 == null;
			return StringUtils.equals ( src.getAcc (), src1.getAcc () );
		}

	}

	public FreeTextTermPersistenceTest() throws Exception {
		super();
	}

	@Before
	public void initPersister () 
	{
		// Needs to be instantiated here, so that the internal cache for the ontology terms is cleared
		// before every test.
		//
		persister = new FreeTextTermPersister<Design> (
				DaoFactory.getInstance(entityManager), new Timestamp ( System.currentTimeMillis () )  ) {};
	}
	
	
	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_persistence.xml";
	}



	@Test
	public void testPersistFreeTerm () throws Exception
	{

		out.println ( "\n\n\n _______________ FreeTextTermPersistanceTest, Basic Test ____________________________\n" );

		Design term = new Design ( "test reference" );

		// Let's attach some OEs

		// new OE
		ReferenceSource source = new ReferenceSource ( "My fancy test ontology 2" );
		source.setAcc ( "bii:tests:MY-SRC-2" );
		OntologyTerm oe1 = new OntologyTerm ( "biionto:101", "test OE", source );
		term.addOntologyTerm ( oe1 );

		// Existing OE
		source = new ReferenceSource ( "TEST ONTOLOGY" );
		source.setAcc ( "BII-1" );
		OntologyTerm oe2 = new OntologyTerm ( "OBI-EO1", "organism", source );
		term.addOntologyTerm ( oe2 );

		Design termNew = persister.persist ( term );
		transaction.commit ();
		session.flush ();


		assertNotNull ( "Uh! Oh! Persister returns null!", termNew );
		assertTrue ( "Ouch! Cannot find the persisted term in the DB!", entityManager.contains(termNew) );
		assertTrue ( "Argh! The persisted term and the original one should be the same!", term == termNew );
		assertNotNull ( "Burp! The persisted term should have an ID!", termNew.getId() );

		Collection<OntologyTerm> oes = termNew.getOntologyTerms ();
		assertNotNull ( "Oh no! The persisted FreeTextTerm does not return OEs!", oes );
		assertEquals ( "Oh no! The persisted FreeTextTerm does not return correct no. of OEs !", 2, oes.size() );

		OntologyTerm oeDB1 = (OntologyTerm) CollectionUtils.find ( oes, new OESelector ( oe1 ) );
		assertNotNull ( "Urp! The term " + oe1 + "was not persisted with the FreeText!", oeDB1 );
		assertNotNull ( "The term: " + oeDB1 + "should have an ID!", oeDB1 );

		OntologyTerm oeDB2 = (OntologyTerm) CollectionUtils.find ( oes, new OESelector ( oe2 ) );
		assertNotNull ( "Urp! The term " + oe2 + "was not persisted with the FreeText!", oeDB2 );
		assertEquals ( "Wrong retrieved ID for the term : " + oeDB2 + "!", new Long ( 501 ), oeDB2.getId () );


		out.println ( "\n _______________ /end: FreeTextTermPersistanceTest, Basic Test ____________________________\n\n" );

	}

}
