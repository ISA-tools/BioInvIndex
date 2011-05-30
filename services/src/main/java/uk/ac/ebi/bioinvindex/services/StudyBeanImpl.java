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
import org.richfaces.model.VisualStackingTreeModel;
import uk.ac.ebi.bioinvindex.dao.BIIDAOException;
import uk.ac.ebi.bioinvindex.dao.StudyDAO;
import uk.ac.ebi.bioinvindex.mibbi.MIProject;
import uk.ac.ebi.bioinvindex.model.*;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.term.*;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.model.xref.ResourceType;
import uk.ac.ebi.bioinvindex.model.xref.Xref;
import uk.ac.ebi.bioinvindex.services.cache.BIICache;
import uk.ac.ebi.bioinvindex.services.cache.Cache;
import uk.ac.ebi.bioinvindex.services.ontologyhandling.Ontology;
import uk.ac.ebi.bioinvindex.services.utils.CommonActions;
import uk.ac.ebi.bioinvindex.services.utils.StringFormating;

import java.util.*;

import static org.jboss.seam.ScopeType.PAGE;


@Name("studyBean")
@Scope(PAGE)
public class StudyBeanImpl implements StudyBean {

    private static final Log log = LogFactory.getLog(StudyBeanImpl.class);
    private static final Cache<String, List<String>> cache = new BIICache<String, List<String>>();

    private Study study;

    @In
    private StudyDAO studyEJB3DAO;

    @In
    private SourceURLResolver sourceURLResolver;

    @In
    private Identity identity;

    private String organism;

    private String design;

    private Map<String, List<Ontology>> factorsToValues;

    private Map<String, List<Ontology>> characteristicsToValues;

    private String contacts;

    private List<AssayGroupInfo> assayInfos;

    private String studyId;

    private Collection<Investigation> investigations;

    private List<String> relatedStudies;

    // todo store study items in the BII cache...

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
            this.study = studyEJB3DAO.getByAccForUser(studyId, identity.getUsername());
            log.info("Study set in bean to " + study.getAcc());
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

    public boolean hasFactors() {

        if (factorsToValues == null) {
            buildFactorsMap();
        }
        return factorsToValues.size() > 0;
    }

    public List<String> getFactors() {
        log.info("StudyBeanImpl.getFactors");
        log.info("Getting factors for " + study.getAcc());

        List<String> factors;
        if ((factors = cache.find(studyId + "/factors")) == null) {

            log.info("Nothing was contained in the cache for " + studyId + "/factors. Now building factor map");

            if (factorsToValues == null) {
                buildFactorsMap();
            }

            factors = new ArrayList<String>(factorsToValues.keySet());
            cache.attach(studyId + "/factors", factors);
        }
        return factors;
    }

    public List<Ontology> getFactorValues(String factorName) {
        if (factorsToValues == null) {
            buildFactorsMap();
        }
        return factorsToValues.get(factorName);
    }

    public List<String> getCharacteristics() {
        log.info("StudyBeanImpl.getCharacteristics");
        log.info("Getting characteristics for " + study.getAcc());
        List<String> characteristics;

        if ((characteristics = cache.find(studyId + "/characteristics")) == null) {
            log.info("Nothing was contained in the cache for " + studyId + "/characteristics. Now building characteristic map");

            if (characteristicsToValues == null) {
                buildCharacteristicsMap();
            }
            characteristics = new ArrayList<String>(characteristicsToValues.keySet());
            cache.attach(studyId + "/characteristics", characteristics);
        }
        return characteristics;
    }

    public List<Ontology> getCharacteristicValues(String characteristic) {

        if (characteristicsToValues == null) {
            buildCharacteristicsMap();
        }
        return characteristicsToValues.get(characteristic);
    }

    public String getObfuscatedAccession() {
        return study.getAcc() + "_" + study.getObfuscationCode();
    }

    public String getContacts() {
        log.info("Getting contacts for " + study.getAcc());

        if (contacts == null) {
            StringBuilder sb = new StringBuilder();
            for (Contact contact : study.getContacts()) {
                sb.append(contact.getFullName());
                sb.append(", ");
            }
            contacts = StringFormating.removeLastComma(sb.toString());
        }
        log.info("StudyBeanImpl.getContacts " + contacts);
        return contacts;
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


    public List<AssayGroupInfo> getAssayInfos() {

        log.info("StudyBeanImpl.getAssayInfos " + study.getAcc());
        if (assayInfos == null) {
            HashMap<String, AssayGroupInfo> groups = new HashMap<String, AssayGroupInfo>();

            for (Assay assay : study.getAssays()) {

                String key = assay.getMeasurement().getName() + assay.getTechnologyName() + assay.getAssayPlatform();

                AssayGroupInfo bean;

                if (groups.containsKey(key)) {
                    bean = groups.get(key);
                } else {
                    bean = new AssayGroupInfo();
                    bean.setEndPoint(assay.getMeasurement().getName());
                    bean.setTechnology(assay.getTechnologyName());
                    bean.setPlatform(assay.getAssayPlatform());

                    groups.put(key, bean);
                }

                if (assay.getXrefs().size() > 0) {
                    DataLink dataLink = new DataLink();

                    Collection<Xref> xrefs = assay.getXrefs();

                    for (Xref xref : xrefs) {
                        ReferenceSource source = xref.getSource();
                        if (source.getAcc().indexOf(ResourceType.RAW.getName()) > -1) {
                            dataLink.addDataOfType(ResourceType.RAW);
                        } else if (source.getAcc().indexOf(ResourceType.PROCESSED.getName()) > -1) {
                            dataLink.addDataOfType(ResourceType.PROCESSED);
                        } else if (source.getAcc().indexOf(ResourceType.ENTRY.getName()) > -1) {
                            dataLink.addDataOfType(ResourceType.ENTRY);
                        }

                        dataLink.setSourceName(source.getAcc());
                        dataLink.setAcc(xref.getAcc());
                    }

                    bean.addDataLink(dataLink);
                }
            }
            assayInfos = new ArrayList<AssayGroupInfo>(groups.values());
        }


        return assayInfos;
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
            sb.append(term.getValue());
            sb.append(", ");
        }

        return StringFormating.removeLastComma(sb.toString());
    }

