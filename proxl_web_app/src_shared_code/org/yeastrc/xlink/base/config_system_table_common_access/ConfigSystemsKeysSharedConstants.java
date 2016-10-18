package org.yeastrc.xlink.base.config_system_table_common_access;

import java.util.HashSet;
import java.util.Set;

public class ConfigSystemsKeysSharedConstants {


	//////   Any plain text inputs need to be added to textConfigKeys in the "static {}" at the bottom
	
	////  Proxl XML File Import Set Up keys

	public static final String PROXL_XML_FILE_IMPORT_TEMP_DIR_KEY = "proxl_xml_file_import_temp_dir";

	public static final String SCAN_FILE_IMPORT_ALLOWED_VIA_WEB_SUBMIT_KEY = "scan_file_import_allowed_via_web_submit";

	////              Delete uploaded files after Import key  -  Specific Values allowed

	public static final String IMPORT_DELETE_UPLOADED_FILES = "import_delete_uploaded_files_after_import";

	////              Terms of Service Enabled key  -  Specific Values allowed

	public static final String TERMS_OF_SERVICE_ENABLED = "terms_of_service_enabled";




	//   Lists of config keys for validation on save
	
	public static final Set<String> textConfigKeys = new HashSet<>();
	
	static {
		textConfigKeys.add( PROXL_XML_FILE_IMPORT_TEMP_DIR_KEY );
		textConfigKeys.add( SCAN_FILE_IMPORT_ALLOWED_VIA_WEB_SUBMIT_KEY );
		
	}
}
