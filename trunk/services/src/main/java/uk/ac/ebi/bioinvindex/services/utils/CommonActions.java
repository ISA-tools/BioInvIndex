package uk.ac.ebi.bioinvindex.services.utils;

import org.jboss.seam.security.Identity;
import uk.ac.ebi.bioinvindex.model.Investigation;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.VisibilityStatus;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;

import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 11/01/2011
 *         Time: 16:52
 */
public class CommonActions {

    /**
     * This method checks a Study and whether or not it should be visible to the currently logged in user.
     *
     * @param study - Study to check
     * @return boolean indicating whether or not a user can view the study
     */
    public static boolean canCurrentUserViewStudy(Study study, Identity identity) {
        if (study != null) {

            if (study.getStatus() == VisibilityStatus.PUBLIC) {
                return true;
            }

            Collection<User> allowedUsers = study.getUsers();
            if (identity != null) {
                String currentUser = identity.getUsername();
                for (User user : allowedUsers) {
                    if (user.getUserName().equals(currentUser)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String buildValuesString(Collection<PropertyValue> values) {
        Set<String> filteredValues = new HashSet<String>(values.size());
        StringBuilder sb = new StringBuilder();
        for (PropertyValue value : values) {
            if (!filteredValues.contains(value.getValue())) {
                filteredValues.add(value.getValue());
                sb.append(value.getValue());
                if (value.getUnit() != null) {
                    sb.append(value.getUnit().getValue());
                }
                sb.append(", ");
            }

        }

        return StringFormating.removeLastComma(sb.toString());
    }


}
