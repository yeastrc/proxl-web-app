package org.yeastrc.proxl.import_xml_to_db_runner_pgm.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderFromPropertiesFile;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.exceptions.ConfigPropertiesFileErrorException;



/**
 * 
 *
 */
public class ProcessImporterRunnerConfigFile {

	private static final Logger log = LoggerFactory.getLogger(  ProcessImporterRunnerConfigFile.class );
	

	private static final String NO_PROPERTIES_FILE_ERROR_MESSAGE = "No DB Connection Properties file found.";
	
	private static final String CONFIG_FILENAME = "run_importer_config_file.properties";

	private static final String PROPERTY_NAME__WAIT_TIME_FOR_NEXT_CHECK_FOR_IMPORT_TO_PROCESS = "wait.time.for.next.check.for.import.to.process";

	private static final String PROPERTY_NAME__JAVA_EXECUTABLE_WITH_PATH = "java.executable.with.path";
	private static String PROPERTY_NAME__JAVA_EXECUTABLE_PARAMETERS = "java.executable.parameters";
	
	private static final String PROPERTY_NAME__IMPORTER_JAR_WITH_PATH = "importer.jar.with.path";
	
	private static final String PROPERTY_NAME__IMPORTER_DB_CONFIG_WITH_PATH = "importer.db.config.file.with.path";
	
	public static final String PROPERTY_NAME__IMPORTER_PID_FILE_WITH_PATH = "importer.pid.file.with.path";
	
	private static final String PROPERTY_NAME__PROXL_WEB_APP_BASE_URL = "proxl.web.app.base.url";
	
