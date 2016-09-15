package org.yeastrc.xlink.www.proxl_xml_file_import.constants;

public class ProxlXMLFileUploadMaxFileSizeConstants {


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
	
}
