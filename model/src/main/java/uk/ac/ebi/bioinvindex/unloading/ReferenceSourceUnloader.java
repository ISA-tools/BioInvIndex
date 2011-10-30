package uk.ac.ebi.bioinvindex.unloading;

import uk.ac.ebi.bioinvindex.model.term.OntologyEntry;
import uk.ac.ebi.bioinvindex.model.xref.AssayTypeDataLocation;
import uk.ac.ebi.bioinvindex.model.xref.ReferenceSource;
import uk.ac.ebi.bioinvindex.model.xref.Xref;

public class ReferenceSourceUnloader extends AbstractAccessibleUnloader<ReferenceSource>
{
	private final static Association[] referringAssociations = new Association [] {
		new Association ( Xref.class, "source" ),
		new Association ( OntologyEntry.class, "source" ),
		new Association ( AssayTypeDataLocation.class, "referenceSource" )
	};

	public ReferenceSourceUnloader ( UnloadManager unloadManager ) {
		super ( unloadManager );
	}

	@Override
	protected Association[] getReferringAssociations () {
		return referringAssociations;
	}

}
