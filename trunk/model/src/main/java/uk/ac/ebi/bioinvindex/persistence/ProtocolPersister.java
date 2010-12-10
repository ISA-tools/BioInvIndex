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

import org.apache.commons.lang.StringUtils;
import uk.ac.ebi.bioinvindex.dao.AccessibleDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Protocol;
import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.model.term.ProtocolComponent;
import uk.ac.ebi.bioinvindex.model.term.ProtocolType;

import java.sql.Timestamp;

/**
 * Persists a {@link Protocol}.
 * 
 * date: Apr 15, 2008
 * @author brandizi
 *
 */
public class ProtocolPersister extends AccessiblePersister<Protocol>
{
	private final OntologyEntryPersister<ProtocolType> typePersister;
	private final FreeTextTermPersister<Parameter> parameterPersister;
	private final FreeTextTermPersister<ProtocolComponent> componentPersister;
	
	
	public ProtocolPersister ( DaoFactory daoFactory, Timestamp submissionTs ) 
	{
		super ( daoFactory, submissionTs );
		dao = daoFactory.getAccessibleDAO ( Protocol.class );
		typePersister = new OntologyEntryPersister<ProtocolType> ( daoFactory, submissionTs ) {};
		parameterPersister = new FreeTextTermPersister<Parameter> ( daoFactory, submissionTs ) {};
		componentPersister = new FreeTextTermPersister<ProtocolComponent> ( daoFactory, submissionTs ) {};
	}

	
	/**
	 * works on the persistence of type and parameters. 
	 * 
	 */
	@Override
	public void preProcess ( Protocol protocol ) 
	{
		// New Accession
		super.preProcess ( protocol );

		// Check the type
		ProtocolType type = protocol.getType ();
		ProtocolType typeNew= typePersister.persist ( type );
		if ( type != typeNew )
			protocol.setType ( typeNew );
		
		for ( Parameter parameter: protocol.getParameters () )
			// We don't need checkings, it's always a new object and not a DB object
			parameterPersister.persist ( parameter );

		for ( ProtocolComponent comp: protocol.getComponents () )
			// We don't need checkings...
			componentPersister.persist ( comp );
		
	}


	/** Not supported, you MUST always provide an accession */
	@Override
	protected String getAccessionPrefix () {
		throw new BIIPersistenceException ( "We never auto-generate the accession for Protocol" );
	}

	/** 
	 * Search by accession. When an existing protocol is found, the current one is completely ignored and the properties
	 * of the existing protocol are kept.
	 * 
	 */
	@Override
	protected Protocol lookup ( Protocol object ) 
	{
		// TODO: check source is not null
		String acc = StringUtils.trimToNull ( object.getAcc() );
		
		if ( acc == null )
			// Must be a new protocol
			return null;
		
		Protocol protoDB = ((AccessibleDAO<Protocol>) dao).getByAcc ( object.getAcc () );
		if ( protoDB != null ) return protoDB;
		return null;
	}


}
