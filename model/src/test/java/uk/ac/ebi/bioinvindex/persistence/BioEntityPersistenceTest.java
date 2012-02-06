package uk.ac.ebi.bioinvindex.persistence;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.dao.ejb3.StudyEJB3DAO;
import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.BioEntity;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.utils.test.TransactionalDBUnitEJB3DAOTest;

import java.sql.Timestamp;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 02/02/2012
 *         Time: 16:24
 */
public class BioEntityPersistenceTest extends TransactionalDBUnitEJB3DAOTest {

    private StudyEJB3DAO dao;

    public BioEntityPersistenceTest() throws Exception {
        super();
    }

    @Before
    public void initPersister() {
        dao = new StudyEJB3DAO(entityManager);
    }

    protected void prepareSettings() {
        beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
        dataSetLocation = "study-data.xml";
    }

    @Test
    public void testPersistBioEntities() throws Exception {
        out.println("\n\n\n _______________  testPersistBioEntities, ____________________________\n");

        Study study = dao.getByAcc("BII-ST-1");

        BioEntity water = new BioEntity("water", "CHEBI:15377");
        BioEntity weed = new BioEntity("weed", "CHEBI:12313");

        BioEntityPersister persister = new BioEntityPersister(DaoFactory.getInstance(entityManager), new Timestamp(System.currentTimeMillis()));
        persister.persist(water);
        persister.persist(weed);

        for (AssayResult assayResult : study.getAssayResults()) {
            assayResult.addBioEntity(water);
            assayResult.addBioEntity(weed);
            entityManager.persist(assayResult);
        }

        log.info("PERSISTED Study with Bioentities");

        log.info("Running checks");

        // now check to ensure that the assay results contain bioentity objects
        study = dao.getByAcc("BII-ST-1");
        assertNotNull("Oh! No study returned by the persister!", study);
        for (AssayResult assayResult : study.getAssayResults()) {
            assertEquals("Oh damn anyway, we don't have the right number of BioEntities", 2, assayResult.getBioEntities().size());
            log.debug(assayResult.getBioEntities());
        }

        out.println("\n\n\n _______________  /end: testPersistBioEntities ____________________________\n");
    }

}
