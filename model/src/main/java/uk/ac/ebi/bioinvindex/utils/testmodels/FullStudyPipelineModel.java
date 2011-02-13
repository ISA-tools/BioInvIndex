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
 
package uk.ac.ebi.bioinvindex.utils.testmodels;

import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.term.AssayTechnology;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.model.term.Measurement;

/**
 * Provides the same model given by {@link ProtocolEquippedModel}, adding an assay, assay results and their
 * links with the study. 
 * 
 * @author brandizi
 * <b>date</b>: Feb 4, 2010
 *
 */
public class FullStudyPipelineModel extends ProtocolEquippedModel
{

	public Assay as1;
	public AssayResult ar1, ar2;

	public FullStudyPipelineModel () 
	{
		super ();

		as1 = new Assay ( study );
		as1.setAcc ( "assay1" );
		as1.setAssayPlatform ( "Foo Platform" );
		Measurement measurement1 = new Measurement ( "bii:tests:measurement:foo", "Foo Measurement Type", fooOnto );
		AssayTechnology tech1 = new AssayTechnology ( "bii:tests:technology:foo", "Foo Technology Type", fooOnto );
		as1.setMeasurement ( measurement1 );
		as1.setTechnology ( tech1 );
		as1.setMaterial ( assayMaterial1 );
		study.addAssay ( as1 );
		
		ar1 = new AssayResult ( dt1, study );
		for ( CharacteristicValue cv: ar1.findPipelineCharacteristicValues () )
			ar1.addCascadedPropertyValue ( cv );
		for ( FactorValue fv: ar1.findPipelineFactorValues () )
			ar1.addCascadedPropertyValue ( fv );
		ar1.addAssay ( as1 );
		
		ar2 = new AssayResult ( dt2, study );
		for ( CharacteristicValue cv: ar2.findPipelineCharacteristicValues () )
			ar2.addCascadedPropertyValue ( cv );
		for ( FactorValue fv: ar2.findPipelineFactorValues () )
			ar2.addCascadedPropertyValue ( fv );
		ar2.addAssay ( as1 );
	}
}
