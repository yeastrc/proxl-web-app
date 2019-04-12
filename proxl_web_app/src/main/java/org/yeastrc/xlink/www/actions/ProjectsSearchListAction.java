package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.internal_services.GetUserDisplayListForSharedObjectId;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProjectTblSubPartsForProjectLists;
import org.yeastrc.xlink.www.objects.UserDisplay;
import org.yeastrc.xlink.www.searcher.SearchSearcher;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetProjectListForCurrentLoggedInUser;
/**
 * 
 *
 */
public class ProjectsSearchListAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectsSearchListAction.class);
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request, response );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to application no project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( userSession == null ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}

			if ( userSession == null ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			if ( userSession.getAuthUserId() == null ) {
				//  No Access Allowed since not a logged in user
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			Boolean userEnabledAppSpecific = AuthUserDAO.getInstance().getUserEnabledAppSpecific( userSession.getAuthUserId() );
			if ( userEnabledAppSpecific == null ) {
				//  No Access Allowed since not a logged in user
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			if ( ! userEnabledAppSpecific.booleanValue() ) {
				//  No Access Allowed since user is disabled
				return mapping.findForward( StrutsGlobalForwardNames.ACCOUNT_DISABLED );
			}
			
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithoutProjectId( request );
			
			List<ProjectTblSubPartsForProjectLists> projects = GetProjectListForCurrentLoggedInUser.getInstance().getProjectListForCurrentLoggedInUser( request );

			List<ProjectForDisplay> projectList = new ArrayList<>( projects.size() );
			for ( ProjectTblSubPartsForProjectLists project : projects ) {
				
				int projectId = project.getId();
				
				ProjectForDisplay projectForDisplay = new ProjectForDisplay(); 
				projectForDisplay.projectMain = project;
				
				Integer projectSharedObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
				if ( projectSharedObjectId == null ) {
					log.warn( "ListUsersForProjectIdService:  projectId is not in database: " + projectId );
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				
				//  Get Last Name and First Name of users on Project
				List<UserDisplay> userListForProject = GetUserDisplayListForSharedObjectId.getInstance().getUserDisplayListExcludeAdminGlobalNoAccessAccountsForSharedObjectId( projectSharedObjectId );
				
				//  Sort on last name then first name
				Collections.sort( userListForProject, new Comparator<UserDisplay>() {
					@Override
					public int compare(UserDisplay o1, UserDisplay o2) {
						int lastNameCompare = o1.getLastName().compareTo( o2.getLastName() );
						if ( lastNameCompare != 0 ) {
							return lastNameCompare;
						}
						return o1.getFirstName().compareTo( o2.getFirstName() );
					}
				});
				StringBuilder usersSB = new StringBuilder( 1000 );
				for ( UserDisplay userDisplay : userListForProject ) {
					if ( usersSB.length() != 0 ) {
						usersSB.append( ", " );
					}
					usersSB.append( userDisplay.getFirstName() );
					usersSB.append( " " );
					usersSB.append( userDisplay.getLastName() );
				}
				String users = usersSB.toString();
				projectForDisplay.users = users;
				
				List<SearchDTO> searches = SearchSearcher.getInstance().getSearchsForProjectId( projectId );
				projectForDisplay.searches = searches;
				
				projectList.add(projectForDisplay);
			}
			
			request.setAttribute( "projectList" , projectList );
			
			return mapping.findForward( "Success" );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	public static class ProjectForDisplay {
		private ProjectTblSubPartsForProjectLists projectMain;
		private List<SearchDTO> searches;
		private String users;
		
		public ProjectTblSubPartsForProjectLists getProjectMain() {
			return projectMain;
		}
		public void setProjectMain(ProjectTblSubPartsForProjectLists projectMain) {
			this.projectMain = projectMain;
		}
		public List<SearchDTO> getSearches() {
			return searches;
		}
		public void setSearches(List<SearchDTO> searches) {
			this.searches = searches;
		}
		public String getUsers() {
			return users;
		}
		public void setUsers(String users) {
			this.users = users;
		}

	}
}
