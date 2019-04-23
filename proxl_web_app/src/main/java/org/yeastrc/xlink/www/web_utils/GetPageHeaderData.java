package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.www.objects.ProjectTblSubPartsForProjectLists;
import org.yeastrc.xlink.www.objects.ProjectTitleHeaderDisplay;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ProjectTblSubPartsForProjectLists;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;
import org.yeastrc.xlink.www.user_web_utils.TruncateProjectTitleForDisplay;
/**
 * This class is for putting data in the "request" scope for the page header 
 *
 * This is for Data at the Header display at the top of the page
 *
 */
public class GetPageHeaderData {
	
	private static final GetPageHeaderData instance = new GetPageHeaderData();
	private GetPageHeaderData() { }
	public static GetPageHeaderData getInstance() { return instance; }
	
	/**
	 * @param projectId
	 * @param request
	 * @throws Exception 
	 */
	public void getPageHeaderDataWithProjectId( int projectId, HttpServletRequest request ) throws Exception {
		getPageHeaderData( projectId, request );
	}
	
	/**
	 * @param request
	 * @throws Exception 
	 */
	public void getPageHeaderDataWithoutProjectId( HttpServletRequest request ) throws Exception {
		getPageHeaderData( null /* projectId */, request );
	}
	
	/**
	 * @param projectId
	 * @param request
	 * @throws Exception 
	 */
	private void getPageHeaderData( Integer projectId, HttpServletRequest request ) throws Exception {

		UserSession userSession = UserSessionManager.getSinglesonInstance().getUserSession(request);
		
		if ( userSession != null ) {

				Integer authUserId = userSession.getAuthUserId();
			if ( userSession.isActualUser() && authUserId != null ) {
				//  have a logged in user
				if ( authUserId != null ) {
					Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel( authUserId );
					boolean headerUserIsAdmin = false;
					if ( userAccessLevel != null 
							&& userAccessLevel == AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {
						headerUserIsAdmin = true;
					}
					request.setAttribute( "headerUserIsAdmin", headerUserIsAdmin );
				}
				request.setAttribute( "headerUser", userSession );
				List<ProjectTblSubPartsForProjectLists> projectsFromDB = GetProjectListForCurrentLoggedInUser.getInstance().getProjectListForCurrentLoggedInUser( request );
				List<ProjectTitleHeaderDisplay> projects = new ArrayList<ProjectTitleHeaderDisplay>( projectsFromDB.size() );
				for ( ProjectTblSubPartsForProjectLists projectFromDB : projectsFromDB ) {
					ProjectTitleHeaderDisplay project = new ProjectTitleHeaderDisplay();
					project.setProjectTblData( projectFromDB );
					String titleHeaderDisplay = TruncateProjectTitleForDisplay.truncateProjectTitleForHeader( projectFromDB.getTitle() );
					project.setTitleHeaderDisplay( titleHeaderDisplay );
					projects.add( project );
				}
				request.setAttribute( "headerProjectList", projects ); 
			}
		}
		if ( projectId != null ) {
			ProjectTblSubPartsForProjectLists projectTblData = Cached_ProjectTblSubPartsForProjectLists.getInstance().getProjectTblSubPartsForProjectLists( projectId );
			ProjectTitleHeaderDisplay project = new ProjectTitleHeaderDisplay();
			project.setProjectTblData( projectTblData );;
			String titleHeaderDisplay = TruncateProjectTitleForDisplay.truncateProjectTitleForHeader( projectTblData.getTitle() );
			project.setTitleHeaderDisplay( titleHeaderDisplay );
			String titleHeaderDisplayNonUser = TruncateProjectTitleForDisplay.truncateProjectTitleForHeaderNonUser( projectTblData.getTitle() );
			project.setTitleHeaderDisplayNonUser( titleHeaderDisplayNonUser );
			request.setAttribute( "headerProject", project ); 
		}
	}
}
