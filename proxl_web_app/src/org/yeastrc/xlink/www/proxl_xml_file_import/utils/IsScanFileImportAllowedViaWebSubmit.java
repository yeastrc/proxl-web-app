package org.yeastrc.xlink.www.proxl_xml_file_import.utils;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;

/**
 * 
 *
 */
public class IsScanFileImportAllowedViaWebSubmit {

	private static final Logger log = Logger.getLogger(IsScanFileImportAllowedViaWebSubmit.class);



	private static final IsScanFileImportAllowedViaWebSubmit instance = new IsScanFileImportAllowedViaWebSubmit();

	private IsScanFileImportAllowedViaWebSubmit() { }
	public static IsScanFileImportAllowedViaWebSubmit getInstance() { return instance; }


	/**
	 * @return
	 */
	public boolean isScanFileImportAllowedViaWebSubmit() {
		
		
		try {

			String scanFileImportAllowedViaWebSubmitString = ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.SCAN_FILE_IMPORT_ALLOWED_VIA_WEB_SUBMIT_KEY );
			
			if ( ConfigSystemsValuesSharedConstants.TRUE.equals( scanFileImportAllowedViaWebSubmitString ) ) {
				
				return true;
			}
			
			return false;

		} catch ( Exception e ) {
			
			String msg = "Exception getting configSystem value for isScanFileImportAllowedViaWebSubmit()";
			log.error( msg, e );

			return false;
		}
	}

}
