package uk.ac.ebi.bioinvindex.unloading.freetextterms;

import uk.ac.ebi.bioinvindex.model.term.ProtocolComponent;
import uk.ac.ebi.bioinvindex.unloading.UnloadManager;

public class ProtocolComponentUnloader extends AbstractFreeTextTermUnloader<ProtocolComponent>
{
	public ProtocolComponentUnloader ( UnloadManager unloadManager ) {
		super ( unloadManager );
	}
}
