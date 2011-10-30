package uk.ac.ebi.bioinvindex.utils.datasourceload;

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

import java.io.InputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.sql.Timestamp;

import org.w3c.dom.*;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.bioinvindex.model.Annotation;
import uk.ac.ebi.bioinvindex.model.term.AnnotationType;
import uk.ac.ebi.bioinvindex.model.term.AnnotationTypes;
import uk.ac.ebi.bioinvindex.model.xref.AssayTypeDataLocation;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.persistence.DataLocationPersister;
import uk.ac.ebi.bioinvindex.persistence.ReferenceSourcePersister;
import uk.ac.ebi.bioinvindex.unloading.UnloadManager;
import uk.ac.ebi.bioinvindex.utils.AccessionGenerator;
import uk.ac.ebi.bioinvindex.dao.ejb3.DaoFactory;
import uk.ac.ebi.bioinvindex.dao.AccessibleDAO;
import uk.ac.ebi.bioinvindex.dao.IdentifiableDAO;

/**
 * @author: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: May 1, 2009
 */
public class DataSourceLoader {

	public static final String DEFAULT_FILE_NAME = "data_locations.xml";

	EntityManager entityManager;

	private static final Log log = LogFactory.getLog(DataSourceLoader.class);

	public void loadAll(InputStream inputStream) throws InvalidConfigurationException {

		ReferenceSource isaTabSource = null;
		Collection<AssayTypeDataLocation> locations = null;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(inputStream);

			// normalize text representation
			doc.getDocumentElement().normalize();

			isaTabSource = parseISATabLocation(doc);
			locations = parseDataSources(doc);

		} catch (SAXParseException err) {
			throw new InvalidConfigurationException(
					"Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() + ":" + err.getMessage (), err);
		} catch (SAXException e) {
			throw new InvalidConfigurationException("Invalid data file location configuration file:" + e.getMessage (), e);
		} catch (IOException e) {
			throw new InvalidConfigurationException("Invalid data file location configuration file" + e.getMessage (), e);
		} catch (ParserConfigurationException e) {
			throw new InvalidConfigurationException("Invalid data file location configuration file" + e.getMessage (), e);
		}

		persistLocations(isaTabSource, locations);

	}

	private void persistLocations(ReferenceSource isaTabSource, Collection<AssayTypeDataLocation> locations)
	{
		EntityTransaction transaction = getEntityManager().getTransaction();

		Timestamp ts = new Timestamp( System.currentTimeMillis() );
		DaoFactory daoFactory = DaoFactory.getInstance ( getEntityManager() );

		DataLocationPersister locPersister = new DataLocationPersister ( daoFactory, ts );
		ReferenceSourcePersister srcPersister = new ReferenceSourcePersister( daoFactory, ts );

		IdentifiableDAO<AssayTypeDataLocation> dao = daoFactory.getIdentifiableDAO ( AssayTypeDataLocation.class );

		List<AssayTypeDataLocation> dataLocations = dao.getAll();

		boolean needsCommit = false;
		for (AssayTypeDataLocation dataLocation : dataLocations )
		{
			// TODO: Playing this way with serialize transactions is dangerous and we should fix this
			// PLEASE LEAVE THIS transaction commands here until we find a workaround, THEY ARE NEEDED in the ISATAB loader
			if ( !transaction.isActive () ) transaction.begin();
			UnloadManager unloadManager =
				new UnloadManager ( daoFactory, dataLocation.getSubmissionTs () );
			unloadManager.queue ( dataLocation );
			unloadManager.delete ();
			needsCommit = true;
		}

		if ( needsCommit ) transaction.commit();
		if ( !transaction.isActive () ) transaction.begin();

		needsCommit = false;
		for (AssayTypeDataLocation location : locations) {
			locPersister.persist ( location );
			needsCommit = true;
		}

		if ( needsCommit ) transaction.commit();
		if ( !transaction.isActive () ) transaction.begin();

		// Gets the old isaTabSource and replace with the new one in case it's already there
		//
		AccessibleDAO<ReferenceSource> daoRef =
			DaoFactory.getInstance ( entityManager ).getAccessibleDAO ( ReferenceSource.class );
		ReferenceSource oldIsaTabSrc = daoRef.getByAcc ( ReferenceSource.ISATAB_METADATA );
		if ( oldIsaTabSrc != null )
		{
			UnloadManager unloadManager =
				new UnloadManager ( DaoFactory.getInstance ( entityManager ), oldIsaTabSrc.getSubmissionTs () );
			unloadManager.queue ( oldIsaTabSrc );

			unloadManager.delete ();
			transaction.commit ();
			// At the end we have another initiated transaction
			transaction.begin ();
		}

		srcPersister.persist ( isaTabSource );
		transaction.commit();

		// Leave an opened transaction, so that it's possible to rejoin the one opened by an invoker
		// TODO: Playing this way to serialize transactions is dangerous and we should fix this
		//
		transaction.begin ();
	}

