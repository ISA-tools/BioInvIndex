package uk.ac.ebi.bioinvindex.dao;

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

import junit.framework.TestCase;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.hibernate.search.jpa.FullTextEntityManager;
import uk.ac.ebi.bioinvindex.dao.ejb3.StudyEJB3DAO;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.term.Design;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

/**
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Sep 6, 2007
 */
//ToDo: it's just to try EntityManager. REMOVE this class!!!!!
public class Test extends TestCase {

	public void test1() throws Exception {
		try {
			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("BIIEntityManager");

			EntityManager entityManager = entityManagerFactory.createEntityManager();

			StudyEJB3DAO dao = new StudyEJB3DAO(entityManager);


			Study study = new Study();
			study.setAcc("22222");
			study.setTitle("rrrr");
			study.setDescription("ddddd");

//            Design design = new Design("design");
//            study.setDesign(design);

//            dao.save(study);

			EntityTransaction tnx = entityManager.getTransaction();
			tnx.begin();
//            entityManager.persist(study);

			Long aLong = dao.save(study);

			System.out.println("aLong = " + aLong);
			tnx.commit();

//            tnx = entityManager.getTransaction();
//            tnx.begin();
//
//            Study study1 = entityManager.find(Study.class, aLong);
//            System.out.println("study1 = " + study1);
//
//            tnx.commit();

			entityManager.close();

			entityManager = entityManagerFactory.createEntityManager();

			tnx = entityManager.getTransaction();
			tnx.begin();

			dao.setEntityManager(entityManager);
			List<Study> list = dao.getAll();

			System.out.println("list = " + list);

			System.out.println("list.size() = " + list.size());
			tnx.commit();
			entityManagerFactory.close();
		} catch (Exception e) {
			System.out.println("EXception");
			e.printStackTrace();
		}
	}

	public void test2() throws Exception {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("BIIEntityManager");

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();

		Design design = new Design("design");
		entityManager.persist(design);

		tnx.commit();
		entityManager.close();

		EntityManager entityManager1 = entityManagerFactory.createEntityManager();
		EntityTransaction tnx1 = entityManager1.getTransaction();
		tnx1.begin();

		List list = entityManager1.createQuery("select d from Design d").getResultList();

		System.out.println("list = " + list);
		tnx1.commit();

		entityManager1.close();
		entityManagerFactory.close();


	}


	public void testFullTextSearch() throws Exception {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("BIIEntityManager");

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		StudyEJB3DAO dao = new StudyEJB3DAO(entityManager);

		Study study = new Study();
		study.setAcc("22222");
		study.setTitle("title1 rrrrr");
		study.setDescription("ddddd");

		Design design = new Design("design");
		study.setDesign(design);

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
//            entityManager.persist(study);

		entityManager.persist(design);
		Long aLong = dao.save(study);

		System.out.println("aLong = " + aLong);
		tnx.commit();

		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.createFullTextEntityManager(entityManager);

		QueryParser queryParser = new QueryParser("title", new StopAnalyzer());
		org.apache.lucene.search.Query luceneQuery = queryParser.parse("title1*");
		javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery);

//		Session session = (Session) entityManager.getDelegate();
//		FullTextSession ftSession = org.hibernate.search.Search.createFullTextSession(session);
//		List list = ftSession.createFullTextQuery(luceneQuery, Study.class)
//				.setMaxResults(100)
//				.list();

		List list = fullTextQuery.getResultList();
		System.out.println("list.size() = " + list.size());

		queryParser = new QueryParser("all", new StopAnalyzer());
		luceneQuery = queryParser.parse("design");
//		javax.persistence.Query fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery);

		list = fullTextEntityManager.createFullTextQuery(luceneQuery)
				.setMaxResults(100)
				.getResultList();

//		List list = fullTextQuery.getResultList();
		System.out.println("list.size() = " + list.size());
	}


}
