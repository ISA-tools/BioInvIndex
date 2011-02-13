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
import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.model.term.MaterialRole;
import uk.ac.ebi.bioinvindex.persistence.AccessiblePersister;
import uk.ac.ebi.bioinvindex.persistence.BIIPersistenceException;
import uk.ac.ebi.bioinvindex.persistence.CharacteristicValuePersister;
import uk.ac.ebi.bioinvindex.persistence.FactorValuePersister;
import uk.ac.ebi.bioinvindex.persistence.OntologyEntryPersister;

public class MaterialPersister extends AccessiblePersister<Material>
{
	private final OntologyEntryPersister<MaterialRole> typePersister;
	private final CharacteristicValuePersister cvalPersister;
	private final FactorValuePersister fvPersister;
	
	public MaterialPersister ( DaoFactory daoFactory, Timestamp submissionTs ) {
		super ( daoFactory, submissionTs );
		typePersister = new OntologyEntryPersister<MaterialRole> ( daoFactory, submissionTs ) {};
		cvalPersister = new CharacteristicValuePersister ( daoFactory, submissionTs ) {};
		fvPersister = new FactorValuePersister ( daoFactory, submissionTs ) {};
	}

	
	@Override
	protected void preProcess ( Material material ) 
	{
		// This is necessary cause otherwise the bloody Hibernate will complain that the node links a transient material 
		MaterialNode backupNode = material.getMaterialNode ();
		material.setMaterialNode ( null );

		super.preProcess ( material );
		
		// Material Type
		MaterialRole type = material.getType ();
		MaterialRole typeDB = typePersister.persist ( type );
		if ( type != typeDB ) material.setType ( typeDB );

		// Same for the FVs
		for ( FactorValue fv: material.getFactorValues () ) {
			fvPersister.persist ( fv );
		}
		
		material.setMaterialNode ( backupNode );
	}

	@Override
	protected void postProcess ( Material material ) 
	{
		// Now the characteristic values
		// They're saved here only, and each characteristic has only one material associated, so they're
		// always new
		for ( CharacteristicValue cv: material.getCharacteristicValues () ) {
			cvalPersister.persist ( cv );
		}

		super.postProcess ( material );
	}


	/** Not supported, you MUST always provide an accession */
	@Override
	protected String getAccessionPrefix () {
		throw new BIIPersistenceException ( "We never auto-generate the accession for Material objects" );
	}

}
