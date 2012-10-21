package uk.ac.ebi.bioinvindex.services.browse;

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
import uk.ac.ebi.bioinvindex.model.xref.ResourceType;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBrowseField;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.bridge.IndexFieldDelimiters;
import uk.ac.ebi.bioinvindex.services.AssayGroupInfo;
import uk.ac.ebi.bioinvindex.services.DataLink;

import java.util.*;

/**
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Feb 26, 2008
 */
public class BrowseStudyBeanImpl extends IndexFieldDelimiters implements BrowseStudyBean, Comparable {
    private static final Log log = LogFactory.getLog(BrowseStudyBeanImpl.class);

    private Map<String, List<String>> characteristics;
    private Map<String, List<String>> factors;

    private Map<StudyBrowseField, String[]> values;

    public BrowseStudyBeanImpl(Map<StudyBrowseField, String[]> values) {
        this.values = values;
    }

    public String getInvestigation() {
        return getFirstValue(StudyBrowseField.INVESTIGATION_ACC);
    }

    public String getAcc() {
        return getFirstValue(StudyBrowseField.STUDY_ACC);
    }

    public String getTitle() {
        return getFirstValue(StudyBrowseField.TITLE);
    }

    public String getOrganism() {
        return getConcatValues(StudyBrowseField.ORGANISM);
    }

    public String getFactor() {
        return getConcatValues(StudyBrowseField.FACTORS);
    }

    public boolean hasFactors() {
        buildFactorsIfNecessary();

        return factors != null && factors.size() > 0;
    }

    public boolean hasCharacteristics() {
        buildCharacteristicsIfNecessary();

        return characteristics != null && characteristics.size() > 0;
    }

    public List<String> getFactorNames() {
        buildFactorsIfNecessary();
        if (factors.size() > 0) {
            return new ArrayList<String>(factors.keySet());
        }
        return new ArrayList<String>();
    }

    public List<String> getCharacteristicNames() {
        buildCharacteristicsIfNecessary();
        if (characteristics.size() > 0) {
            return new ArrayList<String>(characteristics.keySet());
        }

        return new ArrayList<String>();
    }

    public List<String> getFactorValues(String factorName) {
        buildFactorsIfNecessary();
        if (factors.containsKey(factorName)) {
            return factors.get(factorName);
        }

        return new ArrayList<String>();
    }

    public List<String> getCharacteristicValues(String characteristicName) {
        buildCharacteristicsIfNecessary();
        if (characteristics.containsKey(characteristicName)) {
            return characteristics.get(characteristicName);
        }

        return new ArrayList<String>();
    }

    private void buildCharacteristicsIfNecessary() {
        if (characteristics == null) {
            log.info("Building characteristics!");
            characteristics = processPropertyValues(StudyBrowseField.CHARACTERISTICS);
        }
    }

    private void buildFactorsIfNecessary() {
        if (factors == null) {
            log.info("Building factors!");
            factors = processPropertyValues(StudyBrowseField.FACTORS);
        }
    }

    /**
     * Takes the factors and characteristics which
     *
     * @return Map from the factor names to the list of associated values (e.g. dose -> 0.1g, 0.2g)
     */
    private Map<String, List<String>> processPropertyValues(StudyBrowseField fieldToProcess) {

        String[] strings = values.get(fieldToProcess);

        if (strings == null || strings.length < 1) {
            return new HashMap<String, List<String>>();
        }

        Map<String, List<String>> processedValues = new HashMap<String, List<String>>();
        Map<String, Set<String>> addedValues = new HashMap<String, Set<String>>();

        for (String string : strings) {
            if (string.contains("[")) {
                String propertyName = string.substring(0, string.indexOf("["));
                // Should be enough to remove all the extra artifacts.
                String propertyValue = string.replace(propertyName, "").replace("[", "").replaceAll("]", "");

                String[] splitValues = splitPropertyValues(propertyValue);

                if (!processedValues.containsKey(propertyName)) {
                    processedValues.put(propertyName, new ArrayList<String>());
                    addedValues.put(propertyName, new HashSet<String>());
                }

                for (String value : splitValues) {
                    if (!addedValues.get(propertyName).contains(value)) {
                        processedValues.get(propertyName).add(value);
                        addedValues.get(propertyName).add(value);
                    }
                }
            }
        }

        return processedValues;
    }

    private String[] splitPropertyValues(String propertyValue) {

        if (propertyValue.contains(":?")) {
            return propertyValue.split(":\\?");
        } else {
            return new String[]{propertyValue};
        }
    }


    /**
     * Retrieves the AssayInfoBean, required for the Browse view
     *
     * @return List<AssayInfoBean>
     */
    public List<AssayInfoBean> getAssayBeans() {

        String[] strings = values.get(StudyBrowseField.ASSAY_INFO);
        if (strings == null) {
            return new ArrayList<AssayInfoBean>(1);
        }

        List<AssayInfoBean> answer = new ArrayList<AssayInfoBean>(strings.length);
        for (String string : strings) {
            answer.add(processAssayInfoResultString(string));
        }
        return answer;
    }

    /**
     * Retrieves the AssayInfoBean, required for the Browse view
     *
     * @return List<AssayInfoBean>
     */
    public List<AssayGroupInfo> getAssayGroups() {

        String[] strings = values.get(StudyBrowseField.ASSAY_INFO);
        if (strings == null) {
            return new ArrayList<AssayGroupInfo>(1);
        }

        List<AssayGroupInfo> answer = new ArrayList<AssayGroupInfo>(strings.length);
        for (String string : strings) {
            answer.add(processAssayGroupInfoFromString(string));
        }
        return answer;
    }

