package org.yeastrc.proxl.import_xml_to_db.program_default;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.DataErrorsFileConstants;
import org.yeastrc.proxl.import_xml_to_db.constants.ImporterProgramExitCodes;
import org.yeastrc.proxl.import_xml_to_db.constants.ScanFilenameConstants;
import org.yeastrc.proxl.import_xml_to_db.dao.ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO;
import org.yeastrc.proxl.import_xml_to_db.database_update_with_transaction_services.UpdateTrackingTrackingRunRecordsDBTransaction;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderFromPropertiesFile;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderPropertiesFileContentsErrorException;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderPropertiesFileErrorException;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptidePSMCutoffValue;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptidePSMCutoffValues;
import org.yeastrc.proxl.import_xml_to_db.exception.ProxlImporterProjectNotAllowImportException;
import org.yeastrc.proxl.import_xml_to_db.exception.ProxlImporterProxlXMLDeserializeFailException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.importer_core_entry_point.ImporterCoreEntryPoint;
import org.yeastrc.proxl.import_xml_to_db.objects.ImportResults;
import org.yeastrc.proxl.import_xml_to_db.objects.ProxlXMLFileFileContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.ScanFileFileContainer;
import org.yeastrc.proxl.import_xml_to_db.proxl_xml_file_import.dao.ProxlXMLFileImportTrackingSingleFile_Importer_DAO;
import org.yeastrc.proxl.import_xml_to_db.proxl_xml_file_import.run_importer_to_importer_file_data.RunImporterToImporterFileRoot;
import org.yeastrc.proxl.import_xml_to_db.proxl_xml_file_import.run_importer_to_importer_file_data.RunImporterToImporterParameterNamesConstants;
import org.yeastrc.proxl.import_xml_to_db.utils.SHA1SumCalculator;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.xlink.base.proxl_xml_file_import.dao.ProxlXMLFileImportTrackingRun_Base_DAO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dao.ProxlXMLFileImportTrackingSingleFileDAO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dao.ProxlXMLFileImportTracking_Base_DAO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportRunSubStatus;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;

/**
 * Default main entry point for the Proxl XML Importer program 
 *
 */
public class ImporterDefaultMainProgramEntry {
	
	private static final Logger log = Logger.getLogger(ImporterDefaultMainProgramEntry.class);
	
	///////  !!!!!!!!!!!!!!!!!!!
	//   Importer program exit codes moved to class ImporterProgramExitCodes
	
	private static final String FOR_HELP_STRING = "For help, run without any parameters, -h, or --help";
	
	/**
	 * Help file path in the jar.  Help file copied to sysout when -h on command line
	 */
	private static final String HELP_FILE_WITH_PATH = "/help_output_import_proxl_xml.txt";
	public static final String DO_NOT_USE_CUTOFFS_IN_INPUT_FILE = "do-not-use-cutoff-in-input-file";
	public static final String SKIP_POPULATING_PATH_ON_SEARCH_CMD_LINE_PARAM_STRING = "skip_populating_path_on_search";
	private static final String PROXL_DB_NAME_CMD_LINE_PARAM_STRING = "proxl_db_name";
	
	/**
	 * private constructor
	 */
	private ImporterDefaultMainProgramEntry() { }
	/**
	 * Static singleton instance
	 */
	private static final ImporterDefaultMainProgramEntry _instance = new ImporterDefaultMainProgramEntry();
	/**
	 * Static get singleton instance
	 * @return
	 */
	public static ImporterDefaultMainProgramEntry getInstance() {
		return _instance; 
	}
	//	private static boolean databaseConnectionFactoryCreated = false;
	/**
	 * The importer process received a TERM or other signal that triggered the thread
	 * registered in the shutdown hook to run
	 */
	private volatile boolean shutdownReceivedViaShutdownHook = false;
	private volatile ImporterCoreEntryPoint importerCoreEntryPoint;
	
	/**
	 * If set to false, assume it is already created
	 */
	private boolean createDatabaseConnectionFactory = true;
	public void databaseConnectionFactoryAlreadyCreated() {
		createDatabaseConnectionFactory = false;
		//		databaseConnectionFactoryCreated = true;
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ImporterDefaultMainProgramEntry importerDefaultMainProgramEntry = ImporterDefaultMainProgramEntry.getInstance();
		importerDefaultMainProgramEntry.mainNotStaticInternal( args );
	}
	
	/**
	 * @param args
	 * @return insertedSearchId
	 * @throws Exception 
	 */
	private void mainNotStaticInternal( String[] args  ) throws Exception {
		ImportResults importResults = importerDefaultMainProgramEntry( args );
		if ( shutdownReceivedViaShutdownHook 
				&& importResults.getProgramExitCode() != ImporterProgramExitCodes.PROGRAM_EXIT_CODE_DEFAULT_NO_ERRORS_OR_WARNINGS ) {
			//  Override the error code since forced by processing shutdown request on shutdown hook
			System.exit( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_SHUTDOWN_REQUESTED_USING_PROCESS_TERM );
		}
		System.exit( importResults.getProgramExitCode() );
	}
	
	/**
	 * @param args
	 * @return insertedSearchId
	 * @throws Exception 
	 */
	public ImportResults importerDefaultMainProgramEntry( String[] args  ) throws Exception {
		ProxlInput proxlInputForImportParam = null;
		return importerDefaultMainProgramEntryPassingArgsAndProxlXMLObject( args, proxlInputForImportParam );
	}
	
	/**
	 * @param args
	 * @return insertedSearchId
	 * @throws Exception 
	 */
	public ImportResults importerDefaultMainProgramEntryPassingArgsAndProxlXMLObject( 
			String[] args, 
			ProxlInput proxlInputForImportParam
			) throws Exception {
		
		ImportResults importResults = new ImportResults();
		
		//  Default ProgramExitCode to PROGRAM_EXIT_CODE_DEFAULT_NO_ERRORS_OR_WARNINGS
		//  Update ProgramExitCode below if need to use other exit code
		importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_DEFAULT_NO_ERRORS_OR_WARNINGS );
		
		boolean successfulImport = false;
		
		//  Records for the Import Tracking tables used for imports submitted in the web app
		ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO = null;
		ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO = null;
		
		//  Search Name from proxlXMLFileImportTrackingDTO or command Line
		String searchNameOverrideValue = null;
		//  "search_path" field from "proxl_xml_file_import_tracking" table, if import run from Run Import pgm
		String importDirectoryOverrideValue = null;
		
		//  TODO  Not currently used
//		String outputImportResultFileName = null;
		
		Integer userIdInsertingSearch = null;  //  Populated from proxl_xml_file_import_tracking.auth_user_id
		
		String outputDataErrorsFileName = null;
		ImportProgramShutdownThread importProgramShutdownThread = null;
		try {
			if ( args.length == 0 ) {
				printHelp();
				importResults.setImportSuccessStatus(false);
				importResults.setHelpRequestedStatus(true);
				importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_HELP );
				return importResults;  //  EARLY EXIT
			}
			CmdLineParser cmdLineParser = new CmdLineParser();
			CmdLineParser.Option projectIdOpt = cmdLineParser.addIntegerOption( 'p', "project" );	
			CmdLineParser.Option inputProxlFileStringCommandLineOpt = cmdLineParser.addStringOption( 'i', "import-file" );
			CmdLineParser.Option noScanFilesCommandLineOpt = cmdLineParser.addBooleanOption( 'n', "no-scan-files" );
			CmdLineParser.Option inputScanFileStringCommandLineOpt = cmdLineParser.addStringOption( 's', "scan-file" );
			CmdLineParser.Option dbConfigFileNameCommandLineOpt = cmdLineParser.addStringOption( 'c', "config" );
			
