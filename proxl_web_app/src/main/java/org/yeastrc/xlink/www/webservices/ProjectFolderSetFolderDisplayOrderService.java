package org.yeastrc.xlink.www.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.FolderForProjectDAO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.database_update_with_transaction_services.UpdateProjectFolderDisplayOrderUsingDBTransactionService;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;

@Path("/project/folder")
public class ProjectFolderSetFolderDisplayOrderService {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectFolderSetFolderDisplayOrderService.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/SetFoldersOrder")
	public WebserviceResult setFoldersOrder( WebserviceRequest projectSetSearchesOrderRequest,
			@Context HttpServletRequest request ) throws Exception {
		try {
			int[] foldersInOrder = projectSetSearchesOrderRequest.foldersInOrder;
			if ( foldersInOrder == null ) {
				String msg = "Provided foldersInOrder is null";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( foldersInOrder.length == 0 ) {
				String msg = "Provided foldersInOrder is empty";
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
			Integer projectId = null;
			for( int folderId : foldersInOrder ) {
				Integer projectIdFromFolderId =	FolderForProjectDAO.getInstance().getProjectId_ForId( folderId );
				if ( projectIdFromFolderId == null ) {
					String msg = "'folderId' is not in database: " + folderId;
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}

				if ( projectId == null ) {
					projectId = projectIdFromFolderId;
				} else {
					if ( projectId != projectIdFromFolderId ) {
						//  Invalid request, searches not in from or to project
						String msg = "Folders don't all have the same project id, folderId: " + folderId;
						log.error( msg );
						throw new WebApplicationException(
								Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
								.entity( msg )
								.build()
								);
					}
				}
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
			WebserviceResult webserviceResult = new WebserviceResult();
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
			
			
			UpdateProjectFolderDisplayOrderUsingDBTransactionService.getInstance()
			.updateProjectFolderDisplayOrder( foldersInOrder );
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
	public static class WebserviceRequest {
		private int[] foldersInOrder;

		public int[] getFoldersInOrder() {
			return foldersInOrder;
		}

		public void setFoldersInOrder(int[] foldersInOrder) {
			this.foldersInOrder = foldersInOrder;
		}
	}
	
	public static class WebserviceResult {
		private boolean status;
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
	}
}
