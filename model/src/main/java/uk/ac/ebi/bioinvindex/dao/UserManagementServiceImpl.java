package uk.ac.ebi.bioinvindex.dao;

import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.model.VisibilityStatus;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.utils.StringEncryption;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: Nov 24, 2009
 */
public class UserManagementServiceImpl implements UserManagementService {

	private EntityManager entityManager;

	public UserManagementServiceImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public boolean validateCuratorLogin(String username, String password) {
		UserDAO userDAO = DaoFactory.getInstance(getEntityManager()).getUserDAO();
		return userDAO.isValidCuratorCredentials(username, StringEncryption.getInstance().encrypt(password));
	}

	public List<User> getAllUsers() {
		UserDAO userDAO = DaoFactory.getInstance(getEntityManager()).getUserDAO();
		return userDAO.getAll();
	}

	public void addUserToStudy(String acc, User user) {
		StudyDAO studyDAO = DaoFactory.getInstance(entityManager).getStudyDAO();
		studyDAO.addUserToStudy(acc, user);
	}

	public void removeUserFromStudy(String acc, User user) {
		StudyDAO studyDAO = DaoFactory.getInstance(entityManager).getStudyDAO();
		studyDAO.removeUserFromStudy(acc, user);
	}

	/**
	 * Returns the Users assigned to each Study in the database.
	 * todo could probably be improved in terms of speed by having a direct query to the database.
	 *
	 * @return a map between Study accessions to the Users allowed to access the Studies.
	 */
	public Map<String, List<User>> getUsersAssignedToStudies() {
		List<User> users = getAllUsers();

		Map<String, List<User>> studyToUsers = new HashMap<String, List<User>>();
		StudyDAO studyDAO = DaoFactory.getInstance(entityManager).getStudyDAO();

		for (User u : users) {
			List<String> studyAccs = studyDAO.getOwnedStudiesForUser(u.getUserName());
			for (String study : studyAccs) {
				if (!studyToUsers.containsKey(study)) {
					studyToUsers.put(study, new ArrayList<User>());
				}
				studyToUsers.get(study).add(u);
			}
		}

		return studyToUsers;
	}

	public Map<String, VisibilityStatus> getVisibilityStatusForStudies() {
		StudyDAO studyDAO = DaoFactory.getInstance(entityManager).getStudyDAO();
		return studyDAO.getVisibilityStatusForStudies();
	}

	public void changeStudyStatus(String acc, VisibilityStatus status) {
		StudyDAO studyDAO = DaoFactory.getInstance(entityManager).getStudyDAO();
		studyDAO.changeStudyStatus(acc, status);
	}

	public EntityManager getEntityManager() {
		if (entityManager == null) {
			throw new IllegalStateException("EntityManager has not been set on UserManagementServiceImpl before usage");
		}
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
