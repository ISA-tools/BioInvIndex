package uk.ac.ebi.bioinvindex.search.hibernatesearch;

import org.apache.lucene.search.*;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBrowseField;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.BIIFilterQuery;
import uk.ac.ebi.bioinvindex.search.SearchException;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.security.UserRole;
import uk.ac.ebi.bioinvindex.dao.StudyDAO;
import uk.ac.ebi.bioinvindex.dao.UserDAO;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.Session;

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

public class SecureStudyFreeTextSearch {

	private static final Log log = LogFactory.getLog(SecureStudyFreeTextSearch.class);

	protected EntityManager entityManager;

	protected BIIQueryBuilder<Study> queryBuilder = new BIIQueryBuilder<Study>();

	protected boolean checkForVisability = true;

	protected List<String> studyAccsForUser;

	/**
	 * The method extracts values from the index and builds a list of Map objects with StudyBrowseField objects
	 * (which correspond to column names in browse view) to an array of values.
	 * @param filterQuery
	 * @param userName
	 * @return
	 * @throws SearchException
	 */
	public List<Map<StudyBrowseField, String[]>> getAllStudyBrowseFieldValuesForUser(BIIFilterQuery<Study> filterQuery, String userName) throws SearchException {

		prepareUserFilter(userName);

		SearchFactory searchFactory = getSession().getSearchFactory();

		DirectoryProvider directoryProvider = searchFactory.getDirectoryProviders(Study.class)[0];

		List<Map<StudyBrowseField, String[]>> answer = new ArrayList<Map<StudyBrowseField, String[]>>();

		if (filterQuery.getSearchText() != null && !filterQuery.getSearchText().equals("")) {
			//Search index
			search(filterQuery, directoryProvider, answer);
            System.out.println("Results are " + answer.size() + " in length");
		} else {
			//Browse/filter Index
			browse(filterQuery, searchFactory, directoryProvider, answer);
		}
		return answer;
	}

	private void prepareUserFilter(String userName) {
		checkForVisability = true;

		StudyDAO studyDAO = DaoFactory.getInstance(entityManager).getStudyDAO();
		UserDAO userDAO = DaoFactory.getInstance(entityManager).getUserDAO();

		if (userName != null && !userName.equals("")) {
			User user = userDAO.getByUsername(userName);
			if (user.getRole().equals(UserRole.CURATOR)) {
				//Curator can see all studies
				checkForVisability = false;
			} else {
				//get Study accs for the user
				studyAccsForUser = studyDAO.getStudyAccForUser(userName);
			}
		} else {
			//show only public Studies for anonymous user
			studyAccsForUser = studyDAO.getPublicStudyAccs();
		}
	}

	private void browse(BIIFilterQuery filterQuery, SearchFactory searchFactory, DirectoryProvider directoryProvider, List<Map<StudyBrowseField, String[]>> answer) {
		ReaderProvider readerProvider = searchFactory.getReaderProvider();
		IndexReader reader = readerProvider.openReader(directoryProvider);

		try {

			if (filterQuery.getFilters().size() > 0) {
				Filter filter = queryBuilder.buildFilter(filterQuery);

				DocIdSet docIdSet = filter.getDocIdSet(reader);

				DocIdSetIterator iterator = docIdSet.iterator();

                int docNumber;

				while ((docNumber = iterator.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {

					if (reader.isDeleted(docNumber)) continue;

					Document document = reader.document(docNumber);
					processDocument(answer, document);
				}

			} else {
				for (int i = 0; i < reader.maxDoc(); i++) {
					if (reader.isDeleted(i)) continue;

					Document document = reader.document(i);
					processDocument(answer, document);

				}
			}
		}
		catch (IOException e) {
			log.error("Cannot open index ", e);
			throw new SearchException("Cannot open index " + e.getMessage(), e);
		} finally {
			readerProvider.closeReader(reader);
		}
	}

	private void search(BIIFilterQuery filterQuery, DirectoryProvider directoryProvider, List<Map<StudyBrowseField, String[]>> answer) {
		IndexSearcher indexSearcher = null;
		try {
			indexSearcher = new IndexSearcher(directoryProvider.getDirectory(), true);

			Query query = queryBuilder.buildQuery(filterQuery.getSearchText());

			TopDocs topScoringDocuments;

			if (filterQuery.getFilters().size() > 0) {
				Filter filter = queryBuilder.buildFilter(filterQuery);
				topScoringDocuments = indexSearcher.search(query, filter, 1000);

			} else {
				topScoringDocuments = indexSearcher.search(query, 1000);
			}

			for (int i = 0; i < topScoringDocuments.scoreDocs.length; i++) {

				Document document = indexSearcher.doc(topScoringDocuments.scoreDocs[i].doc);
				processDocument(answer, document);
			}
		}
		catch (IOException e) {
			log.error("Cannot open index ", e);
			throw new SearchException("Cannot open index " + e.getMessage(), e);
		} catch (ParseException e) {
			log.error("Cannot open index ", e);
			throw new SearchException("Cannot open index " + e.getMessage(), e);
		} finally {
			if (indexSearcher != null) {
				try {
					indexSearcher.close();
				} catch (IOException e) {
					log.error("Cannot close index ", e);
				}
			}
		}
	}

	private void processDocument(List<Map<StudyBrowseField, String[]>> answer, Document document) {

		if (isVisible(document)) {
			Map<StudyBrowseField, String[]> row = buildRow(document);
			answer.add(row);
		}
	}

	protected boolean isVisible(Document document) {

		if (!checkForVisability) {
			return true;
		}

		String[] accs = document.getValues(StudyBrowseField.STUDY_ACC.getName());
		if (studyAccsForUser != null && studyAccsForUser.size() > 0) {
			if (accs != null && accs.length > 0) {
				if (studyAccsForUser.contains(accs[0])) {
					return true;
				}
			}
		}

		return false;
	}

	private Map<StudyBrowseField, String[]> buildRow(Document document) {
		Map<StudyBrowseField, String[]> row = new HashMap<StudyBrowseField, String[]>();

		addCellValue(StudyBrowseField.INVESTIGATION_ACC, row, document);
		addCellValue(StudyBrowseField.STUDY_ACC, row, document);
		addCellValue(StudyBrowseField.TITLE, row, document);
		addCellValue(StudyBrowseField.ORGANISM, row, document);
		addCellValue(StudyBrowseField.ASSAY_INFO, row, document);
        addCellValue(StudyBrowseField.CHARACTERISTICS, row, document);
        addCellValue(StudyBrowseField.FACTORS, row, document);
        addCellValue(StudyBrowseField.PROTOCOL, row, document);
        addCellValue(StudyBrowseField.PUBLICATION, row, document);
        addCellValue(StudyBrowseField.CONTACT, row, document);

		return row;
	}

	private void addCellValue(StudyBrowseField field, Map<StudyBrowseField, String[]> row, Document document) {
		String[] strings = document.getValues(field.getName());
		if (strings != null) {
			row.put(field, strings);
		}
	}

	private FullTextSession getSession() {
		FullTextSession session;
		Session delegate = (Session) entityManager.getDelegate();

		if (delegate instanceof FullTextSession) {
			session = (FullTextSession) delegate;
		} else {
			session = Search.getFullTextSession(delegate);
		}
		return session;
	}

	public EntityManager getEntityManager() {
		if (entityManager == null) {
			throw new IllegalStateException("EntityManager has not been set on StudyFreeTextSearchImpl before usage");
		}
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
