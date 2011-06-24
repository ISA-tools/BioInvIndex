package uk.ac.ebi.bioinvindex.utils.datasourceload;

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
import uk.ac.ebi.bioinvindex.model.Annotation;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.term.AnnotationTypes;
import uk.ac.ebi.bioinvindex.model.term.AssayTechnology;
import uk.ac.ebi.bioinvindex.model.term.Measurement;
import uk.ac.ebi.bioinvindex.model.xref.AssayTypeDataLocation;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.model.xref.Xref;
import uk.ac.ebi.bioinvindex.dao.IdentifiableDAO;
import uk.ac.ebi.bioinvindex.dao.ReferenceSourceDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;

import javax.persistence.EntityManager;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: May 5, 2009
 */
public class DataLocationManager {

    private static final Log log = LogFactory.getLog(DataLocationManager.class);

    private EntityManager entityManager;

    private Collection<AssayTypeDataLocation> assayTypeDataLocations;
    private String iSATabMetaDataLocation;

    /**
     * The location for this technology/measurement pair, as defined in the data locations file.
     */
    public String getDataLocation(String measurement, String technology, AnnotationTypes dataType) {
        measurement = StringUtils.trimToNull(measurement);
        technology = StringUtils.trimToNull(technology);
        if (measurement == null && technology == null) return "";

        String location = "";

        for (AssayTypeDataLocation assayTypeDataLocation : getAssayTypeDataSources()) {

            String thisMeas = StringUtils.trimToNull(assayTypeDataLocation.getMeasurementType());
            if (thisMeas == null)
                throw new InvalidConfigurationException(
                        "Internal error: I've found a data location with null measurement type"
                );
            String thisTech = StringUtils.trimToNull(assayTypeDataLocation.getTechnologyType());

            if (thisMeas.equalsIgnoreCase(measurement)
                    && StringUtils.equalsIgnoreCase(thisTech, technology)) {
                ReferenceSource referenceSource = assayTypeDataLocation.getReferenceSource();
                if (referenceSource != null) {

                    for (Annotation annotation : referenceSource.getAnnotations()) {

                        // we will go through all the links to ensure we pick up a file location if it exists.
                        if (annotation.getType().getValue().trim().equalsIgnoreCase(dataType.getName().trim())) {
                            if (StringUtils.trimToNull(annotation.getText()) != null) {
                                location = annotation.getText();
                            }
                        }
                    }

                }
            }
        }
        return location.equals("") ? null : location;
    }

    public String getDataLocationLink(String measurement, String technology, String accession, AnnotationTypes dataType) {
        measurement = StringUtils.trimToNull(measurement);
        technology = StringUtils.trimToNull(technology);
        if (measurement == null && technology == null) return "";

        log.info("Getting data location for " + accession);

        // this should now tell me what I'm looking for
        Repository targetRepo = DataSourceUtils.resolveRepositoryFromAccession(accession);

        log.info("Target repo is " + targetRepo);

        log.info("Looking for " + dataType.getName() + " for " + measurement + " using " + technology + " for study with accession " + accession);


        String preferredLocation = "";
        String otherLocation = "";

        for (AssayTypeDataLocation assayTypeDataLocation : getAssayTypeDataSources()) {
            // i want to see that for each assay, if I can predict the link to be shown, based on what is in the DataLocations.xml file
            // and the form of the Accession. e/g. GSE = GEO, E- = ArrayExpress & SRA = ENA
            // e.g. ArrayExpress, GEO, EMBL-BANK. Generic
            log.info("AssayTypeDataLocation reference source is " + assayTypeDataLocation.getReferenceSource().getName() );

            String assayMeasurement = StringUtils.trimToNull(assayTypeDataLocation.getMeasurementType());
            if (assayMeasurement == null)
                throw new InvalidConfigurationException(
                        "Internal error: I've found a data location with null measurement type"
                );

            String assayTechnology = StringUtils.trimToNull(assayTypeDataLocation.getTechnologyType());

            if (StringUtils.equalsIgnoreCase(assayMeasurement, measurement)
                    && StringUtils.equalsIgnoreCase(assayTechnology, technology)) {

                // for now, we're just going to check what's available to me for querying/constructing the URL

                ReferenceSource referenceSource = assayTypeDataLocation.getReferenceSource();
                if (referenceSource != null) {


                    for (Annotation annotation : referenceSource.getAnnotations()) {

                        log.info(annotation.getType().getValue() + " -> has URL -> " + annotation.getText());

                        if (annotation.getType().getValue().trim().equalsIgnoreCase(dataType.getName().trim())) {

                            log.info("Found match for datatype, it is " + annotation.getText());

                            if (DataSourceUtils.matchForAssayRecord(targetRepo, assayTypeDataLocation)) {
                                log.info("Preferred location recorded" + annotation.getText());
                                preferredLocation = annotation.getText();
                            } else {
                                otherLocation = annotation.getText();
                                log.info("other location recorded" + annotation.getText());
                            }


                        }
                    }
                }
            }


        }
        log.info("other location is " + otherLocation);
        log.info("preferred location is " + otherLocation);
        // return a default URL for now
        return preferredLocation.equals("") ? otherLocation : preferredLocation;


    }


