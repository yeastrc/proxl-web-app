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
package org.yeastrc.xlink.www.access_control.common;

import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDatabaseException;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmt_UserEnabled_RecheckCache;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionAlterSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;


/**
 * !! Mostly Internal to Access Control !!
 * 
 * Get the User Session
 * 
 * Refresh the Access Level and Enabled flag
 * 
 * Ends the session (and returns null) if the user is disabled in Proxl or in User Mgmt
 *
 */
public class AccessControl_GetUserSession_RefreshAccessEnabled {

	private static final Logger log = LoggerFactory.getLogger( AccessControl_GetUserSession_RefreshAccessEnabled.class );

	private AccessControl_GetUserSession_RefreshAccessEnabled() { }
	private static final AccessControl_GetUserSession_RefreshAccessEnabled _INSTANCE = new AccessControl_GetUserSession_RefreshAccessEnabled();
	public static AccessControl_GetUserSession_RefreshAccessEnabled getSinglesonInstance() { return _INSTANCE; }
	
	/**
	 * @param httpServletRequest
	 * @return
	 * @throws Exception 
	 */
	public UserSession getUserSession_RefreshAccessEnabled( HttpServletRequest httpServletRequest ) throws Exception {
		
		UserSession userSession = UserSessionManager.getSinglesonInstance().getUserSession( httpServletRequest );
		if ( userSession == null ) {
			return null;
		}
		if ( ! userSession.isActualUser() ) {
			return userSession;
		}
		if ( userSession.getAuthUserId() == null ) {
			return userSession;
		}
		
		AuthUserDTO authUserDTO;
		try {
			authUserDTO = AuthUserDAO.getInstance().getForId( userSession.getAuthUserId() );
		} catch (SQLException e) {
			
			throw new ProxlWebappDatabaseException( e );
		}
		
		if ( authUserDTO == null ) {
			//  No record for user session user id
			log.info( "User session ended:  No user record for the user in the session.  authUserId: " + userSession.getAuthUserId() );
			UserSessionManager.getSinglesonInstance().invalidateUserSession( httpServletRequest );
			return null;
		}
		
		if ( ! authUserDTO.isEnabledAppSpecific() ) {
			//  User disabled in Proxl
			log.info( "User session ended:  User is disabled in Proxl.  authUserId: " + userSession.getAuthUserId() );
			UserSessionManager.getSinglesonInstance().invalidateUserSession( httpServletRequest );
			return null;
		}
		
		if ( userSession.getUserMgmtUserId() != null ) {
			//  Re-check the User Mgmt enabled flag (only makes a call when the user's last check is older than the interval)
			if ( ! UserMgmt_UserEnabled_RecheckCache.getInstance().isUserEnabled( userSession.getUserMgmtUserId() ) ) {
				log.info( "User session ended:  User is disabled or not found in User Mgmt.  authUserId: " + userSession.getAuthUserId() );
				UserSessionManager.getSinglesonInstance().invalidateUserSession( httpServletRequest );
				return null;
			}
		}
		
		if ( authUserDTO.isEnabledAppSpecific() != userSession.isEnabledAppSpecific() ) {
			userSession = UserSessionAlterSession.getSinglesonInstance().changeEnabledAppSpecific( authUserDTO.isEnabledAppSpecific(), userSession, httpServletRequest );
		}
		if ( authUserDTO.getUserAccessLevel() == null && userSession.getUserAccessLevel() == null ) {

		} else if ( ( authUserDTO.getUserAccessLevel() == null && userSession.getUserAccessLevel() != null )
				|| ( ! authUserDTO.getUserAccessLevel().equals( userSession.getUserAccessLevel() ) ) ){
			userSession = UserSessionAlterSession.getSinglesonInstance().changeUserAccessLevel( authUserDTO.getUserAccessLevel(), userSession, httpServletRequest );
		}
		
		return userSession;
	}
}