	private static final String PROPERTY_NAME__COMMAND_RUN_ON_SUCCESSFUL_IMPORT = "command.run.successful.import";
	private static final String PROPERTY_NAME__COMMAND_RUN_ON_SUCCESSFUL_IMPORT_SYSOUT_SYSERR_DIR = "command.run.successful.import.sysout.syserr.dir";
	
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
						throw new ConfigPropertiesFileErrorException( msg );
					}

					try {
						propertiesFileAsStream = new FileInputStream(configFileFromCommandLine);

					} catch ( FileNotFoundException e ) {
						System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );
						String msg = "Properties file not found: " + configFileFromCommandLine.getAbsolutePath() + " exception: " + e.toString();
						//					log.error( msg, e );
						throw new ConfigPropertiesFileErrorException( msg );
					}
				} else {
					//  Get config file from class path

					ClassLoader thisClassLoader = this.getClass().getClassLoader();
					URL configFileUrlObjUrlLocal = thisClassLoader.getResource( CONFIG_FILENAME );

					if ( configFileUrlObjUrlLocal == null ) {
						System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );
						String msg = "Properties file '" + CONFIG_FILENAME + "' not found in class path.";
						//					log.error( msg );
						throw new ConfigPropertiesFileErrorException( msg );
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
						throw new ConfigPropertiesFileErrorException( msg );
					}
				}


				configProps = new Properties();

				configProps.load(propertiesFileAsStream);

			} finally {
				
				if ( propertiesFileAsStream != null ) {
					
					propertiesFileAsStream.close();
				}
			}
			
			String waitTimeForNextCheckForImportToProcess_InSecondsString = configProps.getProperty( PROPERTY_NAME__WAIT_TIME_FOR_NEXT_CHECK_FOR_IMPORT_TO_PROCESS );
			
			String javaExecutableWithPath = configProps.getProperty( PROPERTY_NAME__JAVA_EXECUTABLE_WITH_PATH );
			String javaExecutableParametersString = configProps.getProperty( PROPERTY_NAME__JAVA_EXECUTABLE_PARAMETERS );
			
			String importerJarWithPath = configProps.getProperty( PROPERTY_NAME__IMPORTER_JAR_WITH_PATH );
			String importerDbConfigWithPath = configProps.getProperty( PROPERTY_NAME__IMPORTER_DB_CONFIG_WITH_PATH );
			
			String importerPidFileWithPath = configProps.getProperty( PROPERTY_NAME__IMPORTER_PID_FILE_WITH_PATH );
			
			//  Do Not set PROPERTY_NAME__PROXL_WEB_APP_BASE_URL to Not send email on import completion 
			String proxlWebAppBaseURL = configProps.getProperty( PROPERTY_NAME__PROXL_WEB_APP_BASE_URL );
			
			String commandToRunOnSuccessfulImport = configProps.getProperty( PROPERTY_NAME__COMMAND_RUN_ON_SUCCESSFUL_IMPORT );
			String commandToRunOnSuccessfulImportSyoutSyserrDir = configProps.getProperty( PROPERTY_NAME__COMMAND_RUN_ON_SUCCESSFUL_IMPORT_SYSOUT_SYSERR_DIR );

			if ( StringUtils.isNotEmpty( waitTimeForNextCheckForImportToProcess_InSecondsString ) ) {
				
				//  Have default so ok if empty

				int waitTimeForNextCheckForImportToProcess_InSeconds = -1;
				try {
					waitTimeForNextCheckForImportToProcess_InSeconds = Integer.parseInt( waitTimeForNextCheckForImportToProcess_InSecondsString );
				} catch (Exception e ) {
					String msg = "ERROR:  For config file: parameter '" 
							+ PROPERTY_NAME__WAIT_TIME_FOR_NEXT_CHECK_FOR_IMPORT_TO_PROCESS 
							+ "' is provided but is not an integer.  Value in config file: "
							+ waitTimeForNextCheckForImportToProcess_InSecondsString;
					log.error( msg, e );
					throw new ConfigPropertiesFileErrorException(msg);
				}

				ImporterRunnerConfigData.setWaitTimeForNextCheckForImportToProcess_InSeconds( waitTimeForNextCheckForImportToProcess_InSeconds );
				
				log.warn( "INFO: Config file property '" 
						+ PROPERTY_NAME__WAIT_TIME_FOR_NEXT_CHECK_FOR_IMPORT_TO_PROCESS
						+ "' has value: " 
						+ waitTimeForNextCheckForImportToProcess_InSeconds
						+ " seconds" );
			}

			if ( StringUtils.isEmpty( proxlWebAppBaseURL ) ) {

				//  Do Not set PROPERTY_NAME__PROXL_WEB_APP_BASE_URL to Not send email on import completion 

				String msg = "INFO::  For config file: parameter '" + PROPERTY_NAME__PROXL_WEB_APP_BASE_URL 
						+ "' is not provided or is empty string.  Not calling server to send email on import completion.";
				log.warn( msg );
			} else {

				String msg = "INFO::  Config file: parameter '" + PROPERTY_NAME__PROXL_WEB_APP_BASE_URL 
						+ "' is provided so calling server to send email on import completion.";
				log.warn( msg );
			}

			if ( StringUtils.isEmpty( importerJarWithPath ) ) {

				String msg = "  ERROR:  For config file: parameter '" + PROPERTY_NAME__IMPORTER_JAR_WITH_PATH + "' is not provided or is empty string.";
				log.error( msg );
				throw new ConfigPropertiesFileErrorException(msg);
			}


			if ( StringUtils.isNotEmpty( javaExecutableWithPath ) ) {

				// Have default of "java" so ok if empty
				
				ImporterRunnerConfigData.setJavaExecutableWithPath( javaExecutableWithPath );
			}
			
			if ( StringUtils.isNotEmpty( javaExecutableParametersString ) ) {
				String[] javaExecutableParametersArray = javaExecutableParametersString.split( " " );
				List<String> javaExecutableParametersLocal = new ArrayList<>( javaExecutableParametersArray.length );
				for ( String javaExecutableParameter : javaExecutableParametersArray ) {
					if ( StringUtils.isNotEmpty(javaExecutableParameter) ) {
						javaExecutableParametersLocal.add( javaExecutableParameter );
					}
				}
				if ( ! javaExecutableParametersLocal.isEmpty() ) {
					ImporterRunnerConfigData.setJavaExecutableParameters( javaExecutableParametersLocal );
				}
			}
			
			ImporterRunnerConfigData.setImporterJarWithPath( importerJarWithPath );
			
			if ( StringUtils.isNotEmpty( importerDbConfigWithPath ) ) {
				
				//  If not set, assumes that the importer jar has the DB config data file in it's jar 
				
				ImporterRunnerConfigData.setImporterDbConfigWithPath( importerDbConfigWithPath );
			}

			if ( StringUtils.isNotEmpty( importerPidFileWithPath ) ) {
				
				//  If not set, assumes that there is no pid file 
				
				ImporterRunnerConfigData.setImporterPidFileWithPath( importerPidFileWithPath );

				String msg = "INFO::  PID file: parameter '" + PROPERTY_NAME__IMPORTER_PID_FILE_WITH_PATH 
						+ "' is provided so deleting it when shut down using run control file.  value:  " + importerPidFileWithPath;
				log.warn( msg );
			}
			
			ImporterRunnerConfigData.setProxlWebAppBaseURL( proxlWebAppBaseURL );

			if ( StringUtils.isNotEmpty( commandToRunOnSuccessfulImport ) ) {
				ImporterRunnerConfigData.setCommandToRunOnSuccessfulImport( commandToRunOnSuccessfulImport );

				String msg = "INFO::  Config file: parameter '" + PROPERTY_NAME__COMMAND_RUN_ON_SUCCESSFUL_IMPORT 
						+ "' is provided so calling that program on Successful import.  value:  " + commandToRunOnSuccessfulImport;
				log.warn( msg );
			}
			if ( StringUtils.isNotEmpty( commandToRunOnSuccessfulImportSyoutSyserrDir ) ) {
				ImporterRunnerConfigData.setCommandToRunOnSuccessfulImportSyoutSyserrDir( commandToRunOnSuccessfulImportSyoutSyserrDir );

				String msg = "INFO::  Config file: parameter '" + PROPERTY_NAME__COMMAND_RUN_ON_SUCCESSFUL_IMPORT_SYSOUT_SYSERR_DIR 
						+ "' is provided so calling that program on Successful import.  value:  " + commandToRunOnSuccessfulImportSyoutSyserrDir;
				log.warn( msg );
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
