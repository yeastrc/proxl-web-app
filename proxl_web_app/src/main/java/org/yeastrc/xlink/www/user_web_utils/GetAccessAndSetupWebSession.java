package org.yeastrc.xlink.www.user_web_utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.user_account.UserSessionObject;

public class GetAccessAndSetupWebSession {

	private static final Logger log = LoggerFactory.getLogger( GetAccessAndSetupWebSession.class);
	
	
	private GetAccessAndSetupWebSession() { }
	private static final GetAccessAndSetupWebSession _INSTANCE = new GetAccessAndSetupWebSession();
	public static GetAccessAndSetupWebSession getInstance() { return _INSTANCE; }

	

	/**
	 * @param httpRequest
	 * @return
	 * @throws Exception 
	 */
	public AccessAndSetupWebSessionResult getAccessAndSetupWebSessionNoProjectId( HttpServletRequest httpRequest ) throws Exception {
		
		
		return getAccessAndSetupWebSessionNoProjectId( httpRequest, null /* httpResponse */ );
	}
	
	
	/**
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 * @throws Exception 
	 */
	public AccessAndSetupWebSessionResult getAccessAndSetupWebSessionNoProjectId( HttpServletRequest httpRequest, HttpServletResponse httpResponse ) throws Exception {
		
		HttpSession session = httpRequest.getSession();
		
		AccessAndSetupWebSessionResult accessAndSetupWebSessionResult = new AccessAndSetupWebSessionResult();

		UserSessionObject userSessionObject 
		= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

		if ( userSessionObject == null ) {
		
			//  No User session 
			accessAndSetupWebSessionResult.setNoSession( true );
			return accessAndSetupWebSessionResult;  //  EARLY EXIT
		}
		

		//  Test access to the project id

		AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequest_NonProjectUsageOnly( userSessionObject );

		
		accessAndSetupWebSessionResult.setUserSessionObject( userSessionObject );
		accessAndSetupWebSessionResult.setAuthAccessLevel( authAccessLevel );
		
		return accessAndSetupWebSessionResult;
	}
	

	/**
	 * @param projectId
	 * @param httpRequest
	 * @return
	 * @throws Exception 
	 */
	public AccessAndSetupWebSessionResult getAccessAndSetupWebSessionWithProjectId( int projectId, HttpServletRequest httpRequest ) throws Exception {
		
		
		return getAccessAndSetupWebSessionWithProjectId( projectId, httpRequest, null /* httpResponse */ );
	}
	
	/**
	 * @param projectId
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 * @throws Exception 
	 */
	public AccessAndSetupWebSessionResult getAccessAndSetupWebSessionWithProjectId( int projectId, HttpServletRequest httpRequest, HttpServletResponse httpResponse ) throws Exception {
		
		AccessAndSetupWebSessionResult accessAndSetupWebSessionResult = 
				GetAuthAccessLevelForWebRequest.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, httpRequest, httpResponse );

		return accessAndSetupWebSessionResult;
		
//		HttpSession session = httpRequest.getSession();
//		
//		AccessAndSetupWebSessionResult accessAndSetupWebSessionResult = new AccessAndSetupWebSessionResult();
//
//		UserSessionObject userSessionObject 
//		= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
//
//		if ( userSessionObject == null ) {
//		
//			//  No User session 
//			accessAndSetupWebSessionResult.setNoSession( true );
//			return accessAndSetupWebSessionResult;  //  EARLY EXIT
//		}
//		
//
//		//  Test access to the project id
//
//		AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, projectId );
//
//		
//		accessAndSetupWebSessionResult.setUserSessionObject( userSessionObject );
//		accessAndSetupWebSessionResult.setAuthAccessLevel( authAccessLevel );
//		
//		return accessAndSetupWebSessionResult;
	}
}
