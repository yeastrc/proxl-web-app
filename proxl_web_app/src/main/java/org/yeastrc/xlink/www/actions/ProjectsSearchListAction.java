package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.internal_services.GetUserDisplayListForSharedObjectId;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProjectTblSubPartsForProjectLists;
import org.yeastrc.xlink.www.objects.UserDisplay;
import org.yeastrc.xlink.www.searcher.SearchSearcher;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetProjectListForCurrentLoggedInUser;
/**
 * 
 *
 */
public class ProjectsSearchListAction extends Action {
	
	private static final Logger log = Logger.getLogger(ProjectsSearchListAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			// Get their session first.  
			HttpSession session = request.getSession( false );
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request, response );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to application no project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			UserSessionObject userSessionObject = null;
			if ( session != null ) {
				userSessionObject = (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			}
			if ( userSessionObject == null ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			if ( userSessionObject.getUserDBObject() == null || userSessionObject.getUserDBObject().getAuthUser() == null  ) {
				//  No Access Allowed since not a logged in user
				log.warn( "Forward to StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE, IP: " + request.getRemoteAddr() );
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			AuthUserDTO authUser = userSessionObject.getUserDBObject().getAuthUser();
			if ( ! authUser.isEnabledAppSpecific() ) {
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
				List<UserDisplay> userListForProject = GetUserDisplayListForSharedObjectId.getInstance().getUserDisplayListExcludeAdminGlobalNoAccessAccountsForSharedObjectId( projectSharedObjectId );
				//  Sort on last name then first name
				Collections.sort( userListForProject, new Comparator<UserDisplay>() {
					@Override
					public int compare(UserDisplay o1, UserDisplay o2) {
						int lastNameCompare = o1.getxLinkUserDTO().getLastName().compareTo( o2.getxLinkUserDTO().getLastName() );
						if ( lastNameCompare != 0 ) {
							return lastNameCompare;
						}
						return o1.getxLinkUserDTO().getFirstName().compareTo( o2.getxLinkUserDTO().getFirstName() );
					}
				});
				StringBuilder usersSB = new StringBuilder( 1000 );
				for ( UserDisplay userDisplay : userListForProject ) {
					if ( usersSB.length() != 0 ) {
						usersSB.append( ", " );
					}
					usersSB.append( userDisplay.getxLinkUserDTO().getFirstName() );
					usersSB.append( " " );
					usersSB.append( userDisplay.getxLinkUserDTO().getLastName() );
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
