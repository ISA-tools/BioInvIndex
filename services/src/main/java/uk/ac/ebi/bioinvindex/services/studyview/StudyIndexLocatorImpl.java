package uk.ac.ebi.bioinvindex.services.studyview;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import uk.ac.ebi.bioinvindex.services.browse.BrowseStudyBean;
import uk.ac.ebi.bioinvindex.services.browse.BrowseStudyBeanProvider;


@Name("viewStudyBeanModel")
@AutoCreate
public class StudyIndexLocatorImpl implements StudyIndexLocator {

    private static final Log log = LogFactory.getLog(StudyIndexLocatorImpl.class);
    @In (create = true)
    private BrowseStudyBeanProvider studyBeanProvider;

    private BrowseStudyBean study;

    private String accession;


    public BrowseStudyBean getStudyBean() {

        try {
            if (study == null) {

                study = getStudyBeanProvider().getStudy(accession);

                log.info("I have " + study.getFactorNames().size() + " factors!");
                log.info("I have " + study.getCharacteristicNames().size() + " characteristics!");
            }

            return study;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception thrown...oh no! -> " + e.getMessage());
            return null;
        }
    }

    public BrowseStudyBeanProvider getStudyBeanProvider() {
        return studyBeanProvider;
    }

    public void setStudyBeanProvider(BrowseStudyBeanProvider studyBeanProvider) {
        this.studyBeanProvider = studyBeanProvider;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        study = null;
        this.accession = accession;
    }


}