    /**
     * @return the location for the parameter pair, and replaces {@link DataSourceConfigFields#ACCESSION_PLACEHOLDER}
     *         with {@link #getObfuscatedStudyFileName(Study)}.
     */
    public String buildDataLocation(Study study, String measurement, String technology, AnnotationTypes dataType) {
        return buildLocation(getDataLocation(measurement, technology, dataType), study);
    }


    /**
     * A wrapper of {@link #getDataLocation(String, String, AnnotationTypes)} that uses getName().
     */
    public String getDataLocation(Measurement ep, AssayTechnology tech, AnnotationTypes dataType) {
        return getDataLocation(ep == null ? null : ep.getName(), tech == null ? null : tech.getName(), dataType);
    }

    /**
     * A wrapper of {@link #buildDataLocation(Study, String, String, AnnotationTypes)} that uses getName()
     */
    public String getDataLocation(Study study, Measurement ep, AssayTechnology tech, AnnotationTypes dataType) {
        return buildLocation(getDataLocation(ep == null ? null : ep.getName(), tech == null ? null : tech.getName(), dataType), study);
    }


    /**
     * The location where meta-data files are saved, as defined in the data locations file.
     */
    public String getISATabMetaDataLocation() {
        if (iSATabMetaDataLocation == null) {
            ReferenceSourceDAO sourceDAO = DaoFactory.getInstance(getEntityManager()).getReferenceSourceDAO();
            ReferenceSource referenceSource = sourceDAO.getReferenceSourceByName(ReferenceSource.ISATAB_METADATA);
            if (referenceSource != null) {
                for (Annotation annotation : referenceSource.getAnnotations()) {
                    if (annotation.getType().getValue().equals(AnnotationTypes.ISATAB_LOCATION_PATH.getName())) {
                        iSATabMetaDataLocation = annotation.getText();
                    }
                }
            }
        }
        return iSATabMetaDataLocation;
    }

    /**
     * The location where meta-data files are linked, as defined in the data locations file.
     */
    public String getISATabMetaDataWebLink() {
        if (iSATabMetaDataLocation == null) {
            ReferenceSourceDAO sourceDAO = DaoFactory.getInstance(getEntityManager()).getReferenceSourceDAO();
            ReferenceSource referenceSource = sourceDAO.getReferenceSourceByName(ReferenceSource.ISATAB_METADATA);
            if (referenceSource != null) {
                for (Annotation annotation : referenceSource.getAnnotations()) {
                    if (annotation.getType().getValue().equals(AnnotationTypes.ISATAB_LOCATION_LINK.getName())) {
                        iSATabMetaDataLocation = annotation.getText();
                    }
                }
            }
        }
        return iSATabMetaDataLocation;
    }

