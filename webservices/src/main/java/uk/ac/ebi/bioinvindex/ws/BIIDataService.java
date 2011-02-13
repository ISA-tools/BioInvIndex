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

import uk.ac.ebi.bioinvindex.wsmodel.data.PropertyValuesWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.DataWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.AssayTypeWS;
import uk.ac.ebi.bioinvindex.wsmodel.utils.QueryObject;

import java.util.List;

/**
 * @author: Manon DELAHAYE [manon@ebi.ac.uk]
 * Date: Mar 24, 2009
 */
public interface BIIDataService {

    /**
     * Retrieve the data associated to the property given. It will return a list
     * of DataWS object providing all the information we need.
     * The property fields are filled or not it doesn't matter. The more fields are
     * filled, the more precise the query will be.
     * @param property The property object more or less filled in
     * @return A list of data corresponding to the property given.
     */
    public List<DataWS> getDataByProperty(PropertyValuesWS property);

    /**
     * Retrieve the data associated to the assayType given. It will return a list
     * of DataWS object providing all the information we need.
     * The assayTYpe fields are filled or not it doesn't matter. The more fields are
     * filled, the more precise the query will be.
     * @param assayType The assayType object more or less filled in
     * @return A list of data corresponding to the assayType given.
     */
    public List<DataWS> getDataByAssayType(AssayTypeWS assayType);

    /**
     * Retrieve the data associated to the assayTypes and the properties given. It will
     * return a list of DataWS object providing all the information we need.
     * The assayTypes and properties' fields are filled or not it doesn't matter. The more fields are
     * filled, the more precise the query will be.
     * @param queryObject The query object containing a list of assay types and a list of properties
     * @return A list of data corresponding to the assay types and properties given
     */
    public List<DataWS> getDataByPropertiesAndAssayType(QueryObject queryObject);

    /**
     * Retrieve the data associated to the study having an accesion number equal to the
     * one given in input
     * @param studyAcc The accession number of the study we want the data for
     * @return A list of data associated to the study
     */
    public List<DataWS> getDataByStudyAcc(String studyAcc);
}