package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.objects.ProjectTblSubPartsForProjectLists;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.searcher.ProjectSearcher;
import org.yeastrc.xlink.www.user_account.UserSessionObject;


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
		
		
		// Get their session first.  
		HttpSession session = request.getSession();


		UserSessionObject userSessionObject 
		= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

		if ( userSessionObject == null || userSessionObject.getUserDBObject() == null || userSessionObject.getUserDBObject().getAuthUser() == null ) {
			//  No User session 
			return new ArrayList<>();
		}

		List<ProjectTblSubPartsForProjectLists> projects = null;


		AuthUserDTO authUser = userSessionObject.getUserDBObject().getAuthUser();

		///  Refresh with latest

		authUser = AuthUserDAO.getInstance().getAuthUserDTOForId( authUser.getId() );

		userSessionObject.getUserDBObject().setAuthUser( authUser );


		if ( authUser != null 
				&& authUser.getUserAccessLevel() != null
				&& authUser.getUserAccessLevel() == AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {

			projects = ProjectSearcher.getInstance().getAllProjects();


		} else {

			int authUserId = authUser.getId();

			//  Get projects currently authorized
			projects = ProjectSearcher.getInstance().getProjectsForAuthUserId( authUserId );
			
		}
		
		return projects;
		
		
		
	}
}
