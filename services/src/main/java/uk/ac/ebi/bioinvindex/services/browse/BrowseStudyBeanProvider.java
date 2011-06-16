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
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.search.StudyFreeTextSearch;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.*;
import uk.ac.ebi.bioinvindex.services.utils.AlphanumComparator;

import java.util.*;

/**
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: Feb 26, 2008
 */
@Name("studyBeanProvider")
@AutoCreate
public class BrowseStudyBeanProvider /*implements IStudyBeanProvider<BrowseStudyBean>*/ {
    private static final Log log = LogFactory.getLog(BrowseStudyBeanProvider.class);

    @In(create = true)
    private SecureStudyFreeTextSearch secureStudySearch;

    @In
    private Identity identity;

    private String searchPattern;

    private String assayType;

    private String endPoint;

    private String platform;

    private String organism;

    private List<String> assayTypeNames;

    public Collection<? extends BrowseStudyBean> getItems() {

        List<Map<StudyBrowseField, String[]>> fieldValues =
                secureStudySearch.getAllStudyBrowseFieldValuesForUser(buildBiiFilterQuery(), identity.getUsername());

        //Convert a a field-values map returned by search into a collection of browse beans for view.
        List<BrowseStudyBeanImpl> answer = new ArrayList<BrowseStudyBeanImpl>(fieldValues.size());
        for (Map<StudyBrowseField, String[]> fieldValue : fieldValues) {
            BrowseStudyBeanImpl bean = new BrowseStudyBeanImpl(fieldValue);
            answer.add(bean);
        }

        Collections.sort(answer, new AlphanumComparator<BrowseStudyBeanImpl>());


        Iterator<BrowseStudyBeanImpl> iterator = answer.iterator();

        Set<String> addedAccessions = new HashSet<String>();

        while (iterator.hasNext()) {
            BrowseStudyBeanImpl bean = iterator.next();

            if (addedAccessions.contains(bean.getAcc())) {
                iterator.remove();
            } else {
                addedAccessions.add(bean.getAcc());
            }
        }

        return answer;
    }

    public BrowseStudyBean getStudy(String accession) {

        List<Map<StudyBrowseField, String[]>> fieldValues =
                secureStudySearch.getAllStudyBrowseFieldValuesForUser(buildStudyInfoFilterQuery(accession), identity.getUsername());

        List<BrowseStudyBeanImpl> answer = new ArrayList<BrowseStudyBeanImpl>(fieldValues.size());

        for (Map<StudyBrowseField, String[]> fieldValue : fieldValues) {
            BrowseStudyBeanImpl bean = new BrowseStudyBeanImpl(fieldValue);
            answer.add(bean);
        }


        if (answer.size() > 0) {
            log.info("Returning answer");
            return answer.get(0);
        }

        log.info("Returning null");

        return null;
    }

    private BIIFilterQuery<Study> buildBiiFilterQuery() {
        BIIFilterQuery<Study> query = new StudyBIIFilterQuery<Study>();

        query.setSearchText(getSearchPattern());

        if (getOrganism() != null && !getOrganism().equals("")) {
            query.addFilterValue(FilterField.ORGANISM, getOrganism());
        }

        if (getAssayType() != null && !getAssayType().equals("")) {
            query.addFilterValue(FilterField.TECHNOLOGY_NAME, getAssayType());
        }

        if (getEndPoint() != null && !getEndPoint().equals("")) {
            query.addFilterValue(FilterField.ENDPOINT_NAME, getEndPoint());
        }

        if (getPlatform() != null && !getPlatform().equals("")) {
            query.addFilterValue(FilterField.PLATFORM, getPlatform());
        }

        return query;
    }

    private BIIFilterQuery<Study> buildStudyInfoFilterQuery(String studyAccession) {
        BIIFilterQuery<Study> query = new StudyBIIFilterQuery<Study>();

        query.addFilterValue(FilterField.ACCESSION, studyAccession);

        return query;
    }


    // Boiler plate code: getters and setters.

    public String getSearchPattern() {
        if (searchPattern != null) {
            return searchPattern.toLowerCase();
        }
        return searchPattern;
    }


    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    public String getAssayType() {
        return this.assayType;
    }

    public void setAssayType(String assayType) {
        this.assayType = assayType;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    //Support for multiple selection

    public List<String> getAssayTypeNames() {

        if (assayTypeNames != null) {
            return assayTypeNames;
        }

        if (getAssayType() != null) {
            assayTypeNames = new ArrayList<String>();
            StringTokenizer tokenizer = new StringTokenizer(assayType, ",");
            while (tokenizer.hasMoreTokens()) {
                assayTypeNames.add(tokenizer.nextToken());
            }
        }
        return assayTypeNames;
    }

    public void setAssayTypeNames(List<String> assayTypeNames) {
        this.assayTypeNames = assayTypeNames;

        StringBuilder sb = new StringBuilder();
        for (String assayTypeName : assayTypeNames) {

            sb.append(assayTypeName);
            sb.append(",");
        }
        setAssayType(sb.toString());
    }
}
