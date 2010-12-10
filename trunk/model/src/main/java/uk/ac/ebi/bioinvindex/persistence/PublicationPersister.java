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
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.Accessible;
import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.term.PublicationStatus;
import uk.ac.ebi.bioinvindex.utils.AccessionGenerator;

import java.sql.Timestamp;

/**
 * Persists an {@link Contact}.
 * 
 * date: Nov 11, 2008
 * @author brandizi
 *
 */
public class PublicationPersister extends AnnotatablePersister<Publication>
{
	private final OntologyEntryPersister<PublicationStatus> statusPersister;
	
	// See below private Material backupMaterial;
	
	public PublicationPersister ( DaoFactory daoFactory, Timestamp submissionTs ) 
	{
		super ( daoFactory, submissionTs );
		dao = daoFactory.getAnnotatableDAO ( Publication.class );
		statusPersister = new OntologyEntryPersister<PublicationStatus> ( daoFactory, submissionTs ) {};
	}

	
	/**
	 * It's always new. Returns always the parameter.
	 * Assigns the accession.
	 *  
	 */
	@Override
	public void preProcess ( Publication pub ) 
	{
		super.preProcess ( pub );

		// Status
		PublicationStatus status = pub.getStatus ();
		PublicationStatus statusDB = statusPersister.persist ( status );
		if ( statusDB != status )
			pub.setStatus ( statusDB );
	}


	/** Returns null, a publication has 1-many relation with its owner, so cannot be shared. */
	@Override
	protected Publication lookup ( Publication pub ) 
	{
		return null;
	}


//	/**
//	 * Generate a new accession for the publication (without assigning it)
//	 * It tries to use, in order: the PMID, the DOI, auto-generated ID.
//	 * In any case it prefixes it with the owner's accession (investigation or study)
//	 * TODO: since publication-owner is a 1-n relation, it doesn't make sense that this entity is 
//	 * an {@link Accessible}.
//	 *
//	 */
//	public static String generateAccession ( final Publication pub )
//	{
//		String result = "";
//
//		Investigation investigation = pub.getInvestigation ();
//		if ( investigation != null ) {
//			String iacc = StringUtils.trimToNull ( investigation.getAcc () );
//			if ( iacc == null ) throw new BIIPersistenceException (
//				"Cannot generate an accession for a publication belonging to an" +
//				"investigation without an investigation's accession: " + pub
//			);
//			result = iacc;
//		}
//		else
//		{
//			Study study = pub.getStudy ();
//			if ( study == null ) throw new RuntimeException ( "Cannot generate an accession for a publication that" +
//					"doesn't have an owner: " + pub );
//			String sacc = StringUtils.trimToNull ( study.getAcc () );
//			if ( sacc == null ) throw new BIIPersistenceException (
//				"Cannot generate an accession for a publication belonging to a study without" +
//				"a study's accession: " + pub
//			);
//			result = sacc;
//		}
//
//		result += ":pub:";
//
//		// Prefer the PMID as accession
//		String pmid = StringUtils.trimToNull ( pub.getPmid () );
//		if ( pmid != null ) return result + "pmid:" + pmid;
//
//		String doi = StringUtils.trimToNull ( pub.getDoi () );
//		if ( doi != null ) return result + "doi:" + doi;
//
//		return AccessionGenerator.getInstance ().generateAcc ( result + "bii:" );
//	}
//
//
//	/**
//	 * @return null, we use {@link #generateAccession(Publication)} for generating the accession (in {@link #preProcess(Publication)}
//	 */
//	@Override
//	protected String getAccessionPrefix () {
//		return null;
//	}
}
