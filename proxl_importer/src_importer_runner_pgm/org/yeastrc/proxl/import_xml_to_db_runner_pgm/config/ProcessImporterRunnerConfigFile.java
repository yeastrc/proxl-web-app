package org.yeastrc.proxl.import_xml_to_db_runner_pgm.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderFromPropertiesFile;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderPropertiesFileErrorException;



/**
 * 
 *
 */
public class ProcessImporterRunnerConfigFile {

	private static Logger log = Logger.getLogger( ProcessImporterRunnerConfigFile.class );
	

	private static final String NO_PROPERTIES_FILE_ERROR_MESSAGE = "No DB Connection Properties file found.";
	
	private static final String CONFIG_FILENAME = "run_importer_config_file.properties";
	

	private static final String PROPERTY_NAME__JAVA_EXECUTABLE_WITH_PATH = "java.executable.with.path";

	private static final String PROPERTY_NAME__IMPORTER_JAR_WITH_PATH = "importer.jar.with.path";
	
	private static final String PROPERTY_NAME__IMPORTER_DB_CONFIG_WITH_PATH = "importer.db.config.file.with.path";
	
	/**
	 * private constructor
	 */
	private ProcessImporterRunnerConfigFile() { }

	/**
	 * @return newly created instance
	 */
	public static ProcessImporterRunnerConfigFile getInstance() { 
		return new ProcessImporterRunnerConfigFile(); 
	}
	
	
	/**
	 * Process the import runner config file, saving the config and 
	 * return a IDBConnectionParametersProvider object configured with DB params
	 * 
	 * 
	 * @param configFileFromCommandLine
	 * @return
	 * @throws Exception 
	 */
	public DBConnectionParametersProviderFromPropertiesFile processConfigFile( File configFileFromCommandLine ) throws Exception {
		

		try {
			
			Properties configProps = null;
			
			InputStream propertiesFileAsStream = null;
			
			try {

				if ( configFileFromCommandLine != null ) {

					if ( ! configFileFromCommandLine.exists() ) {

						System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

						String msg = "Properties file not found: " + configFileFromCommandLine.getAbsolutePath();
						//					log.error( msg );

						throw new DBConnectionParametersProviderPropertiesFileErrorException( msg );
					}

					try {

						propertiesFileAsStream = new FileInputStream(configFileFromCommandLine);

					} catch ( FileNotFoundException e ) {

						System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

						String msg = "Properties file not found: " + configFileFromCommandLine.getAbsolutePath() + " exception: " + e.toString();
						//					log.error( msg, e );

						throw new DBConnectionParametersProviderPropertiesFileErrorException( msg );
					}

				} else {

					//  Get config file from class path

					ClassLoader thisClassLoader = this.getClass().getClassLoader();

					URL configFileUrlObjUrlLocal = thisClassLoader.getResource( CONFIG_FILENAME );

					if ( configFileUrlObjUrlLocal == null ) {

						System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

						String msg = "Properties file '" + CONFIG_FILENAME + "' not found in class path.";
						//					log.error( msg );

						throw new DBConnectionParametersProviderPropertiesFileErrorException( msg );

					} else {

						if ( log.isInfoEnabled() ) {

							log.info( "Properties file '" + CONFIG_FILENAME + "' found, load path = " + configFileUrlObjUrlLocal.getFile() );
						}
					}

					propertiesFileAsStream = configFileUrlObjUrlLocal.openStream();


					if ( propertiesFileAsStream == null ) {

						System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

						String msg = "Properties file '" + CONFIG_FILENAME + "' not found in class path.";

						//					log.error( msg );

						throw new DBConnectionParametersProviderPropertiesFileErrorException( msg );
					}
				}


				configProps = new Properties();

				configProps.load(propertiesFileAsStream);

			} finally {
				
				if ( propertiesFileAsStream != null ) {
					
					propertiesFileAsStream.close();
				}
			}
			
			String javaExecutableWithPath = configProps.getProperty( PROPERTY_NAME__JAVA_EXECUTABLE_WITH_PATH );
			String importerJarWithPath = configProps.getProperty( PROPERTY_NAME__IMPORTER_JAR_WITH_PATH );
			String importerDbConfigWithPath = configProps.getProperty( PROPERTY_NAME__IMPORTER_DB_CONFIG_WITH_PATH );


			if ( StringUtils.isEmpty( importerJarWithPath ) ) {

				String msg = "For config file: parameter '" + PROPERTY_NAME__IMPORTER_JAR_WITH_PATH + "' is not provided or is empty string.";
				log.error( msg );
				throw new DBConnectionParametersProviderPropertiesFileErrorException(msg);
			}


			if ( StringUtils.isNotEmpty( javaExecutableWithPath ) ) {
				ImporterRunnerConfigData.setJavaExecutableWithPath( javaExecutableWithPath );;
			}
			
			ImporterRunnerConfigData.setImporterJarWithPath( importerJarWithPath );
			
			if ( StringUtils.isNotEmpty( importerDbConfigWithPath ) ) {
				ImporterRunnerConfigData.setImporterDbConfigWithPath( importerDbConfigWithPath );
			}
			
			ImporterRunnerConfigData.setConfigured(true);
			

			DBConnectionParametersProviderFromPropertiesFile dbConnectionParametersProviderFromPropertiesFile =
					new DBConnectionParametersProviderFromPropertiesFile();

			dbConnectionParametersProviderFromPropertiesFile.getConfigPropertiesFromPropertiesObj( configProps );

			dbConnectionParametersProviderFromPropertiesFile.init();

			return dbConnectionParametersProviderFromPropertiesFile;
			

		} catch ( RuntimeException e ) {

			log.error( "In init(),   Properties file '" + CONFIG_FILENAME + "', exception: " + e.toString(), e );

			throw e;
		}
		
	}
}
