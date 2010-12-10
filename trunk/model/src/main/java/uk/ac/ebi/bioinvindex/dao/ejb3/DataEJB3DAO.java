package uk.ac.ebi.bioinvindex.dao.ejb3;

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

import uk.ac.ebi.bioinvindex.model.AssayResult;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.wsmodel.data.PropertyValuesWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.AssayTypeWS;
import uk.ac.ebi.bioinvindex.wsmodel.utils.QueryObject;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;

/**
 * @author: Manon DELAHAYE [manon@ebi.ac.uk]
 * Date: 07-Apr-2009
 */
public class DataEJB3DAO extends AccessibleEJB3DAO<Study> {

	public DataEJB3DAO() {
		super(Study.class);
	}

	public DataEJB3DAO(EntityManager entityManager) {
		super(Study.class, entityManager);
	}

    /**
     * Retrieve the assay result containing in their properties list a property which looks
     * like the one given.
     * @param property The property
     * @return The list of the assay results retrieved by the query
     */
    public List<AssayResult> getAssayResultByProperty(PropertyValuesWS property)
    {
        return (List<AssayResult>)super.getByProperty("DATA", property);
    }


    /**
     * Retrieve the assay results related to the study where the assays' types look
     * like the one given.
     * @param assayType The assayType
     * @return The list of the assay results retrieved by the query
     */
    public List<AssayResult> getAssayResultByAssayType(AssayTypeWS assayType)
    {
        return (List<AssayResult>)super.getByAssayType("DATA", assayType);
    }

    /**
     * Retrieve the assay results related to the study having as accession number
     * the number given in input
     * @param studyAcc The accession number of the study for which we want to retrieve the data
     * @return The list of the assay results retrieved by the query
     */
    public List<AssayResult> getAssayResultByStudyAcc(String studyAcc)
    {
        Query query = entityManager.createQuery("SELECT distinct ar " +
				"FROM AssayResult ar " +
				"WHERE " +
				"lower(ar.study.acc) = lower(:acc)")
				.setParameter("acc", studyAcc);

		return query.getResultList();
    }
}