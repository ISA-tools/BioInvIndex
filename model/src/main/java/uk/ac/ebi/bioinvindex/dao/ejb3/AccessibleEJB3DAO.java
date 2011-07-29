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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.criterion.Restrictions;

import uk.ac.ebi.bioinvindex.dao.AccessibleDAO;
import uk.ac.ebi.bioinvindex.model.Accessible;
import uk.ac.ebi.bioinvindex.wsmodel.data.AssayTypeWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.PropertyValuesWS;

/**
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Nov 22, 2007
 */
public class AccessibleEJB3DAO<T extends Accessible>
		extends AnnotatableEJB3DAO<T>
		implements AccessibleDAO<T> {

	protected AccessibleEJB3DAO() {
	}

	protected AccessibleEJB3DAO(Class<T> persistentClass) {
		super(persistentClass);
	}

	public AccessibleEJB3DAO(Class<T> persistentClass, EntityManager entityManager) {
		super(persistentClass, entityManager);
	}

	public T getByAcc(String acc) {
		Class<T> clazz = getPersistentClass();
		 return (T) getSession().createCriteria(clazz)
                 .setFlushMode(org.hibernate.FlushMode.MANUAL)
                 .add(Restrictions.eq("acc", acc)).uniqueResult();
	}

    /**
     * Retrieve the assay result or the studies containing in their properties list a property which looks
     * like the one given.
     * @param property The property
     * @param queryType The type of the query : either DATA or STUDY
     * @return The list of the assay results or studyImpl retrieved by the query
     */
    public List getByProperty(String queryType, PropertyValuesWS property)
    {
        String select,from,where;

        /** Retrieving the property attributes to use inside the query */
        String name = property.getName();
        String value = property.getValue();
        String unit = property.getUnit();
        int type = -1;
        if(property.getType() != null)
            type = property.getType().ordinal();

        /*
         * The query is separated in many parts so that we can deal with each of them separately
         * and add things to some and to others.
         * The select, from and where depend on the type of query we are doing : a query which returns
         * studies or a query which returns data.
         */
        if(queryType.equals("DATA"))
        {
            select = "SELECT distinct ar ";
            from = "FROM AssayResult ar join ar.cascadedPropertyValues pv ";
            where = "WHERE 1=1 ";
        }
        //queryType.equals("STUDY")
        else
        {
            select = "SELECT distinct s ";
            from = "FROM Study s , AssayResult ar join ar.cascadedPropertyValues pv ";
            where = "WHERE ar.study.id = s.id ";
        }


        /*
         * The query is not the same whether the type is factor, characteristic or protocol.
         * If the type is none of those 3 possibilities, then we don't deal with the type.
         */
        switch (type)
        {
            // CHARACTERISTIC
            case 0 :
                from += ", Characteristic p ";
                where += "AND pv.type = p.id ";
                break;
            // FACTOR
            case 1 :
                from += ", Factor p ";
                where += "AND pv.type = p.id ";
                break;
            // PROTOCOL
            case 2 :
                from += ", Parameter p ";
                where += "AND pv.type = p.id ";
                break;
        }

        /* We create the query putting all the parts together in the right order */
        String querySyntax = select + from + where;

        /* We complete the query if needed */
        // Name
        if(name != null && name.trim().length() != 0)
            querySyntax += "AND lower(pv.type.value) = lower('"+name.trim().replaceAll("'","''")+"') ";

        // Value
        if(value != null && value.trim().length() != 0)
            querySyntax += "AND lower(pv.value) = lower('"+value.trim().replaceAll("'","''")+"') ";

        //Unit
        if(unit != null && unit.trim().length() != 0)
            querySyntax += "AND lower(pv.unit.value) = lower('"+unit.trim().replaceAll("'","''")+"') ";

        Query query = entityManager.createQuery(querySyntax);

        return query.getResultList();
	}

    /**
     * Retrieve the assay results related to the study or the studies where the assays' types look
     * like the one given.
     * @param assayType The assayType
     * @param queryType The type of the query : either DATA or STUDY
     * @return The list of the assay results or studyImpl retrieved by the query
     */
    public List getByAssayType(String queryType, AssayTypeWS assayType)
    {
        String querySyntax;

        /** Retrieving the assayType attributes to use inside the query */
        String measurement = assayType.getMeasurement();
        String technology = assayType.getTechnology();
        String platform = assayType.getPlatform();

        if(queryType.equals("DATA"))
        {
            querySyntax = "SELECT distinct ar " +
				"FROM AssayResult ar join ar.assays a " +
				"WHERE 1=1 ";
        }
        //queryType.equals("STUDY")
        else
        {
            querySyntax = "SELECT distinct s " +
				"FROM Study s , Assay a " +
				"WHERE " +
				"s.id = a.study.id ";
        }

        /* We complete the query if needed */
        // Name
        if(measurement != null && measurement.trim().length() != 0)
            querySyntax += "AND lower(a.measurement.name) = lower('"+measurement.trim().replaceAll("'","''")+"') ";

        // Value
        if(technology != null && technology.trim().length() != 0)
            querySyntax += "AND lower(a.technology.name) = lower('"+technology.trim().replaceAll("'","''")+"') ";

        //Unit
        if(platform != null && platform.trim().length() != 0)
            querySyntax += "AND lower(a.assayPlatform) = lower('"+platform.trim().replaceAll("'","''")+"') ";

        Query query = entityManager.createQuery(querySyntax);

        return query.getResultList();
    }

}
