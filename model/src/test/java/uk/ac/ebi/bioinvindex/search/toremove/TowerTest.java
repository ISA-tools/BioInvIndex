//package uk.ac.ebi.bioinvindex.search.toremove;
//
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//import org.junit.AfterClass;
//import org.junit.Test;
//import uk.ac.ebi.bioinvindex.model.toremove.*;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//
///**
// * User: Nataliya Sklyar (nsklyar@ebi.ac.uk)
// * Date: Nov 9, 2007
// */
//public class TowerTest {
//
//
//	protected static EntityManager entityManager;
//
//	private static EntityManagerFactory entityManagerFactory;
//
//
//	public TowerTest() throws Exception {
//
//		entityManagerFactory = Persistence.createEntityManagerFactory("BIIEntityManager");
//		entityManager = entityManagerFactory.createEntityManager();
//
//	}
//
//	@AfterClass
//	public static void close() {
//		entityManager.close();
//		entityManagerFactory.close();
//	}
//
//	@Test
//	public void testEmbeddedIndexing() throws Exception {
//		Tower tower = new Tower();
//		tower.setName("JBoss tower");
//		Address a = new Address();
//		a.setStreet("Tower place");
//		a.getTowers().add(tower);
//		tower.setAddress(a);
//		Owner o = new Owner();
//		o.setName("Atlanta Renting corp");
//		a.setOwnedBy(o);
//		o.setAddress(a);
//
//		TowerPropertyValue value1 = new TowerPropertyValue(new TowerProperty("color"), "red");
//		TowerPropertyValue value2 = new TowerPropertyValue(new TowerProperty("size"), "very big");
//
//		tower.addPropertyValue(value1);
//		tower.addPropertyValue(value2);
//
//		Session s = (Session) entityManager.getDelegate();
//		Transaction tx = s.beginTransaction();
//		s.persist(tower);
//		tx.commit();
//
//
////		FullTextSession session = Search.createFullTextSession(s);
////		QueryParser parser = new QueryParser("id", new StandardAnalyzer());
////		Query query;
////		List result;
////
////		query = parser.parse("address.street:place");
////		result = session.createFullTextQuery(query).list();
////		assertEquals("unable to find property in embedded", 1, result.size());
////
////		query = parser.parse("address.ownedBy_name:renting");
////		result = session.createFullTextQuery(query, Tower.class).list();
////		assertEquals("unable to find property in embedded", 1, result.size());
////
////		query = parser.parse("address.id:" + a.getId().toString());
////		result = session.createFullTextQuery(query, Tower.class).list();
////		assertEquals("unable to find property by id of embedded", 1, result.size());
////
////		s.clear();
////
////		tx = s.beginTransaction();
////		Address address = (Address) s.get(Address.class, a.getId());
////		address.getOwnedBy().setName("Buckhead community");
////		tx.commit();
////
////
////		s.clear();
////
////		session = Search.createFullTextSession(s);
////
////		query = parser.parse("address.ownedBy_name:buckhead");
////		result = session.createFullTextQuery(query, Tower.class).list();
////		assertEquals("change in embedded not reflected in root index", 1, result.size());
////
////		s.clear();
////
////		tx = s.beginTransaction();
////		s.delete(s.get(Tower.class, tower.getId()));
////		tx.commit();
////
////		s.close();
//
//	}
//
//}
