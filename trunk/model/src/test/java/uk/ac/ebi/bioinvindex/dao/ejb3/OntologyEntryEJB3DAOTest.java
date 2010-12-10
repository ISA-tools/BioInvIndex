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
import static org.junit.Assert.*;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.model.term.ContactRole;
import uk.ac.ebi.bioinvindex.model.term.Measurement;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;

import javax.persistence.EntityTransaction;
import java.util.List;

/**
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Jan 15, 2008
 */
public class OntologyEntryEJB3DAOTest extends DBUnitEJB3DAOTest {

	public OntologyEntryEJB3DAOTest() throws Exception {
		super();
	}

	@Test
	public void testSave() {
		ReferenceSource source = entityManager.getReference(ReferenceSource.class, (long) 100);
		System.out.println("source = " + source);

		ContactRole entry = new ContactRole("test1", "test name", source);

		OntologyEntryEJB3DAO<ContactRole> dao =
				(OntologyEntryEJB3DAO<ContactRole>) daoFactory.getOntologyEntryDAO(ContactRole.class);

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		dao.save(entry);
		entityTransaction.commit();
	}

	@Test
	public void testSave1() {
		ReferenceSource source = entityManager.getReference(ReferenceSource.class, (long) 100);
		System.out.println("source = " + source);

		ContactRole entry = new ContactRole("test1", "test name", source);

		OntologyEntryEJB3DAO dao = (OntologyEntryEJB3DAO) daoFactory.getOntologyEntryDAO();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		dao.save( entry );
		entityTransaction.commit();
	}

	@Test
	public void testSave2() {
		ReferenceSource source = entityManager.getReference(ReferenceSource.class, (long) 100);
		System.out.println("source = " + source);

		ContactRole entry = new ContactRole("test1", "test name", source);

		OntologyEntryEJB3DAO dao = (OntologyEntryEJB3DAO) daoFactory.getOntologyEntryDAO();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		dao.save( entry );
		entityTransaction.commit();
		
		ReferenceSource source1 = entityManager.getReference(ReferenceSource.class, (long) 100);
		System.out.println("source1 = " + source);

		ContactRole entry1 = new ContactRole("test1", "test name", source1);
		System.out.println("entry1 = " + entry1);
		System.out.println("entry = " + entry);

		System.out.println("src1 = " + source1);
		System.out.println("src = " + source);

		assertEquals(source, source1);
		entry.equals(entry1);
		assertEquals(entry1, entry);
	}

	@Test
	public void testGetOntologyEntryByAcc() throws Exception {
		OntologyEntryEJB3DAO dao = (OntologyEntryEJB3DAO) daoFactory.getOntologyEntryDAO();
		OntologyEntry ontologyEntryByAcc = dao.getOntologyEntryByAcc("OBI-AT1");
		assertNotNull(ontologyEntryByAcc);
	}

	@Test
	public void testGetOntologyEntryByAccAndRefSource() throws Exception {
		OntologyEntryEJB3DAO dao = (OntologyEntryEJB3DAO) daoFactory.getOntologyEntryDAO();
		OntologyEntry ontologyEntryByAcc = dao.getOntologyEntryByAccAndRefSource("OBI-AT1", "BII-1");
		assertNotNull(ontologyEntryByAcc);
	}

	@Test
	public void testGetByNameAndRefSource() throws Exception {
		OntologyEntryEJB3DAO<Measurement> dao = (OntologyEntryEJB3DAO<Measurement>) daoFactory.getOntologyEntryDAO(Measurement.class);
		OntologyEntry ontologyEntryByAcc = dao.getByNameAndRefSource("Gene Expression", "BII-1");
		assertNotNull(ontologyEntryByAcc);
	}

	@Test
	public void testGetOntologyEntriesByRefSource() throws Exception {
		OntologyEntryEJB3DAO<OntologyTerm> dao =
				(OntologyEntryEJB3DAO<OntologyTerm>) daoFactory.getOntologyEntryDAO(OntologyTerm.class);

		List<OntologyTerm> result = dao.getOntologyEntriesByRefSource("CHEBI");
		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	public void testGetOntologyEntryAccsByRefSource() throws Exception {
		OntologyEntryEJB3DAO<OntologyTerm> dao =
				(OntologyEntryEJB3DAO<OntologyTerm>) daoFactory.getOntologyEntryDAO(OntologyTerm.class);

		List<String> result = dao.getOntologyEntryAccsByRefSource("CHEBI");
		assertNotNull ( "Null result!", result );
		assertEquals ( "Wrong # of results!", 2, result.size() );
	}

	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_ontology.xml";
	}
}

