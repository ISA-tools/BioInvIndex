//package uk.ac.ebi.bioinvindex.search;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import org.junit.Test;
//import org.junit.Ignore;
//import uk.ac.ebi.bioinvindex.dao.ejb3.InvestigationEJB3DAO;
//import uk.ac.ebi.bioinvindex.model.impl.Investigation;
//import uk.ac.ebi.bioinvindex.model.impl.xref.InvestigationXrefImpl;
//import uk.ac.ebi.bioinvindex.model.impl.xref.ReferenceSource;
//import uk.ac.ebi.bioinvindex.search.hibernatesearch.InvestigationFreeTextSearchImpl;
//import uk.ac.ebi.bioinvindex.utils.mock.TestSetIndexBuilder;
//import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;
//
//import javax.persistence.EntityTransaction;
//import static java.lang.System.out;
//import java.util.List;
//
///**
// * User: Brandizi
// * Date: Oct 2007
// */
//public class InvestigationSearchTest extends DBUnitEJB3DAOTest
//{
//
//	private InvestigationEJB3DAO dao;
//	private InvestigationFreeTextSearchImpl search;
//
//
//	public InvestigationSearchTest () throws Exception
//	{
//		super();
//
//		dao = new InvestigationEJB3DAO();
//		dao.setEntityManager ( entityManager );
//
//		search = new InvestigationFreeTextSearchImpl ();
//		search.setEntityManager ( entityManager );
//	}
//
//
////	@Test
////	public void testAll() throws Exception
////	{
////		Investigation inv = new Investigation ( "A wonderful Test Investigation" );
////
////		EntityTransaction tnx = entityManager.getTransaction();
////
////		tnx = entityManager.getTransaction();
////		tnx.begin ();
////
////		// Include an Xref too
////		InvestigationXrefImpl xref = new InvestigationXrefImpl ( "FOO.INV.X-REF" );
////		xref.setSource ( new ReferenceSource ( "FOO.INV.SOURCE" ) );
////		inv.addXref ( xref );
////		Long id = dao.save ( inv );
////		tnx.commit ();
////
////
////		List<Investigation> result = search.searchAllFields ( "wonderful*" );
////
////		assertNotNull ( "Urp! Null result from searchAllFields()", result );
////		assertTrue ( "Ops! Empty result from searchAllFields()", result.size () > 0);
////		assertTrue ( "Uh?! The text search result is not an investigation!", result.get ( 0 ) instanceof Investigation );
////
////		Investigation testInv = (Investigation) result.get ( 0 );
////		assertNotNull ( "Uh?! Null result in result from searchAllField()", testInv );
////
////		out.println ( "\n\n***** The indexed investigation: " + testInv );
////
////	}
//
//
////  @Test
////	@Ignore
////	public void testAllFromDb() throws Exception
////	{
////		// We need reindexing the existing test data before searching them
////		out.println ( "*** Reindexing the database" );
////		TestSetIndexBuilder idxBuild = new TestSetIndexBuilder ();
////		idxBuild.setEntityManager ( this.entityManager );
////		idxBuild.indexAllInvestigations ();
////		out.println ( "\n\n*** Reindexing done" );
////
////		List<Investigation> result = search.searchAllFields ( "Test*" );
////
////		assertNotNull ( "Urp! Null result from searchAllFields()", result );
////		assertTrue ( "Ops! Empty result from searchAllFields()", result.size () > 0);
////		assertTrue ( "Uh?! The text search result is not an investigation!", result.get ( 0 ) instanceof Investigation );
////
////		Investigation testInv = result.get ( 0 );
////		assertNotNull ( "Uh?! Null result in result from searchAllField()", testInv );
////
////		out.println ( "\n\n***** The indexed investigation: " + testInv );
////
////	}
//
//
//}
