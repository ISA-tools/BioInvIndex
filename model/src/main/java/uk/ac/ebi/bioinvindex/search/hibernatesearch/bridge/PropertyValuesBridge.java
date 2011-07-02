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

import org.jboss.seam.util.Conversions;
import uk.ac.ebi.bioinvindex.model.term.*;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBrowseField;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk)
 *         Date: Feb 20, 2008
 */
public class PropertyValuesBridge implements FieldBridge {

    public void set(String s, Object value, Document document, LuceneOptions luceneOptions) {

        Collection<PropertyValue> values = (Collection<PropertyValue>) value;

        Map<StudyBrowseField, Map<String, Set<String>>> allPropertyToValues = new HashMap<StudyBrowseField, Map<String, Set<String>>>();


        for (PropertyValue propertyValue : values) {
            String propValue = propertyValue.getValue();

            Map<String, Set<String>> propertyToValues = new HashMap<String, Set<String>>();

            if (propValue != null) {
                Property type = propertyValue.getType();

                if (!propertyToValues.containsKey(type.getValue())) {
                    propertyToValues.put(type.getValue(), new HashSet<String>());
                }

                String unit = "";
                if (propertyValue.getUnit() != null) {
                    unit = propertyValue.getUnit().getValue();
                }

                propertyToValues.get(type.getValue()).add(propValue + (unit.equals("") ? "" : " " + unit));

                if (type instanceof Factor) {

                    if (!allPropertyToValues.containsKey(StudyBrowseField.FACTORS)) {
                        allPropertyToValues.put(StudyBrowseField.FACTORS, new HashMap<String, Set<String>>());
                    }

                    allPropertyToValues.get(StudyBrowseField.FACTORS).putAll(propertyToValues);
                } else {
                    if (!allPropertyToValues.containsKey(StudyBrowseField.CHARACTERISTICS)) {
                        allPropertyToValues.put(StudyBrowseField.CHARACTERISTICS, new HashMap<String, Set<String>>());
                    }

                    allPropertyToValues.get(StudyBrowseField.CHARACTERISTICS).putAll(propertyToValues);
                }
            }
        }

        for (StudyBrowseField dataType : allPropertyToValues.keySet()) {
            for (String propertyType : allPropertyToValues.get(dataType).keySet()) {
                Field factorField = new Field(dataType.getName(),
                        buildPropertyValueCompositeRepresentation(propertyType,
                                allPropertyToValues.get(dataType).get(propertyType)),
                        luceneOptions.getStore(), luceneOptions.getIndex());

                document.add(factorField);
            }
        }
    }

    private String buildPropertyValueCompositeRepresentation(String valueType, Set<String> values) {
        StringBuilder representation = new StringBuilder();

        representation.append(valueType);
        representation.append("[");

        int count = 0;
        for (String value : values) {
            representation.append(value);
            if (count < (values.size() - 1)) {
                representation.append(":?");
            }
            count++;
        }

        representation.append("]");

        return representation.toString();
    }

}
