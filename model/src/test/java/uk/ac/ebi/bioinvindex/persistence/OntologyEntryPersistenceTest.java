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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.term.Design;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;

import java.sql.Timestamp;

public class OntologyEntryPersistenceTest extends TransactionalDBUnitEJB3DAOTest
{

	private OntologyEntryPersister<OntologyTerm> persister;

	public OntologyEntryPersistenceTest() throws Exception {
		super();
	}

	@Before
	public void initPersister ()
	{
		// Needs to be instantiated here, so that the internal cache for the ontology terms is cleared
		// before every test.
		//
		persister = new OntologyEntryPersister<OntologyTerm> (
			DaoFactory.getInstance ( entityManager ), new Timestamp ( System.currentTimeMillis () )  ) {};
	}

	protected void prepareSettings() {
		beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
		dataSetLocation = "test_persistence.xml";
	}


	@Test
	public void testOESourceAddNew() throws Exception
	{
		ReferenceSource source = new ReferenceSource ( "My fancy test ontology" );
		source.setAcc ( "bii:tests:MY-SRC-1" );
		OntologyTerm oe = new OntologyTerm ( "biionto:1010", "test oe", source );

		OntologyTerm oenew = persister.persist ( oe );
		transaction.commit ();

		assertNotNull ( "Uh?! No object returned by the persister!", oenew );

		assertTrue ( "Ouch!? The OE returned by the persister should be the same!", oe == oenew );
		assertTrue ( "Urp! The OE was not saved!", entityManager.contains ( oenew ) );
		assertNotNull ( "Oh! The saved object should have an ID", oenew.getId() );

		ReferenceSource sourceDB = oenew.getSource ();
		assertNotNull ( "Ouch! No source for the persisted OE!", sourceDB );
		assertNotNull ( "Argh! The source of the persisted object should have an ID", source.getId () );
	}


	@Test
	public void testOESourceExisting () throws Exception
	{
		ReferenceSource source = new ReferenceSource ( null );
		source.setAcc ( "BII-1" );
		source.setName ("TEST ONTOLOGY");
		OntologyTerm oe = new OntologyTerm ( "OBI-EO1", "organism", source );

		OntologyTerm oenew = persister.persist ( oe );
		transaction.commit ();

		assertNotNull ( "Uh?! No object returned by the persister!", oenew );
		assertTrue ( "Urp! The OE was not saved!", entityManager.contains ( oenew ) );
		assertTrue ( "Ouch!? The OE returned by the persister should be different!", oe != oenew );
		assertTrue ( "Oh! The saved object should have ID=501", oenew.getId() == 501 );

		ReferenceSource sourceDB = oenew.getSource ();
		assertNotNull ( "Ouch! No source for the persisted OE!", sourceDB );
		assertTrue ( "Argh! The source of the persisted object should have ID=100", sourceDB.getId () == 100 );
	}

}
