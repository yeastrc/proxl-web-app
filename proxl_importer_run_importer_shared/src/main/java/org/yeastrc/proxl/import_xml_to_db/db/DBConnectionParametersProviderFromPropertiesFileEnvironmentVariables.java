package org.yeastrc.proxl.import_xml_to_db.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Default version of IDBConnectionParametersProvider that reads a property file
 *
 */
public class DBConnectionParametersProviderFromPropertiesFileEnvironmentVariables implements IDBConnectionParametersProvider {


	private static final String NO_PROPERTIES_FILE_ERROR_MESSAGE = "No DB Connection Properties file found.";

	private static final String DB_CONFIG_FILENAME = "db_config_file.properties";


	private static final String PROPERTY_NAME__USERNAME = "username";
	private static final String PROPERTY_NAME__PASSWORD = "password";
	private static final String PROPERTY_NAME__DB_HOST   = "dbHost";
	private static final String PROPERTY_NAME__DB_PORT  = "dbPort";

	private static final String PROPERTY_NAME__PROXL_DB_NAME  = "proxl.db.name";


	private static final String ENVIRONMENT_VARIABLE__USERNAME = "PROXL_DB_USER";
	private static final String ENVIRONMENT_VARIABLE__PASSWORD = "PROXL_DB_PASSWORD";
	private static final String ENVIRONMENT_VARIABLE__DB_HOST = "PROXL_DB_HOST_ADDRESS";
	private static final String ENVIRONMENT_VARIABLE__DB_PORT = "PROXL_DB_HOST_PORT";

	private static final String ENVIRONMENT_VARIABLE__PROXL_DB_NAME = "PROXL_DB_NAME";
	



	private static final Logger log = LoggerFactory.getLogger(  DBConnectionParametersProviderFromPropertiesFileEnvironmentVariables.class );


	private File configFile;

	private String username;
	private String password;
	private String dbHost;
	private String dbPort;

	private String proxlDbName;

	private boolean configured;


	@Override
	public void init() 
			throws DBConnectionParametersProviderPropertiesFileContentsErrorException, 
			DBConnectionParametersProviderPropertiesFileErrorException {

		log.debug( "Entered init()" );

		if ( configured ) {

			return;
		}

		String propertiesFilenameMaybeWithPath = null;

		Properties configProps = null;

		InputStream propertiesFileAsStream = null;

		try {

			if ( configFile != null ) {

				propertiesFilenameMaybeWithPath = configFile.getAbsolutePath();

				if ( ! configFile.exists() ) {

					System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

					String msg = "Properties file not found: " + configFile.getAbsolutePath();
					log.error( msg );

					throw new DBConnectionParametersProviderPropertiesFileErrorException( msg );
				}

				try {

					propertiesFileAsStream = new FileInputStream(configFile);

				} catch ( FileNotFoundException e ) {

					System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

					String msg = "Properties file not found: " + configFile.getAbsolutePath() + " exception: " + e.toString();
					//					log.error( msg, e );

					throw new DBConnectionParametersProviderPropertiesFileErrorException( msg );
				}

			} else {

				//  Get config file from class path

				propertiesFilenameMaybeWithPath = DB_CONFIG_FILENAME;

				ClassLoader thisClassLoader = this.getClass().getClassLoader();

				URL configFileUrlObjUrlLocal = thisClassLoader.getResource( DB_CONFIG_FILENAME );

				if ( configFileUrlObjUrlLocal == null ) {

					System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

					String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";
					//					log.error( msg );

					throw new DBConnectionParametersProviderPropertiesFileErrorException( msg );

				} else {

					if ( log.isInfoEnabled() ) {

						log.info( "Properties file '" + DB_CONFIG_FILENAME + "' found, load path = " + configFileUrlObjUrlLocal.getFile() );
					}
				}


				propertiesFilenameMaybeWithPath = configFileUrlObjUrlLocal.getFile();


				propertiesFileAsStream = configFileUrlObjUrlLocal.openStream();


				if ( propertiesFileAsStream == null ) {

					System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

					String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";

					//					log.error( msg );

					throw new DBConnectionParametersProviderPropertiesFileErrorException( msg );
				}
			}


			configProps = new Properties();

			configProps.load( propertiesFileAsStream );

		} catch (IOException e) {

			log.error( "In init(),   Properties file '" 
					+ propertiesFilenameMaybeWithPath + "', IOException: " + e.toString(), e );

			throw new DBConnectionParametersProviderPropertiesFileErrorException( e );

		} finally {

			if ( propertiesFileAsStream != null ) {

				try {
					propertiesFileAsStream.close();
				} catch (IOException e) {

					log.error( "In init(), propertiesFileAsStream.close():   Properties file '" 
							+ propertiesFilenameMaybeWithPath + "', IOException: " + e.toString(), e );

					throw new DBConnectionParametersProviderPropertiesFileErrorException( e );
				}
			}
		}

		getConfigPropertiesFromPropertiesObj(configProps);


		configured = true;
	}


