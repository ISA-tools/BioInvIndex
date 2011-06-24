package uk.ac.ebi.bioinvindex.search.hibernatesearch;

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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.OpenBitSet;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.reader.ReaderProvider;
import org.hibernate.search.store.DirectoryProvider;

import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.search.SearchException;
import uk.ac.ebi.bioinvindex.search.StudyFreeTextSearch;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.dao.StudyDAO;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: Sep 14, 2007
 */
public class StudyFreeTextSearchImpl implements StudyFreeTextSearch {

	protected EntityManager entityManager;
	protected BIIQueryBuilder<Study> queryBuilder = new BIIQueryBuilder<Study>();
	protected boolean userFilter = false;

	public List<Map<StudyBrowseField, String[]>> getAllStudyBrowseFieldValues(BIIFilterQuery<Study> filterQuery) {
		SearchFactory searchFactory = getSession().getSearchFactory();

		DirectoryProvider directoryProvider = searchFactory.getDirectoryProviders(Study.class)[0];

		List<Map<StudyBrowseField, String[]>> answer = new ArrayList<Map<StudyBrowseField, String[]>>();

		if (filterQuery.getSearchText() != null && !filterQuery.getSearchText().equals("")) {
			//Search index
			search(filterQuery, directoryProvider, answer);

		} else {
			//Browse/filter Index
			browse(filterQuery, searchFactory, directoryProvider, answer);
		}
		return answer;
	}

	private void browse(BIIFilterQuery filterQuery, SearchFactory searchFactory, DirectoryProvider directoryProvider, List<Map<StudyBrowseField, String[]>> answer) {
		ReaderProvider readerProvider = searchFactory.getReaderProvider();
		IndexReader reader = readerProvider.openReader(directoryProvider);

		try {

			if (filterQuery.getFilters().size() > 0) {
				Filter filter = queryBuilder.buildFilter(filterQuery);

				DocIdSet docIdSet = filter.getDocIdSet(reader);

				DocIdSetIterator iterator = docIdSet.iterator();

				while (iterator.next()) {
					int i = iterator.doc();

					if (reader.isDeleted(i)) continue;

					Document document = reader.document(i);
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
			e.printStackTrace();
		} finally {
			readerProvider.closeReader(reader);
		}
	}

	private void search(BIIFilterQuery filterQuery, DirectoryProvider directoryProvider, List<Map<StudyBrowseField, String[]>> answer) {
		IndexSearcher indexSearcher = null;
		try {
			indexSearcher = new IndexSearcher(directoryProvider.getDirectory());

			Query query = queryBuilder.buildQuery(filterQuery.getSearchText());

			Hits hits;

			if (filterQuery.getFilters().size() > 0) {
				Filter filter = queryBuilder.buildFilter(filterQuery);
				hits = indexSearcher.search(query, filter);
			} else {
				hits = indexSearcher.search(query);
			}

			for (int i = 0; i < hits.length(); i++) {

				Document document = hits.doc(i);
				processDocument(answer, document);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (indexSearcher != null) {
				try {
					indexSearcher.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void processDocument(List<Map<StudyBrowseField, String[]>> answer, Document document) {

			Map<StudyBrowseField, String[]> row = buildRow(document);
			answer.add(row);
	}

	private Map<StudyBrowseField, String[]> buildRow(Document document) {
		Map<StudyBrowseField, String[]> row = new HashMap<StudyBrowseField, String[]>();

		addCellValue(StudyBrowseField.INVESTIGATION_ACC, row, document);
		addCellValue(StudyBrowseField.STUDY_ACC, row, document);
		addCellValue(StudyBrowseField.TITLE, row, document);
		addCellValue(StudyBrowseField.ORGANISM, row, document);
		addCellValue(StudyBrowseField.FACTOR_NAME, row, document);
		addCellValue(StudyBrowseField.ASSAY_INFO, row, document);
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


	//////////////////////////
	// Dependencies injection
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
