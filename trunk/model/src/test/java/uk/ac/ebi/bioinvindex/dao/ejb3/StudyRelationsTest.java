/*
 * StudyRelationsTest.java
 *
 * Created on September 12, 2007, 2:37 PM
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

import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;

import static java.lang.System.out;
import java.util.Collection;

/**
 * @author brandizi
 */
public class StudyRelationsTest extends DBUnitEJB3DAOTest {

	private StudyEJB3DAO studyDao;

	public StudyRelationsTest() throws Exception {
		super();
	}
	
	protected void prepareSettings() {
		beforeTestOperations.add ( DatabaseOperation.CLEAN_INSERT );
		dataSetLocation = "sample-data.xml";
	}
	

	@Before
	public void setUp() throws Exception {
		studyDao = new StudyEJB3DAO();
		studyDao.setEntityManager(entityManager);
	}


	@Test
	public void testGetContacts() {

		out.println ( "*** Testing Study.getContacts () ***" );
		Study study = studyDao.getById ( Long.valueOf("-1") );
		assertNotNull ( "Ops! No Study! :-( Check you've a proper test data set", study );
		out.println ( "Printing the study: " + study );

		Collection<Contact> contacts = study.getContacts ();
		assertNotNull ( "Ops! No contacts! :-( Check you've a proper test data set", contacts );
		assertTrue		( "Ops! Contacts is empty :-( Check you've a proper test data set", contacts.size () > 0 );

		out.println ( "Printing the contacts: " );
		for ( Contact contact: contacts )
			out.println (  "\t" + contact );
	}

	@Test
	public void testGetPublications ()
	{
		out.println ( "*** Testing Study.getPublications() ***" );
		Study study = studyDao.getById ( Long.valueOf("-1") );
		assertNotNull ( "Ops! No Study! :-( Check you've a proper test data set", study );
		out.println ( "Printing the study: " + study );

		Collection<Publication> pubs = study.getPublications ();
		assertNotNull ( "Ops! No pubs! :-( Check you've a proper test data set", pubs );
		assertTrue		( "Ops! Pubs is empty :-( Check you've a proper test data set", pubs.size () > 0 );

		out.println ( "Printing the pubs: " );
		for ( Publication pub: pubs )
			out.println (  "\t" + pub );
	}


	@Test
	public void testAssays () 
	{
		out.println ( "***** Testing Study<->Assays *****" );
		Study s = new Study ( "test study" );
			s.setAcc ( "s" );
		Assay a1 = new Assay ( s );
			a1.setAcc ( "a1" );
		Assay a2 = new Assay ( s );
			a2.setAcc ( "a2" );
		
		a1.setStudy ( null );
		
		assertTrue ( 
			"Gosh! The study " + s.getTitle ()  + " still contains the assay " + a1.getAcc (), 
			!s.getAssays ().contains ( a1 ) 
		);
		out.println ( "***** /end: Testing Study<->Assays *****" );
	}

}
