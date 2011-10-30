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

import java.sql.Timestamp;
import java.util.LinkedList;

import uk.ac.ebi.bioinvindex.dao.AccessibleDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.Data;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;
import uk.ac.ebi.bioinvindex.persistence.pipeline.DataPersister;

/**
 * Persists an {@link Assay}.
 *
 * date: Apr 15, 2008
 * @author brandizi
 *
 */
public class AssayResultPersister extends Persister<AssayResult>
{
	private final PropertyValuePersister propValuePersister;
	private final AssayPersister assayPersister;
	private final DataPersister dataPersister;

	// See below private Material backupMaterial;

	public AssayResultPersister ( DaoFactory daoFactory, Timestamp submissionTs )
	{
		super ( daoFactory, submissionTs );
		dao = daoFactory.getIdentifiableDAO ( AssayResult.class );
		propValuePersister = new PropertyValuePersister ( daoFactory, submissionTs ) {};
		assayPersister = new AssayPersister ( daoFactory, submissionTs );
		dataPersister = new DataPersister ( daoFactory, submissionTs );
	}


	/**
	 * It's always new. Returns always the parameter.
	 * Does few checkings (eg: that the linked assays are already saved).
	 *
	 */
	@Override
	public void preProcess ( AssayResult ar )
	{
		// The ancestor works with the accession
		super.preProcess ( ar );

		// TODO: shouldn't be needed (see below)
		for ( Assay assay: new LinkedList<Assay> ( ar.getAssays () ) )
		{
			Assay assayDB = assayPersister.persist ( assay );
			if ( assay != assayDB ) {
				ar.removeAssay ( assay );
				ar.addAssay ( assayDB );
			}
		}

		// The cascaded properties are normally saved by the pipeline persisters.
		// In such a case, replacing replacing cascading properties with the instances in the DB is not needed either,
		// cause certainly the objects in memory were saved and updated.
		//
		// We have instead to save them here, if the light persistence mode is enabled
		//
		if ( Persister.isLightPersistence () )
		{
			// The cascaded properties
			for ( PropertyValue<?> pv: ar.getCascadedPropertyValues () ) {
				propValuePersister.persist ( pv );
			}

			ar.setData ( null );
			return;
		}

		// Same applies to data objects
		// The data is persisted by the Pipeline persister
//		Data data = ar.getData ();
//		Data dataDB = dataPersister.cachedLookup ( data );
//		if ( dataDB != null && data != dataDB ) ar.setData ( dataDB );

		Data data = ar.getData ();
		if ( data.getId () == null )
			log.warn ( "WARNING: Saving an assayresult with unsaved data:\n  " + ar + "\n\n  " + data );
	}



	/** Returns null, an assay result is always new. */
	@Override
	protected AssayResult lookup ( AssayResult ar ) {
		return null;
	}


}
