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

import uk.ac.ebi.bioinvindex.dao.BIIDAOException;
import uk.ac.ebi.bioinvindex.dao.StudyDAO;
import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.VisibilityStatus;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.term.*;
import uk.ac.ebi.bioinvindex.wsmodel.data.AssayTypeWS;
import uk.ac.ebi.bioinvindex.wsmodel.data.PropertyValuesWS;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

/**
 * 
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: Jun 22, 2007
 */
public class StudyEJB3DAO extends AccessibleEJB3DAO<Study> implements StudyDAO {

	public StudyEJB3DAO() {
		super(Study.class);
	}

	public StudyEJB3DAO(EntityManager entityManager) {
		super(Study.class, entityManager);
	}

	/**
	 * Extracts a List of FactorValues for a Study with a given id.
	 *
	 * @param studyId
	 *
	 * @return a List of FactorValues
	 */
	public List<Property<FactorValue>> getFactorsForStudy(Long studyId) {

		Query query = entityManager.createQuery("SELECT distinct p " +
				"FROM AssayResult ar join ar.cascadedPropertyValues pv, Factor p " +
				"WHERE " +
				"ar.study.id =:studyId " +
				"AND pv.type = p.id ")
				.setParameter("studyId", studyId);


		return query.getResultList();
	}

	/**
	 * Extracts a List of FactorVaCharacteristicValueImpls for a Study with a given id.
	 *
	 * @param studyId
	 *
	 * @return a List of CharacteristicValue
	 */
	public List<Property<CharacteristicValue>> getCharacteristicsForStudy(Long studyId) {

		Query query = entityManager.createQuery("SELECT distinct p " +
				"FROM AssayResult ar join ar.cascadedPropertyValues pv, Characteristic p " +
				"WHERE " +
				"ar.study.id =:studyId " +
				"AND pv.type = p.id ")
				.setParameter("studyId", studyId);




		return query.getResultList();
	}

	/**
	 * Extracts a List of PropertyValueImpls for a Property with a given <code>name</code> in Study with a given
	 * <code>studyId</code>
	 *
	 * @param studyId
	 * @param propertyName
	 *
	 * @return
	 */
	public List<PropertyValue> getValuesOfPropertyForStudyId(Long studyId, String propertyName) {

		return entityManager.createQuery("SELECT distinct pv " +
				"FROM AssayResult ar join ar.cascadedPropertyValues pv, Property p " +
				"WHERE " +
				"ar.study.id =:studyId " +
				"AND pv.type = p.id " +
				"AND lower(p.value) like lower(:name)")
				.setParameter("studyId", studyId)
				.setParameter("name", propertyName)
				.getResultList();
	}

	/**
	 * Extracts a List of PropertyValueImpls for a Property with a given <code>name</code> in Study with a given
	 * <code>studyAcc</code>
	 *
	 * @param studyAcc     - Study accession
	 * @param propertyName
	 *
	 * @return
	 */
	public List<PropertyValue> getValuesOfPropertyForStudyAcc(String studyAcc, String propertyName) {

		return entityManager.createQuery("SELECT distinct pv " +
				"FROM AssayResult ar join ar.cascadedPropertyValues pv, Property p " +
				"WHERE " +
				"ar.study.acc =:studyId " +
				"AND pv.type = p.id " +
				"AND lower(p.value) like lower(:name)")
				.setParameter("studyId", studyAcc)
				.setParameter("name", propertyName)
				.getResultList();
	}

	/**
	 * Extracts a List of accessions of Studies which contain OntologyTerm with accession <code>termAcc</code> from a
	 * resource with accession <code>refSourceAcc</code>.
	 *
	 * @param termAcc
	 * @param refSourceAcc
	 *
	 * @return
	 */
	public List<String> filterByOntologyTermAndRefAcc(String termAcc, String refSourceAcc) {
		return entityManager.createQuery("SELECT distinct study.acc " +
				"FROM Study study, AssayResult ar join ar.cascadedPropertyValues pv join pv.ontologyTerms ot " +
				"WHERE " +
				"study.id = ar.study.id " +
				"AND ot.acc =:termAcc " +
				"AND lower(ot.source.acc) = lower(:refSourceName)")
				.setParameter("termAcc", termAcc)
				.setParameter("refSourceName", refSourceAcc)
				.getResultList();
	}


	public List<String> filterByOntologyTermAndRefName(String termAcc, String refSourceName) {
		return entityManager.createQuery("SELECT distinct study.acc " +
				"FROM Study study, AssayResult ar join ar.cascadedPropertyValues pv join pv.ontologyTerms ot " +
				"WHERE " +
				"study.id = ar.study.id " +
				"AND ot.acc =:termAcc " +
				"AND lower(ot.source.name) = lower(:refSourceName)")
				.setParameter("termAcc", termAcc)
				.setParameter("refSourceName", refSourceName)
				.getResultList();
	}

