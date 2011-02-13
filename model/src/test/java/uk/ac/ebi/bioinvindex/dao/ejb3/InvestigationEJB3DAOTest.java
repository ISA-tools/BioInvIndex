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

import org.dbunit.operation.DatabaseOperation;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;

import javax.persistence.EntityTransaction;
import static java.lang.System.out;
import java.util.Collection;

/**
 *
 * @author brandizi
 */
public class InvestigationEJB3DAOTest extends DBUnitEJB3DAOTest
{
	InvestigationEJB3DAO dao;


	public InvestigationEJB3DAOTest () throws Exception {
		super();
	}


	@Before
	public void setUp () throws Exception
	{
		dao = (InvestigationEJB3DAO) daoFactory.getInvestigationDao();
	}



	@Test
	public void testCreation () throws Exception
	{
		Investigation inv = new Investigation ( "A wonderful Test Investigation" );
		inv.setAcc("acc1");

		// Include an Xref too
		
// TODO: this cannot work this way, needs the persister.
//		Xref xref = new Xref ( "FOO.INV.X-REF" );
//		xref.setSource ( new ReferenceSource ( "FOO.INV.SOURCE" ) );
//		inv.addXref ( xref );
		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin ();
		Long id = dao.save ( inv );
		tnx.commit ();

		out.println("\n********** New Investigation ID:" + id );
		Investigation inv1 =  dao.getById ( id );
		assertNotNull ( "URP! No investigation reloaded! :-(", inv1 );
		out.println ( "********** Investigation reloaded: " + inv1 );
	}

	@Test
	public void testCreateRelatedStudy ()
	{
		Investigation inv = new Investigation ( "Another wonderful Test Investigation" );
		inv.setAcc("acc1");
		
		EntityTransaction tnx = entityManager.getTransaction();
		tnx.begin ();
		Long id = dao.save ( inv );
		tnx.commit ();

		out.println("*** New Investigation ID:" + id );
		tnx = entityManager.getTransaction();
		tnx.begin ();

		// Include a study
		inv =  dao.getById ( id );
		StudyEJB3DAO daos = new StudyEJB3DAO ();
		daos.setEntityManager ( entityManager );
		Study study = daos.getById ( new Long ( -1 ) );
		study.addInvestigation ( inv );
		daos.save ( study );
		inv.addStudy ( study );
		dao.save ( inv );
		tnx.commit ();

		Investigation inv2 =  dao.getById ( id );
		assertNotNull ( "URP! No investigation reloaded (study creation test) ! :-(", inv2 );
		out.println ( "\n\n********** Investigation reloaded (study creation test): " + inv2 );
		Collection<Study> studies = inv2.getStudies ();
		assertNotNull ( "URP! No Studies saved in the investigation!", studies );
		assertTrue ( "URP! Zero studies from the saved investigation!", studies.size () > 0 );
		out.println ( "\n*** Investigation's studies:" );
		for ( Study s: studies )
			out.println ( s );
	}



	@Test
	public void testLoad () throws Exception
	{
		Investigation inv =  dao.getById ( new Long ( -10 ) );
		assertNotNull ( "Loading of test investigation failed! Check you have a test data set", inv );

		out.println ( "\n\n**** Investigation from test data: " + inv );
		Collection<Study> studies = inv.getStudies ();
		assertNotNull ( "URP! No Studies loaded from the investigation!", studies );
		assertTrue ( "URP! Zero studies from the saved investigation!", studies.size () > 0 );
		out.println ( "\n*** Investigation's studies:" );
		for ( Study s: studies )
			out.println ( s );
	}

	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "sample-data.xml";
	}
}
