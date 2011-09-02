package uk.ac.ebi.bioinvindex.services;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.*;
import org.jboss.seam.security.Identity;
import uk.ac.ebi.bioinvindex.dao.BIIDAOException;
import uk.ac.ebi.bioinvindex.dao.StudyDAO;
import uk.ac.ebi.bioinvindex.mibbi.MIProject;
import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.term.FreeTextTerm;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;
import uk.ac.ebi.bioinvindex.services.cache.BIICache;
import uk.ac.ebi.bioinvindex.services.cache.Cache;
import uk.ac.ebi.bioinvindex.services.studyview.StudyIndexLocatorImpl;
import uk.ac.ebi.bioinvindex.services.utils.CommonActions;
import uk.ac.ebi.bioinvindex.services.utils.StringFormating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.jboss.seam.ScopeType.PAGE;


@Name("studyBean")
@Scope(PAGE)
public class StudyBeanImpl implements StudyBean {

    private static final Log log = LogFactory.getLog(StudyBeanImpl.class);
    private static final Cache<String, Map<String, List<String>>> characteristicFactorCache
            = new BIICache<String, Map<String, List<String>>>();

    private static final Cache<String, Study> studyObjectCache = new BIICache<String, Study>();

    private Study study;

    @In
    private StudyDAO studyEJB3DAO;

    @In
    private SourceURLResolver sourceURLResolver;

    @In
    private Identity identity;

    @In(required = false)
    private StudyIndexLocatorImpl studyIndexLocator;


    private String organism;

    private String design;

    private Map<String, List<String>> factorsToValues;

    private Map<String, List<String>> characteristicsToValues;

    private List<Contact> contacts;

    private List<AssayGroupInfo> assayInfos;

    private String studyId;

    private Collection<Investigation> investigations;

    private List<String> relatedStudies;

    public StudyBeanImpl() {
    }

    public StudyBeanImpl(Study study) {
        log.info("StudyBeanImpl.StudyBeanImpl");
        this.study = study;
    }

    public String getStudyId() {
        return studyId;
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void setStudyId(String studyId) {
        this.studyId = studyId;

        try {
            if ((study = studyObjectCache.find(studyId)) == null) {
                this.study = studyEJB3DAO.getByAccForUser(studyId, identity.getUsername());

                studyObjectCache.attach(studyId, study);

                log.info("Study set in bean to " + study.getAcc());
            }

        } catch (BIIDAOException e) {
            throw new BIIException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BIIException("Study " + studyId + " cannot be fetched.");
        }
    }

    public Study getStudy() {
        return study;
    }

    public String getOrganism() {
        if (organism == null) {

            List<PropertyValue> values = studyEJB3DAO.getValuesOfPropertyForStudyId(study.getId(), "Organism");
            organism = CommonActions.buildValuesString(values);
        }
        log.info("StudyBeanImpl.getOrganism " + organism);
        return organism;
    }

    public String getDesign() {
        if (design == null) {
            design = buildStringFromFreeTextTerms(study.getDesigns());
        }
        log.info("StudyBeanImpl.getDesign " + design);
        return design;
    }

    public String getObfuscatedAccession() {
        return study.getAcc() + "_" + study.getObfuscationCode();
    }

    public List<Contact> getContacts() {
        log.info("Getting contacts for " + study.getAcc());

        return (List<Contact>)study.getContacts();
    }

    public DBLink getPubmedLink(Publication publication) {
        log.info("Getting pubmed link for " + study.getAcc());
        if (publication.getPmid() != null) {
            DBLink link = new DBLink();
            link.setAcc(publication.getPmid());
            link.setUrl("http://www.ebi.ac.uk/citexplore/citationDetails.do?externalId=" + publication.getPmid() + "&dataSource=MED");
            return link;
        }
        return null;
    }

    public boolean hasPubmedId(Publication publication) {
        log.info("publication.getPmid() = " + publication.getPmid());
        return publication.getPmid() != null && !publication.getPmid().equals("");
    }

    public boolean hasMIBBILinks() {
        return study.getMiProjects().size() > 0;
    }

    public List<MIProject> getMIBBILinks() {
        log.info("StudyBeanImpl.getMIBBILinks");
        return (List<MIProject>) study.getMiProjects();
    }

    public boolean hasInvestigation() {
        log.info("Checking if study has an investigation " + study.getAcc());
        return getRelatedStudies().size() > 0;
    }


    public List<String> getRelatedStudies() {
        log.info("Getting related studies for " + study.getAcc());
        if (relatedStudies == null) {
            relatedStudies = new ArrayList<String>();
            if (study != null) {
                for (Investigation investigation : study.getInvestigations()) {
                    for (Study relatedStudy : investigation.getStudies()) {
                        if (!relatedStudy.getAcc().equals(study.getAcc())) {
                            if (!relatedStudies.contains(relatedStudy.getAcc())) {
                                if (CommonActions.canCurrentUserViewStudy(relatedStudy, identity)) {
                                    relatedStudies.add(relatedStudy.getAcc());
                                }
                            }
                        }
                    }
                }
            }
        }
        return relatedStudies;
    }


    private String buildStringFromFreeTextTerms(Collection<? extends FreeTextTerm> terms) {
        StringBuilder sb = new StringBuilder();

        for (FreeTextTerm term : terms) {
            if (term.getValue() != null && !term.getValue().equals("")) {
                sb.append(term.getValue());
                sb.append(", ");
            }
        }

        return StringFormating.removeLastComma(sb.toString());
    }

    public StudyDAO getStudyEJB3DAO() {
        if (studyEJB3DAO == null) {
            throw new IllegalStateException("StudyDAO is required but has not been set");
        }
        return studyEJB3DAO;
    }

    public void setStudyEJB3DAO(StudyDAO studyDao) {
        this.studyEJB3DAO = studyDao;
    }


    public SourceURLResolver getSourceURLResolver() {
        return sourceURLResolver;
    }

    public void setSourceURLResolver(SourceURLResolver sourceURLResolver) {
        if (sourceURLResolver == null) {
            throw new IllegalStateException("sourceURLResolver is required but has not been set");
        }
        this.sourceURLResolver = sourceURLResolver;
    }

    public String toString() {
        return "StudyBeanImpl{" +
                "study=" + study +
                '}';
    }

    public void studyExist() {
        log.info("!!!!!!!!StudyBeanImpl.studyExist " + studyId);
        if (!studyEJB3DAO.studyExists(studyId)) {

            throw new BIIException("Study with id = " + studyId + " is not available");
        }

    }

    public StudyIndexLocatorImpl getBrowseStudyBeanModel() {
        return studyIndexLocator;
    }

    public void setBrowseStudyBeanModel(StudyIndexLocatorImpl browseStudyBeanModel) {
        if (browseStudyBeanModel == null) {
            throw new IllegalStateException("sourceURLResolver is required but has not been set");
        }
        this.studyIndexLocator = browseStudyBeanModel;
    }

    public boolean clearCache() {
        log.info("******");
        log.info("Cleaning up StudyBeanImpl cache");
        characteristicFactorCache.clearCache();
        studyObjectCache.clearCache();

        return true;
    }
}