package org.yeastrc.xlink.www.webservices;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.FolderProjectSearchDAO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.FolderProjectSearchDTO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;

/**
 * 
 *
 */
@Path("/project")
public class ProjectOrganizeSearchesSetSearchFolderService {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectOrganizeSearchesSetSearchFolderService.class);
	
	private static final String TRUE = "true";
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/organizeSearchesSetSearchFolder")
	public WebserviceResult setSearchesOrder( 
			@FormParam("projectSearchId") Integer projectSearchId, 
			@FormParam("folderId") Integer folderId,
			@FormParam("searches_not_in_any_folder") String searches_not_in_any_folderString,
			@Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectSearchId == null ) {
				String msg = "'projectSearchId' is missing";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			boolean searches_not_in_any_folder = false;
			if ( TRUE.equals( searches_not_in_any_folderString ) ) {
				searches_not_in_any_folder = true;
			}
			if ( folderId == null && ( ! searches_not_in_any_folder ) ) {
				String msg = "'folderId' is null and searches_not_in_any_folder is false";
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
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchId: " + projectSearchId;
				log.warn( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
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
			
			if ( searches_not_in_any_folder ) {
				//  Remove project_search_id from folder
				FolderProjectSearchDAO.getInstance().delete( projectSearchId );
			} else {
				//  Add or move project_search_id to folder
				FolderProjectSearchDTO folderProjectSearchDTO = new FolderProjectSearchDTO();
				folderProjectSearchDTO.setProjectSearchId( projectSearchId );
				folderProjectSearchDTO.setFolderId( folderId );
				FolderProjectSearchDAO.getInstance().saveOrUpdate( folderProjectSearchDTO );
			}
			
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
