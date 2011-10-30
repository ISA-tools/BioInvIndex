package uk.ac.ebi.bioinvindex.unloading;

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
import static org.junit.Assert.assertNull;
import org.junit.Test;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.term.Design;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;

import static java.lang.System.out;
import java.sql.Timestamp;

public class FreeTextTermUnloaderTest extends TransactionalDBUnitEJB3DAOTest
{

	public FreeTextTermUnloaderTest () throws Exception {
		super ();
	}


	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_unloading.xml";
	}


	@Test
	public void testFreeTextTermUnload ()
	{
		Timestamp submissionTs = Timestamp.valueOf ( "2008-08-25 17:21:20.200000000" );

		UnloadManager unloaderMgr = new UnloadManager ( DaoFactory.getInstance ( entityManager ), submissionTs );
		unloaderMgr.queueAllByTs ( Design.class );
		unloaderMgr.delete ();
		transaction.commit ();
		session.flush ();
		out.println ( " **** Warnings/Errors: " + unloaderMgr.getMessages () );

		assertNull ( "Oh no! The unloaded term is still here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -9L ) );
		assertNull ( "Oh no! The unloaded term's OE is still here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -99L ) );
		assertNull ( "Oh no! The unloaded term's OE source is still here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -999L ) );
	}



	@Test
	public void testFreeTextTermUnloadKeepSource ()
	{
		Timestamp submissionTs = Timestamp.valueOf ( "2008-08-18 15:21:20.200000000" );
		UnloadManager unloaderMgr = new UnloadManager ( DaoFactory.getInstance ( entityManager ), submissionTs );
		unloaderMgr.queueAllByTs ( Design.class );
		unloaderMgr.delete ();
		transaction.commit ();
		session.flush ();
		out.println ( " **** Warnings/Errors: " + unloaderMgr.getMessages () );

		assertNull ( "Oh no! The unloaded term is still here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -7L ) );
		assertNull ( "Oh no! The unloaded term's OE #-7 is still here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -7L ) );
		assertNull ( "Oh no! The unloaded term's OE #-8 is still here!",
				daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -8L ) );
		assertNull ( "Oh no! The unloaded term's OE source #-200 is still here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -201L ) );
		assertNotNull ( "Oh no! The unloaded term's OE source #-202 should be still here!",
				daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -202L ) );
	}


}
