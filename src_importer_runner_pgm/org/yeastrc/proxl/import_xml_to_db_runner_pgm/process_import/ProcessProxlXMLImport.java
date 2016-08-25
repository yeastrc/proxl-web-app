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
import org.yeastrc.xlink.base.proxl_xml_file_import.dao.ProxlXMLFileImportTracking_Base_DAO;
import org.yeastrc.proxl.import_xml_to_db.proxl_xml_file_import.run_importer_to_importer_file_data.RunImporterToImporterFileRoot;
import org.yeastrc.proxl.import_xml_to_db.proxl_xml_file_import.run_importer_to_importer_file_data.RunImporterToImporterParameterNamesConstants;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.config.ImporterRunnerConfigData;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.constants.RunImporterCommandConstants;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.constants.RunImporterToImporterFilenameConstants;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.run_system_command.RunSystemCommand;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.run_system_command.RunSystemCommandResponse;
import org.yeastrc.xlink.base.proxl_xml_file_import.constants.ProxlXMLFileUploadCommonConstants;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportRunSubStatus;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.base.proxl_xml_file_import.objects.TrackingDTOTrackingRunDTOPair;
import org.yeastrc.xlink.base.proxl_xml_file_import.utils.Proxl_XML_ImporterWrkDirAndSbDrsCmmn;




/**
 * 
 *
 */
public class ProcessProxlXMLImport {

	
	private static final String ERROR_MSG_SYSTEM_ERROR = "System Error";


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
			
			proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.FAILED );
			
			proxlXMLFileImportTrackingRunDTO.setRunSubStatus( ProxlXMLFileImportRunSubStatus.SYSTEM_ERROR );

			proxlXMLFileImportTrackingRunDTO.setDataErrorText( ERROR_MSG_SYSTEM_ERROR );
			proxlXMLFileImportTrackingRunDTO.setImportResultText( ERROR_MSG_SYSTEM_ERROR );


			UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
			.updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
					ProxlXMLFileImportStatus.FAILED, 
					proxlXMLFileImportTrackingDTO.getId(), 
					proxlXMLFileImportTrackingRunDTO );
			
			
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

			proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.FAILED );
			
			proxlXMLFileImportTrackingRunDTO.setRunSubStatus( ProxlXMLFileImportRunSubStatus.SYSTEM_ERROR );

			proxlXMLFileImportTrackingRunDTO.setDataErrorText( ERROR_MSG_SYSTEM_ERROR );
			proxlXMLFileImportTrackingRunDTO.setImportResultText( ERROR_MSG_SYSTEM_ERROR );

			UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
			.updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
					ProxlXMLFileImportStatus.FAILED, 
					proxlXMLFileImportTrackingDTO.getId(), 
					proxlXMLFileImportTrackingRunDTO );
			
			
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
		
		commandAndItsArgumentsAsList.add(  "-jar" );
		commandAndItsArgumentsAsList.add( importJarWithPath );
		
		
//		commandAndItsArgumentsAsList.add(  "--debug" );  //  TODO  TEMP
		
		
		commandAndItsArgumentsAsList.add( 
				"--" + RunImporterToImporterParameterNamesConstants.RUN_IMPORTER_PARAMS_FILE_PARAM_STRING 
				+ "=" + runImporterParamsFilename );
		

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

					 proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.FAILED );
						
					 proxlXMLFileImportTrackingRunDTO.setRunSubStatus( ProxlXMLFileImportRunSubStatus.SYSTEM_ERROR );

					 proxlXMLFileImportTrackingRunDTO.setDataErrorText( ERROR_MSG_SYSTEM_ERROR );
					 proxlXMLFileImportTrackingRunDTO.setImportResultText( ERROR_MSG_SYSTEM_ERROR );

					 UpdateTrackingTrackingRunRecordsDBTransaction.getInstance()
					 .updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
							 ProxlXMLFileImportStatus.FAILED, 
							 proxlXMLFileImportTrackingDTO.getId(), 
							 proxlXMLFileImportTrackingRunDTO );
				}				
			}
			
		} catch (Throwable e) {
			

			log.error( "command failed: " + commandAndItsArgumentsAsList
					+ ", subdirForThisTrackingId:  " + subdirForThisTrackingId.getCanonicalPath() );
			
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
		}
	}
	
	
	
	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {
		
		shutdownRequested = true;
		
		
		try {
		
			if ( runSystemCommand != null ) {

				runSystemCommand.shutdown();
			}
			
		} catch ( NullPointerException e ) {
			
			//  Eat the NullPointerException since that meant that nothing had to be done.
		}
		
		
	}
	
	
}
