package uk.ac.ebi.microarray.seamtest.test;
//
//import org.jboss.seam.log.Log;
//import org.jboss.seam.mock.SeamTest;
//import org.testng.annotations.Test;
//import uk.ac.ebi.bioinvindex.model.impl.Study;
//import uk.ac.ebi.bioinvindex.model.interfaces.Study;
//import uk.ac.ebi.bioinvindex.StudyHomeImpl;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//import java.util.List;
//
//public class StudyTest extends SeamTest {
//	Log log = org.jboss.seam.log.Logging.getLog(StudyTest.class);
//
//	@Test
//	public void testSaveStudy() throws Exception {
//		new FacesRequest("/home.xhtml") {
//
//
//			@Override
//			protected void updateModelValues() throws Exception {
//				setValue("#{study.title}", "Test Study");
//			}
//
//			@Override
//			protected void invokeApplication() {
//				assert getValue("#{study.title}").equals("Test Study");
//				assert invokeMethod("#{manager.saveStudy(study)}") == null;
//				assert getValue("#{study.title}") == null;
//			}
//
//			@Override
//			protected void renderResponse() {
//				List<Study> studies =
//						(List<Study>) getValue("#{studies}");
//				assert studies != null;
//				assert studies.get(studies.size() - 1)
//						.getTitle().equals("Test Study");
//			}
//
//		}.run();
//
//	}
//
//	//Save method should be tested for DAO class
//	@Test
//	public void unitTestSaveStudy() throws Exception {
//
////		StudyHomeImpl studyHome = new StudyHomeImpl();
////		EntityManagerFactory emf = Persistence.createEntityManagerFactory("seamtest");
////		EntityManager entityManager = emf.createEntityManager();
////		setField(studyHome, "entityManager", entityManager);
////
////		Study study = new Study();
////		study.setTitle("Test Study");
////
////		setField(studyHome, "title", "Test Study");
////		// getUserTransaction().begin();
////		entityManager.getTransaction().begin();
////		studyHome.saveStudy();
////		entityManager.getTransaction().commit();
////
////
////		// getUserTransaction().commit();
////
////    List <Study> studies =
////        (List<Study>) getField (studyHome, "studies");
////    assert studies!=null;
////    assert studies.get(studies.size()-1)
////               .getTitle().equals("Test Study");
////
////    study = (Study) getField (studyHome, "study");
////    assert study != null;
////    assert study.getTitle() == null;
////
////		entityManager.close();
//	}
//}

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class StudyTest extends TestCase {
    public StudyTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSetGetId() throws Exception {
        //TODO: Test goes here...
    }

    public void testSetGetName() throws Exception {
        //TODO: Test goes here...
    }

    public void testSetGetDescription() throws Exception {
        //TODO: Test goes here...
    }

    public static Test suite() {
        return new TestSuite(StudyTest.class);
    }
}
