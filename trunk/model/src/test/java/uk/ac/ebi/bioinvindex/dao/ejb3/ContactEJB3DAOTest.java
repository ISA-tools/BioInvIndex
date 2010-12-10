/*
 * ContactEJB3DAOTest.java
 *
 * Created on September 11, 2007, 3:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.term.ContactRole;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;

import javax.persistence.EntityTransaction;
import static java.lang.System.out;
import java.util.List;


/**
 * @author brandizi
 */
public class ContactEJB3DAOTest extends DBUnitEJB3DAOTest {

	private ContactEJB3DAO dao;

	public ContactEJB3DAOTest() throws Exception {
		super();
	}

	@Before
	public void setUp() {
		dao = new ContactEJB3DAO();
		dao.setEntityManager(entityManager);
	}


	@Test
	public void testCreation() throws Exception {

		Contact contact = new Contact("Mister", "", "Test", "mr.test@somewhere.net");
		EntityTransaction tnx = entityManager.getTransaction();

		ReferenceSource reference = new ReferenceSource("OBI-test");
		reference.setAcc("test acc");

		// Creates a new Role too
		ContactRole role = new ContactRole("testsuite:TesterRole1", "Test Role, by Test Suite", reference);

		OntologyEntryEJB3DAO<ContactRole> roleDao = new OntologyEntryEJB3DAO<ContactRole>();
		roleDao.setEntityManager(entityManager);

		tnx.begin();
		entityManager.persist(reference);
		tnx.commit();

		tnx.begin();
		Long roleId = roleDao.save(role);
		tnx.commit();

		assertNotNull("OPS! No new test role created!", roleId);
		role.setId(roleId);
		contact.setRole(role);

		tnx = entityManager.getTransaction();
		tnx.begin();
		Long id = dao.save(contact);
		tnx.commit();
		out.println("New Contact ID:" + id);

		Contact contact1 = entityManager.find(Contact.class, id);
		out.println("Contact reloaded: " + contact1);
	}


	@Test
	@Ignore
	/** Create and search data */
	public void testFindByBasicAttributes() throws Exception {
		Contact contact = new Contact("Jon", "", "Smith", "jjs@somewhere.net");
		contact.setPhone("123");
		contact.setAffiliation("The SuperCorp Inc.");

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		dao.save((Contact) contact);
		tnx.commit();

		List<Contact> objs = dao.findByBasicAttributes("Jon", null, "ith", null);
		assertNotNull("Ops! null result! :-( Check you've a proper test data set", objs);
		assertTrue("Ops! Empty Result! :-( Check you've a proper test data set", objs.size() > 0);

		out.println("OK, showing firts item: ");
		Contact obj = objs.get(0);
		out.println(obj);
	}


	@Test
	@Ignore
	/** Lookup data provided by DBUnit */
	public void testFindByBasicAttributes1() throws Exception {
		Contact contact = new Contact("Marco", "", "Brandizi", "m.b@somewhere.net");
		contact.setPhone("456");
		contact.setAffiliation("European Bioinformatics Institute (EBI)");

		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin();
		dao.save((Contact) contact);
		tnx.commit();

		List<Contact> objs = dao.findByBasicAttributes("Marco", null, "Brandizi", null);
		// List<Contact> objs = dao.findByBasicAttributes ( null, null, "Bean", null );
		assertNotNull("Ops! null result! :-( Check you've a proper test data set", objs);
		assertTrue("Ops! Empty Result! :-( Check you've a proper test data set", objs.size() > 0);

		out.println("OK, showing items: ");
		for (Contact obj : objs)
			out.println(obj + "\n\n");
	}


	@Test
	@Ignore
	public void testGetStudy() {
		Contact contact = dao.getById( new Long ( -2 ) );
		assertNotNull("Ops! No contact retrieved!", contact);
		Study study = contact.getStudy();
		assertNotNull("Ops! No study associated to the contact!", study);
		out.println(study);
	}


}
