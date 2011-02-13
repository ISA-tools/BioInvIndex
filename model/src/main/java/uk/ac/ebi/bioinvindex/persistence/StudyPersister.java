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

import uk.ac.ebi.bioinvindex.dao.StudyDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Protocol;
import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.term.Design;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;

/**
 * Persists a {@link Study}.
 * 
 * date: Apr 15, 2008
 * @author brandizi
 *
 */
public class StudyPersister extends AccessiblePersister<Study>
{
	private final FreeTextTermPersister<Design> designPersister;
	private final ProtocolPersister protocolPersister;
	private final AssayPersister assayPersister;
	private final AssayResultPersister assayResultPersister;
	private final InvestigationPersister investigationPersister;
	private final PublicationPersister pubPersister; 
	private final ContactPersister contactPersister; 
	
	private List<Investigation> backupInvestigations;
	
	public StudyPersister ( DaoFactory daoFactory, Timestamp submissionTs ) 
	{
		super ( daoFactory, submissionTs );
		dao = daoFactory.getStudyDAO ();
		designPersister = new FreeTextTermPersister<Design> ( daoFactory, submissionTs ) {};
		protocolPersister = new ProtocolPersister ( daoFactory, submissionTs );
		assayPersister = new AssayPersister ( daoFactory, submissionTs );
		assayResultPersister = new AssayResultPersister ( daoFactory, submissionTs );
		investigationPersister = new InvestigationPersister ( daoFactory, submissionTs );
		pubPersister = new PublicationPersister ( daoFactory, submissionTs );
		contactPersister = new ContactPersister ( daoFactory, submissionTs );
		logLevel = Level.DEBUG;
	}

	
	/**
	 * Works on several related objects (protocols, investigations). 
	 * 
	 */
	@Override
	public void preProcess ( Study study ) 
	{
		// Pass the work on the accession to the ancestor.
		super.preProcess ( study );
		
		// Designs, we don't need to replace it, it's always new
		for ( Design design: study.getDesigns () )
			designPersister.persist ( design );
		
		// Protocols
		Collection<Protocol> protocols = new ArrayList<Protocol> ( study.getProtocols () );
		for ( Protocol protocol: protocols ) {
			Protocol protocolDB = protocolPersister.persist ( protocol );
			if ( protocol != protocolDB ) {
				study.removeProtocol ( protocol );
				study.addProtocol ( protocolDB );
			}
		}
	
		
		// Now work on the investigations, we must save them and temporary remove, because of 
		// referential integrity checkings. 
		// TODO: is there a smarter way?
		//
		this.backupInvestigations = new ArrayList<Investigation> ( study.getInvestigations () );
		for ( Investigation investigation: backupInvestigations )
			if ( investigation.getId () == null )
				study.removeInvestigation ( investigation );
	}


	/**
	 * Works on things which have to be saved once we have a study in the DB (assays, investigations).
	 */
	@Override
	protected void postProcess ( Study study ) 
	{
		// restore temporarily removed investigations, and persist them.
		// PLEASE NOTE: dunno why, but this must be the first operation, otherwise you get some bloody
		// org.hibernate.TransientObjectException. 
		//
		boolean needsUpdate = false;
		for ( Investigation investigation: this.backupInvestigations ) 
		{
			Investigation investigationDB = investigationPersister.persist ( investigation );
			if ( !study.getInvestigations ().contains ( investigationDB ) ) {
				study.addInvestigation ( investigationDB );
				needsUpdate = true;
			}
		}
		
		if ( needsUpdate )
			dao.update ( study );

		// Publications
		for ( Publication pub: study.getPublications () )
			pubPersister.persist ( pub );

		// Contacts
		for ( Contact contact: study.getContacts () )
			contactPersister.persist ( contact );
		
		// assays
		for ( Assay assay: study.getAssays () ) assayPersister.persist ( assay );

		// assay-results
		for ( AssayResult ar: study.getAssayResults () ) assayResultPersister.persist ( ar );
	
		super.postProcess ( study );
	}



	
	@Override
	protected String getAccessionPrefix () {
		return "bii:study:";
	}

	
	/** Returns null, a study is always new */
	@Override
	protected Study lookup ( Study object ) {
		return null;
	}

	/**
	 * Checks that the study accession is unique, throws an exception in case it isn't.
	 * 
	 */
	@Override
	public Study persist ( Study object ) 
	{
		if ( object == null ) {
			log.warn ( "WARNING: attempt to persist a null study, we will ignore this!" );
			return null;
		}

		Study study = ((StudyDAO) dao).getByAcc ( object.getAcc () );
		if ( study != null ) throw new RuntimeException (
			"The accession \"" + object.getAcc () + "\" (assigned to \"" 
				+ StringUtils.substring ( object.getTitle () , 0, 15 ) + "\") is already being used for another study in the" 
				+ " database (assigned to \"" + StringUtils.substring ( object.getTitle () , 0, 15 ) 
				+ "\"), please define another accession"
		);
		return super.persist ( object );
	}

}
