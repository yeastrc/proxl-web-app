package org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants;

public class ProxlXMLFileUploadWebConstants {

	
	
	public static final String UPLOAD_FILE_FORM_NAME = "uploadFile";


	public static final String UPLOAD_FILE_TEMP_BASE_DIR = "upload_file_temp_base_dir";
	
	
	/**
	 * Prefix for temp subdir per request
	 */
	public static final String UPLOAD_FILE_TEMP_SUB_DIR_PREFIX = "up_tmp_";

	/**
	 * File created when the temp subdir per request is created for create date/time tracking 
	 */
	public static final String UPLOAD_FILE_TEMP_SUB_DIR_CREATE_TRACKING_FILE = "up_tmp_created_tracking.txt";

	
	public static final String UPLOAD_FILE_DATA_FILE_PREFIX = "uploaded_file__data_file_";
	public static final String UPLOAD_FILE_DATA_FILE_SUFFIX = ".xml";
	
	
	public static final String UPLOAD_PROXL_XML_FILE_TEMP_FILENAME_PREFIX = "uploaded_proxl_xml_file_";
	public static final String UPLOAD_PROXL_XML_FILE_TEMP_FILENAME_SUFFIX = ".xml";

	public static final String UPLOAD_SCAN_FILE_TEMP_FILENAME_PREFIX = "uploaded_scan_file_";
	
	
	
	public static final String UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML = ".mzML"; 
	public static final String UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML = ".mzXML";
	
	////////////////////
	
	//  Contents to add to import work dir

	/**
	 * File created when the submit is for the same machine
	 * 
	 *  This file contains the list of files to be imported, since they are not copied to the import dir
	 */
	public static final String IMPORT_FILE_LIST_FILE = "import_file_list.txt";

}
