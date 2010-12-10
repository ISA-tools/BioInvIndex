/*
 * IndexBuilderTest.java
 *
 * Created on September 24, 2007, 4:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package uk.ac.ebi.bioinvindex.search;

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

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyFreeTextSearchImpl;
import uk.ac.ebi.bioinvindex.utils.mock.TestSetIndexBuilder;
import uk.ac.ebi.bioinvindex.utils.test.DBUnitEJB3DAOTest;

import static java.lang.System.out;

/**
 *
 * Run
 *   mvn -Dtest=indexTestSet test
 *
 * and the current database will be reindexed, without its contents changed.
 * WARNING: You must invoke this from maven with the "index" profile.
 *
 * TODO: transform it into a command.
 *
 * @author brandizi
 */
public class indexTestSet extends DBUnitEJB3DAOTest
{
	private StudyFreeTextSearchImpl search;

	public indexTestSet () throws Exception {
	}

	protected void prepareSettings() {
		beforeTestOperations.clear ();
		dataSetLocation = null;
	}

	@Before
	public void setUp ()
	{
		search = new StudyFreeTextSearchImpl ();
		search.setEntityManager ( this.entityManager );
	}


	@Test
	public void run ()
	{
		out.println ( "*** Indexing the database..." );
		TestSetIndexBuilder idxBuild = new TestSetIndexBuilder ();
		idxBuild.setEntityManager ( this.entityManager );
		idxBuild.indexAll ();
		out.println ( "\n\n ...Done ***" );

		session.flush ();
	}
}
