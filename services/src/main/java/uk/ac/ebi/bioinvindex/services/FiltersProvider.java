package uk.ac.ebi.bioinvindex.services;

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

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import static org.jboss.seam.ScopeType.SESSION;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.bioinvindex.search.StudyFreeTextSearch;
import uk.ac.ebi.bioinvindex.services.cache.BIICache;
import uk.ac.ebi.bioinvindex.services.cache.Cache;
import uk.ac.ebi.bioinvindex.services.ontologyhandling.Ontology;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: Mar 13, 2008
 */

@Name("filterProvider")
@AutoCreate
@Scope(SESSION)
public class FiltersProvider implements Serializable {

    private static final Cache<String, Collection<String>> cache = new BIICache<String, Collection<String>>();

    private static final Log log = LogFactory.getLog(FiltersProvider.class);

    @In
    private EntityManager entityManager;

    private Collection<String> organisms;
    private Collection<String> assayPlatforms;
    private Collection<String> endPoints;
    private Collection<String> assayTechnologies;

    public Collection<String> getAssayTechnologies(String measurement) {
        try {

            if ((assayTechnologies = cache.find("filters/technologies")) == null) {

                if (measurement == null || measurement.equals("")) {


                    assayTechnologies = entityManager.createQuery("SELECT distinct t.name from AssayTechnology t " +
                            "where t.name is not null")
                            .getResultList();

                } else {
                    assayTechnologies = entityManager.createQuery("SELECT distinct a.technology.name " +
                            "FROM Assay a " +
                            "WHERE a.measurement.name =:endpoint and lower(a.technology.name) is not null")
                            .setParameter("endpoint", measurement)
                            .getResultList();
                }

                cache.attach("filters/technologies", assayTechnologies);
            }
        } catch (Exception e) {
            log.error("Cannot read a list of AssayTechnologies from the DB", e);
            assayTechnologies = new ArrayList<String>(1);
            assayTechnologies.add("Not available");
        }

        return extractNulls(assayTechnologies);
    }


    public Collection<String> getEndPoints() {

        try {
            if ((endPoints = cache.find("filters/endpoints")) == null) {
                endPoints = entityManager.createQuery("SELECT distinct t.name from Measurement t where lower(t.name) is not null")
                        .getResultList();

                cache.attach("filters/endpoints", endPoints);
            }
        } catch (Exception e) {
            log.error("Cannot read a list of EndPoints from the DB", e);
            endPoints = new ArrayList<String>(1);
            endPoints.add("Not available");
        }
        return extractNulls(endPoints);
    }

    public Collection<String> getOrganisms() {
        try {
            if ((organisms = cache.find("filters/organisms")) == null) {
                organisms = entityManager.createQuery("SELECT distinct pv.value " +
                        "FROM PropertyValue pv, Property p " +
                        "WHERE pv.type = p.id and lower(p.value) = 'organism'")
                        .getResultList();

                cache.attach("filters/organisms", organisms);
            }
        } catch (Exception e) {
            log.error("Cannot read a list of Organisms from the DB", e);
            organisms = new ArrayList<String>(1);
            organisms.add("Not available");
        }

        return extractNulls(organisms);
    }

    public Collection<String> getPlatforms() {
        try {
            if ((assayPlatforms = cache.find("filters/assayPlatforms")) == null) {
                assayPlatforms = entityManager.createQuery("SELECT distinct assay.assayPlatform " +
                        "FROM Assay assay")
                        .getResultList();

                cache.attach("filters/assayPlatforms", assayPlatforms);
            }
        } catch (Exception e) {
            log.error("Cannot read a list of Assay Platform from the DB");
            assayPlatforms = new ArrayList<String>(1);
            assayPlatforms.add("Not available");
        }
        return extractNulls(assayPlatforms);
    }


    /**
     * Extracts any null values from the collection so as not to cause any problems in the web interface.
     *
     * @param collection - the Collection to check for Null values in.
     * @return a Collection with no Null values contained within it.
     */
    private Collection<String> extractNulls(Collection<String> collection) {
        Collection<String> toReturn = new ArrayList<String>();
        for (String s : collection) {
            if (s != null && !s.trim().equals("")) {
                toReturn.add(s);
            }
        }
        return toReturn;
    }

    public boolean clearCache() {
        cache.clearCache();
        return true;
    }


}
