package org.yeastrc.xlink.www.file_import_proxl_xml_scans.webservices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadCommonConstants;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLImportSingleFileUploadStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.utils.Proxl_XML_ImporterWrkDirAndSbDrsCmmn;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappFileUploadFileSystemException;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadSubmitterPgmSameMachineConstants;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadWebConstants;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.dao.ProxlXMLFIleImportTrackingFileIdCreatorDAO;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.database_insert_with_transaction_services.SaveImportTrackingAndChildrenSingleDBTransaction;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.objects.ProxlUploadTempDataFileContents;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.DeleteDirectoryAndContentsUtil;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.IsProxlXMLFileImportFullyConfigured;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.IsScanFileImportAllowedViaWebSubmit;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.Proxl_XML_Importer_Work_Directory_And_SubDirs_Web;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

/**
 * Called when user submits the upload
 *
 */
@Path("/file_import_proxl_xml_scans")
public class ProxlXMLFileImportUploadSubmitService {
	
	private static final Logger log = LoggerFactory.getLogger( ProxlXMLFileImportUploadSubmitService.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/uploadSubmit")
	public UploadSubmitResponse  uploadSubmit( 
			UploadSubmitRequest uploadSubmitRequest ,
			@Context HttpServletRequest request ) throws Exception {
		try {
			if ( uploadSubmitRequest.projectId == null ) {
				String msg = "missing projectId ";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( uploadSubmitRequest.projectId == 0 ) {
				String msg = "Provided projectId is zero";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			int projectId = uploadSubmitRequest.projectId;
			if ( uploadSubmitRequest.uploadKey == null ) {
				String msg = "missing uploadKey ";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			long uploadKey = -1;
			try {
				uploadKey = Long.parseLong( uploadSubmitRequest.uploadKey );
			} catch ( Exception e ) {
				String msg = "Provided uploadKey is invalid";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( uploadSubmitRequest.submitterSameMachine 
					&& ( StringUtils.isEmpty( uploadSubmitRequest.submitterKey ) ) ) {
				String msg = "submitterKey cannot be empty if submitterSameMachine is true";
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			//   Get the project id for this search
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  If NOT Proxl XML File Import is Fully Configured, 
			if ( ! IsProxlXMLFileImportFullyConfigured.getInstance().isProxlXMLFileImportFullyConfigured() ) {
				String msg = "Proxl XML File Import is NOT Fully Configured ";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			//  Confirm projectId is in database
			ProjectDTO projectDTO =	ProjectDAO.getInstance().getProjectDTOForProjectId( projectId );
			if ( projectDTO == null ) {
				// should never happen
				String msg = "Project id is not in database " + projectId;
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( ( ! projectDTO.isEnabled() ) || ( projectDTO.isMarkedForDeletion() ) ) {
				String msg = "Project id is disabled or marked for deletion: " + projectId;
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( ( projectDTO.isProjectLocked() ) ) {
				String msg = "Project id is locked: " + projectId;
				log.warn( msg );
				UploadSubmitResponse uploadSubmitResponse = new UploadSubmitResponse();
				uploadSubmitResponse.setStatusSuccess(false);
				uploadSubmitResponse.setProjectLocked(true);
				return uploadSubmitResponse;  //  EARLY EXIT
			}
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			int authUserId = userSession.getAuthUserId();
			String requestURL = request.getRequestURL().toString();
			String remoteUserIpAddress = request.getRemoteHost();
			File importer_Work_Directory = Proxl_XML_ImporterWrkDirAndSbDrsCmmn.getInstance().get_Proxl_XML_Importer_Work_Directory();
			//  Get the File object for the Base Subdir used to temporarily store the files in this request 
			String uploadFileTempDirString =
					Proxl_XML_Importer_Work_Directory_And_SubDirs_Web.getInstance().getDirForUploadFileTempDir();
			File uploadFileTempDir = new File( importer_Work_Directory, uploadFileTempDirString );
			if ( ! uploadFileTempDir.exists() ) {
				String msg = "uploadFileTempDir does not exist.  uploadFileTempDir: " 
						+ uploadFileTempDir.getAbsolutePath();
				log.error( msg );
				throw new ProxlWebappFileUploadFileSystemException(msg);
			}
			File tempSubdir =
					Proxl_XML_Importer_Work_Directory_And_SubDirs_Web.getInstance()
					.getSubDirForUploadFileTempDir( authUserId, uploadKey, uploadFileTempDir );
			if ( ! tempSubdir.exists() ) {
				if ( log.isInfoEnabled() ) {
					String infoMsg = "tempSubdir does not exist.  tempSubdir: " 
							+ uploadFileTempDir.getAbsolutePath();
					log.info( infoMsg );
				}
				String webErrorMsg = "No Data for uploadKey: " + uploadKey;
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( webErrorMsg )
			    	        .build()
			    	        );
			}
			UploadSubmitResponse uploadSubmitResponse = 
					processRequest( 
							uploadSubmitRequest, 
							remoteUserIpAddress, 
							requestURL,
							projectId, 
							authUserId, 
							importer_Work_Directory, 
							tempSubdir );
			return uploadSubmitResponse;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	/**
	 * @param uploadSubmitRequest
	 * @param remoteUserIpAddress
	 * @param requestURL
	 * @param projectId
	 * @param authUserId
	 * @param importer_Work_Directory
	 * @param tempSubdir
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappFileUploadFileSystemException
	 * @throws IOException
	 */
	private UploadSubmitResponse processRequest(
			UploadSubmitRequest uploadSubmitRequest,
			String remoteUserIpAddress,
			String requestURL,
			int projectId,
			int authUserId,
			File importer_Work_Directory, 
			File tempSubdir
			) throws Exception, ProxlWebappFileUploadFileSystemException, IOException {
		UploadSubmitResponse uploadSubmitResponse = new UploadSubmitResponse();
		boolean isScanFileImportAllowed = IsScanFileImportAllowedViaWebSubmit.getInstance().isScanFileImportAllowedViaWebSubmit();
		List<UploadSubmitRequestFileItem> requestFileItemList = uploadSubmitRequest.fileItems;
		if ( requestFileItemList == null || requestFileItemList.isEmpty() ) {
			String msg = "No files in request";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		//  use filenamesSet to find duplicate filenames
		Set<String> filenamesSet = new HashSet<>();
		boolean foundProxlXMLFile = false;
		for ( UploadSubmitRequestFileItem requestFileItem : requestFileItemList ) {
			if ( requestFileItem.fileIndex == null ) {
				String msg = "requestFileItem.fileIndex == null";
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( requestFileItem.fileType == null ) {
				String msg = "requestFileItem.fileType == null";
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( requestFileItem.getFileType().intValue() == ProxlXMLFileImportFileType.PROXL_XML_FILE.value() ) {
				if ( foundProxlXMLFile ) {
					String msg = "More than one Proxl XML file";
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				foundProxlXMLFile = true;
			} else if ( requestFileItem.getFileType().intValue() == ProxlXMLFileImportFileType.SCAN_FILE.value() ) {
				if ( ! isScanFileImportAllowed ) {
					uploadSubmitResponse.statusSuccess = false;
					uploadSubmitResponse.submittedScanFileNotAllowed = true;
					return uploadSubmitResponse;  //  EARLY EXIT
				}
			} else {
				String msg = "File Type is unknown: " + requestFileItem.getFileType().intValue();
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( requestFileItem.uploadedFilename == null ) {
				String msg = "requestFileItem.uploadedFilename == null";
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( ! filenamesSet.add( requestFileItem.uploadedFilename ) ) {
				String msg = "Duplicate filename: " + requestFileItem.uploadedFilename;
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
		}
		//  Determine search name, starting with search name in Submit request
		String searchName = uploadSubmitRequest.getSearchName();
		if ( StringUtils.isEmpty( searchName ) ) {
			//  No Search name in upload request
			searchName = null; // make null if it is the empty string
		}
		//  use search path in Submit request if populated
		String searchPath = uploadSubmitRequest.getSearchPath();
		if ( StringUtils.isEmpty( searchPath ) ) {
			//  No Search path in upload request
			searchPath= null; // make null if it is the empty string
		}
		List<ProxlXMLFileImportTrackingSingleFileDTO> proxlXMLFileImportTrackingSingleFileDTOList = new ArrayList<>( 1 );
		List<ProxlUploadTempDataFileContentsAndAssocData> proxlUploadTempDataFileContentsAndAssocDataList = null;
		if ( ! uploadSubmitRequest.submitterSameMachine ) {
			ProcessFilesInTempUploadDirResult processFilesInTempUploadDirResult =
					processFilesInTempUploadDir(
							tempSubdir, requestFileItemList);
			proxlUploadTempDataFileContentsAndAssocDataList =
					processFilesInTempUploadDirResult.proxlUploadTempDataFileContentsAndAssocDataList;
			ProxlXMLFileImportTrackingSingleFileDTO proxlXMLFileImportTrackingSingleFileDTO = null;
			//  proxlXMLFileImportTrackingSingleFileDTO entry for Uploaded file(s)
			for (  ProxlUploadTempDataFileContentsAndAssocData proxlUploadTempDataFileContentsAndAssocData : proxlUploadTempDataFileContentsAndAssocDataList ) {
				ProxlUploadTempDataFileContents proxlUploadTempDataFileContents = proxlUploadTempDataFileContentsAndAssocData.proxlUploadTempDataFileContents;
				proxlXMLFileImportTrackingSingleFileDTO = new ProxlXMLFileImportTrackingSingleFileDTO();
				proxlXMLFileImportTrackingSingleFileDTOList.add( proxlXMLFileImportTrackingSingleFileDTO );
				proxlXMLFileImportTrackingSingleFileDTO.setFilenameInUpload( proxlUploadTempDataFileContents.getUploadedFilename() );
				proxlXMLFileImportTrackingSingleFileDTO.setFilenameOnDisk( proxlUploadTempDataFileContents.getSavedToDiskFilename() );
				proxlXMLFileImportTrackingSingleFileDTO.setFileType( proxlUploadTempDataFileContents.getFileType() );
				proxlXMLFileImportTrackingSingleFileDTO.setFileSize( proxlUploadTempDataFileContentsAndAssocData.fileLength );
				proxlXMLFileImportTrackingSingleFileDTO.setCanonicalFilename_W_Path_OnSubmitMachine( proxlUploadTempDataFileContents.getCanonicalFilename_W_Path_OnSubmitMachine() );
				proxlXMLFileImportTrackingSingleFileDTO.setAbsoluteFilename_W_Path_OnSubmitMachine( proxlUploadTempDataFileContents.getAbsoluteFilename_W_Path_OnSubmitMachine() );
				proxlXMLFileImportTrackingSingleFileDTO.setFileUploadStatus( ProxlXMLImportSingleFileUploadStatus.FILE_UPLOAD_COMPLETE );
				if ( proxlUploadTempDataFileContents.getFileType() == ProxlXMLFileImportFileType.PROXL_XML_FILE ) {
					String searchNameFromProxlXMLFile = proxlUploadTempDataFileContents.getSearchNameInProxlXMLFile();
					if ( StringUtils.isEmpty( searchName )
							&& ( StringUtils.isNotEmpty( searchNameFromProxlXMLFile ) )) {
						//  No Search name in upload request AND Search name in Proxl XML file
						searchName = searchNameFromProxlXMLFile;
					}
				}
			}
		} else {
			 //  uploadSubmitRequest.submitterSameMachine true
			// validate submitterKey
			boolean isValid = validateSubmitterKeyForSubmitSameMachine( uploadSubmitRequest.submitterKey, tempSubdir );
			if ( ! isValid ) {
				//  Submitter Key is not valid so remove the tmp upload subdir, making the upload key unusable
				//   This will remove the submitter key file, making the submitter key unusable
				DeleteDirectoryAndContentsUtil.getInstance().deleteDirectoryAndContents( tempSubdir );
				String msg = "Submitter Key Not Valid";
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			//  Validate that submitted files exist and add to proxlXMLFileImportTrackingSingleFileDTOList
			List<UploadSubmitRequestFileItem> fileItemList = uploadSubmitRequest.fileItems;
			for ( UploadSubmitRequestFileItem fileItem : fileItemList ) {
				ProxlXMLFileImportFileType proxlXMLFileImportFileType = null;
				try {
					proxlXMLFileImportFileType = ProxlXMLFileImportFileType.fromValue( fileItem.fileType );
				} catch ( Exception e ) {
					String msg = "File Type is unknown: " + fileItem.fileType;
					log.warn( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg )
							.build()
							);
				}
				File fileItemFile = new File( fileItem.filenameOnDiskWithPathSubSameMachine );
				if ( ! fileItemFile.exists() ) {
					String msg = "File not found: " + fileItemFile.getCanonicalPath();
					log.warn( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg )
							.build()
							);
				}
				long fileSize = fileItemFile.length();
				if ( proxlXMLFileImportFileType == ProxlXMLFileImportFileType.PROXL_XML_FILE ) {
					//  Do minimal validation of Proxl XML file and get search name if in the file
					String searchNameFromProxlXMLFile =
							Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile.getInstance()
							.minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile( fileItemFile );
					if ( StringUtils.isEmpty( searchName )
							&& ( StringUtils.isNotEmpty( searchNameFromProxlXMLFile ) )) {
						//  Search name in Proxl XML file
						searchName = searchNameFromProxlXMLFile;
					}
				}
				ProxlXMLFileImportTrackingSingleFileDTO proxlXMLFileImportTrackingSingleFileDTO = new ProxlXMLFileImportTrackingSingleFileDTO();
				proxlXMLFileImportTrackingSingleFileDTOList.add( proxlXMLFileImportTrackingSingleFileDTO );
				proxlXMLFileImportTrackingSingleFileDTO.setFilenameInUpload( fileItemFile.getName() );
				proxlXMLFileImportTrackingSingleFileDTO.setFilenameOnDisk( fileItemFile.getName() );
				proxlXMLFileImportTrackingSingleFileDTO.setFilenameOnDiskWithPathSubSameMachine( fileItem.filenameOnDiskWithPathSubSameMachine );
				proxlXMLFileImportTrackingSingleFileDTO.setFileType( proxlXMLFileImportFileType );
				proxlXMLFileImportTrackingSingleFileDTO.setFileSize( fileSize );
				proxlXMLFileImportTrackingSingleFileDTO.setCanonicalFilename_W_Path_OnSubmitMachine( fileItemFile.getCanonicalPath() );
				proxlXMLFileImportTrackingSingleFileDTO.setFileUploadStatus( ProxlXMLImportSingleFileUploadStatus.FILE_UPLOAD_COMPLETE );
			}
		}
		//  Get the File object for the Base Subdir used to store the files in this request 
		File importFilesBaseDir = new File( importer_Work_Directory, ProxlXMLFileUploadCommonConstants.IMPORT_BASE_DIR );
		if ( ! importFilesBaseDir.exists() ) {
//				boolean mkdirResult = 
			importFilesBaseDir.mkdir();
		}
		if ( ! importFilesBaseDir.exists() ) {
			String msg = "importFilesBaseDir does not exist after testing for it and attempting to create it.  importFilesBaseDir: " 
					+ importFilesBaseDir.getAbsolutePath();
			log.error( msg );
			throw new ProxlWebappFileUploadFileSystemException(msg);
		}
		int importTrackingId = ProxlXMLFIleImportTrackingFileIdCreatorDAO.getInstance().getNextId();
		String dirNameForImportTrackingId =
				Proxl_XML_ImporterWrkDirAndSbDrsCmmn.getInstance().getDirForImportTrackingId( importTrackingId );
		File dirForImportTrackingId  =  new File( importFilesBaseDir , dirNameForImportTrackingId );
		if ( dirForImportTrackingId.exists() ) {
			String msg = "dirForImportTrackingId already exists: " + dirForImportTrackingId.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
		if ( ! dirForImportTrackingId.mkdir() ) {
			String msg = "Failed to make dirForImportTrackingId: " + dirForImportTrackingId.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
		if ( uploadSubmitRequest.submitterSameMachine ) {
			//  submitterSameMachine  true so create file with list of file names with paths to be imported
			File importFileListFile = 
					new File( dirForImportTrackingId, ProxlXMLFileUploadWebConstants.IMPORT_FILE_LIST_FILE );
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter( new FileWriter( importFileListFile ) );
				writer.write( "List of files to be imported" );
				writer.newLine();
				for ( ProxlXMLFileImportTrackingSingleFileDTO item : proxlXMLFileImportTrackingSingleFileDTOList ) {
					writer.write( item.getFilenameOnDiskWithPathSubSameMachine() );
					writer.newLine();
				}
			} finally {
				if ( writer != null ) {
					writer.close();
				}
			}
		}
		if ( ! uploadSubmitRequest.submitterSameMachine ) {
			//  Files were uploaded so move from temp dir to import dir (import dir name based on tracking id) 
			moveUploadedFilesToWorkDirectory(
					tempSubdir,
					proxlUploadTempDataFileContentsAndAssocDataList,
					dirForImportTrackingId );
		}
		//  Remove the subdir the uploaded file(s) were in
		DeleteDirectoryAndContentsUtil.getInstance().deleteDirectoryAndContents( tempSubdir );
		ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO = new ProxlXMLFileImportTrackingDTO();
		proxlXMLFileImportTrackingDTO.setId( importTrackingId );
		proxlXMLFileImportTrackingDTO.setStatus( ProxlXMLFileImportStatus.QUEUED );
		proxlXMLFileImportTrackingDTO.setPriority( ProxlXMLFileUploadCommonConstants.PRIORITY_STANDARD );
		proxlXMLFileImportTrackingDTO.setProjectId( projectId );
		proxlXMLFileImportTrackingDTO.setAuthUserId( authUserId );
		proxlXMLFileImportTrackingDTO.setSearchName( searchName );
		proxlXMLFileImportTrackingDTO.setSearchPath( searchPath );
		proxlXMLFileImportTrackingDTO.setInsertRequestURL( requestURL );
		proxlXMLFileImportTrackingDTO.setRemoteUserIpAddress( remoteUserIpAddress );
		SaveImportTrackingAndChildrenSingleDBTransaction.getInstance()
		.saveImportTrackingAndChildrenInSingleDBTransaction( proxlXMLFileImportTrackingDTO, proxlXMLFileImportTrackingSingleFileDTOList );
		if ( uploadSubmitRequest.submitterSameMachine ) {
			//  submitterSameMachine true, return the subdir name for import
			uploadSubmitResponse.importerSubDir = dirNameForImportTrackingId;
		}
		uploadSubmitResponse.statusSuccess = true;
		return uploadSubmitResponse;
	}
	
	/**
	 * @param tempSubdir
	 * @param requestFileItemList
	 * @return
	 * @throws JAXBException
	 * @throws ProxlWebappFileUploadFileSystemException
	 * @throws IOException
	 */
	private ProcessFilesInTempUploadDirResult processFilesInTempUploadDir(
			File tempSubdir, 
			List<UploadSubmitRequestFileItem> requestFileItemList )
			throws JAXBException, ProxlWebappFileUploadFileSystemException, IOException {
		ProcessFilesInTempUploadDirResult processFilesInTempUploadDirResult = new ProcessFilesInTempUploadDirResult();
		//  Process files from tempSubdir, matching to request
		List<ProxlUploadTempDataFileContentsAndAssocData> proxlUploadTempDataFileContentsAndAssocData_OnDisk_List =
				getProxlUploadTempDataFileContentsListForTempSubdir( tempSubdir );
		//  Only process files sent in submit request
		List<ProxlUploadTempDataFileContentsAndAssocData> proxlUploadTempDataFileContentsAndAssocDataList = new ArrayList<>( proxlUploadTempDataFileContentsAndAssocData_OnDisk_List.size() );
		for ( UploadSubmitRequestFileItem requestFileItem : requestFileItemList ) {
			ProxlUploadTempDataFileContentsAndAssocData proxlUploadTempDataFileContentsAndAssocDataForRequestFileItem = null;
			for ( ProxlUploadTempDataFileContentsAndAssocData proxlUploadTempDataFileContentsAndAssocData  : 
				proxlUploadTempDataFileContentsAndAssocData_OnDisk_List ) {
				ProxlUploadTempDataFileContents proxlUploadTempDataFileContents = proxlUploadTempDataFileContentsAndAssocData.proxlUploadTempDataFileContents;
				if ( requestFileItem.getFileIndex().intValue() == proxlUploadTempDataFileContents.getFileIndex() 
						&& requestFileItem.getFileType().intValue() == proxlUploadTempDataFileContents.getFileType().value() 
						&& requestFileItem.getUploadedFilename().equals( proxlUploadTempDataFileContents.getUploadedFilename() ) ) { 
					if ( proxlUploadTempDataFileContentsAndAssocDataForRequestFileItem != null ) {
						String msg = "Found more than one file on disk match for file index: " + requestFileItem.getFileIndex().intValue() 
								+ ", file type: " + requestFileItem.getFileType().intValue()
								+ ", uploaded filename: " + requestFileItem.getUploadedFilename();
						log.error( msg );
						throw new ProxlWebappFileUploadFileSystemException( msg );
					}
					proxlUploadTempDataFileContentsAndAssocDataForRequestFileItem = proxlUploadTempDataFileContentsAndAssocData;
				}
			}
			if ( proxlUploadTempDataFileContentsAndAssocDataForRequestFileItem == null ) {
				String msg = "No file on disk matched for file index: " + requestFileItem.getFileIndex().intValue() 
						+ ", file type: " + requestFileItem.getFileType().intValue()
						+ ", uploaded filename: " + requestFileItem.getUploadedFilename();
				log.error( msg );
				throw new ProxlWebappFileUploadFileSystemException(msg);
			}
			proxlUploadTempDataFileContentsAndAssocDataList.add( proxlUploadTempDataFileContentsAndAssocDataForRequestFileItem );
		}
		//  Ensure exactly one Proxl XML file is processed
		boolean processedProxlXMLFile = false;
		for (  ProxlUploadTempDataFileContentsAndAssocData proxlUploadTempDataFileContentsAndAssocData : proxlUploadTempDataFileContentsAndAssocDataList ) {
			ProxlUploadTempDataFileContents proxlUploadTempDataFileContents = proxlUploadTempDataFileContentsAndAssocData.proxlUploadTempDataFileContents;
			if ( proxlUploadTempDataFileContents.getFileType() == ProxlXMLFileImportFileType.PROXL_XML_FILE ) {
				if ( processedProxlXMLFile ) {
					String msg = "More than one Proxl XML file";
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				processedProxlXMLFile = true;
			}
		}
		if ( ! processedProxlXMLFile ) {
			String msg = "Missing Proxl XML file";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		processFilesInTempUploadDirResult.proxlUploadTempDataFileContentsAndAssocDataList = proxlUploadTempDataFileContentsAndAssocData_OnDisk_List;
		return processFilesInTempUploadDirResult;
	}
	
	/**
	 * @param tempSubdir
	 * @param proxlUploadTempDataFileContentsAndAssocDataList
	 * @param dirForImportTrackingId
	 * @throws ProxlWebappFileUploadFileSystemException
	 */
	private void moveUploadedFilesToWorkDirectory(
			File tempSubdir,
			List<ProxlUploadTempDataFileContentsAndAssocData> proxlUploadTempDataFileContentsAndAssocDataList,
			File dirForImportTrackingId)
			throws ProxlWebappFileUploadFileSystemException {
		///   move the uploaded file(s) into importer work dir.
		for (  ProxlUploadTempDataFileContentsAndAssocData proxlUploadTempDataFileContentsAndAssocData : proxlUploadTempDataFileContentsAndAssocDataList ) {
			ProxlUploadTempDataFileContents proxlUploadTempDataFileContents = proxlUploadTempDataFileContentsAndAssocData.proxlUploadTempDataFileContents;
			File uploadedTempFileOnDisk = new File( tempSubdir, proxlUploadTempDataFileContents.getSavedToDiskFilename() );
			File uploadedFile_In_dirForImportTrackingId = new File( dirForImportTrackingId, proxlUploadTempDataFileContents.getSavedToDiskFilename() );
			try {
				FileUtils.moveFile( uploadedTempFileOnDisk, uploadedFile_In_dirForImportTrackingId );
			} catch ( Exception e ) {
				String msg = "Failed to move uploaded file to dirForImportTrackingId.  Src file: " + uploadedTempFileOnDisk
						+ ", dest file: " + uploadedFile_In_dirForImportTrackingId;
				log.error( msg, e );
				throw new ProxlWebappFileUploadFileSystemException(msg, e);
			}
		}
	}
	
	/**
	 * @param tempSubdir
	 * @return List of ProxlUploadTempDataFileContents
	 * @throws JAXBException 
	 * @throws ProxlWebappFileUploadFileSystemException 
	 * @throws IOException 
	 */
	private List<ProxlUploadTempDataFileContentsAndAssocData> getProxlUploadTempDataFileContentsListForTempSubdir( File tempSubdir ) throws JAXBException, ProxlWebappFileUploadFileSystemException, IOException {
		List<ProxlUploadTempDataFileContentsAndAssocData> proxlUploadTempDataFileContentsAndAssocDataList = new ArrayList<>();
		File[] tempSubdirFiles = tempSubdir.listFiles();
		if ( tempSubdirFiles.length > 0 ) {
			JAXBContext jaxbContext = JAXBContext.newInstance( ProxlUploadTempDataFileContents.class );
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			for ( File tempSubdirFile : tempSubdirFiles ) {
				String tempSubdirFilename = tempSubdirFile.getName();
				if ( tempSubdirFilename.startsWith( ProxlXMLFileUploadWebConstants.UPLOAD_FILE_DATA_FILE_PREFIX ) ) {
					//  Unmarshal (read) the object from the file
					ProxlUploadTempDataFileContents proxlUploadTempDataFileContents = null;
					Object objectFromFile = null;
					InputStream inputStream = null;
					try {
						inputStream = new FileInputStream( tempSubdirFile );
						objectFromFile = unmarshaller.unmarshal( inputStream );
					} catch ( Exception e ) {
						String msg = "Failed to read and unmarshall data from file: " + tempSubdirFile.getCanonicalPath();
						log.error( msg );
						throw new ProxlWebappFileUploadFileSystemException(msg,e);
					} finally {
						if ( inputStream != null ) {
							inputStream.close();
						}
					}
					if ( ! ( objectFromFile instanceof ProxlUploadTempDataFileContents ) ) {
						String msg = "object unmarshalled from file is incorrect type: " + objectFromFile.getClass().getCanonicalName();
						log.error( msg );
						throw new ProxlWebappFileUploadFileSystemException(msg);
					}
					try {
						proxlUploadTempDataFileContents = 
								(ProxlUploadTempDataFileContents) objectFromFile;
					} catch ( Exception e ) {
						String msg = "object unmarshalled from file is incorrect type: " + objectFromFile.getClass().getCanonicalName();
						log.error( msg );
						throw new ProxlWebappFileUploadFileSystemException(msg, e);
					}
					ProxlUploadTempDataFileContentsAndAssocData proxlUploadTempDataFileContentsAndAssocData = new ProxlUploadTempDataFileContentsAndAssocData(); 
					proxlUploadTempDataFileContentsAndAssocData.proxlUploadTempDataFileContents = proxlUploadTempDataFileContents;
					//  Get length of uploaded file
					File uploadedFile = null;
					try {
						uploadedFile = new File( tempSubdir, proxlUploadTempDataFileContents.getSavedToDiskFilename() );
						proxlUploadTempDataFileContentsAndAssocData.fileLength = uploadedFile.length();
					} catch ( Exception e ) {
						String msg = "Error getting length of uploaded file: " + uploadedFile.getAbsolutePath();
						log.error( msg );
						throw new ProxlWebappFileUploadFileSystemException(msg, e);
					}
					proxlUploadTempDataFileContentsAndAssocDataList.add( proxlUploadTempDataFileContentsAndAssocData );
				}
			}
		}
		return proxlUploadTempDataFileContentsAndAssocDataList;
	}
	
	/**
	 * @param submitterKey
	 * @param tempSubdir
	 * @return - true if valid, false if invalid
	 * @throws Exception
	 */
	private boolean validateSubmitterKeyForSubmitSameMachine( String submitterKey, File tempSubdir ) throws Exception {
		File submitterKeyFile = new File( 
				tempSubdir, 
				ProxlXMLFileUploadSubmitterPgmSameMachineConstants.SUBMITTER_KEY_FILENAME );
		if ( ! submitterKeyFile.exists() ) {
			String msg = "No Submitter key on server when submitterSameMachine is true.";
			log.warn( msg );
			return false;
		}
		String submitterKeyFileLine = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader( new FileReader(submitterKeyFile));
			submitterKeyFileLine = reader.readLine();
		} catch ( Exception e ) {
			String msg = "Exception reading submitter key in file.";
			log.error( msg, e );
			throw e;
		} finally {
			if ( reader != null ) {
				reader.close();
			}
		}
		if ( ! submitterKey.equals( submitterKeyFileLine ) ) {
			String msg = "Submitter key on server does not match submitter key in request.";
			log.warn( msg );
			return false;
		}
		return true;
	}
	
	/////////////////////////////////
	/////   Classes for internal holders
	private static class ProxlUploadTempDataFileContentsAndAssocData {
		ProxlUploadTempDataFileContents proxlUploadTempDataFileContents;
		long fileLength;
	}
	private static class ProcessFilesInTempUploadDirResult {
		List<ProxlUploadTempDataFileContentsAndAssocData> proxlUploadTempDataFileContentsAndAssocDataList;
	}
	
	/////////////////////////////////
	/////   Classes for webservice request and response
	/**
	 * 
	 *
	 */
	public static class UploadSubmitRequest {
		Integer projectId;
		String uploadKey;
		/**
		 * For submitting on same machine
		 */
		boolean submitterSameMachine;
		/**
		 * For submitting on same machine
		 */
		String submitterKey;
		String searchName;
		String searchPath;
		List<UploadSubmitRequestFileItem> fileItems;
		public Integer getProjectId() {
			return projectId;
		}
		public void setProjectId(Integer projectId) {
			this.projectId = projectId;
		}
		public String getUploadKey() {
			return uploadKey;
		}
		public void setUploadKey(String uploadKey) {
			this.uploadKey = uploadKey;
		}
		public String getSearchName() {
			return searchName;
		}
		public void setSearchName(String searchName) {
			this.searchName = searchName;
		}
		public List<UploadSubmitRequestFileItem> getFileItems() {
			return fileItems;
		}
		public void setFileItems(List<UploadSubmitRequestFileItem> fileItems) {
			this.fileItems = fileItems;
		}
		public String getSubmitterKey() {
			return submitterKey;
		}
		public void setSubmitterKey(String submitterKey) {
			this.submitterKey = submitterKey;
		}
		public boolean isSubmitterSameMachine() {
			return submitterSameMachine;
		}
		public void setSubmitterSameMachine(boolean submitterSameMachine) {
			this.submitterSameMachine = submitterSameMachine;
		}
		public String getSearchPath() {
			return searchPath;
		}
		public void setSearchPath(String searchPath) {
			this.searchPath = searchPath;
		}
	}
	
	public static class UploadSubmitRequestFileItem {
		private String uploadedFilename;
		private Integer fileType;
		private Integer fileIndex;
		private Boolean isProxlXMLFile;
		//  Following are only for submitting on same machine
		private String filenameOnDiskWithPathSubSameMachine;
		public String getUploadedFilename() {
			return uploadedFilename;
		}
		public void setUploadedFilename(String uploadedFilename) {
			this.uploadedFilename = uploadedFilename;
		}
		public Integer getFileType() {
			return fileType;
		}
		public void setFileType(Integer fileType) {
			this.fileType = fileType;
		}
		public Integer getFileIndex() {
			return fileIndex;
		}
		public void setFileIndex(Integer fileIndex) {
			this.fileIndex = fileIndex;
		}
		public Boolean getIsProxlXMLFile() {
			return isProxlXMLFile;
		}
		public void setIsProxlXMLFile(Boolean isProxlXMLFile) {
			this.isProxlXMLFile = isProxlXMLFile;
		}
		public String getFilenameOnDiskWithPathSubSameMachine() {
			return filenameOnDiskWithPathSubSameMachine;
		}
		public void setFilenameOnDiskWithPathSubSameMachine(
				String filenameOnDiskWithPathSubSameMachine) {
			this.filenameOnDiskWithPathSubSameMachine = filenameOnDiskWithPathSubSameMachine;
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class UploadSubmitResponse {
		private boolean statusSuccess;
		private boolean projectLocked;
		private boolean submittedScanFileNotAllowed;
		private String importerSubDir;
		public boolean isStatusSuccess() {
			return statusSuccess;
		}
		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}
		public boolean isProjectLocked() {
			return projectLocked;
		}
		public void setProjectLocked(boolean projectLocked) {
			this.projectLocked = projectLocked;
		}
		public boolean isSubmittedScanFileNotAllowed() {
			return submittedScanFileNotAllowed;
		}
		public void setSubmittedScanFileNotAllowed(boolean submittedScanFileNotAllowed) {
			this.submittedScanFileNotAllowed = submittedScanFileNotAllowed;
		}
		public String getImporterSubDir() {
			return importerSubDir;
		}
		public void setImporterSubDir(String importerSubDir) {
			this.importerSubDir = importerSubDir;
		}
	}
}
