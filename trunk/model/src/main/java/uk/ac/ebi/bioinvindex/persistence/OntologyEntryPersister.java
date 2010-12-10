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

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.bioinvindex.dao.OntologyEntryDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;


/**
 * Persists an {@link OntologyEntry}. This class is abstract in order to make {@link #getPersistedClass()} correctly working.
 * 
 * You can do something like: 
 *   <p><code>oePersister = new OntologyEntryPersister<OntologyTerm> ( daoFactory ) {};</code></p>
 * in order to use this persister directly.<p> 
 * 
 * date: Apr 15, 2008
 * @author brandizi
 *
 * @param <OE>
 */
public abstract class OntologyEntryPersister<OE extends OntologyEntry> extends AnnotatablePersister<OE>
{
	private final ReferenceSourcePersister sourcePersister;

	public OntologyEntryPersister ( DaoFactory daoFactory, Timestamp submissionTs ) 
	{
		super ( daoFactory, submissionTs );
		dao = daoFactory.getOntologyEntryDAO ( getPersistedClass () );
		sourcePersister = new ReferenceSourcePersister ( daoFactory, submissionTs );
	}

	/**
	 * If the OE doesn't already exist, forward the source to the source persister (set the existing source in case it already
	 * exists), and then saves the OE. Does not fix the accession (as done by {@link AccessiblePersister#preProcess(uk.ac.ebi.bioinvindex.model.impl.Accessible)}),  
	 * since the accession must be always provided. 
	 *  
	 */
	@Override
	public void preProcess ( OE oe ) 
	{
		super.preProcess ( oe );
		
		if ( StringUtils.trimToNull ( oe.getAcc () ) == null )
			throw new BIIPersistenceException ( "We never auto-generate the accession for Ontology Entries, term is:" + oe );
		
		// First check your source and save it if doesn't exist
		ReferenceSource source = oe.getSource ();
		ReferenceSource sourceDB = sourcePersister.persist ( source );
		if ( sourceDB != source )
			oe.setSource ( sourceDB );
	}


	/** Not supported, you MUST always provide an accession */
	protected String getAccessionPrefix () {
		throw new BIIPersistenceException ( "We never auto-generate the accession for Ontology Entries" );
	}


	/**
	 * Searches by accession and source's accession. 
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected OE lookup ( OE object ) 
	{
		String acc = StringUtils.trimToNull ( object.getAcc() );
		
		if ( acc == null || acc.contains ( "NULL-ACCESSION" ) )
			// Assume it's a new term
			return null;
		
		ReferenceSource source =  object.getSource ();

		if ( source == null ) {
			throw new PersistenceException ( "Source is null for term " + object + "!!!" );
		}

		return ((OntologyEntryDAO<OE>) dao).getOntologyEntryByAccAndRefSource ( acc, object.getSource ().getAcc () );
	}


	@Override
	protected String getCacheKey ( OE object ) {
		return object.getAcc() + object.getName() + object.getClass().getName() + object.getSource().getName();
	}
	
}
