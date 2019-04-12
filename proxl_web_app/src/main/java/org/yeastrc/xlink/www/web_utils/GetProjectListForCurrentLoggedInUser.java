package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.objects.ProjectTblSubPartsForProjectLists;
import org.yeastrc.xlink.www.searcher.ProjectSearcher;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;
/**
 * This class is for getting the project list for the current logged in user  
 *
 */
public class GetProjectListForCurrentLoggedInUser {
	
	private static final GetProjectListForCurrentLoggedInUser instance = new GetProjectListForCurrentLoggedInUser();
	private GetProjectListForCurrentLoggedInUser() { }
	public static GetProjectListForCurrentLoggedInUser getInstance() { return instance; }
	
	/**
	 * @param request
	 * @throws Exception 
	 */
	public List<ProjectTblSubPartsForProjectLists> getProjectListForCurrentLoggedInUser( HttpServletRequest request ) throws Exception {
		
		UserSession userSession = UserSessionManager.getSinglesonInstance().getUserSession(request);
		
		if ( userSession == null || ( ! userSession.isActualUser() ) ) {
			//  No User session 
			return new ArrayList<>();
		}
		Integer authUserId = userSession.getAuthUserId();
		if ( authUserId == null ) {
			//  No User session 
			return new ArrayList<>();
		}
		List<ProjectTblSubPartsForProjectLists> projects = null;
		//  Get User acess level
		Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel( authUserId );
		if ( userAccessLevel != null
				&& userAccessLevel == AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {
			projects = ProjectSearcher.getInstance().getAllProjects();
		} else {
			
			//  Get projects currently authorized
			projects = ProjectSearcher.getInstance().getProjectsForAuthUserId( authUserId );
		}
		return projects;
	}
}
