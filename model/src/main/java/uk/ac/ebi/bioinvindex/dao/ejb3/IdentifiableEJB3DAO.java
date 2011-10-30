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

import uk.ac.ebi.bioinvindex.dao.IdentifiableDAO;
import uk.ac.ebi.bioinvindex.model.Identifiable;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.util.List;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

/**
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Sep 4, 2007
 * <p/>
 * TODO: In general we should have DO and DomainObjectImpl, otherwise no
 * multiple mapping classes are possible.
 */
public class IdentifiableEJB3DAO<T extends Identifiable>
		extends AbstractEJB3DAO implements IdentifiableDAO<T> {

	protected Class<T> persistentClass;

	protected IdentifiableEJB3DAO() {
	}

	protected IdentifiableEJB3DAO(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	public IdentifiableEJB3DAO(Class<T> persistentClass, EntityManager entityManager) {
		super(entityManager);
		this.persistentClass = persistentClass;
	}

	public Class<T> getPersistentClass() {
		if (persistentClass == null) {
			Type type1 = getClass()
					.getGenericSuperclass();
			Type type = ((ParameterizedType) type1).getActualTypeArguments()[0];
			this.persistentClass = (Class<T>) type;
		}
		return persistentClass;
	}

	@SuppressWarnings("unchecked")
	public T getById(Long id) {
		Class<T> clazz = getPersistentClass();
		return (T) getSession().get(clazz, id);
	}


	public List<T> getAll() {
		return getSession().createCriteria(getPersistentClass()).list();
	}

	public void update(T identifiable) {
		getSession().update(identifiable);
	}

	public Long save(T identifiable) {
		return (Long) getSession().save(identifiable);
	}

	/**
	 * @param id
	 * @return
	 */
	//ToDo: "delete" methods dont work properly for all classes. Problems with references
	public int deleteById(Long id) {
		T o = getById(id);
		if (o == null) {
			return 0;
		}
		delete(o);
		return 1;
	}

	public void delete(T identifiable) {
		getSession().delete(identifiable);
	}


	@SuppressWarnings("unchecked")
	public List<T> getBySubmissionTs ( Timestamp submissionTs )
	{
		return entityManager.createQuery (
			"SELECT e FROM " + getPersistentClass ().getName () + " e WHERE e.submissionTs = :ts" )
			.setParameter ( "ts", submissionTs, TemporalType.TIMESTAMP )
			.getResultList();
	}

}
