package uk.ac.ebi.bioinvindex.persistence;

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
import org.junit.Test;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;

import java.sql.Timestamp;

/**
 * Basic tests of the Persistance architecture (works with {@link ReferenceSourcePersister}).
 *
 * TODO: messages in the assertions
 *
 * date: Apr 8, 2008
 * @author brandizi
 *
 */
public class ReferenceSourcePersistenceTest extends TransactionalDBUnitEJB3DAOTest {

	private ReferenceSourcePersister persister;

	public ReferenceSourcePersistenceTest() throws Exception {
		super();
		persister = new ReferenceSourcePersister ( DaoFactory.getInstance(entityManager), new Timestamp ( System.currentTimeMillis () )  );
	}

	
	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_persistence.xml";
	}


	@Test
	public void testPersistReferenceSourceAddNew () throws Exception {
		ReferenceSource refSource = new ReferenceSource( "test reference" );
		String acc = "xref:999";
		refSource.setAcc ( acc );

		ReferenceSource referenceSourceTest = persister.persist (refSource);
		transaction.commit ();

		assertNotNull(referenceSourceTest);
		assertTrue(entityManager.contains(referenceSourceTest));
		assertTrue ( refSource == referenceSourceTest );
		assertNotNull(referenceSourceTest.getId());
		assertEquals ( acc, referenceSourceTest.getAcc() );
	}



	@Test
	public void testPersistReferenceSourceAttachExisting() throws Exception {
		// Whatever name you specify the object will be replaced by the existing one
		ReferenceSource refSource = new ReferenceSource( null );
		refSource.setAcc ( "BII-1" );

		ReferenceSource referenceSourceTest = persister.persist (refSource);
		transaction.commit ();

		assertNotNull(referenceSourceTest);
		assertTrue(entityManager.contains(referenceSourceTest));
		assertTrue ( refSource != referenceSourceTest );
		assertNotNull(referenceSourceTest.getId());
		assertEquals("BII-1", referenceSourceTest.getAcc());


	}



}
