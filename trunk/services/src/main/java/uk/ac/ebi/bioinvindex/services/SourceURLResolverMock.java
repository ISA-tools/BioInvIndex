package uk.ac.ebi.bioinvindex.services;

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
 * User: Nataliya Sklyar
 * Date: Jan 20, 2009
 *
 * @Depricated
 */
//@Name("sourceURLResolver")
//@AutoCreate
public class SourceURLResolverMock  {

	private static final String AE = "AE";
	private static final String PRIDE = "PRIDE";
	private static final String ENA = "ENA";
	private static final String EMBL_BANK = "EMBL";
	public static final String MEDA = "MEDA";
	public static final String GENERAL = "GENERIC";

	private static final String AE_RAW = "ftp://ftp.ebi.ac.uk/pub/databases/microarray/data/experiment/<type_goes_here>/<acc_goes_here>/<acc_goes_here>.raw.zip";
	private static final String MEDA_RAW = "ftp://ftp.ebi.ac.uk/pub/databases/bii/meda_repo/<acc_goes_here>";
	// todo this needs to be changed...ENA000 is not a given. The parent folder depends on the accession. I can't be bothered changing it now. It will do for now!
	private static final String ENA_RAW = "ftp://ftp.era.ebi.ac.uk/vol1/ERA000/<acc_goes_here>";
	private static final String GENERAL_RAW = "ftp://ftp.ebi.ac.uk/pub/databases/bii/generic_repo/<acc_goes_here>";
	private static final String AE_PROCESSED = "ftp://ftp.ebi.ac.uk/pub/databases/microarray/data/experiment/<type_goes_here>/<acc_goes_here>/<acc_goes_here>.processed.zip";
	private static final String PRIDE_PROCESSED = "ftp://ftp.ebi.ac.uk/pub/databases/pride/PRIDE_Exp_Complete_Ac_<acc_goes_here>.gz";
	private static final String AE_ENTRY = "http://www.ebi.ac.uk/microarray-as/ae/browse.html?keywords=<acc_goes_here>";
	private static final String PRIDE_ENTRY = "http://www.ebi.ac.uk/pride/experimentLink.do?experimentAccessionNumber=<acc_goes_here>";
	private static final String EMBL_ENTRY = "http://www.ebi.ac.uk/cgi-bin/dbfetch?db=EMBL&id=<acc_goes_here>";
	private static final String ENA_ENTRY = "ftp://ftp.era-xml.ebi.ac.uk/ERA000/<acc_goes_here>";

	public String getRawDataURL(String sourceName, String accession) {
		if (sourceName.toUpperCase().indexOf(AE) == 0) {
			String type = accession.substring(accession.indexOf("-") + 1, accession.lastIndexOf("-"));
			return AE_RAW.replaceAll("<acc_goes_here>", accession).replaceAll("<type_goes_here>", type);

		} else if (sourceName.toUpperCase().indexOf(PRIDE) == 0) {
			return PRIDE_PROCESSED.replaceAll("<acc_goes_here>", accession);

		} else if (sourceName.toUpperCase().indexOf(MEDA) == 0) {
			return MEDA_RAW.replaceAll("<acc_goes_here>", accession);

		} else if(sourceName.toUpperCase().indexOf(ENA) == 0) {
			return ENA_RAW.replaceAll("<acc_goes_here>", accession);

		} else if (sourceName.toUpperCase().indexOf(GENERAL) == 0) {
			return GENERAL_RAW.replaceAll("<acc_goes_here>", accession);

		} else {
			return null;
		}
	}

	public String getProcessedDataURL(String sourceName, String accession) {
		if (sourceName.toUpperCase().indexOf(AE) == 0) {
			String type = accession.substring(accession.indexOf("-") + 1, accession.lastIndexOf("-"));
			return AE_PROCESSED.replaceAll("<acc_goes_here>", accession).replaceAll("<type_goes_here>", type);

		} else if (sourceName.toUpperCase().indexOf(PRIDE) == 0) {
			return PRIDE_PROCESSED.replaceAll("<acc_goes_here>", accession);

		} else if (sourceName.toUpperCase().indexOf(MEDA) == 0) {
			return MEDA_RAW.replaceAll("<acc_goes_here>", accession);

		} else if(sourceName.toUpperCase().indexOf(ENA) == 0) {
			return ENA_RAW;

		} else if (sourceName.toUpperCase().indexOf(GENERAL) == 0) {
			return GENERAL_RAW.replaceAll("<acc_goes_here>", accession);

		} else {
			return null;
		}
	}

	/**
	 * Returns the URL to access ArrayExpress, PRIDE, or other...
	 *
	 * @param sourceName Source name for thing
	 * @param accession  Accession for thing
	 * @return Thing
	 */
	public String getEntryURL(String sourceName, String accession) {
		if (sourceName.toUpperCase().indexOf(AE) == 0) {
			return AE_ENTRY.replaceAll("<acc_goes_here>", accession);

		} else if (sourceName.toUpperCase().indexOf(PRIDE) == 0) {
			return PRIDE_ENTRY.replaceAll("<acc_goes_here>", accession);

		} else if(sourceName.toUpperCase().indexOf(EMBL_BANK) == 0) {
			if(accession.contains(";")) {
				accession = accession.substring(0, accession.indexOf(";")).trim();
			}
			return EMBL_ENTRY.replaceAll("<acc_goes_here>", accession);
			
		} else if(sourceName.toUpperCase().indexOf(ENA) == 0) {
			return ENA_ENTRY.replaceAll("<acc_goes_here>", accession);

		}else {
			return null;
		}
	}


	public String getViewImageLocation(String sourceName) {
		if (sourceName.toUpperCase().indexOf(AE) == 0) {
			return "img/download_images/view_ae.png";
		} else if (sourceName.toUpperCase().indexOf(PRIDE) == 0) {
			return "img/download_images/view_pride.png";
		} else if (sourceName.toUpperCase().indexOf(ENA) == 0) {
			return "img/download_images/view_ena.png";
		} else if (sourceName.toUpperCase().indexOf(EMBL_BANK) == 0) {
			return "img/download_images/view_embl.png";
		}

		return "";
	}


}
