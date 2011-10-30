package uk.ac.ebi.bioinvindex.unloading.ontologyterms;

import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.unloading.UnloadManager;

/**
 * TODO: Do we really need this? OE is abstract, to see if ever involved
 *
 * @author brandizi
 * <b>date</b>: Oct 29, 2009
 *
 */
public class OntologyEntryUnloader extends AbstractOntologyEntryUnloader<OntologyEntry>
{
	public OntologyEntryUnloader ( UnloadManager unloadManager ) {
		super ( unloadManager );
	}
}
