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

import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.term.ContactRole;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Persists an {@link Contact}.
 *
 * date: Nov 11, 2008
 * @author brandizi
 *
 */
public class ContactPersister extends Persister<Contact>
{
	private final OntologyEntryPersister<ContactRole> rolePersister;

	// See below private Material backupMaterial;

	public ContactPersister ( DaoFactory daoFactory, Timestamp submissionTs )
	{
		super ( daoFactory, submissionTs );
		dao = daoFactory.getContactDao ();
		rolePersister = new OntologyEntryPersister<ContactRole> ( daoFactory, submissionTs ) {};
	}


	/**
	 * It's always new. Returns always the parameter.
	 *
	 */
	@Override
	public void preProcess ( Contact contact )
	{
		// The ancestor works with the accession
		super.preProcess ( contact );

		// Roles
		Collection<ContactRole> roles = new ArrayList<ContactRole> ( contact.getRoles () );
		for ( ContactRole role: roles ) {
			ContactRole roleDB = (ContactRole) rolePersister.persist ( role );
			if ( role != roleDB ) {
				contact.removeRole ( role );
				contact.addRole ( roleDB );
			}
		}
	}


	/** Returns null, a contact is always new. */
	@Override
	protected Contact lookup ( Contact object ) {
		return null;
	}


}
