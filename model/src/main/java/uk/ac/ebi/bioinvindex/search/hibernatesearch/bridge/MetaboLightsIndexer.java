package uk.ac.ebi.bioinvindex.search.hibernatesearch.bridge;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.LuceneOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;

public class MetaboLightsIndexer {

    static final String[] COLUMNS_TO_INDEX = {"description", "identifier"};

    public static Boolean indexMetaboliteFile(String metaboliteFile, Document document, LuceneOptions luceneOptions) {

        System.out.println("Looking to index " + metaboliteFile);
        // If there is no metabolite file...
        if (metaboliteFile == null) return false;

        // Get the indexed fields with the correspondent values
        HashMap<String, StringBuilder> indexedFields = getIndexedFields(metaboliteFile);

        // For each field indexed
        for (Entry<String, StringBuilder> fld : indexedFields.entrySet()) {
            // Index the field
            Field fvField = new Field("Metabolites_" + fld.getKey(), fld.getValue().toString(), luceneOptions.getStore(), luceneOptions.getIndex());
            document.add(fvField);
        }
        return true;
    }

    public static HashMap<String, StringBuilder> getIndexedFields(String filename) {

        // Create a Hashmap with the index of the columns we want to index (
        HashMap<String, Integer> column_indexes = new HashMap<String, Integer>();

        // Hash with the field as the key and the values (Separated by ~) as values.
        HashMap<String, StringBuilder> indexedFields = new HashMap<String, StringBuilder>();

        // For each field to index
        for (String aCOLUMNS_TO_INDEX : COLUMNS_TO_INDEX) {
            column_indexes.put(aCOLUMNS_TO_INDEX, null);
            indexedFields.put(aCOLUMNS_TO_INDEX, new StringBuilder(""));
        }

        // Read the first line
        try {

            // Get the index based on the first row
            File file = new File(filename);

            // Open the file
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = "";
            int linecount = 1;

            //Go through the file
            while ((line = reader.readLine()) != null) {

                String[] lineArray = lineToArray(line);

                // Get the indexes for the columns descriptions
                if (linecount == 1) {

                    // For each field in the line
                    for (int i = 0; i < lineArray.length; i++) {

                        // Get the field
                        String field = lineArray[i];

                        // Check if it is in the hash
                        if (column_indexes.containsKey(field)) {

                            // Add the index
                            column_indexes.put(field, i);
                        }
                    }

                } else {

                    // Get the values
                    // For each column to index
                    for (Entry<String, Integer> ci : column_indexes.entrySet()) {

                        // Get the value
                        String value = lineArray[ci.getValue()];

                        // If value is NOT empty
                        if (!value.isEmpty()) {

                            // Get the previous value
                            StringBuilder previousValue = indexedFields.get(ci.getKey());

                            // Add the new value
                            previousValue.append("~" + value);

                        }
                    }
                }


                linecount++;
            }

            //Close the reader
            reader.close();


        } catch (Exception e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

        // Return indexed fields
        return indexedFields;

    }


    public static String[] lineToArray(String line) {

        if (line.length() > 0) {
            // Remove the first double quote and the last one
            line = line.substring(1, line.length());
            line = line.substring(0, line.length() - 1);
        }

        // Add -1 to get empty strings.
        //return line.split("[^\"|\"\t\"|\"$]", -1);
        return line.split("\"\t\"", -1);
    }

}