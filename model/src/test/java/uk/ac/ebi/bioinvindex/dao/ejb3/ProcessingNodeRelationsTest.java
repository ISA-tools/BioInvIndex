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
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.term.MaterialRole;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;

import javax.persistence.EntityTransaction;
import static java.lang.System.out;

/**
 * @author brandizi
 */
public class ProcessingNodeRelationsTest extends DBUnitEJB3DAOTest {

	private AccessibleEJB3DAO<Material> materialDao;
	private AccessibleEJB3DAO<MaterialNode> nodeDao;
	private OntologyEntryEJB3DAO<MaterialRole> roleDao;
	private StudyEJB3DAO studyDao;

	public ProcessingNodeRelationsTest() throws Exception {
		super();
	}

	@Before
	public void setUp() throws Exception {
		materialDao = new AccessibleEJB3DAO<Material> ( Material.class, entityManager );
		nodeDao = new AccessibleEJB3DAO<MaterialNode> ( MaterialNode.class, entityManager );
		roleDao = new OntologyEntryEJB3DAO<MaterialRole> ( MaterialRole.class, entityManager );
		studyDao = new StudyEJB3DAO ( entityManager );
	}


	@Test
	public void testMaterialsAndNodes()
	{

		out.println ( "\n\n\n______________ Testing material<->nodes relations ______________\n" );

		EntityTransaction transaction = entityManager.getTransaction ();
		transaction.begin ();

		MaterialRole fooRole = roleDao.getById ( (long) -6 );
		Study study = studyDao.getById ( (long) -2 );

		Material material = new Material ( "Foo Material", fooRole );
		material.setAcc("acc1");
		
		MaterialNode node = new MaterialNode ( study );
		node.setAcc ( "bii:test:node:123" );

		// First save the material, cause node.getMaterial() must be non null and refer to a persisted material
		// materialDao.save ( material );

		node.setMaterial ( material );

		nodeDao.save ( node );
		materialDao.save ( material );

		transaction.commit ();

		assertEquals ( "Argh! material's node is lost!", node, material.getMaterialNode () );

		out.println ( "\n______________ /end: Testing material<->nodes relations ______________\n\n\n" );
	}



	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "sample-data.xml";
	}
}
