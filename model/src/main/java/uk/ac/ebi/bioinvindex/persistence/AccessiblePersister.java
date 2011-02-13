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
import uk.ac.ebi.bioinvindex.model.Accessible;
import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.utils.AccessionGenerator;

import java.sql.Timestamp;

/** 
 * Specific persister which may be used for {@link Accessible}.
 * 
 * date: Apr 15, 2008
 * @author brandizi
 *
 * @param <A>
 */
public abstract class AccessiblePersister<A extends Accessible> extends AnnotatablePersister<A>
{
	
	public AccessiblePersister ( DaoFactory daoFactory, Timestamp submissionTs ) {
		super ( daoFactory, submissionTs );
		dao = daoFactory.getAccessibleDAO ( getPersistedClass () );
	}

	/**
	 * The default pre-processing consists of assigning a new accession, by means of {@link #getAccessionPrefix()}, 
	 * in case it is still null. 
	 * 
	 */
	protected void preProcess ( A object ) 
	{
		super.preProcess ( object );
		
		String accession = StringUtils.trimToNull ( object.getAcc () );
		if ( accession == null ) { 
			// The current object is new, needs a new accession and needs to be stored afterwards
			if ( log.isTraceEnabled () ) log.trace ( "Accessible Persister, getting new Accession for " + object );
			String newAccession = AccessionGenerator.getInstance ().generateAcc ( getAccessionPrefix() );
			object.setAcc ( newAccession );
			log.trace ( "Accessible Persister, new accession is " + object.getAcc () );
		}
	} 
	
	
	/** May be useful if you don't want to pre-process the way this persister does, but you still need to pre-process
	 *  the object as an Annotatable. You should call this method only in your version of #preProcess()
	 */
	protected void forwardPreProcess ( A object ) {
		super.preProcess ( object );
	}
	
	
	/**
	 * The prefix to be used when a new accession has to be created for the object type managed by this persister.
	 * 
	 */
	protected abstract String getAccessionPrefix(); 

}
