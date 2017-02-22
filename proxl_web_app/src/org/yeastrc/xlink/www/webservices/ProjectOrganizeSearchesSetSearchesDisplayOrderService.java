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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.database_update_with_transaction_services.UpdateSearchDisplayOrderUsingDBTransactionService;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;

@Path("/project")
public class ProjectOrganizeSearchesSetSearchesDisplayOrderService {
	
	private static final Logger log = Logger.getLogger(ProjectOrganizeSearchesSetSearchesDisplayOrderService.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/organizeSearchesSetSearchesOrder")
	public ProjectSetSearchesDisplayOrderResult setSearchesOrder( ProjectSetSearchesDisplayOrderRequest projectSetSearchesOrderRequest,
			@Context HttpServletRequest request ) throws Exception {
		try {
			int[] projectSearchesIdsInOrder = projectSetSearchesOrderRequest.searchesInOrder;
			if ( projectSearchesIdsInOrder == null ) {
				String msg = "Provided searchesInOrder is null";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( projectSearchesIdsInOrder.length == 0 ) {
				String msg = "Provided searchesInOrder is empty";
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
			for( int projectSearchId : projectSearchesIdsInOrder ) {
				Integer projectIdFromDB = SearchDAO.getInstance().getProjectIdFromProjectSearchId( projectSearchId );
				if ( projectIdFromDB == null ) {
					String msg = "Search not found in DB for projectSearchId: " + projectSearchId;
					log.error( msg );
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				if ( projectId == null ) {
					projectId = projectIdFromDB;
				} else {
					if ( projectId != projectIdFromDB ) {
						//  Invalid request, searches not in from or to project
						String msg = "project_search records don't all have the same project id, search_id: " + projectSearchId;
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
			ProjectSetSearchesDisplayOrderResult webserviceResult = new ProjectSetSearchesDisplayOrderResult();
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
			UpdateSearchDisplayOrderUsingDBTransactionService.getInstance()
			.updateSearchDisplayOrder( projectSearchesIdsInOrder );
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
	public static class ProjectSetSearchesDisplayOrderRequest {
		private int[] searchesInOrder;
		public int[] getSearchesInOrder() {
			return searchesInOrder;
		}
		public void setSearchesInOrder(int[] searchesInOrder) {
			this.searchesInOrder = searchesInOrder;
		}
	}
	
	public static class ProjectSetSearchesDisplayOrderResult {
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
