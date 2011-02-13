package uk.ac.ebi.bioinvindex.dao;

import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.VisibilityStatus;

import java.util.List;
import java.util.Map;

/**
 * @author: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: Nov 24, 2009
 */
public interface UserManagementService {

	public boolean validateCuratorLogin(String username, String hashedPassword);

	public List<User> getAllUsers();

	public void addUserToStudy(String acc, User user);

	public Map<String, VisibilityStatus> getVisibilityStatusForStudies();

	public void changeStudyStatus(String acc, VisibilityStatus status);

	public void removeUserFromStudy(String acc, User user);

	public Map<String, List<User>> getUsersAssignedToStudies();

}
