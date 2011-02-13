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
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;

import java.sql.Timestamp;

/** 
 * Performs the persistence of a generic {@link PropertyValue}.
 * 
 * The class works by delegating the job to specific delegates (i.e.: delegate pattern).
 * 
 * date: Aug 1, 2008
 * @author brandizi
 *
 */
public class PropertyValuePersister extends AbstractPropertyValuePersister<PropertyValue<?>>
{
	private AbstractPropertyValuePersister<PropertyValue<?>> genericDelegate;
	private CharacteristicValuePersister characteriticDelegate;
	private AbstractPropertyValuePersister<FactorValue> factorValueDelegate;

	
	public PropertyValuePersister ( DaoFactory daoFactory, Timestamp submissionTs ) {
		super ( daoFactory, submissionTs );
	}

	@SuppressWarnings("unchecked")
	private AbstractPropertyValuePersister<PropertyValue<?>> getDelegate ( PropertyValue<?> object )
	{
		if ( (PropertyValue<?>) object instanceof CharacteristicValue ) {
			if ( characteriticDelegate == null )
				characteriticDelegate = new CharacteristicValuePersister ( daoFactory, getSubmissionTs () );
			return (AbstractPropertyValuePersister) characteriticDelegate;
		}

		if ( (PropertyValue<?>) object instanceof FactorValue ) {
			if ( factorValueDelegate == null )
				factorValueDelegate = new AbstractPropertyValuePersister<FactorValue> ( daoFactory, getSubmissionTs () ) {};
			return (AbstractPropertyValuePersister) factorValueDelegate;
		}
		
		if ( genericDelegate == null )
			genericDelegate = new AbstractPropertyValuePersister<PropertyValue<?>> ( daoFactory, getSubmissionTs () ) {};
		return (AbstractPropertyValuePersister) genericDelegate;
	}
	
	/**
	 * Uses the delegate
	 */
	@Override
	protected PropertyValue<?> lookup ( PropertyValue<?> object ) {
		return getDelegate ( object ).lookup ( object );
	}
	
	/**
	 * Uses the delegate
	 */
	@Override
	protected PropertyValue<?> cachedLookup ( PropertyValue<?> object ) {
		return getDelegate ( object ).cachedLookup ( object );
	}

	/**
	 * Uses the delegate
	 */
	@Override
	protected String getCacheKey ( PropertyValue<?> object ) {
		return getDelegate ( object ).getCacheKey ( object );
	}

	/**
	 * Uses the delegate
	 */
	@Override
	public PropertyValue<?> persist ( PropertyValue<?> object ) {
		return getDelegate ( object ).persist ( object );
	}


	/**
	 * Uses the delegate
	 */
	@Override
	protected void preProcess ( PropertyValue<?> propValue ) {
		getDelegate ( propValue ).preProcess ( propValue );
	}
	
	
	/**
	 * Uses the delegate
	 */
	@Override
	protected void postProcess ( PropertyValue<?> object ) {
		getDelegate ( object ).postProcess ( object );
	}


	/**
	 * Uses the delegate
	 */
	@Override
	protected Long save ( PropertyValue<?> term ) {
		return getDelegate ( term ).save ( term );
	}

}
