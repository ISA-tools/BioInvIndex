package uk.ac.ebi.bioinvindex.search;

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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermsFilter;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.dao.ejb3.StudyEJB3DAO;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.VisibilityStatus;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.security.Person;
import uk.ac.ebi.bioinvindex.model.security.UserRole;
// TODO: THis is missing from SVN
// import uk.ac.ebi.bioinvindex.model.security.UserRole;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.term.AssayTechnology;
import uk.ac.ebi.bioinvindex.model.term.Characteristic;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.Design;
import uk.ac.ebi.bioinvindex.model.term.Measurement;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.PropertyRole;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.persistence.StudyPersister;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.BIIFilterQuery;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.FilterField;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBIIFilterQuery;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBrowseField;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyFreeTextSearchImpl;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.SecureStudyFreeTextSearch;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Date;


/**
 * ToDo: Claen up tests!!!
 * <p/>
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: Sep 14, 2007
 */
public class StudySearchTest {

	protected static EntityManager entityManager;

	private static EntityManagerFactory entityManagerFactory;

	private StudyEJB3DAO dao;

	private StudyFreeTextSearchImpl search;

	// private PersisterHelper persisterHelper;

	public StudySearchTest() throws Exception {

		prepareIndexDirectory();

		entityManagerFactory = Persistence.createEntityManagerFactory("BIIEntityManager");
		entityManager = entityManagerFactory.createEntityManager();

		DaoFactory factory = DaoFactory.getInstance(entityManager);

		dao = (StudyEJB3DAO) factory.getStudyDAO();

		search = new StudyFreeTextSearchImpl();
		search.setEntityManager(entityManager);

		//persisterHelper = new PersisterHelper();
		//persisterHelper.setDaoFactory(DaoFactory.getInstance(entityManager));
	}

	@AfterClass
	public static void close() {
		entityManager.close();
		entityManagerFactory.close();
	}


