package uk.ac.ebi.bioinvindex.persistence;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.BioEntity;
import uk.ac.ebi.bioinvindex.model.Data;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.DataNode;
import uk.ac.ebi.bioinvindex.model.term.DataType;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.persistence.pipeline.DataNodePersister;
import uk.ac.ebi.bioinvindex.persistence.pipeline.DataPersister;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.System.out;
import static org.junit.Assert.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 02/02/2012
 *         Time: 16:24
 */
public class BioEntityPersistenceTest extends TransactionalDBUnitEJB3DAOTest {

    private StudyPersister persister;

    public BioEntityPersistenceTest() throws Exception {
        super();
//        persister = new StudyPersister ( DaoFactory.getInstance ( entityManager ), new Timestamp ( System.currentTimeMillis () ) );
    }

    @Before
    public void initPersister() {
        persister = new StudyPersister(DaoFactory.getInstance(entityManager), new Timestamp(System.currentTimeMillis()));
    }

    protected void prepareSettings() {
        beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
        dataSetLocation = "test_BioEntityPersistence.xml";
    }

    @Test
    public void testPersistMetabolites() throws Exception {

        out.println("\n\n\n _______________  testPersistMetabolites, ____________________________\n");

        // persist study
        Study study = new Study("Testing MetaboLights Entities");
        study.setAcc("test-bioentity");
        Study studyDB = persister.persist(study);

        System.out.println("STUDY PERSISTED");

        // persist our reference source
        ReferenceSource referenceSource = new ReferenceSource("blah:acc", "myData");
        ReferenceSourcePersister referenceSourcePersister = new ReferenceSourcePersister(DaoFactory.getInstance(entityManager), new Timestamp(System.currentTimeMillis()));
        referenceSourcePersister.persist(referenceSource);

        System.out.println("REFERENCE SOURCE PERSISTED");

        // persist our data
        DataType dataType = new DataType("123321", "name", referenceSource);
        OntologyEntryPersister<DataType> ontologyEntryPersister = new OntologyEntryPersister<DataType>(DaoFactory.getInstance(entityManager), new Timestamp(System.currentTimeMillis())) {};
        ontologyEntryPersister.save(dataType);
        System.out.println("DATA TYPE SAVED");

        Data data = new Data("http://www.ebi.ac.uk/bioinvindex", dataType);
        data.setAcc("myAcc");
        DataPersister dataPersister = new DataPersister(DaoFactory.getInstance(entityManager), new Timestamp(System.currentTimeMillis()));
        dataPersister.save(data);

        System.out.println("DATA SAVED");

        // persist our processing node
        DataNode node = new DataNode(study, true);
        node.setAcc("mynode-acc");
        node.setData(data);
        DataNodePersister dataNodePersister = new DataNodePersister(DaoFactory.getInstance(entityManager), new Timestamp(System.currentTimeMillis()));
        dataNodePersister.persist(node);

        System.out.println("DATA NODE PERSISTED");

        data.setProcessingNode(node);

        dataPersister.persist(data);

        System.out.println("DATA PERSISTED");

        // persist our data
        AssayResult result = new AssayResult(null, study, new ArrayList<BioEntity>());
        AssayResultPersister assayResultPersister = new AssayResultPersister(DaoFactory.getInstance(entityManager), new Timestamp(System.currentTimeMillis()));

        BioEntity bioEntity1 = new BioEntity("water", "CHEBI:15377");
        result.addBioEntity(bioEntity1);

        study.addAssayResult(result);

        assayResultPersister.persist(result);
        System.out.println("ASSAY RESULT PERSISTED");
        // Let's go with the persistence job!

        transaction.commit();

        assertNotNull("Oh! No study returned by the persister!", studyDB);
        assertEquals("Ouch! The study rerturned by the persister should be the original one!", study, studyDB);
        assertTrue("Argh! Cannot find the persisted study in the DB!", entityManager.contains(studyDB));
        assertNotNull("Urp! The study should have an ID", studyDB.getId());

        // Test the bioentity
        Collection<AssayResult> assayResults = studyDB.getAssayResults();
        assertNotNull("Ops! The retuned study has no an assayGroup!", assayResults);
        assertEquals("Oh no! Bad no. of assayGroups persisted!", 1, assayResults.size());
        for (AssayResult assayResultOfInterest : assayResults) {
            assertEquals("Oh no! Bad no. of assayGroups persisted!", 1, assayResultOfInterest.getBioEntities().size());
        }

        out.println("\n\nPersisted study: " + studyDB);

        out.println("\n\n\n _______________  /end: testPersistMetabolites, Testing new Study  ____________________________\n");
    }


}
