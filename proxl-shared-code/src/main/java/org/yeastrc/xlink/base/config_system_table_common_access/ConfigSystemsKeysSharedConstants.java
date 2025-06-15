package org.yeastrc.xlink.base.config_system_table_common_access;

import java.util.HashSet;
import java.util.Set;

public class ConfigSystemsKeysSharedConstants {


	//////   Any plain text inputs need to be added to textConfigKeys in the "static {}" at the bottom
	
	////  Proxl Cache Files Root Directory - The Root directory that all files used for caching are writen to.
	
	public static final String CACHE_FILES_ROOT_DIRECTORY = "cache_files_root_directory";

	////  Proxl Spectal Storage Service Accept Import Base URL - For Importer to submit scan file for import
	////             URL used to communicate to Spectral Storage Service using Class CallSpectralStorageAcceptImportWebservice
	
	public static final String SPECTRAL_STORAGE_SERVICE_ACCEPT_IMPORT_BASE_URL =
			"spectral_storage_service_accept_import_base_url";

	////  Proxl Spectal Storage Service Base URL - 
	////             URL used to communicate to Spectral Storage Service using Class CallSpectralStorageWebservice
	
	public static final String SPECTRAL_STORAGE_SERVICE_GET_DATA_BASE_URL = 
			"spectral_storage_service_get_data_base_url";
	
	////  Proxl XML File Import Set Up keys

	public static final String file_import_proxl_xml_scans_TEMP_DIR_KEY = "file_import_proxl_xml_scans_temp_dir";

	public static final String SCAN_FILE_IMPORT_ALLOWED_VIA_WEB_SUBMIT_KEY = "scan_file_import_allowed_via_web_submit";

	

	public static final String PROXL_XML_FILE_MAX_FILE_SIZE_IN_GB_KEY = "proxl_xml_file_max_file_size_in_gb";

	public static final String SCAN_FILE_MAX_FILE_SIZE_IN_GB_KEY = "scan_file_max_file_size_in_gb";

	
	
	////              Delete uploaded files after Import key  -  Specific Values allowed

	public static final String IMPORT_DELETE_UPLOADED_FILES = "import_delete_uploaded_files_after_import";

	////              Terms of Service Enabled key  -  Specific Values allowed

	public static final String TERMS_OF_SERVICE_ENABLED = "terms_of_service_enabled";




	//   Lists of config keys for validation on save
	
	public static final Set<String> textConfigKeys = new HashSet<>();
	
	static {
		textConfigKeys.add( CACHE_FILES_ROOT_DIRECTORY );
		textConfigKeys.add( SPECTRAL_STORAGE_SERVICE_ACCEPT_IMPORT_BASE_URL );
		textConfigKeys.add( SPECTRAL_STORAGE_SERVICE_GET_DATA_BASE_URL );

		textConfigKeys.add( PROXL_XML_FILE_MAX_FILE_SIZE_IN_GB_KEY );
		textConfigKeys.add( SCAN_FILE_MAX_FILE_SIZE_IN_GB_KEY );

		textConfigKeys.add( file_import_proxl_xml_scans_TEMP_DIR_KEY );
		textConfigKeys.add( SCAN_FILE_IMPORT_ALLOWED_VIA_WEB_SUBMIT_KEY );
		
	}
}
