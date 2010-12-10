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
 
package uk.ac.ebi.bioinvindex.persistence;

import java.io.InputStream;
import java.io.StringReader;
import java.sql.Timestamp;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.bioinvindex.dao.AccessibleDAO;
import uk.ac.ebi.bioinvindex.dao.AnnotatableDAO;
import uk.ac.ebi.bioinvindex.dao.IdentifiableDAO;
import uk.ac.ebi.bioinvindex.dao.OntologyEntryDAO;
import uk.ac.ebi.bioinvindex.model.Accessible;
import uk.ac.ebi.bioinvindex.model.Annotatable;
import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.Data;
import uk.ac.ebi.bioinvindex.model.Identifiable;
import uk.ac.ebi.bioinvindex.model.Material;
import uk.ac.ebi.bioinvindex.model.Protocol;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.processing.DataAcquisition;
import uk.ac.ebi.bioinvindex.model.processing.DataNode;
import uk.ac.ebi.bioinvindex.model.processing.MaterialNode;
import uk.ac.ebi.bioinvindex.model.processing.MaterialProcessing;
import uk.ac.ebi.bioinvindex.model.processing.ProtocolApplication;
import uk.ac.ebi.bioinvindex.model.term.AssayTechnology;
import uk.ac.ebi.bioinvindex.model.term.Characteristic;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.DataType;
import uk.ac.ebi.bioinvindex.model.term.Factor;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.model.term.MaterialRole;
import uk.ac.ebi.bioinvindex.model.term.Measurement;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.model.term.ParameterValue;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;
import uk.ac.ebi.bioinvindex.model.term.Unit;
import uk.ac.ebi.bioinvindex.model.term.UnitValue;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.persistence.pipeline.AssayMaterialPersister;
import uk.ac.ebi.bioinvindex.persistence.pipeline.MaterialNodePersister;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;
import uk.ac.ebi.bioinvindex.utils.testmodels.FullStudyPipelineModel;
import uk.ac.ebi.bioinvindex.utils.testmodels.ProtocolEquippedModel;
import uk.ac.ebi.bioinvindex.utils.testmodels.SimplePipelineModel;

import static org.junit.Assert.*;


@SuppressWarnings("unused")
public class PipelinePersistenceTest extends TransactionalDBUnitEJB3DAOTest
{

	public PipelinePersistenceTest () throws Exception {
		super ();
	}

	@Override
	protected void prepareSettings()
	{
		beforeTestOperations.add ( DatabaseOperation.CLEAN_INSERT );
		dataSetLocation = "test_pipeline_persistence.xml";
	}

