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
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.term.Characteristic;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.MaterialRole;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;

import javax.persistence.EntityTransaction;

/**
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Jan 7, 2008
 */
public class TestOntologyEntry extends DBUnitEJB3DAOTest {

	public TestOntologyEntry() throws Exception {
		super();
	}

	@Test
	public void testMaterialRoleFromDB() throws Exception {

		OntologyEntryEJB3DAO<MaterialRole> ontologyDAO = (OntologyEntryEJB3DAO) daoFactory.getOntologyEntryDAO(MaterialRole.class);

		//TODO: onotology entry dao should have getOE(acc, refSource). Maybe getOE(OEType, acc, RefSource)
		OntologyEntry matRole = ontologyDAO.getOntologyEntryByAcc("OBI-2");


		Material material = new Material("test material1", (MaterialRole) matRole);
		material.setAcc("test1");

		Study study = new Study ( "Foo Study" );
		study.setAcc ( "fooStudy" );
		MaterialNode mnode = new MaterialNode ( study );
		mnode.setAcc ( "mn_test1" );
		material.setMaterialNode ( mnode );

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		entityManager.persist ( study );
		entityManager.persist ( mnode );
		entityManager.persist(material);
		tnx.commit();
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

	}

	@Test
	public void testMaterialRoleFromDetached() throws Exception {

		ReferenceSource referenceSource = new ReferenceSource("TEST ONTOLOGY");
		referenceSource.setAcc("BII-1");
		MaterialRole matRole = new MaterialRole("OBI-2", "sample", referenceSource);

		//ToDo: save OE
		//1. attach reference source
		//1.1 if ID present - merge, if not find with the same acc, if not save
		//2. attach OE
		//2.1 if ID present - merge, if not save


		Material material = new Material("test material1", matRole);
		material.setAcc("test1");

		ReferenceSourceEJB3DAO refSourceEJB3DAO = new ReferenceSourceEJB3DAO();
		refSourceEJB3DAO.setEntityManager(entityManager);

		OntologyEntryEJB3DAO<?> ontologyDAO = (OntologyEntryEJB3DAO<?>) daoFactory.getOntologyEntryDAO();
		MaterialRole matRoleDB =
				(MaterialRole) ontologyDAO.getOntologyEntryByAccAndRefSource(matRole.getAcc(), referenceSource.getAcc());

		assertNotNull(matRoleDB);
		material.setType(matRoleDB);

		Study study = new Study ( "Foo Study" );
		study.setAcc ( "fooStudy" );
		MaterialNode mnode = new MaterialNode ( study );
		mnode.setAcc ( "mn_test1" );
		material.setMaterialNode ( mnode );


		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		entityManager.persist ( study );
		entityManager.persist ( mnode );
		entityManager.persist(material);
		tnx.commit();
	}

	@Test
	public void testCharacteristics() throws Exception {

		ReferenceSource referenceSource = new ReferenceSource("TEST ONTOLOGY");
		referenceSource.setAcc("BII-1");
		MaterialRole matRole = new MaterialRole("OBI-2", "sample", referenceSource);

		Material material = new Material("test material1", matRole);
		material.setAcc("test1");

		ReferenceSourceEJB3DAO refSourceEJB3DAO = new ReferenceSourceEJB3DAO();
		refSourceEJB3DAO.setEntityManager(entityManager);

		OntologyEntryEJB3DAO<?> ontologyDAO = (OntologyEntryEJB3DAO<?>) daoFactory.getOntologyEntryDAO();
		MaterialRole matRoleDB =
				(MaterialRole) ontologyDAO.getOntologyEntryByAccAndRefSource(matRole.getAcc(), referenceSource.getAcc());

		assertNotNull(matRoleDB);
		material.setType(matRoleDB);

		Study study = new Study ( "Foo Study" );
		study.setAcc ( "fooStudy" );
		MaterialNode mnode = new MaterialNode ( study );
		mnode.setAcc ( "mn_test1" );
		material.setMaterialNode ( mnode );

		//Create and add characteristics
		Characteristic property = new Characteristic(1);
		property.setValue("organism");

		OntologyEntry chTerm = ontologyDAO.getOntologyEntryByAccAndRefSource("OBI-EO1", "BII-1");

		assertNotNull(chTerm);
		property.addOntologyTerm((OntologyTerm) chTerm);

		CharacteristicValue characteristic = new CharacteristicValue(property);
		characteristic.setValue("mouse");

		material.addCharacteristicValue(characteristic);


		Material material2 = new Material("test material2", matRoleDB);
		material2.setAcc("test2");

		CharacteristicValue characteristic2 = new CharacteristicValue(property);
		characteristic2.setValue("cow");

		material2.addCharacteristicValue(characteristic2);

		MaterialNode mnode2 = new MaterialNode ( study );
		mnode2.setAcc ( "mn_test2" );
		material2.setMaterialNode ( mnode2 );


		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		entityManager.persist ( study );
		entityManager.persist ( mnode );
		entityManager.persist(material);
		entityManager.persist ( mnode2 );
		entityManager.persist(material2);
		tnx.commit();
	}

	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_ontology.xml";
	}
}
