package uk.ac.ebi.bioinvindex.ws.utils;

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

import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.wsmodel.study.StudyWSReturnImpl;
import uk.ac.ebi.bioinvindex.wsmodel.study.ContactWS;
import uk.ac.ebi.bioinvindex.wsmodel.study.PublicationWS;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This component is used to lighten the model objects into
 * web services oriented objects (study objects). In fact web services clients
 * don't need to know everything but a lighter part of the
 * objects.
 * @author: Manon DELAHAYE [manon@ebi.ac.uk]
 * Date: 31-Mar-2009
 */
public class LighteningStudyProvider {

    /* This is a Singleton instance */
    private static LighteningStudyProvider instance;

    public static LighteningStudyProvider getInstance()
    {
       if (null == instance)
       {
           instance = new LighteningStudyProvider();
       }
       return instance;
    }

    private LighteningStudyProvider() {
    }

    /**
     * This method lightens a study object
     * @param from The initial study from the model
     * @param to The lighter study used for web services
     */
    public void lightenStudy(Study from, StudyWSReturnImpl to)
    {
        to.setAcc(from.getAcc());
        to.setTitle(from.getTitle());

        List<PublicationWS> publications = new ArrayList<PublicationWS>();
        lightenPublicationList(from.getPublications(), publications);
        to.setPublications(publications);

        List<ContactWS> contacts = new ArrayList<ContactWS>();
        lightenContactList(from.getContacts(), contacts);
        to.setContacts(contacts);
    }

    /**
     * This method lightens a list of studies
     * @param from The initial list of studies made with studies from the model
     * @param to The lighter list studies made with the studies used for web services
     */
    public void lightenStudyList(Collection<Study> from, List<StudyWSReturnImpl> to)
    {
        Iterator<Study> it = from.iterator();

        while(it.hasNext())
        {
            StudyWSReturnImpl study = new StudyWSReturnImpl();
            lightenStudy(it.next(), study);
            to.add(study);
        }
    }

    /**
     * This method lightens a publication object
     * @param from The initial publication from the model
     * @param to The lighter publication used for web services
     */
    public void lightenPublication(Publication from, PublicationWS to)
    {
        to.setTitle(from.getTitle());
        to.setAuthorList(from.getAuthorList());
        to.setPmid(from.getPmid());
        to.setDoi(from.getDoi());
    }

    /**
     * This method lightens a list of publications
     * @param from The initial list of publications made with publications from the model
     * @param to The lighter list publications made with the publications used for web services
     */
    public void lightenPublicationList(Collection<Publication> from, List<PublicationWS> to)
    {
        Iterator<Publication> it = from.iterator();

        while(it.hasNext())
        {
            PublicationWS publication = new PublicationWS();
            lightenPublication(it.next(), publication);
            to.add(publication);
        }
    }

    /**
     * This method lightens a contact object
     * @param from The initial contact from the model
     * @param to The lighter contact used for web services
     */
    public void lightenContact(Contact from, ContactWS to)
    {
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setMidInitials(from.getMidInitials());
        to.setEmail(from.getEmail());
        to.setAddress(from.getAddress());
        to.setPhone(from.getPhone());
        to.setFax(from.getFax());
        to.setAffiliation(from.getAffiliation());
        to.setUrl(from.getUrl());
    }

    /**
     * This method lightens a list of contacts
     * @param from The initial list of contacts made with contacts from the model
     * @param to The lighter list contacts made with the contacts used for web services
     */
    public void lightenContactList(Collection<Contact> from, List<ContactWS> to)
    {
        Iterator<Contact> it = from.iterator();

        while(it.hasNext())
        {
            ContactWS contact = new ContactWS();
            lightenContact(it.next(), contact);
            to.add(contact);
        }
    }
}
