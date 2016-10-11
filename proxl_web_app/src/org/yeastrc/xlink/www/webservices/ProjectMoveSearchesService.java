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
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.database_update_with_transaction_services.UpdateSearchProjectIdUsingDBTransactionService;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MoveSearchesRequest;
import org.yeastrc.xlink.www.objects.MoveSearchesResult;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;

@Path("/project")
public class ProjectMoveSearchesService {

	private static final Logger log = Logger.getLogger(ProjectMoveSearchesService.class);
	
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/moveSearches")
	public MoveSearchesResult moveSearches( MoveSearchesRequest moveSearchesRequest,
			@Context HttpServletRequest request ) throws Exception {


		MoveSearchesResult moveSearchesResult = new MoveSearchesResult();

		try {

			if ( moveSearchesRequest.getProjectId() == null ) {

				String msg = "Provided projectId is null";

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			
			if ( moveSearchesRequest.getMoveToProjectId() == null ) {

				String msg = "Provided moveToProjectId is null";

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			
			if ( moveSearchesRequest.getSearchesToMoveToOtherProject() == null ) {

				String msg = "Provided searchesToMoveToOtherProject is null";

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			
			int projectId = moveSearchesRequest.getProjectId();
			int moveToProjectId = moveSearchesRequest.getMoveToProjectId();
			int[] searchesToMoveToOtherProject = moveSearchesRequest.getSearchesToMoveToOtherProject();

			
			if ( projectId == 0 ) {

				String msg = "Provided projectId is 0, is = " + projectId;

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			
			if ( moveToProjectId == 0 ) {

				String msg = "Provided moveToProjectId is 0, is = " + moveToProjectId;

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			if ( searchesToMoveToOtherProject.length == 0 ) {

				String msg = "Provided searchesToMoveToOtherProject is empty";

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

			AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, projectId );

			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			AuthAccessLevel authAccessLevelMoveToProject = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, moveToProjectId );

			if ( ! authAccessLevelMoveToProject.isProjectOwnerAllowed() ) {

				//  No Access Allowed for move to project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			for( int searchId : searchesToMoveToOtherProject ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {
					
					String msg = "Search not found in DB for searchId: " + searchId;
					
					log.error( msg );

					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				
				if ( projectId != search.getProjectId() && moveToProjectId != search.getProjectId() ) {

					//  Invalid request, searches not in from or to project

					String msg = "search is not in project, search_id: " + search.getId();

					log.error( msg );

					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg )
							.build()
							);
					
				}
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
			
			
			ProjectDTO projectDTOmoveToProjectId = projectDAO.getProjectDTOForProjectId( moveToProjectId );
			
			if ( projectDTOmoveToProjectId == null ) {

				log.warn( "moveToProjectId is not in database: " + moveToProjectId );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			
			if ( projectDTOmoveToProjectId.isMarkedForDeletion() ) {
				
				moveSearchesResult.setMoveToProjectMarkedForDeletion(true);
				moveSearchesResult.setStatus(false);
				
			} else if ( ! projectDTOmoveToProjectId.isEnabled() ) {
				
				moveSearchesResult.setMoveToProjectDisabled(true);
				moveSearchesResult.setStatus(false);
				
			} else {
			
				UpdateSearchProjectIdUsingDBTransactionService.getInstance().updateProjectIdForExistingProjectId( searchesToMoveToOtherProject, moveToProjectId );

				moveSearchesResult.setStatus(true);
			}

			return moveSearchesResult;
			
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
	
		
	
}
