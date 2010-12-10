package uk.ac.ebi.bioinvindex.unloading;

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
 * Reciprocal Public License 1.5 (RPL1.5)
 * [OSI Approved License]
 *
 * Reciprocal Public License (RPL)
 * Version 1.5, July 15, 2007
 * Copyright (C) 2001-2007
 * Technical Pursuit Inc.,
 * All Rights Reserved.
 *
 * http://www.opensource.org/licenses/rpl1.5.txt
 *
 * __________
 * Sponsors
 * __________
 * This work has been funded mainly by the EU Carcinogenomics (http://www.carcinogenomics.eu) [PL 037712] and in part by the
 * EU NuGO [NoE 503630](http://www.nugo.org/everyone) projects and in part by EMBL-EBI.
 */

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;

import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Study;

/**
 * Unloads instances of {@link Investigation} (if they have the right submission ID and are not related to 
 * existing studies). 
 * 
 * <p><b>date</b>: Aug 25, 2008</p>
 * @author brandizi
 *
 */
public class InvestigationUnloader extends AbstractReferrerUnloader<Investigation>
{
//	private final static Association referringAssociations [] = new Association [] {
//		new Association ( Study.class, "investigations" )
//	};  
	
	public InvestigationUnloader ( UnloadManager unloadManager ) {
		super ( unloadManager );
		logLevel = Level.DEBUG;
	}

	@Override
	public boolean queue ( Investigation investigation ) 
	{
		if ( investigation == null || investigation.getId () == null ) return false;

		// Give up if there is some study that is not to be deleted, this assumes
		// all studied have been already queued.
		//
		// We cannot arrange this check via getReferringAssociations() and cancelDelete(), 
		// since the publications and contacts must be deleted before the investigations and therefore
		// we couldn't cancel them when it's too late.
		//
		Map<Long, Study> deletedStudies = unloadManager.getDeletedObjectsByType ( Study.class );
		Set<Long> deletedStudyIds = deletedStudies.keySet ();
		
		if ( deletedStudyIds != null && !deletedStudyIds.isEmpty () ) 
		{
			// Should never happen, but anyway...
			for ( Study study: investigation.getStudies () )
				if (  !deletedStudyIds.contains ( study.getId () ) ) {
					if ( log.isTraceEnabled () )
						log.trace ( 
							"Investigatiion " + investigation.getId () + " still has study " + study.getId () + ", won't be deleted" 
					);
					return false;
				}
		}
		else 
		{
			log.warn ( 
				"Internal problem in the unloader: deleting Investigation " + investigation.getAcc () 
				+ " without any study scheduled for deletion" 
			);
			return false;
		}

		// Because we want it away in any case, when no other study is pointing anymore to it
		//
		unloadManager.setAdditionalTs ( investigation.getSubmissionTs () );

		if ( !super.queue ( investigation ) ) return false;
		unloadManager.queueAll ( investigation.getPublications () );
		unloadManager.queueAll ( investigation.getContacts () );
		return true;
	}

	
//	@Override
//	protected void cancelDelete ( Investigation investigation ) 
//	{
//		unloadManager.unqueueAll ( investigation.getPublications () );
//		unloadManager.unqueueAll ( investigation.getContacts () );
//		return true;
//	}

	
//	@Override
//	protected Association [] getReferringAssociations () {
//		return referringAssociations;
//	}


}
