package org.yeastrc.proxl.import_xml_to_db.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Default version of IDBConnectionParametersProvider that reads a property file
 *
 */
public class DBConnectionParametersProviderFromPropertiesFile implements IDBConnectionParametersProvider {

	public static String DB_CONFIG_FILENAME = "db_config_file.properties";
	
	
	private static String PROPERTY_NAME__USERNAME = "username";
	private static String PROPERTY_NAME__PASSWORD = "password";
	private static String PROPERTY_NAME__DB_URL   = "dbURL";
	private static String PROPERTY_NAME__DB_PORT  = "dbPort";

	private static String PROPERTY_NAME__PROXL_DB_NAME  = "proxl.db.name";
	private static String PROPERTY_NAME__NRSEQ_DB_NAME  = "nrseq.db.name";

	private static Logger log = Logger.getLogger(DBConnectionParametersProviderFromPropertiesFile.class);
	
	
	private File configFile;


	private String username;
	private String password;
	private String dbURL;
	private String dbPort;

	private String proxlDbName;
	private String nrseqDbName;
	
	
	@Override
	public void init() throws Exception {

		log.debug( "Entered init()" );

		try {
			
			
			InputStream propertiesFileAsStream = null;
			
			
			if ( configFile != null ) {
				
				if ( ! configFile.exists() ) {

					String msg = "Properties file not found: " + configFile.getAbsolutePath();
					log.error( msg );
					throw new Exception( msg );
				}
				
				try {

					propertiesFileAsStream = new FileInputStream(configFile);
					
				} catch ( FileNotFoundException e ) {

					String msg = "Properties file not found: " + configFile.getAbsolutePath() + " exception: " + e.toString();
					log.error( msg, e );
					throw new Exception( msg, e );
				}
				
			} else {

				//  Get config file from class path
				
				ClassLoader thisClassLoader = this.getClass().getClassLoader();

				URL configPropFile = thisClassLoader.getResource( DB_CONFIG_FILENAME );


				if ( configPropFile == null ) {

					String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";
					log.error( msg );
					throw new Exception( msg );

				} else {

					log.info( "Properties file '" + DB_CONFIG_FILENAME + "' found, load path = " + configPropFile.getFile() );
				}
				
				propertiesFileAsStream = thisClassLoader.getResourceAsStream( DB_CONFIG_FILENAME );
				

				if ( propertiesFileAsStream == null ) {

					String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";

					log.error( msg );

					throw new Exception( msg );
				}
			}

//			if ( propertiesFileAsStream == null ) {
//
//				//  Should not get this error here but extra protection if code above didn't catch it.
//				
//				String msg = "Properties file not found.";
//
//				log.error( msg );
//
//				throw new Exception( msg );
//				
//			} else {

				Properties configProps = new Properties();

				configProps.load(propertiesFileAsStream);
				
				 username = configProps.getProperty( PROPERTY_NAME__USERNAME );
				 password = configProps.getProperty( PROPERTY_NAME__PASSWORD );
				 dbURL = configProps.getProperty( PROPERTY_NAME__DB_URL );
				 dbPort = configProps.getProperty( PROPERTY_NAME__DB_PORT );
				 
				 proxlDbName = configProps.getProperty( PROPERTY_NAME__PROXL_DB_NAME );
				 nrseqDbName = configProps.getProperty( PROPERTY_NAME__NRSEQ_DB_NAME );
				 
				 System.out.println( "Database connection parameters:");
				 if ( StringUtils.isNotEmpty( username ) ) {
					 
					 System.out.println( "username has a value" );
				 }
				 if ( StringUtils.isNotEmpty( password ) ) {
					 
					 System.out.println( "password has a value" );
				 }
				 System.out.println( "dbURL: " + dbURL );
				 System.out.println( "dbPort: " + dbPort );
				 
				 if ( StringUtils.isNotEmpty( proxlDbName ) ) {
					 
					 System.out.println( PROPERTY_NAME__PROXL_DB_NAME + ": " + proxlDbName );
				 }
				 if ( StringUtils.isNotEmpty( nrseqDbName ) ) {
					 
					 System.out.println( PROPERTY_NAME__NRSEQ_DB_NAME + ": " + nrseqDbName );
				 }
//			}

		} catch ( RuntimeException e ) {

			log.error( "In init(),   Properties file '" + DB_CONFIG_FILENAME + "', exception: " + e.toString(), e );

			throw e;
		}
		
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
		
		return dbURL;
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
		return nrseqDbName;
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


	public void setNrseqDbName(String nrseqDbName) {
		this.nrseqDbName = nrseqDbName;
	}


}
