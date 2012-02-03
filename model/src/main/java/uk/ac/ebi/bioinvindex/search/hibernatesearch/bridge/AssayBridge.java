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
import uk.ac.ebi.bioinvindex.model.Annotation;
import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.processing.Assay;
import uk.ac.ebi.bioinvindex.model.term.AnnotationTypes;
import uk.ac.ebi.bioinvindex.model.xref.Xref;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBrowseField;
import uk.ac.ebi.bioinvindex.utils.datasourceload.DataLocationManager;
import uk.ac.ebi.bioinvindex.utils.processing.ProcessingUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("BIIEntityManager");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        DataLocationManager dataLocationManager = new DataLocationManager();
        dataLocationManager.setEntityManager(entityManager);

        for (Assay assay : assays) {

            if (assay.getTechnologyName().equals("mass spectrometry")) {
                Collection<AssayResult> assayResults = ProcessingUtils.findAssayResultsFromAssay(assay);

                String fileLink = dataLocationManager.getDataLocationLink(assay.getMeasurement().getName(), assay.getTechnologyName(), assay.getStudy().getObfuscationCode(),
                        AnnotationTypes.GENERIC_DATA_FILE_LINK);

                System.out.println("File link: " + fileLink);

                String pathLink = dataLocationManager.getDataLocationLink(assay.getMeasurement().getName(), assay.getTechnologyName(), assay.getStudy().getObfuscationCode(),
                        AnnotationTypes.GENERIC_DATA_FILE_PATH);

                System.out.println("Path link: " + pathLink);

                for (AssayResult result : assayResults) {
                    for (Annotation annotation : result.getData().getAnnotation("metaboliteFile")) {
                        System.out.printf("Type: %s -> Value: %s\n", annotation.getType().getValue(), annotation.getText());

                        MetaboLightsIndexer.indexMetaboliteFile(pathLink.replace("${study-acc}",
                                assay.getStudy().getObfuscationCode()) + annotation.getText(),
                                document,
                                luceneOptions);
                    }
                }
            }

            String type = buildType(assay);

            if (!assayTypeToInfo.containsKey(type)) {
                AssayTypeInfo info = new AssayTypeInfo();
                assayTypeToInfo.put(type, info);
            }

            for (Xref xref : assay.getXrefs()) {
                System.out.println("Adding XREF to AssayTypeInfo: " + xref.getSource().getAcc() + "(" + xref.getAcc() + ") for " + type);

                StringBuilder sb = new StringBuilder();
                sb.append("xref(").append(xref.getAcc()).append("->");
                sb.append(xref.getSource().getAcc()).append(")");

                assayTypeToInfo.get(type).addAccession(sb.toString());
            }

            assayTypeToInfo.get(type).increaseCount();
        }
        // each data link should be stored perhaps, or at least whatever is required to make it display in the Study page.

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

        entityManager.close();
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