	public List<String> filterFactorsByOntologyTermAndRefName(String termAcc, String refSourceName) {
		return entityManager.createQuery("SELECT distinct study.acc " +
				"FROM Study study, AssayResult ar join ar.cascadedPropertyValues pv join pv.ontologyTerms ot " +
				"WHERE " +
				"study.id = ar.study.id " +
				"AND pv.type.role =:role " +
				"AND ot.acc =:termAcc " +
				"AND lower(ot.source.name) = lower(:refSourceName)")
				.setParameter("role", PropertyRole.FACTOR)
				.setParameter("termAcc", termAcc)
				.setParameter("refSourceName", refSourceName)
				.getResultList();
	}

	public List<String> filterFactorsByOntologyTermAndRefNameForUser(String termAcc, String refSourceName, String userName) {
		return entityManager.createQuery("SELECT distinct study.acc " +
				"FROM Study study join study.users user, AssayResult ar join ar.cascadedPropertyValues pv join pv.ontologyTerms ot " +
				"WHERE " +
				"study.id = ar.study.id " +
				"AND pv.type.role =:role " +
				"AND ot.acc =:termAcc " +
				"AND lower(ot.source.name) = lower(:refSourceName)" +
				"AND " +
				"(user.userName =:userName OR " +
				"study.status =:status)"
		)
				.setParameter("role", PropertyRole.FACTOR)
				.setParameter("termAcc", termAcc)
				.setParameter("refSourceName", refSourceName)
				.setParameter("status", VisibilityStatus.PUBLIC)
				.setParameter("userName", userName)
				.getResultList();
	}

	public boolean studyExists(String acc) {
		List list = entityManager.createQuery("SELECT distinct study.id FROM Study study where study.acc =:acc")
				.setParameter("acc", acc).getResultList();
		return list.size() > 0;
	}
	// Manon's test
	/*public Study getStudyByAcc(String acc) {


		Query query = entityManager.createQuery("SELECT distinct s " +
				"FROM Study s " +
				"WHERE " +
				"s.acc =:acc ")
				.setParameter("acc", acc);
		return (Study)query.getSingleResult();
	}*/

	/**
	 * Retrieve the accession numbers of the existing studies
	 *
	 * @return The list of the studies' accession numbers
	 */
	public List<String> getAllStudyAccs() {
		Query query = entityManager.createQuery("SELECT s.acc " +
				"FROM Study s ");

		return query.getResultList();
	}

	/**
	 * Retrieve the list of studies which have this contact in their contact list.
	 *
	 * @param contact The contact
	 *
	 * @return The list of the studies having this contact in their contact list.
	 */
	public List<Study> getStudiesByContact(Contact contact) {
		String querySyntax = "SELECT distinct s " +
				"FROM Study s , Contact c " +
				"WHERE " +
				"s.id = c.study.id ";

		if (contact.getFirstName() != null) {
			querySyntax += "AND lower(c.firstName) = lower(:firstName)";
		}
		if (contact.getLastName() != null) {
			querySyntax += "AND lower(c.lastName) = lower(:lastName)";
		}
		if (contact.getMidInitials() != null) {
			querySyntax += "AND lower(c.midInitials) = lower(:midInitials)";
		}
		if (contact.getEmail() != null) {
			querySyntax += "AND lower(c.email) = lower(:email)";
		}

		Query query = entityManager.createQuery(querySyntax);

		if (contact.getFirstName() != null) {
			query.setParameter("firstName", contact.getFirstName());
		}
		if (contact.getLastName() != null) {
			query.setParameter("lastName", contact.getLastName());
		}
		if (contact.getMidInitials() != null) {
			query.setParameter("midInitials", contact.getMidInitials());
		}
		if (contact.getEmail() != null) {
			query.setParameter("email", contact.getEmail());
		}

		return query.getResultList();
	}

	/**
	 * Retrieve the list of studies which have a contact with this last name in their contact list.
	 *
	 * @param lastName The last name of the contact
	 *
	 * @return The list of the studies having this contact in their contact list.
	 */
	public List<Study> getStudiesByContactLastName(String lastName) {
		//SELECT * FROM STUDY s, CONTACT c WHERE s.ID  = c.STUDY_ID AND C.LASTNAME = 'lastName'

		Query query = entityManager.createQuery("SELECT distinct s " +
				"FROM Study s , Contact c " +
				"WHERE " +
				"s.id = c.study.id " +
				"AND lower(c.lastName) = lower(:lastName)")
				.setParameter("lastName", lastName);

		return query.getResultList();
	}

	/**
	 * Retrieve the list of studies which have a contact with this email in their contact list.
	 *
	 * @param email The email of the contact
	 *
	 * @return The list of the studies having this contact in their contact list.
	 */
	public List<Study> getStudiesByContactEmail(String email) {
		Query query = entityManager.createQuery("SELECT distinct s " +
				"FROM Study s , Contact c " +
				"WHERE " +
				"s.id = c.study.id " +
				"AND lower(c.email) = lower(:email)")
				.setParameter("email", email);

		return query.getResultList();
	}

	/**
	 * Retrieve the list of studies which have a contact with this affiliation in their contact list.
	 *
	 * @param affiliation The affiliation of the contact
	 *
	 * @return The list of the studies having this contact in their contact list.
	 */
	public List<Study> getStudiesByContactAffiliation(String affiliation) {
		Query query = entityManager.createQuery("SELECT distinct s " +
				"FROM Study s , Contact c " +
				"WHERE " +
				"s.id = c.study.id " +
				"AND lower(c.affiliation) = lower(:affiliation)")
				.setParameter("affiliation", affiliation);

		return query.getResultList();
	}

