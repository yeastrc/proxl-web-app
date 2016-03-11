package org.yeastrc.proxl.import_xml_to_db.program_default;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.ScanFilenameConstants;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProvider;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.PrintHelpOnlyException;
import org.yeastrc.proxl.import_xml_to_db.importer_core_entry_point.ImporterCoreEntryPoint;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Default main entry point for the Kojak Importer program 
 *
 */
public class ImporterDefaultMainProgramEntry {

	private static final Logger log = Logger.getLogger(ImporterDefaultMainProgramEntry.class);


	private static final int PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED = 0;

//	private static final String DB_CONFIG_FILENAME_WITH_PATH_CMD_LINE_PARAM_STRING  = "db_config_filename_with_path";

	private static boolean databaseConnectionFactoryCreated = false;



	/**
	 * If set to false, assume it is already created
	 */
	private static boolean createDatabaseConnectionFactory = true;


	public static void databaseConnectionFactoryAlreadyCreated() {

		createDatabaseConnectionFactory = false;

		databaseConnectionFactoryCreated = true;
	}


	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		

		boolean successfulImport = false;

		int programExitCode = PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED;


		try {




			CmdLineParser cmdLineParser = new CmdLineParser();

			CmdLineParser.Option projectIdOpt = cmdLineParser.addIntegerOption( 'p', "project" );	

			CmdLineParser.Option noScanFilesCommandLineOpt = cmdLineParser.addBooleanOption( 'n', "no_scan_files" );

			
			CmdLineParser.Option helpOpt = cmdLineParser.addBooleanOption('h', "help"); 


			// parse command line options
			try { cmdLineParser.parse(args); }
			catch (IllegalOptionValueException e) {

				System.err.println(e.getMessage());

				programExitCode = 1;
				throw new PrintHelpOnlyException();
			}
			catch (UnknownOptionException e) {
				System.err.println(e.getMessage());

				programExitCode = 1;
				throw new PrintHelpOnlyException();
			}

			Boolean help = (Boolean) cmdLineParser.getOptionValue(helpOpt, Boolean.FALSE);
			if(help) {

				programExitCode = 1;
				throw new PrintHelpOnlyException();
			}

			Integer projectId = (Integer)cmdLineParser.getOptionValue( projectIdOpt );

			Boolean noScanFilesCommandLineOptChosen = (Boolean) cmdLineParser.getOptionValue( noScanFilesCommandLineOpt, Boolean.FALSE);

			String[] remainingArgs = cmdLineParser.getRemainingArgs();

			if( remainingArgs.length < 1 ) {
				System.err.println( "Got unexpected number of arguments.\n" );

				programExitCode = 1;
				throw new PrintHelpOnlyException();
			}


			if( noScanFilesCommandLineOptChosen != null 
					&& ( ! noScanFilesCommandLineOptChosen )
					&& remainingArgs.length < 2 ) {
				System.err.println( "At least one scan file is required since 'no scan files' param not specified.\n" );

				programExitCode = 1;
				throw new PrintHelpOnlyException();
			}

			if( projectId == null || projectId == 0 ) {
				System.err.println( "Must specify a project id using -p\n" );

				programExitCode = 1;
				throw new PrintHelpOnlyException();
			}

			String mainXMLFilenameToImport = remainingArgs[ 0 ];

			File mainXMLFileToImport = new File( mainXMLFilenameToImport );


			if( ! mainXMLFileToImport.exists() ) {

				System.err.println( "Could not find main XML File To Import file: " + mainXMLFileToImport.getAbsolutePath() );

				programExitCode = 1;
				throw new PrintHelpOnlyException();
			}

			List<File> scanFileList = new ArrayList<>( remainingArgs.length -1 );

			if ( remainingArgs.length > 1 ) {

				for ( int index = 1; index < remainingArgs.length; index++ ) {

					String scanFilename = remainingArgs[ index ];


					if ( ! ( scanFilename.endsWith( ScanFilenameConstants.MZ_ML_SUFFIX ) 
							|| scanFilename.endsWith( ScanFilenameConstants.MZ_XML_SUFFIX ) ) ) {

						System.err.println( "Scan file name must end with '"
								+ ScanFilenameConstants.MZ_ML_SUFFIX 
								+ "' or '"
								+ ScanFilenameConstants.MZ_XML_SUFFIX
								+ "' and have the correct contents to match the filename suffix.");

						System.err.println( "" );

						programExitCode = 1;
						throw new PrintHelpOnlyException();
					}

					File scanFile = new File( scanFilename );

					if( ! scanFile.exists() ) {

						System.err.println( "Could not find scan file: " + scanFile.getAbsolutePath() );

						programExitCode = 1;
						throw new PrintHelpOnlyException();
						//					System.exit( 1 );
					}


					scanFileList.add( scanFile );
				}
			}
			
			System.out.println( "Now: " + new Date() );
			System.out.println( "" );

			System.out.println( "Performing Proxl import for parameters:" );
			System.out.println( "project id: " + projectId );
			System.out.println( "main XML File To Import file: " + mainXMLFileToImport.getAbsolutePath() );

			if ( scanFileList == null || scanFileList.isEmpty() ) {

				System.out.println( " " );

				System.out.println( "No Scan files" );


				System.out.println( " " );

				System.out.println( " " );
				
			} else {

				System.out.println( " " );

				System.out.println( "Scan files full path:" );

				for ( File scanFile : scanFileList ) {

					System.out.println( scanFile.getCanonicalPath() );
				}


				System.out.println( " " );

				System.out.println( " " );

			}



			ImportProgramShutdown importProgramShutdown = new ImportProgramShutdown();

			//   add a shutdown hook that will be called either when the operating system sends a SIGKILL signal on Unix or all threads terminate ( normal exit )

			//           Also called when ctrl-c is pressed on Unix or Windows

			//  public void addShutdownHook(Thread hook)

			Runtime runtime = Runtime.getRuntime();
			runtime.addShutdownHook( importProgramShutdown );


			if ( createDatabaseConnectionFactory ) {

				DBConnectionParametersProvider dbConnectionParametersProvider = new DBConnectionParametersProvider();

				//				if ( dbConfigFile != null ) {
				//
				//					dbConnectionParametersProvider.setConfigFile( dbConfigFile );
				//				}

				dbConnectionParametersProvider.init();
				
				ImportDBConnectionFactory importDBConnectionFactory = ImportDBConnectionFactory.getInstance();

				importDBConnectionFactory.setDbConnectionParametersProvider( dbConnectionParametersProvider );
				


				DBConnectionFactory.setDbConnectionFactoryImpl( importDBConnectionFactory );

				databaseConnectionFactoryCreated = true;
			}


			//////////////////////////////////////

			//////////   Do the import

			ImporterCoreEntryPoint.getInstance().doImport( 
					projectId, 
					mainXMLFileToImport, 
					scanFileList );


			System.out.println( "" );
			System.out.println( "--------------------------------------" );
			System.out.println( "" );
			System.out.println( "Now: " + new Date() );
			System.out.println( "" );
			
			System.out.println( "Completed Proxl import for parameters:" );

			System.out.println( "project id: " + projectId );
			System.out.println( "main XML File To Import file: " + mainXMLFileToImport.getAbsolutePath() );

			if ( scanFileList == null || scanFileList.isEmpty() ) {

				System.out.println( " " );

				System.out.println( "No Scan files" );


				System.out.println( " " );

				System.out.println( " " );
				
			} else {

				System.out.println( " " );

				System.out.println( "Scan files full path:" );

				for ( File scanFile : scanFileList ) {

					System.out.println( scanFile.getCanonicalPath() );
				}


				System.out.println( " " );

				System.out.println( " " );

			}



			System.out.println( " " );

			System.out.println( "--------------------------------------" );

			System.out.println( " " );


			successfulImport = true;

		} catch ( PrintHelpOnlyException e ) {

			//  land here when only need to print the help

			printHelp();


		} catch ( Exception e ) {

			System.out.println( "Exception in processing" );
			System.err.println( "Exception in processing" );

			e.printStackTrace( System.out );
			e.printStackTrace( System.err );


			throw e;

		} finally {




			log.info( "Main Thread:  Calling DBConnectionFactory.closeAllConnections(); on main thread.");


			try {
				// free up our db resources
				DBConnectionFactory.closeAllConnections();


				log.info( "COMPLETE:  Main Thread:  Calling DBConnectionFactory.closeAllConnections(); on main thread.");


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

		if ( programExitCode != PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED ) {

			System.exit( programExitCode );
		}
	}



	private static void printHelp() throws Exception {

		String line = "Usage: <run jar script> -p project_id  "
				+ " [ -n | --no_scan_files ] "
				+ " <main xml file to import> [ <scan file to import> ... ]";

//		if ( createDatabaseConnectionFactory ) {
//
//			line += " [ --" + DB_CONFIG_FILENAME_WITH_PATH_CMD_LINE_PARAM_STRING + "=db_config_filename_and_path ] ";
//
//		}


		System.err.println( line );

		System.err.println( "E.g.:  java -jar <name of main jar>  -p 5 -no_scan_files /path/to/main_import_xml.xml " );
		System.err.println( "" );
		System.err.println( "<run jar script> is the appropriate script for your language to run the main jar with the other jars on the java class path" );
		System.err.println( "" );

		System.err.println( "The -p is required.");
		System.err.println( "" );
		System.err.println( "\"-n\"  or \"--no_scan_files\" is required if there are no scan files.");
		System.err.println( "" );
		System.err.println( "The scan files must be either " + ScanFilenameConstants.MZ_ML_SUFFIX
				+ " or " + ScanFilenameConstants.MZ_XML_SUFFIX 
				+ " and have the correct filename suffix that matches the contents.");

		System.err.println( "" );



		System.err.println( "" );
//
//		if ( createDatabaseConnectionFactory ) {
//
//			System.err.println( "" );
//			System.err.println( "The --" + DB_CONFIG_FILENAME_WITH_PATH_CMD_LINE_PARAM_STRING + " is optional.");
//			System.err.println( "If --" + DB_CONFIG_FILENAME_WITH_PATH_CMD_LINE_PARAM_STRING 
//					+ " is not provided, the filename '" 
//					+ DBConnectionParametersProvider.DB_CONFIG_FILENAME
//					+ "' will be searched for in the class path" );
//		}


//		System.err.println( "" );
//		System.err.println( "linkers available:" );
//		System.err.println( "" );
//		System.err.println( "abbr\tmonolink linker masses (the mass must match exactly for the variable mod to be identified as a monolink");
//		System.err.println( "" );
//
//		List<LinkerDTO>  linkerList = LinkerDAO.getInstance().getAllLinkerDTO();
//
//		for ( LinkerDTO linkerDTO : linkerList ) {
//
//			System.err.println( linkerDTO.getAbbr() );
//
//			List<LinkerMonolinkMassDTO>  linkerMonolinkMassList = LinkerMonolinkMassDAO.getInstance().getLinkerMonolinkMassDTOForLinkerId( linkerDTO.getId() );
//
//			for ( LinkerMonolinkMassDTO linkerMonolinkMassDTO : linkerMonolinkMassList ) {
//
//				System.err.println( "\t" + linkerMonolinkMassDTO.getMass() );
//			}
//
//		}
	}



	/**
	 *
	 *  Class for processing kill signal. This is also run when all the threads in the application die/exit run()
	 */
	public static class ImportProgramShutdown extends Thread {


		//		private volatile Thread mainThread;


		/*
		 * method that will run when kill signal is received
		 */
		public void run() {

			log.info( "ImportProgramShutdown::run() called now(): " + new Date());

			Thread thisThread = Thread.currentThread();

			thisThread.setName( "Thread-Process-Shutdown-Request" );



			log.info( "Calling DBConnectionFactory.closeAllConnections(); on shutdown thread to ensure connections closed.");

			//  Ensure database connections get closed before program dies.

			try {
				// free up our db resources
				DBConnectionFactory.closeAllConnections();

				log.info( "COMPLETE:  Calling DBConnectionFactory.closeAllConnections(); on shutdown thread to ensure connections closed.");


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



			System.out.println( "ImportProgramShutdown::run() exiting now(): " + new Date() );

		}


	}


}
