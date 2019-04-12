/*
* Original author: Daniel Jaschob <djaschob .at. uw.edu>
*                  
* Copyright 2018 University of Washington - Seattle, WA
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.yeastrc.xlink.www.user_session_management;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Manage the user in the session
 * 
 * The HTTP Session should never be accessed anywhere but here
 *
 */
public class UserSessionManager {
	
	private static final Logger log = LoggerFactory.getLogger( UserSessionManager.class );
	
	private static final String USER_SESSION__SESSION_ATTRIBUTE = "proxlUserSession";

	private UserSessionManager() { }
	private static final UserSessionManager _INSTANCE = new UserSessionManager();
	public static UserSessionManager getSinglesonInstance() { return _INSTANCE; }
	
	/**
	 * @param userSession
	 * @param httpServletRequest
	 */
	public void createNewUserSession( UserSession userSession, HttpServletRequest httpServletRequest ) {
		
		//  first invalidate existing session
		HttpSession httpSessionOLD = httpServletRequest.getSession( false /* do not create if not exist */ );
		if ( httpSessionOLD != null ) {
			httpSessionOLD.invalidate();
		}
		
		HttpSession httpSession = httpServletRequest.getSession( true /* create if not exist */ );
		httpSession.setAttribute( USER_SESSION__SESSION_ATTRIBUTE, userSession );
	}
	
	
	/**
	 * @param httpServletRequest
	 * @return
	 */
	public UserSession getUserSession( HttpServletRequest httpServletRequest ) {
		
		HttpSession httpSession = httpServletRequest.getSession( false /* don't create if not exist */ );
		if ( httpSession == null ) {
			return null;
		}
		
		Object sessionObject = httpSession.getAttribute( USER_SESSION__SESSION_ATTRIBUTE );
		if ( sessionObject == null ) {
			return null;
		}
		if ( ! ( sessionObject instanceof UserSession ) ) {
			String msg = "Session object not correct class 'UserSession'. is class: " + sessionObject.getClass();
			log.error( msg );
			return null;
		}
		UserSession userSession = (UserSession) sessionObject;
		return userSession;
	}
	
	/**
	 * @param userSession
	 * @param httpServletRequest
	 */
	public void setUserSession( UserSession userSession, HttpServletRequest httpServletRequest ) {
		HttpSession httpSession = httpServletRequest.getSession( true /* create if not exist */ );
		httpSession.setAttribute( USER_SESSION__SESSION_ATTRIBUTE, userSession );
	}

	/**
	 * Invalidate the active user session
	 * @param httpServletRequest
	 */
	public void invalidateUserSession( HttpServletRequest httpServletRequest ) {
		
		//  first invalidate existing session
		HttpSession httpSessionOLD = httpServletRequest.getSession( false /* do not create if not exist */ );
		if ( httpSessionOLD != null ) {
			httpSessionOLD.removeAttribute( USER_SESSION__SESSION_ATTRIBUTE );
			httpSessionOLD.invalidate();
		}
	}
	
	
	
	
	//  Not Used
	
	/**
	 * @param userId
	 * @param httpServletRequest
	 */
//	public void updateUserSession_UserId( int userId, HttpServletRequest httpServletRequest ) {
//
//		UserSession userSession = null;
//		
//		HttpSession httpSession = httpServletRequest.getSession( true /* create if not exist */ );
//		{
//			Object sessionObject = httpSession.getAttribute( USER_SESSION__SESSION_ATTRIBUTE );
//			if ( sessionObject != null ) {
//				if ( ! ( sessionObject instanceof UserSession ) ) {
//					String msg = "Session object not correct class 'UserSession'. is class: " + sessionObject.getClass();
//					log.error( msg );
//				} else {
//					userSession = (UserSession) sessionObject;
//				}
//			}
//		}
//		if ( userSession == null ) {
//			userSession = new UserSession();
//		}
//		userSession.userId = userId;
//		httpSession.setAttribute( USER_SESSION__SESSION_ATTRIBUTE, userSession );
//	}
	
}
