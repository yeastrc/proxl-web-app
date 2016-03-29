package org.yeastrc.proxl.import_xml_to_db.program_default;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.ScanFilenameConstants;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderFromPropertiesFile;
import org.yeastrc.proxl.import_xml_to_db.db.DBConnectionParametersProviderFromPropertiesFileException;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.PrintHelpOnlyException;
import org.yeastrc.proxl.import_xml_to_db.importer_core_entry_point.ImporterCoreEntryPoint;
import org.yeastrc.proxl.import_xml_to_db.objects.ImportResults;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Default main entry point for the Kojak Importer program 
 *
 */
public class ImporterDefaultMainProgramEntry {

	private static final Logger log = Logger.getLogger(ImporterDefaultMainProgramEntry.class);


	private static final int PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED = 0;
	
	private static final int PROGRAM_EXIT_CODE_INVALID_INPUT = 1;
	
	private static final int PROGRAM_EXIT_CODE_HELP = 1;
	
	private static final String FOR_HELP_STRING = "For help, run without any parameters, -h, or --help";

	
	private static final String PROXL_DB_NAME_CMD_LINE_PARAM_STRING = "proxl_db_name";

//	private static boolean databaseConnectionFactoryCreated = false;



	/**
	 * If set to false, assume it is already created
	 */
	private static boolean createDatabaseConnectionFactory = true;


	public static void databaseConnectionFactoryAlreadyCreated() {

		createDatabaseConnectionFactory = false;

//		databaseConnectionFactoryCreated = true;
	}


	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		importerDefaultMainProgramEntry( args );
	}
	
	
	/**
	 * @param args
	 * @return insertedSearchId
	 * @throws Exception 
	 */
	public static ImportResults importerDefaultMainProgramEntry( String[] args  ) throws Exception {
		
		
		ImportResults importResults = new ImportResults();
		
		
		boolean successfulImport = false;

		int programExitCode = PROGRAM_EXIT_CODE_DEFAULT_NO_SYTEM_EXIT_CALLED;


		try {
			
			if ( args.length == 0 ) {
				
	            printHelp();
	            
	            System.exit( PROGRAM_EXIT_CODE_HELP );
			}




			CmdLineParser cmdLineParser = new CmdLineParser();

			CmdLineParser.Option projectIdOpt = cmdLineParser.addIntegerOption( 'p', "project" );	

			CmdLineParser.Option inputProxlFileStringCommandLineOpt = cmdLineParser.addStringOption( 'i', "import-file" );

			CmdLineParser.Option noScanFilesCommandLineOpt = cmdLineParser.addBooleanOption( 'n', "no-scan-files" );

			CmdLineParser.Option inputScanFileStringCommandLineOpt = cmdLineParser.addStringOption( 's', "scan-file" );


			CmdLineParser.Option dbConfigFileNameCommandLineOpt = cmdLineParser.addStringOption( 'c', "config" );

			CmdLineParser.Option proxlDatabaseNameCommandLineOpt = cmdLineParser.addStringOption( 'Z', PROXL_DB_NAME_CMD_LINE_PARAM_STRING );


			CmdLineParser.Option verboseOpt = cmdLineParser.addBooleanOption('V', "verbose"); 
			CmdLineParser.Option debugOpt = cmdLineParser.addBooleanOption('D', "debug"); 

			
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
			
			
//			log.warn( "Log msg to WARN" );
//			log.error( "Log msg to ERROR" );

			Integer projectId = (Integer)cmdLineParser.getOptionValue( projectIdOpt );

			Boolean noScanFilesCommandLineOptChosen = (Boolean) cmdLineParser.getOptionValue( noScanFilesCommandLineOpt, Boolean.FALSE);

			String proxlDatabaseName = (String)cmdLineParser.getOptionValue( proxlDatabaseNameCommandLineOpt );
			
			String dbConfigFileName = (String)cmdLineParser.getOptionValue( dbConfigFileNameCommandLineOpt );
			

			String inputProxlFileString = (String)cmdLineParser.getOptionValue( inputProxlFileStringCommandLineOpt );
			
	        @SuppressWarnings("rawtypes")
	        Vector inputScanFileStringVector = cmdLineParser.getOptionValues( inputScanFileStringCommandLineOpt );

			
			
			String[] remainingArgs = cmdLineParser.getRemainingArgs();

			if( remainingArgs.length > 0 ) {

				System.out.println( "Unexpected command line parameters:");
				
				for ( String remainingArg : remainingArgs ) {
				
					System.out.println( remainingArg );
				}
				
				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			


			if( ( noScanFilesCommandLineOptChosen == null || ( ! noScanFilesCommandLineOptChosen ) )
					&& ( inputScanFileStringVector == null || ( inputScanFileStringVector.isEmpty() ) ) ) {
				
				System.err.println( "At least one scan file is required since 'no scan files' param not specified.\n" );

				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}

			if( projectId == null || projectId == 0 ) {
				System.err.println( "Must specify a project id using -p\n" );

				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}
			
			
			File dbConfigFile = null;
			
			if ( StringUtils.isNotEmpty( dbConfigFileName ) ) {

				dbConfigFile = new File( dbConfigFileName );



				if( ! dbConfigFile.exists() ) {

					System.err.println( "Could not find DB Config File: " + dbConfigFile.getAbsolutePath() );

					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );
					
					System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
				}
			}			


			File mainXMLFileToImport = new File( inputProxlFileString );

			
			importResults.setImportedProxlXMLFile( mainXMLFileToImport );
			
			

			if( ! mainXMLFileToImport.exists() ) {

				System.err.println( "Could not find main XML File To Import file: " + mainXMLFileToImport.getAbsolutePath() );

				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );
				
				System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
			}

			List<File> scanFileList = new ArrayList<>(  );
			
			if ( inputScanFileStringVector != null && ( ! inputScanFileStringVector.isEmpty() ) ) {

				for ( Object inputScanFileStringObject : inputScanFileStringVector ) {

					if ( ! (  inputScanFileStringObject instanceof String ) ) {

						System.err.println( "Internal ERROR:  inputScanFileStringObject is not a String object." );
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );

						System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
					}

					String inputScanFileString = (String) inputScanFileStringObject;

					if( inputScanFileString == null || inputScanFileString.equals( "" ) ) {

						System.err.println( "Internal ERROR:  inputScanFileStringObject is empty or null." );
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );

						System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
					}


					if ( ! ( inputScanFileString.endsWith( ScanFilenameConstants.MZ_ML_SUFFIX ) 
							|| inputScanFileString.endsWith( ScanFilenameConstants.MZ_XML_SUFFIX ) ) ) {

						System.err.println( "Scan file name must end with '"
								+ ScanFilenameConstants.MZ_ML_SUFFIX 
								+ "' or '"
								+ ScanFilenameConstants.MZ_XML_SUFFIX
								+ "' and have the correct contents to match the filename suffix.");

			        	System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						
						System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
					}

					File scanFile = new File( inputScanFileString );

					if( ! scanFile.exists() ) {

						System.err.println( "Could not find scan file: " + scanFile.getAbsolutePath() );
						
			        	System.err.println( "" );
						System.err.println( FOR_HELP_STRING );
						
						System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );
					}


					scanFileList.add( scanFile );
				}
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

				DBConnectionParametersProviderFromPropertiesFile dbConnectionParametersProvider = new DBConnectionParametersProviderFromPropertiesFile();

				if ( dbConfigFile != null ) {

					dbConnectionParametersProvider.setConfigFile( dbConfigFile );
				}
				
				try {

					dbConnectionParametersProvider.init();

				} catch ( DBConnectionParametersProviderFromPropertiesFileException e ) {

					
					
					System.exit( 1 );
					
					
					
				} catch ( Exception e ) {
					
					System.err.println( "Failed processing DB config file." );
					
					System.exit( 1 );
				}
					
				if ( StringUtils.isNotEmpty( proxlDatabaseName ) ) {
				
					dbConnectionParametersProvider.setProxlDbName( proxlDatabaseName );
				}
				
				ImportDBConnectionFactory importDBConnectionFactory = ImportDBConnectionFactory.getInstance();

				importDBConnectionFactory.setDbConnectionParametersProvider( dbConnectionParametersProvider );
				

				DBConnectionFactory.setDbConnectionFactoryImpl( importDBConnectionFactory );

