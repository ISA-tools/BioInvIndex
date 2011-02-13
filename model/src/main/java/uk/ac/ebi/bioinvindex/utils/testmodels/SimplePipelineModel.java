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

import uk.ac.ebi.bioinvindex.model.Data;
import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.DataAcquisition;
import uk.ac.ebi.bioinvindex.model.processing.DataNode;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.processing.MaterialProcessing;
import uk.ac.ebi.bioinvindex.model.term.Characteristic;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.DataType;
import uk.ac.ebi.bioinvindex.model.term.Factor;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.model.term.MaterialRole;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.Unit;
import uk.ac.ebi.bioinvindex.model.term.UnitValue;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;

/**
 * Provides this model for testing purposes: 
 *
 * <pre>
 * src1---p1---as1---p2---ar1 
 * src2---/            \--ar2
 * </pre>
 * 
 * @author brandizi
 * <b>date</b>: Feb 3, 2010
 *
 */
public class SimplePipelineModel
{
	public ReferenceSource fooOnto;
	public DataType dataType;
	public MaterialRole srcRole;
	public MaterialRole assayRole;
	public Study study;
	public MaterialNode nsrc1;
	public Material src1;
	public Characteristic c1, c2;
	public OntologyTerm c1Ot, cv1Ot;
	public CharacteristicValue cv1, cv2;
	public Unit cv2Unit;
	public UnitValue cv2UnitVal;
	public Factor fact1;
	public FactorValue fv1;
	public MaterialNode nsrc2;
	public Material src2;
	public MaterialProcessing p1;
	public MaterialNode nas1;
	public Material assayMaterial1;
	public DataAcquisition p2;
	public DataNode nar1, nar2;
	public Data dt1, dt2;
	public Factor fact2;
	public FactorValue fv2;
	
	public SimplePipelineModel () 
	{
		fooOnto = new ReferenceSource ( "foo_onto", "Foo Ontology" );  

		dataType = new DataType ( 
			"bii:tests:data:generic_assay_raw_data", "Raw Data Type", 
			fooOnto 
		);
		srcRole = new MaterialRole ( 
			"bii:tests:material:source", "Source", 
			new ReferenceSource ( "bii:tests:ontos:foo", "Foo Ontology")  
		);
		assayRole = new MaterialRole ( 
			"bii:tests:material:generic_assay", "Assay", 
			new ReferenceSource ( "bii:tests:ontos:foo", "Foo Ontology")  
		);
		
		
		study = new Study ( "A foo study" );
		study.setAcc ( "simple_study_n_pipeline_1" );
		
		nsrc1 = new MaterialNode ( study ); nsrc1.setAcc ( "nsrc1" );
		src1 = new Material ( "Source Material 1", srcRole ); src1.setAcc ( "src1" );

	  
		c1 = new Characteristic ( "Organism", 0 );
	  c1Ot = new OntologyTerm ( "bii:tests:characteristics:organism", "Organism", fooOnto  );
	  c1.addOntologyTerm ( c1Ot );
	  cv1 = new CharacteristicValue ( "Mus Musculus", c1 );
	  cv1Ot = new OntologyTerm ( "mus01", "Mus Musculus", fooOnto );
	  cv1.addOntologyTerm ( cv1Ot );
	  
	  c2 = new Characteristic ( "Age", 1 );
	  cv2 = new CharacteristicValue ( "10", c2 );
	  cv2Unit = new Unit ( "Time Unit" );
	  cv2UnitVal = new UnitValue ( "weeks", cv2Unit );
	  cv2.setUnit ( cv2UnitVal );
	  fact1 = new Factor ( "Foo Factor 1", 2 );
	  fv1 = new FactorValue ( "Foo Factor Value 1", fact1 );
	  src1.addCharacteristicValue ( cv1 );
	  src1.addCharacteristicValue ( cv2 );
	  src1.addFactorValue ( fv1 );
		nsrc1.setMaterial ( src1 );
		
		nsrc2 = new MaterialNode ( study ); nsrc2.setAcc ( "nsrc2" );

		src2 = new Material ( "Source Material 2", srcRole ); src2.setAcc ( "src2" );

		nsrc2.setMaterial ( src2 );
		
		p1 = new MaterialProcessing ( study ); p1.setAcc ( "p1" );

		
		p1.addInputNode ( nsrc1 );
		p1.addInputNode ( nsrc2 );
		
		nas1 = new MaterialNode ( study ); nas1.setAcc ( "nas1" );
		
		assayMaterial1 = new Material ( "Assay Material 1", assayRole ); assayMaterial1.setAcc ( "assayMaterial1" );
		nas1.setMaterial ( assayMaterial1 );
		p1.addOutputNode ( nas1 );
		p2 = new DataAcquisition ( study ); p2.setAcc ( "p2" );
		
		p2.addInputNode ( nas1 );
	
		nar1 = new DataNode ( study ); nar1.setAcc ( "nar1" );
		
		
		dt1 = new Data ( "data1.txt", dataType ); dt1.setAcc ( "dt1" );
	  fact2 = new Factor ( "Foo Factor 2", 4 );
	  fv2 = new FactorValue ( "Foo Factor Value 2", fact2 );
		dt1.addFactorValue ( fv2 );
		nar1.setData ( dt1 ); 
		
		nar2 = new DataNode ( study ); nar2.setAcc ( "nar2" );
		
		dt2 = new Data ( "data2.txt", dataType ); dt2.setAcc ( "dt2" );
		nar2.setData ( dt2 );
		p2.addOutputNode ( nar1 );
		p2.addOutputNode ( nar2 );
	}
}
