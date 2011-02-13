package uk.ac.ebi.bioinvindex.services.ontologyhandling;

import uk.ac.ebi.bioinvindex.services.utils.StringFormating;

import java.io.Serializable;

/**
 * Ontology
 *
 * @author eamonnmaguire
 * @date Apr 14, 2010
 */


public class Ontology implements Serializable {

	private String source;
	private String accession;
	private String term;
	private String url;

	public Ontology(String termAccession, String termSource, String termName) {
		this.accession = termAccession;
		this.term = termName;
		this.source = termSource;


	}

	public String getAccession() {
		return accession;
	}

	public String getTerm() {
		return term;
	}

	public String getSource() {
		return source;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean hasURL() {

		return url != null && !StringFormating.isEmpty(url);
	}
	

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Ontology ontology = (Ontology) o;

		return !(accession != null ? !accession.equals(ontology.accession) :
				ontology.accession != null) && !(source != null ? !source.equals(ontology.source) :
				ontology.source != null) && !(term != null ? !term.equals(ontology.term) : ontology.term != null);

	}

	@Override
	public int hashCode() {
		int result = source != null ? source.hashCode() : 0;
		result = 31 * result + (accession != null ? accession.hashCode() : 0);
		result = 31 * result + (term != null ? term.hashCode() : 0);
		return result;
	}
}
