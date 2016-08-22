package org.yeastrc.xlink.www.proxl_xml_file_import.constants;

//import java.text.NumberFormat;

public class ProxlXMLFileUploadWebConstants {

	
	public static final long MAX_PROXL_XML_FILE_UPLOAD_SIZE = ( 150 * ( (long) ( 1000 * 1000 ) ) ); // 150MB max


	// Must cast some part to long. 
	// Otherwise int is used and the value overflows the int max and the resulting value is smaller than expected.
	public static final long MAX_SCAN_FILE_UPLOAD_SIZE = ( 10 * ( (long) ( 1000 * 1000 * 1000 ) ) ); // 10GB max

//	public static final int MAX_SCAN_FILE_UPLOAD_SIZE = ( 2 * 10 * 1000  ); // temp smaller max of 20KB
	

	//  !!!!   Must keep these in sync with the numbers above
	
	public static final String MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED = "150MB";

	
	public static final String MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED = "10GB";


//	public static final String MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED = NumberFormat.getInstance().format(MAX_PROXL_XML_FILE_UPLOAD_SIZE);
//
//	
//	public static final String MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED = NumberFormat.getInstance().format(MAX_SCAN_FILE_UPLOAD_SIZE);

	
	//   PROXL_XML

	public static long get_PROXL_XML_MAX_FILE_UPLOAD_SIZE() {
		return MAX_PROXL_XML_FILE_UPLOAD_SIZE;
	}

	public static String get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_AS_STRING() {
		return Long.toString( MAX_PROXL_XML_FILE_UPLOAD_SIZE );
	}
	
	public static String get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED() {
		return MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED;
	}
	
	//  SCAN
	
	public static long get_SCAN_MAX_FILE_UPLOAD_SIZE() {
		return MAX_SCAN_FILE_UPLOAD_SIZE;
	}

	public static String get_MAX_SCAN_FILE_UPLOAD_SIZE_AS_STRING() {
		return Long.toString( MAX_SCAN_FILE_UPLOAD_SIZE );
	}
	
	public static String get_MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED() {
		return MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED;
	}
	
	
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
	
}
