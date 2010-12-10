package uk.ac.ebi.bioinvindex.utils;

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

import org.hibernate.Session;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;

import uk.ac.ebi.bioinvindex.model.term.AnnotationTypes;
import uk.ac.ebi.bioinvindex.model.xref.AssayTypeDataLocation;
import uk.ac.ebi.bioinvindex.dao.IdentifiableDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.utils.datasourceload.DataLocationManager;
import uk.ac.ebi.bioinvindex.utils.datasourceload.DataSourceLoader;

import java.util.List;

/**
 * @author: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: May 6, 2009
 */
public class DataLocationManagerTest {

	private DataLocationManager locationManager = new DataLocationManager();
	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("BIIEntityManager");
	private EntityManager entityManager;

	public DataLocationManagerTest() 
	{
		entityManager = entityManagerFactory.createEntityManager();
	}

	@Before
	public void loadTestConfiguration() 
	{
		DataSourceLoader loader = new DataSourceLoader();
		loader.setEntityManager(entityManager);
		loader.loadAll( this.getClass().getClassLoader().getResourceAsStream("testdata/dataconfig.xml") );
	}

	@Test
	public void testGetDataLocation() throws Exception
	{
		IdentifiableDAO<AssayTypeDataLocation> dao = DaoFactory.getInstance(entityManager).getIdentifiableDAO(
			AssayTypeDataLocation.class
		);
		List<AssayTypeDataLocation> dataLocations = dao.getAll();
		assertEquals( "Ouch! Wrong number of data locations!", 3, dataLocations.size());

		locationManager.setEntityManager(entityManager);

		String raw_location = locationManager.getDataLocation ( 
			"transcription profiling", "DNA microarray", AnnotationTypes.RAW_DATA_FILE_PATH
		);
		assertEquals ( "Wrong location fetched", "//raw1/acc_goes_here", raw_location );
		
		String dataLocation = locationManager.getISATabMetaDataLocation();
		assertEquals ( "Wrong ISATAB location fetched!", 
			"ftp://ftp.ebi.ac.uk/pub/databases/bii/submission_repo/acc_goes_here", dataLocation
		);
	}
	
	@Test
	public void testReplacement () throws Exception
	{
		IdentifiableDAO<AssayTypeDataLocation> dao = DaoFactory.getInstance(entityManager).getIdentifiableDAO(
			AssayTypeDataLocation.class
		);

		{
			locationManager.setEntityManager(entityManager);

			List<AssayTypeDataLocation> dataLocations = dao.getAll();
			assertEquals( "Ouch! Wrong number of data locations!", 3, dataLocations.size());
	
			String raw_location = locationManager.getDataLocation ( 
				"transcription profiling", "DNA microarray", AnnotationTypes.RAW_DATA_FILE_PATH
			);
			assertEquals ( "Wrong location fetched", "//raw1/acc_goes_here", raw_location );
			
			String dataLocation = locationManager.getISATabMetaDataLocation();
			assertEquals ( "Wrong ISATAB location fetched!", 
				"ftp://ftp.ebi.ac.uk/pub/databases/bii/submission_repo/acc_goes_here", dataLocation
			);
		}

		{
			// Now let's load another one and see
			DataSourceLoader loader = new DataSourceLoader();
	
			loader.setEntityManager ( entityManager );
			loader.loadAll( this.getClass().getClassLoader().getResourceAsStream ( "testdata/dataconfig1.xml" ) );
			Session session = (Session) entityManager.getDelegate ();
			session.clear ();
			locationManager = new DataLocationManager();
			locationManager.setEntityManager ( entityManager );
		}

		{
			List<AssayTypeDataLocation> dataLocations = dao.getAll();
			assertEquals ( "Ouch! Wrong number of data locations!", 4, dataLocations.size());
	
			String raw_location = locationManager.getDataLocation ( 
				"transcription profiling", "DNA microarray", AnnotationTypes.RAW_DATA_FILE_PATH
			);
			assertEquals ( "Wrong replacement location fetched!", "//raw1_new/acc_goes_here", raw_location);
			
			String dataLocation = locationManager.getISATabMetaDataLocation();
			assertNotNull(dataLocation);
			assertEquals ( "Wrong ISATAB location fetched!", "ftp://new/isa", dataLocation);

			String dataLocation1 = locationManager.getDataLocation ( 
				"foo measure", "foo tech", AnnotationTypes.PROCESSED_DATA_FILE_LINK
			);
			assertEquals ( "Wrong new location fetched!", "ftp://processed_foo/${study-acc}", dataLocation1 );

		}

	}
	
	
	
	@Test
	public void testEBILocations () throws Exception
	{
		IdentifiableDAO<AssayTypeDataLocation> dao = DaoFactory.getInstance(entityManager).getIdentifiableDAO(
			AssayTypeDataLocation.class
		);

		{
			locationManager.setEntityManager(entityManager);

			List<AssayTypeDataLocation> dataLocations = dao.getAll();
			assertEquals( "Ouch! Wrong number of data locations!", 3, dataLocations.size());
	
			String raw_location = locationManager.getDataLocation ( 
				"transcription profiling", "DNA microarray", AnnotationTypes.RAW_DATA_FILE_PATH
			);
			assertEquals ( "Wrong location fetched", "//raw1/acc_goes_here", raw_location );
			
			String dataLocation = locationManager.getISATabMetaDataLocation();
			assertEquals ( "Wrong ISATAB location fetched!", 
				"ftp://ftp.ebi.ac.uk/pub/databases/bii/submission_repo/acc_goes_here", dataLocation
			);
		}

		{
			// Now let's load another one and see
			DataSourceLoader loader = new DataSourceLoader();
	
			loader.setEntityManager ( entityManager );
			loader.loadAll( this.getClass().getClassLoader().getResourceAsStream ( "testdata/data_locations.ebi.xml" ) );
			Session session = (Session) entityManager.getDelegate ();
			session.clear ();
			locationManager = new DataLocationManager();
			locationManager.setEntityManager ( entityManager );
		}

		{
//			List<AssayTypeDataLocation> dataLocations = dao.getAll();
//			assertEquals ( "Ouch! Wrong number of data locations!", 4, dataLocations.size());
//	
//			String raw_location = locationManager.getDataLocation ( 
//				"transcription profiling", "DNA microarray", AnnotationTypes.RAW_DATA_FILE_PATH
//			);
//			assertEquals ( "Wrong replacement location fetched!", "//raw1_new/acc_goes_here", raw_location);
//			
//			String dataLocation = locationManager.getISATabMetaDataLocation();
//			assertNotNull(dataLocation);
//			assertEquals ( "Wrong ISATAB location fetched!", "ftp://new/isa", dataLocation);
//
//			String dataLocation1 = locationManager.getDataLocation ( 
//				"foo measure", "foo tech", AnnotationTypes.PROCESSED_DATA_FILE_LINK
//			);
//			assertEquals ( "Wrong new location fetched!", "ftp://processed_foo/${study-acc}", dataLocation1 );

		}

	}
}
