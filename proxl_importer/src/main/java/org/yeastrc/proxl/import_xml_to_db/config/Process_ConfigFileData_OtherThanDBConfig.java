/*
* Original author: Daniel Jaschob <djaschob .at. uw.edu>
*                  
* Copyright 2019 University of Washington - Seattle, WA
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.yeastrc.proxl.import_xml_to_db.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process Values from the config file, other than the DB configuration, which is processed elsewhere
 *
 */
public class Process_ConfigFileData_OtherThanDBConfig {

	private static final Logger log = LoggerFactory.getLogger( Process_ConfigFileData_OtherThanDBConfig.class );

	private static final String PROPERTY_NAME__SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION = "spectral.storage.send.scan.file.location";

	private static final String SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION__TRUE = "true";

	private static final String PROPERTY_NAME__SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION_IF_STARTS_WITH = 
			"spectral.storage.send.scan.file.location.if.path.starts.with";

	
	/**
	 * private constructor
	 */
	private Process_ConfigFileData_OtherThanDBConfig() { }

	/**
	 * @return newly created instance
	 */
	public static Process_ConfigFileData_OtherThanDBConfig getInstance() { 
		return new Process_ConfigFileData_OtherThanDBConfig(); 
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
	public void processConfigFile( File configFileFromCommandLine ) throws Exception {

		InputStream propertiesFileAsStream = null;
		try {

			propertiesFileAsStream = new FileInputStream( configFileFromCommandLine );

			Properties configProps = new Properties();
			configProps.load( propertiesFileAsStream );

			String propertyValue = null;

			propertyValue = configProps.getProperty( PROPERTY_NAME__SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				if ( SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION__TRUE.equals( propertyValue ) ) {
					ImporterConfigFileData_OtherThanDBConfig.setSpectralStorageService_sendScanFileLocation(true);
				}
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION_IF_STARTS_WITH );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				ImporterConfigFileData_OtherThanDBConfig.setSpectralStorageService_sendScanFileLocation_IfPathStartsWith( propertyValue );

				log.warn( "INFO: Config file property '" 
						+ PROPERTY_NAME__SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION_IF_STARTS_WITH
						+ "' is set to '" 
						+ SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION__TRUE
						+ "'.");
			}

			ImporterConfigFileData_OtherThanDBConfig.setConfigured(true);

			if ( ImporterConfigFileData_OtherThanDBConfig.isSpectralStorageService_sendScanFileLocation()
					&& ImporterConfigFileData_OtherThanDBConfig.getSpectralStorageService_sendScanFileLocation_IfPathStartsWith() != null ) {

				log.warn( "INFO: Config file property '" 
						+ PROPERTY_NAME__SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION
						+ "' is set to '" 
						+ SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION__TRUE
						+ "'.  "
						+ " and config file property '" 
						+ PROPERTY_NAME__SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION_IF_STARTS_WITH
						+ "' is set to '" 
						+ SPECTRAL_STORAGE_SEND_SCAN_FILE_LOCATION__TRUE
						+ "'."
						+ "  So will be sending Scan file location to Spectral Storage Service.  If the location is not accepted, the scan file contents will be sent."
						);
			}
			
		} catch ( RuntimeException e ) {

			log.error( "In processConfigFile(),   Properties file '" + configFileFromCommandLine.getAbsolutePath() + "', exception: " + e.toString(), e );

			throw e;
		}
		
	}
}
