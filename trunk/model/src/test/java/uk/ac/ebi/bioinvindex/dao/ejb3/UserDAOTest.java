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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import uk.ac.ebi.bioinvindex.dao.UserDAO;
import uk.ac.ebi.bioinvindex.model.security.Person;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;

import javax.persistence.EntityTransaction;
import java.util.List;

/**
 * User: Nataliya Sklyar Date: Dec 3, 2008
 */
public class UserDAOTest extends DBUnitEJB3DAOTest {

	private UserDAO userDAO;

	public UserDAOTest() throws Exception {
		userDAO = new UserEJB3DAO(entityManager);
	}

	@Test
	public void testSave() throws Exception {
		User user = new Person();
		user.setUserName("test user");
		user.setPassword("test");

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		Long id = userDAO.save(user);
		tnx.commit();
		assertNotNull(id);

		User testUser = userDAO.getByUsername("test user");
		assertNotNull(testUser);
		assertEquals(user, testUser);

	}

	@Test
	public void testIsValidCredentials() throws Exception {
		boolean result = userDAO.isValidCredentials("curator", "pass");
		assertTrue(result);

		result = userDAO.isValidCredentials("test_user", "pass");
		assertTrue(result);

		result = userDAO.isValidCredentials("test_user", "wrong_pass");
		assertFalse(result);

		result = userDAO.isValidCredentials("wrong_user", "passqqq");
		assertFalse(result);
	}

	@Test
	public void testIsValidCuratorCredentials() throws Exception {
		boolean result = userDAO.isValidCuratorCredentials("curator", "pass");
		assertTrue(result);

		result = userDAO.isValidCuratorCredentials("test_user", "pass");
		assertFalse(result);

		result = userDAO.isValidCuratorCredentials("wrong_user", "passqqq");
		assertFalse(result);
	}

	@Test
	public void testGetAll() throws Exception{
		List<User> all = userDAO.getAll();
		assertEquals(2, all.size());
	}

	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "study-data.xml";
	}
}