//				databaseConnectionFactoryCreated = true;
			}


			//////////////////////////////////////

			//////////   Do the import

			int insertedSearchId = 
					ImporterCoreEntryPoint.getInstance().doImport( 
							projectId, 
							mainXMLFileToImport, 
							scanFileList );

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

			System.err.println( "" );
			System.err.println( FOR_HELP_STRING );
			
			System.exit( PROGRAM_EXIT_CODE_INVALID_INPUT );


		} catch ( Exception e ) {

			System.out.println( "Exception in processing" );
			System.err.println( "Exception in processing" );

			e.printStackTrace( System.out );
			e.printStackTrace( System.err );


			throw e;

		} finally {


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
		
		return importResults;
	}



	private static void printHelp() throws Exception {

		try( BufferedReader br = 
				new BufferedReader(
						new InputStreamReader( 
								ImporterDefaultMainProgramEntry.class
								.getResourceAsStream( "/help_output_import_proxl_xml.txt" ) ) ) ) {
			
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
	public static class ImportProgramShutdown extends Thread {


		//		private volatile Thread mainThread;


		/*
		 * method that will run when kill signal is received
		 */
		public void run() {

			if ( log.isDebugEnabled() ) {

				log.debug( "ImportProgramShutdown::run() called now(): " + new Date());
			}
			
			Thread thisThread = Thread.currentThread();

			thisThread.setName( "Thread-Process-Shutdown-Request" );



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

				log.debug( "ImportProgramShutdown::run() exiting now(): " + new Date() );
			}

		}


	}


}
