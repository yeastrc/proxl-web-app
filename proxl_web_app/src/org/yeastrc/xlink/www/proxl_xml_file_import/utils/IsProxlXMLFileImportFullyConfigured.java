package org.yeastrc.xlink.www.proxl_xml_file_import.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;

/**
 * 
 *
 */
public class IsProxlXMLFileImportFullyConfigured {

	private static final Logger log = Logger.getLogger(IsProxlXMLFileImportFullyConfigured.class);



	private static final IsProxlXMLFileImportFullyConfigured instance = new IsProxlXMLFileImportFullyConfigured();

	private IsProxlXMLFileImportFullyConfigured() { }
	public static IsProxlXMLFileImportFullyConfigured getInstance() { return instance; }


	/**
	 * @return
	 */
	public boolean isProxlXMLFileImportFullyConfigured() {
		
		
		try {

			String proxlXMLFileImportTempDir = ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.PROXL_XML_FILE_IMPORT_TEMP_DIR_KEY );
			
			if ( proxlXMLFileImportTempDir != null ) {
				
				if ( StringUtils.isNotEmpty( proxlXMLFileImportTempDir.trim() ) ) {
				
					return true;
				}
			}
			
			return false;

		} catch ( Exception e ) {
			
			String msg = "Exception getting configSystem value for isProxlXMLFileImportFullyConfigured()";
			log.error( msg, e );

			return false;
		}
	}

}
