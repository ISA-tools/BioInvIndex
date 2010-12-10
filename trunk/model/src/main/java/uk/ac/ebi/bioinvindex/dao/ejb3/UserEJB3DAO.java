package uk.ac.ebi.bioinvindex.dao.ejb3;

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

import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.bioinvindex.dao.UserDAO;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.security.UserRole;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * @author Nataliya Sklyar
 * Date: Dec 2, 2008
 */
public class UserEJB3DAO extends IdentifiableEJB3DAO<User> implements UserDAO {

	public UserEJB3DAO() {
		super(User.class);
	}

	public UserEJB3DAO(EntityManager entityManager) {
		super(User.class, entityManager);
	}

	public User getByUsername(String username) {
		return (User) getSession().createCriteria(User.class)
					.add(Restrictions.eq("userName", username))
					.uniqueResult();
	}

	public boolean isValidCredentials(String username, String hashedPassword) {
		User user = (User) getSession().createCriteria(User.class)
				.add(Restrictions.eq("userName", username))
				.add(Restrictions.eq("password", hashedPassword))
				.uniqueResult();
		return (user != null);
	}

		public boolean isValidCuratorCredentials(String username, String hashedPassword) {
		User user = (User) getSession().createCriteria(User.class)
				.add(Restrictions.eq("userName", username))
				.add(Restrictions.eq("password", hashedPassword))
				.add(Restrictions.eq("role", UserRole.CURATOR))
				.uniqueResult();
		return (user != null);
	}

}