	@Test
	public void testBasics ()
	{
		SimplePipelineModel pip = new SimplePipelineModel ();
		
		Timestamp ts = new Timestamp ( System.currentTimeMillis () );
		StudyPersister studyPersister = new StudyPersister ( daoFactory, ts );
		AssayMaterialPersister assayMaterialPersister = new AssayMaterialPersister ( daoFactory, ts );
		studyPersister.persist ( pip.study );
		assayMaterialPersister.persist (  pip.assayMaterial1 );
		commitTansaction ();
		session.flush ();
		
		AccessibleDAO<Accessible> adao = daoFactory.getAccessibleDAO ( Accessible.class );
		OntologyEntryDAO<OntologyEntry> oedao = daoFactory.getOntologyEntryDAO ();
				
		assertNotNull ( "Ops! nsrc1 not saved!", adao.getByAcc (  pip.nsrc1.getAcc () ) );
		assertNotNull ( "Ops! nsrc2 not saved!", adao.getByAcc (  pip.nsrc2.getAcc () ) );
		assertNotNull ( "Ops! p1 not saved!", adao.getByAcc (  pip.p1.getAcc () ) );
		assertNotNull ( "Ops! nas1 not saved!", adao.getByAcc (  pip.nas1.getAcc () ) );
		assertNotNull ( "Ops! p2 not saved!", adao.getByAcc (  pip.p2.getAcc () ) );
		assertNotNull ( "Ops! nar1 not saved!", adao.getByAcc (  pip.nar1.getAcc () ) );
		assertNotNull ( "Ops! src1 not saved!", adao.getByAcc (  pip.src1.getAcc () ) );
		assertNotNull ( "Ops! src2 not saved!", adao.getByAcc (  pip.src2.getAcc () ) );
		assertNotNull ( "Ops! assay1 not saved!", adao.getByAcc (  pip.assayMaterial1.getAcc () ) );
		assertNotNull ( "Ops! dt1 not saved!", adao.getByAcc (  pip.dt1.getAcc () ) );
		assertNotNull ( "Ops! dt2 not saved!", adao.getByAcc (  pip.dt2.getAcc () ) );
		assertNotNull ( "Ops! cv1 not saved!",  pip.cv1.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.cv1.getClass () ).getById (  pip.cv1.getId () ) );
		assertNotNull ( "Ops! cv2 not saved!",  pip.cv2.getId () );
		assertNotNull ( "Ops! cv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.cv2.getClass () ).getById (  pip.cv2.getId () ) );
		assertNotNull ( "Ops! fv1 not saved!",  pip.fv1.getId () );
		assertNotNull ( "Ops! fv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.fv1.getClass () ).getById (  pip.fv1.getId () ) );
		assertNotNull ( "Ops! fv2 not saved!",  pip.fv2.getId () );
		assertNotNull ( "Ops! fv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.fv2.getClass () ).getById (  pip.fv2.getId () ) );
		assertNotNull ( "Ops! c1 not saved!",  pip.c1.getId () );
		assertNotNull ( "Ops! c1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.c1.getClass () ).getById (  pip.c1.getId () ) );
		assertNotNull ( "Ops! c2 not saved!",  pip.c2.getId () );
		assertNotNull ( "Ops! c2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.c2.getClass () ).getById (  pip.c2.getId () ) );
		assertNotNull ( "Ops! fact1 not saved!",  pip.fact1.getId () );
		assertNotNull ( "Ops! fact1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.fact1.getClass () ).getById (  pip.fact1.getId () ) );
		assertNotNull ( "Ops! fact2 not saved!",  pip.fact2.getId () );
		assertNotNull ( "Ops! fact2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.fact2.getClass () ).getById (  pip.fact2.getId () ) );
		assertNotNull ( "Ops! cv2UnitVal not saved!",  pip.cv2UnitVal.getId () );
		assertNotNull ( "Ops! cv2UnitVal not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.cv2UnitVal.getClass () ).getById (  pip.cv2UnitVal.getId () ) );
		assertNotNull ( "Ops! cv2Unit not saved!",  pip.cv2Unit.getId () );
		assertNotNull ( "Ops! cv2Unit not saved (retrieval test)!", daoFactory.getIdentifiableDAO (  pip.cv2Unit.getClass () ).getById (  pip.cv2Unit.getId () ) );
		ReferenceSource fooOntoDB = (ReferenceSource) adao.getByAcc (  pip.fooOnto.getAcc () );
		assertNotNull ( "Ops! fooOnto not saved!", fooOntoDB );
		assertEquals ( "Ops! fooOnto #ID is wrong!", -1L,  fooOntoDB.getId ()  );
		assertNotNull ( "Ops! srcRole not saved!", oedao.getByAcc (  pip.srcRole.getAcc () ) );
		assertNotNull ( "Ops! assayRole not saved!", oedao.getByAcc (  pip.assayRole.getAcc () ) );
		assertNotNull ( "Ops! dataType not saved!", oedao.getByAcc (  pip.dataType.getAcc () ) );
		OntologyEntry c1OtDB = oedao.getByAcc (  pip.c1Ot.getAcc () );
		assertNotNull ( "Ops! fooOnto not saved!", c1OtDB );
		assertEquals ( "Ops! fooOnto #ID is wrong!", -1L,  c1OtDB.getId ()  );
		
		pip.nas1 = (MaterialNode) adao.getByAcc ( pip.nas1.getAcc () );
		pip.p1 = (MaterialProcessing) adao.getByAcc ( pip.p1.getAcc () );
		pip.p2 = (DataAcquisition) adao.getByAcc ( pip.p2.getAcc () );
		assertTrue ( "Urp! The p1->nas1 link seems not saved!", pip.nas1.getDownstreamProcessings ().contains ( pip.p1 ) );
		assertTrue ( "Urp! The nas1->p2 link seems not saved!", pip.nas1.getUpstreamProcessings ().contains ( pip.p2 ) );
		
		pip.nar1 = (DataNode) adao.getByAcc ( pip.nar1.getAcc () );
		pip.dt1 = (Data) adao.getByAcc ( pip.dt1.getAcc () );
		assertEquals ( "Urp! The nar1->dt1 link seems not saved!", pip.dt1, pip.nar1.getData () );
		assertEquals ( "Urp! The dt1->nar1 link seems not saved!", pip.nar1, pip.dt1.getProcessingNode () );
	}

	@Test
	public void testWithProtocols ()
	{
		ProtocolEquippedModel pip = new ProtocolEquippedModel ();
		
		Timestamp ts = new Timestamp ( System.currentTimeMillis () );
		StudyPersister studyPersister = new StudyPersister ( daoFactory, ts );
		AssayMaterialPersister assayMaterialPersister = new AssayMaterialPersister ( daoFactory, ts );
		studyPersister.persist ( pip.study );
		assayMaterialPersister.persist ( pip.assayMaterial1 );
		commitTansaction ();
		session.flush ();
		
		AccessibleDAO<Accessible> adao = daoFactory.getAccessibleDAO ( Accessible.class );
		OntologyEntryDAO<OntologyEntry> oedao = daoFactory.getOntologyEntryDAO ();
				
		assertNotNull ( "Ops! nsrc1 not saved!", adao.getByAcc ( pip.nsrc1.getAcc () ) );
		assertNotNull ( "Ops! nsrc2 not saved!", adao.getByAcc ( pip.nsrc2.getAcc () ) );
		assertNotNull ( "Ops! p1 not saved!", adao.getByAcc ( pip.p1.getAcc () ) );
		assertNotNull ( "Ops! nas1 not saved!", adao.getByAcc ( pip.nas1.getAcc () ) );
		assertNotNull ( "Ops! p2 not saved!", adao.getByAcc ( pip.p2.getAcc () ) );
		assertNotNull ( "Ops! nar1 not saved!", adao.getByAcc ( pip.nar1.getAcc () ) );
		assertNotNull ( "Ops! src1 not saved!", adao.getByAcc ( pip.src1.getAcc () ) );
		assertNotNull ( "Ops! src2 not saved!", adao.getByAcc ( pip.src2.getAcc () ) );
		assertNotNull ( "Ops! assayMaterial1 not saved!", adao.getByAcc ( pip.assayMaterial1.getAcc () ) );

		assertNotNull ( "Ops! dt1 not saved!", adao.getByAcc ( pip.dt1.getAcc () ) );
		assertNotNull ( "Ops! dt2 not saved!", adao.getByAcc ( pip.dt2.getAcc () ) );
		assertNotNull ( "Ops! cv1 not saved!", pip.cv1.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv1.getClass () ).getById ( pip.cv1.getId () ) );
		assertNotNull ( "Ops! cv2 not saved!", pip.cv2.getId () );
		assertNotNull ( "Ops! cv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv2.getClass () ).getById ( pip.cv2.getId () ) );
		assertNotNull ( "Ops! fv1 not saved!", pip.fv1.getId () );
		assertNotNull ( "Ops! fv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fv1.getClass () ).getById ( pip.fv1.getId () ) );
		assertNotNull ( "Ops! fv2 not saved!", pip.fv2.getId () );
		assertNotNull ( "Ops! fv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fv2.getClass () ).getById ( pip.fv2.getId () ) );
		assertNotNull ( "Ops! c1 not saved!", pip.c1.getId () );
		assertNotNull ( "Ops! c1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.c1.getClass () ).getById ( pip.c1.getId () ) );
		assertNotNull ( "Ops! c2 not saved!", pip.c2.getId () );
		assertNotNull ( "Ops! c2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.c2.getClass () ).getById ( pip.c2.getId () ) );
		assertNotNull ( "Ops! fact1 not saved!", pip.fact1.getId () );
		assertNotNull ( "Ops! fact1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fact1.getClass () ).getById ( pip.fact1.getId () ) );
		assertNotNull ( "Ops! fact2 not saved!", pip.fact2.getId () );
		assertNotNull ( "Ops! fact2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fact2.getClass () ).getById ( pip.fact2.getId () ) );
		
		assertNotNull ( "Ops! cv2UnitVal not saved!", pip.cv2UnitVal.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv2UnitVal.getClass () ).getById ( pip.cv2UnitVal.getId () ) );
		assertNotNull ( "Ops! cv2Unit not saved!", pip.cv2Unit.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv2Unit.getClass () ).getById ( pip.cv2Unit.getId () ) );
		assertNotNull ( "Ops! paramv1 not saved!", pip.paramv1.getId () );
		assertNotNull ( "Ops! paramv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.paramv1.getClass () ).getById ( pip.paramv1.getId () ) );
		assertNotNull ( "Ops! paramv2 not saved!", pip.paramv2.getId () );
		assertNotNull ( "Ops! paramv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.paramv2.getClass () ).getById ( pip.paramv2.getId () ) );
		assertNotNull ( "Ops! param1 not saved!", pip.param1.getId () );
		assertNotNull ( "Ops! param1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.param1.getClass () ).getById ( pip.param1.getId () ) );
		assertNotNull ( "Ops! param2 not saved!", pip.param2.getId () );
		assertNotNull ( "Ops! param2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.param2.getClass () ).getById ( pip.param2.getId () ) );
		assertNotNull ( "Ops! paramv2UnitVal not saved!", pip.paramv2UnitVal.getId () );
		assertNotNull ( "Ops! paramv2UnitVal not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.paramv2UnitVal.getClass () ).getById ( pip.paramv2UnitVal.getId () ) );
		assertNotNull ( "Ops! paramv2Unit not saved!", pip.paramv2Unit.getId () );
		assertNotNull ( "Ops! paramv2Unit not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.paramv2Unit.getClass () ).getById ( pip.paramv2Unit.getId () ) );
		
		assertNotNull ( "Ops! proto1 not saved!", adao.getByAcc ( pip.proto1.getAcc () ) );
		assertNotNull ( "Ops! proto2 not saved!", adao.getByAcc ( pip.proto2.getAcc () ) );
		
		ReferenceSource fooOntoDB = (ReferenceSource) adao.getByAcc ( pip.fooOnto.getAcc () );
		assertNotNull ( "Ops! fooOnto not saved!", fooOntoDB );
		assertEquals ( "Ops! fooOnto #ID is wrong!", -1L,  fooOntoDB.getId ()  );
		assertNotNull ( "Ops! srcRole not saved!", oedao.getByAcc ( pip.srcRole.getAcc () ) );
		assertNotNull ( "Ops! assayRole not saved!", oedao.getByAcc ( pip.assayRole.getAcc () ) );
		assertNotNull ( "Ops! dataType not saved!", oedao.getByAcc ( pip.dataType.getAcc () ) );
		OntologyEntry c1OtDB = oedao.getByAcc ( pip.c1Ot.getAcc () );
		assertNotNull ( "Ops! fooOnto not saved!", c1OtDB );
		assertEquals ( "Ops! fooOnto #ID is wrong!", -1L,  c1OtDB.getId ()  );
		
		pip.nas1 = (MaterialNode) adao.getByAcc ( pip.nas1.getAcc () );
		pip.p1 = (MaterialProcessing) adao.getByAcc ( pip.p1.getAcc () );
		pip.p2 = (DataAcquisition) adao.getByAcc ( pip.p2.getAcc () );
		assertTrue ( "Urp! The p1->nas1 link seems not saved!", pip.nas1.getDownstreamProcessings ().contains ( pip.p1 ) );
		assertTrue ( "Urp! The nas1->p2 link seems not saved!", pip.nas1.getUpstreamProcessings ().contains ( pip.p2 ) );
		
		pip.papp1 = (ProtocolApplication) adao.getByAcc ( pip.papp1.getAcc () );
		pip.proto1 = (Protocol) adao.getByAcc ( pip.proto1.getAcc () );
		assertEquals ( "Urp! papp1->proto1 link seems not saved", pip.papp1.getProtocol (), pip.proto1 );
	}	

	@Test
	public void testFullStudy ()
	{
		FullStudyPipelineModel pip = new FullStudyPipelineModel ();
		
		Timestamp ts = new Timestamp ( System.currentTimeMillis () );
		StudyPersister studyPersister = new StudyPersister ( daoFactory, ts );
		studyPersister.persist ( pip.study );
		commitTansaction ();
		session.flush ();
		
		AccessibleDAO<Accessible> adao = daoFactory.getAccessibleDAO ( Accessible.class );
		OntologyEntryDAO<OntologyEntry> oedao = daoFactory.getOntologyEntryDAO ();
		
		assertNotNull ( "Ops! nsrc1 not saved!", adao.getByAcc ( pip.nsrc1.getAcc () ) );
		assertNotNull ( "Ops! nsrc2 not saved!", adao.getByAcc ( pip.nsrc2.getAcc () ) );
		assertNotNull ( "Ops! p1 not saved!", adao.getByAcc ( pip.p1.getAcc () ) );
		assertNotNull ( "Ops! nas1 not saved!", adao.getByAcc ( pip.nas1.getAcc () ) );
		assertNotNull ( "Ops! p2 not saved!", adao.getByAcc ( pip.p2.getAcc () ) );
		assertNotNull ( "Ops! nar1 not saved!", adao.getByAcc ( pip.nar1.getAcc () ) );
		assertNotNull ( "Ops! src1 not saved!", adao.getByAcc ( pip.src1.getAcc () ) );
		assertNotNull ( "Ops! src2 not saved!", adao.getByAcc ( pip.src2.getAcc () ) );
		assertNotNull ( "Ops! assayMaterial1 not saved!", adao.getByAcc ( pip.assayMaterial1.getAcc () ) );

		assertNotNull ( "Ops! dt1 not saved!", adao.getByAcc ( pip.dt1.getAcc () ) );
		assertNotNull ( "Ops! dt2 not saved!", adao.getByAcc ( pip.dt2.getAcc () ) );
		assertNotNull ( "Ops! cv1 not saved!", pip.cv1.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv1.getClass () ).getById ( pip.cv1.getId () ) );
		assertNotNull ( "Ops! cv2 not saved!", pip.cv2.getId () );
		assertNotNull ( "Ops! cv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv2.getClass () ).getById ( pip.cv2.getId () ) );
		assertNotNull ( "Ops! fv1 not saved!", pip.fv1.getId () );
		assertNotNull ( "Ops! fv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fv1.getClass () ).getById ( pip.fv1.getId () ) );
		assertNotNull ( "Ops! fv2 not saved!", pip.fv2.getId () );
		assertNotNull ( "Ops! fv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fv2.getClass () ).getById ( pip.fv2.getId () ) );
		
		assertNotNull ( "Ops! c1 not saved!", pip.c1.getId () );
		assertNotNull ( "Ops! c1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.c1.getClass () ).getById ( pip.c1.getId () ) );
		assertNotNull ( "Ops! c2 not saved!", pip.c2.getId () );
		assertNotNull ( "Ops! c2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.c2.getClass () ).getById ( pip.c2.getId () ) );
		assertNotNull ( "Ops! fact1 not saved!", pip.fact1.getId () );
		assertNotNull ( "Ops! fact1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fact1.getClass () ).getById ( pip.fact1.getId () ) );
		assertNotNull ( "Ops! fact2 not saved!", pip.fact2.getId () );
		assertNotNull ( "Ops! fact2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fact2.getClass () ).getById ( pip.fact2.getId () ) );
		
		assertNotNull ( "Ops! cv2UnitVal not saved!", pip.cv2UnitVal.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv2UnitVal.getClass () ).getById ( pip.cv2UnitVal.getId () ) );
		assertNotNull ( "Ops! cv2Unit not saved!", pip.cv2Unit.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv2Unit.getClass () ).getById ( pip.cv2Unit.getId () ) );

		assertNotNull ( "Ops! paramv1 not saved!", pip.paramv1.getId () );
		assertNotNull ( "Ops! paramv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.paramv1.getClass () ).getById ( pip.paramv1.getId () ) );
		assertNotNull ( "Ops! paramv2 not saved!", pip.paramv2.getId () );
		assertNotNull ( "Ops! paramv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.paramv2.getClass () ).getById ( pip.paramv2.getId () ) );
		assertNotNull ( "Ops! param1 not saved!", pip.param1.getId () );
		assertNotNull ( "Ops! param1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.param1.getClass () ).getById ( pip.param1.getId () ) );
		assertNotNull ( "Ops! param2 not saved!", pip.param2.getId () );
		assertNotNull ( "Ops! param2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.param2.getClass () ).getById ( pip.param2.getId () ) );
		assertNotNull ( "Ops! paramv2UnitVal not saved!", pip.paramv2UnitVal.getId () );
		assertNotNull ( "Ops! paramv2UnitVal not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.paramv2UnitVal.getClass () ).getById ( pip.paramv2UnitVal.getId () ) );
		assertNotNull ( "Ops! paramv2Unit not saved!", pip.paramv2Unit.getId () );
		assertNotNull ( "Ops! paramv2Unit not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.paramv2Unit.getClass () ).getById ( pip.paramv2Unit.getId () ) );
		
		assertNotNull ( "Ops! proto1 not saved!", adao.getByAcc ( pip.proto1.getAcc () ) );
		assertNotNull ( "Ops! proto2 not saved!", adao.getByAcc ( pip.proto2.getAcc () ) );
		
		ReferenceSource fooOntoDB = (ReferenceSource) adao.getByAcc ( pip.fooOnto.getAcc () );
		assertNotNull ( "Ops! fooOnto not saved!", fooOntoDB );
		assertEquals ( "Ops! fooOnto #ID is wrong!", -1L,  fooOntoDB.getId ()  );
		assertNotNull ( "Ops! srcRole not saved!", oedao.getByAcc ( pip.srcRole.getAcc () ) );
		assertNotNull ( "Ops! assayRole not saved!", oedao.getByAcc ( pip.assayRole.getAcc () ) );
		assertNotNull ( "Ops! dataType not saved!", oedao.getByAcc ( pip.dataType.getAcc () ) );
		OntologyEntry c1OtDB = (OntologyEntry) oedao.getByAcc ( pip.c1Ot.getAcc () );
		assertNotNull ( "Ops! fooOnto not saved!", c1OtDB );
		assertEquals ( "Ops! fooOnto #ID is wrong!", -1L,  c1OtDB.getId ()  );
		
		assertNotNull ( "Ops! as1 not saved!", adao.getByAcc ( pip.as1.getAcc () ) );
		assertNotNull ( "Ops! ar1 not saved!", pip.ar1.getId () );
		assertNotNull ( "Ops! ar1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.ar1.getClass () ).getById ( pip.ar1.getId () ) );
		assertNotNull ( "Ops! ar2 not saved!", pip.ar2.getId () );
		assertNotNull ( "Ops! ar2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.ar2.getClass () ).getById ( pip.ar2.getId () ) );
		
		for ( PropertyValue<?> pv: pip.ar1.getCascadedPropertyValues () ) {
			assertNotNull ( "Ops! pv not saved!", pv.getId () );
			assertNotNull ( "Ops! pv not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pv.getClass () ).getById ( pv.getId () ) );
		}

		for ( PropertyValue<?> pv: pip.ar2.getCascadedPropertyValues () ) {
			assertNotNull ( "Ops! pv not saved!", pv.getId () );
			assertNotNull ( "Ops! pv not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pv.getClass () ).getById ( pv.getId () ) );
		}
		
		assertTrue ( "Ops! ar1 does not contain fv1", pip.ar1.getCascadedPropertyValues ().contains ( pip.fv1 ) );
		assertTrue ( "Ops! ar1 does not contain fv2", pip.ar1.getCascadedPropertyValues ().contains ( pip.fv2 ) );
		
		pip.nar1 = (DataNode) adao.getByAcc ( pip.nar1.getAcc () );
		pip.dt1 = (Data) adao.getByAcc ( pip.dt1.getAcc () );
		assertEquals ( "Urp! The nar1->dt1 link seems not saved!", pip.dt1, pip.nar1.getData () );
		assertEquals ( "Urp! The dt1->nar1 link seems not saved!", pip.nar1, pip.dt1.getProcessingNode () );
		
		pip.ar1 = daoFactory.getIdentifiableDAO ( AssayResult.class ).getById ( pip.ar1.getId () );
		assertEquals ( "Urp! The ar1->dt1 link seems not saved!", pip.dt1, pip.ar1.getData () );

		pip.as1 = (Assay) adao.getByAcc ( pip.as1.getAcc () );
		pip.assayMaterial1 = (Material) adao.getByAcc ( pip.assayMaterial1.getAcc () );
		assertEquals ( "Urp! The as1->assayMaterial1 link seems not saved!", pip.assayMaterial1, pip.as1.getMaterial () );
		
	}	
	
	
	/**
	 * TODO: Comment me!
	 */
	@Test
	public void testLightPersistence ()
	{
		System.setProperty ( Persister.LIGHT_PERSISTENCE_PROPERTY, "true" );
		
		FullStudyPipelineModel pip = new FullStudyPipelineModel ();
		
		Timestamp ts = new Timestamp ( System.currentTimeMillis () );
		StudyPersister studyPersister = new StudyPersister ( daoFactory, ts );
		studyPersister.persist ( pip.study );
		commitTansaction ();
		session.flush ();
		
		AccessibleDAO<Accessible> adao = daoFactory.getAccessibleDAO ( Accessible.class );
		OntologyEntryDAO<OntologyEntry> oedao = daoFactory.getOntologyEntryDAO ();
		
		assertNotNull ( "Ops! cv1 not saved!", pip.cv1.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv1.getClass () ).getById ( pip.cv1.getId () ) );
		assertNotNull ( "Ops! cv2 not saved!", pip.cv2.getId () );
		assertNotNull ( "Ops! cv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv2.getClass () ).getById ( pip.cv2.getId () ) );
		assertNotNull ( "Ops! fv1 not saved!", pip.fv1.getId () );
		assertNotNull ( "Ops! fv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fv1.getClass () ).getById ( pip.fv1.getId () ) );
		assertNotNull ( "Ops! fv2 not saved!", pip.fv2.getId () );
		assertNotNull ( "Ops! fv2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fv2.getClass () ).getById ( pip.fv2.getId () ) );
		
		assertNotNull ( "Ops! c1 not saved!", pip.c1.getId () );
		assertNotNull ( "Ops! c1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.c1.getClass () ).getById ( pip.c1.getId () ) );
		assertNotNull ( "Ops! c2 not saved!", pip.c2.getId () );
		assertNotNull ( "Ops! c2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.c2.getClass () ).getById ( pip.c2.getId () ) );
		assertNotNull ( "Ops! fact1 not saved!", pip.fact1.getId () );
		assertNotNull ( "Ops! fact1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fact1.getClass () ).getById ( pip.fact1.getId () ) );
		assertNotNull ( "Ops! fact2 not saved!", pip.fact2.getId () );
		assertNotNull ( "Ops! fact2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.fact2.getClass () ).getById ( pip.fact2.getId () ) );
		
		assertNotNull ( "Ops! cv2UnitVal not saved!", pip.cv2UnitVal.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv2UnitVal.getClass () ).getById ( pip.cv2UnitVal.getId () ) );
		assertNotNull ( "Ops! cv2Unit not saved!", pip.cv2Unit.getId () );
		assertNotNull ( "Ops! cv1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.cv2Unit.getClass () ).getById ( pip.cv2Unit.getId () ) );
		
		assertNotNull ( "Ops! proto1 not saved!", adao.getByAcc ( pip.proto1.getAcc () ) );
		assertNotNull ( "Ops! proto2 not saved!", adao.getByAcc ( pip.proto2.getAcc () ) );
		
		ReferenceSource fooOntoDB = (ReferenceSource) adao.getByAcc ( pip.fooOnto.getAcc () );
		assertNotNull ( "Ops! fooOnto not saved!", fooOntoDB );
		assertEquals ( "Ops! fooOnto #ID is wrong!", -1L,  fooOntoDB.getId ()  );
		OntologyEntry c1OtDB = (OntologyEntry) oedao.getByAcc ( pip.c1Ot.getAcc () );
		assertNotNull ( "Ops! fooOnto not saved!", c1OtDB );
		assertEquals ( "Ops! fooOnto #ID is wrong!", -1L,  c1OtDB.getId ()  );
		
		assertNotNull ( "Ops! as1 not saved!", adao.getByAcc ( pip.as1.getAcc () ) );
		assertNotNull ( "Ops! ar1 not saved!", pip.ar1.getId () );
		assertNotNull ( "Ops! ar1 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.ar1.getClass () ).getById ( pip.ar1.getId () ) );
		assertNotNull ( "Ops! ar2 not saved!", pip.ar2.getId () );
		assertNotNull ( "Ops! ar2 not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pip.ar2.getClass () ).getById ( pip.ar2.getId () ) );
		
		for ( PropertyValue<?> pv: pip.ar1.getCascadedPropertyValues () ) {
			assertNotNull ( "Ops! pv not saved!", pv.getId () );
			assertNotNull ( "Ops! pv not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pv.getClass () ).getById ( pv.getId () ) );
		}

		for ( PropertyValue<?> pv: pip.ar2.getCascadedPropertyValues () ) {
			assertNotNull ( "Ops! pv not saved!", pv.getId () );
			assertNotNull ( "Ops! pv not saved (retrieval test)!", daoFactory.getIdentifiableDAO ( pv.getClass () ).getById ( pv.getId () ) );
		}
		
		assertTrue ( "Ops! ar1 does not contain fv1", pip.ar1.getCascadedPropertyValues ().contains ( pip.fv1 ) );
		assertTrue ( "Ops! ar1 does not contain fv2", pip.ar1.getCascadedPropertyValues ().contains ( pip.fv2 ) );
	}

}