    /**
     * @return {@link #getISATabMetaDataLocation()} and replaces {@link DataSourceConfigFields#ACCESSION_PLACEHOLDER} with
     *         {@link #getObfuscatedStudyFileName(Study)}.
     */
    public String buildISATabMetaDataLocation(Study study) {
        return buildLocation(getISATabMetaDataLocation(), study);
    }

    /**
     * Builds the wen link about the place where the ISATAB meta-data are for this study.
     * USE ALWAYS this method for getting such URL, since it taked into account other aspects, such as the
     * obfuscation codes.
     *
     * @return {@link #getISATabMetaDataWebLink()} and replaces {@link DataSourceConfigFields#ACCESSION_PLACEHOLDER} with
     *         {@link #getObfuscatedStudyFileName(Study)}.
     */
    public String buildISATabMetaDataLink(Study study) {
        return buildLocation(getISATabMetaDataWebLink(), study);
    }

    /**
     * All the data sources defined in the data locations file.
     */
    protected Collection<AssayTypeDataLocation> getAssayTypeDataSources() {
        if (assayTypeDataLocations == null) {
            IdentifiableDAO<AssayTypeDataLocation> dao = DaoFactory.getInstance(getEntityManager()).getIdentifiableDAO(AssayTypeDataLocation.class);
            this.assayTypeDataLocations = dao.getAll();
        }
        return this.assayTypeDataLocations;
    }

    /**
     * Returns all possible file locations for the types metadata, raw, processed, generic. Replaces {@link
     * DataSourceConfigFields#ACCESSION_PLACEHOLDER}. If studyAccession is null, does not do such a replacement.
     * <p/>
     * Result includes {@link #buildISATabMetaDataLocation(Study)}.
     *
     * @return
     */
    public Collection<String> getAllDataLocations(Study study) {
        Collection<String> result = new ArrayList<String>();
        Collection<AssayTypeDataLocation> locations = getAssayTypeDataSources();
        if (locations == null) return result;

        result.add(study == null ? getISATabMetaDataLocation() : buildLocation(getISATabMetaDataLocation(), study));

        for (AssayTypeDataLocation location : locations) {
            ReferenceSource source = location.getReferenceSource();
            for (AnnotationTypes type : AnnotationTypes.DATA_PATH_ANNOTATIONS) {
                String loc = source.getSingleAnnotationValue(type.getName());
                if (loc == null) continue;
                result.add(study == null ? loc : buildLocation(loc, study));
            }
        }
        return result;
    }

    /**
     * A wrapper with study == null
     */
    public Collection<String> getAllDataLocations() {
        return getAllDataLocations(null);
    }

    /**
     * Takes a pattern about a file location and replaces the place-holder about the study accession. The end result is the
     * final path you have to use for dispatching files about the passed study.
     */
    public static String buildLocation(String pattern, Study study) {
        return buildLocationFromRawParam(pattern, getObfuscatedStudyFileName(study));
    }

    /**
     * Simply replaces the place-holder with rawPAram, this is intended to be used at low level
     */
    public static String buildLocationFromRawParam(String pattern, String rawParam) {
        if(pattern == null)
            return "";

        if(rawParam == null) {
            return pattern;
        }
        return pattern.replace(
                DataSourceConfigFields.ACCESSION_PLACEHOLDER.getName(),
                rawParam
        );
    }

