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

package uk.ac.ebi.bioinvindex.persistence.pipeline;

import java.sql.Timestamp;
import java.util.List;

import uk.ac.ebi.bioinvindex.dao.AccessibleDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.processing.Node;
import uk.ac.ebi.bioinvindex.model.processing.Processing;
import uk.ac.ebi.bioinvindex.persistence.AccessiblePersister;
import uk.ac.ebi.bioinvindex.persistence.AssayPersister;
import uk.ac.ebi.bioinvindex.persistence.BIIPersistenceException;
import uk.ac.ebi.bioinvindex.utils.processing.ExperimentalPipelineVisitor;
import uk.ac.ebi.bioinvindex.utils.processing.GraphCollector;
import uk.ac.ebi.bioinvindex.utils.processing.GraphCollector.Edge;

/**
 * Persists the material associated with an assay and cascades over all the graph that is reached from the material
 * node linked to the assay's material. This persister is used by {@link AssayPersister}, to trigger the persistence
 * of the assay associated pipeline.
 *
 * @author brandizi
 * <b>date</b>: Mar 11, 2010
 *
 */
public class AssayMaterialPersister extends AccessiblePersister<Material>
{
	private final GenericGraphElementPersister gePersisterFactory;

	public AssayMaterialPersister ( DaoFactory daoFactory, Timestamp submissionTs )
	{
		super ( daoFactory, submissionTs );
		gePersisterFactory = new GenericGraphElementPersister ( daoFactory, submissionTs );
	}

	/**
	 * Persist the material and all the graph reachable from it.
	 *
	 * TODO: should preprocess/postprocess be private/final?
	 */
	public Material persist ( Material material )
	{
		// We don't save nulls...
		if ( material == null ) {
			log.trace ( "persister of class " + getPersistedClass ().getSimpleName () + ", persisting object is null, returning" );
			return null;
		}

		// It was already saved with some other assay
		if ( material.getId () != null ) {
			if ( log.isTraceEnabled () )
				log.trace ( "persister, not persisting " + material + ", has already an ID, should be already in the DB" );
			return material;
		}

		// If it is already in the DB, returns it
		// TODO: is this fast enough?!
		Material materialDB = cachedLookup ( material );
		if ( materialDB != null ) {
			if ( log.isTraceEnabled () ) log.trace ( "persister, not persisting " + material + ", is already in the DB" );
			return material;
		}

		MaterialNode matNode = material.getMaterialNode ();
		GraphCollector gcoll = new GraphCollector ();
		ExperimentalPipelineVisitor visitor = new ExperimentalPipelineVisitor ( gcoll );
		visitor.visit ( matNode );

		// First save the nodes, then the processings
		for ( Edge edge: gcoll.getEdges () )
		{
			{
				List<Node> inputs = edge.getInputs ();
				int nins = inputs.size ();
				for ( int i = 0; i < nins; i++ )
				{
					Node input = inputs.get ( i );
					Node inputDB = gePersisterFactory.persistCasted ( input );
					if ( inputDB != input )
						inputs.set ( i, inputDB );
				}
			}

			{
				List<Node> outs = edge.getOutputs ();
				int nouts = outs.size ();
				for ( int i = 0; i < nouts; i++ )
				{
					Node out = outs.get ( i );
					Node outDB = gePersisterFactory.persistCasted ( out );
					if ( outDB != out )
						outs.set ( i, outDB );
				}
			}
		}

//		for ( Node node: gcoll.getNodes () )
//			gePersisterFactory.persistCasted ( node );

		for ( Edge edge: gcoll.getEdges () )
		{
			Processing proc = edge.getProcessing ();
			for ( Node input: edge.getInputs () )
				proc.addInputNode ( input );
			for ( Node output: edge.getOutputs () )
				proc.addOutputNode ( output );
			gePersisterFactory.persistCasted ( proc );
		}

		// It is certainly new, because the material and the graph linked to it are saved here only
		// The material is saved in the DB by the chain of node persister invocations.
		// TODO: is it fast enough?!
		return cachedLookup ( material );
	}

	/**
	 * Object with the same accession are certainly reused, cause the accession is unique.
	 */
	@Override
	protected Material lookup ( Material material ) {
		return ((AccessibleDAO<Material>) dao).getByAcc ( material.getAcc () );
	}

	/**
	 * Object with the same accession are certainly reused, cause the accession is unique.
	 */
	@Override
	protected String getCacheKey ( Material material ) {
		return material.getAcc ();
	}

	/** Not supported, you MUST always provide an accession */
	@Override
	protected String getAccessionPrefix () {
		throw new BIIPersistenceException ( "We never auto-generate the accession for ReferenceSource" );
	}
}
