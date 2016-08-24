package org.yeastrc.xlink.www.proxl_xml_file_import.webservices;


import java.io.File;
import java.io.FileInputStream;
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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.proxl_xml_file_import.constants.ProxlXMLFileUploadCommonConstants;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLImportSingleFileUploadStatus;
import org.yeastrc.xlink.base.proxl_xml_file_import.utils.Proxl_XML_ImporterWrkDirAndSbDrsCmmn;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappFileUploadFileSystemException;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.proxl_xml_file_import.constants.ProxlXMLFileUploadWebConstants;
import org.yeastrc.xlink.www.proxl_xml_file_import.dao.ProxlXMLFIleImportTrackingFileIdCreatorDAO;
import org.yeastrc.xlink.www.proxl_xml_file_import.database_insert_with_transaction_services.SaveImportTrackingAndChildrenSingleDBTransaction;
import org.yeastrc.xlink.www.proxl_xml_file_import.objects.ProxlUploadTempDataFileContents;
import org.yeastrc.xlink.www.proxl_xml_file_import.utils.DeleteDirectoryAndContentsUtil;
import org.yeastrc.xlink.www.proxl_xml_file_import.utils.IsProxlXMLFileImportFullyConfigured;
import org.yeastrc.xlink.www.proxl_xml_file_import.utils.IsScanFileImportAllowedViaWebSubmit;
import org.yeastrc.xlink.www.proxl_xml_file_import.utils.Proxl_XML_Importer_Work_Directory_And_SubDirs_Web;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.webservices.ProjectListForCurrentUserService;


/**
 * Called when user submits the upload
 *
 */
@Path("/proxl_xml_file_import")
public class ProxlXMLFileImportUploadSubmitService {

	

	private static final Logger log = Logger.getLogger(ProjectListForCurrentUserService.class);


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
			

			// Get the session first.  
//			HttpSession session = request.getSession();

			//   Get the project id for this search
			

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

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
			
			

			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			

			int authUserId = userSessionObject.getUserDBObject().getAuthUser().getId();
			
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
	
	
	///////////////////////////////////////
	

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
		
		
		//  Process files from tempSubdir, matching to request
		
		List<ProxlUploadTempDataFileContentsAndAssocData> proxlUploadTempDataFileContentsAndAssocData_OnDisk_List =
				getProxlUploadTempDataFileContentsListForTempSubdir( tempSubdir );
		
		
		//  Only process files sent in submit request

		List<ProxlUploadTempDataFileContentsAndAssocData> proxlUploadTempDataFileContentsAndAssocDataList = new ArrayList<>( proxlUploadTempDataFileContentsAndAssocData_OnDisk_List.size() );

		for ( UploadSubmitRequestFileItem requestFileItem : requestFileItemList ) {
			

			if ( requestFileItem.getFileType().intValue() == ProxlXMLFileImportFileType.SCAN_FILE.value() 
					&& ( ! IsScanFileImportAllowedViaWebSubmit.getInstance().isScanFileImportAllowedViaWebSubmit() ) ) {
				
				uploadSubmitResponse.setStatusSuccess(false);

				uploadSubmitResponse.scanFileNotAllowed = true;
				
				return uploadSubmitResponse;  //  EARLY EXIT
			}
			
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
						throw new ProxlWebappFileUploadFileSystemException(msg);
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
		
		
		
		
		String searchName = uploadSubmitRequest.getSearchName();
		
		if ( StringUtils.isEmpty( searchName ) ) {
			
			//  No Search name in upload request
		
			searchName = null; // make null if it is the empty string

			//  Get Search name from Proxl XML file if it is set

			for (  ProxlUploadTempDataFileContentsAndAssocData proxlUploadTempDataFileContentsAndAssocData : proxlUploadTempDataFileContentsAndAssocDataList ) {

				ProxlUploadTempDataFileContents proxlUploadTempDataFileContents = proxlUploadTempDataFileContentsAndAssocData.proxlUploadTempDataFileContents;
				
				if ( proxlUploadTempDataFileContents.getFileType() == ProxlXMLFileImportFileType.PROXL_XML_FILE ) {

					searchName = proxlUploadTempDataFileContents.getSearchNameInProxlXMLFile();
				}
			}
		}

		
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
		
		//  Remove the subdir the uploaded file(s) were in
		
		DeleteDirectoryAndContentsUtil.getInstance().deleteDirectoryAndContents( tempSubdir );
		
		
		
		ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO = new ProxlXMLFileImportTrackingDTO();
		
		proxlXMLFileImportTrackingDTO.setId( importTrackingId );

		proxlXMLFileImportTrackingDTO.setStatus( ProxlXMLFileImportStatus.QUEUED );
		
		
		proxlXMLFileImportTrackingDTO.setProjectId( projectId );
		proxlXMLFileImportTrackingDTO.setAuthUserId( authUserId );
		
		proxlXMLFileImportTrackingDTO.setSearchName( searchName );
		
		
		proxlXMLFileImportTrackingDTO.setInsertRequestURL( requestURL );
		
		
		proxlXMLFileImportTrackingDTO.setRemoteUserIpAddress( remoteUserIpAddress );
		
		//  Generate a UNIQUE hash
		
		String hashIdentifier = "NotUsableHash";
		
		
		proxlXMLFileImportTrackingDTO.setHashIdentifier( hashIdentifier );
		
		
		
		List<ProxlXMLFileImportTrackingSingleFileDTO> proxlXMLFileImportTrackingSingleFileDTOList = new ArrayList<>( 1 );
		
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
			proxlXMLFileImportTrackingSingleFileDTO.setFileUploadStatus( ProxlXMLImportSingleFileUploadStatus.FILE_UPLOAD_COMPLETE );
		}
		
		SaveImportTrackingAndChildrenSingleDBTransaction.getInstance()
		.saveImportTrackingAndChildrenInSingleDBTransaction( proxlXMLFileImportTrackingDTO, proxlXMLFileImportTrackingSingleFileDTOList );
		
		
		uploadSubmitResponse.statusSuccess = true;
		
		return uploadSubmitResponse;
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
	
	/////////////////////////////////
	
	/////   Classes for internal holders
	
	private static class ProxlUploadTempDataFileContentsAndAssocData {

		ProxlUploadTempDataFileContents proxlUploadTempDataFileContents;
	
		long fileLength;
	
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
		
		String searchName;
		
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
		
		
	}
	
	public static class UploadSubmitRequestFileItem {

		private String uploadedFilename;
		private Integer fileType;
		private Integer fileIndex;
		private Boolean isProxlXMLFile;
		
		
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
	}

	/**
	 * 
	 *
	 */
	public static class UploadSubmitResponse {

		private boolean statusSuccess;

		private boolean projectLocked;
		
		private boolean scanFileNotAllowed;

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

		public boolean isScanFileNotAllowed() {
			return scanFileNotAllowed;
		}

		public void setScanFileNotAllowed(boolean scanFileNotAllowed) {
			this.scanFileNotAllowed = scanFileNotAllowed;
		} 
	}
}
