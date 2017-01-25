package org.yeastrc.proxl.import_xml_to_db_submit_pgm.program;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.config.ConfigParams;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportConfigException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportReportedErrorException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportServerReponseException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportUserDataException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportUsernamePasswordFileException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.main.SubmitProxlUploadMain;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.main.SubmitProxlUploadMain.SubmitResult;

/**
 * 
 *
 */
public class SubmitProxlImportProgram {

	private static final Logger log = Logger.getLogger(SubmitProxlImportProgram.class);

	private static final int PROGRAM_EXIT_CODE_INVALID_CONFIGURATION = 1;

	private static final int PROGRAM_EXIT_CODE_INVALID_INPUT = 2;
	
	private static final int PROGRAM_EXIT_CODE_ERROR_WITH_SERVER = 10;

	private static final int PROGRAM_EXIT_CODE_PROGRAM_PROBLEM = 99;

	private static final int PROGRAM_EXIT_CODE_HELP = 1;

	private static final String FOR_HELP_STRING = "For help, run without any parameters, -h, or --help";

	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		
		String usernameFromCommandLine = null;
		String passwordFromCommandLine = null;
		String usernamePasswordFileCommandLine = null;


		int projectId = -1;
		String projectIdString = null;
		
		String proxlXMLFileString = null;
		
		File proxlXMLFile = null;
		
		List<File> scanFiles = null;

		

		try {


			CmdLineParser cmdLineParser = new CmdLineParser();

			CmdLineParser.Option configFileFromCommandLineCommandLineOpt = cmdLineParser.addStringOption( 'c', "config" );

			CmdLineParser.Option projectIdFromCommandLineCommandLineOpt = cmdLineParser.addStringOption( 'p', "project-id" );

			CmdLineParser.Option proxlXMLFileFromCommandLineCommandLineOpt = cmdLineParser.addStringOption( 'i', "proxl-xml-file" );

			CmdLineParser.Option scanFilesFromCommandLineCommandLineOpt = cmdLineParser.addStringOption( 's', "scan-file" );

			CmdLineParser.Option noScanFilesCommandLineOpt = cmdLineParser.addBooleanOption( 'n', "no-scan-files" );


			CmdLineParser.Option searchNameFromCommandLineCommandLineOpt = cmdLineParser.addStringOption( 'Z', "search-description" );

			CmdLineParser.Option noSearchNameCommandLineOpt = cmdLineParser.addBooleanOption( 'Z', "no-search-description" );

			
			CmdLineParser.Option sendSearchPathCommandLineOpt = cmdLineParser.addBooleanOption( 'Z', "send-search-path" );
		
			
			//  Username and password 
			CmdLineParser.Option usernameCommandLineOpt = cmdLineParser.addStringOption( 'Z', "username" );

			CmdLineParser.Option passwordCommandLineOpt = cmdLineParser.addStringOption( 'Z', "password" );

			CmdLineParser.Option usernamePasswordFileCommandLineOpt = cmdLineParser.addStringOption( 'Z', "username-password-file" );
		
			


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

			
			usernameFromCommandLine = (String)cmdLineParser.getOptionValue( usernameCommandLineOpt );

			passwordFromCommandLine = (String)cmdLineParser.getOptionValue( passwordCommandLineOpt );
			
			usernamePasswordFileCommandLine = (String)cmdLineParser.getOptionValue( usernamePasswordFileCommandLineOpt );
			
			
			String configFile = (String)cmdLineParser.getOptionValue( configFileFromCommandLineCommandLineOpt );

			projectIdString = (String)cmdLineParser.getOptionValue( projectIdFromCommandLineCommandLineOpt );
			

			proxlXMLFileString = (String)cmdLineParser.getOptionValue( proxlXMLFileFromCommandLineCommandLineOpt );


			@SuppressWarnings("rawtypes")
			Vector inputScanFileStringVector = cmdLineParser.getOptionValues( scanFilesFromCommandLineCommandLineOpt );
			
			

			Boolean noScanFilesCommandLineOptChosen = (Boolean) cmdLineParser.getOptionValue( noScanFilesCommandLineOpt, Boolean.FALSE);


			if ( StringUtils.isNotEmpty( projectIdString ) ) {

				try {
					projectId = Integer.parseInt( projectIdString );

				} catch ( Exception e ) {

					System.err.println( "Project id on command line must be an integer. Value entered: " + projectIdString );

					System.exit(PROGRAM_EXIT_CODE_INVALID_INPUT);  //  EARLY EXIT
				}
			}
			
			if ( StringUtils.isEmpty(proxlXMLFileString)) {
				
				System.err.println( "Proxl XML file must be specified." );
				
				System.exit(PROGRAM_EXIT_CODE_INVALID_INPUT);  //  EARLY EXIT
			}

			proxlXMLFile = new File( proxlXMLFileString );

			if( ! proxlXMLFile.exists() ) {

				System.err.println( "Could not find Proxl XML file: " + proxlXMLFile.getAbsolutePath() );

				System.err.println( "" );
				System.err.println( FOR_HELP_STRING );

				System.exit(PROGRAM_EXIT_CODE_INVALID_INPUT);  //  EARLY EXIT
			}
			
			

			if ( inputScanFileStringVector != null && ( ! inputScanFileStringVector.isEmpty() ) ) {

				scanFiles = new ArrayList<>();

				for ( Object inputScanFileStringObject : inputScanFileStringVector ) {

					if ( ! (  inputScanFileStringObject instanceof String ) ) {

						System.err.println( "Internal ERROR:  inputScanFileStringObject is not a String object." );
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );

						System.exit(PROGRAM_EXIT_CODE_PROGRAM_PROBLEM);  //  EARLY EXIT
					}

					String inputScanFileString = (String) inputScanFileStringObject;

					if( inputScanFileString == null || inputScanFileString.equals( "" ) ) {

						System.err.println( "Internal ERROR:  inputScanFileStringObject is empty or null." );
						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );

						System.exit(PROGRAM_EXIT_CODE_PROGRAM_PROBLEM);  //  EARLY EXIT
					}

					File scanFile = new File( inputScanFileString );

					if( ! scanFile.exists() ) {

						System.err.println( "Could not find scan file: " + scanFile.getAbsolutePath() );

						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );

						System.exit(PROGRAM_EXIT_CODE_INVALID_INPUT);  //  EARLY EXIT
					}

