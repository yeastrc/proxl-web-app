package org.yeastrc.xlink.www.file_import_proxl_xml_scans.servlets_as_webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.utils.Proxl_XML_ImporterWrkDirAndSbDrsCmmn;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappFileUploadFileSystemException;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadMaxFileSizeConstants;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants.ProxlXMLFileUploadWebConstants;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.objects.ProxlUploadTempDataFileContents;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.IsScanFileImportAllowedViaWebSubmit;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.Proxl_XML_Importer_Work_Directory_And_SubDirs_Web;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *  This Servlet should be considered a webservice as it returns JSON
 */
public class UploadFileForImportServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger( UploadFileForImportServlet.class );
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//  For multipart forms (which is what is passed to this servlet), 
		//  	request.getParameter(...) only comes from the query string
//		String uploadType = request.getParameter( "uploadTypeQueryString" ); 
		String scanFileSuffix = null;
		File uploadFileTempSubDirForThisRequestFileObj = null;
		File uploadedFileOnDisk = null;
		ProxlXMLFileImportFileType fileType = null;
		int fileIndex = -1;
		int projectId = -1;
		long uploadKey = -1;
		long maxFileSize = -1;
		String maxFileSizeFormatted = null;
		try {
//			String requestURL = request.getRequestURL().toString();
			String uploadedFilename = request.getParameter( "filename" );
			String fileIndexString = request.getParameter( "file_index" );
			String fileTypeString = request.getParameter( "file_type" );
			String projectIdString = request.getParameter( "project_id" );
			String uploadKeyString = request.getParameter( "upload_key" );
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
				log.error( "'file_type' query parameter is not a valid value: " + projectIdString );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				throw new FailResponseSentException();
			}
			try {
				fileType = ProxlXMLFileImportFileType.fromValue( fileTypeInt );
			} catch (Exception e ) {
				log.error( "'file_type' query parameter is not a valid value: " + projectIdString );
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
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				response.setStatus( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE.getStatusCode() );
				response.setContentType( "text" );
				response.getWriter().print( WebServiceErrorMessageConstants.NO_SESSION_TEXT );
				throw new FailResponseSentException();
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				response.setStatus( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE.getStatusCode() );
				response.setContentType( "text" );
				response.getWriter().print( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT );
				throw new FailResponseSentException();
			}
			int authUserId = userSessionObject.getUserDBObject().getAuthUser().getId();
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
			//  Create ServletFileUpload object from Apache Commons File Upload
			//  	to process the servlet request from the browser.
			//  This processes the form submitted from the browser
			//		and transfers upload files to temporary files on the server
			//		in the directory specified in the DiskFileItemFactory object
			//  DiskFileItemFactory is part of Apache Commons File Upload and is used to help copy the files in this HTTP request
			//                        to files on the local disk
			DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
