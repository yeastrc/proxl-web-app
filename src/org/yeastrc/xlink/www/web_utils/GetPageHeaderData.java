package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.objects.ProjectTitleAbstractHeaderDisplay;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
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
		
		
		// Get their session first.  
		HttpSession session = request.getSession();


		UserSessionObject userSessionObject 
		= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

		if ( userSessionObject == null ) {

			//  No User session 

			return;
		}
		
		
		if ( userSessionObject.getUserDBObject() != null && userSessionObject.getUserDBObject().getAuthUser() != null  ) {

			//  have a logged in user
			
			AuthUserDTO authUser = null;
			
			if ( userSessionObject.getUserDBObject() != null && userSessionObject.getUserDBObject().getAuthUser() != null ) {
				
				authUser = userSessionObject.getUserDBObject().getAuthUser();
				
				///  Refresh with latest
				
				authUser = AuthUserDAO.getInstance().getAuthUserDTOForId( authUser.getId() );
				
				userSessionObject.getUserDBObject().setAuthUser( authUser );
				
				
				boolean headerUserIsAdmin = false;
				
				if ( authUser.getUserAccessLevel() != null 
						&& authUser.getUserAccessLevel() == AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {
					
					headerUserIsAdmin = true;
				}
				
				request.setAttribute( "headerUserIsAdmin", headerUserIsAdmin );
			}

			request.setAttribute( "headerUser", userSessionObject.getUserDBObject() );
			
			List<ProjectDTO> projectsFromDB = GetProjectListForCurrentLoggedInUser.getInstance().getProjectListForCurrentLoggedInUser( request );
			
			List<ProjectTitleAbstractHeaderDisplay> projects = new ArrayList<ProjectTitleAbstractHeaderDisplay>( projectsFromDB.size() );
			
			for ( ProjectDTO projectFromDB : projectsFromDB ) {
				
				ProjectTitleAbstractHeaderDisplay project = new ProjectTitleAbstractHeaderDisplay();
				
				project.setProjectDTO(projectFromDB);
				
				String titleHeaderDisplay = TruncateProjectTitleForDisplay.truncateProjectTitleForHeader( projectFromDB.getTitle() );
				
				project.setTitleHeaderDisplay( titleHeaderDisplay );
				
				
				
				
				projects.add( project );
			}
			
			
			request.setAttribute( "headerProjectList", projects ); 
		}

		
		if ( projectId != null ) {
			
			ProjectDTO projectFromDB = ProjectDAO.getInstance().getProjectDTOForProjectId( projectId );
			
			ProjectTitleAbstractHeaderDisplay project = new ProjectTitleAbstractHeaderDisplay();
			
			project.setProjectDTO(projectFromDB);

			String titleHeaderDisplay = TruncateProjectTitleForDisplay.truncateProjectTitleForHeader( projectFromDB.getTitle() );
			
			project.setTitleHeaderDisplay( titleHeaderDisplay );
			

			String titleHeaderDisplayNonUser = TruncateProjectTitleForDisplay.truncateProjectTitleForHeaderNonUser( projectFromDB.getTitle() );

			project.setTitleHeaderDisplayNonUser( titleHeaderDisplayNonUser );
			
			request.setAttribute( "headerProject", project ); 
			 
		}
		
		
		
		
		
	}
}