			//  TODO  Not currently used
//			CmdLineParser.Option outputImportResultFileCommandLineOpt = cmdLineParser.addStringOption( 'Z', "output-import-result-file" );
			CmdLineParser.Option outputDataErrorsFileCommandLineOpt = cmdLineParser.addStringOption( 'Z', "output-data-errors-file" );
			//  'Z' is arbitrary and won't be suggested to user
			CmdLineParser.Option skipPopulatingPathOnSearchLineOpt = cmdLineParser.addBooleanOption( 'Z', SKIP_POPULATING_PATH_ON_SEARCH_CMD_LINE_PARAM_STRING );
			//  'Z' is arbitrary and won't be suggested to user
			CmdLineParser.Option proxlDatabaseNameCommandLineOpt = cmdLineParser.addStringOption( 'Z', PROXL_DB_NAME_CMD_LINE_PARAM_STRING );
			//  'Z' is arbitrary and won't be suggested to user
			CmdLineParser.Option dropPeptideCutoffValueOpt = cmdLineParser.addStringOption( 'Z', "drop-peptide-cutoff" );
			CmdLineParser.Option dropPsmCutoffValueOpt = cmdLineParser.addStringOption( 'Z', "drop-psm-cutoff" );
		//  'Z' is arbitrary and won't be suggested to user
			CmdLineParser.Option doNotUseCutoffInInputFileOpt = cmdLineParser.addBooleanOption( 'Z', DO_NOT_USE_CUTOFFS_IN_INPUT_FILE );
			//  'Z' is arbitrary and won't be suggested to user
			CmdLineParser.Option filenameWithSearchIdToCreateOnSuccessInsertOpt = cmdLineParser.addStringOption( 'Z', "filename-w-srch-id-create-on-success" );
			CmdLineParser.Option verboseOpt = cmdLineParser.addBooleanOption('V', "verbose"); 
			CmdLineParser.Option debugOpt = cmdLineParser.addBooleanOption('D', "debug"); 
			//  'Z' is arbitrary and won't be suggested to user
			CmdLineParser.Option runImporterParamsFileCommandLineOpt = cmdLineParser.addStringOption( 'Z', RunImporterToImporterParameterNamesConstants.RUN_IMPORTER_PARAMS_FILE_PARAM_STRING );
			CmdLineParser.Option helpOpt = cmdLineParser.addBooleanOption('h', "help"); 
			// parse command line options
			try { cmdLineParser.parse(args); }
			catch (IllegalOptionValueException e) {
				System.err.println(e.getMessage());
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				importResults.setImportSuccessStatus( false) ;
				importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
				return importResults;  //  EARLY EXIT
			}
			catch (UnknownOptionException e) {
				System.err.println(e.getMessage());
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				importResults.setImportSuccessStatus( false) ;
				importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
				return importResults;  //  EARLY EXIT
			}
			Boolean help = (Boolean) cmdLineParser.getOptionValue(helpOpt, Boolean.FALSE);
			if(help) {
				printHelp();
				importResults.setImportSuccessStatus( false) ;
				importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_HELP );
				return importResults;  //  EARLY EXIT
			}
			//  Show an error if there is anything on the command line not associated with a parameter
			String[] remainingArgs = cmdLineParser.getRemainingArgs();
			if( remainingArgs.length > 0 ) {
				System.out.println( "Unexpected command line parameters:");
				for ( String remainingArg : remainingArgs ) {
					System.out.println( remainingArg );
				}
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				importResults.setImportSuccessStatus( false) ;
				importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
				return importResults;  //  EARLY EXIT
			}
			Boolean verbose = (Boolean) cmdLineParser.getOptionValue(verboseOpt, Boolean.FALSE);
			Boolean debugValue = (Boolean) cmdLineParser.getOptionValue(debugOpt, Boolean.FALSE);
			if ( verbose != null &&  verbose ) {
				LogManager.getRootLogger().setLevel(Level.INFO);
			}
			if ( debugValue != null &&  debugValue ) {
				LogManager.getRootLogger().setLevel(Level.DEBUG);
			}
			if ( log.isInfoEnabled() ) {
				System.out.println( "Processing " + args.length + " command line arguments." );
				System.out.println( "Processing the following command line arguments:" );
				for ( String arg : args ) {
					System.out.println( arg );
				}
				System.out.println( "End of command line arguments.");
			}
			Boolean doNotUseCutoffInInputFile = (Boolean) cmdLineParser.getOptionValue( doNotUseCutoffInInputFileOpt, Boolean.FALSE);
			Boolean skipPopulatingPathOnSearchLineOptChosen = (Boolean) cmdLineParser.getOptionValue( skipPopulatingPathOnSearchLineOpt, Boolean.FALSE);
			String proxlDatabaseName = (String)cmdLineParser.getOptionValue( proxlDatabaseNameCommandLineOpt );
			String dbConfigFileName = (String)cmdLineParser.getOptionValue( dbConfigFileNameCommandLineOpt );
			String filenameWithSearchIdToCreateOnSuccessInsert = (String)cmdLineParser.getOptionValue( filenameWithSearchIdToCreateOnSuccessInsertOpt );
			File dbConfigFile = null;
			if ( StringUtils.isNotEmpty( dbConfigFileName ) ) {
				dbConfigFile = new File( dbConfigFileName );
				if( ! dbConfigFile.exists() ) {
					System.err.println( "Could not find DB Config File: " + dbConfigFile.getAbsolutePath() );
					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					importResults.setImportSuccessStatus( false) ;
					importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
					return importResults;  //  EARLY EXIT
				}
			}			
			//  TODO  Not currently used
