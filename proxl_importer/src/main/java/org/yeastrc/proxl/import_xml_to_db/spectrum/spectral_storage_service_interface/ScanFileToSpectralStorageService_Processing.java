package org.yeastrc.proxl.import_xml_to_db.spectrum.spectral_storage_service_interface;

import java.io.File;
import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.config.ImporterConfigFileData_OtherThanDBConfig;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterConfigException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterSpectralStorageServiceErrorException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterSpectralStorageServiceRetryExceededException;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums.WebserviceSpectralStorageAcceptImport_ProcessStatusEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.Get_UploadedScanFileInfo_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.Get_UploadedScanFileInfo_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_AddScanFileFromFilenameAndPath_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_AddScanFileFromFilenameAndPath_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Delete_For_ScanProcessStatusKey_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Delete_For_ScanProcessStatusKey_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Init_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Init_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Submit_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Submit_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_UploadScanFile_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_UploadScanFile_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.webservice_connect.main.CallSpectralStorageAcceptImportWebservice;
import org.yeastrc.spectral_storage.accept_import_web_app.webservice_connect.main.CallSpectralStorageAcceptImportWebserviceInitParameters;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemTableGetValueCommon;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.dto.ScanFileDTO;

/**
 * Send the scan file to the Spectral Storage Service, get the API Key, update API key in scan_file table
 *
 */
public class ScanFileToSpectralStorageService_Processing {

	private static final Logger log = LoggerFactory.getLogger( ScanFileToSpectralStorageService_Processing.class);
	
	//  Send Scan File to Spectral Storage Service Retry Max and Delay
	
	private static final int SEND_FILE_RETRY_COUNT_MAX = 10;
	private static final int SEND_FILE_RETRY_DELAY = 2 * 1000; // 2 second

	//  Wait Delay after Send/Submit to Spectral Storage Service
	
	private static final int WAIT_AFTER_SEND_SUBMIT_DELAY = 3 * 1000; // 3 second
	
	//  Get API Key from Spectral Storage Service Retry Max and Delay.  Incrementally longer delays after more retries
	
	private static final int GET_API_KEY_RETRY_COUNT_MAX_STEP_1 = 5;  // First 5 retries
	private static final int GET_API_KEY_RETRY_DELAY_STEP_1 = 5 * 1000; // 5 second
	
	private static final int GET_API_KEY_RETRY_COUNT_MAX_TOTAL_ALLOWED = 6500;  // Total number of retries allowed
	private static final int GET_API_KEY_RETRY_DELAY_STEP_LAST = 15 * 1000; // 15 second
	
	///   Retry for max of roughly 26 hours.  Could exceed this if another web app or Proxl Instance has uploaded a lot of scan files to Spectra Storage Service
	
	// TODOO TEMP smaller values
	
//	private static final int GET_API_KEY_RETRY_COUNT_MAX_STEP_1 = 1;
//	private static final int GET_API_KEY_RETRY_DELAY_STEP_1 = 10; // 10 milliseconds
//	
//	private static final int GET_API_KEY_RETRY_COUNT_MAX_STEP_2 = 1;
//	private static final int GET_API_KEY_RETRY_DELAY_STEP_2 = 10; // 10 milliseconds
//	
//	private static final int GET_API_KEY_RETRY_COUNT_MAX_STEP_3 = 1;
//	private static final int GET_API_KEY_RETRY_DELAY_STEP_3 = 10; // 10 milliseconds

	private static ScanFileToSpectralStorageService_Processing _INSTANCE = new ScanFileToSpectralStorageService_Processing();

	private ScanFileToSpectralStorageService_Processing() { }
	public static ScanFileToSpectralStorageService_Processing getStaticInstance() { return _INSTANCE; }

	/**
	 * Send the scan file to the Spectral Storage Service, get the API Key, update API key in scan_file table
	 * 
	 * @param scanFileWithPath
	 * @param scanFileDTO
	 * @throws Exception 
	 */
	public void sendScanFileToSpectralStorageServiceUpdateScanFileSpectralStorageAPIKey( File scanFileWithPath, ScanFileDTO scanFileDTO ) throws Exception {
		
		String spectralStorageServiceBaseURL = 
				ConfigSystemTableGetValueCommon.getInstance()
				.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.SPECTRAL_STORAGE_SERVICE_ACCEPT_IMPORT_BASE_URL );
		
		if ( StringUtils.isEmpty( spectralStorageServiceBaseURL ) ) {
			String msg = "No Config value for Spectral Storage Base URL, key: " + ConfigSystemsKeysSharedConstants.SPECTRAL_STORAGE_SERVICE_ACCEPT_IMPORT_BASE_URL;
			log.error( msg );
			throw new ProxlImporterConfigException( msg );
		}
		
