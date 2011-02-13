package uk.ac.ebi.bioinvindex.utils.test;

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

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.StringDataType;
import org.dbunit.dataset.datatype.BinaryStreamDataType;

import java.sql.Types;

/**
 * Needed in H2 to map booleans.
 * <p/>
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk)
 * Date: Sep 21, 2007
 */
public class H2DataTypeFactory extends DefaultDataTypeFactory {

	protected static final DataType CLOB_AS_STRING = new StringDataType("CLOB", Types.CLOB);
	protected static final DataType BLOB_AS_STREAM = new BinaryStreamDataType("BLOB", Types.BLOB);

//	private static final Log log = LogFactory.getLog(H2DataTypeFactory.class);

	public DataType createDataType(int sqlType, String sqlTypeName)
			throws DataTypeException {
		if (sqlType == Types.BOOLEAN) {
			return DataType.BOOLEAN;
		}

		// BLOB
		if ("BLOB".equals(sqlTypeName)) {
			return BLOB_AS_STREAM;
		}

		// CLOB
		if ("CLOB".equals(sqlTypeName)) {
			return CLOB_AS_STRING;
		}

		return super.createDataType(sqlType, sqlTypeName);
	}
}
