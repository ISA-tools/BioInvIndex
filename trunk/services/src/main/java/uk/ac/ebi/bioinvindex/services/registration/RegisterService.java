package uk.ac.ebi.bioinvindex.services.registration;


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

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;
import uk.ac.ebi.bioinvindex.dao.UserDAO;
import uk.ac.ebi.bioinvindex.utils.StringEncryption;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import java.util.Date;


/**
 * User: Nataliya Sklyar
 * Date: Dec 2, 2008
 */
@Stateful
@Name("register")
public class RegisterService implements Register {

	@In(required = false)
	@Out
	private UserBean user;

	@In
	private UserDAO userDAO;

	@In
	private FacesMessages facesMessages;

	private String verify;

	private boolean registered;

	private static Logger log = Logger.getLogger(RegisterService.class.getName());

	public void register() {
		if (user.getPassword().equals(verify)) {

			if (userDAO.getByUsername(user.getUserName()) == null) {
				user.setJoinDate(new Date());
				user.setPassword(hashPassword(user.getPassword()));
				userDAO.save(user.getPerson());

				facesMessages.add("Successfully registered as #{user.userName}");
				registered = true;
			} else {
				facesMessages.addToControl("username", "Username #{user.userName} already exists");
			}
		} else {
			facesMessages.addToControl("verify", "Re-enter your password");
			verify = null;
		}
	}

	public boolean hasErrors() {

		for (FacesMessage message : facesMessages.getCurrentMessages()) {

			String[] parts = message.getDetail().split(":");
			if (parts.length > 3) {
				message.setSummary(parts[3].substring(0, parts[3].length() - 1) + " for " + parts[1]);
			}
		}

		return facesMessages != null && (facesMessages.getCurrentMessages().size() > 0);
	}

	public void invalid() {
		facesMessages.add("Please try again");
	}

	public boolean isRegistered() {
		return registered;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	@Remove
	public void destroy() {
	}

	public String hashPassword(String pass) {
		return StringEncryption.getInstance().encrypt(pass);
	}
}