	public void prepareIndexDirectory() throws Exception {

		Properties properties = new Properties();

		URL resource = StudySearchTest.class.getResource("/hibernate.properties");
		System.out.println("resource.getPath() = " + resource.getPath());

		InputStream asStream = StudySearchTest.class.getResourceAsStream("/hibernate.properties");

		properties.load(asStream);

		String location = properties.getProperty("hibernate.search.default.indexBase");
		File dir = new File(location);
		if (dir.exists()) {
			deleteDir(dir);
		}

		dir.mkdir();
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

//	@Test
//	public void testTitle() throws Exception {
//
//		Study study = buildStudy("acc1", "my test experiment");
//		Design design = buildDesign("acc2", "study design");
//		study.setDesign(design);
//
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//		entityManager.persist(design);
//		entityManager.persist(study);
//		tnx.commit();
//
//		List<Study> result = search.searchAndFilterByAssayType("my test*");
//
//		assertEquals(1, result.size());
//		assertTrue(result.get(0) instanceof Study);
//
//		Study testStudy = (Study) result.get(0);
//		assertEquals(study.getTitle(), testStudy.getTitle());
//
//	}

//	@Test
//	public void testAll1() throws Exception {
//
//		Study study = buildStudy("acc1", "my test experiment");
//		Design design = buildDesign("parallel design");
//		study.setDesign(design);
//
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//		entityManager.persist(design);
//		entityManager.persist(study);
//		tnx.commit();
//
//		List<Study> result = search.searchAllFields("my test");
//
//		assertEquals(1, result.size());
//		assertTrue(result.get(0) instanceof Study);
//
//		Study testStudy = (Study) result.get(0);
//		assertEquals(study.getTitle(), testStudy.getTitle());
//
//		//Search for a field from Design object
//		result = search.searchAllFields("parallel");
//
//		assertEquals(1, result.size());
//		assertTrue(result.get(0) instanceof Study);
//
//		testStudy = (Study) result.get(0);
//		assertEquals(study.getTitle(), testStudy.getTitle());
//	}

//	@Test
//	public void testAll2() throws Exception {
//
//		Study study = buildStudy("acc1", "study to test");
//		Design design = buildDesign("study design");
//		study.setDesign(design);
//
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//		entityManager.persist(design);
//		entityManager.persist(study);
//		tnx.commit();
//
//		List<Study> result = search.searchAllFields("design");
//
//		assertEquals(1, result.size());
//		assertTrue(result.get(0) instanceof Study);
//
//		Study testStudy = result.get(0);
//		assertEquals(study.getTitle(), testStudy.getTitle());
//	}

	//ToDo: Fix indexing, and lucene-based search first, introduce all_content field
//	@Ignore
//	public void testAll3() throws Exception {
//
//		Study study = buildStudy("acc1", "study to test");
//		Assay assay = buildAssay("acc2", "my assay type", study);
//
//		study.addAssay(assay);
//		Design design = buildDesign("study design");
//		study.setDesign(design);
//
//
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//		entityManager.persist(design);
//		entityManager.persist(study);
//		entityManager.persist(assay);
//		tnx.commit();
//
//		List<Study> result = search.searchAllFields("assay");
//
//		assertEquals(1, result.size());
//		assertTrue(result.get(0) instanceof Study);
//		Study testStudy = result.get(0);
//		assertEquals(study.getTitle(), testStudy.getTitle());
//	}


//	@Test
//	public void testGetIds() throws Exception {
//
//		Study study = buildStudy("acc1", "study to test");
//		Design design = buildDesign("study design");
//		study.setDesign(design);
//
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//		entityManager.persist(design);
//		entityManager.persist(study);
//		tnx.commit();
//
//		List<Object[]> result = search.getIdsAllFields("design");
//
//		assertEquals(1, result.size());
//
//	}

	//ToDo: Fix this test, the method works in web app, but test fails!
//	@Ignore
//	public void testFilterByAssayType() throws Exception {
//
//		Study study1 = buildStudy("acc1", "study to test");
//		Assay assay = buildAssay("acc2", "my assay type", study1);
//		study1.addAssay(assay);
//
//		Study study2 = buildStudy("acc2", "study to test 2");
//		Study study3 = buildStudy("acc3", "study to test 3");
//		Study study4 = buildStudy("acc4", "study to test 3");
//
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//		entityManager.persist(study1);
//		entityManager.persist(assay);
//		entityManager.persist(study2);
//		entityManager.persist(study3);
//		entityManager.persist(study4);
//
//		tnx.commit();
//
//		List<Study> list = dao.findByAssayType("my assay type");
//		System.out.println("list.size = " + list.size());
//
//		List<Study> result = search.searchAndFilterByAssayType("study", "my assay type");
//
//		assertEquals(1, result.size());
//
//	}

//  @Test
//	public void testAssayCascadedProp() throws Exception {
//
//		Session session = (Session) entityManager.getDelegate ();
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//
//		Study study = buildStudy("study_acc1", "study to test");
//
//		Assay assay = buildAssay("assay_acc2", "my assay type", study);
//
////		study.addAssay(assay);
//		Design design = buildDesign("study design");
//
//		Characteristic char1 = buildCharacteristic("organism", "specie", PropertyRole.FACTOR);
//
////		assay.addCascadedPropertyValue(buildCharacteristicValue("human", char1, "homo sapiens"));
////		assay.addCascadedPropertyValue(buildCharacteristicValue("little mouse", char1, "Mus musculus"));
//
//		Characteristic char2 = buildCharacteristic("size", null, PropertyRole.PROPERTY);
//
////		assay.addCascadedPropertyValue(buildCharacteristicValue("big", char2, null));
////		assay.addCascadedPropertyValue(buildCharacteristicValue("small", char2, null));
//
//		// Saves all what comes from here.
//		new StudyPersister ( DaoFactory.getInstance ( entityManager ), null ).persist ( study );
//
//		tnx.commit();
//
////		List<Object[]> list = search.getTable("human");
////		System.out.println("list.size() = " + list.size());
////
////		Object[] firstResult = (Object[]) list.get(0);
////		System.out.println("firstResult.si = " + firstResult.length);
////		for (int i = 0; i < firstResult.length; i++) {
////			System.out.println("firstResult = " + firstResult[i]);
////
////		}
//
////		List<Map<StudyBrowseField, String[]>> studyBrowseFieldValues = search.getStudyBrowseFieldValues(0, 10);
////		System.out.println("studyBrowseFieldValues = " + studyBrowseFieldValues);
//
//		BIIFilterQuery filterQuery = new StudyBIIFilterQuery();
//		filterQuery.addFilterValue(FilterField.ORGANISM, "little mouse");
////		filterQuery.addFilterValue(FilterField.ENDPOINT_NAME, "first endpoint");
////	filterQuery.setSearchText("little");
//
////		BIIQueryBuilder queryBuilder = new BIIQueryBuilder();
////		Filter filter = queryBuilder.buildFilter(filterQuery);
////		System.out.println("filter = " + filter);
//
//		List<Map<StudyBrowseField, String[]>> studyBrowseFieldValues1 =
//				search.getStudyBrowseFieldValues(filterQuery, 0, 10);
//		System.out.println("studyBrowseFieldValues1 = " + studyBrowseFieldValues1);
//
////		List<Map<StudyBrowseField, String[]>> studyBrowseFieldValues2 =
////				search.getStudyBrowseFieldValues(null, 0, 10);
////		System.out.println("studyBrowseFieldValues2 = " + studyBrowseFieldValues2);
//
////		search.indexReaderUse();
//	}


	@Test
	public void testAssayCascadedProp2() throws Exception {

		Session session = (Session) entityManager.getDelegate();
		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();

		Study study = buildStudy("study_acc11111", "study to test");

		Assay assay = buildAssay("assay_acc22221", "my assay type", study);

//		study.addAssay(assay);
		Design design = buildDesign("study design");
		study.setDesign(design);

		Characteristic char1 = buildCharacteristic("organism", "specie", PropertyRole.FACTOR);


		Characteristic char2 = buildCharacteristic("size", null, PropertyRole.PROPERTY);


		// Saves all what comes from here.
		new StudyPersister(DaoFactory.getInstance(entityManager), null).persist(study);

		tnx.commit();


		BIIFilterQuery filterQuery = new StudyBIIFilterQuery();
//		filterQuery.setSearchText("organism:human");


		List<Map<StudyBrowseField, String[]>> studyBrowseFieldValues1 =
				search.getAllStudyBrowseFieldValues(filterQuery);
		System.out.println("studyBrowseFieldValues1 = " + studyBrowseFieldValues1);

//		List<Map<StudyBrowseField, String[]>> studyBrowseFieldValues2 =
//				search.getStudyBrowseFieldValues(null, 0, 10);
//		System.out.println("studyBrowseFieldValues2 = " + studyBrowseFieldValues2);

//		search.indexReaderUse();
	}


//	@Test
//	public void testGetAssayPlatforms() throws Exception{
//
//		Study study = buildStudy("study_acc1", "study to test");
//		Assay assay = buildAssay("assay_acc2", "my assay type", study);
//
////		study.addAssay(assay);
//		Design design = buildDesign("study design");
//		study.setDesign(design);
//
//		Characteristic char1 = buildCharacteristic("organism", "specie", PropertyRole.FACTOR);
////		assay.addCascadedPropertyValue(buildCharacteristicValue("human", char1, "homo sapiens"));
////		assay.addCascadedPropertyValue(buildCharacteristicValue("little mouse", char1, "Mus musculus"));
//
//		Characteristic char2 = buildCharacteristic("size", null, PropertyRole.PROPERTY);
////		assay.addCascadedPropertyValue(buildCharacteristicValue("big", char2, null));
////		assay.addCascadedPropertyValue(buildCharacteristicValue("small", char2, null));
//
//
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//		// MB: uses the new persistence API
//		new StudyPersister ( DaoFactory.getInstance ( entityManager ), null ).persist ( study );
//		tnx.commit();
//
//		Collection<String> platforms = search.getAssayPlatforms();
//		System.out.println("platforms = " + platforms);
//	}

//	@Test
//	public void testUser() throws Exception{
//
//		Study study = buildStudy("study_acc1", "study to test");
//
//		User user1 = new Person();
//		user1.setUserName("curator");
//		user1.setRole(UserRole.CURATOR);
//
//		User user2 = new Person();
//		user2.setUserName("test");
//		user2.setRole(UserRole.SUBMITTER);
//
//		study.addUser(user1);
//		study.addUser(user2);
//
//
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//		// MB: uses the new persistence API
//		new StudyPersister ( DaoFactory.getInstance ( entityManager ), null ).persist ( study );
//		tnx.commit();
//
////		Collection<String> platforms = search.getAssayPlatforms();
////		System.out.println("platforms = " + platforms);
//	}

	@Test
	public void testUser() throws Exception {

		Study study = buildStudy("study_acc1", "study to test");

		User user1 = new Person();
		user1.setUserName("curator");
		user1.setRole(UserRole.CURATOR);
		user1.setJoinDate(new Date());

		User user2 = new Person();
		user2.setUserName("test_user1");
		user2.setRole(UserRole.SUBMITTER);

		User user3 = new Person();
		user3.setUserName("test_user2");
		user3.setRole(UserRole.SUBMITTER);

		study.addUser(user1);
		study.addUser(user2);
		study.setStatus(VisibilityStatus.PUBLIC);

		Study study2 = buildStudy("study_acc2", "study to test 2");
		study2.setStatus(VisibilityStatus.PRIVATE);
		study2.addUser(user3);

		Study study3 = buildStudy("study_acc3", "study to test 3");
		study3.setStatus(VisibilityStatus.PRIVATE);
		study3.addUser(user2);

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.persist(user3);


		StudyPersister studyPersister = new StudyPersister(DaoFactory.getInstance(entityManager), null);
		studyPersister.persist(study);
		studyPersister.persist(study2);
		studyPersister.persist(study3);
		tnx.commit();

		BIIFilterQuery query = new StudyBIIFilterQuery();

		SecureStudyFreeTextSearch secureSearch = new SecureStudyFreeTextSearch();
		secureSearch.setEntityManager(entityManager);

		List<Map<StudyBrowseField, String[]>> list = secureSearch.getAllStudyBrowseFieldValuesForUser(query, "test_user1");
		System.out.println("list.size() = " + list.size());
		System.out.println("list = " + list);
		assertEquals(2, list.size());


		list = secureSearch.getAllStudyBrowseFieldValuesForUser(query, "test_user2");
		assertEquals(2, list.size());

		list = secureSearch.getAllStudyBrowseFieldValuesForUser(query, "curator");
		assertEquals(3, list.size());

		list = secureSearch.getAllStudyBrowseFieldValuesForUser(query, "");
		assertEquals(1, list.size());


//		Collection<String> platforms = search.getAssayPlatforms();
//		System.out.println("platforms = " + platforms);
	}

	@Test
	@Ignore
	public void test() throws Exception {

		Study study = buildStudy("study_acc1", "study to test");
		Assay assay = buildAssay("assay_acc2", "my assay type", study);

//		study.addAssay(assay);
		Design design = buildDesign("study design");
		study.setDesign(design);

		//ToDo: Use AssayResult
		Characteristic char1 = buildCharacteristic("organism", "specie", PropertyRole.FACTOR);
//		assay.addCascadedPropertyValue(buildCharacteristicValue("human", char1, "homo sapiens"));
//		assay.addCascadedPropertyValue(buildCharacteristicValue("little mouse", char1, "Mus musculus"));

		Characteristic char2 = buildCharacteristic("size", null, PropertyRole.PROPERTY);
//		assay.addCascadedPropertyValue(buildCharacteristicValue("big", char2, null));
//		assay.addCascadedPropertyValue(buildCharacteristicValue("small", char2, null));


		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		// MB: uses the new persistence API
		new StudyPersister(DaoFactory.getInstance(entityManager), null).persist(study);
		tnx.commit();


		FullTextSession session;
		Session deligate = (Session) entityManager.getDelegate();

		if (deligate instanceof FullTextSession) {
			session = (FullTextSession) deligate;
		} else {
			session = Search.createFullTextSession(deligate);
		}

		SearchFactory searchFactory = session.getSearchFactory();

		DirectoryProvider directoryProvider = searchFactory.getDirectoryProviders(Study.class)[0];

		ReaderProvider readerProvider = searchFactory.getReaderProvider();
		IndexReader reader = readerProvider.openReader(directoryProvider);

		BitSet bitSet = new BitSet(reader.maxDoc());

//		Term term1 = new Term(FilterField.ORGANISM.getName(), "mouse");
//		Term term2 = new Term(FilterField.ORGANISM.getName(), "little");

		Analyzer analyzer = new StandardAnalyzer();

		StringReader stringReader = new StringReader("first endpoint");
		TokenStream tokenStream = analyzer.tokenStream(null, stringReader);

		TermsFilter filter = new TermsFilter();
		while (true) {
			Token token = tokenStream.next();
			if (token == null) break;

			System.out.println("token = " + token);
			filter.addTerm(new Term(FilterField.ENDPOINT_NAME.getName(), token.termText()));

		}

//		TermsFilter filter = new TermsFilter();
//		filter.addTerm(term1);
//		filter.addTerm(term2);

		BitSet bitSet1 = filter.bits(reader);
		System.out.println("bitSet1.cardinality() = " + bitSet1.cardinality());

//		TermDocs termDocs = reader.termDocs(term);
//		while (termDocs.next()) {
//			bitSet.set(termDocs.doc());
//		}
//
//		System.out.println("bitSet.cardinality() = " + bitSet.cardinality());

	}

	protected Study buildStudy(String acc, String title) {
		Study study = new Study(title);
		study.setAcc(acc);
		study.setDescription("Very long mock Study description");
		study.setObjective("Very important Study objective");

		return study;
	}

	protected Design buildDesign(String name) {
		Design design = new Design(name);

		return design;
	}

	protected Assay buildAssay(String acc, String type, Study study) {
		Assay assay = new Assay(study);
		assay.setAcc(acc);
		ReferenceSource reference = new ReferenceSource("OBI-test");
		reference.setAcc("test acc");

		// MB: doing it with the new persistence API
		//entityManager.persist(reference);

		AssayTechnology assayType = new AssayTechnology("at_1", type, reference);
		//entityManager.persist(assayType);

		Measurement measurement = new Measurement("ep_1", "first endpoint", reference);
		//entityManager.persist(endPoint);


		assay.setTechnology(assayType);
		assay.setMeasurement(measurement);
		return assay;
	}

	protected CharacteristicValue buildCharacteristicValue(String valueName,
	                                                       Characteristic property,
	                                                       String ontologyTermName) {

		CharacteristicValue characteristic = new CharacteristicValue(property);
		characteristic.setValue(valueName);

		if (ontologyTermName != null) {
			ReferenceSource referenceSource = new ReferenceSource("new ref source");
			OntologyEntry chTerm1 = new OntologyTerm(ontologyTermName, ontologyTermName, referenceSource);

			characteristic.addOntologyTerm((OntologyTerm) chTerm1);
		}

		// MB: new persistence API used
		// persisterHelper.preparePropertyValue(characteristic);
		// entityManager.persist(characteristic);

		return characteristic;
	}

	protected Characteristic buildCharacteristic(String propertyName,
	                                             String ontologyTermName,
	                                             PropertyRole role) {

		//Create and add characteristics
		Characteristic property = new Characteristic(1);
		property.setValue(propertyName);

		if (ontologyTermName != null) {
			ReferenceSource referenceSource = new ReferenceSource("new ref source");
			OntologyEntry chTerm1 = new OntologyTerm(ontologyTermName, ontologyTermName, referenceSource);

			property.addOntologyTerm((OntologyTerm) chTerm1);
		}

		property.setRole(role);
		return property;
	}
}
