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

/**
 * Definitions used in the {@link DataSourceLoader} for the data locations file.
 *
 * @author: Nataliya Sklyar (nsklyar@ebi.ac.uk) Date: May 5, 2009
 */
public enum DataSourceConfigFields {

	/** The XML element for defining an assay data location */
	DATASOURCE("datasource"),

	/** The data source attributes for defining the measurment/technology type of the location */
	MEASUREMENT_TYPE("measurement_type"),
	/** The data source attributes for defining the measurment/technology type of the location */
	TECHNOLOGY_TYPE("technology_type"),

	/** Data source name. Names are used for several puorposes by the web app. E.g.: when the source name is ArrayExpress,
	 *  the BII web app searches for Assay Comments of the type "ArrayExpress Raw Data Link" and uses these comment values
	 *  for building links to the external resource.
	 */
	NAME("name"),

	/** The general URL and description of the resource */
	URL("url"),
	/** The general URL and description of the resource */
	DESCRIPTION("description"),

	/** The element of data source for defining the raw data paths*/
	RAW_DATA("raw_data"),
	/** The element of data source for defining the processed data paths*/
	PROCESSED_DATA("processed_data"),

	/** The element of data source for defining the generic data file paths*/
	GENERIC_DATA("generic_data"),

	/** The attribute (of data source) for the specific web page about a certain assay */
	WEB_ENTRY("db_entry"),

	/** The element that define the meta-data location */
	ISATAB_SOURCE("isatab_source"),
	/** The element belonging to {@link #ISATAB_SOURCE} for defining the location details */
	ISATAB_LOCATION("location"),

	/** The attribute for defining the writeable FS pattern path of a location */
	FILESYSTEM_PATH("filesystem_path"),

	/** The attribute for defining the pattern URL of a location */
	WEB_URL("web_url"),

	/** This placeholder is replaced by the real BII study accession when file/URL paths are processed OR it is
	 *  replaced by the accession that is read from the assay file comments, eg: M-EXP-123 */
	ACCESSION_PLACEHOLDER("${study-acc}"),

	/**
	 * This is a patch used for ArrayExpress data URLs. Whenever a data URL has this placeholder, it assumes that the
	 * data accession has the form "X-Y-Z" and this placeholder is replaced with Y. It is needed to build AE URLs like:
	 *
	 * ftp://ftp.ebi.ac.uk/pub/databases/microarray/data/experiment/MEXP/E-MEXP-115/E-MEXP-115.raw.zip
	 *
	 * TODO: File patterns can only be based on this study/data-type place holders. In future We might need/want some
	 * much more flexible approach (e.g.: scripts passed via XML).
	 *
	 */
	ACCESSION_PREFIX_PLACEHOLDER ( "${study-acc-prefix}" );


	private String name;

	DataSourceConfigFields(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
