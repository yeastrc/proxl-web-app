package org.yeastrc.xlink.www.user_web_utils;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cookie_mgmt.main.ProxlDataCookieManagement;
import org.yeastrc.xlink.www.cookie_mgmt.main.PublicAccessCodeSessionManagement;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.internal_services.GetAuthLevelFromXLinkData;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.web_utils.RefreshAllowedReadAccessProjectIds;

/**
 * 
 *
 */
public class GetAuthAccessLevelForWebRequest {

	private static final Logger log = Logger.getLogger(GetAuthAccessLevelForWebRequest.class);
	
	
	private GetAuthAccessLevelForWebRequest() { }
	private static final GetAuthAccessLevelForWebRequest _INSTANCE = new GetAuthAccessLevelForWebRequest();
	public static GetAuthAccessLevelForWebRequest getInstance() { return _INSTANCE; }
	
	
	
	/**
	 * @param projectId
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 * @throws Exception
	 */
	public AccessAndSetupWebSessionResult getAccessAndSetupWebSessionWithProjectId( int projectId, HttpServletRequest httpRequest, HttpServletResponse httpResponse ) throws Exception {
		
		
		
		HttpSession session = httpRequest.getSession();
		
		AccessAndSetupWebSessionResult accessAndSetupWebSessionResult = new AccessAndSetupWebSessionResult();
		
		ProjectDTO projectOnlyProjectLockedPublicAccessLevel = ProjectDAO.getInstance().getProjectLockedPublicAccessLevelPublicAccessLockedForProjectId( projectId );
		
		if ( projectOnlyProjectLockedPublicAccessLevel == null ) {
			
			String msg = "Failed to get project for project id: " + projectId;
				
			log.error( msg );

			throw new Exception( msg );
		}
		

		UserSessionObject userSessionObject 
		= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

		
		boolean noAccessAllowedAtUserLevel = false;
		
		AuthAccessLevel authAccessLevel = null;
		
		if ( userSessionObject != null ) {
		
			//  Existing User session object 

			XLinkUserDTO xLinkUserDBObject = userSessionObject.getUserDBObject();


			if ( xLinkUserDBObject != null ) {

				//  This is a signed in user so use their access.

				///  Refresh with latest

				AuthUserDTO authUser = AuthUserDAO.getInstance().getAuthUserDTOForId( xLinkUserDBObject.getAuthUser().getId() );

				xLinkUserDBObject.setAuthUser( authUser );


				//  If user is not enabled, return access level none

				if ( ! authUser.isEnabled() ) {

					authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_NONE );

					noAccessAllowedAtUserLevel = true;

				} else {

					//  If the user level access is ADMIN or NONE, use that, otherwise retrieve the access at the project level

					Integer userAccessLevelAtUserLevel = xLinkUserDBObject.getAuthUser().getUserAccessLevel();

					if ( userAccessLevelAtUserLevel != null 
							&& ( ! userAccessLevelAtUserLevel.equals( AuthAccessLevelConstants.ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER ) ) ) {

						if ( userAccessLevelAtUserLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {

							authAccessLevel = new AuthAccessLevel( userAccessLevelAtUserLevel );

						} else if ( userAccessLevelAtUserLevel >= AuthAccessLevelConstants.ACCESS_LEVEL_NONE ) {

							authAccessLevel = new AuthAccessLevel( userAccessLevelAtUserLevel );

							noAccessAllowedAtUserLevel = true;
						}

					} else {

						authAccessLevel = GetAuthLevelFromXLinkData.getInstance().getAuthLevelForAuthUserIdProjectId( xLinkUserDBObject, projectId );
					}
				}

			}
			
		}
		
		if ( ( ! noAccessAllowedAtUserLevel ) 
				&& ( authAccessLevel == null 
						|| authAccessLevel.getAuthAccessLevel() == AuthAccessLevelConstants.ACCESS_LEVEL_NONE ) ) {
			
			
			if ( userSessionObject == null ) {
				
				
				userSessionObject = new UserSessionObject();
				
				//  Get public access codes from cookie to refresh into 
				
				List<String> projectPublicAccessCodeCookieList = 
						ProxlDataCookieManagement.getInstance().getPublicAccessCodesCookieList( httpRequest );
				
				PublicAccessCodeSessionManagement.getInstance().addPublicAccessCodesToUserSessionObjectUsingStringFromCookie( projectPublicAccessCodeCookieList , userSessionObject );
			
			}
			
			//  Check if project ids from public access code provide access if no access from user 
			
			RefreshAllowedReadAccessProjectIds.refreshAllowedReadAccessProjectIds( userSessionObject );
			
			//  allow read access based on project id access code
			
			Set<Integer> allowedReadAccessProjectIds = userSessionObject.getAllowedReadAccessProjectIds();
			

			if ( allowedReadAccessProjectIds != null && allowedReadAccessProjectIds.contains( projectId ) ) {
				
				//  Save userSessionObject in case just created it, since it is now needed.

				session.setAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN, userSessionObject );

				authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY );
			
			} else {

				Integer publicAccessLevel = projectOnlyProjectLockedPublicAccessLevel.getPublicAccessLevel();
				
				if ( publicAccessLevel != null ) {
					
					
					//  Save userSessionObject in case just created it, since it is now needed.

					session.setAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN, userSessionObject );


					//  Project public access level set so use that access level
			
					authAccessLevel = new AuthAccessLevel( publicAccessLevel );
				
				} else {
					
					//  Save userSessionObject in case just created it, since it is now needed.

					session.setAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN, userSessionObject );

