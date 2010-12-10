package uk.ac.ebi.bioinvindex.dao;


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

import uk.ac.ebi.bioinvindex.model.Study;
import uk.ac.ebi.bioinvindex.model.VisibilityStatus;
import uk.ac.ebi.bioinvindex.model.security.User;
import uk.ac.ebi.bioinvindex.model.term.CharacteristicValue;
import uk.ac.ebi.bioinvindex.model.term.FactorValue;
import uk.ac.ebi.bioinvindex.model.term.Property;
import uk.ac.ebi.bioinvindex.model.term.PropertyValue;

import java.util.List;
import java.util.Map;

/**
 * @author Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Jun 22, 2007
 */
public interface StudyDAO extends AccessibleDAO<Study> {

	public List<Property<FactorValue>> getFactorsForStudy(Long studyId);

	public List<PropertyValue> getValuesOfPropertyForStudyId(Long studyId, String propertyName);

	public List<Property<CharacteristicValue>> getCharacteristicsForStudy(Long studyId);

    public List<String> filterByOntologyTermAndRefAcc(String termAcc, String refSourceName);

	public List<PropertyValue> getValuesOfPropertyForStudyAcc(String studyAcc, String propertyName);

	public List<String> filterByOntologyTermAndRefName(String termAcc, String refSourceName);

	public boolean studyExists(String acc);

	public List<String> filterFactorsByOntologyTermAndRefName(String termAcc, String refSourceName);

	public List<String> getStudyAccForUser(String userName);

	public List<String> getPublicStudyAccs();

	public Study getByAccForUser(String acc, String userName);

	public void changeStudyStatus(String acc, VisibilityStatus status);

	public Map<String, VisibilityStatus> getVisibilityStatusForStudies();

	public void addUserToStudy(String acc, User user);

	public void removeUserFromStudy(String acc, User user);

	public List<String> getOwnedStudiesForUser(String userName);

	public List<String> filterFactorsByOntologyTermAndRefNameForUser(String termAcc, String refSourceName, String userName);
}
