package org.yeastrc.proxl.import_xml_to_db_runner_pgm.program;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderFromPropertiesFile;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderPropertiesFileErrorException;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.config.ProcessImporterRunnerConfigFile;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.dao.ConfigSystemDAO;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.main.ImporterRunnerMain;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemTableGetValueCommon;

/**
 * 
 *
 */
public class ImporterRunnerProgram {

	private static final Logger log = Logger.getLogger(ImporterRunnerProgram.class);
//	private static final int PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED = 0;
	private static final int PROGRAM_EXIT_CODE_INVALID_INPUT = 1;
	private static final int PROGRAM_EXIT_CODE_HELP = 1;
	private static final String FOR_HELP_STRING = "For help, run without any parameters, -h, or --help";
	private static final String PROXL_DB_NAME_CMD_LINE_PARAM_STRING = "proxl_db_name";
	private static final String MAX_TRACKING_RECORD_PRIORITY_TO_RETRIEVE_CMD_LINE_PARAM_STRING = "max_tracking_record_priority_to_retrieve";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		int maxTrackingRecordPriorityToRetrieve = Integer.MAX_VALUE;
		ImportRunnerProgramShutdown importRunnerProgramShutdown = null;
		try {
//			if ( args.length == 0 ) {
//				printHelp();
//				System.exit( PROGRAM_EXIT_CODE_HELP );
//			}
			CmdLineParser cmdLineParser = new CmdLineParser();
			CmdLineParser.Option configFileFromCommandLineFileNameCommandLineOpt = cmdLineParser.addStringOption( 'c', "config" );
			CmdLineParser.Option proxlDatabaseNameCommandLineOpt = cmdLineParser.addStringOption( 'Z', PROXL_DB_NAME_CMD_LINE_PARAM_STRING );
			CmdLineParser.Option maxTrackingRecordPriorityToRetrieveCommandLineOpt = cmdLineParser.addStringOption( 'Z', MAX_TRACKING_RECORD_PRIORITY_TO_RETRIEVE_CMD_LINE_PARAM_STRING );
			CmdLineParser.Option helpOpt = cmdLineParser.addBooleanOption('h', "help"); 
			// parse command line options
			try { cmdLineParser.parse(args); }
			catch (IllegalOptionValueException e) {
				System.err.println(e.getMessage());
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			catch (UnknownOptionException e) {
				System.err.println(e.getMessage());
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			Boolean help = (Boolean) cmdLineParser.getOptionValue(helpOpt, Boolean.FALSE);
			if(help) {
				printHelp();
				System.exit( PROGRAM_EXIT_CODE_HELP );
			}
			///////   Initialize Database Access
			String configFileFromCommandLineFileName = (String)cmdLineParser.getOptionValue( configFileFromCommandLineFileNameCommandLineOpt );
			String proxlDatabaseName = (String)cmdLineParser.getOptionValue( proxlDatabaseNameCommandLineOpt );
			String maxTrackingRecordPriorityToRetrieveString = (String)cmdLineParser.getOptionValue( maxTrackingRecordPriorityToRetrieveCommandLineOpt );
			if ( StringUtils.isNotEmpty( maxTrackingRecordPriorityToRetrieveString ) ) {
				try {
					int maxTrackingRecordPriorityToRetrieveCmdLineInt = Integer.parseInt( maxTrackingRecordPriorityToRetrieveString );
					maxTrackingRecordPriorityToRetrieve = maxTrackingRecordPriorityToRetrieveCmdLineInt;
				} catch ( Exception e ) {
					System.err.println( "--" + MAX_TRACKING_RECORD_PRIORITY_TO_RETRIEVE_CMD_LINE_PARAM_STRING
							+ "  option must be an integer.  Value passed: " + maxTrackingRecordPriorityToRetrieveString );
					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
				}
				System.out.println( "processed command line parameter: --" + MAX_TRACKING_RECORD_PRIORITY_TO_RETRIEVE_CMD_LINE_PARAM_STRING 
						+ "=" + maxTrackingRecordPriorityToRetrieve );
			}
			if ( StringUtils.isEmpty( configFileFromCommandLineFileName ) ) {
				System.err.println( "Config file is required." );
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			File configFileFromCommandLine = null;
			if ( StringUtils.isNotEmpty( configFileFromCommandLineFileName ) ) {
				configFileFromCommandLine = new File( configFileFromCommandLineFileName );
				if( ! configFileFromCommandLine.exists() ) {
					System.err.println( "Could not find Config File: " + configFileFromCommandLine.getAbsolutePath() );
					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
				}
			}			
			
			DBConnectionParametersProviderFromPropertiesFile dbConnectionParametersProvider = null;
			try {
				if ( log.isDebugEnabled() ) {
					log.debug( "processing config file from command line: " + configFileFromCommandLine.getAbsolutePath() );
				}
				dbConnectionParametersProvider =
						ProcessImporterRunnerConfigFile.getInstance().processConfigFile( configFileFromCommandLine );
			} catch ( DBConnectionParametersProviderPropertiesFileErrorException e ) {
				System.exit( 1 );
			} catch ( Exception e ) {
				System.err.println( "Failed processing DB config file." );
				if ( log.isDebugEnabled() ) {
					e.printStackTrace();
				}
				System.exit( 1 );
			}
			
			if ( dbConnectionParametersProvider != null ) {
				if ( StringUtils.isNotEmpty( proxlDatabaseName ) ) {
					dbConnectionParametersProvider.setProxlDbName( proxlDatabaseName );
				}
			} else {
				System.err.println( "Unable to set proxlDatabaseName since no config file present");
			}
			
			ImportDBConnectionFactory importDBConnectionFactory = ImportDBConnectionFactory.getInstance();
			importDBConnectionFactory.setDbConnectionParametersProvider( dbConnectionParametersProvider );
			importDBConnectionFactory.setDatabaseConnectionTestOnBorrow(true);
			DBConnectionFactory.setDbConnectionFactoryImpl( importDBConnectionFactory );
			ConfigSystemDAO configSystemDAO = ConfigSystemDAO.getInstance();
			ConfigSystemTableGetValueCommon.getInstance().setIConfigSystemTableGetValue( configSystemDAO );
			
			//  Read value from config_system table to ensure can access database
//			@SuppressWarnings("unused")
//			String valueNotInDB = configSystemDAO.getConfigValueForConfigKey( "KeyNotInDB" );

			ImporterRunnerMain importerRunnerMain = ImporterRunnerMain.getInstance();
			importRunnerProgramShutdown = new ImportRunnerProgramShutdown();
			importRunnerProgramShutdown.setImporterRunnerMain( importerRunnerMain );
			
			//   add a shutdown hook that will be called either when the operating system sends a SIGKILL signal on Unix or all threads terminate ( normal exit )
			//           Also called when ctrl-c is pressed on Unix or Windows when running in foreground
			//  public void addShutdownHook(Thread hook)
			Runtime runtime = Runtime.getRuntime();
			runtime.addShutdownHook( importRunnerProgramShutdown );
			
			///////////////////////////
			//   Main Processing
			importerRunnerMain.importerRunnerMain( maxTrackingRecordPriorityToRetrieve );
			
		} catch ( Exception e ) {
			System.out.println( "Exception in processing" );
			System.err.println( "Exception in processing" );
			e.printStackTrace( System.out );
			e.printStackTrace( System.err );
			throw e;
		} finally {
			Runtime runtime = Runtime.getRuntime();
			runtime.removeShutdownHook( importRunnerProgramShutdown );
			importRunnerProgramShutdown.setImporterRunnerMain( null );
			if ( log.isDebugEnabled() ) {
				log.debug( "Main Thread:  Calling DBConnectionFactory.closeAllConnections(); on main thread.");
			}
			try {
				// free up our db resources
				DBConnectionFactory.closeAllConnections();
				if ( log.isDebugEnabled() ) {
					log.debug( "COMPLETE:  Main Thread:  Calling DBConnectionFactory.closeAllConnections(); on main thread.");
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
		//		if ( programExitCode != PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED ) {
		//			System.exit( programExitCode );
		//		}
	}
	
	/**
	 * @throws Exception
	 */
	private static void printHelp() throws Exception {
		try( BufferedReader br = 
				new BufferedReader(
						new InputStreamReader( 
								ImporterRunnerProgram.class
								.getResourceAsStream( "/help_output_import_pgm_runner.txt" ) ) ) ) {
			String line = null;
			while ( ( line = br.readLine() ) != null )
				System.out.println( line );				
		} catch ( Exception e ) {
			System.out.println( "Error printing help." );
		}
	}
	
	/**
	 *
	 *  Class for processing kill signal. This is also run when all the threads in the application die/exit run()
	 */
	public static class ImportRunnerProgramShutdown extends Thread {
		
		//		private volatile Thread mainThread;
		
		private volatile ImporterRunnerMain importerRunnerMain;
		
		/*
		 * method that will run when kill signal is received
		 */
		public void run() {
			if ( log.isDebugEnabled() ) {
				log.debug( "ImportRunnerProgramShutdown::run() called now(): " + new Date());
			}
			Thread thisThread = Thread.currentThread();
			thisThread.setName( "Thread-Process-Shutdown-Request" );
			if ( importerRunnerMain != null ) {
				importerRunnerMain.shutdown();
			}
			if ( log.isDebugEnabled() ) {
				log.debug( "Calling DBConnectionFactory.closeAllConnections(); on shutdown thread to ensure connections closed.");
			}
			//  Ensure database connections get closed before program dies.
			try {
				// free up our db resources
				DBConnectionFactory.closeAllConnections();
				if ( log.isDebugEnabled() ) {
					log.debug( "COMPLETE:  Calling DBConnectionFactory.closeAllConnections(); on shutdown thread to ensure connections closed.");
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
			if ( log.isDebugEnabled() ) {
				log.debug( "ImportRunnerProgramShutdown::run() exiting now(): " + new Date() );
			}
		}
		public ImporterRunnerMain getImporterRunnerMain() {
			return importerRunnerMain;
		}
		public void setImporterRunnerMain(ImporterRunnerMain importerRunnerMain) {
			this.importerRunnerMain = importerRunnerMain;
		}
	}
}