					userSessionObject = null;

//					authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_NONE );
				}
			}
		}

//		if ( authAccessLevel == null ) {
//			
//			String msg = "authAccessLevel == null at the end of processing, this is an error.";
//			
//			log.error( msg );
//			
//			throw new Exception( msg );
//		}
		
		if ( authAccessLevel != null
				&& projectOnlyProjectLockedPublicAccessLevel.isProjectLocked() 
				&& authAccessLevel.getAuthAccessLevel() != AuthAccessLevelConstants.ACCESS_LEVEL_NONE ) {
			
			//  Override auth level to read only if project is locked.
			
			int authAccessLevelIfNotLocked = authAccessLevel.getAuthAccessLevel();
			
			authAccessLevel.setAuthAccessLevel(	AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY );
			
			authAccessLevel.setAuthAccessLevelIfNotLocked( authAccessLevelIfNotLocked );
		}
		
		
		if ( userSessionObject == null ) {
		
			//  No User session 
			accessAndSetupWebSessionResult.setNoSession( true );
			return accessAndSetupWebSessionResult;  //  EARLY EXIT
		}
		
		
		
		accessAndSetupWebSessionResult.setUserSessionObject( userSessionObject );
		accessAndSetupWebSessionResult.setAuthAccessLevel( authAccessLevel );
		
		return accessAndSetupWebSessionResult;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////
	
	
	///////////  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	///////////   Legacy code 
	
	
	
	

	/**
	 * This is for use specifically for non-project related access level
	 * 
	 * @param userSessionObject
	 * @return null if not set on user object
	 * @throws Exception
	 */
	public AuthAccessLevel getAuthAccessLevelForWebRequest_NonProjectUsageOnly( UserSessionObject userSessionObject ) throws Exception {
		
		if ( userSessionObject == null ) {
			
			String msg = "userSessionObject == null";
			
			throw new IllegalArgumentException( msg );
		}
		

		XLinkUserDTO xLinkUserDBObject = userSessionObject.getUserDBObject();
		
		
		if ( xLinkUserDBObject == null ) {
			
			
			AuthAccessLevel authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_NONE );

			return authAccessLevel;
		}
		
		///  Refresh with latest
		
		AuthUserDTO authUser = AuthUserDAO.getInstance().getAuthUserDTOForId( xLinkUserDBObject.getAuthUser().getId() );
		
		xLinkUserDBObject.setAuthUser( authUser );
		
		
		//  If user is not enabled, return access level none
		
		if ( ! authUser.isEnabled() ) {
		
			AuthAccessLevel authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_NONE );

			return authAccessLevel;
			
		} else {
		
			Integer userAccessLevelInteger = xLinkUserDBObject.getAuthUser().getUserAccessLevel();

			if ( userAccessLevelInteger != null ) {

				AuthAccessLevel authAccessLevel = new AuthAccessLevel( userAccessLevelInteger );

				return authAccessLevel;
			}
		}
		
		return null;
	}
	
	

	/**
	 * Auth access level for specific project, unless the user level is ADMIN or NONE
	 * 
	 * @param userSessionObject
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public AuthAccessLevel getAuthAccessLevelForWebRequestProjectId( UserSessionObject userSessionObject, int projectId ) throws Exception {
		
		if ( userSessionObject == null ) {
			
			String msg = "userSessionObject == null";
			
			throw new IllegalArgumentException( msg );
		}
		
		
		ProjectDTO projectOnlyProjectLockedPublicAccessLevel = ProjectDAO.getInstance().getProjectLockedPublicAccessLevelPublicAccessLockedForProjectId( projectId );
		

		if ( projectOnlyProjectLockedPublicAccessLevel == null ) {
			
			String msg = "Failed to get project for project id: " + projectId;
			
			log.error( msg );

			throw new Exception( msg );
		}
		
		
		
		XLinkUserDTO xLinkUserDBObject = userSessionObject.getUserDBObject();
		
		boolean noAccessAllowedAtUserLevel = false;
		
		AuthAccessLevel authAccessLevel = null;
		
		if ( xLinkUserDBObject != null ) {
			
			//  This is a signed in user so use their access.
			
			///  Refresh with latest
			
			AuthUserDTO authUser = AuthUserDAO.getInstance().getAuthUserDTOForId( xLinkUserDBObject.getAuthUser().getId() );
			
			xLinkUserDBObject.setAuthUser( authUser );

			
			//  If user is not enabled, return access level none
			
			if ( ! authUser.isEnabled() ) {
			
				authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_NONE );
				
				noAccessAllowedAtUserLevel = true;
				
			} else {

				//  If the user level access is ADMIN or NONE, use that, otherwise retrieve the access at the project level

				Integer userAccessLevelAtUserLevel = xLinkUserDBObject.getAuthUser().getUserAccessLevel();

				if ( userAccessLevelAtUserLevel != null 
						&& ( ! userAccessLevelAtUserLevel.equals( AuthAccessLevelConstants.ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER ) ) ) {
					
					if ( userAccessLevelAtUserLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {

						authAccessLevel = new AuthAccessLevel( userAccessLevelAtUserLevel );

					} else if ( userAccessLevelAtUserLevel >= AuthAccessLevelConstants.ACCESS_LEVEL_NONE ) {

						authAccessLevel = new AuthAccessLevel( userAccessLevelAtUserLevel );
						
						noAccessAllowedAtUserLevel = true;
					}

				} else {

					authAccessLevel = GetAuthLevelFromXLinkData.getInstance().getAuthLevelForAuthUserIdProjectId( xLinkUserDBObject, projectId );
				}
			}
		
		}
		
		if ( ( ! noAccessAllowedAtUserLevel ) 
				&& ( authAccessLevel == null 
						|| authAccessLevel.getAuthAccessLevel() == AuthAccessLevelConstants.ACCESS_LEVEL_NONE ) ) {
		
			//  Check if project ids from public access code provide access if no access from user 
			
			RefreshAllowedReadAccessProjectIds.refreshAllowedReadAccessProjectIds( userSessionObject );
			
			//  allow read access based on project id access code
			
			Set<Integer> allowedReadAccessProjectIds = userSessionObject.getAllowedReadAccessProjectIds();
			

			if ( allowedReadAccessProjectIds != null && allowedReadAccessProjectIds.contains( projectId ) ) {

				authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY );
			
			} else {

				Integer publicAccessLevel = projectOnlyProjectLockedPublicAccessLevel.getPublicAccessLevel();
				
				if ( publicAccessLevel != null ) {
					
					//  Project public access level set so use that access level
			
					authAccessLevel = new AuthAccessLevel( publicAccessLevel );
				
				} else {

					authAccessLevel = new AuthAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_NONE );
				}
			}
		}

		if ( authAccessLevel == null ) {
			
			String msg = "authAccessLevel == null at the end of processing, this is an error.";
			
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		if ( projectOnlyProjectLockedPublicAccessLevel.isProjectLocked() 
				&& authAccessLevel.getAuthAccessLevel() != AuthAccessLevelConstants.ACCESS_LEVEL_NONE ) {
			
			//  Override auth level to read only if project is locked.
			
			int authAccessLevelIfNotLocked = authAccessLevel.getAuthAccessLevel();
			
			authAccessLevel.setAuthAccessLevel(	AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY );
			
			authAccessLevel.setAuthAccessLevelIfNotLocked( authAccessLevelIfNotLocked );
		}
		
		
		if ( ( ! projectOnlyProjectLockedPublicAccessLevel.isEnabled() )
				|| projectOnlyProjectLockedPublicAccessLevel.isMarkedForDeletion() ) {
			
			//  Override auth level to none if project is not enabled or marked for deletion.
			
			int authAccessLevelIfNotLocked = authAccessLevel.getAuthAccessLevel();
			
			authAccessLevel.setAuthAccessLevel(	AuthAccessLevelConstants.ACCESS_LEVEL_NONE );
			
			authAccessLevel.setAuthAccessLevelIfNotLocked( authAccessLevelIfNotLocked );
		}
		
		
		return authAccessLevel;
	
		
	}
	


	/**
	 * Auth access level for specific related project, unless the user level is ADMIN or NONE
	 * 
	 * @param userSessionObject
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public AuthAccessLevel getAuthAccessLevelForWebRequestSearchId( UserSessionObject userSessionObject, int searchId ) throws Exception {
		
		
		Integer projectId = SearchDAO.getInstance().getSearchProjectId( searchId );
		
		if ( projectId == null ) {
			
			String msg = "Failed to get project id for search id: " + searchId;
			
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		AuthAccessLevel authAccessLevel = getAuthAccessLevelForWebRequestProjectId( userSessionObject, projectId );

		
		return authAccessLevel;
	
		
	}
	
	
}
