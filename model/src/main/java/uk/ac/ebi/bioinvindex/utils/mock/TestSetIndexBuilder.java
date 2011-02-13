package uk.ac.ebi.bioinvindex.utils.mock;

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

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import uk.ac.ebi.bioinvindex.dao.ejb3.InvestigationEJB3DAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.StudyEJB3DAO;
import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Study;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import static java.lang.System.out;
import java.util.List;

/**
 * An helper class used to create an Hibernate Search Index from the current database.
 * Issue this command to run this utility:
 * <p/>
 * mvn -Dtest=indexTestSet test
 *
 * @author brandizi
 */
public class TestSetIndexBuilder {

	private int BATCH_SIZE = 1;

	private EntityManager entityManager;

	public TestSetIndexBuilder() {
	}


	public void indexAllStudies() {
		EntityManager entityManager = getEntityManager();


		StudyEJB3DAO dao = new StudyEJB3DAO(entityManager);
		FullTextEntityManager fullTxtEm = Search.createFullTextEntityManager(entityManager);

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		List<Study> all = dao.getAll();
		for (Study study : all) {
			out.println("\t\tIndexing Study #" + study.getAcc());
			fullTxtEm.index(study);
//			fullTxtEm.index ( study.getDesigns() );

//			for (User user : study.getUsers()) {
//				fullTxtEm.index(user);
//			}
//			for (Assay assay : study.getAssays() )
//			{
//				fullTxtEm.index ( assay );
//				fullTxtEm.index ( assay.getMeasurement () );
//				fullTxtEm.index ( assay.getTechnology () );
//				fullTxtEm.index ( assay.getPlatform () );
//				for ( PropertyValue<?> pval: assay.getCascadedPropertyValues () )
//					fullTxtEm.index ( pval );
//			}

		}
		tnx.commit();
	}


//	public void indexAllStudies1() {
//
//		getSession().setFlushMode(FlushMode.MANUAL);
//		getSession().setCacheMode(CacheMode.IGNORE);
//		Transaction transaction = getSession().beginTransaction();
//
////Scrollable results will avoid loading too many objects in memory
//		ScrollableResults results = getSession().createCriteria(Study.class)
//				.setFetchSize(BATCH_SIZE)
//				.scroll(ScrollMode.FORWARD_ONLY);
//		int index = 0;
//		while (results.next()) {
//			index++;
//			Object o = results.get(0);
//			getSession().index(o); //index each element
////			if (index % BATCH_SIZE == 0) {
////				getSession().flushToIndexes(); //apply changes to indexes
////				getSession().clear(); //clear since the queue is processed
////			}
//		}
//		transaction.commit();
//	}



	public void indexAll() {
//		this.indexAllInvestigations ();
		this.indexAllStudies();
	}


	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

//	private FullTextSession getSession() {
//		FullTextSession session;
//		Session deligate = (Session) entityManager.getDelegate();
//
//		if (deligate instanceof FullTextSession) {
//			session = (FullTextSession) deligate;
//		} else {
//			session = org.hibernate.search.Search.createFullTextSession(deligate);
//		}
//		return session;
//	}

	public static void main ( String args [] ) {
		out.println ( "\n\n*** Getting the Entity Manager" );
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("BIIEntityManager");
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		out.println ( "\n\n*** Done! Now reindexing" );
		TestSetIndexBuilder builder = new TestSetIndexBuilder ();
		builder.setEntityManager ( entityManager );
		builder.indexAllStudies ();

		out.println ( "\n\n*** That's all!" );
	}

}
