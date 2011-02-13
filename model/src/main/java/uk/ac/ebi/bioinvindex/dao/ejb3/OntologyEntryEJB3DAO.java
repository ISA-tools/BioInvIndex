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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.bioinvindex.dao.OntologyEntryDAO;
import uk.ac.ebi.bioinvindex.model.term.Measurement;
import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author nsklyar
 */
public class OntologyEntryEJB3DAO<T extends OntologyEntry>
		extends AnnotatableEJB3DAO<T>
		implements OntologyEntryDAO<T> {

	private static final Log log = LogFactory.getLog(OntologyEntryEJB3DAO.class);

	public OntologyEntryEJB3DAO() {
		super();
	}

	protected OntologyEntryEJB3DAO(Class<T> persistentClass) {
		super(persistentClass);
	}

	public OntologyEntryEJB3DAO(Class<T> persistentClass, EntityManager entityManager) {
		super(persistentClass, entityManager);
	}

		public T getByAcc(String acc) {
		Class<T> clazz = getPersistentClass();
		return (T) getSession().createCriteria(clazz)
					.add(Restrictions.eq("acc", acc))
					.uniqueResult();
	}

	/**
	 * @param acc
	 * @return
	 */
	public T getOntologyEntryByAcc(String acc) {
		return (T) getSession().createCriteria(getPersistentClass())
				.add(Restrictions.eq("acc", acc))
				.uniqueResult();
	}

	public T getOntologyEntryByAccAndRefSource( String acc, String refSourceAcc) {
		return (T) getSession().createCriteria(getPersistentClass())
				.add(Restrictions.eq("acc", acc))
				.createCriteria("source")
				.add(Restrictions.eq("acc", refSourceAcc))
				.uniqueResult();
	}

	public T getByNameAndRefSource(String name, String refSourceAcc) {
		return (T) getSession().createCriteria(getPersistentClass())
				.add(Restrictions.eq("name", name))
				.createCriteria("source")
				.add(Restrictions.eq("acc", refSourceAcc))
				.uniqueResult();

	}

	public List<T> getOntologyEntriesByRefSource(String refSourceName) {
		List result = getSession().createCriteria(getPersistentClass())
				.createCriteria("source")
				.add(Restrictions.eq("name", refSourceName).ignoreCase())
				.list();
		return result;
	}

	public List<String> getOntologyEntryAccsByRefSource(String refSourceAcc) {
		return entityManager.createQuery("SELECT distinct ot.acc " +
				"FROM  OntologyEntry ot " +
				"WHERE lower(ot.source.acc) = lower(:refSourceName)")
				.setParameter("refSourceName", refSourceAcc)
				.getResultList();
	}
}
