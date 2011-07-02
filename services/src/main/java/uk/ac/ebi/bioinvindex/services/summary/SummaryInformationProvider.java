package uk.ac.ebi.bioinvindex.services.summary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import uk.ac.ebi.bioinvindex.services.AssayGroupInfo;
import uk.ac.ebi.bioinvindex.services.browse.BrowseStudyBean;
import uk.ac.ebi.bioinvindex.services.browse.BrowseStudyBeanProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/06/2011
 *         Time: 16:03
 */
@Name("summaryInformationProvider")
@Scope(ScopeType.PAGE)
public class SummaryInformationProvider {

    private static final Log log = LogFactory.getLog(SummaryInformationProvider.class);
    @In
    private BrowseStudyBeanProvider studyBeanProvider;

    private List<BrowseStudyBean> studyBeans;

    /**
     * Returns a String representation suitable for RaphaelJS of the Studies to the number of assays
     *
     * @return data -> [1093, 1231, 233, 2123] legend -> [BII-S-8, BII-S-9, BII-S10, BII-S-11]
     */
    public String getStudyToAssayCount() {
        if (studyBeans == null) {
            getStudyInformationBeans();
        }

        StringBuilder values = new StringBuilder();
        StringBuilder legend = new StringBuilder();

        for(BrowseStudyBean study : studyBeans) {
            values.append(!values.toString().equals("") ? "," : "").append(countAssays(study));
            legend.append(!legend.toString().equals("") ? "," : "").append("\"%%.%%" + study.getAcc() + "\"");
        }

        return "[" + values.toString() + "], {legend: [" + legend.toString() + "]";

    }

    public String getTechnologyDistributionAcrossStudies() {
        if (studyBeans == null) {
            getStudyInformationBeans();
        }

        StringBuilder ys = new StringBuilder();
        StringBuilder xs = new StringBuilder();
        StringBuilder yAxis = new StringBuilder();
        StringBuilder xAxis = new StringBuilder();
        StringBuilder data = new StringBuilder();

        return "";
    }

    private Map<String, Map<String, Integer>> buildDataForAssayAndStudyDistribution() {
        return null;
    }

    private List<String> getAllAvailableAssays() {
        List<String> allAvailableAssays = new ArrayList<String>();

        return allAvailableAssays;
    }

    private int countAssays(BrowseStudyBean study) {
        int count = 0;
        for(AssayGroupInfo groupInfo : study.getAssayGroups()) {
            count += groupInfo.getCount();
        }

        return count;
    }

    public List<BrowseStudyBean> getStudyInformationBeans() {
        try {
            studyBeans = (List<BrowseStudyBean>) getStudyBeanProvider().getItems();
            return studyBeans;
        } catch (Exception e) {
            log.error("Exception thrown...oh no! -> " + e.getMessage());
            return new ArrayList<BrowseStudyBean>();
        }
    }

    public BrowseStudyBeanProvider getStudyBeanProvider() {
        return studyBeanProvider;
    }


    public void setStudyBeanProvider(BrowseStudyBeanProvider studyBeanProvider) {
        this.studyBeanProvider = studyBeanProvider;
    }

}
