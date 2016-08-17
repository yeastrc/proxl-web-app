package org.yeastrc.xlink.base.config_system_table_common_access;

import java.util.HashSet;
import java.util.Set;

public class ConfigSystemsKeysSharedConstants {


	//////   Any plain text inputs need to be added to textConfigKeys in the "static {}" at the bottom
	
	

	////  Proxl XML File Import Set Up keys

	public static final String PROXL_XML_FILE_IMPORT_TEMP_DIR_KEY = "proxl_xml_file_import_temp_dir";


	//   Lists of config keys for validation on save
	
	public static final Set<String> textConfigKeys = new HashSet<>();
	
	static {
		textConfigKeys.add( PROXL_XML_FILE_IMPORT_TEMP_DIR_KEY );
		
	}
}
