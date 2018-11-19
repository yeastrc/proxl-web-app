package org.yeastrc.xlink.www.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.FolderForProjectDAO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.database_update_with_transaction_services.DeleteFolderResetSearchDisplayOrderUsingDBTransactionService;
import org.yeastrc.xlink.www.dto.FolderForProjectDTO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;

/**
 * 
 *
 */
@Path("/project/folder")
public class ProjectFolderMaintService {
	
	private static final Logger log = Logger.getLogger(ProjectFolderMaintService.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/addFolder")
	public WebserviceResult addFolder( 
			@FormParam("project_id") Integer projectId, 
			@FormParam("folderName") String folderName,
			@Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectId == null ) {
				String msg = "'project_id' is missing";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( StringUtils.isEmpty( folderName ) ) {
				String msg = "'folderName' is empty or missing";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			// Get the session first.  
			HttpSession session = request.getSession();
			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			if ( userSessionObject == null ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			AuthAccessLevel authAccessLevel = 
					GetAuthAccessLevelForWebRequest.getInstance()
					.getAuthAccessLevelForWebRequestProjectId( userSessionObject, projectId );
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			//  END Auth Check
			//////////////////////////
			
			//  Get user
			XLinkUserDTO xLinkUserDTO = userSessionObject.getUserDBObject();
			if ( xLinkUserDTO == null ) {
				String msg = "xLinkUserDTO == null for access level isProjectOwnerAllowed().";
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			AuthUserDTO authUserDTO = xLinkUserDTO.getAuthUser();
			if ( authUserDTO == null ) {
				String msg = "authUserDTO == null for access level isProjectOwnerAllowed().";
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			int authUserId = authUserDTO.getId();

			WebserviceResult webserviceResult = new WebserviceResult();
			
			ProjectDAO projectDAO = ProjectDAO.getInstance();
			ProjectDTO projectDTO = projectDAO.getProjectDTOForProjectId( projectId );
			if ( projectDTO == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			if ( projectDTO.isProjectLocked() ) {
				webserviceResult.setProjectLocked(true);
				webserviceResult.setStatus(false);
				return webserviceResult;  //  EARLY Return
			} 
			if ( projectDTO.isMarkedForDeletion() ) {
				webserviceResult.setProjectMarkedForDeletion(true);
				webserviceResult.setStatus(false);
				return webserviceResult;  //  EARLY Return
			} 
			if ( ! projectDTO.isEnabled() ) {
				webserviceResult.setProjectDisabled(true);
				webserviceResult.setStatus(false);
				return webserviceResult;  //  EARLY Return
			}
			
			FolderForProjectDTO folderForProjectDTO = new FolderForProjectDTO();
			folderForProjectDTO.setProjectId( projectId );
			folderForProjectDTO.setName( folderName );
			FolderForProjectDAO.getInstance().save( folderForProjectDTO, authUserId );
			
			webserviceResult.setAddedFolderId( folderForProjectDTO.getId() );
			
			webserviceResult.setStatus(true);
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE ) // This string will be passed to the client
					.build()
					);
		}
	}
	

	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/deleteFolder")
	public WebserviceResult deleteFolder( 
			@FormParam("folderId") Integer folderId, 
			@Context HttpServletRequest request ) throws Exception {
		try {
			if ( folderId == null ) {
				String msg = "'folderId' is missing";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			// Get the session first.  
			HttpSession session = request.getSession();
			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			if ( userSessionObject == null ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			Integer projectId =	FolderForProjectDAO.getInstance().getProjectId_ForId( folderId );
			if ( projectId == null ) {
				String msg = "'folderId' is not in database: " + folderId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			AuthAccessLevel authAccessLevel = 
					GetAuthAccessLevelForWebRequest.getInstance()
					.getAuthAccessLevelForWebRequestProjectId( userSessionObject, projectId );
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  END Auth Check

			//  Get user
			XLinkUserDTO xLinkUserDTO = userSessionObject.getUserDBObject();
			if ( xLinkUserDTO == null ) {
				String msg = "xLinkUserDTO == null for access level isProjectOwnerAllowed().";
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			AuthUserDTO authUserDTO = xLinkUserDTO.getAuthUser();
			if ( authUserDTO == null ) {
				String msg = "authUserDTO == null for access level isProjectOwnerAllowed().";
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			
			WebserviceResult webserviceResult = new WebserviceResult();
			
			ProjectDAO projectDAO = ProjectDAO.getInstance();
			ProjectDTO projectDTO = projectDAO.getProjectDTOForProjectId( projectId );
			if ( projectDTO == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			if ( projectDTO.isProjectLocked() ) {
				webserviceResult.setProjectLocked(true);
				webserviceResult.setStatus(false);
				log.warn( "Failed attempt to delete folder from locked project.  folderId: " + folderId + ", projectId: " + projectId );
				return webserviceResult;  //  EARLY Return
			} 
			if ( projectDTO.isMarkedForDeletion() ) {
				webserviceResult.setProjectMarkedForDeletion(true);
				webserviceResult.setStatus(false);
				log.warn( "Failed attempt to delete folder from project marked for deletion.  folderId: " + folderId + ", projectId: " + projectId );
				return webserviceResult;  //  EARLY Return
			} 
			if ( ! projectDTO.isEnabled() ) {
				webserviceResult.setProjectDisabled(true);
				webserviceResult.setStatus(false);
				log.warn( "Failed attempt to delete folder from disabled project.  folderId: " + folderId + ", projectId: " + projectId );
				return webserviceResult;  //  EARLY Return
			}
			
			FolderForProjectDTO folderForProjectDTO = FolderForProjectDAO.getInstance().getFolderForProjectDTO_ForId( folderId );
			
			DeleteFolderResetSearchDisplayOrderUsingDBTransactionService.getInstance()
			.deleteFolderResetSearchDisplayOrder( folderId );
			
			log.warn( "INFO: Folder for project deleted by user. username: " + authUserDTO.getUsername() + ", userId: " + authUserDTO.getId()
					+ ", projectId: " + projectId 
					+ ", folder name: " + folderForProjectDTO.getName() );
			
			webserviceResult.setStatus(true);
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE ) // This string will be passed to the client
					.build()
					);
		}
	}
	

	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/renameFolder")
	public WebserviceResult renameFolder( 
			@FormParam("folderId") Integer folderId, 
			@FormParam("folderName") String folderName,
			@Context HttpServletRequest request ) throws Exception {
		try {
			if ( folderId == null ) {
				String msg = "'folderId' is missing";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( StringUtils.isEmpty( folderName ) ) {
				String msg = "'folderName' is empty or missing";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			// Get the session first.  
			HttpSession session = request.getSession();
			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			if ( userSessionObject == null ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			Integer projectId =	FolderForProjectDAO.getInstance().getProjectId_ForId( folderId );
			if ( projectId == null ) {
				String msg = "'folderId' is not in database: " + folderId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			AuthAccessLevel authAccessLevel = 
					GetAuthAccessLevelForWebRequest.getInstance()
					.getAuthAccessLevelForWebRequestProjectId( userSessionObject, projectId );
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  END Auth Check

			//  Get user
			XLinkUserDTO xLinkUserDTO = userSessionObject.getUserDBObject();
			if ( xLinkUserDTO == null ) {
				String msg = "xLinkUserDTO == null for access level isProjectOwnerAllowed().";
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			AuthUserDTO authUserDTO = xLinkUserDTO.getAuthUser();
			if ( authUserDTO == null ) {
				String msg = "authUserDTO == null for access level isProjectOwnerAllowed().";
				log.error( msg );
				throw new ProxlWebappInternalErrorException( msg );
			}
			int authUserId = authUserDTO.getId();

			WebserviceResult webserviceResult = new WebserviceResult();
			
			ProjectDAO projectDAO = ProjectDAO.getInstance();
			ProjectDTO projectDTO = projectDAO.getProjectDTOForProjectId( projectId );
			if ( projectDTO == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			if ( projectDTO.isProjectLocked() ) {
				webserviceResult.setProjectLocked(true);
				webserviceResult.setStatus(false);
				return webserviceResult;  //  EARLY Return
			} 
			if ( projectDTO.isMarkedForDeletion() ) {
				webserviceResult.setProjectMarkedForDeletion(true);
				webserviceResult.setStatus(false);
				return webserviceResult;  //  EARLY Return
			} 
			if ( ! projectDTO.isEnabled() ) {
				webserviceResult.setProjectDisabled(true);
				webserviceResult.setStatus(false);
				return webserviceResult;  //  EARLY Return
			}
			
			FolderForProjectDAO.getInstance().updateName( folderId, folderName, authUserId );
			
			webserviceResult.setStatus(true);
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	//////////////////////
	//  Classes for web service request and response
	public static class WebserviceResult {
		private boolean status;
		private int addedFolderId;
		private boolean projectMarkedForDeletion;
		private boolean projectDisabled;
		private boolean projectLocked;

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		public boolean isProjectMarkedForDeletion() {
			return projectMarkedForDeletion;
		}

		public void setProjectMarkedForDeletion(boolean projectMarkedForDeletion) {
			this.projectMarkedForDeletion = projectMarkedForDeletion;
		}

		public boolean isProjectDisabled() {
			return projectDisabled;
		}

		public void setProjectDisabled(boolean projectDisabled) {
			this.projectDisabled = projectDisabled;
		}

		public boolean isProjectLocked() {
			return projectLocked;
		}

		public void setProjectLocked(boolean projectLocked) {
			this.projectLocked = projectLocked;
		}

		public int getAddedFolderId() {
			return addedFolderId;
		}

		public void setAddedFolderId(int addedFolderId) {
			this.addedFolderId = addedFolderId;
		}
	}
}
