package uk.ac.ebi.bioinvindex.ws;

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

import uk.ac.ebi.bioinvindex.wsmodel.study.StudyWSReturnImpl;
import uk.ac.ebi.bioinvindex.wsmodel.study.ContactWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.PropertyValuesWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.AssayTypeWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.DataWS;
import uk.ac.ebi.bioinvindex.wsmodel.utils.QueryObject;

import java.util.List;

/**
 * @author: Manon DELAHAYE [manon@ebi.ac.uk]
 * Date: Mar 24, 2009
 */
public interface BIIStudyService {

    /**
     * Retrieve all the studies' accesion numbers
     * @return A list of the accesion numbers existing
     */
    public List<String> getAllStudyAccs();

    /**
     * Retrieve the study corresponding to the accession number given
     * @param acc The accession number of the study we want to retrieve
     * @return The study corresponding to the accession number given in parameter
     */
    public StudyWSReturnImpl getStudyByAcc(String acc);

//    /**
//     * Retrieve the list of studies which have the contact given in their contact list
//     * @param contact The contact that we have to search for
//     * @return The list of the studies having the contact given in parameter in their contact list
//     */
//    public List<StudyWSReturnImpl> getStudiesByContact(ContactWS contact);

    /**
     * Retrieve the list of studies which have a contact with this last name in
     * their contact list.
     * @param lastName The last name of the contact
     * @return The list of the studies having this contact in their contact list.
     */
    public List<StudyWSReturnImpl> getStudiesByContactLastName(String lastName);

    /**
     * Retrieve the list of studies which have a contact with this email in
     * their contact list.
     * @param email The email of the contact
     * @return The list of the studies having this contact in their contact list.
     */
    public List<StudyWSReturnImpl> getStudiesByContactEmail(String email);

    /**
     * Retrieve the list of studies which have a contact with this affiliation in
     * their contact list.
     * @param affiliation The affiliation of the contact
     * @return The list of the studies having this contact in their contact list.
     */
    public List<StudyWSReturnImpl> getStudiesByContactAffiliation(String affiliation);

    /**
     * Retrieve the list of studies which have a publication with this pubmed id in
     * their publications list.
     * @param pubmed_id The pubmed id of the publication
     * @return The list of the studies having this publication in their publications list.
     */
    public List<StudyWSReturnImpl> getStudiesByPubmedIdOrDoi(String pubmed_id);

    /**
     * Retrieve the study related to the property given. It will return a list
     * of studyWS object.
     * The property fields are filled or not it doesn't matter. The more fields are
     * filled, the more precise the query will be.
     * @param property The property object more or less filled in
     * @return A list of studies related to the property given.
     */
    public List<StudyWSReturnImpl> getStudiesByProperty(PropertyValuesWS property);

    /**
     * Retrieve the study related to the assayType given. It will return a list
     * of studyWS object.
     * The assayTYpe fields are filled or not it doesn't matter. The more fields are
     * filled, the more precise the query will be.
     * @param assayType The assayType object more or less filled in
     * @return A list of studies related to the assayType given.
     */
    public List<StudyWSReturnImpl> getStudiesByAssayType(AssayTypeWS assayType);

    /**
     * Retrieve the study related to the assayTypes and the properties given. It will
     * return a list of studyWS object.
     * The assayTypes and properties' fields are filled or not it doesn't matter. The more fields are
     * filled, the more precise the query will be.
     * @param queryObject The query object containing a list of assay types and a list of properties
     * @return A list of studies related to the assay types and properties given
     */
    public List<StudyWSReturnImpl> getStudiesByPropertiesAndAssayType(QueryObject queryObject);
}