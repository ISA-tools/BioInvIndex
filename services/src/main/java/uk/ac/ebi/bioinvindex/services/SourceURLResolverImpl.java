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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import uk.ac.ebi.bioinvindex.model.term.AnnotationTypes;
import uk.ac.ebi.bioinvindex.model.xref.ResourceType;
import uk.ac.ebi.bioinvindex.services.cache.BIICache;
import uk.ac.ebi.bioinvindex.services.cache.Cache;
import uk.ac.ebi.bioinvindex.utils.datasourceload.DataLocationManager;
import uk.ac.ebi.bioinvindex.utils.datasourceload.DataSourceConfigFields;

/**
 * todo modify this to have generic url resolvers. Allow to specify a generic url and point out to anything.
 *
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: May 12, 2009
 */
@Name("sourceURLResolver")
@AutoCreate
public class SourceURLResolverImpl implements SourceURLResolver {

    private static Cache<String, String> cache = new BIICache<String, String>();

    private static final Log log = LogFactory.getLog(SourceURLResolverImpl.class);

    @In
    private DataLocationManager dataLocationManager;

    public String getRawDataURL(String measurement, String technology, String accession) {
        try {
            return getDataURL(measurement, technology, accession, AnnotationTypes.RAW_DATA_FILE_LINK);
        } catch (Exception e) {
            log.error("Unable to resolve Raw data URL");
            return "";
        }
    }

    public String getProcessedDataURL(String measurement, String technology, String accession) {
        try {

            return getDataURL(measurement, technology, accession, AnnotationTypes.PROCESSED_DATA_FILE_LINK);
        } catch (Exception e) {
            log.error("Unable to resolve Processed data URL");
            return "";
        }
    }

    public String getEntryURL(String measurement, String technology, String accession) {
        try {
            String location = dataLocationManager.getDataLocation(measurement, technology, AnnotationTypes.WEB_ENTRY_URL);
            if (!StringUtils.isEmpty(location) && !StringUtils.isEmpty(accession)) {
                return location.replace(DataSourceConfigFields.ACCESSION_PLACEHOLDER.getName(), accession);
            }
        } catch (Exception e) {
            log.error("Cannot read data location URL");

        }
        return "";
    }

    public String getIsaTabLocation(String studyAcc) {
        try {
            String location = dataLocationManager.getISATabMetaDataWebLink();
            if (!StringUtils.isEmpty(location) && !StringUtils.isEmpty(studyAcc)) {
                return location.replace(DataSourceConfigFields.ACCESSION_PLACEHOLDER.getName(), studyAcc);
            }
        } catch (Exception e) {
            log.error("Cannot read data location URL");
        }
        return "";
    }

    public boolean hasRawData(String measurement, String technology, DataLink dataLink) {
        try {
            return dataLink.hasDataOfType(ResourceType.RAW.getName()) &&
                    StringUtils.trimToNull(getRawDataURL(measurement, technology, dataLink.getAcc())) != null;
        } catch (Exception e) {
            log.error("Unable to determine if study has raw data!");
            return false;
        }
    }

    public boolean hasProcessedData(String measurement, String technology, DataLink dataLink) {
        try {
            return dataLink.hasDataOfType(ResourceType.PROCESSED.getName()) &&
                    StringUtils.trimToNull(getProcessedDataURL(measurement, technology, dataLink.getAcc())) != null;
        } catch (Exception e) {
            log.error("Unable to determine if study has Processed data!");
            return false;
        }
    }

    public boolean hasWebEntry(String measurement, String technology, DataLink dataLink) {
        try {
            boolean hasWeb = dataLink.hasDataOfType(ResourceType.ENTRY.getName());
            boolean linkNotBlank = StringUtils.trimToNull(getEntryURL(measurement, technology, dataLink.getAcc())) != null;

            return hasWeb && linkNotBlank;
        } catch (Exception e) {
            log.error("Unable to determine if study has a web entry!");
            return false;
        }
    }

    public String getViewImageLocation(String sourceName) {

        if (sourceName.toUpperCase().indexOf("AE") == 0 || sourceName.toUpperCase().indexOf("ARRAYEXPRESS") == 0) {
            return "ArrayExpress";
        } else if (sourceName.toUpperCase().indexOf("PRIDE") == 0) {
            return "PRIDE";
        } else if (sourceName.toUpperCase().indexOf("ENA") == 0) {
            return "ENA";
        } else if (sourceName.toUpperCase().indexOf("EMBL_BANK") == 0) {
            return "EMBL-Bank";
        } else if (sourceName.toUpperCase().indexOf("EMBL:WEB") == 0) {
            return "EMBL";
        } else if (sourceName.toUpperCase().indexOf("GEO") == 0) {
            return "GEO";
        } else {
            return "External link";
        }
    }

    private String getDataURL(String measurement, String technology, String accession, AnnotationTypes type) {
        try {

            String location;
            if ((location = cache.find(accession + "/" + measurement + "/" + (technology == null ? "noTechnology" : technology) + "/" + type.getName())) == null) {

                String tmpLocation = dataLocationManager.getDataLocationLink(measurement, technology, accession, type);

                if (!StringUtils.isEmpty(tmpLocation) && !StringUtils.isEmpty(accession)) {
                    String locationWithAcc = tmpLocation.replace(DataSourceConfigFields.ACCESSION_PLACEHOLDER.getName(), accession);

                    location = locationWithAcc;
                    //For AE links
                    if (locationWithAcc.indexOf(DataSourceConfigFields.ACCESSION_PREFIX_PLACEHOLDER.getName()) > 0 && accession.indexOf("-") > -1) {
                        String dataType = accession.substring(accession.indexOf("-") + 1, accession.lastIndexOf("-"));
                        location = locationWithAcc.replace(DataSourceConfigFields.ACCESSION_PREFIX_PLACEHOLDER.getName(), dataType);
                    }

                    cache.attach(accession + "/" + measurement + "/" + (technology == null ? "noTechnology" : technology) + "/" + type.getName(), location);
                }
            }

            return location;
        } catch (Exception e) {
            log.error("Cannot read data location URL");
        }
        return "";
    }

    public boolean clearCache() {
        log.info("******");
        log.info("Cleaning up SourceURLResolvers cache");

        cache.clearCache();

        return true;
    }
}
