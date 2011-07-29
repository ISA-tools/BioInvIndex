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
import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBrowseField;

import java.util.Collection;

public class PublicationBridge extends IndexFieldDelimiters implements FieldBridge {

    public void set(String s, Object o, Document document, LuceneOptions luceneOptions) {
        System.out.println("____BUILDING PUBLICATIONS FOR INDEX...._____");

        Collection<Publication> publications = (Collection<Publication>) o;

        System.out.println("There are " + publications.size() + " publications available...");

        for(Publication publication : publications) {

            String representation = buildRepresentationForIndex("title:" + publication.getTitle(), "authors:" + publication.getAuthorList(),
                    "pubmed:" + publication.getPmid(), "doi:" + publication.getDoi());

            System.out.println("Publication representation is " + representation);

            Field fvField = new Field(StudyBrowseField.PUBLICATION.getName(), representation, luceneOptions.getStore(), luceneOptions.getIndex());
            document.add(fvField);
        }
    }
}