//					
//					In DiskFileItemFactory, if temp directory is not specified, it uses 
//					tempDir = new File(System.getProperty("java.io.tmpdir"));
//					
//					which on one Tomcat installation is
//					/data/webtools/apache-tomcat-7.0.53/temp
			log.info( "DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD: " + DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD );
			if ( diskFileItemFactory.getRepository() == null ) {
				log.info( "diskFileItemFactory.getRepository() == null" );
			} else {
				log.info( "diskFileItemFactory.getRepository().getAbsolutePath(): '" 
						+ diskFileItemFactory.getRepository().getAbsolutePath() + "'" );
			}
			log.info( "diskFileItemFactory.getSizeThreshold(): '" 
					+ diskFileItemFactory.getSizeThreshold() + "'" );
			File diskFileItemFactoryRepository = uploadFileTempSubDirForThisRequestFileObj; // Put diskFileItemFactory temp files in subdirectory directory
			diskFileItemFactory.setRepository( diskFileItemFactoryRepository );
			ServletFileUpload servletFileUpload = new ServletFileUpload( diskFileItemFactory );
		       // file upload size limit
			servletFileUpload.setFileSizeMax( maxFileSize );
			int filesUploadedCount = 0;
			List<FileItem> fileItemListFromServletFileUpload = null;
			try {
				//  This will throw an exception if the send is aborted in the browser
				fileItemListFromServletFileUpload = servletFileUpload.parseRequest( request );
			} catch ( FileUploadException e ) {
				log.error( "FileUploadException parsing the request to get the parts in 'servletFileUpload.parseRequest( request )'", e );
				throw e;
			} catch ( Exception e ) {
				log.error( "Exception parsing the request to get the parts in 'servletFileUpload.parseRequest( request )'", e );
				throw e;
			}
			//  ServletFileUpload object from Apache Commons File Upload 
			//		is done processing the form and uploaded file(s) 
			//		have been transfered to the server
			//   Create the filename that the uploaded file will be saved as
			uploadedFileOnDisk = 
					Proxl_XML_Importer_Work_Directory_And_SubDirs_Web.getInstance()
					.getUploadFile( scanFileSuffix, fileIndex, fileType, uploadFileTempSubDirForThisRequestFileObj );
			String uploadedFileOnDiskFilename = uploadedFileOnDisk.getName();
			//  Process the parsed form and uploaded files
			boolean processedUploadedFile = false;
			//  collect form fields here.  No form fields so no variables here
			//   String aFormFieldValue = null;
			// fileItemListFromServletFileUpload object is returned from  ServletFileUpload object from Apache Commons File Upload
			log.info( "fileItemListFromServletFileUpload size " + fileItemListFromServletFileUpload.size() );
			for ( FileItem fileItem : fileItemListFromServletFileUpload ) {
			    if ( fileItem.isFormField() ) {
			    	//  No form fields since all non-file upload data is passed as query string parameters
			    	//  form field that is not a file upload
//			    	String fieldName = item.getFieldName();
//			    	String fieldValue = item.getString();
//			    	
//			    	if ( "XXXXXX".equals( fieldName) ) {
//			    		
//			    		aFormFieldValue = fieldValue;
//			    	}
			    } else {
					String fieldName = fileItem.getFieldName();
					//  Only allow the expected field name
					if ( ! ProxlXMLFileUploadWebConstants.UPLOAD_FILE_FORM_NAME.equals( fieldName ) ) {
						log.error( "File uploaded using field name other than allowed field name. " 
								+ "Allowed field name: " + ProxlXMLFileUploadWebConstants.UPLOAD_FILE_FORM_NAME
								+ ", received field name: " + fieldName );
						response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
						JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
						importFileServletResponse.setStatusSuccess(false);
						importFileServletResponse.setUploadFile_fieldNameInvalid(true);
						OutputStream responseOutputStream = response.getOutputStream();
						// send the JSON response 
						ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
						mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
						responseOutputStream.flush();
						responseOutputStream.close();
						throw new FailResponseSentException();
					}
					filesUploadedCount++;
					//  Only allow one file to be uploaded in the request
					if ( filesUploadedCount > 1 ) {
						log.error( "More than one file uploaded in the request." );
						response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
						JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
						importFileServletResponse.setStatusSuccess(false);
						importFileServletResponse.setMoreThanOneuploadedFile(true);;
						OutputStream responseOutputStream = response.getOutputStream();
						// send the JSON response 
						ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
						mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
						responseOutputStream.flush();
						responseOutputStream.close();
						throw new FailResponseSentException();
					}
				    String fileNameForFormObject = fileItem.getName();
				    @SuppressWarnings("unused")
				    String contentType = fileItem.getContentType();
				    @SuppressWarnings("unused")
				    boolean isInMemory = fileItem.isInMemory();
				    @SuppressWarnings("unused")
				    long sizeInBytes = fileItem.getSize();
				    uploadedFilename = fileNameForFormObject;  // re-assign filename to the filename from the form
					log.info( "started Upload for filename " + fileNameForFormObject );
					log.info( "item.getSize(): " + fileItem.getSize() );
					fileItem.write( uploadedFileOnDisk );
					log.info( "Completed transfer to server for user uploaded filename " + fileNameForFormObject );
					processedUploadedFile = true;
				}
			}  //  END:  for ( FileItem fileItem : fileItemListFromServletFileUpload ) {
			if ( ! processedUploadedFile ) {
				log.error( "No file uploaded." );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
				importFileServletResponse.setStatusSuccess(false);
				importFileServletResponse.setNoUploadedFile(true);
				OutputStream responseOutputStream = response.getOutputStream();
				// send the JSON response 
				ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
				mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
				responseOutputStream.flush();
				responseOutputStream.close();
				throw new FailResponseSentException();
			}
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
		} catch (FileSizeLimitExceededException ex ) {
			ex.getActualSize();
			ex.getPermittedSize();
			log.error( "SizeLimitExceededException: " + ex.toString(), ex );
			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
			JSON_Servlet_Response_Object importFileServletResponse = new JSON_Servlet_Response_Object();
			importFileServletResponse.setStatusSuccess(false);
			importFileServletResponse.setFileSizeLimitExceeded(true);
			importFileServletResponse.setMaxSize( ex.getPermittedSize() );
			importFileServletResponse.setMaxSizeFormatted( maxFileSizeFormatted );
			OutputStream responseOutputStream = response.getOutputStream();
			// send the JSON response 
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			mapper.writeValue( responseOutputStream, importFileServletResponse ); // where first param can be File, OutputStream or Writer
			responseOutputStream.flush();
			responseOutputStream.close();
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
		//  Example for forwarding to a JSP
//        
//        //File uploaded successfully
//        request.setAttribute("message", "File Uploaded Successfully");
//     } catch (Exception ex) {
//        request.setAttribute("message", "File Upload Failed due to " + ex);
//     }          
//  
// }else{
//     request.setAttribute("message",
//                          "Sorry this Servlet only handles file upload request");
// }
//
// request.getRequestDispatcher("/result.jsp").forward(request, response);
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
		private boolean moreThanOneuploadedFile;
		private boolean filenameInFormNotMatchFilenameInQueryString;
		private boolean noUploadedFile;
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
		public void setUploadFile_fieldNameInvalid(boolean uploadFile_fieldNameInvalid) {
			this.uploadFile_fieldNameInvalid = uploadFile_fieldNameInvalid;
		}
		@SuppressWarnings("unused")
		public boolean isMoreThanOneuploadedFile() {
			return moreThanOneuploadedFile;
		}
		public void setMoreThanOneuploadedFile(boolean moreThanOneuploadedFile) {
			this.moreThanOneuploadedFile = moreThanOneuploadedFile;
		}
		@SuppressWarnings("unused")
		public boolean isFilenameInFormNotMatchFilenameInQueryString() {
			return filenameInFormNotMatchFilenameInQueryString;
		}
		@SuppressWarnings("unused")
		public void setFilenameInFormNotMatchFilenameInQueryString(
				boolean filenameInFormNotMatchFilenameInQueryString) {
			this.filenameInFormNotMatchFilenameInQueryString = filenameInFormNotMatchFilenameInQueryString;
		}
		@SuppressWarnings("unused")
		public boolean isNoUploadedFile() {
			return noUploadedFile;
		}
		public void setNoUploadedFile(boolean noUploadedFile) {
			this.noUploadedFile = noUploadedFile;
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
