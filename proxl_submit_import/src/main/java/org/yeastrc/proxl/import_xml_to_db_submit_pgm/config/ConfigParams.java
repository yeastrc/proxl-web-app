package org.yeastrc.proxl.import_xml_to_db_submit_pgm.config;

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

import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.ConfigPropertiesConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportConfigException;


/**
 * 
 *
 */
public class ConfigParams {


	private static final Logger log = LoggerFactory.getLogger(  ConfigParams.class );

	private static final ConfigParams instance = new ConfigParams();

	private ConfigParams() { }
	public static ConfigParams getInstance() { return instance; }


	private static final String NO_PROPERTIES_FILE_ERROR_MESSAGE = "No Configuration Properties file found.";


	private File configFileCommandLine;

	private String proxlWebAppUrl;
	
	private String proxlUploadBaseDir;

	private boolean submitterSameMachine;
	
	private String writeProxlUploadDirFilename;
	

	private boolean configured;
	

	public String getProxlWebAppUrl() {
		if ( ! configured ) {
			
			throw new IllegalStateException("readConfigParams() not called or failed");
		}
		return proxlWebAppUrl;
	}
	public String getProxlUploadBaseDir() {
		if ( ! configured ) {
			
			throw new IllegalStateException("readConfigParams() not called or failed");
		}
		return proxlUploadBaseDir;
	}
	public boolean isSubmitterSameMachine() {
		if ( ! configured ) {
			
			throw new IllegalStateException("readConfigParams() not called or failed");
		}
		return submitterSameMachine;
	}
	public String getWriteProxlUploadDirFilename() {
		if ( ! configured ) {
			
			throw new IllegalStateException("readConfigParams() not called or failed");
		}
		return writeProxlUploadDirFilename;
	}


	public void readConfigParams() throws Exception {

		if ( configured ) {

			return;
		}

		String propertiesFilenameMaybeWithPath = null;

		Properties configProps = null;

		InputStream propertiesFileAsStream = null;

		try {

			if ( configFileCommandLine != null ) {

				propertiesFilenameMaybeWithPath = configFileCommandLine.getAbsolutePath();

				if ( ! configFileCommandLine.exists() ) {

					System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

					String msg = "Properties file not found: " + configFileCommandLine.getAbsolutePath();
					log.error( msg );

					throw new ProxlSubImportConfigException( msg );
				}

				try {

					propertiesFileAsStream = new FileInputStream( configFileCommandLine );

				} catch ( FileNotFoundException e ) {

					System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

					String msg = "Properties file not found: " + configFileCommandLine.getAbsolutePath() + " exception: " + e.toString();
					//					log.error( msg, e );

					throw new ProxlSubImportConfigException( msg );
				}

			} else {

				//  Get config file from class path

				propertiesFilenameMaybeWithPath = ConfigPropertiesConstants.CONFIG_FILENAME;

				ClassLoader thisClassLoader = this.getClass().getClassLoader();

				URL configFileUrlObjUrlLocal = thisClassLoader.getResource( ConfigPropertiesConstants.CONFIG_FILENAME );

				if ( configFileUrlObjUrlLocal == null ) {

					System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

					String msg = "Properties file '" + ConfigPropertiesConstants.CONFIG_FILENAME + "' not found in class path.";
					//					log.error( msg );

					throw new ProxlSubImportConfigException( msg );

				} else {

					if ( log.isInfoEnabled() ) {

						log.info( "Properties file '" + ConfigPropertiesConstants.CONFIG_FILENAME + "' found, load path = " + configFileUrlObjUrlLocal.getFile() );
					}
				}


				propertiesFilenameMaybeWithPath = configFileUrlObjUrlLocal.getFile();


				propertiesFileAsStream = configFileUrlObjUrlLocal.openStream();


				if ( propertiesFileAsStream == null ) {

					System.err.println( NO_PROPERTIES_FILE_ERROR_MESSAGE );

					String msg = "Properties file '" + ConfigPropertiesConstants.CONFIG_FILENAME + "' not found in class path.";

					//					log.error( msg );

					throw new ProxlSubImportConfigException( msg );
				}
			}


			configProps = new Properties();

			configProps.load( propertiesFileAsStream );

		} catch (IOException e) {

			log.error( "In init(),   Properties file '" 
					+ propertiesFilenameMaybeWithPath + "', IOException: " + e.toString(), e );

			throw new ProxlSubImportConfigException( e );

		} finally {

			if ( propertiesFileAsStream != null ) {

				try {
					propertiesFileAsStream.close();
				} catch (IOException e) {

					log.error( "In init(), propertiesFileAsStream.close():   Properties file '" 
							+ propertiesFilenameMaybeWithPath + "', IOException: " + e.toString(), e );

					throw new ProxlSubImportConfigException( e );
				}
			}
		}


		proxlWebAppUrl = configProps.getProperty( ConfigPropertiesConstants.CONFIG_PARAM_PROXL_WEB_APP_URL );
		
		if ( proxlWebAppUrl != null ) {
			
			proxlWebAppUrl = proxlWebAppUrl.trim();
		}

		proxlUploadBaseDir = configProps.getProperty( ConfigPropertiesConstants.CONFIG_PARAM_PROXL_UPLOAD_BASE_DIR );
		
		if ( proxlUploadBaseDir != null ) {
			
			proxlUploadBaseDir = proxlUploadBaseDir.trim();
		}

		if ( StringUtils.isEmpty( proxlWebAppUrl ) ) {

			String msg = "For Config Properties file: parameter '" + ConfigPropertiesConstants.CONFIG_PARAM_PROXL_WEB_APP_URL + "' is not provided or is empty string.";
			System.err.println( msg );
			throw new ProxlSubImportConfigException(msg);
		}
		
		if ( StringUtils.isNotEmpty( proxlUploadBaseDir ) ) {

			submitterSameMachine = true;
		}
		

		writeProxlUploadDirFilename = configProps.getProperty( ConfigPropertiesConstants.CONFIG_PARAM_WRITE_PROXL_UPLOAD_DIR_FILENAME );
		
		if ( writeProxlUploadDirFilename != null ) {
			
			writeProxlUploadDirFilename = writeProxlUploadDirFilename.trim();
		}

		
		configured = true;
	}
	
	
	public File getConfigFileCommandLine() {
		return configFileCommandLine;
	}
	public void setConfigFileCommandLine(File configFileCommandLine) {
		this.configFileCommandLine = configFileCommandLine;
	}

}
