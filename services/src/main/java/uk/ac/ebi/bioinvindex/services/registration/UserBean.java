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

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import static org.jboss.seam.ScopeType.SESSION;
import org.jboss.seam.security.digest.DigestUtils;

import javax.persistence.Table;

import uk.ac.ebi.bioinvindex.model.security.Person;

import java.util.Date;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.hibernate.validator.Email;

/**
 * User: Nataliya Sklyar
 * Date: Dec 3, 2008
 */
@Name("user")
@Scope(SESSION)
public class UserBean {

	private Person person;

	public Person getPerson() {
		if (person == null) {
			person = new Person();
		}
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}


	@NotNull
  @Length(max = 100)
	public String getFirstName() {
		return getPerson().getFirstName();
	}

	public void setFirstName(String firstName) {
		getPerson().setFirstName(firstName);
	}

	@NotNull
  @Length(max = 100)
	public String getLastName() {
		return getPerson().getLastName();
	}

	public void setLastName(String lastName) {
		getPerson().setLastName(lastName);
	}

	public void setEmail(String email) {
		getPerson().setEmail(email);
	}

	@NotNull
  @Length(max = 100)
  @Email
	public String getEmail() {
		return getPerson().getEmail();
	}

	 @Length(min=4, max=15)
   @Pattern(regex="^\\w*$", message="not a valid username")
	public String getUserName() {
		return getPerson().getUserName();
	}

	public void setUserName(String userName) {
		getPerson().setUserName(userName);
	}

	@NotNull
   @Length(min=5, max=15)
	public String getPassword() {
		return getPerson().getPassword();
	}

	public void setPassword(String password) {
		getPerson().setPassword(password);
	}

	@NotNull
  @Length(max = 256)
	public String getAddress() {
		return getPerson().getAddress();
	}

	public void setAddress(String address) {
		getPerson().setAddress(address);
	}

	@NotNull
  @Length(max = 256)
	public String getAffiliation() {
		return person.getAffiliation();
	}

	public void setAffiliation(String affiliation) {
		person.setAffiliation(affiliation);
	}

	public Date getJoinDate() {
		return getPerson().getJoinDate();
	}

	public void setJoinDate(Date joinDate) {
		getPerson().setJoinDate(joinDate);
	}



}
