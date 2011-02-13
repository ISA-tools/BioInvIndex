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

import uk.ac.ebi.bioinvindex.model.Annotation;
import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.processing.MaterialProcessing;
import uk.ac.ebi.bioinvindex.model.processing.ProtocolApplication;
import uk.ac.ebi.bioinvindex.model.term.AnnotationType;
import uk.ac.ebi.bioinvindex.model.term.MaterialRole;

/**
 * Provides this model for testing purposes: 
 * 
 * <pre>
 * src1---p1---sample1---p11---as1---p2---ar1 
 * src2---/      |                     \--ar2
 *               |
 * sample.txt  -&gt;|&lt;-    assay file        
 * </pre>
 *
 * @author brandizi
 * <b>date</b>: Feb 3, 2010
 *
 */
public class SeparatedFilesModel extends FullStudyPipelineModel
{
	public Material sample1;
	public MaterialNode nsample1;
	public MaterialProcessing p11;
	public ProtocolApplication papp11;

	public SeparatedFilesModel () 
	{
		super ();

		src1.addAnnotation ( new Annotation ( new AnnotationType ( "sampleFileId" ), "sample.txt" ) );
		src2.addAnnotation ( new Annotation ( new AnnotationType ( "sampleFileId" ), "sample.txt" ) );
		
		sample1 = new Material ( "sample1", new MaterialRole ( "sample", "Sample", fooOnto ) );
		sample1.setAcc ( "sample1" );
		sample1.addAnnotation ( new Annotation ( new AnnotationType ( "sampleFileId" ), "sample.txt" ) );
		sample1.addAnnotation ( new Annotation ( new AnnotationType ( "assayFileId" ), "assay.txt" ) );
		
		nsample1 = new MaterialNode ( study ); nsample1.setAcc ( "nsample1" );
		nsample1.setMaterial ( sample1 );
		
		p1.removeOutputNode ( nas1 );
		p1.addOutputNode ( nsample1 );

		p11 = new MaterialProcessing ( study ); p11.setAcc ( "p11" );
		
		papp11 = new ProtocolApplication ( proto1 );
		papp1.setAcc ( "papp11" );
		p11.addProtocolApplication ( papp11 );
		p11.addInputNode ( nsample1 );
		p11.addOutputNode ( nas1 );
		
		as1.addAnnotation ( new Annotation ( new AnnotationType ( "assayFileId" ), "assay.txt" ) );
		assayMaterial1.addAnnotation ( new Annotation ( new AnnotationType ( "assayFileId" ), "assay.txt" ) );
		
		dt1.addAnnotation ( new Annotation ( new AnnotationType ( "assayFileId" ), "assay.txt" ) );
		dt2.addAnnotation ( new Annotation ( new AnnotationType ( "assayFileId" ), "assay.txt" ) );
	}

}
