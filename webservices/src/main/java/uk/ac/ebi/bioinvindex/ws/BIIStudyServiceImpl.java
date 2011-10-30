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

import org.jboss.seam.Component;
import uk.ac.ebi.bioinvindex.ws.utils.ComponentProvider;
import uk.ac.ebi.bioinvindex.ws.utils.LighteningStudyProvider;
import uk.ac.ebi.bioinvindex.wsmodel.study.StudyWSReturnImpl;
import uk.ac.ebi.bioinvindex.wsmodel.study.ContactWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.AssayTypeWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.PropertyValuesWS;
import uk.ac.ebi.bioinvindex.wsmodel.utils.QueryObject;
import uk.ac.ebi.bioinvindex.dao.ejb3.StudyEJB3DAO;
import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Study;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.*;

/**
 * @author: Manon DELAHAYE [manon@ebi.ac.uk]
 * Date: Mar 23, 2009
 */

@WebService
@XmlSeeAlso({StudyWSReturnImpl.class})
public class BIIStudyServiceImpl implements BIIStudyService
{

	@WebMethod
	public List<String> getAllStudyAccs()
    {
		/* This was used with fake studies in order to use the indexes. We should implement it later on */
        /*StudyFreeTextSearch studyFreeTextSearch = componentProvider.getStudySearch();
        List<DataType> accNumbers = studyFreeTextSearch.getStudyAccs(new StudyBIIFilterQuery());*/

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();

        List<String> accNumbers = studyDAO.getAllStudyAccs();

        return accNumbers;
	}

    @WebMethod
    /* Use Case 1 */
    public StudyWSReturnImpl getStudyByAcc(String acc)
    {
        LighteningStudyProvider lighteningStudyProvider = LighteningStudyProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();

        StudyWSReturnImpl studyWS = new StudyWSReturnImpl();
        lighteningStudyProvider.lightenStudy(studyDAO.getByAcc(acc), studyWS);

        return studyWS;
    }

//    @WebMethod
    /* Use Case 2 */
    // TODO it doesn't work
//    public List<StudyWSReturnImpl> getStudiesByContact(ContactWS contact)
//    {
//        LighteningStudyProvider lighteningStudyProvider = LighteningStudyProvider.getInstance();
//
//        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
//        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();
//
//        List<StudyWSReturnImpl> studiesWS = new ArrayList<StudyWSReturnImpl>();
//
//        List<Study> studies = studyDAO.getStudiesByContact(new Contact(contact.getFirstName(), contact.getMidInitials(), contact.getLastName(), contact.getEmail()));
//
//        lighteningStudyProvider.lightenStudyList(studies, studiesWS);
//
//        return studiesWS;
//    }

    @WebMethod
    /* Use Case 3 */
    public List<StudyWSReturnImpl> getStudiesByContactLastName(String lastName)
    {
        LighteningStudyProvider lighteningStudyProvider = LighteningStudyProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();

        List<StudyWSReturnImpl> studiesWS = new ArrayList<StudyWSReturnImpl>();

        List<Study> studies = studyDAO.getStudiesByContactLastName(lastName);

        lighteningStudyProvider.lightenStudyList(studies, studiesWS);

        return studiesWS;
    }

    @WebMethod
    /* Use Case 4 */
    public List<StudyWSReturnImpl> getStudiesByContactEmail(String email)
    {
        LighteningStudyProvider lighteningStudyProvider = LighteningStudyProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();

        List<StudyWSReturnImpl> studiesWS = new ArrayList<StudyWSReturnImpl>();

        List<Study> studies = studyDAO.getStudiesByContactEmail(email);

        lighteningStudyProvider.lightenStudyList(studies, studiesWS);

        return studiesWS;
    }

    @WebMethod
    /* Use Case 5 */
    public List<StudyWSReturnImpl> getStudiesByContactAffiliation(String affiliation)
    {
        LighteningStudyProvider lighteningStudyProvider = LighteningStudyProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();

        List<StudyWSReturnImpl> studiesWS = new ArrayList<StudyWSReturnImpl>();

        List<Study> studies = studyDAO.getStudiesByContactAffiliation(affiliation);

        lighteningStudyProvider.lightenStudyList(studies, studiesWS);

        return studiesWS;
    }

    @WebMethod
    /* Use Case 6 */
    public List<StudyWSReturnImpl> getStudiesByPubmedIdOrDoi(String id)
    {
        LighteningStudyProvider lighteningStudyProvider = LighteningStudyProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();

        List<StudyWSReturnImpl> studiesWS = new ArrayList<StudyWSReturnImpl>();

        List<Study> studies = studyDAO.getStudiesByPubmedIdOrDoi(id);

        lighteningStudyProvider.lightenStudyList(studies, studiesWS);

        return studiesWS;
    }

    @WebMethod
    public List<StudyWSReturnImpl> getStudiesByProperty(PropertyValuesWS property)
    {
        LighteningStudyProvider lighteningStudyProvider = LighteningStudyProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();

        List<StudyWSReturnImpl> studiesWS = new ArrayList<StudyWSReturnImpl>();

        List<Study> studies = studyDAO.getStudiesByProperty(property);

        lighteningStudyProvider.lightenStudyList(studies, studiesWS);

        return studiesWS;
    }

    @WebMethod
    public List<StudyWSReturnImpl> getStudiesByAssayType(AssayTypeWS assayType)
    {
        LighteningStudyProvider lighteningStudyProvider = LighteningStudyProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();

        List<StudyWSReturnImpl> studiesWS = new ArrayList<StudyWSReturnImpl>();

        List<Study> studies = studyDAO.getStudiesByAssayType(assayType);

        lighteningStudyProvider.lightenStudyList(studies, studiesWS);

        return studiesWS;
    }

    @WebMethod
    public List<StudyWSReturnImpl> getStudiesByPropertiesAndAssayType(QueryObject queryObject) {

        return findAssayResultInCommon(queryObject);
    }

    public List<StudyWSReturnImpl> findAssayResultInCommon(QueryObject queryObject)
    {
        LighteningStudyProvider lighteningStudyProvider = LighteningStudyProvider.getInstance();

        ComponentProvider componentProvider = (ComponentProvider) Component.getInstance(ComponentProvider.class, true);
        StudyEJB3DAO studyDAO = componentProvider.getStudyEJB3DAO();

        List<StudyWSReturnImpl> studies = new ArrayList<StudyWSReturnImpl>();

        List<Study> list;
        List<Study> wholeList = new ArrayList<Study>();

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

            list = studyDAO.getStudiesByProperty(property);

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

            list = studyDAO.getStudiesByAssayType(assayType);

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
            lighteningStudyProvider.lightenStudyList(wholeList,studies);
        }

        return studies;

    }
}