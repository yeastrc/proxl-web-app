package org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants;

/**
 * Keep in sync with server
 *
 */
public class SendToServerConstants {

	public static final String UPLOAD_FILE_HEADER_NAME_UPLOAD_KEY = "X-Proxl-upload_key";
	public static final String UPLOAD_FILE_HEADER_NAME_PROJECT_ID = "X-Proxl-project_id";
	public static final String UPLOAD_FILE_HEADER_NAME_FILE_INDEX = "X-Proxl-file_index";
	public static final String UPLOAD_FILE_HEADER_NAME_FILE_TYPE = "X-Proxl-file_type";
	public static final String UPLOAD_FILE_HEADER_NAME_FILENAME = "X-Proxl-filename";

	/**
	 * Header name for uploaded file on submitting machine ( Java Canonical Path )
	 */
	public static final String UPLOAD_FILE_HEADER_NAME_UPLOADED_FILENAME_W_PATH_CANONICAL = 
			"X-Proxl-uploadFileWPathCanonical";
	/**
	 * Header name for uploaded file on submitting machine ( Java Absolute Path )
	 */
	public static final String UPLOAD_FILE_HEADER_NAME_UPLOADED_FILENAME_W_PATH_ABSOLUTE = 
			"X-Proxl-uploadFileWPathAbsolute";
	
	
	/**
	 * Form name for uploaded file
	 */
	public static final String UPLOAD_FILE_FORM_NAME = "uploadFile";
	

	public static final String ENCODING_CHARACTER_SET = "UTF-8";

}
