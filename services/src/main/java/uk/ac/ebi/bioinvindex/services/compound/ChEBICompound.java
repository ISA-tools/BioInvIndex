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

import org.jboss.seam.util.Base64;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Serializable;


/**
 * Object to represent results of ChEBI query for use when creating compound page....
 * @author: eamonnmaguire
 * @date Oct 15, 2008
 */
public class ChEBICompound implements Comparable {
    private static final String CHEBI_URL = "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=";
    private String chebiId;
    private String name;
    private String definitions;
    private String structure;
    private String formula;
    private String imgLoc;

    // we also need information about the related studies!
    private List<String> studyAcc;
    private List<String> organsUsedOn;
    private List<String> organismsUsedOn;

    /**
     * ChEBICompound constructor
     * @param chebiId - ChEBI ID for the compound
     * @param name - Name of compound
     * @param definitions - Definition of the compound
     * @param structure - A String containing a MDL MolFile representation of the Compound structure for later rendering
     * @param formula - The formula representing the compound
     */
    public ChEBICompound(String chebiId, String name, String definitions,
        String structure, String formula, String imgLoc) {
        this.chebiId = chebiId;
        this.name = name;
        this.definitions = definitions;
        this.structure = structure;
        this.formula = formula;
        this.imgLoc = imgLoc;

        organismsUsedOn = new ArrayList<String>();
        organsUsedOn = new ArrayList<String>();
        studyAcc = new ArrayList<String>();
    }

    public void addToOrganismsUsed(String organismUsedOn) {
   
        if (!this.organismsUsedOn.contains(organismUsedOn)) {
            this.organismsUsedOn.add(organismUsedOn);
        }
    }

    public void addToOrgansUsed(String organUsedOn) {

        if (!this.organsUsedOn.contains(organUsedOn)) {
            this.organsUsedOn.add(organUsedOn);
        }
    }

    public int compareTo(Object cc) {
        int result = ((ChEBICompound) cc).getName().toLowerCase()
                      .compareTo(name.toLowerCase());

        if (result < 0) {
            return 1;
        } else if (result > 0) {
            return -1;
        }

        return 0;
    }

    public String getChebiId() {
        return chebiId;
    }

    public static String getChebiUrl() {
        return CHEBI_URL;
    }

    public String getDefinitions() {
        return definitions;
    }

    public String getFormula() {
        return formula;
    }

    public String getName() {
        return name;
    }

    public List<String> getOrganismsUsedOn() {
        return organismsUsedOn;
    }

    public List<String> getOrgansUsedOn() {
        return organsUsedOn;
    }

    public String getStructure() {
        return structure;
    }

    public List<String> getStudyAcc() {
        return studyAcc;
    }

    public String getImgLoc() {
        return imgLoc;
    }



    public void setStudyAcc(List<String> studyAcc) {
        this.studyAcc = studyAcc;
    }
}
