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

import static java.lang.System.out;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Protocol;
import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.term.Characteristic;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.ContactRole;
import uk.ac.ebi.bioinvindex.model.term.Design;
import uk.ac.ebi.bioinvindex.model.term.Factor;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.model.term.ProtocolComponent;
import uk.ac.ebi.bioinvindex.model.term.ProtocolType;
import uk.ac.ebi.bioinvindex.model.term.PublicationStatus;
import uk.ac.ebi.bioinvindex.model.term.Unit;
import uk.ac.ebi.bioinvindex.model.term.UnitValue;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;

public class StudyUnloaderTest extends TransactionalDBUnitEJB3DAOTest
{

	public StudyUnloaderTest () throws Exception {
		super ();
	}


	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_unloading.xml";
	}

	@Test
	public void testStudyUnload ()
	{
		String acc = "bii:-5";

		assertNotNull ( "Oh no! The unloaded study is not here!",
			daoFactory.getStudyDAO ().getByAcc ( acc ) );
		assertNotNull ( "Oh no! The design #-5 associated to the study is not here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -5L ) );
		assertNotNull ( "Oh no! The design #-11 associated to the study is not here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -11L ) );
		assertNotNull ( "Oh no! The OE #-10 is not here!",
			daoFactory.getIdentifiableDAO ( OntologyTerm.class ).getById ( -10L ) );
		assertNotNull ( "Oh no! The RefSource #-210 is not here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -210L ) );
		
		
		Timestamp submissionTs = Timestamp.valueOf ( "2008-08-18 16:21:20.200000000" );

		UnloadManager unloaderMgr = new UnloadManager ( DaoFactory.getInstance ( entityManager ), submissionTs );
		StudyUnloader unloader = (StudyUnloader) unloaderMgr.getUnloader ( Study.class );
		unloader.queueByAcc ( acc );
		unloaderMgr.delete ();
		transaction.commit ();
		session.flush ();
		out.println ( " **** Warnings/Errors: " + unloaderMgr.getMessages () );
		

		assertNull ( "Oh no! The unloaded study is still here!",
			daoFactory.getStudyDAO ().getByAcc ( acc ) );
		assertNull ( "Oh no! The design #-5 associated to the study is still here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -5L ) );
		assertNull ( "Oh no! The design #-11 associated to the study is still here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -11L ) );
		assertNull ( "Oh no! The OE #-10 is still here!",
			daoFactory.getIdentifiableDAO ( OntologyTerm.class ).getById ( -10L ) );
		assertNull ( "Oh no! The RefSource #-210 is still here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -210L ) );
	}

	@Test
	public void testStudyProtocols ()
	{
		Timestamp submissionTs = Timestamp.valueOf ( "2008-08-18 16:21:20.200000000" );
		String acc = "bii:-6";

		assertNotNull ( "Oh no! The unloaded study is not here!",
			daoFactory.getStudyDAO ().getByAcc ( acc ) );

		assertNotNull ( "Oh no! The design associated to the study is not here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -6L ) );
		assertNotNull ( "Oh no! The OE #-6 is not here!",
			daoFactory.getIdentifiableDAO ( OntologyTerm.class ).getById ( -6L ) );
		assertNotNull ( "Oh no! The RefSource #-210 is not here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -200L ) );

		assertNotNull ( "Oh no! The Protocol #-1 is not here!",
			daoFactory.getIdentifiableDAO ( Protocol.class ).getById ( -1L ) );

		assertNotNull ( "Oh no! The ProtocolType #-11 is not here!",
			daoFactory.getIdentifiableDAO ( ProtocolType.class ).getById ( -11L ) );

		assertNotNull ( "Oh no! The Param #-1 is not here!",
			daoFactory.getIdentifiableDAO ( Parameter.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The OE #-11 is not here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -11L ) );
		assertNotNull ( "Oh no! The RefSource #-202 is not here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -202L ) );

		assertNotNull ( "Oh no! The Component #-12 is not here!",
			daoFactory.getIdentifiableDAO ( ProtocolComponent.class ).getById ( -12L ) );
		assertNotNull ( "Oh no! The RefSource #-1012 is not here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -1012L ) );		
		
		UnloadManager unloaderMgr = new UnloadManager ( DaoFactory.getInstance ( entityManager ), submissionTs );
		StudyUnloader unloader = (StudyUnloader) unloaderMgr.getUnloader ( Study.class );
		unloader.queueByAcc ( acc );
		unloaderMgr.delete ();
		transaction.commit ();
		session.flush ();
		out.println ( " **** Warnings/Errors: " + unloaderMgr.getMessages () );

		
		assertNull ( "Oh no! The unloaded study is still here!",
			daoFactory.getStudyDAO ().getByAcc ( acc ) );

		assertNull ( "Oh no! The design associated to the study is still here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -6L ) );
		assertNotNull ( "Oh no! The OE #-6 should still be here!",
			daoFactory.getIdentifiableDAO ( OntologyTerm.class ).getById ( -6L ) );
		assertNotNull ( "Oh no! The RefSource #-200 should be still here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -200L ) );

		assertNull ( "Oh no! The Protocol #-1 is still here!",
			daoFactory.getIdentifiableDAO ( Protocol.class ).getById ( -1L ) );

		assertNull ( "Oh no! The ProtocolType #-11 is still here!",
			daoFactory.getIdentifiableDAO ( ProtocolType.class ).getById ( -11L ) );

		assertNull ( "Oh no! The Param #-1 is still here!",
			daoFactory.getIdentifiableDAO ( Parameter.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The RefSource #-202 should still be here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -202L ) );

		assertNull ( "Oh no! The Component #-12 is still here!",
			daoFactory.getIdentifiableDAO ( ProtocolComponent.class ).getById ( -12L ) );
		assertNull ( "Oh no! The RefSource #-1012 is still here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -1012L ) );		

	}
	
  @Test
	public void testStudyAssays ()
	{
		Timestamp submissionTs = Timestamp.valueOf ( "2008-08-18 17:21:20.200000000" );

		assertNotNull ( "Oh no! The unloaded study is not here!",
			daoFactory.getStudyDAO ().getByAcc ( "bii:-7" ) );
		assertNotNull ( "Oh no! The design associated to the study is not here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -8L ) );
		assertNotNull ( "Oh no! The assay #-1! is not here!",
			daoFactory.getIdentifiableDAO ( Assay.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The assay #-2! is not here!",
			daoFactory.getIdentifiableDAO ( Assay.class ).getById ( -2L ) );
		assertNotNull ( "Oh no! The OE #-20! is not here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -20L ) );
		assertNotNull ( "Oh no! The OE #-21! is not here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -21L ) );
		assertNotNull ( "Oh no! The  CharacteristicValue #-1! is not here!",
			daoFactory.getIdentifiableDAO ( CharacteristicValue.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The  FactorValue #-2! is not here!",
			daoFactory.getIdentifiableDAO ( FactorValue.class ).getById ( -2L ) );
		assertNotNull ( "Oh no! The  Factor #-3! is not here!",
			daoFactory.getIdentifiableDAO ( Factor.class ).getById ( -3L ) );
		assertNotNull ( "Oh no! The  Characteristic #-2! is not here!",
			daoFactory.getIdentifiableDAO ( Characteristic.class ).getById ( -2L ) );
		assertNotNull ( "Oh no! The  ReferenceSource #-211! is not here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -211L ) );
		assertNotNull ( "Oh no! The  UnitValue #-1! is not here!",
			daoFactory.getIdentifiableDAO ( UnitValue.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The  Unit Type #-1! is not here!",
			daoFactory.getIdentifiableDAO ( Unit.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The OE #-22! is not here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -22L ) );
		assertNotNull ( "Oh no! The Role #-23! is not here!", 
			daoFactory.getIdentifiableDAO ( ContactRole.class ).getById ( -23L ) );
		assertNotNull ( "Oh no! The Contact #-1! is not here!", daoFactory.getContactDao ().getById ( -1L ) );
		
		
		UnloadManager unloaderMgr = new UnloadManager ( DaoFactory.getInstance ( entityManager ), submissionTs );
		StudyUnloader unloader = (StudyUnloader) unloaderMgr.getUnloader ( Study.class );
		unloader.queueById ( -7L );
		unloaderMgr.delete ();
		transaction.commit ();
		session.flush ();
		out.println ( " **** Warnings/Errors: " + unloaderMgr.getMessages () );

		
		assertNull ( "Oh no! The unloaded study is still here!",
			daoFactory.getStudyDAO ().getByAcc ( "bii:-7" ) );
		assertNull ( "Oh no! The design associated to the study is still here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -8L ) );
		assertNull ( "Oh no! The assay #-1! is still here!",
			daoFactory.getIdentifiableDAO ( Assay.class ).getById ( -1L ) );
		assertNull ( "Oh no! The assay #-2! is still here!",
			daoFactory.getIdentifiableDAO ( Assay.class ).getById ( -2L ) );
		assertNull ( "Oh no! The OE #-20! is still here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -20L ) );
		assertNull ( "Oh no! The OE #-21! is still here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -21L ) );
		assertNull ( "Oh no! The  CharacteristicValue #-1! is still here!",
			daoFactory.getIdentifiableDAO ( CharacteristicValue.class ).getById ( -1L ) );
		assertNull ( "Oh no! The  FactorValue #-2! is still here!",
			daoFactory.getIdentifiableDAO ( FactorValue.class ).getById ( -2L ) );
		assertNull ( "Oh no! The  Factor #-3! is still here!",
			daoFactory.getIdentifiableDAO ( Factor.class ).getById ( -3L ) );
		assertNull ( "Oh no! The  Characteristic #-2! is still here!",
			daoFactory.getIdentifiableDAO ( Characteristic.class ).getById ( -2L ) );
		assertNull ( "Oh no! The  ReferenceSource #-211! is still here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -211L ) );
		assertNull ( "Oh no! The  UnitValue #-1! is still here!",
			daoFactory.getIdentifiableDAO ( UnitValue.class ).getById ( -1L ) );
		assertNull ( "Oh no! The  Unit Type #-1! is still here!",
			daoFactory.getIdentifiableDAO ( Unit.class ).getById ( -1L ) );
		assertNull ( "Oh no! The OE #-22! is still here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -22L ) );

		assertNull ( "Oh no! The Role #-23! is still here!", 
				daoFactory.getIdentifiableDAO ( ContactRole.class ).getById ( -23L ) );
		assertNull ( "Oh no! The Contact #-1! is still here!", daoFactory.getContactDao ().getById ( -1L ) );

	}

	@Test
	public void testStudyInvestigations ()
	{
		assertNotNull ( "Oh no! The study #7 is not here!",
			daoFactory.getStudyDAO ().getByAcc ( "bii:-7" ) );
		assertNotNull ( "Oh no! The design #8 associated to the study is not here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -8L ) );
		assertNotNull ( "Oh no! The assay #-1! is not here!",
			daoFactory.getIdentifiableDAO ( Assay.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The assay #-2! is not here!",
			daoFactory.getIdentifiableDAO ( Assay.class ).getById ( -2L ) );
		assertNotNull ( "Oh no! The OE #-20! is not here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -20L ) );
		assertNotNull ( "Oh no! The OE #-21! is not here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -21L ) );
		assertNotNull ( "Oh no! The  CharacteristicValue #-1! is not here!",
			daoFactory.getIdentifiableDAO ( CharacteristicValue.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The  Characteristic #-1! is not here!",
			daoFactory.getIdentifiableDAO ( Characteristic.class ).getById ( -2L ) );
		assertNotNull ( "Oh no! The  ReferenceSource #-211! is not here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -211L ) );
		assertNotNull ( "Oh no! The  UnitValue #-1! is not here!",
			daoFactory.getIdentifiableDAO ( UnitValue.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The  Unit Type #-1! is not here!",
			daoFactory.getIdentifiableDAO ( Unit.class ).getById ( -1L ) );
		assertNotNull ( "Oh no! The OE #-22! is not here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -22L ) );
		assertNotNull ( "Oh no! The Investigation #-11! is not here!",
			daoFactory.getIdentifiableDAO ( Investigation.class ).getById ( -11L ) );
		assertNotNull ( "Oh no! The Investigation #-10! is not here!",
			daoFactory.getIdentifiableDAO ( Investigation.class ).getById ( -10L ) );
		assertNotNull ( "Oh no! The Publication #-11! is not here!",
			daoFactory.getIdentifiableDAO ( Publication.class ).getById ( -11L ) );
		assertNotNull ( "Oh no! The Publication Status #-31! is not here!",
			daoFactory.getIdentifiableDAO ( PublicationStatus.class ).getById ( -31L ) );
		assertNotNull ( "Oh no! The Publication #-7! is not here!",
			daoFactory.getIdentifiableDAO ( Publication.class ).getById ( -7L ) );
		assertNotNull ( "Oh no! The Publication Status #-30! is not here!",
			daoFactory.getIdentifiableDAO ( PublicationStatus.class ).getById ( -30L ) );


		Timestamp submissionTs = Timestamp.valueOf ( "2008-08-18 17:21:20.200000000" );
		
		UnloadManager unloaderMgr = new UnloadManager ( DaoFactory.getInstance ( entityManager ), submissionTs );
		StudyUnloader unloader = (StudyUnloader) unloaderMgr.getUnloader ( Study.class );
		unloader.queueById ( -7L );
		unloaderMgr.delete ();
		transaction.commit ();
		session.flush ();
		out.println ( " **** Warnings/Errors: " + unloaderMgr.getMessages () );
		
		assertNull ( "Oh no! The Publication #-11! is still here!",
			daoFactory.getIdentifiableDAO ( Publication.class ).getById ( -11L ) );
		assertNull ( "Oh no! The Publication Status #-31! is still here!",
			daoFactory.getIdentifiableDAO ( PublicationStatus.class ).getById ( -31L ) );
		assertNull ( "Oh no! The Publication #-7! is still here!",
			daoFactory.getIdentifiableDAO ( Publication.class ).getById ( -7L ) );
		assertNull ( "Oh no! The Publication Status #-30! is still here!",
			daoFactory.getIdentifiableDAO ( PublicationStatus.class ).getById ( -30L ) );
		assertNull ( "Oh no! The unloaded study is still here!",
			daoFactory.getStudyDAO ().getByAcc ( "bii:-7" ) );
		assertNull ( "Oh no! The design associated to the study is still here!",
			daoFactory.getIdentifiableDAO ( Design.class ).getById ( -8L ) );
		assertNull ( "Oh no! The assay #-1! is still here!",
			daoFactory.getIdentifiableDAO ( Assay.class ).getById ( -1L ) );
		assertNull ( "Oh no! The assay #-2! is still here!",
			daoFactory.getIdentifiableDAO ( Assay.class ).getById ( -2L ) );
		assertNull ( "Oh no! The OE #-20! is still here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -20L ) );
		assertNull ( "Oh no! The OE #-21! is still here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -21L ) );
		assertNull ( "Oh no! The  CharacteristicValue #-1! is still here!",
			daoFactory.getIdentifiableDAO ( CharacteristicValue.class ).getById ( -1L ) );
		assertNull ( "Oh no! The  Characteristic #-1! is still here!",
			daoFactory.getIdentifiableDAO ( Characteristic.class ).getById ( -2L ) );
		assertNull ( "Oh no! The  ReferenceSource #-211! is still here!",
			daoFactory.getIdentifiableDAO ( ReferenceSource.class ).getById ( -211L ) );
		assertNull ( "Oh no! The  UnitValue #-1! is still here!",
			daoFactory.getIdentifiableDAO ( UnitValue.class ).getById ( -1L ) );
		assertNull ( "Oh no! The  Unit Type #-1! is still here!",
			daoFactory.getIdentifiableDAO ( Unit.class ).getById ( -1L ) );
		assertNull ( "Oh no! The OE #-22! is still here!",
			daoFactory.getIdentifiableDAO ( OntologyEntry.class ).getById ( -22L ) );
		assertNull ( "Oh no! The Investigation #-11! is still here!",
			daoFactory.getIdentifiableDAO ( Investigation.class ).getById ( -11L ) );
		assertNotNull ( "Oh no! The Investigation #-10! should still be here!",
			daoFactory.getIdentifiableDAO ( Investigation.class ).getById ( -10L ) );

	}

}
