package org.yeastrc.proxl.import_xml_to_db_runner_pgm.process_import;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.database_update_with_transaction_services.UpdateTrackingTrackingRunRecordsDBTransaction;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemTableGetValueCommon;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTrackingRun_Base_DAO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTracking_Base_DAO;
import org.yeastrc.proxl.import_xml_to_db.file_import_proxl_xml_scans.run_importer_to_importer_file_data.RunImporterToImporterFileRoot;
import org.yeastrc.proxl.import_xml_to_db.file_import_proxl_xml_scans.run_importer_to_importer_file_data.RunImporterToImporterParameterNamesConstants;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.config.ImporterRunnerConfigData;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.constants.RunImporterCommandConstants;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.constants.RunImporterToImporterFilenameConstants;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.delete_directory_and_contents.DeleteDirectoryAndContents;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.on_import_finish.OnImprtFnshCllWbSrvc;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.run_system_command.RunSystemCommand;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.run_system_command.RunSystemCommandResponse;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadCommonConstants;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportRunSubStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.objects.TrackingDTOTrackingRunDTOPair;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.utils.Proxl_XML_ImporterWrkDirAndSbDrsCmmn;

/**
 * 
 *
 */
public class ProcessProxlXMLImport {
	
	private static final String ERROR_MSG_SYSTEM_ERROR = "System Error";
	
	private static final int RETRY_DELAY_INITIAL = 1;
	private static final int RETRY_DELAY_EXTENDED_1 = 20;
	private static final int RETRY_DELAY_EXTENDED_2 = 90;
	private static final int RETRY_COUNT_SWITCH_TO_EXTENDED_1 = 5;
	private static final int RETRY_COUNT_SWITCH_TO_EXTENDED_2 = 10;
	
	private static final Logger log = Logger.getLogger(ProcessProxlXMLImport.class);
	
	//  private constructor
	private ProcessProxlXMLImport() { }
	/**
	 * @return newly created instance
	 */
	public static ProcessProxlXMLImport getInstance() { 
		return new ProcessProxlXMLImport(); 
	}
	
	private volatile boolean shutdownRequested = false;
	private volatile RunSystemCommand runSystemCommand;
	
	/**
	 * awaken thread to allow shutdown
	 */
	public void awaken() {
		log.error( "awaken() called.");
		synchronized (this) {
			notify();
		}
	}
	

	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {
		log.error( "shutdown() called. Calling runSystemCommand.shutdown() then calling awaken()");
		shutdownRequested = true;
		try {
			if ( runSystemCommand != null ) {
				runSystemCommand.shutdown();
			}
		} catch ( NullPointerException e ) {
			//  Eat the NullPointerException since that meant that nothing had to be done.
		}
		log.error( "shutdown() called. Called runSystemCommand.shutdown() Now calling awaken()");
		awaken();
	}
	
