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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.LowerCaseDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;

import uk.ac.ebi.bioinvindex.utils.test.H2DataTypeFactory;

/**
 * Base class used to make test classes which uses the database and Hibernate peristence.
 *
 * User: Nataliya Sklyar (nsklyar@ebi.ac.uk), Review by brandizi, Ago 2008
 * Date: Sep 10, 2007
 */
public abstract class DBUnitTest
{

	protected static List<DatabaseOperation> beforeTestOperations = new ArrayList<DatabaseOperation>();
	protected static List<DatabaseOperation> afterTestOperations = new ArrayList<DatabaseOperation>();
	protected EntityManagerFactory entityManagerFactory;
	protected EntityManager entityManager;
	protected Session session;
	protected Connection connection;
	protected IDatabaseConnection dbunitConnection;

	protected String dataSetLocation;

	protected final static Logger log = Logger.getLogger ( DBUnitTest.class );

	/**
	 * Calls {@link #init()}
	 *
	 * @throws Exception
	 */
	protected DBUnitTest() throws Exception {
		init ();
	}

	/**
	 * specify {@link #dataSetLocation}, {@link #beforeTestOperations}, {@value #afterTestOperations}.
	 * The default is:
	 *
	 * <pre>
	 * 	beforeTestOperations.add ( DatabaseOperation.CLEAN_INSERT );
	 * 	dataSetLocation = "study-data.xml";
	 * </pre>
	 */
	protected void prepareSettings()
	{
		beforeTestOperations.add ( DatabaseOperation.CLEAN_INSERT );
		dataSetLocation = "study-data.xml";
	}

	/**
	 * Initializes the class for doing a test. This default calls {@link #prepareSettings()} {@link #initEntityManager()},
	 *
	 * @throws Exception
	 */
	protected void init () throws Exception
	{
		prepareSettings();
		initEntityManager ( true );
	}

	/**
	 * Runs {@link #initDataSet()} before every test.
	 *
	 */
	@Before
	public void beforeTestProcessing () throws Exception
	{
		initDataSet ();
		// cleanAll ();
	}


	/**
	 * By default Performs the operations in {@link #afterTestOperations} and closes connection/session/entityManager.
	 *
	 */
	@After
	public void afterTestProcessing () throws Exception
	{
		if ( afterTestOperations == null || afterTestOperations.size () == 0 )
			return;

		// TODO: Problems when have we Hibernate and committed transactions
		setReferentialIntegrityCheckings ( false );
		IDatabaseConnection dbUnitCon = createDbUnitConnection ();

		IDataSet dset = getDataSet ();
		try {
			for ( DatabaseOperation op : afterTestOperations ) {
				op.execute ( dbUnitCon, dset );
			}
		}
		finally {
			setReferentialIntegrityCheckings ( true );
			connection.close();
		}
		session.flush();
		close ();
	}


	/**
	 * Closes the EntityManager.
	 *
	 * @throws Exception
	 */
	protected void close()
	{
		// if ( entityManager.isOpen () ) entityManager.close();
		if ( entityManagerFactory.isOpen () ) entityManagerFactory.close();
	}


	/**
	 * Loads an XML data-set, used by {@link #initDataSet()}.
	 *
	 */
	protected IDataSet getDataSet() throws DataSetException, IOException
	{
		if (dataSetLocation == null) {
			System.out.println ( "*** WARNING: Test subclass should prepare a dataset location" );
			return new DefaultDataSet ();
		}

		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream ( dataSetLocation );

		IDataSet dataSet = new FlatXmlDataSet ( input );

		// This allows to specify [null] for null values
		ReplacementDataSet repDs = new ReplacementDataSet ( dataSet );
		repDs.addReplacementObject ( "[null]", null );

		// Converts only column names to lower case (required in MySQL).
		return new LowerCaseDataSet ( repDs ) {
			@Override
			public boolean isCaseSensitiveTableNames () { return true; }
		};

	}



