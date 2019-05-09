package org.yeastrc.xlink.www.file_import_proxl_xml_scans.struts_action_as_webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.utils.Proxl_XML_ImporterWrkDirAndSbDrsCmmn;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappFileUploadFileSystemException;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadMaxFileSizeConstants;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadWebConstants;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.objects.ProxlUploadTempDataFileContents;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.IsScanFileImportAllowedViaWebSubmit;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.Proxl_XML_Importer_Work_Directory_And_SubDirs_Web;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import com.fasterxml.jackson.databind.ObjectMapper;


///////   In Progress to pass file via the post contents (just getInputStream() instead of via multipart form)

//   The Java Submitter Pgm is successfully passing the cookie for the JSESSIONID so getSession returns the logged in user.
//
//   Next need to remove all the Multipart form processing and write from the InputStream directly to the final file on disk.
//   		(This is the way Limelight works) 


/**
 * Upload 
 * 
 * This Struts Action should be considered a webservice as it returns JSON
 */
public class UploadFileForImportWebserviceAction extends Action {

	private static final Logger log = LoggerFactory.getLogger(  UploadFileForImportWebserviceAction.class );

	private static final int COPY_FILE_ARRAY_SIZE = 32 * 1024;
	
	//  Keep all these Strings in sync with the class SendToServerConstants in subdir proxl_submit_import
	
	public static final String UPLOAD_FILE_HEADER_NAME_UPLOAD_KEY = "X-Proxl-upload_key";
	public static final String UPLOAD_FILE_HEADER_NAME_PROJECT_ID = "X-Proxl-project_id";
	public static final String UPLOAD_FILE_HEADER_NAME_FILE_INDEX = "X-Proxl-file_index";
	public static final String UPLOAD_FILE_HEADER_NAME_FILE_TYPE = "X-Proxl-file_type";
	public static final String UPLOAD_FILE_HEADER_NAME_FILENAME = "X-Proxl-filename";
	
