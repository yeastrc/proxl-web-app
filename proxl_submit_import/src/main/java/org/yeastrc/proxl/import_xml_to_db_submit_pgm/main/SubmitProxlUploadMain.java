package org.yeastrc.proxl.import_xml_to_db_submit_pgm.main;


import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.config.ConfigParams;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.ScanFilenameConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.UploadFileSubDirConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.enum_classes_from_base.ProxlXMLFileImportFileType;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportConfigException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportReportedErrorException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportServerReponseException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportUserDataException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportUsernamePasswordFileException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.get_submitter_key.GetSubmitterKey;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.java_console_abstraction.JavaConsoleAbstraction;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.java_console_abstraction.JavaConsoleAbstractionException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.java_console_abstraction.JavaConsoleAbstractionFactory;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.AreScanFileUploadsAllowedGet;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.AreScanFileUploadsAllowedGet.ScanFilesAllowedResult;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.ListProjectsGet;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.LoginPost;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.LogoutPost;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.UploadFilePost;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.UploadFilePost.UploadFileResult;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.UploadInitPost;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.UploadSubmitPost;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.ListProjectsGet.ProjectListForCurrentUserServiceResult;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.ListProjectsGet.ProjectListForCurrentUserServiceResultItem;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.UploadInitPost.UploadInitResult;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication.UploadSubmitPost.UploadSubmitResult;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication_objects.UploadSubmitRequest;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication_objects.UploadSubmitRequestFileItem;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.username_password_file.UsernamePasswordFileContents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class SubmitProxlUploadMain {

	private static final Logger log = Logger.getLogger(SubmitProxlUploadMain.class);

	

	private static final SubmitProxlUploadMain instance = new SubmitProxlUploadMain();

	private SubmitProxlUploadMain() { }
	public static SubmitProxlUploadMain getInstance() { return instance; }

	
	private static final int PROGRAM_EXIT_CODE_NO_ERROR = 0;

	private static final int PROGRAM_EXIT_CODE_INVALID_CONFIGURATION = 1;
	
	private static final int PROGRAM_EXIT_CODE_INVALID_INPUT = 2;
	
	private static final int PROGRAM_EXIT_CODE_NO_PROJECTS_FOR_USER = 3;
	
	private static final int PROGRAM_EXIT_CODE_UPLOAD_SUBMIT_FAILED = 4;

	private static final int PROGRAM_EXIT_CODE_UPLOAD_SEND_FILE_FAILED = 5;
	
//	private static final int PROGRAM_EXIT_CODE_PROGRAM_PROBLEM = 99;


	private static final String FOR_HELP_STRING = "For help, run without any parameters, -h, or --help";

	/**
	 * 
	 *
	 */
	public static class SubmitResult {
		
		int exitCode = PROGRAM_EXIT_CODE_NO_ERROR;

		public int getExitCode() {
			return exitCode;
		}

		public void setExitCode(int exitCode) {
			this.exitCode = exitCode;
		}
	}
	
	
	public SubmitResult submitUpload(
			
			boolean submitterSameMachine,
			String baseURL, 
			String baseURLWithServicesPath, 
			File uploadBaseDir,
			
			String usernameFromCommandLine,
			String passwordFromCommandLine, 
			String usernamePasswordFileCommandLine,
			
			int projectId,
			String projectIdString, 
			File proxlXMLFile, 

			List<File> scanFiles,
			
			String searchName,
			String searchPath,
			Boolean noSearchNameCommandLineOptChosen,
			
			
			Boolean noScanFilesCommandLineOptChosen
			
			) throws Exception,
			JavaConsoleAbstractionException, 
			IOException,
			JsonProcessingException {
		
		
		
		SubmitResult submitResult = new SubmitResult();
		

		String username = null;
		String password = null; 
		
		if ( StringUtils.isEmpty( usernameFromCommandLine ) 
				&& StringUtils.isEmpty( usernameFromCommandLine ) ) {

			if ( StringUtils.isNotEmpty( usernamePasswordFileCommandLine ) ) {

				File usernamePasswordFileCommandLineFile = new File( usernamePasswordFileCommandLine );

				if ( ! usernamePasswordFileCommandLineFile.exists() ) {

					System.err.println( " Username Password File does not exist: " 
							+ usernamePasswordFileCommandLineFile.getCanonicalPath() );
					submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
					return submitResult;    //  EARLY EXIT
				}

				UsernamePasswordFileContents usernamePasswordFileContents = UsernamePasswordFileContents.getInstance();

				usernamePasswordFileContents.setUsernamePasswordFileCommandLine( usernamePasswordFileCommandLineFile );

				usernamePasswordFileContents.readUsernamePasswordFileContents();

				username = usernamePasswordFileContents.getUsername();
				password = usernamePasswordFileContents.getPassword();
			}
		}

		if ( StringUtils.isNotEmpty( usernameFromCommandLine ) ) {

			username = usernameFromCommandLine;
		}

		if ( StringUtils.isNotEmpty( passwordFromCommandLine ) ) {

			password = passwordFromCommandLine;
		}
		

		CloseableHttpClient httpclient = null;

		try {


			Console systemConsole = System.console();

			if ( systemConsole == null ) {

				if ( StringUtils.isEmpty( usernameFromCommandLine ) 
						|| StringUtils.isEmpty( passwordFromCommandLine ) ) {

					System.out.println( "The environment this program is running in does not support reading passwords securely from user input" );
					System.out.println( "All data entered will be echoed to the screen.");
				}

			}


			//  Create a Java Console abstraction object that handles when the Java Console cannot be created (like running in an IDE)
			JavaConsoleAbstraction javaConsoleAbstraction = JavaConsoleAbstractionFactory.defaultConsoleIO();


			System.out.println( "Connecting to Proxl web app using URL: " + baseURL );

			//  Create Apache HTTP Client instance for connecting to the web app on the server

			httpclient = HttpClients.createDefault();


			//  Get info on if scan files are allowed

			ScanFilesAllowedResult scanFilesAllowedResult =
					AreScanFileUploadsAllowedGet.getInstance().areScanFileUploadsAllowedGet( baseURLWithServicesPath, httpclient );



			if ( ! scanFilesAllowedResult.isScanFilesAllowed() ) {

				if ( scanFiles != null && ( ! scanFiles.isEmpty() ) ) {

					System.err.println( "Import of scan files to this installation of Proxl is denied." );

					submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
					
					return submitResult;  //  EARLY EXIT
				}

			} else {

				// Scan files allowed

				if ( ( ! noScanFilesCommandLineOptChosen ) && ( scanFiles == null || scanFiles.isEmpty() ) ) {

					System.err.println( "Must specify no scan files if there are no scan files." );

					submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
					
					return submitResult;  //  EARLY EXIT
				}

				if ( noScanFilesCommandLineOptChosen && ( scanFiles != null && ( ! scanFiles.isEmpty() ) ) ) {

					System.err.println( "cannot specify a scan file and no scan files at the same time." );

					submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
					
					return submitResult;  //  EARLY EXIT
				}
			}



			if ( scanFiles != null && ( ! scanFiles.isEmpty() ) ) {

				for ( File scanFile : scanFiles ) {

					String errorStringScanSuffixValidation = validateScanFileSuffix( scanFile.getName() );

					if ( errorStringScanSuffixValidation != null ) {

						System.err.println( errorStringScanSuffixValidation );

						System.err.println( "" );
						System.err.println( FOR_HELP_STRING );

						submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
						
						return submitResult;  //  EARLY EXIT
					}

				}
			}
			

			if ( StringUtils.isEmpty( username ) ) {

				javaConsoleAbstraction.writer().write( "username: " );

				javaConsoleAbstraction.writer().flush();

				username = javaConsoleAbstraction.readLine();

				if ( StringUtils.isEmpty( username ) ) {

					System.out.println( "username and password are required." );

					submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
					
					return submitResult;  //  EARLY EXIT
				}
			}

			if ( StringUtils.isEmpty( password ) ) {

				javaConsoleAbstraction.writer().write( "password: " );

				javaConsoleAbstraction.writer().flush();

				password = new String( javaConsoleAbstraction.readPassword() );

				if ( StringUtils.isEmpty( password ) ) {

					System.out.println( "username and password are required." );

					submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
					
					return submitResult;  //  EARLY EXIT
				}
			}

			//  Throws ProxlSubImportUserDataException on failed login
			LoginPost.getInstance().loginPost( username, password, baseURLWithServicesPath, httpclient );


			///////////////

			/////  Project Id for import


			//  Project List

			ProjectListForCurrentUserServiceResult projectListForCurrentUserServiceResult =
					ListProjectsGet.getInstance().listProjectsGet( baseURLWithServicesPath, httpclient );

			if ( projectListForCurrentUserServiceResult.getProjectList().isEmpty() ) {

				System.out.println( "There are no projects available for upload.  See Help (-h) for more info.");

				submitResult.exitCode = PROGRAM_EXIT_CODE_NO_PROJECTS_FOR_USER;
				
				return submitResult;  //  EARLY EXIT
			}


			if ( projectIdString != null ) {

				// Validate project id on command line, exit if not valid

				boolean validProjectIdEntered = false;

				for ( ProjectListForCurrentUserServiceResultItem projectItem : projectListForCurrentUserServiceResult.getProjectList() ) {

					if ( projectItem.getId() == projectId ) {

						validProjectIdEntered = true;
						break;
					}
				}

				if ( ! validProjectIdEntered ) {

					System.out.println();
					System.out.println( "The project id entered is not valid." );

					submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
					
					return submitResult;  //  EARLY EXIT
				}

			}



			boolean validProjectIdEntered = false;

			while ( ! validProjectIdEntered ) {

				if ( projectIdString != null ) {

					for ( ProjectListForCurrentUserServiceResultItem projectItem : projectListForCurrentUserServiceResult.getProjectList() ) {

						if ( projectItem.getId() == projectId ) {

							validProjectIdEntered = true;
							break;
						}
					}

					if ( ! validProjectIdEntered ) {

						System.out.println();
						System.out.println( "The project id entered is not valid." );
					}

				}

				if ( ! validProjectIdEntered ) {

					System.out.println( "Choose one of the following projects to upload to.  Enter the project id in () before the project title" );

					for ( ProjectListForCurrentUserServiceResultItem projectItem : projectListForCurrentUserServiceResult.getProjectList() ) {

						System.out.print( "(" + projectItem.getId() + ") ");
						System.out.println( projectItem.getTitle() );
					}

					javaConsoleAbstraction.writer().write( "project id: " );

					javaConsoleAbstraction.writer().flush();

					projectIdString = javaConsoleAbstraction.readLine();

					if ( StringUtils.isEmpty( projectIdString ) ) {

						System.out.println( "a project id required." );

						submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
						
						return submitResult;  //  EARLY EXIT
					}

					try {
						projectId = Integer.parseInt( projectIdString );

					} catch ( Exception e ) {

						System.err.println( "Project id on command line must be an integer. Value entered: " + projectIdString );

						submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
						
						return submitResult;  //  EARLY EXIT
					}
				}

			}


			if ( StringUtils.isEmpty( searchName ) && ( ! noSearchNameCommandLineOptChosen ) ) {

				javaConsoleAbstraction.writer().write( "Brief description of the search (optional, press enter if no value): " );

				javaConsoleAbstraction.writer().flush();

				searchName = javaConsoleAbstraction.readLine();

				if ( StringUtils.isEmpty( searchName ) ) {

					searchName = null;
				}
			}



			UploadInitResult uploadInitResult = 
					UploadInitPost.getInstance()
					.uploadInitPost( projectIdString, submitterSameMachine, baseURLWithServicesPath, httpclient );

			if ( ! uploadInitResult.isStatusSuccess() ) {
				
				if ( uploadInitResult.isProjectLocked() ) {
					
					System.err.println( "Unable to upload to this project as it is now locked." );

					submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
					
					return submitResult;    //  EARLY EXIT
				}

				System.err.println( "Upload failed at init.  Please try again." );

				System.err.println( "If this error continues, contact the administrator of your Proxl Instance." );
				
				submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_INPUT;
				
				return submitResult;    //  EARLY EXIT
				
			}
			
			
			if ( log.isDebugEnabled() ) {

				System.out.println( "UploadTempSubdir: " + uploadInitResult.getUploadTempSubdir() );
			}

			String submitterKey = null;

			if ( submitterSameMachine ) {

				File uploadTmpBaseDir = new File( uploadBaseDir, UploadFileSubDirConstants.UPLOAD_FILE_TEMP_BASE_DIR );


				if ( ! uploadTmpBaseDir.exists() ) {

					System.err.println( "Configuration Error or System Error:  "
							+ "Temp Upload Base Directory does not exist: " + uploadTmpBaseDir.getCanonicalPath() );


					submitResult.exitCode = PROGRAM_EXIT_CODE_INVALID_CONFIGURATION;
					
					return submitResult;    //  EARLY EXIT
				}

				submitterKey = 
						GetSubmitterKey.getInstance().getSubmitterKey( uploadInitResult.getUploadTempSubdir(), uploadTmpBaseDir );

//				System.out.println( "submitterKey:" + submitterKey );
			}


			//////////

			//   Build Submit objects and upload files to server if not running this pgm on server



			UploadSubmitRequest uploadSubmitRequest = new UploadSubmitRequest();

			uploadSubmitRequest.setProjectId( projectId );
			uploadSubmitRequest.setSearchName( searchName ); // optional
			uploadSubmitRequest.setSearchPath( searchPath ); // optional
			uploadSubmitRequest.setUploadKey( uploadInitResult.getUploadKey() );

			if ( submitterSameMachine ) {
				uploadSubmitRequest.setSubmitterSameMachine( true );
				uploadSubmitRequest.setSubmitterKey( submitterKey );
			}

			List<UploadSubmitRequestFileItem> fileItems = new ArrayList<>();
			uploadSubmitRequest.setFileItems( fileItems );



			int fileIndex = 1;

			{
				//  Process Proxl XML file

				UploadSubmitRequestFileItem uploadSubmitRequestFileItem = new UploadSubmitRequestFileItem();
				fileItems.add(uploadSubmitRequestFileItem);

				uploadSubmitRequestFileItem.setFileIndex( fileIndex );
				uploadSubmitRequestFileItem.setFileType( ProxlXMLFileImportFileType.PROXL_XML_FILE.value() );
				uploadSubmitRequestFileItem.setIsProxlXMLFile( true );
				uploadSubmitRequestFileItem.setUploadedFilename( proxlXMLFile.getName() );

				if ( submitterSameMachine ) {

					uploadSubmitRequestFileItem.setFilenameOnDiskWithPathSubSameMachine( proxlXMLFile.getCanonicalPath() );
				}

				if ( ! submitterSameMachine ) {

					UploadFileResult uploadFileResult =
							UploadFilePost.getInstance()
							.uploadFilePost( 
									proxlXMLFile, 
									uploadSubmitRequestFileItem.getFileIndex(), 
									uploadSubmitRequestFileItem.getFileType(), 
									projectIdString, 
									uploadInitResult.getUploadKey(), 
									baseURL, 
									httpclient );


					if ( ! uploadFileResult.isStatusSuccess() ) {
						
						System.err.println( "FAILED sending Proxl XML file to server: " + proxlXMLFile.getCanonicalPath() );

						System.err.println( "If this error continues, contact the administrator of your Proxl Instance." );
						
						submitResult.exitCode = PROGRAM_EXIT_CODE_UPLOAD_SEND_FILE_FAILED;
						
						return submitResult;    //  EARLY EXIT
						
					}
					
					
					System.out.println( "Sent Proxl XML file to server: " + proxlXMLFile.getCanonicalPath() );
				}
			}


			//	Process scanFiles

			if ( scanFiles != null ) {

				for ( File scanFile : scanFiles ) {

					fileIndex++;

					UploadSubmitRequestFileItem uploadSubmitRequestFileItem = new UploadSubmitRequestFileItem();
					fileItems.add(uploadSubmitRequestFileItem);

					uploadSubmitRequestFileItem.setFileIndex( fileIndex );
					uploadSubmitRequestFileItem.setFileType( ProxlXMLFileImportFileType.SCAN_FILE.value() );
					uploadSubmitRequestFileItem.setIsProxlXMLFile( false );
					uploadSubmitRequestFileItem.setUploadedFilename( scanFile.getName() );

					if ( submitterSameMachine ) {

						uploadSubmitRequestFileItem.setFilenameOnDiskWithPathSubSameMachine( scanFile.getCanonicalPath() );
					}

					if ( ! submitterSameMachine ) {

						UploadFileResult uploadFileResult =
								UploadFilePost.getInstance()
								.uploadFilePost( 
										scanFile, 
										uploadSubmitRequestFileItem.getFileIndex(), 
										uploadSubmitRequestFileItem.getFileType(), 
										projectIdString, 
										uploadInitResult.getUploadKey(), 
										baseURL, 
										httpclient );

						if ( ! uploadFileResult.isStatusSuccess() ) {
							
							System.err.println( "FAILED sending Scan file to server: " + proxlXMLFile.getCanonicalPath() );

							System.err.println( "If this error continues, contact the administrator of your Proxl Instance." );
							
							submitResult.exitCode = PROGRAM_EXIT_CODE_UPLOAD_SEND_FILE_FAILED;
							
							return submitResult;    //  EARLY EXIT
							
						}
						
						System.out.println( "Sent Scan file to server: " + scanFile.getCanonicalPath() );
					}
				}
			}

			// Submit the upload:  send the JSON submit 


			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object

			byte[] uploadSubmitRequest_JSON = mapper.writeValueAsBytes( uploadSubmitRequest );


			UploadSubmitResult uploadSubmitResult =
					UploadSubmitPost.getInstance().uploadSubmitPost( uploadSubmitRequest_JSON, baseURLWithServicesPath, httpclient );

			if ( ! uploadSubmitResult.isStatusSuccess() ) {

				System.err.println( "Upload Submit Failed" );

				submitResult.exitCode = PROGRAM_EXIT_CODE_UPLOAD_SUBMIT_FAILED;
				
				return submitResult;  //  EARLY EXIT
			}

			if ( submitterSameMachine ) {

//				System.out.println( "ImporterSubDir: (Null if not submit from same machine) " + uploadSubmitResult.getImporterSubDir() );

				File importBaseDir = new File( uploadBaseDir, UploadFileSubDirConstants.IMPORT_BASE_DIR );


				File importSubDir = new File( importBaseDir, uploadSubmitResult.getImporterSubDir() );

//				System.out.println( "Importer Sub Dir full path: " + importSubDir.getCanonicalPath() );

				if ( ! importSubDir.exists() ) {

//					System.out.println( "Importer Sub Dir full path does NOT EXIST: " + importSubDir.getCanonicalPath() );
				}
				
				String writeProxlUploadDirFilename = ConfigParams.getInstance().getWriteProxlUploadDirFilename();
				
				if ( StringUtils.isNotEmpty( writeProxlUploadDirFilename ) ) {
					
					BufferedWriter writer = null;
					
					try {
						
						writer = new BufferedWriter( new FileWriter( writeProxlUploadDirFilename ) );
						
						writer.write( "Importer Directory:");
						writer.newLine();
						writer.write( importSubDir.getCanonicalPath() );
						writer.newLine();
						
					} catch ( Exception e ) {
						
						System.err.println( "Failed to write file '" + writeProxlUploadDirFilename 
								+ "' containing the import directory.");
						e.printStackTrace();
						throw e;
						
					} finally {
						
						if ( writer != null ) {
							
							writer.close();
						}
					}
				}

			}

//			System.out.println( "Submitted Upload");


			//  Log out

			LogoutPost.getInstance().logoutPost( baseURLWithServicesPath, httpclient );

//			System.out.println( "Logout complete");
			
			
			System.out.println( "Submission complete");

			
		} catch ( ProxlSubImportUsernamePasswordFileException e ) {

			// Already reported so do not report
			
			throw e;
			
		} catch ( ProxlSubImportReportedErrorException e ) {

			// Already reported so do not report
			
			throw e;
		
		} catch ( ProxlSubImportUserDataException e ) {
			
			// Already reported so do not report

			throw e;
			
		} catch ( ProxlSubImportConfigException e ) {
			
			// Already reported so do not report

			throw e;
			
		} catch ( ProxlSubImportServerReponseException e ) {
			
			// Already reported so do not report
			
			throw e;
			
		} catch (Exception e) {

			log.error("Failed.", e );
			throw e;


		} finally {

			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.close();

		}

		return submitResult;
	}
	

	
	/**
	 * validateScanFileSuffix
	 * 
	 * @param inputScanFileString
	 * @return null if no error, otherwise return the error message
	 */
	private static String validateScanFileSuffix( String inputScanFileString ) {
		
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
	
}