    private String getFirstValue(StudyBrowseField fieldName) {
        String[] strings = values.get(fieldName);
        if (strings != null && strings.length >= 1) {
            return strings[0];
        } else {
            return "";
        }
    }

    private AssayInfoBean processAssayInfoResultString(String assayInfoString) {
        AssayInfoBean assayInfoBean = new AssayInfoBean();

        System.out.println("The assay info string is: " + assayInfoString);

        String[] infoSections;

        infoSections = splitAssayInfoString(assayInfoString);

        // at this point we now have an array where the first element should be the assay and all others are the DB links

        assayInfoBean = createAssayInfoBean(assayInfoBean, infoSections[0]);

        return assayInfoBean;
    }


    private AssayGroupInfo processAssayGroupInfoFromString(String assayGroupInfoString) {
        AssayGroupInfo assayGroupInfo = new AssayGroupInfo();
        String[] infoSections;

        infoSections = splitAssayInfoString(assayGroupInfoString);

        // at this point we now have an array where the first element should be the assay and all others are the DB links

        AssayInfoBean assay = new AssayInfoBean();

        assay = createAssayInfoBean(assay, infoSections[0]);

        assayGroupInfo.setEndPoint(assay.getEndPoint());
        assayGroupInfo.setTechnology(assay.getTechnology());
        assayGroupInfo.setPlatform(assay.getPlatform());

        try {
            assayGroupInfo.setCount(Integer.valueOf(assay.getCount()));
        } catch (NumberFormatException nfe) {
            assayGroupInfo.setCount(0);
        }

        System.out.println("Created Assay group " + assayGroupInfo.getDisplayLabel() + ", now checking dbLinks.");

        Map<String, DataLink> accessionToDataLink = new HashMap<String, DataLink>();

        for (int dbLinkIndex = 1; dbLinkIndex < infoSections.length; dbLinkIndex++) {

            System.out.println("Processing DB Link for Assay Group: " + infoSections[dbLinkIndex]);
            DataLink link = createDBLink(infoSections[dbLinkIndex]);

            String accessionToCheck = link.getAcc();

            if (link.getAcc().contains(">")) {
                int indexOfAngularBracket = link.getAcc().indexOf(">");
                accessionToCheck = link.getAcc().substring(0, indexOfAngularBracket);
                link.setAcc(link.getAcc().substring(indexOfAngularBracket + 1));
            }

            if (!accessionToDataLink.containsKey(accessionToCheck)) {
                accessionToDataLink.put(accessionToCheck, link);
            }

            accessionToDataLink.get(accessionToCheck).addDataOfType(determineDataType(link.getSourceName()), link.getAcc());
        }

        Set<String> addedDataLinks = new HashSet<String>();

        for (String accession : accessionToDataLink.keySet()) {
            String linksAsString = accessionToDataLink.get(accession).getLinksAsString();
            if (!addedDataLinks.contains(linksAsString)) {
                assayGroupInfo.addDataLink(accessionToDataLink.get(accession));
                addedDataLinks.add(linksAsString);
            }
        }
        return assayGroupInfo;
    }


    private String[] splitAssayInfoString(String assayInfoString) {
        String[] infoSections;
        if (assayInfoString.contains(":?")) {
            infoSections = assayInfoString.split(":\\?");
        } else {
            infoSections = new String[]{assayInfoString};
        }
        return infoSections;
    }


    private AssayInfoBean createAssayInfoBean(AssayInfoBean assayInfoBean, String assayRepresentation) {

        assayRepresentation = assayRepresentation.replace("assay(", "").replace(")", "");

        Scanner scanner = new Scanner(assayRepresentation);
        scanner.useDelimiter("\\|");

        if (scanner.hasNext()) {
            assayInfoBean.setEndPoint(scanner.next());
        } else {
            assayInfoBean.setEndPoint("");
        }

        if (scanner.hasNext()) {
            String token = scanner.next();
            try {
                // check to see if the String can be made into a number.
                Integer.valueOf(token);
                assayInfoBean.setCount(token);
                assayInfoBean.setTechnology("");
            } catch (NumberFormatException nfe) {
                assayInfoBean.setTechnology(token);
                if (scanner.hasNext()) {
                    assayInfoBean.setCount(scanner.next());
                } else {
                    assayInfoBean.setCount("0");
                }
            }
        }

        return assayInfoBean;
    }

    private DataLink createDBLink(String dbLinkRepresentation) {
        DataLink link = new DataLink();

        dbLinkRepresentation = dbLinkRepresentation.replace("xref(", "").replace("link(", "").replace(")", "");

        if (dbLinkRepresentation.contains("->")) {
            String[] dbLinkParts = dbLinkRepresentation.split("->");
            link.setAcc(dbLinkParts[0]);
            link.setSourceName(dbLinkParts[1].toUpperCase());
        }

        return link;
    }

    private ResourceType determineDataType(String sourceName) {
        if (sourceName.contains(ResourceType.RAW.getName())) {
            return ResourceType.RAW;
        } else if (sourceName.contains(ResourceType.PROCESSED.getName())) {
            return ResourceType.PROCESSED;
        } else if (sourceName.contains(ResourceType.ENTRY.getName())) {
            return ResourceType.ENTRY;
        }

        return null;
    }

    private String getConcatValues(StudyBrowseField field) {
        String[] strings = values.get(field);
        if (strings == null || strings.length < 1) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Set<String> values = new HashSet<String>(strings.length);
        for (String string : strings) {
            if (!"".equals(string) && !values.contains(string)) {
                sb.append(string);
                sb.append(", ");
                values.add(string);
            }
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    public int compareTo(Object o) {
        return getAcc().compareTo(((BrowseStudyBean) o).getAcc());
    }

    @Override
    public String toString() {
        return getAcc();
    }
}
