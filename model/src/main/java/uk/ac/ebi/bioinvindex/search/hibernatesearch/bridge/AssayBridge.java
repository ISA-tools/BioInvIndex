package uk.ac.ebi.bioinvindex.search.hibernatesearch.bridge;

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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.xref.Xref;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBrowseField;
import uk.ac.ebi.bioinvindex.utils.processing.ProcessingUtils;

import java.util.*;

/**
 * Creates a  String template: endpoint|technology|number|&&&acc1!!!url1&&&acc2!!!url2
 *
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk)
 *         Date: Feb 22, 2008
 */

public class AssayBridge extends IndexFieldDelimiters implements FieldBridge {

    public void set(String s, Object o, Document document, LuceneOptions luceneOptions) {
        Map<String, AssayTypeInfo> assayTypeToInfo = new HashMap<String, AssayTypeInfo>();

        Collection<Assay> assays = (Collection<Assay>) o;

        for (Assay assay : assays) {


            String type = buildType(assay);

            if (!assayTypeToInfo.containsKey(type)) {
                AssayTypeInfo info = new AssayTypeInfo();
                assayTypeToInfo.put(type, info);
            }
            // only go looking for assay results if there is a material associated with the assay.
            if (assay.getMaterial() != null) {
                Collection<AssayResult> assayResults = ProcessingUtils.findAssayResultsFromAssay(assay);
                createAssayExternalLinks(assayTypeToInfo, assayResults, type);
            }
            createXrefs(assayTypeToInfo, assay, type);

            assayTypeToInfo.get(type).increaseCount();
        }

        for (String type : assayTypeToInfo.keySet()) {
            StringBuilder fullInfo = new StringBuilder();
            fullInfo.append("assay(").append(type);
            fullInfo.append(FIELDS_DELIM);

            AssayTypeInfo info = assayTypeToInfo.get(type);

            fullInfo.append(info.getCount());
            fullInfo.append(")");

            fullInfo.append(":?");

            int outputCount = 0;

            System.out.println("There are " + info.getAccessions().size() + " accessions associated with this...");
            for (String acc : info.getAccessions()) {

                fullInfo.append(acc);

                if (outputCount != info.getAccessions().size() - 1) {
                    fullInfo.append(":?");
                }

                outputCount++;
            }
            Field fvField = new Field(StudyBrowseField.ASSAY_INFO.getName(), fullInfo.toString(), luceneOptions.getStore(), luceneOptions.getIndex());
            document.add(fvField);
        }
    }

    private void createXrefs(Map<String, AssayTypeInfo> assayTypeToInfo, Assay assay, String type) {
        for (Xref xref : assay.getXrefs()) {
            StringBuilder sb = new StringBuilder();
            sb.append("xref(").append(xref.getAcc()).append("->");
            sb.append(xref.getSource().getAcc()).append(")");
            assayTypeToInfo.get(type).addAccession(sb.toString());
        }
    }

    private void createAssayExternalLinks(Map<String, AssayTypeInfo> assayTypeToInfo, Collection<AssayResult> assayResults, String type) {
        Set<String> addedLinks = new HashSet<String>();
        for (AssayResult result : assayResults) {
            // we're only looking at links...should accommodate webdav etc. too
            if (result.getData() != null) {
                String dataFileName = result.getData().getName() == null ? "" : result.getData().getName();
                if (dataFileName.matches("(http|ftp|https).*") && dataFileName.contains("/")) {
                    // we only store the folder since that will take us to multiple file locations. Otherwise we'd have too
                    // many individual links pointing to the same place.
                    String folder = dataFileName.substring(0, result.getData().getName().lastIndexOf("/"));
                    if (!addedLinks.contains(folder)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("link(").append(folder).append("->");
                        String dataFileType = result.getData().getType().getName() == null ? "" : result.getData().getType().getName();
                        sb.append(dataFileType).append(")");
                        addedLinks.add(folder);
                        assayTypeToInfo.get(type).addAccession(sb.toString());
                    }
                }
            }
        }
    }

    private String buildType(Assay assay) {
        return assay.getMeasurement().getName() + "|" + assay.getTechnologyName();
    }


    private class AssayTypeInfo {

        private int count = 0;

        private Set<String> accessions = new HashSet<String>();

        public int getCount() {
            return count;
        }

        public Set<String> getAccessions() {
            return accessions;
        }

        public void increaseCount() {
            count++;
        }

        public void addAccession(String acc) {
            accessions.add(acc);
        }
    }
}
