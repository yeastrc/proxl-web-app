package org.yeastrc.xlink.base.file_import_proxl_xml_scans.utils;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemTableGetValueCommon;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadCommonConstants;
/**
 * 
 *
 */
public class Proxl_XML_ImporterWrkDirAndSbDrsCmmn {
	
	private static final Logger log = Logger.getLogger( Proxl_XML_ImporterWrkDirAndSbDrsCmmn.class );
	//  private constructor
	private Proxl_XML_ImporterWrkDirAndSbDrsCmmn() { }
	/**
	 * @return newly created instance
	 */
	public static Proxl_XML_ImporterWrkDirAndSbDrsCmmn getInstance() { 
		return new Proxl_XML_ImporterWrkDirAndSbDrsCmmn(); 
	}
	/**
	 * @return true if valid, otherwise throws exception
	 * @throws Exception
	 */
	public boolean validate_Proxl_XML_Importer_Work_Directory() throws Exception {
//		File tempDir = 
				get_Proxl_XML_Importer_Work_Directory();
		return true;
	}
	
	/**
	 * @return File pointing to temp dir, otherwise throws Exception
	 * @throws Exception
	 */
	public File get_Proxl_XML_Importer_Work_Directory() throws Exception {
		String proxlXMLFileImportTempDir = null; 
		try {
			proxlXMLFileImportTempDir = ConfigSystemTableGetValueCommon.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.file_import_proxl_xml_scans_TEMP_DIR_KEY );
		} catch (Exception e ) {
			String msg = "Exception getting file upload temp dir for validation";
			log.error( msg, e );
			throw e;
		}
		if ( StringUtils.isEmpty( proxlXMLFileImportTempDir ) ) {
			String msg = "Proxl_XML File _Importer_Work_Directory not found in configuration table. config_key: " 
					+ ConfigSystemsKeysSharedConstants.file_import_proxl_xml_scans_TEMP_DIR_KEY ;
			log.error( msg );
			throw new Exception(msg);
		}
		File tempDir = new File( proxlXMLFileImportTempDir );
		if ( ! tempDir.exists() ) {
			String msg = "Proxl_XML File _Importer_Work_Directory does not exist for 'config_key' in configuration table : '" 
					+ ConfigSystemsKeysSharedConstants.file_import_proxl_xml_scans_TEMP_DIR_KEY 
					+ "'.  Proxl_XML File _Importer_Work_Directory: " + tempDir.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
		return tempDir;
	}
	
	/**
	 * @return
	 */
	public String getDirForImportTrackingId( int importTrackingId ) {
		String dirName = ProxlXMLFileUploadCommonConstants.IMPORT_DIR_FOR_ID + importTrackingId;
		return dirName;
	}
}
