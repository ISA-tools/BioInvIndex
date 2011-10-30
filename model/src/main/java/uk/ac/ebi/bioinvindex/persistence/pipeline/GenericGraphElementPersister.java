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

import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.processing.DataAcquisition;
import uk.ac.ebi.bioinvindex.model.processing.DataNode;
import uk.ac.ebi.bioinvindex.model.processing.DataProcessing;
import uk.ac.ebi.bioinvindex.model.processing.GraphElement;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.processing.MaterialProcessing;
import uk.ac.ebi.bioinvindex.persistence.BIIPersistenceException;

/**
 * Concrete implementation for {@link GraphElement} persistence. Specific persisters are called by
 * {@link #persistCasted(GraphElement)}, depending on the specific object passed there.
 *
 * @author brandizi
 * <b>date</b>: Feb 3, 2010
 *
 */
public class GenericGraphElementPersister extends GraphElementPersister<GraphElement>
{
	private MaterialNodePersister materialNodePersister;
	private DataNodePersister dataNodePersister;
	private MaterialProcessingPersister materialProcPersister;
	private DataAcquisitionPersister dataAcquisitionPersister;
	private DataProcessingPersister dataProcPersister;

	public GenericGraphElementPersister ( DaoFactory daoFactory, Timestamp submissionTs ) {
		super ( daoFactory, submissionTs );
	}

	/**
	 * Gets the proper persister, dependin on the specific parameter type.
	 */
	@SuppressWarnings("unchecked")
	private <GE extends GraphElement> GraphElementPersister<GE> getDelegate ( GE ge )
	{
		if ( materialNodePersister == null )
		{
			Timestamp submissionTs = getSubmissionTs ();
			materialNodePersister = new MaterialNodePersister ( daoFactory, submissionTs );
			dataNodePersister = new DataNodePersister ( daoFactory, submissionTs );
			materialProcPersister = new MaterialProcessingPersister ( daoFactory, submissionTs );
			dataAcquisitionPersister = new DataAcquisitionPersister ( daoFactory, submissionTs );
			dataProcPersister = new DataProcessingPersister ( daoFactory, submissionTs );
		}

		if ( ge instanceof MaterialNode ) return (GraphElementPersister<GE>) materialNodePersister;
		else if ( ge instanceof DataNode ) return (GraphElementPersister<GE>) dataNodePersister;
		else if ( ge instanceof MaterialProcessing ) return (GraphElementPersister<GE>) materialProcPersister;
		else if ( ge instanceof DataAcquisition ) return (GraphElementPersister<GE>) dataAcquisitionPersister;
		else if ( ge instanceof DataProcessing ) return (GraphElementPersister<GE>) dataProcPersister;
		else
			throw new BIIPersistenceException (
				"Internal error: cannot find a persister for the type " + ge.getClass().getSimpleName ()
			);
	}

	/**
	 * Uses the delegate
	 */
	@Override
	protected void preProcess ( GraphElement ge ) {
		getDelegate ( ge ).preProcess ( ge );
	}

	/**
	 * Uses the delegate
	 */
	@Override
	protected GraphElement lookup ( GraphElement ge ) {
		return getDelegate ( ge ).lookup ( ge );
	}

	/**
	 * Uses the delegate
	 */
	@Override
	protected GraphElement cachedLookup ( GraphElement ge ) {
		return getDelegate ( ge ).cachedLookup ( ge );
	}

	/**
	 * Uses the delegate
	 */
	@Override
	protected String getCacheKey ( GraphElement ge ) {
		return getDelegate ( ge ).getCacheKey ( ge );
	}

	/**
	 * Uses the delegate
	 */
	@Override
	protected void forwardPreProcess ( GraphElement ge ) {
		getDelegate ( ge ).forwardPreProcess ( ge );
	}

	/**
	 * Uses the delegate
	 */
	@Override
	public GraphElement persist ( GraphElement ge ) {
		return getDelegate ( ge ).persist ( ge );
	}



	/**
	 * Specific persisters are called here, depending on the specific object passed here.
	 * {@link #persist(GraphElement)} works the same way and in fact this method calls it, the difference is
	 * that here the type casting is automatically done.
	 *
	 */
	@SuppressWarnings("unchecked")
	public <GE extends GraphElement> GE persistCasted ( GE ge ) {
		return (GE) persist ( ge );
	}

	/**
	 * Uses the delegate
	 */
	@Override
	protected void postProcess ( GraphElement ge ) {
		getDelegate ( ge ).postProcess ( ge );
	}

	/**
	 * Uses the delegate
	 */
	@Override
	protected Long save ( GraphElement ge ) {
		return getDelegate ( ge ).save ( ge );
	}

}