	protected Collection<AssayTypeDataLocation> parseDataSources(Document doc) throws InvalidConfigurationException {

		Collection<AssayTypeDataLocation> locations = new HashSet<AssayTypeDataLocation>();
		try {

			NodeList dataSources = doc.getElementsByTagName(DataSourceConfigFields.DATASOURCE.getName());

			log.info("Data Location Manager, Total no of data locations: " + dataSources.getLength());

			for (int s = 0; s < dataSources.getLength(); s++) {

				Node dataSourceNode = dataSources.item(s);
				if (dataSourceNode.getNodeType() == Node.ELEMENT_NODE) {

					Element dataSourceElement = (Element) dataSourceNode;

					String measurementType = dataSourceElement.getAttribute(DataSourceConfigFields.MEASUREMENT_TYPE.getName());
					String technologyType = dataSourceElement.getAttribute(DataSourceConfigFields.TECHNOLOGY_TYPE.getName());
					technologyType = StringUtils.trimToNull ( technologyType );

					if (StringUtils.trimToNull(measurementType) == null ) {
						throw new InvalidConfigurationException(
							"measurement_type must be specified in data source confuguration file");
					}

					ReferenceSource refSource = createReferenceSource(dataSourceElement);

					addAnnotation(dataSourceElement, refSource, DataSourceConfigFields.RAW_DATA, AnnotationTypes.RAW_DATA_FILE_PATH, AnnotationTypes.RAW_DATA_FILE_LINK);
					addAnnotation(dataSourceElement, refSource, DataSourceConfigFields.PROCESSED_DATA, AnnotationTypes.PROCESSED_DATA_FILE_PATH, AnnotationTypes.PROCESSED_DATA_FILE_LINK);
					addAnnotation(dataSourceElement, refSource, DataSourceConfigFields.GENERIC_DATA, AnnotationTypes.GENERIC_DATA_FILE_PATH, AnnotationTypes.GENERIC_DATA_FILE_LINK);
					addAnnotation(dataSourceElement, refSource, DataSourceConfigFields.WEB_ENTRY, AnnotationTypes.WEB_ENTRY_URL, AnnotationTypes.WEB_ENTRY_URL);

					AssayTypeDataLocation assayTypeDataLocation = new AssayTypeDataLocation(measurementType, technologyType, refSource);
					locations.add(assayTypeDataLocation);
				}
			}

		} catch (Throwable t) {
			throw new InvalidConfigurationException(
					"Invalid configuration file", t);
		}

		return locations;
	}

	protected ReferenceSource parseISATabLocation(Document doc) throws InvalidConfigurationException {

		ReferenceSource referenceSource = null;

		try {

			NodeList isaSource = doc.getElementsByTagName(DataSourceConfigFields.ISATAB_SOURCE.getName());

			if (isaSource.getLength() < 1) {
				throw new InvalidConfigurationException("Location for ISATab data is not specified.");
			}

			Node isaSourceNode = isaSource.item(0);

			if (isaSourceNode.getNodeType() == Node.ELEMENT_NODE) {

				Element isaSourceElement = (Element) isaSourceNode;

				referenceSource = new ReferenceSource(ReferenceSource.ISATAB_METADATA);
				referenceSource.setAcc(referenceSource.getName());

				addAnnotation(isaSourceElement, referenceSource, DataSourceConfigFields.ISATAB_LOCATION,
						AnnotationTypes.ISATAB_LOCATION_PATH, AnnotationTypes.ISATAB_LOCATION_LINK);

			}
			return referenceSource;

		} catch (Throwable t) {
			throw new InvalidConfigurationException(
					"Invalid configuration file", t);
		}
	}

	private ReferenceSource createReferenceSource(Element dataSourceElement) {

		String name = StringUtils.trimToNull(dataSourceElement.getAttribute( DataSourceConfigFields.NAME.getName () ));
		String measurementType = dataSourceElement.getAttribute(DataSourceConfigFields.MEASUREMENT_TYPE.getName());
		String technologyType = dataSourceElement.getAttribute(DataSourceConfigFields.TECHNOLOGY_TYPE.getName());
		technologyType = StringUtils.trimToNull ( technologyType );

		ReferenceSource referenceSource;
		if ( name != null) {
			referenceSource = new ReferenceSource( name );
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(measurementType).append("-").append(technologyType);
			referenceSource = new ReferenceSource(sb.toString());
		}

		referenceSource.setUrl(dataSourceElement.getAttribute("url"));
		referenceSource.setDescription(dataSourceElement.getAttribute("description"));
		// We must add an id, cause otherwise you'll have multiple assay types assigned to the same source,
		// which doesn't make sense
		referenceSource.setAcc( AccessionGenerator.getInstance ().generateAcc ( referenceSource.getName() + "." ) );

		return referenceSource;
	}

	private void addAnnotation(Element dataSourceElement, ReferenceSource referenceSource,
	                           DataSourceConfigFields field, AnnotationTypes annotationTypePath,
	                           AnnotationTypes annotationTypeWeb) {

		NodeList nodeList = dataSourceElement.getElementsByTagName(field.getName());
		if (nodeList != null) {
			Element firstElement = (Element) nodeList.item(0);

			if (firstElement != null) {
				String fsPath = firstElement.getAttribute(DataSourceConfigFields.FILESYSTEM_PATH.getName());

				if (StringUtils.trimToNull(fsPath) != null) {
					AnnotationType type = new AnnotationType(annotationTypePath.getName());
					Annotation annotation = new Annotation(type, fsPath);
					referenceSource.addAnnotation(annotation);
				}

				String webUrl = firstElement.getAttribute(DataSourceConfigFields.WEB_URL.getName());
				if (StringUtils.trimToNull(webUrl) != null) {
					AnnotationType type = new AnnotationType(annotationTypeWeb.getName());
					Annotation annotation = new Annotation(type, webUrl);
					referenceSource.addAnnotation(annotation);
				}

			}
		}
	}

	public EntityManager getEntityManager() {
		if (entityManager == null) {
			throw new IllegalStateException("entityManager is required but has not been set");
		}
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
