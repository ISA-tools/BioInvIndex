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
import uk.ac.ebi.bioinvindex.model.processing.GraphElement;
import uk.ac.ebi.bioinvindex.persistence.AccessiblePersister;
import uk.ac.ebi.bioinvindex.persistence.BIIPersistenceException;

/**
 * Abstract persister for pipeline graph element. 
 * 
 * @author brandizi
 * <b>date</b>: Feb 3, 2010
 * 
 * @param <GE>
 * @see {@link GenericGraphElementPersister}
 * 
 */
public abstract class GraphElementPersister<GE extends GraphElement> extends AccessiblePersister<GE>
{
	public GraphElementPersister ( DaoFactory daoFactory, Timestamp submissionTs ) {
		super ( daoFactory, submissionTs );
	}

	/** Not supported, you MUST always provide an accession */
	@Override
	protected String getAccessionPrefix () {
		throw new BIIPersistenceException ( "We never auto-generate the accession for Data objects" );
	}

	/**
	 * Just calls the super, needed for implementing subclasses/delegates.
	 */
	@Override
	protected void preProcess ( GE ge ) 
	{
		super.preProcess ( ge );
	}

	/**
	 * Just calls the super, needed for implementing subclasses/delegates.
	 */
	@Override
	protected void postProcess ( GE object ) {
		super.postProcess ( object );
	}

	/**
	 * Just calls the super, needed for implementing subclasses/delegates.
	 */
	@Override
	protected GE lookup ( GE object ) {
		return super.lookup ( object );
	}

	/**
	 * Just calls the super, needed for implementing subclasses/delegates.
	 */
	@Override
	protected void forwardPreProcess ( GE object ) {
		super.forwardPreProcess ( object );
	}

	/**
	 * Just calls the super, needed for implementing subclasses/delegates.
	 */
	@Override
	protected GE cachedLookup ( GE object ) {
		return super.cachedLookup ( object );
	}

	/**
	 * Just calls the super, needed for implementing subclasses/delegates.
	 */
	@Override
	protected String getCacheKey ( GE object ) {
		return super.getCacheKey ( object );
	}

	/**
	 * Just calls the super, needed for implementing subclasses/delegates.
	 */
	@Override
	protected Long save ( GE object ) {
		return super.save ( object );
	}

	/**
	 * Just calls the super, needed for implementing subclasses/delegates.
	 */
	@Override
	public GE persist ( GE object ) {
		return super.persist ( object );
	}

}