					scanFiles.add( scanFile );
				}
			}

			

			String searchName = (String)cmdLineParser.getOptionValue( searchNameFromCommandLineCommandLineOpt );
			

			Boolean noSearchNameCommandLineOptChosen = (Boolean) cmdLineParser.getOptionValue( noSearchNameCommandLineOpt, Boolean.FALSE);
			
			Boolean sendSearchPathCommandLineOptChosen = (Boolean) cmdLineParser.getOptionValue( sendSearchPathCommandLineOpt, Boolean.FALSE);

			
			ConfigParams configParams = ConfigParams.getInstance();
			
			if ( StringUtils.isNotEmpty( configFile ) ) {
				
				File configFileCommandLine = new File( configFile );
				
				if ( ! configFileCommandLine.exists() ) {
					
					System.err.println( "config file specified on command line does not exist: " + configFileCommandLine.getAbsolutePath() );
					
					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );

					System.exit(PROGRAM_EXIT_CODE_INVALID_INPUT);  //  EARLY EXIT
				}
				
				configParams.setConfigFileCommandLine( configFileCommandLine );
			}
			
			configParams.readConfigParams();
			
			String baseURL = configParams.getProxlWebAppUrl();
			
			boolean submitterSameMachine = configParams.isSubmitterSameMachine();
			String uploadBaseDirString = configParams.getProxlUploadBaseDir();
			


			String baseURLWithServicesPath = baseURL + "/services";

			File uploadBaseDir = null;

			if ( StringUtils.isNotEmpty( uploadBaseDirString ) ) {

				uploadBaseDir = new File( uploadBaseDirString );


				if ( ! uploadBaseDir.exists() ) {

					System.err.println( "Upload Base Directory in configuration does not exist: " + uploadBaseDir.getCanonicalPath() );

					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );

					System.exit( PROGRAM_EXIT_CODE_INVALID_CONFIGURATION );  //  EARLY EXIT
				}
			}
			
			String searchPath = null;
			
			if ( sendSearchPathCommandLineOptChosen ) {

				try {
					searchPath = proxlXMLFile.getCanonicalFile().getParentFile().getCanonicalPath();


				} catch ( Exception e ) {

					System.err.println( "System error getting path for Proxl XML file: " + proxlXMLFile.getCanonicalPath() );

					System.err.println( "" );
					System.err.println( FOR_HELP_STRING );

					System.exit( PROGRAM_EXIT_CODE_PROGRAM_PROBLEM  );  //  EARLY EXIT

				}
			}
			
			
			System.out.println( "Run with '-h' to view help" );


			SubmitResult submitResult = 
					SubmitProxlUploadMain.getInstance().submitUpload(
							submitterSameMachine, 
							baseURL,
							baseURLWithServicesPath, 
							uploadBaseDir, 
							
							usernameFromCommandLine, 
							passwordFromCommandLine,
							usernamePasswordFileCommandLine,
							
							projectId, 
							projectIdString, 

							proxlXMLFile, 
							scanFiles,

							searchName,
							searchPath,
							
							noSearchNameCommandLineOptChosen,
							noScanFilesCommandLineOptChosen);
			
			
			System.exit( submitResult.getExitCode() );

			
		} catch ( ProxlSubImportUsernamePasswordFileException e ) {

			// Already reported so do not report
			
			System.out.println( "Program Failed.  See Syserr for more info.");
			System.exit( PROGRAM_EXIT_CODE_ERROR_WITH_SERVER );
			
		} catch ( ProxlSubImportReportedErrorException e ) {

			// Already reported so do not report
			
			System.out.println( "Program Failed.  See Syserr for more info.");
			System.exit( PROGRAM_EXIT_CODE_ERROR_WITH_SERVER );
						
		} catch ( ProxlSubImportUserDataException e ) {
			
			// Already reported so do nothing
			
			System.out.println( "Program Failed.  See Syserr for more info.");
			System.exit( PROGRAM_EXIT_CODE_ERROR_WITH_SERVER );

		} catch ( ProxlSubImportConfigException e ) {
			
			// Already reported so do nothing
			
			System.out.println( "Program Failed.  See Syserr for more info.");
			System.exit( PROGRAM_EXIT_CODE_ERROR_WITH_SERVER );

		} catch ( ProxlSubImportServerReponseException e ) {
			
			// Already reported so do nothing
			
			System.out.println( "Program Failed.  See Syserr for more info.");
			System.exit( PROGRAM_EXIT_CODE_ERROR_WITH_SERVER );
			
		} catch (Exception e) {

			System.out.println( "Program Failed.  See Syserr for more info.");
			log.error("Failed.", e );
			throw e;


		} finally {

		}

	}
	


	/**
	 * @throws Exception
	 */
	private static void printHelp() throws Exception {

		try( BufferedReader br = 
				new BufferedReader(
						new InputStreamReader( 
								SubmitProxlImportProgram.class
								.getResourceAsStream( "/help_output_submit_import_pgm.txt" ) ) ) ) {

			String line = null;
			while ( ( line = br.readLine() ) != null )
				System.out.println( line );				

		} catch ( Exception e ) {
			System.out.println( "Error printing help." );
		}


	}



}