	/**
	 * @param configProps - Properties object to get properties from
	 * @throws DBConnectionParametersProviderFromPropertiesFileException
	 */
	public void getConfigPropertiesFromPropertiesObj( Properties configProps )
			throws DBConnectionParametersProviderPropertiesFileContentsErrorException {

		username = System.getenv( ENVIRONMENT_VARIABLE__USERNAME );
		password = System.getenv( ENVIRONMENT_VARIABLE__PASSWORD );
		dbHost = System.getenv( ENVIRONMENT_VARIABLE__DB_HOST );
		dbPort = System.getenv( ENVIRONMENT_VARIABLE__DB_PORT );

		proxlDbName = System.getenv( ENVIRONMENT_VARIABLE__PROXL_DB_NAME );
		
		//  Get from Config file if NOT in Environment Variables

		if ( username == null ) {
			username = configProps.getProperty( PROPERTY_NAME__USERNAME );
		}
		if ( password == null ) {
			password = configProps.getProperty( PROPERTY_NAME__PASSWORD );
		}
		if ( dbHost == null ) {
			dbHost = configProps.getProperty( PROPERTY_NAME__DB_HOST );
		}
		if ( dbPort == null ) {
			dbPort = configProps.getProperty( PROPERTY_NAME__DB_PORT );
		}
		if ( proxlDbName == null ) {
			proxlDbName = configProps.getProperty( PROPERTY_NAME__PROXL_DB_NAME );
		}

		
		if ( StringUtils.isEmpty( username ) ) {

			String msg = "For Database connection parameters file: parameter '" + PROPERTY_NAME__USERNAME + "' is not provided or is empty string.";
			System.err.println( msg );
			throw new DBConnectionParametersProviderPropertiesFileContentsErrorException(msg);
		}

		if ( StringUtils.isEmpty( password ) ) {
			String msg = "For Database connection parameters file: parameter '" + PROPERTY_NAME__PASSWORD + "' is not provided or is empty string.";
			System.err.println( msg );
			throw new DBConnectionParametersProviderPropertiesFileContentsErrorException(msg);
		}

		if ( StringUtils.isEmpty( dbHost ) ) {
			String msg = "For Database connection parameters file: parameter '" + PROPERTY_NAME__DB_HOST + "' is not provided or is empty string.";
			System.err.println( msg );
			throw new DBConnectionParametersProviderPropertiesFileContentsErrorException(msg);
		}


//		if ( log.isInfoEnabled() ) {

			System.out.println( "Database connection parameters:");
			
			if ( StringUtils.isNotEmpty( username ) ) {
				System.out.println( "Environment Variable '" + ENVIRONMENT_VARIABLE__DB_HOST 
						+ "' OR Database connection parameters file: parameter '" + PROPERTY_NAME__USERNAME + "' has a value" );
			}
			if ( StringUtils.isNotEmpty( password ) ) {
				System.out.println( "Environment Variable '" + ENVIRONMENT_VARIABLE__PASSWORD 
						+ "' OR Database connection parameters file: parameter '" + PROPERTY_NAME__PASSWORD + "' has a value" );
			}
			System.out.println( "Environment Variable '" + ENVIRONMENT_VARIABLE__DB_HOST 
					+ "' OR Database connection parameters file: parameter '" + PROPERTY_NAME__DB_HOST + "': " + dbHost );

			System.out.println( "Environment Variable '" + ENVIRONMENT_VARIABLE__DB_PORT 
					+ "' OR Database connection parameters file: parameter '" + PROPERTY_NAME__DB_PORT + "': " + dbPort );

			if ( StringUtils.isNotEmpty( proxlDbName ) ) {
				System.out.println( "Environment Variable '" + ENVIRONMENT_VARIABLE__PROXL_DB_NAME 
						+ "' OR Database connection parameters file: parameter '" + PROPERTY_NAME__PROXL_DB_NAME + "': " + proxlDbName );

				System.out.println( PROPERTY_NAME__PROXL_DB_NAME + ": " + proxlDbName );
			}
//		}

		configured = true;
	}


	@Override
	public String getUsername() {

		return username;
	}

	@Override
	public String getPassword() {

		return password;
	}

	@Override
	public String getDBURL() {

		return dbHost;
	}

	@Override
	public String getDBPort() {

		return dbPort;
	}

	@Override
	public String getProxlDbName() {
		return proxlDbName;
	}

	@Override
	public String getNrseqDbName() {
		return null;
	}





	public File getConfigFile() {
		return configFile;
	}


	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}


	public void setProxlDbName(String proxlDbName) {
		this.proxlDbName = proxlDbName;
	}



}
