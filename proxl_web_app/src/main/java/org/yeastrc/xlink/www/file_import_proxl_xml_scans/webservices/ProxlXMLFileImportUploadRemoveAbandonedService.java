package org.yeastrc.xlink.www.file_import_proxl_xml_scans.webservices;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.utils.Proxl_XML_ImporterWrkDirAndSbDrsCmmn;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.DeleteDirectoryAndContentsUtil;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.IsProxlXMLFileImportFullyConfigured;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.Proxl_XML_Importer_Work_Directory_And_SubDirs_Web;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

/**
 * Called when user closes the upload overlay after uploading files
 *
 */
@Path("/file_import_proxl_xml_scans")
public class ProxlXMLFileImportUploadRemoveAbandonedService {
	
	private static final Logger log = LoggerFactory.getLogger( ProxlXMLFileImportUploadRemoveAbandonedService.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/removeAbandonedUploadKey")
	public RemoveAbandonedResponse  removeAbandonedUploadKey( 
			RemoveAbandonedRequest uploadSubmitRequest ,
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
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			int authUserId = userSession.getAuthUserId();
			File importer_Work_Directory = Proxl_XML_ImporterWrkDirAndSbDrsCmmn.getInstance().get_Proxl_XML_Importer_Work_Directory();
			//  Get the File object for the Base Subdir used to temporarily store the files in this request 
			String uploadFileTempDirString =
					Proxl_XML_Importer_Work_Directory_And_SubDirs_Web.getInstance().getDirForUploadFileTempDir();
			File uploadFileTempDir = new File( importer_Work_Directory, uploadFileTempDirString );
			if ( ! uploadFileTempDir.exists() ) {
				String msg = "uploadFileTempDir does not exist.  uploadFileTempDir: " 
						+ uploadFileTempDir.getAbsolutePath();
				log.warn( msg );
//				throw new ProxlWebappFileUploadFileSystemException(msg);
				RemoveAbandonedResponse removeAbandonedResponse = new RemoveAbandonedResponse();
				return removeAbandonedResponse;
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
				RemoveAbandonedResponse removeAbandonedResponse = new RemoveAbandonedResponse();
				return removeAbandonedResponse;
			}
			//  Remove the subdir the uploaded file(s) were in
			DeleteDirectoryAndContentsUtil.getInstance().deleteDirectoryAndContents( tempSubdir );
			RemoveAbandonedResponse removeAbandonedResponse = new RemoveAbandonedResponse();
			removeAbandonedResponse.statusSuccess = true;
			return removeAbandonedResponse;
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
	 * 
	 *
	 */
	public static class RemoveAbandonedRequest {
		Integer projectId;
		String uploadKey;
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
	}
	
	/**
	 * 
	 *
	 */
	public static class RemoveAbandonedResponse {
		private boolean statusSuccess;
		public boolean isStatusSuccess() {
			return statusSuccess;
		}
		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}
	}
}
