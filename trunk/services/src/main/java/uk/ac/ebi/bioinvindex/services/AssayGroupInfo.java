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

import java.util.ArrayList;
import java.util.List;

/**
 * User: Nataliya Sklyar
 * Date: Jan 20, 2009
 */
public class AssayGroupInfo {

	private String endPoint;

	private String technology;

	private String platform;

	private List<DataLink> dataLinks = new ArrayList<DataLink>();

	private List<String> accessions = new ArrayList<String>();

//	private Collection<DBLink> dbLinks= new ArrayList<DBLink>();

	private String sourceName;


	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public List<String> getAccessions() {
		return accessions;
	}

	public void addAccession(String accession) {
		if (!accessions.contains(accession)) {
			accessions.add(accession);
		}
	}

	public List<DataLink> getDataLinks() {
		return dataLinks;
	}

	public void addDataLink(DataLink link) {
		if (!dataLinks.contains(link)) {
			dataLinks.add(link);
		}
	}

	public String getSourceName() {
		return sourceName.toLowerCase();
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AssayGroupInfo that = (AssayGroupInfo) o;

		if (endPoint != null ? !endPoint.equals(that.endPoint) : that.endPoint != null) return false;
		if (platform != null ? !platform.equals(that.platform) : that.platform != null) return false;
		if (technology != null ? !technology.equals(that.technology) : that.technology != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = endPoint != null ? endPoint.hashCode() : 0;
		result = 31 * result + (technology != null ? technology.hashCode() : 0);
		result = 31 * result + (platform != null ? platform.hashCode() : 0);
		return result;
	}

}
