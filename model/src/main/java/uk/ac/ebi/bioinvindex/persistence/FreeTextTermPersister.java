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
import uk.ac.ebi.bioinvindex.model.term.FreeTextTerm;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Persists an {@link FreeTextTerm}.
 * 
 * This class is abstract in order to make {@link #getPersistedClass()} correctly working.
 * You can do something like: 
 *   <p><code>term = new FreeTextTermPersister&lt;Parameter&gt; ( daoFactory, submissionTs );</code></p>
 * in order to use this persister directly.<p/> 
 * 
 * PLEASE NOTE: this class must be abstract, because of {@link #getPersistedClass()}.
 * 
 * date: Apr 15, 2008
 * 
 * @author brandizi
 *
 * @param <T>
 */
public abstract class FreeTextTermPersister<T extends FreeTextTerm> extends Persister<T>
{
	private final OntologyEntryPersister<OntologyTerm> oePersister;

	public FreeTextTermPersister ( DaoFactory daoFactory, Timestamp submissionTs) {
		super ( daoFactory, submissionTs );
		dao = daoFactory.getFreeTextTermDAO ( getPersistedClass () ); 
		oePersister = new OntologyEntryPersister<OntologyTerm> ( daoFactory, submissionTs ) {};
	}
	
	
	/** 
	 * Always returns the same object, cause a Free Term is always created from scratch. 
	 * Uses the {@link OntologyEntryPersister} for the attached OEs and replaces them in case they already exists in the DB.
	 *  
	 */
	@Override
	protected void preProcess ( T term ) 
	{
		super.preProcess ( term );
		
		// Persist all the OEs associated
		for ( OntologyTerm oe: new ArrayList<OntologyTerm> ( term.getOntologyTerms () ) ) {
			OntologyTerm oenew = oePersister.persist ( oe );
			if ( oenew != oe ) {
				term.removeOntologyTerm ( oe );
				term.addOntologyTerm ( oenew );
			}
		}
	}



}
