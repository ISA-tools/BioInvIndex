package uk.ac.ebi.bioinvindex.persistence;

import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.BioEntity;

import java.sql.Timestamp;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 06/02/2012
 *         Time: 11:30
 */
public class BioEntityPersister extends Persister<BioEntity>{

    public BioEntityPersister(DaoFactory daoFactory, Timestamp submissionTs) {
        super(daoFactory, submissionTs);
    }

    @Override
    protected void preProcess(BioEntity object) {
        super.preProcess(object);
    }

    @Override
    protected void postProcess(BioEntity object) {
        super.postProcess(object);
    }
}