	/**
	 * @param trackingDTOTrackingRunDTOPair
	 * @throws Exception 
	 */
	public void processProxlXMLImport( TrackingDTOTrackingRunDTOPair trackingDTOTrackingRunDTOPair ) throws Exception {
		
		ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO = trackingDTOTrackingRunDTOPair.getProxlXMLFileImportTrackingDTO();
		if ( log.isInfoEnabled() ) {
			log.info( "Processing import for tracking id: " + proxlXMLFileImportTrackingDTO.getId() );
		}
		ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO = trackingDTOTrackingRunDTOPair.getProxlXMLFileImportTrackingRunDTO();
		File proxl_XML_Importer_Work_Directory =
				Proxl_XML_ImporterWrkDirAndSbDrsCmmn.getInstance().get_Proxl_XML_Importer_Work_Directory();
		File importerBaseDir = new File( proxl_XML_Importer_Work_Directory, ProxlXMLFileUploadCommonConstants.IMPORT_BASE_DIR );
		String subdirNameForThisTrackingId =
				Proxl_XML_ImporterWrkDirAndSbDrsCmmn.getInstance().getDirForImportTrackingId( proxlXMLFileImportTrackingDTO.getId() );
		File subdirForThisTrackingId = new File( importerBaseDir, subdirNameForThisTrackingId );
		if ( ! subdirForThisTrackingId.exists() ) {
			String msg = "subdirForThisTrackingId does not exist: " + subdirForThisTrackingId.getCanonicalPath();
			log.error( msg );
			
			markImportTrackingAsFailed( proxlXMLFileImportTrackingDTO, proxlXMLFileImportTrackingRunDTO, ProxlXMLFileImportRunSubStatus.SYSTEM_ERROR );
			
			throw new ProxlImporterInteralException(msg);
		}
		final String importJarWithPath = ImporterRunnerConfigData.getImporterJarWithPath();
		//  Expecting absolute path
		File importJarWithPathFileAloneObj = new File( importJarWithPath );
		File importJarWithPathFileRelativeToTrackingDirObj = new File( subdirForThisTrackingId, importJarWithPath );
		if ( ( ! importJarWithPathFileAloneObj.exists() ) 
				&& ( ! importJarWithPathFileRelativeToTrackingDirObj.exists() ) ) {
			String errorMsg = "Import Jar with Path is not found, "
					+ "using the subdirectory that the command will be run in as the starting point."
					+ " Import Jar with Path in config file: "
					+ importJarWithPath;
			log.error( errorMsg ) ;
			
			markImportTrackingAsFailed( proxlXMLFileImportTrackingDTO, proxlXMLFileImportTrackingRunDTO, ProxlXMLFileImportRunSubStatus.SYSTEM_ERROR);
			
			throw new ProxlImporterInteralException(errorMsg);
		}
		//   Create a params file that is passed to the importer
		String runImporterParamsFilename = 
				RunImporterToImporterFilenameConstants.RUN_IMPORTER_TO_IMPORTER_FILENAME_PREFIX
				+ proxlXMLFileImportTrackingRunDTO.getId() // Include run id as part of filename
				+ RunImporterToImporterFilenameConstants.RUN_IMPORTER_TO_IMPORTER_FILENAME_SUFFFIX;
		String importerOutputDataErrorsFilename = 
				RunImporterToImporterFilenameConstants.IMPORTER_OUTPUT_DATA_ERRORS_FILENAME_PREFIX
				+ proxlXMLFileImportTrackingRunDTO.getId() // Include run id as part of filename
				+ RunImporterToImporterFilenameConstants.IMPORTER_OUTPUT_DATA_ERRORS_FILENAME_SUFFFIX;
		RunImporterToImporterFileRoot runImporterToImporterFileRoot = new RunImporterToImporterFileRoot();
		runImporterToImporterFileRoot.setImportTrackingRunId( proxlXMLFileImportTrackingRunDTO.getId() );
		runImporterToImporterFileRoot.setProjectId( proxlXMLFileImportTrackingDTO.getProjectId() );
		runImporterToImporterFileRoot.setOutputDataErrorsFileName( importerOutputDataErrorsFilename );
//		runImporterToImporterFileRoot.setSystemInStringForShutdown( StringSendImporterToRequestShutdownConstants.SHUTDOWN );
		runImporterToImporterFileRoot.setSkipPopulatingPathOnSearch( true );
		//  Marshal (write) the object to the file
		JAXBContext jaxbContext = JAXBContext.newInstance( RunImporterToImporterFileRoot.class );
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		File runImporterParamsFile = new File( subdirForThisTrackingId, runImporterParamsFilename );
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream( runImporterParamsFile );
			marshaller.marshal( runImporterToImporterFileRoot, outputStream );
		} catch ( Exception e ) {
			throw e;
		} finally {
			if ( outputStream != null ) {
				outputStream.close();
			}
		}
		String javaCommand = "java";
		if ( StringUtils.isNotEmpty( ImporterRunnerConfigData.getJavaExecutableWithPath() ) ) {
			javaCommand = ImporterRunnerConfigData.getJavaExecutableWithPath();
		}
		
		List<String> commandAndItsArgumentsAsList = new ArrayList<>( 20 );
		commandAndItsArgumentsAsList.add( javaCommand );
		
