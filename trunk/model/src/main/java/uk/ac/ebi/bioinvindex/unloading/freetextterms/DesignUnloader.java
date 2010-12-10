package uk.ac.ebi.bioinvindex.unloading.freetextterms;

import uk.ac.ebi.bioinvindex.model.term.Design;
import uk.ac.ebi.bioinvindex.unloading.UnloadManager;

public class DesignUnloader extends AbstractFreeTextTermUnloader<Design>
{
	public DesignUnloader ( UnloadManager unloadManager ) {
		super ( unloadManager );
	}
}