//			outputImportResultFileName = (String)cmdLineParser.getOptionValue( outputImportResultFileCommandLineOpt );
			
			outputDataErrorsFileName = (String)cmdLineParser.getOptionValue( outputDataErrorsFileCommandLineOpt );
			Integer projectId = null;
			Boolean noScanFilesCommandLineOptChosen = null;
			ProxlXMLFileFileContainer mainXMLFileToImportContainer = null;
			List<ScanFileFileContainer> scanFileFileContainerList = null;
			List<File> scanFileList = null;
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues = new DropPeptidePSMCutoffValues();
			//			log.warn( "Log msg to WARN" );
			//			log.error( "Log msg to ERROR" );
			//   add a shutdown hook that will be called either when the operating system sends a SIGKILL signal on Unix or all threads terminate ( normal exit )
			//           Also called when ctrl-c is pressed on Unix or Windows
			//  public void addShutdownHook(Thread hook)
			Thread mainThread = Thread.currentThread();
			importProgramShutdownThread = new ImportProgramShutdownThread();
			importProgramShutdownThread.setMainThread( mainThread );
			Runtime runtime = Runtime.getRuntime();
			runtime.addShutdownHook( importProgramShutdownThread );
			if ( createDatabaseConnectionFactory ) {
				DBConnectionParametersProviderFromPropertiesFile dbConnectionParametersProvider = new DBConnectionParametersProviderFromPropertiesFile();
				if ( dbConfigFile != null ) {
					dbConnectionParametersProvider.setConfigFile( dbConfigFile );
				}
				try {
					dbConnectionParametersProvider.init();
				} catch ( DBConnectionParametersProviderPropertiesFileErrorException e ) {
					importResults.setImportSuccessStatus( false) ;
					if ( dbConfigFile != null ) {
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
					} else {
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_CONFIGURATION_PARAMETER_VALUES );
					}
					return importResults;  //  EARLY EXIT
				} catch ( DBConnectionParametersProviderPropertiesFileContentsErrorException e ) {
					importResults.setImportSuccessStatus( false) ;
					importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_CONFIGURATION_PARAMETER_VALUES );
					return importResults;  //  EARLY EXIT
				} catch ( Exception e ) {
					System.err.println( "Failed processing DB config file." );
					importResults.setImportSuccessStatus( false) ;
					importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_SYSTEM_ERROR );
					return importResults;  //  EARLY EXIT
				}
				if ( StringUtils.isNotEmpty( proxlDatabaseName ) ) {
					dbConnectionParametersProvider.setProxlDbName( proxlDatabaseName );
				}
				ImportDBConnectionFactory importDBConnectionFactory = ImportDBConnectionFactory.getInstance();
				importDBConnectionFactory.setDbConnectionParametersProvider( dbConnectionParametersProvider );
				DBConnectionFactory.setDbConnectionFactoryImpl( importDBConnectionFactory );
				//				databaseConnectionFactoryCreated = true;
			}
			
			///  runImporterParamsFilename is the file that the Run Importer Program creates
			///                            to pass parameter values to the Importer 
			///                            instead of passing them as command line parameters 
			String runImporterParamsFilename = (String)cmdLineParser.getOptionValue( runImporterParamsFileCommandLineOpt );
			
			if ( StringUtils.isNotEmpty( runImporterParamsFilename ) ) {
				
				//  Have a file from the run importer to parse and update the parameter variables with
				
				File runImporterParamsFile = new File( runImporterParamsFilename );
				if( ! runImporterParamsFile.exists() ) {
					System.err.println( "Could not find runImporterParamsFilename File: " + runImporterParamsFile.getAbsolutePath() );
					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					importResults.setImportSuccessStatus( false) ;
					importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
					return importResults;  //  EARLY EXIT
				}
				JAXBContext jaxbContext = JAXBContext.newInstance( RunImporterToImporterFileRoot.class );
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				Object unmarshalledObject = null;
				InputStream inputStream = null;
				try {
					inputStream = new FileInputStream( runImporterParamsFile );
					unmarshalledObject = unmarshaller.unmarshal( inputStream );
				} catch ( Exception e ) {
					throw e;
				} finally {
					if ( inputStream != null ) {
						inputStream.close();
					}
				}
				if ( ! ( unmarshalledObject instanceof RunImporterToImporterFileRoot ) ) {
					String msg = "Object unmarshalled "
							+ " cannot be cast to RunImporterToImporterFileRoot.  unmarshalledObject.getClass().getCanonicalName(): " + unmarshalledObject.getClass().getCanonicalName();
					System.err.println( msg );
					System.out.println( msg );
					throw new Exception(msg);
				}
				RunImporterToImporterFileRoot runImporterToImporterFileRoot = (RunImporterToImporterFileRoot) unmarshalledObject;
				projectId = runImporterToImporterFileRoot.getProjectId();
//				outputImportResultFileName = runImporterToImporterFileRoot.getOutputImportResultFileName();
				outputDataErrorsFileName = runImporterToImporterFileRoot.getOutputDataErrorsFileName();
				if ( runImporterToImporterFileRoot.isSkipPopulatingPathOnSearch() ) {
					skipPopulatingPathOnSearchLineOptChosen = true;
				}
				
//				if ( runImporterToImporterFileRoot.getSystemInStringForShutdown() != null ) {
//					//  Create a thread that will read the system in until the SystemInStringForShutdown String
//					//  is passed and then it will shut down the importer.
//					ShutdownByReadingSystemInSpecificTextThread shutdownByReadingSystemInSpecificTextThread = new ShutdownByReadingSystemInSpecificTextThread();
//					shutdownByReadingSystemInSpecificTextThread.setSystemInStringForShutdown( runImporterToImporterFileRoot.getSystemInStringForShutdown() );
//					shutdownByReadingSystemInSpecificTextThread.start();
//				}
				
				//  Get the Run Importer Tracking Run record and its Tracking record
				int proxlRunImporterTrackingRunId = runImporterToImporterFileRoot.getImportTrackingRunId();
				proxlXMLFileImportTrackingRunDTO = 
						ProxlXMLFileImportTrackingRun_Base_DAO.getInstance().getItem( proxlRunImporterTrackingRunId );
				if ( proxlXMLFileImportTrackingRunDTO == null ) {
					String msg = "proxlRunImporterTrackingRunId not found in database: " + proxlRunImporterTrackingRunId;
					log.error( msg );
					throw new ProxlImporterInteralException( msg );  //  TODO  Change exception class
				}
				int proxlRunImporterTrackingId = proxlXMLFileImportTrackingRunDTO.getProxlXmlFileImportTrackingId();
				proxlXMLFileImportTrackingDTO =
						ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( proxlRunImporterTrackingId );
				if ( proxlXMLFileImportTrackingDTO == null ) {
					String msg = "proxlRunImporterTrackingId not found in database: " + proxlRunImporterTrackingId;
					log.error( msg );
					throw new ProxlImporterInteralException( msg );  //  TODO  Change exception class
				}
				
				userIdInsertingSearch = proxlXMLFileImportTrackingDTO.getAuthUserId();
				
				//  Search Name User entered in form, or null if nothing entered
				searchNameOverrideValue = proxlXMLFileImportTrackingDTO.getSearchName();
				if ( StringUtils.isNotEmpty( proxlXMLFileImportTrackingDTO.getSearchPath() ) ) {
					skipPopulatingPathOnSearchLineOptChosen = false;
					importDirectoryOverrideValue = proxlXMLFileImportTrackingDTO.getSearchPath();
				}
				
				///  Get the Proxl XML file and Scan files
				List<ProxlXMLFileImportTrackingSingleFileDTO> fileDBRecordList = 
						ProxlXMLFileImportTrackingSingleFileDAO.getInstance()
						.getForTrackingId( proxlRunImporterTrackingId );
				ProxlXMLFileImportTrackingSingleFileDTO proxlXMLFileDBRecord = null;
				List<ProxlXMLFileImportTrackingSingleFileDTO> scanFilesDBRecords = new ArrayList<>( fileDBRecordList.size() ); 
				for ( ProxlXMLFileImportTrackingSingleFileDTO fileDBRecordItem : fileDBRecordList ) {
					if ( fileDBRecordItem.getFileType() == ProxlXMLFileImportFileType.PROXL_XML_FILE ) {
						proxlXMLFileDBRecord = fileDBRecordItem;
					} else if ( fileDBRecordItem.getFileType() == ProxlXMLFileImportFileType.SCAN_FILE ) {
						scanFilesDBRecords.add( fileDBRecordItem );
					} else {
						String msg = "Unexpected value in ProxlXMLFileImportTrackingSingleFileDTO.fileType: " + fileDBRecordItem.getFileType();
						log.error( msg );
						throw new ProxlImporterInteralException( msg ); 
					}
				}
				if ( proxlXMLFileDBRecord == null ) {
					String msg = "No Proxl XML File file record for proxlRunImporterTrackingId: " + proxlRunImporterTrackingId;
					log.error( msg );
					throw new ProxlImporterInteralException( msg ); 
				}
				mainXMLFileToImportContainer = new ProxlXMLFileFileContainer();
				File mainXMLFileToImport = null;
				if ( StringUtils.isNotEmpty( proxlXMLFileDBRecord.getFilenameOnDiskWithPathSubSameMachine() ) ) {
					//  Populate Path on Search when submit on same machine
					skipPopulatingPathOnSearchLineOptChosen = false;
					String getProxlXMLFilenameOnDiskToImport = proxlXMLFileDBRecord.getFilenameOnDiskWithPathSubSameMachine();
					mainXMLFileToImport = new File( getProxlXMLFilenameOnDiskToImport );
					mainXMLFileToImportContainer.setMainXMLFileToImport( mainXMLFileToImport );
					importResults.setImportedProxlXMLFile( mainXMLFileToImport );
					if( ! mainXMLFileToImport.exists() ) {
						//  The User provided the path to this file 
						//  when the import was submitted, so this is considered a data error
						String msg = "Could not find Proxl XML File To Import: " + mainXMLFileToImport.getCanonicalPath();
						System.err.println( msg );
						throw new ProxlImporterDataException(msg);
						
//						importResults.setImportSuccessStatus( false ) ;
//						
//						//  TODO	Consider different exit code since the import tracking tables 
//						//  		are out of sync with the filesystem.
//						
//						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
//
//						return importResults;  //  EARLY EXIT
					}
				} else {
					String getProxlXMLFilenameOnDiskToImport = proxlXMLFileDBRecord.getFilenameOnDisk();
					mainXMLFileToImport = new File( getProxlXMLFilenameOnDiskToImport );
					mainXMLFileToImportContainer.setMainXMLFileToImport( mainXMLFileToImport );
					importResults.setImportedProxlXMLFile( mainXMLFileToImport );
					if( ! mainXMLFileToImport.exists() ) {
						System.err.println( "Could not find mainXMLFileToImport File: " + mainXMLFileToImport.getCanonicalPath() );
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						importResults.setImportSuccessStatus( false ) ;
						//  TODO	Consider different exit code since the import tracking tables 
						//  		are out of sync with the filesystem.
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
						return importResults;  //  EARLY EXIT
					}
				}				
				long mainXMLFileToImportFileSize = mainXMLFileToImport.length();
				String SHA1Sum = SHA1SumCalculator.getInstance().getSHA1Sum( mainXMLFileToImport );
				ProxlXMLFileImportTrackingSingleFile_Importer_DAO.getInstance()
				.updateFileSizeSHA1Sum(mainXMLFileToImportFileSize, SHA1Sum, proxlXMLFileDBRecord.getId() );
				scanFileFileContainerList = new ArrayList<>();
				if ( scanFilesDBRecords.isEmpty() ) {
					noScanFilesCommandLineOptChosen = true; 
				} else {
					for ( ProxlXMLFileImportTrackingSingleFileDTO scanFileDBRecord : scanFilesDBRecords ) {
						if ( StringUtils.isNotEmpty( scanFileDBRecord.getFilenameOnDiskWithPathSubSameMachine() ) ) {
							String scanFileString = scanFileDBRecord.getFilenameOnDiskWithPathSubSameMachine();
							File scanFile = new File( scanFileString );
							String scanFilename = scanFile.getName();
							String errorStringScanSuffixValidation = validateScanFileSuffix( scanFilename );
							if ( errorStringScanSuffixValidation != null ) {
								System.err.println( errorStringScanSuffixValidation );
								throw new ProxlImporterDataException( errorStringScanSuffixValidation );
							}
							if( ! scanFile.exists() ) {
								//  The User provided the path to this file 
								//  when the import was submitted, so this is considered a data error
								String msg = "Could not find Scan File To Import: " + scanFile.getCanonicalPath();
								System.err.println( msg );
								throw new ProxlImporterDataException(msg);
								
//								importResults.setImportSuccessStatus( false ) ;
//								
//								importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
//
//								return importResults;  //  EARLY EXIT
							}
							ScanFileFileContainer scanFileFileContainer = new ScanFileFileContainer();
							scanFileFileContainer.setScanFile( scanFile );
							scanFileFileContainer.setScanFileDBRecord( scanFileDBRecord );
							scanFileFileContainerList.add( scanFileFileContainer );
						} else {
							String inputScanFileString = scanFileDBRecord.getFilenameOnDisk();
							String errorStringScanSuffixValidation = validateScanFileSuffix( inputScanFileString );
							if ( errorStringScanSuffixValidation != null ) {
								System.err.println( errorStringScanSuffixValidation );
								System.err.println( "" );
								System.err.println( FOR_HELP_STRING );
								importResults.setImportSuccessStatus( false) ;
								importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
								return importResults;  //  EARLY EXIT
							}
							File scanFile = new File( inputScanFileString );
							if( ! scanFile.exists() ) {
								System.err.println( "Could not find scan file: " + scanFile.getAbsolutePath() );
								System.err.println( "" );
								System.err.println( FOR_HELP_STRING );
								importResults.setImportSuccessStatus( false) ;
								importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
								//  TODO	Consider different exit code since the import tracking tables 
								//  		are out of sync with the filesystem.
								return importResults;  //  EARLY EXIT
							}
							ScanFileFileContainer scanFileFileContainer = new ScanFileFileContainer();
							scanFileFileContainer.setScanFile( scanFile );
							scanFileFileContainer.setScanFileDBRecord( scanFileDBRecord );
							scanFileFileContainerList.add( scanFileFileContainer );
						}
					}					
					scanFileList = new ArrayList<>( scanFileFileContainerList.size() );
					for ( ScanFileFileContainer scanFileFileContainer : scanFileFileContainerList ) {
						scanFileList.add( scanFileFileContainer.getScanFile() );
					}
					importResults.setScanFileList( scanFileList );
				}
			} else {
				///   NO parameters file so process the command line parameters
				////////////////////////
				//   Get values from command line params
				projectId = (Integer)cmdLineParser.getOptionValue( projectIdOpt );
				noScanFilesCommandLineOptChosen = (Boolean) cmdLineParser.getOptionValue( noScanFilesCommandLineOpt, Boolean.FALSE);
				String inputProxlFileString = (String)cmdLineParser.getOptionValue( inputProxlFileStringCommandLineOpt );
				@SuppressWarnings("rawtypes")
				Vector inputScanFileStringVector = cmdLineParser.getOptionValues( inputScanFileStringCommandLineOpt );
				@SuppressWarnings("rawtypes")
				Vector  dropPeptideCutoffValueVector = cmdLineParser.getOptionValues( dropPeptideCutoffValueOpt );
				@SuppressWarnings("rawtypes")
				Vector  dropPsmCutoffValueVector = cmdLineParser.getOptionValues( dropPsmCutoffValueOpt );
				if( ( noScanFilesCommandLineOptChosen == null || ( ! noScanFilesCommandLineOptChosen ) )
						&& ( inputScanFileStringVector == null || ( inputScanFileStringVector.isEmpty() ) ) ) {
					System.err.println( "At least one scan file is required since 'no scan files' param not specified.\n" );
					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					importResults.setImportSuccessStatus( false) ;
					importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
					return importResults;  //  EARLY EXIT
				}
				if( projectId == null || projectId == 0 ) {
					System.err.println( "Must specify a project id using -p\n" );
					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					importResults.setImportSuccessStatus( false) ;
					importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
					return importResults;  //  EARLY EXIT
				}
				File mainXMLFileToImport = new File( inputProxlFileString );
				mainXMLFileToImportContainer = new ProxlXMLFileFileContainer();
				mainXMLFileToImportContainer.setMainXMLFileToImport( mainXMLFileToImport );
				importResults.setImportedProxlXMLFile( mainXMLFileToImport );
				if( ! mainXMLFileToImport.exists() ) {
					System.err.println( "Could not find main XML File To Import file: " + mainXMLFileToImport.getAbsolutePath() );
					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					importResults.setImportSuccessStatus( false) ;
					importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
					return importResults;  //  EARLY EXIT
				}
				scanFileFileContainerList = new ArrayList<>(  );
				if ( inputScanFileStringVector != null && ( ! inputScanFileStringVector.isEmpty() ) ) {
					for ( Object inputScanFileStringObject : inputScanFileStringVector ) {
						if ( ! (  inputScanFileStringObject instanceof String ) ) {
							System.err.println( "Internal ERROR:  inputScanFileStringObject is not a String object." );
							System.err.println( "" );
							System.err.println( FOR_HELP_STRING );
							importResults.setImportSuccessStatus( false) ;
							importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
							return importResults;  //  EARLY EXIT
						}
						String inputScanFileString = (String) inputScanFileStringObject;
						if( inputScanFileString == null || inputScanFileString.equals( "" ) ) {
							System.err.println( "Internal ERROR:  inputScanFileStringObject is empty or null." );
							System.err.println( "" );
							System.err.println( FOR_HELP_STRING );
							importResults.setImportSuccessStatus( false) ;
							importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
							return importResults;  //  EARLY EXIT
						}
						String errorStringScanSuffixValidation = validateScanFileSuffix( inputScanFileString );
						if ( errorStringScanSuffixValidation != null ) {
							System.err.println( errorStringScanSuffixValidation );
							System.err.println( "" );
							System.err.println( FOR_HELP_STRING );
							importResults.setImportSuccessStatus( false) ;
							importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
							return importResults;  //  EARLY EXIT
						}
						File scanFile = new File( inputScanFileString );
						if( ! scanFile.exists() ) {
							System.err.println( "Could not find scan file: " + scanFile.getAbsolutePath() );
							System.err.println( "" );
							System.err.println( FOR_HELP_STRING );
							importResults.setImportSuccessStatus( false) ;
							importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
							return importResults;  //  EARLY EXIT
						}
						ScanFileFileContainer scanFileFileContainer = new ScanFileFileContainer();
						scanFileFileContainer.setScanFile( scanFile );
						scanFileFileContainerList.add( scanFileFileContainer );
					}
				}
				scanFileList = new ArrayList<>( scanFileFileContainerList.size() );
				for ( ScanFileFileContainer scanFileFileContainer : scanFileFileContainerList ) {
					scanFileList.add( scanFileFileContainer.getScanFile() );
				}
				importResults.setScanFileList( scanFileList );
				if ( log.isInfoEnabled() ) {
					System.out.println( "Now: " + new Date() );
					System.out.println( "" );
				}
				System.out.println( "Performing Proxl import for parameters:" );
				System.out.println( "project id: " + projectId );
				if ( createDatabaseConnectionFactory && StringUtils.isNotEmpty( proxlDatabaseName ) ) {
					System.out.println( "'--" + PROXL_DB_NAME_CMD_LINE_PARAM_STRING 
							+ "=' specified so importing to database name: " + proxlDatabaseName );
				}
				/////////////////////////
				//   Peptide and PSM cutoffs 
				final String PEPTIDE_PSM_CUTOFF_SEPARATOR = ":";
				List<DropPeptidePSMCutoffValue> dropPeptideCutoffValueList = new ArrayList<>();
				dropPeptidePSMCutoffValues.setDropPeptideCutoffValuesCommandLineList( dropPeptideCutoffValueList );
				for ( Object dropPeptideCutoffValueStringObject : dropPeptideCutoffValueVector ) {
					if ( ! (  dropPeptideCutoffValueStringObject instanceof String ) ) {
						System.err.println( "dropPeptideCutoffValueStringObject is not a String object\n" );
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						importResults.setImportSuccessStatus( false) ;
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
						return importResults;  //  EARLY EXIT
					}
					String dropPeptideCutoffValueString = (String) dropPeptideCutoffValueStringObject;
					if( dropPeptideCutoffValueString == null || dropPeptideCutoffValueString.equals( "" ) ) {
						System.err.println( "'--drop-peptide-cutoff' must have a value" );
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						importResults.setImportSuccessStatus( false) ;
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
						return importResults;  //  EARLY EXIT
					}
					String[] dropPeptideCutoffValueStringSplit = dropPeptideCutoffValueString.split( PEPTIDE_PSM_CUTOFF_SEPARATOR );
					if ( dropPeptideCutoffValueStringSplit.length != 2 ) {
						System.err.println( "'--drop-peptide-cutoff' must have 2 values (annotation name" + PEPTIDE_PSM_CUTOFF_SEPARATOR + "cutoff value)" 
								+ " separated by '" + PEPTIDE_PSM_CUTOFF_SEPARATOR + "'" );	
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						importResults.setImportSuccessStatus( false) ;
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
						return importResults;  //  EARLY EXIT
					}
					String annotationName = dropPeptideCutoffValueStringSplit[ 0 ];
					String cutoffValueString = dropPeptideCutoffValueStringSplit[ 1 ];
					DropPeptidePSMCutoffValue dropPeptidePSMCutoffValue = new DropPeptidePSMCutoffValue();
					dropPeptidePSMCutoffValue.setAnnotationName( annotationName );
					try {
						BigDecimal cutoffValue = new BigDecimal( cutoffValueString );
						dropPeptidePSMCutoffValue.setCutoffValue( cutoffValue );
					} catch ( Exception e ) {
						System.err.println( "The second value (cutoff) for '--drop-peptide-cutoff' must be a valid decimal number."
								+ " Invalid cutoff value: " + cutoffValueString ); 
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						importResults.setImportSuccessStatus( false) ;
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
						return importResults;  //  EARLY EXIT
					}
					dropPeptideCutoffValueList.add( dropPeptidePSMCutoffValue );
				}
				List<DropPeptidePSMCutoffValue> dropPsmCutoffValueList = new ArrayList<>();
				dropPeptidePSMCutoffValues.setDropPSMCutoffValuesCommandLineList( dropPsmCutoffValueList );
				for ( Object dropPsmCutoffValueStringObject : dropPsmCutoffValueVector ) {
					if ( ! (  dropPsmCutoffValueStringObject instanceof String ) ) {
						System.err.println( "dropPsmCutoffValueStringObject is not a String object\n" );
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						importResults.setImportSuccessStatus( false) ;
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
						return importResults;  //  EARLY EXIT
					}
					String dropPsmCutoffValueString = (String) dropPsmCutoffValueStringObject;
					if( dropPsmCutoffValueString == null || dropPsmCutoffValueString.equals( "" ) ) {
						System.err.println( "'--drop-peptide-cutoff' must have a value" );
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						importResults.setImportSuccessStatus( false) ;
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
						return importResults;  //  EARLY EXIT
					}
					String[] dropPsmCutoffValueStringSplit = dropPsmCutoffValueString.split( PEPTIDE_PSM_CUTOFF_SEPARATOR );
					if ( dropPsmCutoffValueStringSplit.length != 2 ) {
						System.err.println( "'--drop-peptide-cutoff' must have 2 values (annotation name" + PEPTIDE_PSM_CUTOFF_SEPARATOR + "cutoff value)" 
								+ " separated by '" + PEPTIDE_PSM_CUTOFF_SEPARATOR + "'" );	
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						importResults.setImportSuccessStatus( false) ;
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
						return importResults;  //  EARLY EXIT
					}
					String annotationName = dropPsmCutoffValueStringSplit[ 0 ];
					String cutoffValueString = dropPsmCutoffValueStringSplit[ 1 ];
					DropPeptidePSMCutoffValue dropPeptidePSMCutoffValue = new DropPeptidePSMCutoffValue();
					dropPeptidePSMCutoffValue.setAnnotationName( annotationName );
					try {
						BigDecimal cutoffValue = new BigDecimal( cutoffValueString );
						dropPeptidePSMCutoffValue.setCutoffValue( cutoffValue );
					} catch ( Exception e ) {
						System.err.println( "The second value (cutoff) for '--drop-peptide-cutoff' must be a valid decimal number."
								+ " Invalid cutoff value: " + cutoffValueString ); 
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						importResults.setImportSuccessStatus( false) ;
						importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_INVALID_COMMAND_LINE_PARAMETER_VALUES );
						return importResults;  //  EARLY EXIT
					}
					dropPsmCutoffValueList.add( dropPeptidePSMCutoffValue );
				}
				//  END of process command line parameters
			}
			/////////////////////////
			System.out.println( "main XML File To Import file: " 
					+ mainXMLFileToImportContainer.getMainXMLFileToImport().getCanonicalPath() );
			if ( scanFileList == null || scanFileList.isEmpty() ) {
				System.out.println( " " );
				System.out.println( "No Scan files" );
				System.out.println( " " );
				System.out.println( " " );
			} else {
				System.out.println( " " );
				System.out.println( "Scan files full path:" );
				for ( File scanFile : scanFileList ) {
					System.out.println( scanFile.getAbsolutePath() );
				}
				System.out.println( " " );
				System.out.println( "Scan files following all soft links, full path:" );
				for ( File scanFile : scanFileList ) {
					System.out.println( scanFile.getCanonicalPath() );
				}
				System.out.println( " " );
				System.out.println( " " );
			}
			
			//////////////////////////////////////
			//////////   Do the import
			importerCoreEntryPoint = ImporterCoreEntryPoint.getInstance();
			int insertedSearchId = 
					importerCoreEntryPoint.doImport( 
							projectId, 
							userIdInsertingSearch,
							searchNameOverrideValue,
							importDirectoryOverrideValue,
							mainXMLFileToImportContainer.getMainXMLFileToImport(), 
							proxlInputForImportParam,
							scanFileFileContainerList,
							dropPeptidePSMCutoffValues,
							skipPopulatingPathOnSearchLineOptChosen,
							doNotUseCutoffInInputFile 
							);
			
			importResults.setSearchId( insertedSearchId );
			
			System.out.println( "" );
			System.out.println( "--------------------------------------" );
			System.out.println( "" );
			System.out.println( "Now: " + new Date() );
			System.out.println( "" );
			System.out.println( "Completed Proxl import for parameters:" );
			System.out.println( "project id: " + projectId );
			if ( createDatabaseConnectionFactory && StringUtils.isNotEmpty( proxlDatabaseName ) ) {
				System.out.println( "'--" + PROXL_DB_NAME_CMD_LINE_PARAM_STRING 
						+ "=' specified so imported to database name: " + proxlDatabaseName );
			}
			System.out.println( "main XML File To Import file: " 
					+ mainXMLFileToImportContainer.getMainXMLFileToImport().getAbsolutePath() );
			if ( scanFileList == null || scanFileList.isEmpty() ) {
				System.out.println( " " );
				System.out.println( "No Scan files" );
				System.out.println( " " );
				System.out.println( " " );
			} else {
				System.out.println( " " );
				System.out.println( "Scan files full path:" );
				for ( File scanFile : scanFileList ) {
					System.out.println( scanFile.getAbsolutePath() );
				}
				System.out.println( " " );
				System.out.println( "Scan files following all soft links, full path:" );
				for ( File scanFile : scanFileList ) {
					System.out.println( scanFile.getCanonicalPath() );
				}
				System.out.println( " " );
				System.out.println( " " );
			}
			System.out.println( " " );
			System.out.println( "--------------------------------------" );
			System.out.println( " " );
			if ( StringUtils.isNotEmpty( filenameWithSearchIdToCreateOnSuccessInsert ) ) {
				try {
					String filenameWithSearchIdToCreateOnSuccessInsertActual = filenameWithSearchIdToCreateOnSuccessInsert + insertedSearchId;
					FileWriter writer = null;
					try {
						writer = new FileWriter( filenameWithSearchIdToCreateOnSuccessInsertActual );
						writer.write( "Inserted search id: " + insertedSearchId );
					} finally {
						if ( writer != null ) {
							writer.close();
						}
					}
				} catch ( Throwable t ) {
					//  just eat it
				}
			}
			successfulImport = true;
			importProgramShutdownThread.setNormalProgramCompletionReached( true );
			//  Update records for import submitted by web app
			if ( proxlXMLFileImportTrackingDTO != null && proxlXMLFileImportTrackingRunDTO != null ) {
				proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.COMPLETE );
				proxlXMLFileImportTrackingRunDTO.setInsertedSearchId( insertedSearchId );
				UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
				.updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
						ProxlXMLFileImportStatus.COMPLETE, 
						proxlXMLFileImportTrackingDTO.getId(), 
						proxlXMLFileImportTrackingRunDTO );
				ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO.getInstance()
				.updateInsertedSearchId( proxlXMLFileImportTrackingRunDTO );
			}
			
		} catch ( ProxlImporterProxlXMLDeserializeFailException e ) {
			String exceptionMessage = e.getMessage();
			String errorMessage = "The Proxl XML File has XML structure problems and failed to parse."
					+ "  Please update the program that generated the Proxl XML File"
					+ " or ensure that the correct version is being used."
					+ "  Parse error message: " 
					+ exceptionMessage;
			if ( outputDataErrorsFileName != null ) {
				writeDataErrorToFile( errorMessage, e, outputDataErrorsFileName );
			}
			importResults.setImportSuccessStatus( false) ;
			importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_DATA_ERROR );
			//  Update records for import submitted by web app
			if ( proxlXMLFileImportTrackingDTO != null && proxlXMLFileImportTrackingRunDTO != null ) {
				proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.FAILED );
				proxlXMLFileImportTrackingRunDTO.setRunSubStatus( ProxlXMLFileImportRunSubStatus.DATA_ERROR );
				proxlXMLFileImportTrackingRunDTO.setDataErrorText( errorMessage );
				// TODO   Maybe populate this with something else
				proxlXMLFileImportTrackingRunDTO.setImportResultText( errorMessage );
				UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
				.updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
						ProxlXMLFileImportStatus.FAILED, 
						proxlXMLFileImportTrackingDTO.getId(), 
						proxlXMLFileImportTrackingRunDTO );
			}
			return importResults;  //  EARLY EXIT
		} catch ( ProxlImporterProjectNotAllowImportException e ) {
			if ( outputDataErrorsFileName != null ) {
				writeDataErrorToFile( 
						"The upload can no longer be inserted into this project." + e.getMessage(), 
						e, outputDataErrorsFileName );
			}
			importResults.setImportSuccessStatus( false) ;
			importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_PROJECT_NOT_ALLOW_IMPORT );
			//  Update records for import submitted by web app
			if ( proxlXMLFileImportTrackingDTO != null && proxlXMLFileImportTrackingRunDTO != null ) {
				String dataErrorText = "The upload can no longer be inserted into this project.";
				proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.FAILED );
				proxlXMLFileImportTrackingRunDTO.setRunSubStatus( ProxlXMLFileImportRunSubStatus.PROJECT_NOT_ALLOW_IMPORT );
				proxlXMLFileImportTrackingRunDTO.setDataErrorText( dataErrorText );
				// TODO   Maybe populate this with something else
				proxlXMLFileImportTrackingRunDTO.setImportResultText( dataErrorText );
				UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
				.updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
						ProxlXMLFileImportStatus.FAILED, 
						proxlXMLFileImportTrackingDTO.getId(), 
						proxlXMLFileImportTrackingRunDTO );
			}
			return importResults;  //  EARLY EXIT
		} catch ( ProxlImporterDataException e) {
			processImporterDataException(
					importResults,
					proxlXMLFileImportTrackingDTO,
					proxlXMLFileImportTrackingRunDTO, 
					outputDataErrorsFileName,
					e );
			return importResults;
		} catch ( ProxlBaseDataException e) {
			processBaseDataException(
					importResults,
					proxlXMLFileImportTrackingDTO,
					proxlXMLFileImportTrackingRunDTO, 
					outputDataErrorsFileName,
					e );
			return importResults;
		} catch ( Exception e ) {
			if ( ! shutdownReceivedViaShutdownHook ) {
				System.out.println( "Exception in processing" );
				System.err.println( "Exception in processing" );
				e.printStackTrace( System.out );
				e.printStackTrace( System.err );
			}
			throw e;
			
		} finally {
			if ( importProgramShutdownThread != null ) {
				importProgramShutdownThread.setNormalProgramCompletionReached( true );
			}
			if ( log.isDebugEnabled() ) {
				log.debug( "Main Thread:  Calling DBConnectionFactory.closeAllConnections(); on main thread.");
			}
			try {
				// free up our db resources
				DBConnectionFactory.closeAllConnections();
				if ( log.isDebugEnabled() ) {
					log.debug( "Main Thread:  Call to DBConnectionFactory.closeAllConnections(); on main thread Completed.");
				}
			} catch ( Exception e ) {
				System.out.println( "----------------------------------------");
				System.out.println( "----");
				System.err.println( "----------------------------------------");
				System.err.println( "----");
				System.out.println( "Main Thread:  Exception in closing database connections" );
				System.err.println( "Main Thread:  Exception in closing database connections" );
				e.printStackTrace( System.out );
				e.printStackTrace( System.err );
				System.out.println( "----");
				System.out.println( "----------------------------------------");
				System.err.println( "----");
				System.err.println( "----------------------------------------");
				throw e;
			}
		}
		if ( successfulImport ) {
			System.out.println( "" );
			System.out.println( "--------------------------------------" );
			System.out.println( "" );
			System.out.println( "Done Importing data.  Import Successful." );
			System.out.println( "" );
			System.out.println( "--------------------------------------" );
			System.out.println( "" );
		}
		return importResults;
	}
	
	/**
	 * @param importResults
	 * @param proxlXMLFileImportTrackingDTO
	 * @param proxlXMLFileImportTrackingRunDTO
	 * @param outputDataErrorsFileName
	 * @param exception
	 * @throws IOException
	 * @throws Exception
	 */
	private void processImporterDataException(
			ImportResults importResults,
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO,
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO,
			String outputDataErrorsFileName, 
			ProxlImporterDataException exception )
			throws IOException, Exception {
		
		processDataException_call_Importer_or_BaseExceptionProcessing( importResults, proxlXMLFileImportTrackingDTO, proxlXMLFileImportTrackingRunDTO, outputDataErrorsFileName, exception );
	}
	
	/**
	 * @param importResults
	 * @param proxlXMLFileImportTrackingDTO
	 * @param proxlXMLFileImportTrackingRunDTO
	 * @param outputDataErrorsFileName
	 * @param exception
	 * @throws IOException
	 * @throws Exception
	 */
	private void processBaseDataException(
			ImportResults importResults,
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO,
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO,
			String outputDataErrorsFileName, 
			ProxlBaseDataException exception )
			throws IOException, Exception {
		
		processDataException_call_Importer_or_BaseExceptionProcessing( importResults, proxlXMLFileImportTrackingDTO, proxlXMLFileImportTrackingRunDTO, outputDataErrorsFileName, exception );
	}
	
	//  Only call this method by the prev 2 methods
	/**
	 * @param importResults
	 * @param proxlXMLFileImportTrackingDTO
	 * @param proxlXMLFileImportTrackingRunDTO
	 * @param outputDataErrorsFileName
	 * @param exception
	 * @throws IOException
	 * @throws Exception
	 */
	private void processDataException_call_Importer_or_BaseExceptionProcessing(
			ImportResults importResults,
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO,
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO,
			String outputDataErrorsFileName, 
			Exception exception )
			throws IOException, Exception {
		
		if ( ( ! ( exception instanceof ProxlImporterDataException ) )
				&& ( ! ( exception instanceof ProxlImporterDataException ) ) ) {
			String msg = "exception not ProxlImporterDataException or ProxlImporterDataException";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		if ( outputDataErrorsFileName != null ) {
			writeDataErrorToFile( exception.getMessage(), exception, outputDataErrorsFileName );
		}
		importResults.setImportSuccessStatus( false) ;
		importResults.setProgramExitCode( ImporterProgramExitCodes.PROGRAM_EXIT_CODE_DATA_ERROR );
		//  Update records for import submitted by web app
		if ( proxlXMLFileImportTrackingDTO != null && proxlXMLFileImportTrackingRunDTO != null ) {
			proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.FAILED );
			proxlXMLFileImportTrackingRunDTO.setRunSubStatus( ProxlXMLFileImportRunSubStatus.DATA_ERROR );
			proxlXMLFileImportTrackingRunDTO.setDataErrorText( exception.getMessage() );
			// TODO   Maybe populate this with something else
			proxlXMLFileImportTrackingRunDTO.setImportResultText( exception.getMessage() );
			UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
			.updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
					ProxlXMLFileImportStatus.FAILED, 
					proxlXMLFileImportTrackingDTO.getId(), 
					proxlXMLFileImportTrackingRunDTO );
		}
	}
	
	/**
	 * validateScanFileSuffix
	 * 
	 * @param inputScanFileString
	 * @return null if no error, otherwise return the error message
	 */
	private String validateScanFileSuffix( String inputScanFileString ) {
		String errorString = null;
		if ( ! ( inputScanFileString.endsWith( ScanFilenameConstants.MZ_ML_SUFFIX ) 
				|| inputScanFileString.endsWith( ScanFilenameConstants.MZ_XML_SUFFIX ) ) ) {
			errorString =  "Scan file name must end with '"
					+ ScanFilenameConstants.MZ_ML_SUFFIX 
					+ "' or '"
					+ ScanFilenameConstants.MZ_XML_SUFFIX
					+ "' and have the correct contents to match the filename suffix.";
		}
		return errorString;
	}
	
	/**
	 * @throws Exception
	 */
	private static void printHelp() throws Exception {
		try( BufferedReader br = 
				new BufferedReader(
						new InputStreamReader( 
								ImporterDefaultMainProgramEntry.class
								.getResourceAsStream( HELP_FILE_WITH_PATH ) ) ) ) {
			String line = null;
			while ( ( line = br.readLine() ) != null )
				System.out.println( line );				
		} catch ( Exception e ) {
			System.out.println( "Error printing help." );
		}
	}
	
	/**
	 * @param Exception
	 */
	public static void writeDataErrorToFile( String errorMessage, Exception exception, String outputDataErrorsFileName ) throws IOException {
		//  exception is maybe:
		// ProxlBaseDataException 
		// ProxlImporterDataException 
		// ProxlImporterProjectNotAllowImportException
		// ProxlImporterProxlXMLDeserializeFailException
		String dataErrorString = errorMessage;
		File outputDataErrorsFile = new File( outputDataErrorsFileName );
		BufferedWriter bwriter = null;
		try {
			bwriter = new BufferedWriter( new OutputStreamWriter( 
					new FileOutputStream(outputDataErrorsFile), DataErrorsFileConstants.FILE_CHARACTER_ENCODING ) );
			bwriter.write( DataErrorsFileConstants.FILE_TEXT_ABOVE_ERROR );
			bwriter.newLine();
			bwriter.newLine();
			bwriter.write( dataErrorString );
			bwriter.newLine();
		} catch( Exception e ) {
			String msg = "Failed to write data error to file: " + outputDataErrorsFile.getCanonicalPath();
			log.error( msg, e );
			throw e;
		} finally {
			if ( bwriter != null ) {
				bwriter.close();
			}
		}
	}
	
	/**
	 *
	 *  Class for processing kill signal. This is also run when all the threads in the application die/exit run()
	 */
	public static class ImportProgramShutdownThread extends Thread {
		
		private static final Logger logImportProgramShutdownThread = Logger.getLogger(ImportProgramShutdownThread.class);
		private final static int WAIT_TIME_FOR_MAIN_THREAD_TO_EXIT_IF_NORMAL_COMPLETION_REACHED = 2 * 1000; // 2 seconds
		private volatile Thread mainThread;
		private volatile boolean normalProgramCompletionReached = false;
		
		/*
		 * method that will run when kill signal is received
		 */
		public void run() {
			Thread thisThread = Thread.currentThread();
			thisThread.setName( "Thread-Process-Shutdown-Request" );
			if ( normalProgramCompletionReached ) {
				//  Main thread is about to exit normally so wait for it to exit normally
				try {
					mainThread.join( WAIT_TIME_FOR_MAIN_THREAD_TO_EXIT_IF_NORMAL_COMPLETION_REACHED );
				} catch (InterruptedException e) {
					String msg = "Wait for main thread to exit was interrupted.  allowing process to exit";
					log.error( msg );
				}
				return;  //  EARLY EXIT
			}
			if ( logImportProgramShutdownThread.isDebugEnabled() ) {
				logImportProgramShutdownThread.debug( "ImportProgramShutdownThread::run() called now(): " + new Date());
			}
			ImporterDefaultMainProgramEntry.getInstance().shutdownReceivedViaShutdownHook = true;
			try {
				ImporterDefaultMainProgramEntry.getInstance().importerCoreEntryPoint.setShutdownRequested(true);
			} catch ( Exception e ) {
				//  Eat this exception since may be null pointer exception or other exception
			}
			if ( logImportProgramShutdownThread.isDebugEnabled() ) {
				logImportProgramShutdownThread.debug( "Calling DBConnectionFactory.closeAllConnections(); on shutdown thread to ensure connections closed.");
			}
			{
				String msg = "Program termination has been requested.  Closing database connections.";
				System.out.println( msg );
				System.err.println( msg );
			}
			//  Ensure database connections get closed before program dies.
			try {
				// free up our db resources
				DBConnectionFactory.closeAllConnections();
				if ( logImportProgramShutdownThread.isDebugEnabled() ) {
					logImportProgramShutdownThread.debug( "COMPLETE:  Calling DBConnectionFactory.closeAllConnections(); on shutdown thread to ensure connections closed.");
				}
			} catch ( Exception e ) {
				System.out.println( "----------------------------------------");
				System.out.println( "----");
				System.err.println( "----------------------------------------");
				System.err.println( "----");
				System.out.println( "Shutdown Thread: Exception in closing database connections  on shutdown thread" );
				System.err.println( "Shutdown Thread: Exception in closing database connections  on shutdown thread" );
				e.printStackTrace( System.out );
				e.printStackTrace( System.err );
				System.out.println( "----");
				System.out.println( "----------------------------------------");
				System.err.println( "----");
				System.err.println( "----------------------------------------");
			}
			{
				String msg = "Program termination has been requested.  Proxl XML file has not been fully imported.  Database connections have been closed."
						+ "  Program exiting";
				System.out.println( msg );
				System.err.println( msg );
			}
			if ( logImportProgramShutdownThread.isDebugEnabled() ) {
				logImportProgramShutdownThread.debug( "ImportProgramShutdownThread::run() exiting now(): " + new Date() );
			}
		}
		
		public Thread getMainThread() {
			return mainThread;
		}
		public void setMainThread(Thread mainThread) {
			this.mainThread = mainThread;
		}
		public boolean isNormalProgramCompletionReached() {
			return normalProgramCompletionReached;
		}
		public void setNormalProgramCompletionReached(
				boolean normalProgramCompletionReached) {
			this.normalProgramCompletionReached = normalProgramCompletionReached;
		}
	}
	
	public ImporterCoreEntryPoint getImporterCoreEntryPoint() {
		return importerCoreEntryPoint;
	}
}
