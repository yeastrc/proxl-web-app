package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.database_update_with_transaction_services.CopyProjectSearchIdToNewProjectUsingDBTransactionService;
import org.yeastrc.xlink.www.database_update_with_transaction_services.MoveProjectSearchIdToNewProjectUsingDBTransactionService;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProjectToCopyToResultItem;
import org.yeastrc.xlink.www.searcher.ProjectSearchIdAssocSearchIdInProjectIdSearcher;
import org.yeastrc.xlink.www.searcher.ProjectToCopyToSearcher;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;

/**
 * 
 *
 */
@Path("/project")
public class ProjectCopySearchesService {
	
	private static final Logger log = Logger.getLogger(ProjectCopySearchesService.class);
	
	/**
	 * @param projectId
	 * @param projectSearchIdBeingCopiedList
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listOtherProjectsForProjectIdExcludingProjectSearchIds") 
	public GetOtherProjectsResult getOtherProjects( 
			@QueryParam( "projectId" ) Integer projectId,
			@QueryParam( "projectSearchIdBeingCopied" ) List<Integer> projectSearchIdBeingCopiedList,
			@Context HttpServletRequest request )
	throws Exception {
		
		if ( projectId == null ) {
			String msg = ": Provided projectId null or missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( projectId == 0 ) {
			String msg = ": Provided projectId is zero";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( projectSearchIdBeingCopiedList == null ) {
			String msg = ": Provided projectSearchIdBeingCopied null or missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( projectSearchIdBeingCopiedList.isEmpty() ) {
			String msg = ": Provided projectSearchIdBeingCopied is empty";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
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

			////////   Auth complete
			//////////////////////////////////////////
			
			//  get other projects this user has rights to
			List<ProjectToCopyToResultItem>  projectToCopyToResultItemList = null;
			if ( authAccessLevel.isAdminAllowed() ) {
				// Get all projects excluding current one
				projectToCopyToResultItemList = 
						ProjectToCopyToSearcher.getInstance().getAllExcludingProjectId( projectId );
			} else {
				//  Get projects this user is owner for
				int authUserId = userSessionObject.getUserDBObject().getAuthUser().getId();
				int maxAuthLevel = AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER;
				projectToCopyToResultItemList = 
						ProjectToCopyToSearcher.getInstance()
						.getForAuthUserExcludingProjectId( authUserId, maxAuthLevel, projectId );
			}
			//  Next remove projects where all the search ids are already in that project and not marked for deletion
			
			List<ProjectToCopyToResultItem> projectToCopyToResultItemListAfterRemovingProjects = new ArrayList<>( projectToCopyToResultItemList.size() );
			for ( ProjectToCopyToResultItem projectToCopyToResultItem : projectToCopyToResultItemList ) {

				boolean foundProjectSearchIdBeingCopiedNotInDestinationProject = false;
				for ( Integer projectSearchIdBeingCopied : projectSearchIdBeingCopiedList ) {
				
					if ( ! ProjectSearchIdAssocSearchIdInProjectIdSearcher.getInstance()
							.isSearchIdAssocWithProjectSearchIdInProjectId(
									projectSearchIdBeingCopied, projectToCopyToResultItem.getProjectId() ) ) {
						foundProjectSearchIdBeingCopiedNotInDestinationProject = true;
						break;
					}
				}
				if ( foundProjectSearchIdBeingCopiedNotInDestinationProject ) {
					projectToCopyToResultItemListAfterRemovingProjects.add(projectToCopyToResultItem);
				}
			}
			
			//  Create webservice result object
			
			GetOtherProjectsResult getOtherProjectsResult = new GetOtherProjectsResult();
			
			List<GetOtherProjectsItem> otherProjects = new ArrayList<>( projectToCopyToResultItemListAfterRemovingProjects.size() );
			getOtherProjectsResult.status = true;
			getOtherProjectsResult.otherProjects = otherProjects;
			
			for ( ProjectToCopyToResultItem item :  projectToCopyToResultItemListAfterRemovingProjects ) {
				GetOtherProjectsItem getOtherProjectsItem = new GetOtherProjectsItem();
				getOtherProjectsItem.setProjectId( item.getProjectId() );
				getOtherProjectsItem.setProjectTitle( item.getProjectTitle() );
				otherProjects.add( getOtherProjectsItem );
			}
			
			return getOtherProjectsResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
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
	 * @param projectId "To Project"
	 * @param projectSearchids - from "From Project"
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listProjectSearchIdsWhereSearchIdIsInProject") 
	public ListProjectSearchIdsWhereSearchIdIsInProjectResult listProjectSearchIdsWhereSearchIdIsInProject( 
			@QueryParam( "projectId" ) Integer projectId,
			@QueryParam( "projectSearchids" ) List<Integer> projectSearchids,
			@Context HttpServletRequest request )
	throws Exception {
		
		if ( projectId == null ) {
			String msg = ": Provided projectId null or missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( projectId == 0 ) {
			String msg = ": Provided projectId is zero";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( projectSearchids == null ) {
			String msg = ": Provided projectSearchids null or missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( projectSearchids.isEmpty() ) {
			String msg = ": Provided projectSearchids is empty";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResultForProjectId =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResultForProjectId.getUserSessionObject();
			if ( accessAndSetupWebSessionResultForProjectId.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResultForProjectId.getAuthAccessLevel();
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			////////   Auth complete
			//////////////////////////////////////////

			//  Get list of projectSearchids Found In Project 
			
			List<Integer> projectSearchidsFoundInProject = new ArrayList<>( projectSearchids.size() );
			for ( Integer projectSearchid : projectSearchids ) {
				if ( ProjectSearchIdAssocSearchIdInProjectIdSearcher.getInstance()
						.isSearchIdAssocWithProjectSearchIdInProjectId(
								projectSearchid, projectId ) ) {
					projectSearchidsFoundInProject.add( projectSearchid );
					break;
				}
			}

			//  Create webservice result object

			ListProjectSearchIdsWhereSearchIdIsInProjectResult webserviceResult = new ListProjectSearchIdsWhereSearchIdIsInProjectResult();

			List<ListProjectSearchIdsWhereSearchIdIsInProjectItem> webserviceItems = new ArrayList<>( projectSearchidsFoundInProject.size() );
			webserviceResult.status = true;
			webserviceResult.projectSearchIdsInProject = webserviceItems;
			
			for ( Integer projectSearchidFoundInProject:  projectSearchidsFoundInProject ) {
				ListProjectSearchIdsWhereSearchIdIsInProjectItem webserviceItem = new ListProjectSearchIdsWhereSearchIdIsInProjectItem();
				webserviceItem.projectSearchId = projectSearchidFoundInProject;
				webserviceItems.add( webserviceItem );
			}
			
			webserviceResult.status = true;
			
			return webserviceResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
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
	 * @param copySearchesRequest
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/copySearches")
	public CopySearchesResult copySearches( CopySearchesRequest copySearchesRequest,
			@Context HttpServletRequest request ) throws Exception {
		CopySearchesResult copySearchesResult = new CopySearchesResult();
		try {
			if ( copySearchesRequest.getProjectId() == null ) {
				String msg = "Provided projectId is null";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( copySearchesRequest.getCopyToProjectId() == null ) {
				String msg = "Provided copyToProjectId is null";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( copySearchesRequest.getSearchesToCopyToOtherProject() == null ) {
				String msg = "Provided searchesToCopyToOtherProject is null";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( copySearchesRequest.copyToOtherProject == null ) {
				String msg = "Provided copyToOtherProject is null";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( copySearchesRequest.copyAllSearches == null ) {
				String msg = "Provided copyAllSearches is null";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( copySearchesRequest.copyAllSearches ) {
				String msg = "Provided copyAllSearches is true (Not currently supported)";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			int projectId = copySearchesRequest.getProjectId();
			int copyToProjectId = copySearchesRequest.getCopyToProjectId();
			int[] searchesToCopyToOtherProject = copySearchesRequest.getSearchesToCopyToOtherProject();
			if ( projectId == 0 ) {
				String msg = "Provided projectId is 0, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( copyToProjectId == 0 ) {
				String msg = "Provided copyToProjectId is 0, is = " + copyToProjectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( searchesToCopyToOtherProject.length == 0 ) {
				String msg = "Provided searchesToCopyToOtherProject is empty";
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
			AuthAccessLevel authAccessLevelMoveToProject = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, copyToProjectId );
			if ( ! authAccessLevelMoveToProject.isProjectOwnerAllowed() ) {
				//  No Access Allowed for move to project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			for( int projectSearchId : searchesToCopyToOtherProject ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = "Search not found in DB for projectSearchId: " + projectSearchId;
					log.error( msg );
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				if ( projectId != search.getProjectId() && copyToProjectId != search.getProjectId() ) {
					//  Invalid request, searches not in from or to project
					String msg = "search is not in project, projectSearchId: " + search.getProjectSearchId() 
						+ ", search_id: " + search.getSearchId();
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
			ProjectDTO projectDTOcopyToProjectId = projectDAO.getProjectDTOForProjectId( copyToProjectId );
			if ( projectDTOcopyToProjectId == null ) {
				log.warn( "copyToProjectId is not in database: " + copyToProjectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectDTOcopyToProjectId.isMarkedForDeletion() ) {
				copySearchesResult.setCopyToProjectMarkedForDeletion(true);
				copySearchesResult.setStatus(false);
			} else if ( ! projectDTOcopyToProjectId.isEnabled() ) {
				copySearchesResult.setCopyToProjectDisabled(true);
				copySearchesResult.setStatus(false);
			} else {
				if ( copySearchesRequest.copyToOtherProject ) {
					CopyProjectSearchIdToNewProjectUsingDBTransactionService.getInstance()
					.copyProjectSearchIdToNewProjectId( searchesToCopyToOtherProject, copyToProjectId, copySearchesRequest.copyAllSearches );
				} else {
					MoveProjectSearchIdToNewProjectUsingDBTransactionService.getInstance()
					.moveProjectSearchIdToNewProjectId( searchesToCopyToOtherProject, copyToProjectId, copySearchesRequest.copyAllSearches );
				}
				copySearchesResult.setStatus(true);
			}
			return copySearchesResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Throwable e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE ) // This string will be passed to the client
					.build()
					);
		}
	}

	////////////////////////////
	
	///////  Classes for request and response

	/**
	 * This is returned from getOtherProjects
	 */
	public static class GetOtherProjectsResult {

