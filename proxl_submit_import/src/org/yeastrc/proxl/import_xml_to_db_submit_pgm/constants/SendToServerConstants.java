package org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants;

/**
 * Keep in sync with server
 *
 */
public class SendToServerConstants {

	public static final String UPLOAD_FILE_QUERY_PARAMETER_UPLOAD_KEY = "upload_key";
	public static final String UPLOAD_FILE_QUERY_PARAMETER_PROJECT_ID = "project_id";
	public static final String UPLOAD_FILE_QUERY_PARAMETER_FILE_INDEX = "file_index";
	public static final String UPLOAD_FILE_QUERY_PARAMETER_FILE_TYPE = "file_type";
	public static final String UPLOAD_FILE_QUERY_PARAMETER_FILENAME = "filename";
	

	/**
	 * Form name for uploaded file on submitting machine ( Java Canonical Path )
	 */
	public static final String UPLOAD_FILE_FORM_NAME_UPLOADED_FILENAME_W_PATH_CANONICAL = 
			"canonicalFilename_W_Path_OnSubmitMachine";
	/**
	 * Form name for uploaded file on submitting machine ( Java Absolute Path )
	 */
	public static final String UPLOAD_FILE_FORM_NAME_UPLOADED_FILENAME_W_PATH_ABSOLUTE = 
			"absoluteFilename_W_Path_OnSubmitMachine";

	/**
	 * Form name for uploaded file
	 */
	public static final String UPLOAD_FILE_FORM_NAME = "uploadFile";
	

	public static final String ENCODING_CHARACTER_SET = "UTF-8";

}
