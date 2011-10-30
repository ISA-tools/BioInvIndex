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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.bioinvindex.dao.AccessibleDAO;
import uk.ac.ebi.bioinvindex.dao.AnnotatableDAO;
import uk.ac.ebi.bioinvindex.dao.ContactDao;
import uk.ac.ebi.bioinvindex.dao.FreeTextTermDAO;
import uk.ac.ebi.bioinvindex.dao.IdentifiableDAO;
import uk.ac.ebi.bioinvindex.dao.InvestigationDao;
import uk.ac.ebi.bioinvindex.dao.OntologyEntryDAO;
import uk.ac.ebi.bioinvindex.dao.ReferenceSourceDAO;
import uk.ac.ebi.bioinvindex.dao.StudyDAO;
import uk.ac.ebi.bioinvindex.dao.UserDAO;
import uk.ac.ebi.bioinvindex.model.Accessible;
import uk.ac.ebi.bioinvindex.model.Annotatable;
import uk.ac.ebi.bioinvindex.model.Identifiable;
import uk.ac.ebi.bioinvindex.model.term.FreeTextTerm;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;

import javax.persistence.EntityManager;

/**
 * A singleton factory class to create DAO's instances
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Jan 15, 2008
 *
 */
public class DaoFactory {

	private static final Log log = LogFactory.getLog( DaoFactory.class );

	private EntityManager entityManager;

	protected DaoFactory(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public static DaoFactory getInstance(EntityManager entityManager) {
		return new DaoFactory(entityManager);
	}

	// TODO: is it correct that we provide a DAO for such generic things?

	public AnnotatableDAO<Annotatable> getAnnotatableDAO () {
		return getAnnotatableDAO ( Annotatable.class );
	}

	public <T extends Annotatable> AnnotatableDAO<T> getAnnotatableDAO ( Class<T> termClass ) {
		return new AnnotatableEJB3DAO<T> ( termClass, entityManager );
	}

	public AccessibleDAO<Accessible> getAccessibleDAO () {
		return getAccessibleDAO ( Accessible.class );
	}

	public <T extends Accessible> AccessibleDAO<T> getAccessibleDAO ( Class<T> accessibleClass ) {
		return new AccessibleEJB3DAO<T> ( accessibleClass, entityManager );
	}

	public FreeTextTermDAO<FreeTextTerm> getFreeTextTermDAO () {
		return getFreeTextTermDAO ( FreeTextTerm.class );
	}

	public <FT extends FreeTextTerm> FreeTextTermDAO<FT> getFreeTextTermDAO ( Class<FT> ftClass ) {
		return new FreeTextTermEJBDAO<FT> ( ftClass, entityManager );
	}


	public IdentifiableDAO<Identifiable> getIdentifiableDAO () {
		return getIdentifiableDAO ( Identifiable.class );
	}

	public <T extends Identifiable> IdentifiableDAO<T> getIdentifiableDAO ( Class<T> identifiableClass ) {
		return new IdentifiableEJB3DAO<T> ( identifiableClass, entityManager );
	}

	public StudyDAO getStudyDAO() {
		return new StudyEJB3DAO(entityManager);
	}

	public OntologyEntryDAO<OntologyEntry> getOntologyEntryDAO() {
		return new OntologyEntryEJB3DAO<OntologyEntry>(OntologyEntry.class, entityManager);
	}

	public <T extends OntologyEntry> OntologyEntryDAO<T> getOntologyEntryDAO(Class<T> type) {
		return new OntologyEntryEJB3DAO<T>(type, entityManager);
	}

	public InvestigationDao getInvestigationDao() {
		return new InvestigationEJB3DAO(entityManager);
	}

	public ReferenceSourceDAO getReferenceSourceDAO(){
		return new ReferenceSourceEJB3DAO(entityManager);
	}

	public ContactDao getContactDao() {
		return new ContactEJB3DAO(entityManager);
	}

	public UserDAO getUserDAO() {
		return new UserEJB3DAO(entityManager);
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}


}
