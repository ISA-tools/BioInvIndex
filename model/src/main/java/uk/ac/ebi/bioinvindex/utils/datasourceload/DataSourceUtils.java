package uk.ac.ebi.bioinvindex.utils.datasourceload;

import uk.ac.ebi.bioinvindex.model.xref.AssayTypeDataLocation;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 23/05/2011
 *         Time: 18:55
 */
public class DataSourceUtils {

    public static Repository resolveRepositoryFromAccession(String accession) {
        // some pre-processing
        accession = accession.toLowerCase().trim();

        for (Repository repository : Repository.values()) {

            if(accession.matches(repository.getStartStringRegEx())) {
                return repository;
            }
        }

        return Repository.GENERIC;
    }

    public static boolean matchForAssayRecord(Repository repository, AssayTypeDataLocation assayTypeDataLocation) {

        if(assayTypeDataLocation.getReferenceSource() == null) {
            return false;
        }

        for(String alias : repository.getAliases()) {
           if(assayTypeDataLocation.getReferenceSource().getName().trim().equalsIgnoreCase(alias.trim())) {
               return true;
           }
        }

        return false;

    }

}