	/**
	 * Header name for uploaded file on submitting machine ( Java Canonical Path )
	 */
	public static final String UPLOAD_FILE_HEADER_NAME_UPLOADED_FILENAME_W_PATH_CANONICAL = 
			"X-Proxl-uploadFileWPathCanonical";
	/**
	 * Header name for uploaded file on submitting machine ( Java Absolute Path )
	 */
	public static final String UPLOAD_FILE_HEADER_NAME_UPLOADED_FILENAME_W_PATH_ABSOLUTE = 
			"X-Proxl-uploadFileWPathAbsolute";
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {

		
		
		//  	request.getParameter(...) only comes from the query string
//		String uploadType = request.getParameter( "uploadTypeQueryString" ); 
		String scanFileSuffix = null;
		File uploadFileTempSubDirForThisRequestFileObj = null;
		File uploadedFileOnDisk = null;
		ProxlXMLFileImportFileType fileType = null;
		String canonicalFilename_W_Path_OnSubmitMachine = null;
		String absoluteFilename_W_Path_OnSubmitMachine = null;
		int fileIndex = -1;
		int projectId = -1;
		long uploadKey = -1;
		long maxFileSize = -1;
		String maxFileSizeFormatted = null;
		try {
//			String requestURL = request.getRequestURL().toString();
			
			canonicalFilename_W_Path_OnSubmitMachine = request.getHeader( UPLOAD_FILE_HEADER_NAME_UPLOADED_FILENAME_W_PATH_CANONICAL );
			absoluteFilename_W_Path_OnSubmitMachine = request.getHeader( UPLOAD_FILE_HEADER_NAME_UPLOADED_FILENAME_W_PATH_ABSOLUTE );

			String uploadedFilename = request.getHeader( UPLOAD_FILE_HEADER_NAME_FILENAME );
			String fileIndexString = request.getHeader( UPLOAD_FILE_HEADER_NAME_FILE_INDEX );
			String fileTypeString = request.getHeader( UPLOAD_FILE_HEADER_NAME_FILE_TYPE );
			String projectIdString = request.getHeader( UPLOAD_FILE_HEADER_NAME_PROJECT_ID );
			String uploadKeyString = request.getHeader( UPLOAD_FILE_HEADER_NAME_UPLOAD_KEY );

			if ( StringUtils.isEmpty( uploadedFilename ) ) {
				log.error( "'filename' query parameter is not sent or is empty" );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			if ( StringUtils.isEmpty( fileIndexString ) ) {
				log.error( "'file_index' query parameter is not sent or is empty" );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			try {
				fileIndex = Integer.parseInt( fileIndexString );
			} catch (Exception e ) {
				log.error( "'file_index' query parameter is not an integer: " + projectIdString );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			if ( StringUtils.isEmpty( fileTypeString ) ) {
				log.error( "'file_type' query parameter is not sent or is empty" );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			int fileTypeInt = -1;
			try {
				fileTypeInt = Integer.parseInt( fileTypeString );
			} catch (Exception e ) {
				log.error( "'file_type' query parameter is not a valid value: " + fileTypeString );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			try {
				fileType = ProxlXMLFileImportFileType.fromValue( fileTypeInt );
			} catch (Exception e ) {
				log.error( "'file_type' query parameter is not a valid value: " + fileTypeString );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			if ( fileType == ProxlXMLFileImportFileType.SCAN_FILE 
					&& ( ! IsScanFileImportAllowedViaWebSubmit.getInstance().isScanFileImportAllowedViaWebSubmit() ) ) {
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
				importFileServletResponse.setStatusSuccess(false);
				importFileServletResponse.scanFileNotAllowed = true;

				OutputStream responseOutputStream = response.getOutputStream();
				// send the JSON response 
				ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
				mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
				responseOutputStream.flush();
				responseOutputStream.close();
				throw new FailResponseSentException();
			}
			if ( StringUtils.isEmpty( projectIdString ) ) {
				log.error( "'project_id' query parameter is not sent or is empty" );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			try {
				projectId = Integer.parseInt( projectIdString );
			} catch (Exception e ) {
				log.error( "'project_id' query parameter is not an integer: " + projectIdString );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			if ( StringUtils.isEmpty( uploadKeyString ) ) {
				log.error( "'upload_key' query parameter is not sent or is empty" );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			try {
				uploadKey = Long.parseLong( uploadKeyString );
			} catch (Exception e ) {
				log.error( "'upload_key' query parameter is not an integer: " + projectIdString );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			if ( fileType == ProxlXMLFileImportFileType.SCAN_FILE ) {
				//  TODO  Only allow scan files if configured to allow upload scan files
				//  if not allow scan files, throw exception here with 400 error
			}
			if ( fileType == ProxlXMLFileImportFileType.PROXL_XML_FILE ) {
				maxFileSize = ProxlXMLFileUploadMaxFileSizeConstants.MAX_PROXL_XML_FILE_UPLOAD_SIZE;
				maxFileSizeFormatted = ProxlXMLFileUploadMaxFileSizeConstants.MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED;
			} else if ( fileType == ProxlXMLFileImportFileType.SCAN_FILE ) {
				maxFileSize = ProxlXMLFileUploadMaxFileSizeConstants.MAX_SCAN_FILE_UPLOAD_SIZE;
				maxFileSizeFormatted = ProxlXMLFileUploadMaxFileSizeConstants.MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED;
			} else {
				String msg = "Unknown value for fileType: " + fileType;
				log.error( msg );
				throw new ProxlWebappFileUploadFileSystemException( msg );
			}
			
			//  Validate maxFileSize against reported upload size in header
			
			long uploadFileSizeInHeader = request.getContentLengthLong();

			if ( uploadFileSizeInHeader > maxFileSize ) {
				//  Return Error -  Status Code 400
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
				importFileServletResponse.setStatusSuccess(false);
				importFileServletResponse.setFileSizeLimitExceeded( true );
				importFileServletResponse.setMaxSize( maxFileSize );
				importFileServletResponse.setMaxSizeFormatted( maxFileSizeFormatted );
				OutputStream responseOutputStream = response.getOutputStream();
				// send the JSON response 
				ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
				mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
				responseOutputStream.flush();
				responseOutputStream.close();
				throw new FailResponseSentException();
			}
			
			
			///////  Add additional checking of the filename for scan files
			if ( fileType == ProxlXMLFileImportFileType.SCAN_FILE ) {
				if ( uploadedFilename.endsWith( ProxlXMLFileUploadWebConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) ) {
					scanFileSuffix = ProxlXMLFileUploadWebConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML;
				} else if ( uploadedFilename.endsWith( ProxlXMLFileUploadWebConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) {
					scanFileSuffix = ProxlXMLFileUploadWebConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML;
				} else {
					response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
					JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
					importFileServletResponse.setStatusSuccess(false);
					importFileServletResponse.scanFilenameSuffixNotValid = true;

					OutputStream responseOutputStream = response.getOutputStream();
					// send the JSON response 
					ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
					mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
					responseOutputStream.flush();
					responseOutputStream.close();
					throw new FailResponseSentException();
				}
			}
			//  Confirm projectId is in database
			Integer authShareableObjectId =	ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			if ( authShareableObjectId == null ) {
				// should never happen
				String msg = "Project id is not in database: " + projectId;
				log.error( msg );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				response.setStatus( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE.getStatusCode() );
				response.setContentType( "text" );
				response.getWriter().print( WebServiceErrorMessageConstants.NO_SESSION_TEXT );
				throw new FailResponseSentException();
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				response.setStatus( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE.getStatusCode() );
				response.setContentType( "text" );
				response.getWriter().print( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT );
				throw new FailResponseSentException();
			}
			int authUserId = userSession.getAuthUserId();
			//  Confirm projectId is in database
			ProjectDTO projectDTO =	ProjectDAO.getInstance().getProjectDTOForProjectId( projectId );
			if ( projectDTO == null ) {
				// should never happen
				String msg = "Project id is not in database: " + projectId;
				log.error( msg );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			if ( ( ! projectDTO.isEnabled() ) || ( projectDTO.isMarkedForDeletion() ) ) {
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			if ( ( projectDTO.isProjectLocked() ) ) {
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
				importFileServletResponse.setStatusSuccess(false);
				importFileServletResponse.setProjectLocked(true);
				OutputStream responseOutputStream = response.getOutputStream();
				// send the JSON response 
				ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
				mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
				responseOutputStream.flush();
				responseOutputStream.close();
				throw new FailResponseSentException();
			}
			File importer_Work_Directory = Proxl_XML_ImporterWrkDirAndSbDrsCmmn.getInstance().get_Proxl_XML_Importer_Work_Directory();
			//  Get the File object for the Base Subdir used to first store the files in this request 
			String uploadFileTempDirString =
					Proxl_XML_Importer_Work_Directory_And_SubDirs_Web.getInstance().getDirForUploadFileTempDir();
			File uploadFileTempDir = new File( importer_Work_Directory, uploadFileTempDirString );
			if ( ! uploadFileTempDir.exists() ) {
				String msg = "uploadFileTempDir does not exist.  uploadFileTempDir: " 
						+ uploadFileTempDir.getAbsolutePath();
				log.error( msg );
				throw new ProxlWebappFileUploadFileSystemException(msg);
			}
			uploadFileTempSubDirForThisRequestFileObj =
					Proxl_XML_Importer_Work_Directory_And_SubDirs_Web.getInstance()
					.getSubDirForUploadFileTempDir( authUserId, uploadKey, uploadFileTempDir );
			if ( ! uploadFileTempSubDirForThisRequestFileObj.exists() ) {
				String msg = "uploadFileTempSubDirForThisRequestFileObj does not exist exists: " 
						+ uploadFileTempSubDirForThisRequestFileObj.getCanonicalPath();
				log.warn( msg );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
				importFileServletResponse.setStatusSuccess(false);
				importFileServletResponse.setUploadKeyNotValid( true );
				OutputStream responseOutputStream = response.getOutputStream();
				// send the JSON response 
				ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
				mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
				responseOutputStream.flush();
				responseOutputStream.close();
				throw new FailResponseSentException();
			}
			///   Create matching "data" file for uploaded file with data about the file.
			File dataFile = 
					Proxl_XML_Importer_Work_Directory_And_SubDirs_Web.getInstance()
					.getDataFile( fileIndex, uploadFileTempSubDirForThisRequestFileObj );
			if ( dataFile.exists() ) {
				String msg = "dataFile already exists: " + dataFile.getCanonicalPath();
				log.warn( msg );
				throw new ProxlWebappFileUploadFileSystemException(msg);
			}
			
			//  File object to write incoming file to:  uploadedFileOnDisk
			//   Create the filename that the uploaded file will be saved as
			uploadedFileOnDisk = 
					Proxl_XML_Importer_Work_Directory_And_SubDirs_Web.getInstance()
					.getUploadFile( scanFileSuffix, fileIndex, fileType, uploadFileTempSubDirForThisRequestFileObj );
			String uploadedFileOnDiskFilename = uploadedFileOnDisk.getName();
			

			//  Copy InputStream containing POST body into file on disk
			{
				long totalBytesCopied = 0;
				boolean fileTooLarge = false;
				
				try ( InputStream inputStreamFromPOSTLocal = request.getInputStream() ) {

					try ( FileOutputStream fos = new FileOutputStream( uploadedFileOnDisk )) {
						byte[] buf = new byte[ COPY_FILE_ARRAY_SIZE ];
						int len;
						while ((len = inputStreamFromPOSTLocal.read(buf)) != -1){
							if ( len > 0 ) {
								fos.write(buf, 0, len);
								totalBytesCopied += len;
								if ( totalBytesCopied > maxFileSize ) {
	
									fileTooLarge = true;
									break;
								}
							}
						}
					}
				}
				if ( fileTooLarge ) {
					
					if ( ! uploadedFileOnDisk.delete() ) {
						log.warn("Failed to delete upload file that is canceled since too large.  file: " + uploadedFileOnDisk.getAbsolutePath() );
					}
					//  Return Error -  Status Code 400
					response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
					JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
					importFileServletResponse.setStatusSuccess(false);
					importFileServletResponse.setFileSizeLimitExceeded( true );
					importFileServletResponse.setMaxSize( maxFileSize );
					importFileServletResponse.setMaxSizeFormatted( maxFileSizeFormatted );
					OutputStream responseOutputStream = response.getOutputStream();
					// send the JSON response 
					ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
					mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
					responseOutputStream.flush();
					responseOutputStream.close();
					throw new FailResponseSentException();
				}
			}
			
			//  After have file uploaded:
			
			String searchNameInProxlXMLFile = null;
			if ( fileType == ProxlXMLFileImportFileType.PROXL_XML_FILE ) {
				//////   Validate that the user uploaded a Proxl XML file and get the "name" attr on root element
				try {
					//  Throws exception if not valid
					//  It will return null if there is no "name" attr on root element
					searchNameInProxlXMLFile = 
							Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile.getInstance()
							.minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile( uploadedFileOnDisk );
				} catch ( ProxlWebappDataException e ) {
					response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
					JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
					importFileServletResponse.setStatusSuccess(false);
					importFileServletResponse.proxlXMLFilerootXMLNodeIncorrect = true;
					//				private boolean proxlXMLFileFailsInitialParse;
					//				private boolean proxlXMLFilerootXMLNodeIncorrect;
					OutputStream responseOutputStream = response.getOutputStream();
					// send the JSON response 
					ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
					mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
					responseOutputStream.flush();
					responseOutputStream.close();
					throw new FailResponseSentException();
				} catch ( Exception e ) {
					response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
					JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
					importFileServletResponse.setStatusSuccess(false);
					importFileServletResponse.proxlXMLFileFailsInitialParse = true;
					OutputStream responseOutputStream = response.getOutputStream();
					// send the JSON response 
					ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
					mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
					responseOutputStream.flush();
					responseOutputStream.close();
					throw new FailResponseSentException();
				}
			} else if ( fileType == ProxlXMLFileImportFileType.SCAN_FILE ) {
				//////   Validate that the user uploaded a mzML or mzXML file 
//				try {
//
//					//  Throws exception if not valid
//					
//					//  TODO  Need to code Scan File Validator
//
////					processUploadedScanFile_Validate( uploadedFileOnDisk );
//
//				} catch ( ProxlWebappDataException e ) {
//
//
//					response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
//
//
//					JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
//
//					importFileServletResponse.setStatusSuccess(false);
//
//					importFileServletResponse.scanFilerootXMLNodeIncorrect = true;
//
//					OutputStream responseOutputStream = response.getOutputStream();
//
//
//					// send the JSON response 
//					ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
//					mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
//
//					responseOutputStream.flush();
//					responseOutputStream.close();
//
//					throw new FailResponseSentException();
//
//				} catch ( Exception e ) {
//
//
//					response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
//
//
//					JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
//
//					importFileServletResponse.setStatusSuccess(false);
//
//					importFileServletResponse.proxlXMLFileFailsInitialParse = true;
//
//					OutputStream responseOutputStream = response.getOutputStream();
//
//
//					// send the JSON response 
//					ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
//					mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
//
//					responseOutputStream.flush();
//					responseOutputStream.close();
//
//					throw new FailResponseSentException();
//
//				}
			} else {
				String msg = "Unknown value for fileType: " + fileType;
				log.error( msg );
				throw new ProxlWebappFileUploadFileSystemException( msg );
			}
			///   Create matching "data" file for uploaded file with data about the file.
			ProxlUploadTempDataFileContents proxlUploadTempDataFileContents = new ProxlUploadTempDataFileContents();
			proxlUploadTempDataFileContents.setUploadedFilename( uploadedFilename );
			proxlUploadTempDataFileContents.setSavedToDiskFilename( uploadedFileOnDiskFilename );
			proxlUploadTempDataFileContents.setFileIndex( fileIndex );
			proxlUploadTempDataFileContents.setFileType( fileType );
			proxlUploadTempDataFileContents.setSearchNameInProxlXMLFile( searchNameInProxlXMLFile );
			proxlUploadTempDataFileContents.setCanonicalFilename_W_Path_OnSubmitMachine( canonicalFilename_W_Path_OnSubmitMachine );
			proxlUploadTempDataFileContents.setAbsoluteFilename_W_Path_OnSubmitMachine( absoluteFilename_W_Path_OnSubmitMachine );
			//  Marshal (write) the object to the file
			JAXBContext jaxbContext = JAXBContext.newInstance( ProxlUploadTempDataFileContents.class );
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			OutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream( dataFile );
				marshaller.marshal( proxlUploadTempDataFileContents, outputStream );
			} catch ( Exception e ) {
				throw e;
			} finally {
				if ( outputStream != null ) {
					outputStream.close();
				}
			}
			JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
			importFileServletResponse.setStatusSuccess(true);
			OutputStream responseOutputStream = response.getOutputStream();
			// send the JSON response 
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
			log.info( "Completed Uploads");
		} catch ( FailResponseSentException e ) {
			//  No longer delete dir
//			cleanupOnError( uploadFileTempSubDirForThisRequestFileObj );
		} catch (Throwable ex){
			log.error( "Exception: " + ex.toString(), ex );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
			JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
			importFileServletResponse.setStatusSuccess(false);
			OutputStream responseOutputStream = response.getOutputStream();
			// send the JSON response 
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
			responseOutputStream.flush();
			responseOutputStream.close();
			//  No longer delete dir
//			cleanupOnError( uploadFileTempSubDirForThisRequestFileObj );
		}

		return null;
	}
	
	/**
	 * 
	 *
	 */
	private static class FailResponseSentException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * 
	 *
	 */
	private static class JSON_Servlet_Response_Object {
		private boolean statusSuccess;
		//  These are populated for FileSizeLimitExceededException exception
		private boolean fileSizeLimitExceeded;
		private long maxSize;
		private String maxSizeFormatted;
		private boolean uploadFile_fieldNameInvalid;
		private boolean uploadKeyNotValid;
		private boolean proxlXMLFileFailsInitialParse;
		private boolean proxlXMLFilerootXMLNodeIncorrect;
		private boolean scanFileNotAllowed;
		private boolean scanFilenameSuffixNotValid;
		private boolean scanFileFailsInitialParse;
		private boolean scanFilerootXMLNodeIncorrect;
		private boolean projectLocked; 
		@SuppressWarnings("unused")
		public boolean isStatusSuccess() {
			return statusSuccess;
		}
		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}
		@SuppressWarnings("unused")
		public boolean isFileSizeLimitExceeded() {
			return fileSizeLimitExceeded;
		}
		public void setFileSizeLimitExceeded(boolean fileSizeLimitExceeded) {
			this.fileSizeLimitExceeded = fileSizeLimitExceeded;
		}
		@SuppressWarnings("unused")
		public long getMaxSize() {
			return maxSize;
		}
		public void setMaxSize(long maxSize) {
			this.maxSize = maxSize;
		}
		@SuppressWarnings("unused")
		public String getMaxSizeFormatted() {
			return maxSizeFormatted;
		}
		public void setMaxSizeFormatted(String maxSizeFormatted) {
			this.maxSizeFormatted = maxSizeFormatted;
		}
		@SuppressWarnings("unused")
		public boolean isUploadFile_fieldNameInvalid() {
			return uploadFile_fieldNameInvalid;
		}
		@SuppressWarnings("unused")
		public boolean isProjectLocked() {
			return projectLocked;
		}
		public void setProjectLocked(boolean projectLocked) {
			this.projectLocked = projectLocked;
		}
		@SuppressWarnings("unused")
		public boolean isProxlXMLFileFailsInitialParse() {
			return proxlXMLFileFailsInitialParse;
		}
		@SuppressWarnings("unused")
		public void setProxlXMLFileFailsInitialParse(
				boolean proxlXMLFileFailsInitialParse) {
			this.proxlXMLFileFailsInitialParse = proxlXMLFileFailsInitialParse;
		}
		@SuppressWarnings("unused")
		public boolean isProxlXMLFilerootXMLNodeIncorrect() {
			return proxlXMLFilerootXMLNodeIncorrect;
		}
		@SuppressWarnings("unused")
		public void setProxlXMLFilerootXMLNodeIncorrect(
				boolean proxlXMLFilerootXMLNodeIncorrect) {
			this.proxlXMLFilerootXMLNodeIncorrect = proxlXMLFilerootXMLNodeIncorrect;
		}
		@SuppressWarnings("unused")
		public boolean isUploadKeyNotValid() {
			return uploadKeyNotValid;
		}
		public void setUploadKeyNotValid(boolean uploadKeyNotValid) {
			this.uploadKeyNotValid = uploadKeyNotValid;
		}
		@SuppressWarnings("unused")
		public boolean isScanFilenameSuffixNotValid() {
			return scanFilenameSuffixNotValid;
		}
		@SuppressWarnings("unused")
		public void setScanFilenameSuffixNotValid(boolean scanFilenameSuffixNotValid) {
			this.scanFilenameSuffixNotValid = scanFilenameSuffixNotValid;
		}
		@SuppressWarnings("unused")
		public boolean isScanFileFailsInitialParse() {
			return scanFileFailsInitialParse;
		}
		@SuppressWarnings("unused")
		public void setScanFileFailsInitialParse(boolean scanFileFailsInitialParse) {
			this.scanFileFailsInitialParse = scanFileFailsInitialParse;
		}
		@SuppressWarnings("unused")
		public boolean isScanFilerootXMLNodeIncorrect() {
			return scanFilerootXMLNodeIncorrect;
		}
		@SuppressWarnings("unused")
		public void setScanFilerootXMLNodeIncorrect(boolean scanFilerootXMLNodeIncorrect) {
			this.scanFilerootXMLNodeIncorrect = scanFilerootXMLNodeIncorrect;
		}
		@SuppressWarnings("unused")
		public boolean isScanFileNotAllowed() {
			return scanFileNotAllowed;
		}
		@SuppressWarnings("unused")
		public void setScanFileNotAllowed(boolean scanFileNotAllowed) {
			this.scanFileNotAllowed = scanFileNotAllowed;
		}
	}
}