    /**
     * Returns the proper web link for a given data type and for the assay parameter, which can be one of
     * {@link AnnotationTypes#RAW_DATA_FILE_LINK}, {@link AnnotationTypes#PROCESSED_DATA_FILE_LINK},
     * {@link AnnotationTypes#GENERIC_DATA_FILE_LINK}.
     * <p/>
     * The link is built by taking annotations inserted by the file dispatcher in the data manager.
     * <p/>
     * WARNING: USE THIS METHOD TO BUILD data web links! Cause the value to replace the place holder with (in the
     * pattern) can vary, depending on wether it is about an external source (e.g. ArrayExpress) or it was built
     * by using the BII accession.
     */
    public String buildAssayWebLink(Assay assay, AnnotationTypes type) {
        if (assay == null) return null;

        String urlPattern =
                StringUtils.trimToNull(getDataLocation(assay.getMeasurement(), assay.getTechnology(), type));
        if (urlPattern == null) return null;

        String searchStr = null;
        if (type == AnnotationTypes.RAW_DATA_FILE_LINK) searchStr = ":RAW";
        else if (type == AnnotationTypes.PROCESSED_DATA_FILE_LINK) searchStr = ":PROCESSED";
        else if (type == AnnotationTypes.GENERIC_DATA_FILE_PATH) searchStr = ":GENERIC";

        if (searchStr == null) return null;

        Xref xref = assay.getSingleXref(searchStr);
        if (xref == null) return null;

        String rawVal = StringUtils.trimToNull(xref.getAcc());
        if (rawVal == null) return null;

        return buildLocationFromRawParam(urlPattern, rawVal);
    }


    public EntityManager getEntityManager() {
        if (entityManager == null)
            throw new IllegalStateException("entityManager is required but has not been set");
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Converts an accession to something suitable for being used as file name. Which means replacing some bad characters,
     * such as ":" and other stuff.
     * <p/>
     * <b>Please note</b>: this <b>is not</b> the inverse of {@link #filePath2Id(String)}.
     *
     * @param accession
     * @return
     */
    public static String accession2FileName(String accession) {
        if (accession == null || accession.length() == 0) return accession;

        accession = accession.trim();
        if (accession.length() == 0) return accession;

        // Deal with the protocol in the URL
        int i = accession.indexOf(':');
        if (i != -1) {
            accession = StringUtils.substring(accession, i + 1);
            if (accession.startsWith("//")) accession = StringUtils.substring(accession, 2);
        }

        String replacedChars = ":/.? ()[]{}";
        accession = StringUtils.replaceChars(
                accession, replacedChars, StringUtils.repeat("_", replacedChars.length())
        );

        return accession;
    }

    /**
     * @return {@link #accession2FileName(String)} + "_" + {@link Study#getObfuscationCode()}, the result is to be used
     *         for building obfuscated file paths about the study, cannot be accessed in an unauthorized way.
     */
    public static String getObfuscatedStudyFileName(Study study) {
        if (study == null) throw new RuntimeException("Cannot return an obfuscation code for a null study");
        String acc = StringUtils.trimToNull(study.getAcc());
        if (acc == null) throw new RuntimeException("Cannot return an obfuscation code for a study without accession");

        String obfuscation = study.getObfuscationCode();
        if (obfuscation == null) {
            obfuscation = RandomStringUtils.randomAlphanumeric(10);
            study.setObfuscationCode(obfuscation);
        }
        return accession2FileName(acc + "_" + obfuscation);
    }


    /**
     * Converts a file path into a string which is more appropriate as identifier. At the moment replaces '/' and other bad
     * characters with '_', removes extension (the last '.' until the end of the string), replaces remaining '.'s with
     * '_'.
     * <p/>
     * <b>Please note</b>: this <b>is not</b> the inverse of {@link #accession2FileName(String)}.
     */
    public static String filePath2Id(String path) {
        if (path == null || path.length() == 0) return path;

        path = path.trim();
        if (path.length() == 0) return path;

        // Deal with the protocol in the URL
        int i = path.indexOf(':');
        if (i != -1) {
            path = StringUtils.substring(path, i + 1);
            if (path.startsWith("//")) path = StringUtils.substring(path, 2);
        }

        path = path.replace('/', '_');

        // Remove the last dot and what it follows, this is considered the extension
        i = path.lastIndexOf('.');
        if (i != -1) path = path.substring(0, i);

        // If there are still further dots, convert them into nicer characters
        path = path.replace('.', '_');

        return path;
    }


}
