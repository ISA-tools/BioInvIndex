package uk.ac.ebi.bioinvindex.services.ontologyhandling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * OntologyResolver resolves an Ontology to a URL so that a user can click on a term defined as being an ontology to look
 * at it's definition in OLS or BioPortal
 * <p/>
 * Bioportal (NCIt) - http://bioportal.bioontology.org/visualize/42331/Diagnostic_or_Prognostic_Factor
 * Bioportal (Normal) - http://bioportal.bioontology.org/visualize/42629/?id=EFO_0000001
 * OLS - http://www.ebi.ac.uk/ontology-lookup/?termId=EFO_0001435
 *
 * @author eamonnmaguire
 * @date Apr 14, 2010
 */


public class OntologyResolver implements Serializable {

	private static Set<String> badURLs = new HashSet<String>();

	private static final Log log = LogFactory.getLog(OntologyResolver.class);

	private static final String BIOPORTAL_URL_TEMPLATE = "http://bioportal.bioontology.org/visualize/<<ONTOLOGY_VERSION>>/<<TERM_ACCESSION>>";
	private static final String OLS_URL_TEMPLATE = "http://www.ebi.ac.uk/ontology-lookup/?termId=<<TERM_ACCESSION>>";

	public void resolveOntology(Ontology ontology) {

		String ontologyTermURL = constructURL(ontology);

		if (ontologyTermURL != null) {
			ontology.setUrl(ontologyTermURL);
		}
	}

	private String constructURL(Ontology ontology) {
		URL toReturn;

		String url;
		if (useOLS(ontology)) {

			String source = ontology.getSource().equalsIgnoreCase("newt") ? ontology.getAccession() :
					ontology.getSource() + ":" + ontology.getAccession();

			url = OLS_URL_TEMPLATE.replaceAll("<<TERM_ACCESSION>>", source);
		} else {

			BioPortalOntologies bpo = BioPortalOntologies.getBioPortalOntology(ontology);
			url = BIOPORTAL_URL_TEMPLATE.replaceAll("<<ONTOLOGY_VERSION>>", bpo.getLatestAccession());
			url = url.replaceAll("<<TERM_ACCESSION>>", ontology.getSource() + "_" + ontology.getAccession());
		}

		try {
			toReturn = new URL(url);
			log.info("URL for " + ontology.getTerm() + " is " + url);
			// if URL works, then return it for use!!

			if (badURLs.contains(url)) {
				return null;
			}

			if (testURL(toReturn)) {
				return url;
			} else {
				badURLs.add(url);
			}

		} catch (MalformedURLException e) {
			return null;
		}

		return null;
	}

	private boolean testURL(URL urlName) {
		try {
			HttpURLConnection con =
					(HttpURLConnection) urlName.openConnection();
			con.setRequestMethod("HEAD");
			con.setReadTimeout(1500);
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (MalformedURLException e) {
			log.debug("URL is malformed. This should never happen.");
			return false;
		} catch (IOException e) {
			log.debug("Read timed out when performing operation. Terminated connection and marked URL as bad.");
			// e.printStackTrace();
			return false;
		}
	}

	private boolean useOLS(Ontology ontology) {
		for (BioPortalOntologies bpo : BioPortalOntologies.values()) {
			if (bpo.getSource().equalsIgnoreCase(ontology.getSource().toLowerCase())) {
				return false;
			}
		}

		return true;
	}


	enum BioPortalOntologies {
		NCIt("NCIt", "42331"), OBI("OBI", "40832"), EFO("EFO", "42629");

		public String source;
		public String latestAccession;

		BioPortalOntologies(String source, String latestAccession) {
			this.source = source;
			this.latestAccession = latestAccession;
		}

		public String getLatestAccession() {
			return latestAccession;
		}

		public String getSource() {
			return source;
		}

		public static BioPortalOntologies getBioPortalOntology(Ontology ontology) {
			for (BioPortalOntologies bpo : BioPortalOntologies.values()) {
				if (bpo.source.equalsIgnoreCase(ontology.getSource().toLowerCase())) {
					return bpo;
				}
			}
			return null;
		}
	}
}