		try {
			CallSpectralStorageAcceptImportWebserviceInitParameters initParams = new CallSpectralStorageAcceptImportWebserviceInitParameters();

			initParams.setSpectralStorageServerBaseURL( spectralStorageServiceBaseURL );

			CallSpectralStorageAcceptImportWebservice callSpectralStorageWebservice = CallSpectralStorageAcceptImportWebservice.getInstance();

			callSpectralStorageWebservice.init( initParams );
			
			log.warn( "Calling spectralStorageService_InitUploadScanFileProcess(...) scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
			
			UploadScanFile_Init_Response uploadScanFile_Init_Response =
					spectralStorageService_InitUploadScanFileProcess( scanFileWithPath, scanFileDTO, callSpectralStorageWebservice );

			Thread.sleep( 2000 ); // 2 second sleep
			
			boolean sendScanFileLocationCompleteSuccessful = false;
			
			if ( ImporterConfigFileData_OtherThanDBConfig.isSpectralStorageService_sendScanFileLocation() ) {
				
				boolean sendScanFileLocation = true;
				if ( ImporterConfigFileData_OtherThanDBConfig.getSpectralStorageService_sendScanFileLocation_IfPathStartsWith() != null ) {
					String ifPathStartsWith = ImporterConfigFileData_OtherThanDBConfig.getSpectralStorageService_sendScanFileLocation_IfPathStartsWith();
					String scanFileWithPath_CanonicalPathString = scanFileWithPath.getCanonicalPath();
					if ( ! scanFileWithPath_CanonicalPathString.startsWith( ifPathStartsWith ) ) {
						sendScanFileLocation = false;
					}
				}

				if ( sendScanFileLocation ) {

					if ( log.isInfoEnabled() ) {
						log.info( "INFO: Calling sendScanFilenameWithPathToSpectralStorageService(...) scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
					}

					UploadScanFile_AddScanFileFromFilenameAndPath_Response uploadScanFile_AddScanFileFromFilenameAndPath_Response =
							sendScanFilenameWithPathToSpectralStorageService( uploadScanFile_Init_Response, scanFileWithPath, callSpectralStorageWebservice );

					if ( log.isInfoEnabled() ) {
						log.info( "INFO: After sendScanFilenameWithPathToSpectralStorageService(...) scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
					}

					if ( uploadScanFile_AddScanFileFromFilenameAndPath_Response.isStatusSuccess() ) {

						sendScanFileLocationCompleteSuccessful = true;

					} else {

						//  Check in this order since if isUploadScanFileWithPath_FilePathsAllowedNotConfigured is true, isUploadScanFileWithPath_FilePathNotAllowed is also set to true
						if ( uploadScanFile_AddScanFileFromFilenameAndPath_Response.isUploadScanFileWithPath_FilePathsAllowedNotConfigured() ) {

							//  Already reported in called method
//							log.warn( "Send of Scan file with path to Spectral Storage Service rejected.  Will next send scan file contents" );
//							log.warn( "  ... addnl info: Proxl Importer configured to send Scan file path to Spectral Storage Service but Spectral Storage Service not configured to accept Scan File Locations." );
//							log.warn( "  ... addnl info: call sendScanFilenameWithPathToSpectralStorageService(...) returned statusSuccess False" );

						} else if ( uploadScanFile_AddScanFileFromFilenameAndPath_Response.isUploadScanFileWithPath_FilePathNotAllowed() ) {

							//  Already reported in called method
//							log.warn( "Send of Scan file with path to Spectral Storage Service rejected.  Will next send scan file contents" );
//							log.warn( "  ... addnl info: Proxl Importer configured to send Scan file path to Spectral Storage Service but for this specific scan file, the Scan file path was not allowed.  Scan File with path (Java Get Canonical file with Path): "
//									+ scanFileWithPath.getCanonicalPath() );
//							log.warn( "  ... addnl info: call sendScanFilenameWithPathToSpectralStorageService(...) returned statusSuccess False" );

						} else {
							String msg = "Send of Scan file with path to Spectral Storage Service Failed.";
							log.warn( msg );
							log.warn( "  ... addnl info: call sendScanFilenameWithPathToSpectralStorageService(...) returned statusSuccess False" );
							if ( uploadScanFile_AddScanFileFromFilenameAndPath_Response.isUploadScanFileTempKey_NotFound() ) {
								log.warn( "  ... addnl info: For some reason the key returned by the init call is no longer in the system at Spectral Storage Service.  submitScanFileToSpectralStorageService(...) returned 'UploadScanFileTempKey_NotFound' true. UploadScanFileTempKey: " 
										+ uploadScanFile_Init_Response.getUploadScanFileTempKey() );
							}
							if ( uploadScanFile_AddScanFileFromFilenameAndPath_Response.isUploadScanFileTempKey_Expired() ) {
								log.warn( "  ... addnl info: Too much time has elapsed since the call to the start of this submit scan file to spectral storage service (too much time since call to init). submitScanFileToSpectralStorageService(...) returned 'UploadScanFileTempKey_Expired' true. UploadScanFileTempKey: " 
										+ uploadScanFile_Init_Response.getUploadScanFileTempKey() );
							}
							if ( uploadScanFile_AddScanFileFromFilenameAndPath_Response.isUploadScanFileTempKey_NotFound() ) {
								log.warn( "  ... addnl info: sendScanFilenameWithPathToSpectralStorageService(...) returned 'UploadScanFileTempKey_NotFound' true. UploadScanFileTempKey: " + uploadScanFile_Init_Response.getUploadScanFileTempKey() );
							}
							if ( uploadScanFile_AddScanFileFromFilenameAndPath_Response.isUploadScanFileTempKey_Expired() ) {
								log.warn( "  ... addnl info: sendScanFilenameWithPathToSpectralStorageService(...) returned 'UploadScanFileTempKey_Expired' true. UploadScanFileTempKey: " + uploadScanFile_Init_Response.getUploadScanFileTempKey() );
							}
							throw new ProxlImporterSpectralStorageServiceErrorException( msg );
						}
					}
				}
			}

			if ( ! sendScanFileLocationCompleteSuccessful ) {

				//  Sending Scan File with Path not done or not accepted, so sending the file contents

				if ( log.isInfoEnabled() ) {
					log.info( "INFO: Calling sendScanFileToSpectralStorageService_ActuallySendScanFile(...) scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
				}
				
				UploadScanFile_UploadScanFile_Response uploadScanFile_UploadScanFile_Response = 
						sendScanFileToSpectralStorageService_ActuallySendScanFile( uploadScanFile_Init_Response, scanFileWithPath, scanFileDTO, callSpectralStorageWebservice );

				if ( log.isInfoEnabled() ) {
					log.info( "INFO: After sendScanFileToSpectralStorageService(...) (will next sleep for X seconds) scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
				}

				if ( ! uploadScanFile_UploadScanFile_Response.isStatusSuccess() ) {
					String msg = "Send of Scan file Contents to Spectral Storage Service Failed.";
					log.warn( msg );
					log.warn( "  ... addnl info: call sendScanFileToSpectralStorageService_ActuallySendScanFile(...) returned statusSuccess False" );
					if ( uploadScanFile_UploadScanFile_Response.isUploadScanFileTempKey_NotFound() ) {
						log.warn( "  ... addnl info: For some reason the key returned by the init call is no longer in the system at Spectral Storage Service.  submitScanFileToSpectralStorageService(...) returned 'UploadScanFileTempKey_NotFound' true. UploadScanFileTempKey: " 
								+ uploadScanFile_Init_Response.getUploadScanFileTempKey() );
					}
					if ( uploadScanFile_UploadScanFile_Response.isUploadScanFileTempKey_Expired() ) {
						log.warn( "  ... addnl info: Too much time has elapsed since the call to the start of this submit scan file to spectral storage service (too much time since call to init). submitScanFileToSpectralStorageService(...) returned 'UploadScanFileTempKey_Expired' true. UploadScanFileTempKey: " 
								+ uploadScanFile_Init_Response.getUploadScanFileTempKey() );
					}
					if ( uploadScanFile_UploadScanFile_Response.isUploadScanFileTempKey_NotFound() ) {
						log.warn( "  ... addnl info: sendScanFileToSpectralStorageService_ActuallySendScanFile(...) returned 'UploadScanFileTempKey_NotFound' true. UploadScanFileTempKey: " + uploadScanFile_Init_Response.getUploadScanFileTempKey() );
					}
					if ( uploadScanFile_UploadScanFile_Response.isUploadScanFileTempKey_Expired() ) {
						log.warn( "  ... addnl info: sendScanFileToSpectralStorageService_ActuallySendScanFile(...) returned 'UploadScanFileTempKey_Expired' true. UploadScanFileTempKey: " + uploadScanFile_Init_Response.getUploadScanFileTempKey() );
					}

					throw new ProxlImporterSpectralStorageServiceErrorException( msg );
				}

				Thread.sleep( 2000 ); // sleep in milliseconds
			}
			
			UploadScanFile_Submit_Response uploadScanFile_Submit_Response =
					submitScanFileToSpectralStorageService( 
							uploadScanFile_Init_Response,
							scanFileWithPath, 
							scanFileDTO,
							callSpectralStorageWebservice );

			//  Wait Delay for submit to Spectral Storage Service to be fully stabilized
			Thread.sleep( WAIT_AFTER_SEND_SUBMIT_DELAY );
			
			getSpectralServiceAPI_AndUpdateScanFileTable( scanFileWithPath, scanFileDTO, callSpectralStorageWebservice, uploadScanFile_Submit_Response );
			
			UploadScanFile_Delete_For_ScanProcessStatusKey_Request uploadScanFile_Delete_For_ScanProcessStatusKey_Request = new UploadScanFile_Delete_For_ScanProcessStatusKey_Request();
			uploadScanFile_Delete_For_ScanProcessStatusKey_Request.setScanProcessStatusKey( uploadScanFile_Init_Response.getUploadScanFileTempKey() );
			
			UploadScanFile_Delete_For_ScanProcessStatusKey_Response uploadScanFile_Delete_For_ScanProcessStatusKey_Response =
					callSpectralStorageWebservice.call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice( uploadScanFile_Delete_For_ScanProcessStatusKey_Request );

			if ( ! uploadScanFile_Delete_For_ScanProcessStatusKey_Response.isStatusSuccess() ) {
				String msg2 = "Call to call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice(...) returned status NOT SUCCESS:";
				log.error( msg2 );
			}
			
		} catch ( Exception e ) {
			String msg = "Failed to send scan file to Spectral Storage";
			log.error( msg, e );
			throw e;
		}
	}

	/**
	 * @param scanFileWithPath
	 * @param scanFileDTO
	 * @param callSpectralStorageWebservice
	 * @return
	 * @throws ProxlImporterDataException
	 */
	private UploadScanFile_Init_Response spectralStorageService_InitUploadScanFileProcess( 
			File scanFileWithPath, 
			ScanFileDTO scanFileDTO,
			CallSpectralStorageAcceptImportWebservice callSpectralStorageWebservice ) throws Exception {

		UploadScanFile_Init_Response response = null;

		int retryCount = 0;

		while( true ) {  // use 'break;' inside loop to exit
			
			response = null; // reset to null for each iteration of loop

			retryCount++;

			if ( retryCount > SEND_FILE_RETRY_COUNT_MAX ) {
				String msg = "spectralStorageService_InitUploadScanFileProcess failed for retryCount > SEND_FILE_RETRY_COUNT. retryCount: " + retryCount 
						+ ", SEND_FILE_RETRY_COUNT_MAX: " + SEND_FILE_RETRY_COUNT_MAX
						+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
				log.error( msg );
				throw new ProxlImporterSpectralStorageServiceRetryExceededException(msg);
			}
			
			if ( retryCount > 1 ) {
				log.warn( "In spectralStorageService_InitUploadScanFileProcess(...) retryCount: " + retryCount + ", scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
				
			}
			
			UploadScanFile_Init_Request webserviceRequest = new UploadScanFile_Init_Request();
			
			try {
				//  Send scan file to Spectral Storage Service
				response = callSpectralStorageWebservice.call_UploadScanFile_Init_Webservice( webserviceRequest );

				if ( ! response.isStatusSuccess() ) {
					String msg = "UploadScanFile_Init: Failed to send scan file to Spectral Storage. response.isStatusSuccess() is false.  UploadScanFileTempKey: " 
							 + response.getUploadScanFileTempKey()
							 + ", Scan File: " + scanFileWithPath.getAbsolutePath();
					log.error( msg );
					throw new ProxlImporterSpectralStorageServiceErrorException(msg);
				}

				break;  //  EXIT LOOP
				
			} catch ( Exception e ) {

				if ( retryCount == SEND_FILE_RETRY_COUNT_MAX ) {
					String scanProcessStatusKeyResponsePart = ", response from Spectral Storage Service call interface is null (call may have thrown exception).";
					if ( response != null ) {
						scanProcessStatusKeyResponsePart = " UploadScanFileTempKey: " + response.getUploadScanFileTempKey();
					}
					
					String msg = "UploadScanFile_Init: Send Scan File to Spectral Storage Service send threw exception and failed for retryCount == SEND_FILE_RETRY_COUNT. " + scanProcessStatusKeyResponsePart
						+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
					log.error( msg, e );
					throw new ProxlImporterSpectralStorageServiceErrorException( msg, e );
				}
			}

			Thread.sleep( SEND_FILE_RETRY_DELAY ); // Sleep wait for retry
		}
		
		return response;
	}
	
	/**
	 * @param uploadScanFile_Init_Response
	 * @param scanFileWithPath
	 * @param scanFileDTO
	 * @param callSpectralStorageWebservice
	 * @return
	 * @throws ProxlImporterDataException
	 */
	private UploadScanFile_UploadScanFile_Response sendScanFileToSpectralStorageService_ActuallySendScanFile( 
			UploadScanFile_Init_Response uploadScanFile_Init_Response,
			File scanFileWithPath, 
			ScanFileDTO scanFileDTO,
			CallSpectralStorageAcceptImportWebservice callSpectralStorageWebservice ) throws Exception {

		UploadScanFile_UploadScanFile_Response response = null;

		int retryCount = 0;

		while( true ) {  // use 'break;' inside loop to exit
			
			response = null;

			retryCount++;

			if ( retryCount > SEND_FILE_RETRY_COUNT_MAX ) {
				String msg = "UploadSendScanFile: Send Scan File to Spectral Storage Service failed for retryCount > SEND_FILE_RETRY_COUNT_MAX. "
						+ " retryCount: " + retryCount
						+ ", SEND_FILE_RETRY_COUNT_MAX: " + SEND_FILE_RETRY_COUNT_MAX
						+ ", UploadScanFileTempKey: " + uploadScanFile_Init_Response.getUploadScanFileTempKey()
						+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
				log.error( msg );
				throw new ProxlImporterSpectralStorageServiceRetryExceededException(msg);
			}
			
			if ( retryCount > 1 ) {
				log.warn( "UploadSendScanFile: In sendScanFileToSpectralStorageService(...) retryCount: " + retryCount + ", scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
			}
			
			UploadScanFile_UploadScanFile_Request uploadScanFile_UploadScanFile_Request = new UploadScanFile_UploadScanFile_Request();
			uploadScanFile_UploadScanFile_Request.setUploadScanFileTempKey( uploadScanFile_Init_Response.getUploadScanFileTempKey() );
			uploadScanFile_UploadScanFile_Request.setScanFile( scanFileWithPath );
			
			try {
				//  Send scan file to Spectral Storage Service
				response = callSpectralStorageWebservice.call_UploadScanFile_UploadScanFile_Service( uploadScanFile_UploadScanFile_Request );

				if ( ! response.isStatusSuccess() ) {
					String msg = "UploadSendScanFile: Failed to Submit scan file to Spectral Storage. response.isStatusSuccess() is false.  UploadScanFileTempKey: " 
							 + uploadScanFile_Init_Response.getUploadScanFileTempKey()
							 + ", Scan File: " + scanFileWithPath.getAbsolutePath();
					log.error( msg );
					throw new ProxlImporterSpectralStorageServiceErrorException(msg);
				}

				break;  //  EXIT LOOP
				
			} catch ( Exception e ) {

				if ( retryCount == SEND_FILE_RETRY_COUNT_MAX ) {
					String scanProcessStatusKeyResponsePart = ", response from Spectral Storage Service call interface is null (call may have thrown exception).";
					if ( response != null ) {
						scanProcessStatusKeyResponsePart = " StatusSuccess: " + response.isStatusSuccess()
							+ ", UploadScanFileTempKey: " + uploadScanFile_Init_Response.getUploadScanFileTempKey();
					}
					
					String msg = "UploadSendScanFile: Send Scan File to Spectral Storage Service send threw exception and failed for retryCount == SEND_FILE_RETRY_COUNT. " + scanProcessStatusKeyResponsePart
						+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
					log.error( msg, e );
					throw new ProxlImporterSpectralStorageServiceErrorException( msg, e );
				}
			}

			Thread.sleep( SEND_FILE_RETRY_DELAY ); // Sleep wait for retry
		}
		
		return response;
	}

	/**
	 * @param uploadScanFile_Init_Response
	 * @param scanFileWithPath
	 * @param scanFileDTO
	 * @param callSpectralStorageAcceptImportWebservice
	 * @return
	 * @throws ProxlImporterDataException
	 */
	private UploadScanFile_AddScanFileFromFilenameAndPath_Response sendScanFilenameWithPathToSpectralStorageService( 
			UploadScanFile_Init_Response uploadScanFile_Init_Response,
			File scanFileWithPath, 
			CallSpectralStorageAcceptImportWebservice callSpectralStorageAcceptImportWebservice ) throws Exception {

		UploadScanFile_AddScanFileFromFilenameAndPath_Response response = null;
		
		boolean uploadScanFileTempKey_NotFound_ErrorResponse = false;

		int retryCount = 0;

		while( true ) {  // use 'break;' inside loop to exit

			retryCount++;

			if ( retryCount > SEND_FILE_RETRY_COUNT_MAX ) {
				String msg = "Send Scan Filename with Path to Spectral Storage Service. Actually send the filename with Path. In sendScanFilenameWithPathToSpectralStorageService():  failed for retryCount > SEND_FILE_RETRY_COUNT.  StatusSuccess: " + response.isStatusSuccess()
					+ ", UploadScanFileTempKey: " + uploadScanFile_Init_Response.getUploadScanFileTempKey()
					+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
				log.error( msg );
				throw new ProxlImporterSpectralStorageServiceRetryExceededException(msg);
			}
			
			if ( retryCount > 1 ) {
				log.warn( "In sendScanFilenameWithPathToSpectralStorageService(...) retryCount: " + retryCount + ", scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
				
			}
			

			UploadScanFile_AddScanFileFromFilenameAndPath_Request uploadScanFile_AddScanFileFromFilenameAndPath_Request = new UploadScanFile_AddScanFileFromFilenameAndPath_Request();
			uploadScanFile_AddScanFileFromFilenameAndPath_Request.setUploadScanFileTempKey( uploadScanFile_Init_Response.getUploadScanFileTempKey() );
			uploadScanFile_AddScanFileFromFilenameAndPath_Request.setFilenameWithPath( scanFileWithPath.getAbsolutePath() );
			uploadScanFile_AddScanFileFromFilenameAndPath_Request.setFileSize( BigInteger.valueOf( scanFileWithPath.length() ) );
			
			try {
				//  Send scan file to Spectral Storage Service
				response = callSpectralStorageAcceptImportWebservice.call_UploadScanFile_AddScanFileFromFilenameAndPath_Webservice( uploadScanFile_AddScanFileFromFilenameAndPath_Request );

				if ( ! response.isStatusSuccess() ) {

					//  Check in this order since if isUploadScanFileWithPath_FilePathsAllowedNotConfigured is true, isUploadScanFileWithPath_FilePathNotAllowed is also set to true

					if ( response.isUploadScanFileWithPath_FilePathsAllowedNotConfigured() ) {

						log.warn( "Send of Scan file with path to Spectral Storage Service rejected.  Will next send scan file contents" );
						log.warn( "  ... addnl info: Proxl Importer configured to send Scan file path to Spectral Storage Service but Spectral Storage Service not configured to accept Scan File Locations." );
						log.warn( "  ... addnl info: call sendScanFilenameWithPathToSpectralStorageService(...) returned statusSuccess False" );

						return response; // EARLY EXIT

					} else if ( response.isUploadScanFileWithPath_FilePathNotAllowed() ) {

						log.warn( "Send of Scan file with path to Spectral Storage Service rejected.  Will next send scan file contents" );
						log.warn( "  ... addnl info: Proxl Importer configured to send Scan file path to Spectral Storage Service but for this specific scan file, the Scan file path was not allowed.  Scan File with path (Java Get Canonical file with Path): "
								+ scanFileWithPath.getCanonicalPath() );
						log.warn( "  ... addnl info: call sendScanFilenameWithPathToSpectralStorageService(...) returned statusSuccess False" );

						return response; // EARLY EXIT
					}
					
					if ( response.isUploadScanFileTempKey_NotFound() ) {
						uploadScanFileTempKey_NotFound_ErrorResponse = true;
						String msg = "Send Scan File to Spectral Storage Service. Actually send the file. In sendScanFilenameWithPathToSpectralStorageService(): call_UploadScanFile_UploadScanFile_Service return UploadScanFileTempKey_NotFound true.  UploadScanFileTempKey: " 
								 + uploadScanFile_Init_Response.getUploadScanFileTempKey()
								 + ", Scan File: " + scanFileWithPath.getAbsolutePath();
						log.error( msg );
					}
					String msg = "Send Scan File to Spectral Storage Service. Actually send the file. In sendScanFilenameWithPathToSpectralStorageService(): call_UploadScanFile_UploadScanFile_Service return StatusSuccess false.  UploadScanFileTempKey: " 
							 + uploadScanFile_Init_Response.getUploadScanFileTempKey()
							 + ", Scan File: " + scanFileWithPath.getAbsolutePath();
					log.error( msg );
					throw new ProxlImporterSpectralStorageServiceErrorException(msg);
				}

				break;  //  EXIT LOOP
				
			} catch ( Exception e ) {

				if ( retryCount == SEND_FILE_RETRY_COUNT_MAX || uploadScanFileTempKey_NotFound_ErrorResponse ) {
					String scanProcessStatusKeyResponsePart = ", response from Spectral Storage Service call interface is null (call may have thrown exception).";
					if ( response != null ) {
						scanProcessStatusKeyResponsePart = " StatusSuccess: " + response.isStatusSuccess()
							+ ", UploadScanFileTempKey: " + uploadScanFile_Init_Response.getUploadScanFileTempKey();
					}
					
					String msg = "Send Scan File to Spectral Storage Service. Actually send the file. In sendScanFilenameWithPathToSpectralStorageService(): call_UploadScanFile_UploadScanFile_Service threw exception and failed for retryCount == SEND_FILE_RETRY_COUNT or uploadScanFileTempKey_NotFound_ErrorResponse is true. uploadScanFileTempKey_NotFound_ErrorResponse: " 
							+ uploadScanFileTempKey_NotFound_ErrorResponse
							+ ", scanProcessStatusKeyResponsePar: " + scanProcessStatusKeyResponsePart
							+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
					log.error( msg, e );
					throw new ProxlImporterSpectralStorageServiceErrorException( msg, e );
				}
			}

			Thread.sleep( SEND_FILE_RETRY_DELAY ); // Sleep wait for retry
		}
		
		return response;
	}
	


	/**
	 * @param uploadScanFile_Init_Response
	 * @param scanFileWithPath
	 * @param scanFileDTO
	 * @param callSpectralStorageWebservice
	 * @return
	 * @throws ProxlImporterDataException
	 */
	private UploadScanFile_Submit_Response submitScanFileToSpectralStorageService( 
			UploadScanFile_Init_Response uploadScanFile_Init_Response,
			File scanFileWithPath, 
			ScanFileDTO scanFileDTO,
			CallSpectralStorageAcceptImportWebservice callSpectralStorageWebservice ) throws Exception {

		UploadScanFile_Submit_Response response = null;

		int retryCount = 0;

		while( true ) {  // use 'break;' inside loop to exit

			retryCount++;

			if ( retryCount > SEND_FILE_RETRY_COUNT_MAX ) {
				String msg = "UploadScanFile_Submit: Send Scan File to Spectral Storage Service failed for retryCount > SEND_FILE_RETRY_COUNT.  StatusSuccess: " + response.isStatusSuccess()
					+ ", UploadScanFileTempKey: " + uploadScanFile_Init_Response.getUploadScanFileTempKey()
					+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
				log.error( msg );
				throw new ProxlImporterSpectralStorageServiceRetryExceededException(msg);
			}

			if ( retryCount > 1 ) {
				log.warn( "UploadScanFile_Submit: In submitScanFileToSpectralStorageService(...) retryCount: " + retryCount + ", scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
				
			}
			
			UploadScanFile_Submit_Request uploadScanFile_Submit_Request = new UploadScanFile_Submit_Request();
			uploadScanFile_Submit_Request.setUploadScanFileTempKey( uploadScanFile_Init_Response.getUploadScanFileTempKey() );
			
			try {
				//  Send scan file to Spectral Storage Service
				response = callSpectralStorageWebservice.call_UploadScanFile_Submit_Webservice( uploadScanFile_Submit_Request );

				if ( ! response.isStatusSuccess() ) {
					String msg = "UploadScanFile_Submit: Failed to send scan file to Spectral Storage: response.isStatusSuccess() is false. response.isUploadScanFileTempKey_NotFound(): " 
							+ response.isUploadScanFileTempKey_NotFound()
							+ ", response.isNoUploadedScanFile(): "
							+ response.isNoUploadedScanFile();
					log.error( msg );
					throw new ProxlImporterSpectralStorageServiceErrorException(msg);
				}

				ScanFileDAO.getInstance().updateSpectralStorageProcessKeyTemp( response.getScanProcessStatusKey(), scanFileDTO.getId() );
				
				break;  //  EXIT LOOP
				
			} catch ( Exception e ) {

				if ( retryCount == SEND_FILE_RETRY_COUNT_MAX ) {
					String scanProcessStatusKeyResponsePart = ", response from Spectral Storage Service call interface is null (call may have thrown exception).";
					if ( response != null ) {
						scanProcessStatusKeyResponsePart = " ScanProcessStatusKey: " + response.getScanProcessStatusKey();
					}
					
					String msg = "UploadScanFile_Submit: Send Scan File to Spectral Storage Service send threw exception and failed for retryCount == SEND_FILE_RETRY_COUNT. " + scanProcessStatusKeyResponsePart
						+ ", Scan File: " + scanFileWithPath.getAbsolutePath()
						+ "\n Exception Caught toString: " + e.toString();
					log.error( msg, e );
					throw new ProxlImporterSpectralStorageServiceErrorException( msg, e );
				}
			}

			Thread.sleep( SEND_FILE_RETRY_DELAY ); // Sleep wait for retry
		}
		
		return response;
	}
	
	/**
	 * @param scanFileWithPath
	 * @param scanFileDTO
	 * @param callSpectralStorageWebservice
	 * @param response
	 * @throws Exception
	 */
	private void getSpectralServiceAPI_AndUpdateScanFileTable( 
			File scanFileWithPath, ScanFileDTO scanFileDTO,
			CallSpectralStorageAcceptImportWebservice callSpectralStorageWebservice, 
			UploadScanFile_Submit_Response response ) throws  Exception {

		//  Try with progressively longer delays and different retry count max

		//  Wait for Spectral Storage API Key - TODO  Change to get the API Key later in processing but will need to pass ScanFileDTO out to main processing to do that
		
		String spectralStorageAPIKey = null;
		
		int retryCount = 0;
		
		while( true ) {  // use 'break;' inside loop to exit

			if ( retryCount > 0 ) {
				// Sleep before retry
				if ( retryCount < GET_API_KEY_RETRY_COUNT_MAX_STEP_1 ) {
					Thread.sleep( GET_API_KEY_RETRY_DELAY_STEP_1 ); // Sleep wait for retry
				} else {
					Thread.sleep( GET_API_KEY_RETRY_DELAY_STEP_LAST ); // Sleep wait for retry
				}
			}

			retryCount++;

			if ( retryCount > 1 ) {
				log.warn( "In getSpectralServiceAPI_AndUpdateScanFileTable(...) retryCount: " + retryCount + ", scanFileWithPath: " + scanFileWithPath.getAbsolutePath() );
				
			}
			

			if ( retryCount > GET_API_KEY_RETRY_COUNT_MAX_TOTAL_ALLOWED ) {
				String msg = "Waited too long for Spectral Storage System to process Scan file.  ScanProcessStatusKey: " + response.getScanProcessStatusKey()
					+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
				log.error( msg );
				throw new ProxlImporterSpectralStorageServiceRetryExceededException(msg);
			}
			
			Get_UploadedScanFileInfo_Response get_UploadedScanFileInfo_Response = null;

			try {
				Get_UploadedScanFileInfo_Request webserviceRequest = new Get_UploadedScanFileInfo_Request();
				webserviceRequest.setScanProcessStatusKey( response.getScanProcessStatusKey() );

				get_UploadedScanFileInfo_Response =
						callSpectralStorageWebservice.call_Get_UploadedScanFileInfo_Webservice( webserviceRequest );

			} catch ( Exception e ) {

				{
					String msg = "getSpectralServiceAPI_AndUpdateScanFileTable: Get API Key from Spectral Storage Service failed "
							+ " and send threw exception  . retryCount: " + retryCount + ", ScanProcessStatusKey: " + response.getScanProcessStatusKey()
							+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
					log.error( msg, e );
				}

				if ( retryCount == GET_API_KEY_RETRY_COUNT_MAX_TOTAL_ALLOWED ) {
					String msg = "Get API Key from Spectral Storage Service failed for retryCount == " + GET_API_KEY_RETRY_COUNT_MAX_TOTAL_ALLOWED + " (GET_API_KEY_RETRY_COUNT_MAX_TOTAL_ALLOWED)"
							+ " and send threw exception  .  ScanProcessStatusKey: " + response.getScanProcessStatusKey()
						+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
					log.error( msg, e );
					throw new ProxlImporterSpectralStorageServiceErrorException( msg, e );
				}
				
				continue;  //  EARLY LOOP CONTINUE
			}

			if ( get_UploadedScanFileInfo_Response.isScanProcessStatusKey_NotFound() ) {
				String msg = "Error in processing since ScanProcessStatusKey is not in Spectral Storage System.  ScanProcessStatusKey: " + response.getScanProcessStatusKey()
				+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
				log.error( msg );
				throw new ProxlImporterSpectralStorageServiceErrorException(msg);
			}

			if ( get_UploadedScanFileInfo_Response.getStatus() == WebserviceSpectralStorageAcceptImport_ProcessStatusEnum.FAIL ) {
				String msg = "Spectral Storage System Failed to process Scan file.  ScanProcessStatusKey: " + response.getScanProcessStatusKey()
				+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
				log.error( msg );
				
				try {

					UploadScanFile_Delete_For_ScanProcessStatusKey_Request webserviceRequest = new UploadScanFile_Delete_For_ScanProcessStatusKey_Request();
					webserviceRequest.setScanProcessStatusKey( response.getScanProcessStatusKey() );
					
					UploadScanFile_Delete_For_ScanProcessStatusKey_Response uploadScanFile_Delete_For_ScanProcessStatusKey_Response =
							callSpectralStorageWebservice.call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice( webserviceRequest );
					
					if ( ! uploadScanFile_Delete_For_ScanProcessStatusKey_Response.isStatusSuccess() ) {
						String msg2 = "Call to call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice(...) returned status fail:";
						log.error( msg2 );
					}
				} catch ( Exception e ) {
					String msg2 = "Call to call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice(...) threw exception:";
					log.error( msg2, e );
				}
				
				throw new ProxlImporterSpectralStorageServiceErrorException(msg);
			}

			{
				String msg = "INFO: getSpectralServiceAPI_AndUpdateScanFileTable: Get API Key from Spectral Storage Service "
						+ " Status: " + get_UploadedScanFileInfo_Response.getStatus() + ", retryCount: " + retryCount + ", ScanProcessStatusKey: " + response.getScanProcessStatusKey()
						+ ", Scan File: " + scanFileWithPath.getAbsolutePath();
				log.warn( msg );
			}

			if ( get_UploadedScanFileInfo_Response.getStatus() == WebserviceSpectralStorageAcceptImport_ProcessStatusEnum.SUCCESS ) {
				
				spectralStorageAPIKey = get_UploadedScanFileInfo_Response.getScanFileAPIKey();
				
				{
					String msg = "INFO: getSpectralServiceAPI_AndUpdateScanFileTable: !!  Spectral Storage Service has finished processing Scan File. Spectral Storage API Key : "
							+ spectralStorageAPIKey;
					log.warn( msg );
				}
				
				break;  //  LOOP EXIT
			}
			
		}
		
		String spectralStorageAPIKeyInDB = ScanFileDAO.getInstance().getSpectralStorageAPIKeyById( scanFileDTO.getId() );
		
		if ( StringUtils.isNotEmpty( spectralStorageAPIKeyInDB ) ) {
			// populated so must match
		
			if ( ! spectralStorageAPIKeyInDB.equals( spectralStorageAPIKey ) ) {
				String msg = "Spectral Storage System return API Key does not match API Key in Proxl DB.  ScanProcessStatusKey: " + response.getScanProcessStatusKey()
				+ ", Scan File: " + scanFileWithPath.getAbsolutePath()
				+ ", API Key in Proxl DB: " + spectralStorageAPIKeyInDB
				+ ", API Key from Spectral Storage System: " + spectralStorageAPIKey;
				log.error( msg );
				throw new ProxlImporterSpectralStorageServiceErrorException(msg);
			}
			
		} else {
			// store new API Key value in DB
			ScanFileDAO.getInstance().updateSpectralStorageAPIKey( spectralStorageAPIKey, scanFileDTO.getId() );
		}

		try {

			UploadScanFile_Delete_For_ScanProcessStatusKey_Request webserviceRequest = new UploadScanFile_Delete_For_ScanProcessStatusKey_Request();
			webserviceRequest.setScanProcessStatusKey( response.getScanProcessStatusKey() );
			
			UploadScanFile_Delete_For_ScanProcessStatusKey_Response uploadScanFile_Delete_For_ScanProcessStatusKey_Response =
					callSpectralStorageWebservice.call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice( webserviceRequest );
			
			if ( ! uploadScanFile_Delete_For_ScanProcessStatusKey_Response.isStatusSuccess() ) {
				String msg2 = "Call to call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice(...) returned status fail:";
				log.error( msg2 );
			}
		} catch ( Exception e ) {
			String msg2 = "Call to call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice(...) threw exception:";
			log.error( msg2, e );
		}
		
	} 
	
}
