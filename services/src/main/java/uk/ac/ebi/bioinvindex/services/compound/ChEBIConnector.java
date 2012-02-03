package uk.ac.ebi.bioinvindex.services.compound;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;
import uk.ac.ebi.bioinvindex.dao.OntologyEntryDAO;
import uk.ac.ebi.bioinvindex.dao.StudyDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;
import uk.ac.ebi.bioinvindex.services.browse.BrowseBean;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Eamonn Maguire
 * @date Oct 14, 2008
 */
@Name("taxon")
@Scope(ScopeType.PAGE)
@AutoCreate
public class ChEBIConnector implements BrowseBean {

    @In
    private EntityManager entityManager;

    @In
    private Identity identity;

    private static final Log log = LogFactory.getLog(ChEBIConnector.class);

    private OntologyEntryDAO<OntologyTerm> ontologyDAO;

    private StudyDAO studyEJB3DAO;

    private List<ChEBICompound> compoundSearchResults;

    public int getRowCount() {
        try {
            return getCompounds().size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List getItemList() {
        return getCompounds();  //To change body of implemented methods use File | Settings | File Templates.
    }

    private List<ChEBICompound> getCompounds() {
        try {
            if (compoundSearchResults == null) {
                compoundSearchResults = new ArrayList<ChEBICompound>();

                List<OntologyTerm> result = getOntologyDAO().getOntologyEntriesByRefSource("CHEBI");

                for (OntologyTerm ontologyTerm : result) {

                    // need to determine what to search by here since if we have a chebi accession, we can do a more exact
                    // search - quicker!

                    log.info("Getting ChEBI record for : " + ontologyTerm.getAcc());

                    String formula = "";

                    // create the image of the compound using the ChEBICompoundImageCreator helper class
                    //ToDo: Uncomment the following line when needed, fails when deployed on tc-test
                    //                    String compoundImgLoc = "file:///" + ChEBICompoundImageCreator.createImageForCompound(structure.getStructure(), compound);

                    String compoundImgLoc = "file:///";
                    ChEBICompound chebiCompound = new ChEBICompound(ontologyTerm.getAcc(),
                            ontologyTerm.getName(), "",
                            "", formula, compoundImgLoc);

                    List<String> studyAccsForCompound =
                            getStudyEJB3DAO().filterFactorsByOntologyTermAndRefNameForUser(ontologyTerm.getAcc(), "CHEBI", identity.getUsername());


                    chebiCompound.setStudyAcc(studyAccsForCompound);


                    for (String studyAcc : studyAccsForCompound) {

                        List<PropertyValue> organismsUsingCompound = getStudyEJB3DAO().getValuesOfPropertyForStudyAcc(studyAcc,
                                "organism");

                        for (PropertyValue pvi : organismsUsingCompound) {
                            chebiCompound.addToOrganismsUsed(pvi.getValue());
                        }
                    }

                    if (studyAccsForCompound != null && studyAccsForCompound.size() > 0) {
                        compoundSearchResults.add(chebiCompound);
                    }

                }

            }

            log.info("Produced " + compoundSearchResults.size() +
                    "ChEBICompound objects to display in browser :o) ");
            return compoundSearchResults;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ArrayList<ChEBICompound>();
        }

    }

    public OntologyEntryDAO getOntologyDAO() {
        if (ontologyDAO == null) {
            ontologyDAO = DaoFactory.getInstance(entityManager).getOntologyEntryDAO(OntologyTerm.class);
        }
        return ontologyDAO;
    }

    public StudyDAO getStudyEJB3DAO() {
        if (studyEJB3DAO == null) {
            studyEJB3DAO = DaoFactory.getInstance(entityManager).getStudyDAO();
        }
        return studyEJB3DAO;
    }


    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Image> getImages() {
        List<Image> images = new ArrayList<Image>();

        Image i1 = new Image("http://www.googlyfoogly.com/images/AWESOME.jpg", "I'm awesome", "01");
        Image i2 = new Image("http://9.media.tumblr.com/tumblr_kv4399hWWz1qzdr4go1_500.jpg", "I'm awesome too", "02");
        Image i3 = new Image("http://www.googlyfoogly.com/images/AWESOME.jpg", "I'm awesome three", "03");

        images.add(i1);
        images.add(i2);
        images.add(i3);

        return images;
    }
}
