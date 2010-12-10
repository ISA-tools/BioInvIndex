package uk.ac.ebi.bioinvindex.unloading.bioproperties;

import uk.ac.ebi.bioinvindex.model.term.Parameter;
import uk.ac.ebi.bioinvindex.unloading.UnloadManager;

public class ProtocolParameterUnloader extends AbstractPropertyTypeUnloader<Parameter>
{
	public ProtocolParameterUnloader ( UnloadManager unloadManager ) {
		super ( unloadManager );
	}

	/**
	 * At the moment it's null, since we don't store protocol parameter values yet.
	 */
	@Override
	protected Association [] getReferringAssociations () {
		return null; 
	}
}
