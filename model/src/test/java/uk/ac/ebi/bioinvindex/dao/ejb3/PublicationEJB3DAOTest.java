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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.model.term.PublicationStatus;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.model.xref.Xref;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;

import javax.persistence.EntityTransaction;
import static java.lang.System.out;
import java.util.Collection;


/**
 *
 * <dl><dt>date:</dt><dd>Sept 11, 2007</dd></dl>
 * @author brandizi
 *
 */
public class PublicationEJB3DAOTest extends DBUnitEJB3DAOTest
{
	PublicationEJB3DAO dao;


	public PublicationEJB3DAOTest () throws Exception {
		super();
	}

	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "contact-data.xml";
	}


	@Before
	public void setUp () throws Exception
	{
		dao = new PublicationEJB3DAO ();
		dao.setEntityManager ( entityManager );
	}

	@Test
	@Ignore
	public void testCreation () throws Exception
	{
		Publication pub = new Publication ( "Some stupid things of life", "Mister X" );

//		Xref xref = new Xref ( "FOO.X-REF" );
//		pub.addXref ( xref );
//		xref.setSource ( new ReferenceSource ( "FOO.SOURCE" ) );

		ReferenceSource reference = new ReferenceSource("OBI-test");
		reference.setAcc("test acc");
		PublicationStatus status = new PublicationStatus ( "pub.status.published", "Published", reference );
		pub.setStatus ( status );

		EntityTransaction tnx  = entityManager.getTransaction();
		tnx.begin ();
		entityManager.persist(status);
		Long id = dao.save ( pub );
		tnx.commit ();


		Publication pub1 =  dao.getById ( id );
		assertNotNull ( "URP! No publication reloaded! :-(", pub1 );

		status = pub1.getStatus ();
		assertNotNull ( "No PUB Status!", status );

//		Collection<Xref> refs = pub1.getXrefs ();
//		assertNotNull ( "No References!", refs );
//		out.println ( "  Xrefs: " );
//		for ( Xref xref1: refs ) {
//			ReferenceSource xsrc = xref1.getSource ();
//			if ( "FOO.X-REF".equals ( xref1.getAcc() ) ) {
//				assertNotNull ( "This Xref is expected to have a source!", xsrc );
//			}
//		}
  }

}