		private boolean status;
		private List<GetOtherProjectsItem> otherProjects;

		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
		}
		public List<GetOtherProjectsItem> getOtherProjects() {
			return otherProjects;
		}
		public void setOtherProjects(List<GetOtherProjectsItem> otherProjects) {
			this.otherProjects = otherProjects;
		}
	}

	/**
	 * Item in GetOtherProjectsResult
	 */
	public static class GetOtherProjectsItem {
		private int projectId;
		private String projectTitle;
		
		public int getProjectId() {
			return projectId;
		}
		public void setProjectId(int projectId) {
			this.projectId = projectId;
		}
		public String getProjectTitle() {
			return projectTitle;
		}
		public void setProjectTitle(String projectTitle) {
			this.projectTitle = projectTitle;
		}

	}
	

	/**
	 * This is returned from listProjectSearchIdsWhereSearchIdIsInProject
	 */
	public static class ListProjectSearchIdsWhereSearchIdIsInProjectResult {

		private boolean status;
		private List<ListProjectSearchIdsWhereSearchIdIsInProjectItem> projectSearchIdsInProject;

		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
		}
		public List<ListProjectSearchIdsWhereSearchIdIsInProjectItem> getProjectSearchIdsInProject() {
			return projectSearchIdsInProject;
		}
		public void setProjectSearchIdsInProject(
				List<ListProjectSearchIdsWhereSearchIdIsInProjectItem> projectSearchIdsInProject) {
			this.projectSearchIdsInProject = projectSearchIdsInProject;
		}

	}

	/**
	 * Item in listProjectSearchIdsWhereSearchIdIsInProject
	 */
	public static class ListProjectSearchIdsWhereSearchIdIsInProjectItem {
		private int projectSearchId;

		public int getProjectSearchId() {
			return projectSearchId;
		}
		public void setProjectSearchId(int projectSearchId) {
			this.projectSearchId = projectSearchId;
		}
	}
	
	
	/**
	 * copySearches Request
	 *
	 */
	public static class CopySearchesRequest {

		private Integer projectId;
		private Integer copyToProjectId;
		private int[] searchesToCopyToOtherProject;
		private Boolean copyToOtherProject;
		private Boolean copyAllSearches;

		public Integer getProjectId() {
			return projectId;
		}
		public void setProjectId(Integer projectId) {
			this.projectId = projectId;
		}
		public int[] getSearchesToCopyToOtherProject() {
			return searchesToCopyToOtherProject;
		}
		public void setSearchesToCopyToOtherProject(int[] searchesToCopyToOtherProject) {
			this.searchesToCopyToOtherProject = searchesToCopyToOtherProject;
		}
		public Integer getCopyToProjectId() {
			return copyToProjectId;
		}
		public void setCopyToProjectId(Integer copyToProjectId) {
			this.copyToProjectId = copyToProjectId;
		}
		public Boolean getCopyToOtherProject() {
			return copyToOtherProject;
		}
		public void setCopyToOtherProject(Boolean copyToOtherProject) {
			this.copyToOtherProject = copyToOtherProject;
		}
		public Boolean getCopyAllSearches() {
			return copyAllSearches;
		}
		public void setCopyAllSearches(Boolean copyAllSearches) {
			this.copyAllSearches = copyAllSearches;
		}

	}

	/**
	 * This is returned from copySearches
	 */
	public static class CopySearchesResult {

		private boolean status;
		private boolean copyToProjectMarkedForDeletion;
		private boolean copyToProjectDisabled;

		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
		}
		public boolean isCopyToProjectMarkedForDeletion() {
			return copyToProjectMarkedForDeletion;
		}
		public void setCopyToProjectMarkedForDeletion(boolean copyToProjectMarkedForDeletion) {
			this.copyToProjectMarkedForDeletion = copyToProjectMarkedForDeletion;
		}
		public boolean isCopyToProjectDisabled() {
			return copyToProjectDisabled;
		}
		public void setCopyToProjectDisabled(boolean copyToProjectDisabled) {
			this.copyToProjectDisabled = copyToProjectDisabled;
		}

	}

}
