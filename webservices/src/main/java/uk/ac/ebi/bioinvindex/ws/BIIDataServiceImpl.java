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
import uk.ac.ebi.bioinvindex.ws.utils.ComponentProvider;
import uk.ac.ebi.bioinvindex.ws.utils.LighteningDataProvider;
import uk.ac.ebi.bioinvindex.wsmodel.utils.QueryObject;
import uk.ac.ebi.bioinvindex.dao.ejb3.DataEJB3DAO;
import uk.ac.ebi.bioinvindex.model.AssayResult;

import javax.jws.WebService;
import javax.jws.WebMethod;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.jboss.seam.Component;

/**
 * @author: Manon DELAHAYE [manon@ebi.ac.uk]
 * Date: 07-Apr-2009
 */
@WebService
public class BIIDataServiceImpl implements BIIDataService {

    @WebMethod
    public List<DataWS> getDataByProperty(PropertyValuesWS property)
    {
        LighteningDataProvider lighteningDataProvider = LighteningDataProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        DataEJB3DAO dataDAO = componentProvider.getDataEJB3DAO();

        List<AssayResult> list = dataDAO.getAssayResultByProperty(property);

        /* We create the list of DataWS objects from the assay results we retrieved */
        List<DataWS> data = new ArrayList<DataWS>();
        lighteningDataProvider.createDataWSListFromAssayResultList(list,data);

        return data;
    }

    @WebMethod
    public List<DataWS> getDataByAssayType(AssayTypeWS assayType)
    {
		LighteningDataProvider lighteningDataProvider = LighteningDataProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        DataEJB3DAO dataDAO = componentProvider.getDataEJB3DAO();

        List<AssayResult> list = dataDAO.getAssayResultByAssayType(assayType);

        /* We create the list of DataWS objects from the assay results we retrieved */
        List<DataWS> data = new ArrayList<DataWS>();
        lighteningDataProvider.createDataWSListFromAssayResultList(list,data);

        return data;
    }

    @WebMethod
    public List<DataWS> getDataByPropertiesAndAssayType(QueryObject queryObject)
    {
        return findAssayResultInCommon(queryObject);
    }

    @WebMethod
    public List<DataWS> getDataByStudyAcc(String studyAcc)
    {
        LighteningDataProvider lighteningDataProvider = LighteningDataProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        DataEJB3DAO dataDAO = componentProvider.getDataEJB3DAO();

        List<AssayResult> list = dataDAO.getAssayResultByStudyAcc(studyAcc);

        /* We create the list of DataWS objects from the assay results we retrieved */
        List<DataWS> data = new ArrayList<DataWS>();
        lighteningDataProvider.createDataWSListFromAssayResultList(list,data);

        return data;
    }

    public List<DataWS> findAssayResultInCommon(QueryObject queryObject)
    {
        LighteningDataProvider lighteningDataProvider = LighteningDataProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        DataEJB3DAO dataDAO = componentProvider.getDataEJB3DAO();

        List<DataWS> data = new ArrayList<DataWS>();
        List<AssayResult> list;
        List<AssayResult> wholeList = new ArrayList<AssayResult>();

        /**
         * Iteration of the properties list
         *
         * We ask for the assay results of the first property. We then have a list.
         * We ask for the second property. We only keep the ones in common with the list obtained from the first property.
         * Etc until the list is empty or there is no more property to iterate
         */
        boolean firstTime = true;
        List<PropertyValuesWS> properties = queryObject.getProperties();
        Iterator it = properties.iterator();
        while(it.hasNext())
        {
            PropertyValuesWS property = (PropertyValuesWS)it.next();

            list = dataDAO.getAssayResultByProperty(property);

//            String finTexte = " / First time : "+firstTime;
            if(firstTime)
            {
                wholeList = list;
                firstTime = false;
            }
            else
                    wholeList.retainAll(list);

//            System.out.println("P Taille liste : "+list.size()+ " / Taille wholeList : "+wholeList.size() + finTexte);

            /* If they have no AssayResult in common, then we don't go on iterating */
            if(wholeList.size() == 0)
                break;
        }

        /**
         * Iteration of the assayTypes
         *
         * Same algorithm for assay types.
         */
        firstTime = true;
        List<AssayTypeWS> assayTypes = queryObject.getAssayTypes();
        it = assayTypes.iterator();
        while(it.hasNext())
        {
            AssayTypeWS assayType = (AssayTypeWS)it.next();

            list = dataDAO.getAssayResultByAssayType(assayType);

//            String finTexte = " / First time : "+firstTime;
            if(firstTime && wholeList.size() == 0)
            {
                wholeList = list;
                firstTime = false;
            }
            else
                wholeList.retainAll(list);

//            System.out.println("A Taille liste : "+list.size()+ " / Taille wholeList : "+wholeList.size() + finTexte);

            if(wholeList.size() == 0)
                break;
        }

        if(wholeList.size() != 0)
        {
            lighteningDataProvider.createDataWSListFromAssayResultList(wholeList,data);
        }

        return data;

    }
}
