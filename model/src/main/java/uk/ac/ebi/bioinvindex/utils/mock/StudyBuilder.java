package uk.ac.ebi.bioinvindex.utils.mock;

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

import uk.ac.ebi.bioinvindex.model.Contact;
import uk.ac.ebi.bioinvindex.model.Publication;
import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.processing.Assay;

import java.util.Date;

/**
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Sep 14, 2007
 */
public class StudyBuilder {

	private Study study;

	public StudyBuilder() {
		study = new Study();
	}

	public Study buildStudy() {
		return study;
	}

	public StudyBuilder id(Long id) {
		study.setId(id);
		return this;
	}

	public StudyBuilder acc(String acc) {
		study.setAcc(acc);
		return this;
	}

	public StudyBuilder title(String title) {
		study = new Study(title);
		return this;
	}

	public StudyBuilder description(String desctiption) {
		study.setDescription(desctiption);
		return this;
	}

	public StudyBuilder objective(String objective) {
		study.setObjective(objective);
		return this;
	}

	public StudyBuilder dates(Date releaseDate, Date submissionDate) {
		study.setReleaseDate(releaseDate);
		study.setSubmissionDate(submissionDate);
		return this;
	}

	public StudyBuilder contact(Contact contact) {
		study.addContact(contact);
		return this;
	}

	public StudyBuilder assay(Assay assay) {
		study.addAssay(assay);
		return this;
	}

	public StudyBuilder publication(Publication publication) {
		study.addPublication(publication);
		return this;
	}

	public static void main(String[] args) {
		StudyBuilder builder = new StudyBuilder();

		Study study1 = builder.title("rrrr")
				.description("dfdfdfdf")
				.objective("fgfgdgd")
				.buildStudy();
		System.out.println("study1 = " + study1);

	}
}
