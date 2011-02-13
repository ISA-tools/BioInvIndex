package uk.ac.ebi.bioinvindex.dao.ejb3;

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

import org.dbunit.operation.DatabaseOperation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.VisibilityStatus;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.term.AssayTechnology;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.Measurement;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.model.term.Property;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;
import uk.ac.ebi.bioinvindex.dao.UserDAO;

import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Map;

/**
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: Jun 21, 2007
 */
public class StudyEJB3DAOTest extends DBUnitEJB3DAOTest {

	private StudyEJB3DAO dao;
	private AccessibleEJB3DAO<Assay> assayDao;
	private OntologyEntryEJB3DAO<Measurement> endpointDao;
	private OntologyEntryEJB3DAO<AssayTechnology> techDao;

	public StudyEJB3DAOTest() throws Exception {
		super();
		dao = new StudyEJB3DAO(entityManager);
		assayDao = new AccessibleEJB3DAO<Assay>(Assay.class, entityManager);
		endpointDao = new OntologyEntryEJB3DAO<Measurement>(Measurement.class, entityManager);
		techDao = new OntologyEntryEJB3DAO<AssayTechnology>(AssayTechnology.class, entityManager);
	}


	@Test
	public void testGetByAcc() throws Exception {
		Study study = dao.getByAcc("BII-ST-1");
		assertNotNull(study);
		System.out.println("study = " + study);
	}

	@Test
	public void testGetByAccFail() throws Exception {
		Study study = dao.getByAcc("not_existing_acc");
		assertNull(study);
	}

	@Test
	public void testGetAll() {
		List<Study> list = dao.getAll();
		assertNotNull(list);
		assertEquals(3, list.size());
	}

//	@Test
//	public void testGetSourceMaterials() throws Exception {
//		OntologyEntryDAOHelper helper = new OntologyEntryDAOHelper();
//		helper.setEntityManager(entityManager);
//
//		MaterialRole role = helper.getMaterialTypeByAcc(MaterialRoles.SOURCE.getAcc());
//		Collection<Material> materials = dao.getMaterialsOfType(Long.valueOf("100"), role);
//		assertNotNull(materials);
//		assertEquals(2, materials.size());
//
//		materials = dao.getMaterialsOfType(Long.valueOf("101"), role);
//		assertNotNull(materials);
//		assertEquals(0, materials.size());
//
//	}

//	@Test
//	public void testFindByProperty() {
//
//		Study study = dao.getById(Long.valueOf("100"));
//
//		QueryParameter qp1 = new QueryParameter("title", "Subtle metabolic and liver gene transcriptional changes underlie diet-induced fatty liver susceptibility in insulin-resistant mice");
//		List<Study> result = dao.findByProperty(qp1);
//		assertEquals(1, result.size());
//		assertEquals(study.getId(), result.get(0).getId());
//
//		Calendar calendar = new GregorianCalendar(2007, Calendar.AUGUST, 30);
//		Date date = calendar.getTime();
//
////		TODO: First fix Oracle problems with dates
////		QueryParameter qp2 = new QueryParameter("submissionDate", date);
////		result = dao.findByProperty(qp2);
////		assertEquals(1, result.size());
////		assertEquals(study.getId(), result.get(0).getId());
//
//	}

//	@Test
//	public void testFilterByProperty() {
//
//		List<Study> list = dao.getAll();
//		assertEquals(2, list.size());
//
//		QueryParameter qp1 = new QueryParameter("title", "Subtle metabolic and liver gene transcriptional changes underlie diet-induced fatty liver susceptibility in insulin-resistant mice");
//
//		List<Long> ids = new ArrayList<Long>();
//		ids.add((long) 100);
//		ids.add((long) 101);
//		List<Study> studies = dao.filterByProperty(ids, qp1);
//
//		assertNotNull(studies);
//		assertEquals(1, studies.size());
//
//	}

	@Test
	public void testSaveStudyWithAssay() {
		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();

		Study study = new Study("test study");
		study.setAcc("STUDY-FOO-1");
		// Must save the study first, assays must refer to a persisted object
		Long id = dao.save(study);

		Measurement endpoint = endpointDao.getById((long) 203);
		AssayTechnology tech = techDao.getById((long) 301);

		// Automatically calls study.addAssay()
		Assay assay1 = new Assay(study);
		assay1.setAcc("ASSAY-FOO-1");
		assay1.setMeasurement(endpoint);
		assay1.setTechnology(tech);

		assayDao.save(assay1);
		// save again so that assays are updated

		Assay assay2 = new Assay(study);
		assay2.setAcc("ASSAY-FOO-2");
		assay2.setMeasurement(endpoint);
		assay2.setTechnology(tech);
		assayDao.save(assay2);

		assertEquals("Ouch! Wrong no. of assays stored by the study before persistence", 2, study.getAssays().size());

		tnx.commit();

		Study testStudy = dao.getById((long) id);
		assertNotNull("Urp! No study seems saved!", testStudy);
		//assertEquals ( "Ops! Wrong study retrieved!", "STUDY-FOO-1", testStudy.getAcc () );
		assertEquals("Ourgh! Wrong no. of assays retrieved from the study!", 2, testStudy.getAssays().size());
	}

