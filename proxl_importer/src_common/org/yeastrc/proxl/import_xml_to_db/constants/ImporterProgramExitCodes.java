package org.yeastrc.proxl.import_xml_to_db.constants;

public class ImporterProgramExitCodes {


	public static final int PROGRAM_EXIT_CODE_DEFAULT_NO_ERRORS_OR_WARNINGS = 0;

	public static final int PROGRAM_EXIT_CODE_HELP = 1;

	
	public static final int PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES = 2;

	public static final int PROGRAM_EXIT_CODE_INVALID_CONFIGURATION_PARAMETER_VALUES = 3;

	public static final int PROGRAM_EXIT_CODE_PROJECT_NOT_ALLOW_IMPORT = 4;
	
	/**
	 * The Proxl XML file or Scan Files contain data errors
	 */
	public static final int PROGRAM_EXIT_CODE_DATA_ERROR = 6;

	public static final int PROGRAM_EXIT_CODE_SYSTEM_ERROR = 10;
	

	/**
	 * The importer process received a TERM or other signal that triggered the thread
	 * registered in the shutdown hook to run
	 */
	public static final int PROGRAM_EXIT_CODE_SHUTDOWN_REQUESTED_USING_PROCESS_TERM = 20;
	

}
