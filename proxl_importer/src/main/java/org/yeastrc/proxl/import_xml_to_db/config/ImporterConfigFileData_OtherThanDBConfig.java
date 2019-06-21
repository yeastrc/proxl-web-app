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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Values from the config file, other than the DB configuration, which is processed elsewhere
 *
 */
public class ImporterConfigFileData_OtherThanDBConfig {
	
	private static final Logger log = LoggerFactory.getLogger( ImporterConfigFileData_OtherThanDBConfig.class );

	private static boolean spectralStorageService_sendScanFileLocation;
	
	private static String spectralStorageService_sendScanFileLocation_IfPathStartsWith;
	

	private static boolean configured = false;


	public static boolean isSpectralStorageService_sendScanFileLocation() {
		if ( ! configured ) {
			String msg = "ImporterConfigFileData_OtherThanDBConfig not configured";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		return spectralStorageService_sendScanFileLocation;
	}


	public static String getSpectralStorageService_sendScanFileLocation_IfPathStartsWith() {
		if ( ! configured ) {
			String msg = "ImporterConfigFileData_OtherThanDBConfig not configured";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		return spectralStorageService_sendScanFileLocation_IfPathStartsWith;
	}


	public static boolean isConfigured() {
		return configured;
	}

	//  Setters Package Private

	static void setSpectralStorageService_sendScanFileLocation(boolean spectralStorageService_sendScanFileLocation) {
		ImporterConfigFileData_OtherThanDBConfig.spectralStorageService_sendScanFileLocation = spectralStorageService_sendScanFileLocation;
	}


	static void setSpectralStorageService_sendScanFileLocation_IfPathStartsWith(
			String spectralStorageService_sendScanFileLocation_IfPathStartsWith) {
		ImporterConfigFileData_OtherThanDBConfig.spectralStorageService_sendScanFileLocation_IfPathStartsWith = spectralStorageService_sendScanFileLocation_IfPathStartsWith;
	}


	static void setConfigured(boolean configured) {
		ImporterConfigFileData_OtherThanDBConfig.configured = configured;
	}
}