	/**
	 * Retrieve the list of studies which have a publication with this pubmed id in their publications list.
	 *
	 * @param id The pubmed id of the publication
	 *
	 * @return The list of the studies having this publication in their publications list.
	 */
	public List<Study> getStudiesByPubmedIdOrDoi(String id) {
		Query query = entityManager.createQuery("SELECT distinct s " +
				"FROM Study s , Publication p " +
				"WHERE " +
				"s.id = p.study.id " +
				"AND (lower(p.pmid) = lower(:id)" +
				"OR lower(p.doi) = lower(:id))")
				.setParameter("id", id);

		return query.getResultList();
	}

	/**
	 * Retrieve the study containing in their properties list a property which looks like the one given.
	 *
	 * @param property The property
	 *
	 * @return The list of the studyImpl retrieved by the query
	 */
	public List<Study> getStudiesByProperty(PropertyValuesWS property) {
		return (List<Study>) super.getByProperty("STUDY", property);
	}

	/**
	 * Retrieve the study where the assays' types look like the one given.
	 *
	 * @param assayType The assayType
	 *
	 * @return The list of the studies retrieved by the query
	 */
	public List<Study> getStudiesByAssayType(AssayTypeWS assayType) {
		return (List<Study>) super.getByAssayType("STUDY", assayType);
	}

	public List<String> getPublicStudyAccs() {
		Query query = entityManager.createQuery("SELECT distinct s.acc " +
				"FROM Study s " +
				"WHERE " +
				"s.status =:status")
				.setParameter("status", VisibilityStatus.PUBLIC);

		return query.getResultList();

	}

	/**
	 * Returns all Study accessions beloning to the user with a given user name and all studies with PUBLIC status.
	 *
	 * @param userName
	 *
	 * @return
	 */
	public List<String> getStudyAccForUser(String userName) {

		//ToDo: problem with this query: returns only those Studies which have a user attached, if a Study is public
		//but doesn't have attached user, it will not be returned
		Query query = entityManager.createQuery("SELECT distinct s.acc " +
				"FROM Study s join s.users user " +
				"WHERE " +
				"user.userName =:userName OR " +
				"s.status =:status")
				.setParameter("status", VisibilityStatus.PUBLIC)
				.setParameter("userName", userName);

		return query.getResultList();
	}

	/**
	 * Returns all Study accessions beloning to the user with a given user name ONLY.
	 *
	 * @param userName
	 *
	 * @return
	 */
	public List<String> getOwnedStudiesForUser(String userName) {

		//ToDo: problem with this query: returns only those Studies which have a user attached, if a Study is public
		//but doesn't have attached user, it will not be returned
		Query query = entityManager.createQuery("SELECT distinct s.acc " +
				"FROM Study s join s.users user " +
				"WHERE " +
				"user.userName =:userName")
				.setParameter("userName", userName);

		return query.getResultList();
	}

	public Study getByAccForUser(String acc, String userName) {

		Query query = entityManager.createQuery("SELECT s " +
				"FROM Study s join s.users user " +
				"WHERE " +
				"s.acc =:acc AND (user.userName =:userName"
				+ " OR s.status =:status)"
		)
				.setParameter("acc", acc)
				.setParameter("status", VisibilityStatus.PUBLIC)
				.setParameter("userName", userName);

		Study study;
		try {
			study = (Study) query.getSingleResult();
		} catch (NoResultException e) {
			throw new BIIDAOException("Study with acc " + acc + " doesn't exist for user " + userName, e);
		}

		return study;
	}

	public void changeStudyStatus(String acc, VisibilityStatus status) {
		Query query = entityManager.createQuery("UPDATE Study s SET " +
				"s.status =:status " +
				"WHERE s.acc =:acc")
				.setParameter("status", status)
				.setParameter("acc", acc);

		int updatedStudies = query.executeUpdate();
	}

	/**
	 * Creates a Map of study.acc to its visibility status
	 *
	 * @return
	 */
	public Map<String, VisibilityStatus> getVisibilityStatusForStudies() {

		Query query = entityManager.createQuery("SELECT distinct s.acc, s.status " +
				"FROM Study s");

		List list = query.getResultList();
		Map<String, VisibilityStatus> answer = new HashMap<String, VisibilityStatus>(list.size());

		for (Object aList : list) {
			Object[] row = (Object[]) aList;
			if (row.length == 2) {
				answer.put((String) row[0], (VisibilityStatus) row[1]);
			}
		}
		return answer;
	}

	public void addUserToStudy(String acc, User user) {
		Study study = getByAcc(acc);
		study.addUser(user);

		entityManager.persist(study);
	}

	public void removeUserFromStudy(String acc, User user) {
		Study study = getByAcc(acc);
		if(study.getUsers().contains(user)) {
			study.removerUser(user);
		}
		entityManager.persist(study);
	}
}
