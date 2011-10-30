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
import uk.ac.ebi.bioinvindex.model.term.Unit;
import uk.ac.ebi.bioinvindex.model.term.UnitValue;

import java.sql.Timestamp;

/**
 * Works on the persistence of {@link UnitValue} and its {@link Unit}.
 *
 * date: Apr 23, 2008
 * @author brandizi
 *
 */
public class UnitValuePersister extends FreeTextTermPersister<UnitValue>
{
	private final FreeTextTermPersister<Unit> unitPersister;

	public UnitValuePersister ( DaoFactory daoFactory, Timestamp submissionTs )
	{
		super ( daoFactory, submissionTs );
		unitPersister = new FreeTextTermPersister<Unit> ( daoFactory, submissionTs ) {};
	}

	/**
	 * Checks/persists the unit type.
	 *
	 */
	@Override
	public void preProcess ( UnitValue unitValue )
	{
		super.preProcess ( unitValue );

		Unit type = unitValue.getType ();
		Unit typeDB = unitPersister.persist ( type );
		if ( type != typeDB )
			unitValue.setType ( typeDB );
	}

	@Override
	protected void postProcess ( UnitValue unitValue ) {
		super.postProcess ( unitValue );
		log.trace ( "unit persisted: " + unitValue );
	}


}
