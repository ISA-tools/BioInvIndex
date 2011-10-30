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

import uk.ac.ebi.bioinvindex.model.Protocol;
import uk.ac.ebi.bioinvindex.model.processing.ProtocolApplication;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.model.term.ParameterValue;
import uk.ac.ebi.bioinvindex.model.term.Unit;
import uk.ac.ebi.bioinvindex.model.term.UnitValue;

/**
 * Provides the same model given by {@link SimplePipelineModel}, adding protocols and protocol applications to
 * {@link SimplePipelineModel#p1} and {@link SimplePipelineModel#p2}.
 *
 * @author brandizi
 * <b>date</b>: Feb 3, 2010
 *
 */
public class ProtocolEquippedModel extends SimplePipelineModel
{
	public Protocol proto1, proto2;
	public ProtocolApplication papp1, papp2;
	public Parameter param1, param2;
	public OntologyTerm param1Ot;
	public ParameterValue paramv1, paramv2;
	public Unit paramv2Unit;
	public UnitValue paramv2UnitVal;

	public ProtocolEquippedModel ()
	{
		super ();

		proto1 = new Protocol ( "Protocol 1", null ); proto1.setAcc ( "proto1" );
		study.addProtocol ( proto1 );
		papp1 = new ProtocolApplication ( proto1 ); papp1.setAcc ( "papp1" );
		p1.addProtocolApplication ( papp1 );

		param1 = new Parameter ( "Sampling Method", 2 );
	  param1Ot = new OntologyTerm ( "bii:tests:parameters:sampling", "Sampling Method", fooOnto  );
	  param1.addOntologyTerm ( param1Ot );
	  paramv1 = new ParameterValue ( "Filtering", param1 );
	  papp1.addParameterValue ( paramv1 );

		param2 = new Parameter ( "Centrifugation Speed", 3 );
	  paramv2 = new ParameterValue ( "1500", param2 );
	  paramv2Unit = new Unit ( "Angular Speed Unit" );
	  paramv2UnitVal = new UnitValue ( "rpm", paramv2Unit );
	  paramv2.setUnit ( paramv2UnitVal );
	  papp1.addParameterValue ( paramv2 );

		proto2 = new Protocol ( "Protocol 2", null ); proto2.setAcc ( "proto2" );
		study.addProtocol ( proto2 );
		papp2 = new ProtocolApplication ( proto2 ); papp2.setAcc ( "papp2" );
		p2.addProtocolApplication ( papp2 );
	}
}
