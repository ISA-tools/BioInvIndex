package uk.ac.ebi.bioinvindex.unloading.ontologyterms;

import uk.ac.ebi.bioinvindex.model.term.FreeTextTerm;
import uk.ac.ebi.bioinvindex.model.term.OntologyTerm;
import uk.ac.ebi.bioinvindex.unloading.UnloadManager;

public class OntologyTermUnloader extends AbstractOntologyEntryUnloader<OntologyTerm>
{
	private final static Association[] referringAssociations = new Association [] {
		new Association ( FreeTextTerm.class, "ontologyTerms" ),
	};

	public OntologyTermUnloader ( UnloadManager unloadManager ) {
		super ( unloadManager );
	}

	@Override
	protected Association[] getReferringAssociations () {
		return referringAssociations;
	}

}
