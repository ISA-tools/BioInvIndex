package uk.ac.ebi.bioinvindex.utils.datasourceload;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 23/05/2011
 *         Time: 19:13
 */
public enum Repository {

    PRIDE("^[0-9]+","pride"), ARRAYEXPRESS("^(e-).*", "arrayexpress"),  GEO("^[(gse)|(ges)]*[0-9]+", "geo"),
    ENA("^[(sra)|(ena)]*[0-9]+","embl-bank", "ena", "ebi/ena"), GENERIC("generic", "generic", "none", "generic proteomic location", "generic microarray location");


    private String startString;
    private String[] aliases;

    Repository(String startString, String... aliases) {
        this.aliases = aliases;
        this.startString = startString;
    }

    public String getStartStringRegEx() {
        return startString;
    }

    public String[] getAliases() {
        return aliases;
    }
}