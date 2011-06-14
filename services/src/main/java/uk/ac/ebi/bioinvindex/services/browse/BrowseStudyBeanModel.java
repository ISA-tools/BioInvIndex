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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBrowseField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: Feb 26, 2008
 */
@Name("browseStudyBeanModel")
@Scope(ScopeType.PAGE)
public class BrowseStudyBeanModel implements BrowseBean<BrowseStudyBean> {
    private static final Log log = LogFactory.getLog(BrowseStudyBeanModel.class);
    @In
    private BrowseStudyBeanProvider studyBeanProvider;

    private int totalAssays = 0;

    private List<BrowseStudyBean> studyBeans;

    public int getRowCount() {
        try {
            return getItemList().size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<BrowseStudyBean> getItemList() {
        log.info("In getItemList()");
        try {

            log.info("Getting study items");
            studyBeans = (List<BrowseStudyBean>) getStudyBeanProvider().getItems();

            return studyBeans;
        } catch (Exception e) {
            log.error("Exception thrown...oh no! -> " + e.getMessage());
            return new ArrayList<BrowseStudyBean>();
        }
    }

    public int getTotalAssays() {
        log.info("Getting total assays");
        try {
            if (totalAssays == 0) {
                for (BrowseStudyBean bsb : getItemList()) {
                    log.info("Processing " + bsb.getAssayBeans().size() + " assays...");
                    for (AssayInfoBean aib : bsb.getAssayBeans()) {
                        int assayCount;
                        try {
                            assayCount = Integer.valueOf(aib.getCount());
                        } catch (NumberFormatException nfe) {
                            log.error("Invalid numeric value for Assay count found.");
                            assayCount = 0;
                        }

                        log.info("Adding " + assayCount + " assays for " + aib.getEndPoint() + " using " + aib.getTechnology());
                        totalAssays += assayCount;
                    }
                }
            }
            return totalAssays;
        } catch (Exception e) {
            return 0;
        }
    }

    public BrowseStudyBeanProvider getStudyBeanProvider() {
        return studyBeanProvider;
    }


    public void setStudyBeanProvider(BrowseStudyBeanProvider studyBeanProvider) {
        this.studyBeanProvider = studyBeanProvider;
    }

}