	/**
	 * A facility to clean all the tables in the database, independently on the fact they appear or not in the dataset.
	 *
	 * TODO: Not completely sure it works with Oracle... :-\
	 *
	 * @throws SQLException
	 */
	protected void cleanAll () throws SQLException
	{
		setReferentialIntegrityCheckings ( false );

		try {
			Statement delstmt = connection.createStatement ();
			DatabaseMetaData dbmsMeta = connection.getMetaData();
			String dbmsName = dbmsMeta.getDatabaseProductName ().toLowerCase ();
			String dbmsCatalog = connection.getCatalog ();
			if ( dbmsCatalog == null )
				// Let's try with the user name
				dbmsCatalog = dbmsMeta.getUserName ().toUpperCase ();

			String dbmsSchema = null;
			if ( dbmsName.contains ( "oracle" ) )
			{
				// The damn Oracle needs the schema in getTables(), otherwise it returns undeletable
				// system tables too.
				//
				Statement stmt = connection.createStatement ();
				ResultSet rs = stmt.executeQuery ( "select sys_context('USERENV', 'CURRENT_SCHEMA') CURRENT_SCHEMA from dual" );
				if ( rs.next () )
					dbmsSchema = rs.getString ( 1 );
				stmt.close ();
			}

			log.debug ( "DBMSUnitTest.cleanAll(), DBMS Name: '"
				+ dbmsName + "' Catalog: '" + dbmsCatalog + "' Schema: '" + dbmsSchema + "'"
			);


	    ResultSet tbrs = dbmsMeta.getTables ( dbmsCatalog, dbmsSchema, null, new String[] { "TABLE" } );

			while ( tbrs.next () )
			{
				String tbname = StringUtils.trimToNull ( tbrs.getString ( "TABLE_NAME" ) );
				if ( tbname == null ) continue;
				// Oracle system tables
				String sql = "DROP TABLE " + tbname;
				if ( !dbmsName.contains ( "mysql" ) )
					sql += " CASCADE CONSTRAINTS";
				log.debug ( "DBUnitTest, adding sql: " + sql );
				delstmt.addBatch ( sql );
			}
			delstmt.executeBatch ();
		}
		finally {
			setReferentialIntegrityCheckings ( true );
			connection.close();
			// All tables were deleted, we need this to force schema recreation.
			initEntityManager ( true );
		}
	}


	/**
	 * Inits several variables used by this class.
	 *
	 * @param forceRecreation when false, objects are not reinitialized if they are non null.
	 *
	 */
	protected void initEntityManager ( boolean forceRecreation )
	{
		if ( forceRecreation && entityManager != null )
			close ();
		if ( forceRecreation || entityManagerFactory == null )
			entityManagerFactory = Persistence.createEntityManagerFactory ( "BIIEntityManager" );
		if ( forceRecreation || entityManager == null )
			entityManager = entityManagerFactory.createEntityManager();
		if ( forceRecreation || session == null )
			session = (Session) entityManager.getDelegate();
		if ( forceRecreation || connection == null )
			connection = session.connection();
	}

	/**
	 * Defaults to {@link #initEntityManager(boolean) initConnection(false)}
	 *
	 */
	protected void initEntityManager () {
		initEntityManager ( false );
	}