	@Test
	public void testRetrieveStudyWithAssay() {
		Study testStudy = dao.getById((long) 100);
		assertEquals("Ouch! Wrong number of assays from retrieved study", 2, testStudy.getAssays().size());
	}

	@Test
	public void testGetFactorsForStudy() throws Exception {
		List<Property<FactorValue>> list = dao.getFactorsForStudy((long) 100);
		assertNotNull(list);
		assertEquals(2, list.size());
	}

//	@Test
//	public void testGetFactorNamesForStudy() throws Exception {
//		List<String> list = dao.getFactorNamesForStudy((long) 100);
//		assertNotNull(list);
//		System.out.println("list = " + list);
//		assertEquals(2, list.size());
//	}

//	@Test
//	public void testGetFactorValuesForFactorInStudy() throws Exception {
//		List<PropertyValue> list = dao.getFactorValuesForFactorInStudy("Diet", (long) 100);
//		assertNotNull(list);
//		System.out.println("list = " + list);
//		assertEquals(2, list.size());
//	}

	@Test
	public void testGetCharacteristicsForStudy() throws Exception {
		List<Property<CharacteristicValue>> list = dao.getCharacteristicsForStudy((long) 100);
		assertNotNull(list);
		assertEquals(4, list.size());

		list = dao.getCharacteristicsForStudy((long) 101);
		assertNotNull(list);
		assertEquals(0, list.size());
	}

//	@Test
//	public void testFindByAssayType() throws Exception {
//		List<Study> list = dao.findByAssayType("Transcription Profiling");
//		assertNotNull(list);
//		assertEquals(2, list.size());
//	}

	@Test
	public void testGetValuesOfProperty() {
		List<PropertyValue> propertyValues = dao.getValuesOfPropertyForStudyId((long) 100, "organism");
		assertNotNull(propertyValues);
		assertEquals(2, propertyValues.size());
	}

	@Test
	public void testGetValuesOfPropertyForStudyAcc() {
		List<PropertyValue> propertyValues = dao.getValuesOfPropertyForStudyAcc("BII-ST-1", "organism");
		assertNotNull(propertyValues);
		assertEquals(2, propertyValues.size());
	}

//	@Test
//	public void testGetValuesOfProperty1() throws Exception {
//		List<PropertyValue> propertyValues = dao.getValuesOfProperty("organism");
//		assertNotNull(propertyValues);
//		assertEquals(2, propertyValues.size());
//	}

	@Test
	public void testFilterByOntologyTerm() throws Exception {
		List<String> list = dao.filterByOntologyTermAndRefName("CHEBI:30069", "CHEBI");
		assertNotNull("Cannot find CHEBI:30069! (null result)", list);
		assertEquals("Cannot find CHEBI:30069! (wrong result size)", 1, list.size());
	}

	/**
	 * TODO: study.status was made transient. Uncomment definitions in study_data.xml
	 */
	// @Test
	public void testGetPublicStudies() throws Exception {
		List<String> list = dao.getPublicStudyAccs();
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals("BII-ST-2", list.get(0));
	}

	/**
	 * TODO: this doesn't work cause study.status was made transient. Uncomment definitions in study_data.xml
	 */
	// @Test
	public void testGetStudiesForUser() throws Exception {
		List<String> list = dao.getStudyAccForUser("test_user");
		System.out.println("list = " + list);
		assertNotNull(list);
		assertEquals(2, list.size());

		list = dao.getStudyAccForUser("");
		System.out.println("list = " + list);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals("BII-ST-2", list.get(0));

		list = dao.getStudyAccForUser(null);
		System.out.println("list = " + list);
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals("BII-ST-2", list.get(0));

	}

	@Test
	public void testGetOwnedStudiesForUser() throws Exception {
		List<String> list = dao.getOwnedStudiesForUser("test_user");
		System.out.println("list = " + list);
		assertNotNull(list);
		assertEquals(1, list.size());
	}