		if ( ImporterRunnerConfigData.getJavaExecutableParameters() != null && ( ! ImporterRunnerConfigData.getJavaExecutableParameters().isEmpty() ) ) {
			for ( String javaExecutableParameter : ImporterRunnerConfigData.getJavaExecutableParameters() ) {
				commandAndItsArgumentsAsList.add( javaExecutableParameter );
			}
		}
		
		commandAndItsArgumentsAsList.add(  "-jar" );
		commandAndItsArgumentsAsList.add( importJarWithPath );
//		commandAndItsArgumentsAsList.add(  "--debug" );  //  TODO  TEMP
		commandAndItsArgumentsAsList.add( 
				"--" + RunImporterToImporterParameterNamesConstants.RUN_IMPORTER_PARAMS_FILE_PARAM_STRING 
				+ "=" + runImporterParamsFilename );
		commandAndItsArgumentsAsList.add( 
				"--" + RunImporterToImporterParameterNamesConstants.RUN_IMPORTER_PARAMS_CREATE_FILE_ON_SUCCESS_STRING
				+ "=" + RunImporterToImporterParameterNamesConstants.RUN_IMPORTER_PARAM_VALUE_CREATE_FILE_ON_SUCCESS_STRING );
		if ( ImporterRunnerConfigData.getImporterDbConfigWithPath() != null ) {
			commandAndItsArgumentsAsList.add( "-c" );
			commandAndItsArgumentsAsList.add( ImporterRunnerConfigData.getImporterDbConfigWithPath() );
		}
		String filenameToWriteSysoutTo = 
				RunImporterCommandConstants.RUN_IMPORTER_COMMAND_SYSOUT_WRITTEN_TO_FILENAME_PREFIX
				+ proxlXMLFileImportTrackingRunDTO.getId() // Include run id as part of filename
				+ RunImporterCommandConstants.RUN_IMPORTER_COMMAND_SYSOUT_WRITTEN_TO_FILENAME_SUFFFIX;
		String filenameToWriteSyserrTo = 
				RunImporterCommandConstants.RUN_IMPORTER_COMMAND_SYSERR_WRITTEN_TO_FILENAME_PREFIX
				+ proxlXMLFileImportTrackingRunDTO.getId() // Include run id as part of filename
				+ RunImporterCommandConstants.RUN_IMPORTER_COMMAND_SYSERR_WRITTEN_TO_FILENAME_SUFFFIX;
		File fileToWriteSysoutTo = new File( subdirForThisTrackingId, filenameToWriteSysoutTo );
		File fileToWriteSyserrTo = new File( subdirForThisTrackingId, filenameToWriteSyserrTo );
		runSystemCommand = RunSystemCommand.getInstance();
		try {
			RunSystemCommandResponse runSystemCommandResponse = 
					runSystemCommand.runCmd( 
							commandAndItsArgumentsAsList, 
							subdirForThisTrackingId /* dirToRunCommandIn*/, 
							fileToWriteSysoutTo /* fileToWriteSysoutTo*/,
							fileToWriteSyserrTo /* fileToWriteSyserrTo*/,
							false /* throwExceptionOnCommandFailure */ );
			if ( runSystemCommandResponse.isShutdownRequested() ) {
				log.warn( "command was aborted for run importer program shutdown: " + commandAndItsArgumentsAsList
						+ ", subdirForThisTrackingId:  " + subdirForThisTrackingId.getCanonicalPath() );
				 proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.RE_QUEUED );
				 UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
				 .updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
						 ProxlXMLFileImportStatus.RE_QUEUED, 
						 proxlXMLFileImportTrackingDTO.getId(), 
						 proxlXMLFileImportTrackingRunDTO );
			} else {
				if ( ! runSystemCommandResponse.isCommandSuccessful() ) {
					log.error( "command failed: exit code: "
							+ runSystemCommandResponse.getCommandExitCode()
							+ ", command: "
							+ commandAndItsArgumentsAsList
							+ ", subdirForThisTrackingId:  " + subdirForThisTrackingId.getCanonicalPath() );
				}
				//  If importer did not update the status in DB, set to failed
				ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO_AfterImporterRun =
						ProxlXMLFileImportTracking_Base_DAO.getInstance()
						.getItem( proxlXMLFileImportTrackingDTO.getId() );
				if ( proxlXMLFileImportTrackingDTO_AfterImporterRun.getStatus()
						== ProxlXMLFileImportStatus.STARTED ) {
					//  Status left as started so change to failed
					log.error( "command failed: exit code: "
							+ runSystemCommandResponse.getCommandExitCode()
							+ ", importer left status as 'started' so changing to 'failed' "
							+ ", command: "
							+ commandAndItsArgumentsAsList
							+ ", subdirForThisTrackingId:  " + subdirForThisTrackingId.getCanonicalPath() );
					
					markImportTrackingAsFailed( proxlXMLFileImportTrackingDTO_AfterImporterRun, proxlXMLFileImportTrackingRunDTO, ProxlXMLFileImportRunSubStatus.SYSTEM_ERROR );
				}				
			}
		} catch (Throwable e) {
			log.error( "command failed: " + commandAndItsArgumentsAsList
					+ ", subdirForThisTrackingId:  " + subdirForThisTrackingId.getCanonicalPath() );

			markImportTrackingAsFailed( proxlXMLFileImportTrackingDTO, proxlXMLFileImportTrackingRunDTO, ProxlXMLFileImportRunSubStatus.SYSTEM_ERROR );

			proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.FAILED );
			proxlXMLFileImportTrackingRunDTO.setDataErrorText( ERROR_MSG_SYSTEM_ERROR );
			proxlXMLFileImportTrackingRunDTO.setImportResultText( ERROR_MSG_SYSTEM_ERROR );
			UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
			.updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
					ProxlXMLFileImportStatus.FAILED, 
					proxlXMLFileImportTrackingDTO.getId(), 
					proxlXMLFileImportTrackingRunDTO );
			throw new Exception( e );
		} finally {
			runSystemCommand = null;
			OnImprtFnshCllWbSrvc.getInstance()
			.callProxlWebServiceOnSingleImportFinish( 
					proxlXMLFileImportTrackingDTO.getId(), 
					proxlXMLFileImportTrackingRunDTO.getId() );
		}
		
		final String commandToRunOnSuccessfulImport = ImporterRunnerConfigData.getCommandToRunOnSuccessfulImport();
		if ( StringUtils.isNotEmpty( commandToRunOnSuccessfulImport ) ) {
			// Run command on successful import is configured, so run it

			//  Get TrackingRunDTO After Importer Run to get inserted search id
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO_AfterImporterRun = 
					ProxlXMLFileImportTrackingRun_Base_DAO.getInstance()
					.getItem( proxlXMLFileImportTrackingRunDTO.getId() );
			
			if ( ProxlXMLFileImportStatus.COMPLETE.equals( proxlXMLFileImportTrackingRunDTO_AfterImporterRun.getRunStatus() ) ) {

				if ( proxlXMLFileImportTrackingRunDTO_AfterImporterRun.getInsertedSearchId() == null ) {
					String msg = "proxlXMLFileImportTrackingRunDTO_AfterImporterRun.getInsertedSearchId() == null "
							+ " for proxlXMLFileImportTrackingRunDTO_AfterImporterRun.getId(): " 
							+ proxlXMLFileImportTrackingRunDTO_AfterImporterRun.getId();
					log.error( msg );
					throw new Exception(msg);
				}
				File dirToWriteSysoutSyserrTo = subdirForThisTrackingId;
				String commandToRunOnSuccessfulImportSyoutSyserrDirString = ImporterRunnerConfigData.getCommandToRunOnSuccessfulImportSyoutSyserrDir();
				if ( StringUtils.isNotEmpty( commandToRunOnSuccessfulImportSyoutSyserrDirString ) ) {
					File commandToRunOnSuccessfulImportSyoutSyserrDir = new File( commandToRunOnSuccessfulImportSyoutSyserrDirString );
					if ( commandToRunOnSuccessfulImportSyoutSyserrDir.exists() ) {
						dirToWriteSysoutSyserrTo = commandToRunOnSuccessfulImportSyoutSyserrDir;
					}
				}

				List<String> commandAndItsArgumentsAsListRunOnSuccess = new ArrayList<>( 20 );
				commandAndItsArgumentsAsListRunOnSuccess.add( commandToRunOnSuccessfulImport );
				commandAndItsArgumentsAsListRunOnSuccess.add( 
						Integer.toString( proxlXMLFileImportTrackingRunDTO_AfterImporterRun.getInsertedSearchId() ) );

				File fileToWriteSysoutToRunOnSuccess = new File( dirToWriteSysoutSyserrTo, "commandRunOnSuccessSysout.txt" );
				File fileToWriteSyserrToRunOnSuccess = new File( dirToWriteSysoutSyserrTo, "commandRunOnSuccessSyserr.txt" );

				RunSystemCommand runSystemCommand = RunSystemCommand.getInstance();
				try {
					RunSystemCommandResponse runSystemCommandResponse = 
							runSystemCommand.runCmd( 
									commandAndItsArgumentsAsListRunOnSuccess, 
									subdirForThisTrackingId /* dirToRunCommandIn*/, 
									fileToWriteSysoutToRunOnSuccess /* fileToWriteSysoutTo*/,
									fileToWriteSyserrToRunOnSuccess /* fileToWriteSyserrTo*/,
									false /* throwExceptionOnCommandFailure */ );

				} catch ( Throwable e ) {

					log.error( "Failed running commandToRunOnSuccessfulImport: " + commandToRunOnSuccessfulImport
							+ ", subdirForThisTrackingId: " + subdirForThisTrackingId, e );

				} finally {

				}
			}			
		}
		
		deleteUploadedFilesIfConfiguredAndStatusSuccess( proxlXMLFileImportTrackingDTO, subdirForThisTrackingId );
	}
	
	/**
	 * @param proxlXMLFileImportTrackingDTO
	 * @param proxlXMLFileImportTrackingRunDTO
	 * @throws Exception
	 */
	public void markImportTrackingAsFailed(
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO,
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO,
			ProxlXMLFileImportRunSubStatus proxlXMLFileImportRunSubStatus ) throws Exception {
		
		proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.FAILED );
		proxlXMLFileImportTrackingRunDTO.setRunSubStatus( proxlXMLFileImportRunSubStatus );
		proxlXMLFileImportTrackingRunDTO.setDataErrorText( ERROR_MSG_SYSTEM_ERROR );
		proxlXMLFileImportTrackingRunDTO.setImportResultText( ERROR_MSG_SYSTEM_ERROR );
		
		boolean updatedDBCompleted = false;
		int retryCount = 0;
		int nextTryInMinutes = 0;
		
		while ( ! updatedDBCompleted && ! shutdownRequested ) {
		
			try {
				UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
				.updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
						ProxlXMLFileImportStatus.FAILED, 
						proxlXMLFileImportTrackingDTO.getId(), 
						proxlXMLFileImportTrackingRunDTO );
				updatedDBCompleted = true;
				
			} catch ( Exception e ) {
				
				retryCount++;

				if ( retryCount > RETRY_COUNT_SWITCH_TO_EXTENDED_2 ) {
					nextTryInMinutes +=  RETRY_DELAY_EXTENDED_2;
				} else if ( retryCount > RETRY_COUNT_SWITCH_TO_EXTENDED_1 ) {
					nextTryInMinutes +=  RETRY_DELAY_EXTENDED_1;
				} else {
					nextTryInMinutes +=  RETRY_DELAY_INITIAL;
				}
				
				String msg = "Failed to update Import Tracking status to Failed for tracking id: " + proxlXMLFileImportTrackingDTO.getId()
					+ ", run id: " + proxlXMLFileImportTrackingRunDTO.getId()
					+ ".  Will retry to update again in " + nextTryInMinutes + " minute(s).";
				log.error( msg );
				
				int waitTimeInSeconds = nextTryInMinutes * 60;
				synchronized (this) {
					try {
						wait( ( (long) waitTimeInSeconds ) * 1000 ); //  wait for notify() call or timeout, in milliseconds
					} catch (InterruptedException ie) {
						log.info("waitForSleepTime():  wait() interrupted with InterruptedException");
					}
				}

			}
		}
		

		if ( ! updatedDBCompleted && shutdownRequested ) {
			
			String msg = "Shutdown requested.  Update DB For Fail of import was not completed. tracking id: " + proxlXMLFileImportTrackingDTO.getId()
					+ ", run id: " + proxlXMLFileImportTrackingRunDTO.getId()
					+ ". ";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		

		boolean callServerCompleted = false;
		retryCount = 0;
		nextTryInMinutes = 0;

		while ( ! callServerCompleted && ! shutdownRequested ) {
		
			try {
				OnImprtFnshCllWbSrvc.getInstance()
				.callProxlWebServiceOnSingleImportFinish( 
						proxlXMLFileImportTrackingDTO.getId(), 
						proxlXMLFileImportTrackingRunDTO.getId() );
				callServerCompleted = true;

			} catch ( Exception e ) {
				
				retryCount++;

				if ( retryCount > RETRY_COUNT_SWITCH_TO_EXTENDED_2 ) {
					
					String msg = "Failed to call Proxl Web app for Failed status for tracking id: " + proxlXMLFileImportTrackingDTO.getId()
					+ ", run id: " + proxlXMLFileImportTrackingRunDTO.getId()
					+ ".  Retry count exceeded so no more retries will be attempted.";
					log.error( msg );
					throw new ProxlImporterInteralException(msg);
					
				} else if ( retryCount > RETRY_COUNT_SWITCH_TO_EXTENDED_1 ) {
					nextTryInMinutes +=  RETRY_DELAY_EXTENDED_1;
				} else {
					nextTryInMinutes +=  RETRY_DELAY_INITIAL;
				}
				
				String msg = "Failed to call Proxl Web app for Failed status for tracking id: " + proxlXMLFileImportTrackingDTO.getId()
					+ ", run id: " + proxlXMLFileImportTrackingRunDTO.getId()
					+ ".  Will retry to update again in " + nextTryInMinutes + " minute(s).";
				log.error( msg );
				
				int waitTimeInSeconds = nextTryInMinutes * 60;
				synchronized (this) {
					try {
						wait( ( (long) waitTimeInSeconds ) * 1000 ); //  wait for notify() call or timeout, in milliseconds
					} catch (InterruptedException ie) {
						log.info("waitForSleepTime():  wait() interrupted with InterruptedException");
					}
				}

			}
		}
		

		if ( ! callServerCompleted && shutdownRequested ) {
			
			String msg = "Shutdown requested.  Call to Proxl Web app For Fail of import was not completed. tracking id: " + proxlXMLFileImportTrackingDTO.getId()
					+ ", run id: " + proxlXMLFileImportTrackingRunDTO.getId()
					+ ". ";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		
	}
	
	/**
	 * @param proxlXMLFileImportTrackingDTO
	 * @throws Exception
	 */
	private void deleteUploadedFilesIfConfiguredAndStatusSuccess( ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO, File subdirForThisTrackingId ) throws Exception {
		
		int trackingId = proxlXMLFileImportTrackingDTO.getId();
		//  Get current record for ProxlXMLFileImportTrackingDTO since the importer pgm may have updated it.
		ProxlXMLFileImportTrackingDTO currentTrackingDTO = ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( trackingId );
		if ( currentTrackingDTO.getStatus() != ProxlXMLFileImportStatus.COMPLETE ) {
			//  Status is not COMPLETE so NO DELETION
			return;  //  EARLY EXIT
		}
		// Get configuration item
		try {
			String deleteFilesConfigValue =
					ConfigSystemTableGetValueCommon.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.IMPORT_DELETE_UPLOADED_FILES );
			if ( ! ConfigSystemsValuesSharedConstants.TRUE.equals( deleteFilesConfigValue ) ) {
				//  Config value in table is not true string.
				return;  //  EARLY EXIT
			}
		} catch ( IllegalStateException e ) {
			//  Config key not in table.  Assume don't want files deleted
			return;  //  EARLY EXIT
		}
		DeleteDirectoryAndContents.getInstance().deleteDirectoryAndContents( subdirForThisTrackingId );
	}
	
}
