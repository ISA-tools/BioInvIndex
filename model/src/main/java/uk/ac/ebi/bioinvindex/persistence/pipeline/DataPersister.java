package uk.ac.ebi.bioinvindex.persistence.pipeline;

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

import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Data;
import uk.ac.ebi.bioinvindex.model.processing.DataNode;
import uk.ac.ebi.bioinvindex.model.term.DataType;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.persistence.AccessiblePersister;
import uk.ac.ebi.bioinvindex.persistence.BIIPersistenceException;
import uk.ac.ebi.bioinvindex.persistence.FactorValuePersister;
import uk.ac.ebi.bioinvindex.persistence.OntologyEntryPersister;

public class DataPersister extends AccessiblePersister<Data>
{
	private final OntologyEntryPersister<DataType> typePersister;
	private final FactorValuePersister fvPersister;


	public DataPersister ( DaoFactory daoFactory, Timestamp submissionTs ) {
		super ( daoFactory, submissionTs );
		typePersister = new OntologyEntryPersister<DataType> ( daoFactory, submissionTs ) {};
		fvPersister = new FactorValuePersister ( daoFactory, submissionTs ) {};
	}


	@Override
	protected void preProcess ( Data data )
	{
		// This is necessary cause otherwise the bloody Hibernate will complain that the node links a transient material
		DataNode backupNode = data.getProcessingNode ();
		data.setProcessingNode ( null );

		super.preProcess ( data );

		// Data Type
		DataType type = data.getType ();
		DataType typeDB = typePersister.persist ( type );
		if ( type != typeDB ) data.setType ( typeDB );

		// FVs
		for ( FactorValue fv: data.getFactorValues () )
			fvPersister.persist ( fv );

		data.setProcessingNode ( backupNode );
	}


//	/**
//	 * Object with the same accession are certainly reused, cause the accession is unique.
//	 */
//	@Override
//	protected Data lookup ( Data data )
//	{
//		// This is necessary cause otherwise the bloody Hibernate will complain that the node links a transient material
//		DataNode backupNode = data.getProcessingNode ();
//		data.setProcessingNode ( null );
//
//		Data result = ((AccessibleDAO<Data>) dao).getByAcc ( data.getAcc () );
//		data.setProcessingNode ( backupNode );
//		return result;
//	}
//
//	/**
//	 * Object with the same accession are certainly reused, cause the accession is unique.
//	 */
//	@Override
//	protected String getCacheKey ( Data data ) {
//		return data.getAcc ();
//	}


	/** Not supported, you MUST always provide an accession */
	@Override
	protected String getAccessionPrefix () {
		throw new BIIPersistenceException ( "We never auto-generate the accession for Data objects" );
	}

}