    private void buildFactorsMap() {
        factorsToValues = new HashMap<String, List<Ontology>>();

        Map<String, Set<PropertyValue>> factorsToValueSet = new HashMap<String, Set<PropertyValue>>();

        List<Property<FactorValue>> factors = studyEJB3DAO.getFactorsForStudy(study.getId());

        for (Property<FactorValue> factor : factors) {

            Set<PropertyValue> values = factorsToValueSet.get(factor.getValue());
            if (values == null) {
                values = new HashSet<PropertyValue>();
                factorsToValueSet.put(factor.getValue().toLowerCase(), values);
            }
            for (FactorValue factorValue : factor.getPropertyValues()) {

                values.add(factorValue);
            }
        }

        for (String factorName : factorsToValueSet.keySet()) {
            Set<PropertyValue> values = factorsToValueSet.get(factorName);
            factorsToValues.put(factorName, buildOntologyTermsFromValues(values));
        }
    }

    private List<Ontology> buildOntologyTermsFromValues(Set<PropertyValue> values) {

        List<Ontology> ontologies = new ArrayList<Ontology>();

//		OntologyResolver ontologyURLResolver = new OntologyResolver();

        for (PropertyValue value : values) {
            Ontology ontologyTerm;
            if (value.getSingleOntologyTerm() != null) {
                ontologyTerm = new Ontology(value.getSingleOntologyTerm().getAcc(),
                        value.getSingleOntologyTerm().getSource().getName(), value.getValue());
            } else {
                ontologyTerm = new Ontology("", "", value.getValue());
            }

            if (!ontologies.contains(ontologyTerm)) {
//				if (!StringFormating.isEmpty(ontologyTerm.getSource())) {
//					ontologyURLResolver.resolveOntology(ontologyTerm);
//				}
                ontologies.add(ontologyTerm);
            }

            if (value.getUnit() != null) {
                Ontology ontologyUnit;
                if (value.getUnit().getSingleOntologyTerm() != null) {
                    ontologyUnit = new Ontology(value.getUnit().getSingleOntologyTerm().getAcc(),
                            value.getUnit().getSingleOntologyTerm().getSource().getName(),
                            value.getUnit().getValue());
                } else {
                    ontologyUnit = new Ontology("", "", value.getUnit().getValue());
                }

                if (!ontologies.contains(ontologyUnit)) {
//					if (!StringFormating.isEmpty(ontologyUnit.getSource())) {
//						ontologyURLResolver.resolveOntology(ontologyUnit);
//					}
                    ontologies.add(ontologyUnit);
                }
            }
        }
        return ontologies;
    }

    private void buildCharacteristicsMap() {
        this.characteristicsToValues = new HashMap<String, List<Ontology>>();
        Map<String, Set<PropertyValue>> characteristicsToValues = new HashMap<String, Set<PropertyValue>>();

        List<Property<CharacteristicValue>> studyCharacteristicValues = studyEJB3DAO.getCharacteristicsForStudy(study.getId());

        for (Property<CharacteristicValue> characteristicValueProperty : studyCharacteristicValues) {

            Set<PropertyValue> values = characteristicsToValues.get(characteristicValueProperty.getValue());

            if (values == null) {
                values = new HashSet<PropertyValue>();
                characteristicsToValues.put(
                        characteristicValueProperty.getValue().toLowerCase(),
                        values);
            }

            for (CharacteristicValue characteristicValue : characteristicValueProperty.getPropertyValues()) {
                values.add(characteristicValue);
            }
        }

        for (String characteristic : characteristicsToValues.keySet()) {
            Set<PropertyValue> values = characteristicsToValues.get(characteristic);

            this.characteristicsToValues.put(characteristic, buildOntologyTermsFromValues(values));
        }
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

    public boolean clearCache() {
        log.info("******");
        log.info("Cleaning up StudyBeanImpl cache");
        cache.clearCache();

        return true;
    }
}

