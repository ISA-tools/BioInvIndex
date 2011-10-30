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

import javax.persistence.Query;

import org.apache.log4j.Level;

import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.GraphElement;
import uk.ac.ebi.bioinvindex.model.processing.Node;
import uk.ac.ebi.bioinvindex.model.processing.Processing;

/**
 * Unloads instances of {@link Study}.
 *
 * @author brandizi
 * <b>date</b>: Oct 27, 2009
 *
 */
public class StudyUnloader extends AbstractReferrerUnloader<Study>
{
	public StudyUnloader ( UnloadManager unloadManager ) {
		super ( unloadManager );
		logLevel = Level.DEBUG;
	}

	/**
	 * Unloads the study's linked entities
	 *
	 */
	@SuppressWarnings ( "unchecked" )
	@Override
	public boolean queue ( Study study )
	{
		if ( !super.queue ( study ) ) return false;
		unloadManager.queueAll ( study.getDesigns () );
		unloadManager.queueAll ( study.getPublications () );
		unloadManager.queueAll ( study.getContacts () );
		unloadManager.queueAll ( study.getProtocols () );
		unloadManager.queueAll ( study.getAssays () );
		unloadManager.queueAll ( study.getAssayResults () );
		unloadManager.queueAll ( study.getInvestigations () );

		// A quick way to get rid of all the nodes in the experimental pipeline associated to this study
		Query q = unloadManager.getDaoFactory ().getEntityManager ().createQuery (
			"SELECT ge FROM " + GraphElement.class.getName () + " ge WHERE ge.study.id = :studyId"
		);
		q.setParameter ( "studyId", study.getId () );
		for ( Object o: q.getResultList () ) {
			if ( o instanceof Processing) unloadManager.queue ( Processing.class, (Processing) o );
			else if ( o instanceof Node) unloadManager.queue ( (Class<Node>) o.getClass (), (Node) o );
		}
		return true;
	}

}