	/**
	 * Enables/disables the referential integrity checkings in the database.
	 *
	 * @param isset
	 * @throws SQLException
	 */
	protected void setReferentialIntegrityCheckings ( boolean isset ) throws SQLException
	{

		String sql = null;

		DatabaseMetaData dbmsMeta = connection.getMetaData ();
		String dbmsName = dbmsMeta.getDatabaseProductName ().toLowerCase ();
		String dbmsCatalog = connection.getCatalog ();
		if ( dbmsCatalog == null )
			// Let's try with the user name
			dbmsCatalog = dbmsMeta.getUserName ().toUpperCase ();

		log.debug ( "DBUnitTest.setReferentialIntegrityCheckings(), DBMS Name: '" + dbmsName + "' Catalog: '" + dbmsCatalog + "'" );

		if ( dbmsName.contains ( "h2" ) )
		 sql = "SET REFERENTIAL_INTEGRITY " + isset;
		else if ( dbmsName.contains ( "mysql" ) )
			sql = "set FOREIGN_KEY_CHECKS = " + ( isset ? "1" : "0" );
		else if ( dbmsName.contains ( "oracle" ) )
		{
			// Oracle is quite messy...
			String sqlCs = "select css.*, decode(CONSTRAINT_TYPE, 'P', '0', 'C', '1', 'U', 2, 'R', '3', 1000) ctype " +
				"from sys.all_constraints css where owner = '" + dbmsCatalog + "' order by ctype " + (isset ? "ASC" : "DESC" );

			ResultSet rs = connection.createStatement ().executeQuery ( sqlCs );
			Statement csDelStmt = connection.createStatement ();
			while ( rs.next () )
			{
				String sqlCsCmd = isset ? "enable" : "disable";
				String tbname = rs.getString ( "TABLE_NAME" );
				String csname = rs.getString ( "CONSTRAINT_NAME" );

				String sqlCsDel = "alter table " + dbmsCatalog + "." + tbname + " " + sqlCsCmd + " constraint " + csname;
				log.debug ( "DBUnitTest, adding sql: " + sqlCsDel );
				csDelStmt.addBatch ( sqlCsDel );
			}
			csDelStmt.executeBatch ();
			return;
		}

		if ( sql == null )
			throw new SQLException ( "Don't know how to change referential integrity checks for the database: '" + dbmsName + "'" );

		connection.createStatement ().execute ( sql );
	}


	/**
	 * Initializes the database with data-set and operations returned by {@link #getDataSet()}.
	 *
	 */
	protected void initDataSet () throws SQLException, DataSetException, DatabaseUnitException, IOException
	{
		if ( beforeTestOperations == null || beforeTestOperations.size () == 0 )
			return;

		setReferentialIntegrityCheckings ( false );
		IDatabaseConnection dbUnitCon = createDbUnitConnection ();

		IDataSet dset = getDataSet ();
		try {
			for ( DatabaseOperation op : beforeTestOperations ) {
				op.execute ( dbUnitCon, dset );
			}
		}
		finally {
			setReferentialIntegrityCheckings ( true );
			connection.close();
		}
		session.flush ();
	}


	/**
	 * Creates a new database connection to be used by DbUnit, used {@link #initDataSet()} and {@link #afterTestProcessing()}.
	 * @throws SQLException
	 *
	 */
	protected IDatabaseConnection createDbUnitConnection () throws SQLException, DatabaseUnitException
	{
		IDatabaseConnection dbUnitCon = new DatabaseConnection ( connection );
		DatabaseConfig config = dbUnitCon.getConfig();

		DatabaseMetaData dbmsMeta = connection.getMetaData ();
		String dbmsName = dbmsMeta.getDatabaseProductName ().toLowerCase ();
		String dbmsCatalog = connection.getCatalog ();
		if ( dbmsCatalog == null )
			// Let's try with the user name
			dbmsCatalog = dbmsMeta.getUserName ().toUpperCase ();

		IDataTypeFactory dtf = new DefaultDataTypeFactory ();
		if ( dbmsName.contains ( "h2" ) ) dtf = new H2DataTypeFactory ();
		else if ( dbmsName.contains ( "mysql" ) ) dtf = new MySqlDataTypeFactory ();
		else if ( dbmsName.contains ( "oracle") ) dtf = new Oracle10DataTypeFactory ();
		else
			System.out.println (
				"WARNING: Don't know which DBUnit DataType factory to use with '" + dbmsName + ", hope the default works"
		);

		config.setProperty ( DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dtf );

		return dbUnitCon;
	}
}
