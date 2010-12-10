package uk.ac.ebi.bioinvindex.services.browse;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.StudyBrowseField;
import uk.ac.ebi.bioinvindex.search.hibernatesearch.bridge.AssayInfoDelimiters;
import uk.ac.ebi.bioinvindex.services.DBLink;

import java.util.*;

/**
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Feb 26, 2008
 */
public class BrowseStudyBeanImpl implements BrowseStudyBean, AssayInfoDelimiters, Comparable {
	private static final Log log = LogFactory.getLog(BrowseStudyBeanImpl.class);

	private Map<StudyBrowseField, String[]> values;

	public BrowseStudyBeanImpl(Map<StudyBrowseField, String[]> values) {
		this.values = values;
	}

	public String getInvestigation() {
		return getFirstValue(StudyBrowseField.INVESTIGATION_ACC);
	}

	public String getAcc() {
		return getFirstValue(StudyBrowseField.STUDY_ACC);
	}

	public String getTitle() {
		return getFirstValue(StudyBrowseField.TITLE);
	}

	public String getOrganism() {
		return getConcatValues(StudyBrowseField.ORGANISM);
	}

	public String getFactor() {
		return getConcatValues(StudyBrowseField.FACTOR_NAME);
	}


	public List<AssayInfoBean> getAssayBeans() {
		// a check to prevent fetching the assays twice, since the first time it is need to get the total number of assays for display in the page

		String[] strings = values.get(StudyBrowseField.ASSAY_INFO);
		if (strings == null) {
			return new ArrayList<AssayInfoBean>(1);
		}

		List<AssayInfoBean> answer = new ArrayList<AssayInfoBean>(strings.length);
		for (String string : strings) {
			answer.add(processAssayInfoResultString(string));
		}
		return answer;
	}

	private String getFirstValue(StudyBrowseField fieldName) {
		String[] strings = values.get(fieldName);
		if (strings != null && strings.length >= 1) {
			return strings[0];
		} else {
			return "";
		}
	}

	private AssayInfoBean processAssayInfoResultString(String assayInfoString) {
		AssayInfoBean assayInfoBean = new AssayInfoBean();

		// String template: endpoint|technology|number|&&&acc1!!!url1&&&acc2!!!url2
//		System.out.println("AssayInfoString: " + assayInfoString);
//		log.error("AssayInfoString: " + assayInfoString);

		Scanner scanner = new Scanner(assayInfoString);
		scanner.useDelimiter("\\|");

		if (scanner.hasNext()) {
			assayInfoBean.setEndPoint(scanner.next());
		} else {
			assayInfoBean.setEndPoint("");
		}

		if (scanner.hasNext()) {
			String token = scanner.next();
			try {
				// check to see if the String can be made into a number.
				Integer.valueOf(token);
				assayInfoBean.setCount(token);
				assayInfoBean.setTechnology("");
			} catch (NumberFormatException nfe) {
				assayInfoBean.setTechnology(token);
				if (scanner.hasNext()) {
					assayInfoBean.setCount(scanner.next());
				} else {
					assayInfoBean.setCount("0");
				}
			}
		}

		return assayInfoBean;
	}

	private String getConcatValues(StudyBrowseField field) {
		String[] strings = values.get(field);
		if (strings == null || strings.length < 1) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		Set<String> values = new HashSet<String>(strings.length);
		for (String string : strings) {
			if (!"".equals(string) && !values.contains(string)) {
				sb.append(string);
				sb.append(", ");
				values.add(string);
			}
		}
		sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

	private DBLink createLink(String toParse) {
		DBLink link = new DBLink();

		StringTokenizer tokenizer = new StringTokenizer(toParse, ACC_URL_DELIM);
		if (tokenizer.hasMoreTokens()) {
			link.setAcc(tokenizer.nextToken());
		}

		if (tokenizer.hasMoreTokens()) {
			link.setUrl(tokenizer.nextToken() + link.getAcc());
		}
		return link;
	}

	public int compareTo(Object o) {
		return getAcc().compareTo(((BrowseStudyBean) o).getAcc());
	}

	@Override
	public String toString() {
		return getAcc();
	}
}