	@Test
	public void testGetbyAccForUser() throws Exception {
		//Test for private study
		Study study = dao.getByAccForUser("BII-ST-1", "test_user");
		assertNotNull(study);

		//Test for private study with curator user
		study = dao.getByAccForUser("BII-ST-1", "curator");
		assertNotNull(study);

		//Test for private study with guest user
		Exception exception = null;
		try {
			study = dao.getByAccForUser("BII-ST-1", "ttt");
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull(exception);


		//Test for public study
		study = dao.getByAccForUser("BII-ST-2", "test_user");
		assertNotNull(study);
	}

	@Test
	public void testChangeStudyStatus() throws Exception {

		Study study = dao.getByAcc("BII-ST-1");
		assertEquals(VisibilityStatus.PRIVATE, study.getStatus());

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		dao.changeStudyStatus("BII-ST-1", VisibilityStatus.PUBLIC);
		tnx.commit();

		entityManager.clear();

		Study study1 = dao.getByAcc("BII-ST-1");
		assertEquals(VisibilityStatus.PUBLIC, study1.getStatus());

		tnx = entityManager.getTransaction();
		tnx.begin();
		dao.changeStudyStatus("BII-ST-2", VisibilityStatus.PRIVATE);
		tnx.commit();

		study1 = dao.getByAcc("BII-ST-2");
		assertEquals(VisibilityStatus.PRIVATE, study1.getStatus());
	}

	@Test
	public void testGetVisibilityStatusForStudies() throws Exception {

		Map<String, VisibilityStatus> statusMap = dao.getVisibilityStatusForStudies();
		assertNotNull(statusMap);
		assertEquals(3, statusMap.size());
		assertEquals(VisibilityStatus.PRIVATE, statusMap.get("BII-ST-1"));
		assertEquals(VisibilityStatus.PUBLIC, statusMap.get("BII-ST-2"));
	}

	@Test
	public void testAddUserToStudy() throws Exception {

		UserDAO userDAO = DaoFactory.getInstance(entityManager).getUserDAO();
		User user = userDAO.getByUsername("test_user");

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		dao.addUserToStudy("BII-ST-2", user);
		tnx.commit();

		entityManager.clear();

		Study study1 = dao.getByAcc("BII-ST-2");
		assertEquals(2, study1.getUsers().size());

	}
	
	
	@Test
	public void testRemoveUserFromStudy() throws Exception {

		UserDAO userDAO = DaoFactory.getInstance(entityManager).getUserDAO();
		User user = userDAO.getByUsername("test_user");

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		dao.removeUserFromStudy("BII-ST-2", user);
		tnx.commit();

		entityManager.clear();

		Study study1 = dao.getByAcc("BII-ST-2");
		assertEquals(1, study1.getUsers().size());

	}


	//ToDo: possibly move this test into another class
//	@Test
//	public void testSaveMaterrialWithProperties() {
//
//		OntologyEntryDAOHelper helper = new OntologyEntryDAOHelper();
//		helper.setEntityManager(entityManager);
//		MaterialRole role = helper.getMaterialTypeByAcc(MaterialRoles.SOURCE.getAcc());
//
//		Material material = new Material("test material1", role);
//
//		Property property = new Property(PropertyRole.FACTOR, 1);
//		property.setName("organism");
//		property.setAcc("test-acc2");
//
//		CharacteristicValue characteristic = new CharacteristicValue(property);
//		characteristic.setAcc("test-CH101");
//		characteristic.setValue("mouse");
//
//		Property property1 = new Property(PropertyRole.CHARACTERISTICS, 2);
//		property1.setName("sex");
//		property1.setAcc("test-acc1");
//
//		CharacteristicValue characteristic1 = new CharacteristicValue(property1);
//		characteristic1.setAcc("test-CH102");
//		characteristic1.setValue("male");
//
//		material.addCharacteristicValue(characteristic);
//		material.addCharacteristicValue(characteristic1);
//
//		Material material1 = new Material("test material2", role);
//		CharacteristicValue characteristic2 = new CharacteristicValue(property);
//		characteristic2.setAcc("test-CH103");
//		characteristic2.setValue("rat");
//
//		CharacteristicValue characteristic3 = new CharacteristicValue(property1);
//		characteristic3.setAcc("test-CH104");
//		characteristic3.setValue("female");
//
//		material1.addCharacteristicValue(characteristic2);
//		material1.addCharacteristicValue(characteristic3);
//
//		EntityTransaction tnx = entityManager.getTransaction();
//		tnx.begin();
//		entityManager.persist(material);
//		entityManager.persist(material1);
//		tnx.commit();
//
//		Material testMaterial1 = entityManager.find(Material.class, (long) 1);
//		assertNotNull(testMaterial1);
//		assertEquals(2, testMaterial1.getCharacteristicValues().size());
//		assertTrue(testMaterial1.getCharacteristicValues().contains(characteristic));
//		assertTrue(testMaterial1.getCharacteristicValues().contains(characteristic1));
//
//		Material testMaterial2 = entityManager.find(Material.class, (long) 2);
//		assertNotNull(testMaterial2);
//		assertEquals(2, testMaterial2.getCharacteristicValues().size());
//		assertTrue(testMaterial2.getCharacteristicValues().contains(characteristic2));
//		assertTrue(testMaterial2.getCharacteristicValues().contains(characteristic3));
//	}

	@Ignore
	public void testSaveWithProtocol() {

	}

	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "study-data.xml";
	}
}