package org.yeastrc.proxl.import_xml_to_db_runner_pgm.constants;

public class GetImportStatus_FileConstants {

	public static final String GET_IMPORT_STATUS_FILENAME = "proxl_get_import_last_check_status.txt";

	public static final String GET_IMPORT_STATUS_NONE_FOUND_REQUEST_TO_PROCESS_TEXT = "Import Request To Process: NONE Found";

	public static final String GET_IMPORT_STATUS_YES_FOUND_REQUEST_TO_PROCESS_TEXT = "Import Request To Process: YES Found: Request Id: ";
	
	public static final String GET_IMPORT_STATUS_YES_ERROR_CHECKING_FOR_REQUEST_TEXT = 
			"Import Request To Process: ERROR checking for request: ";

	public static final String GET_IMPORT_STATUS_YES_ERROR_PROCESSING_REQUEST_TEXT = 
			"Import Request To Process: ERROR processing request: ";
}
