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

import javax.servlet.http.HttpServletRequest;

import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDatabaseException;
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
 */
public class AccessControl_GetUserSession_RefreshAccessEnabled {

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
			return null;
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
