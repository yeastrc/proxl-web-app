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

/**
 * Alter User Session
 * 
 * Saves Updated User session using UserSessionManager 
 *
 */
public class UserSessionAlterSession {

	private UserSessionAlterSession() { }
	private static final UserSessionAlterSession _INSTANCE = new UserSessionAlterSession();
	public static UserSessionAlterSession getSinglesonInstance() { return _INSTANCE; }
	
	/**
	 * @param enabledAppSpecific
	 * @param userSession
	 * @param httpServletRequest
	 * @return
	 */
	public UserSession changeEnabledAppSpecific( boolean enabledAppSpecific, UserSession userSession, HttpServletRequest httpServletRequest ) {
		
		if ( userSession == null ) {
			throw new IllegalArgumentException( "changeEnabledAppSpecific(...) userSession == null" );
		}

		UserSession userSessionUpdated =
			UserSessionBuilder.getBuilder()
			.fromUserSession( userSession )
			.setEnabledAppSpecific( enabledAppSpecific )
			.build();
		
		//  Update session
		UserSessionManager.getSinglesonInstance().setUserSession( userSessionUpdated, httpServletRequest );
		
		return userSession;
	}
	
	/**
	 * @param userAccessLevel
	 * @param userSession
	 * @param httpServletRequest
	 * @return
	 */
	public UserSession changeUserAccessLevel( Integer userAccessLevel, UserSession userSession, HttpServletRequest httpServletRequest ) {

		if ( userSession == null ) {
			throw new IllegalArgumentException( "changeUserAccessLevel(...) userSession == null" );
		}
		
		UserSession userSessionUpdated =
			UserSessionBuilder.getBuilder()
			.fromUserSession( userSession )
			.setUserAccessLevel( userAccessLevel )
			.build();
		
		//  Update session
		UserSessionManager.getSinglesonInstance().setUserSession( userSessionUpdated, httpServletRequest );
		
		return userSession;
	}

	/**
	 * @param projectId
	 * @param projectPublicAccessCode
	 * @param userSession - can be null
	 * @param httpServletRequest
	 * @return
	 */
	public UserSession add_AllowedReadAccessProjectId_AllowedReadAccessProjectPublicAccessCodes(
			int projectId, String projectPublicAccessCode, UserSession userSession, HttpServletRequest httpServletRequest ) {

		if ( projectPublicAccessCode == null ) {
			throw new IllegalArgumentException( "add_AllowedReadAccessProjectId_AllowedReadAccessProjectPublicAccessCodes(...) projectPublicAccessCode == null" );
		}
		
		UserSessionBuilder userSessionBuilder = UserSessionBuilder.getBuilder();

		if ( userSession != null ) {
			userSessionBuilder.fromUserSession( userSession );
		}
		
		UserSession userSessionUpdated =
			userSessionBuilder.addAllowedReadAccessProjectId( projectId )
			.addAllowedReadAccessProjectPublicAccessCode( projectPublicAccessCode )
			.build();
		
		//  Update session
		UserSessionManager.getSinglesonInstance().setUserSession( userSessionUpdated, httpServletRequest );
		
		return userSession;
	}
	
	
}
