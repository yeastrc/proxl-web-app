/*
 * Original author: Daniel Jaschob <djaschob .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use userSession file except in compliance with the License.
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

import java.util.HashSet;

import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCreateAccountRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;

/**
 * Build UserSession
 *
 */
public class UserSessionBuilder {

	private UserSessionBuilder() { }
	
	/**
	 * @return
	 */
	public static UserSessionBuilder getBuilder() {
		return new UserSessionBuilder();
	}

	private UserSession userSession = new UserSession();

	/**
	 * @return
	 */
	public UserSession build() {
		return userSession;
	}

	/**
	 * @param userSession
	 * @return
	 */
	public UserSessionBuilder fromUserSession( UserSession userSession ) {

		this.userSession = userSession.makeClone();

		return this;
	}

	/**
	 * @param userDTO
	 * @return
	 */
	public UserSessionBuilder fromAuthUserDTO( AuthUserDTO authUserDTO ) {

		userSession.actualUser = true;
		
		userSession.authUserId = authUserDTO.getId();
		userSession.userMgmtUserId = authUserDTO.getUserMgmtUserId();
		userSession.userAccessLevel = authUserDTO.getUserAccessLevel();
		userSession.enabledAppSpecific = authUserDTO.isEnabledAppSpecific();
		
		return this;
	}

	/**
	 * @param userMgmtGetUserDataResponse
	 * @return
	 */
	public UserSessionBuilder fromGetUserDataForIdAccountWebserviceResponse( UserMgmtGetUserDataResponse userMgmtGetUserDataResponse ) {

		userSession.actualUser = true;
		
		userSession.username = userMgmtGetUserDataResponse.getUsername();
		userSession.email = userMgmtGetUserDataResponse.getEmail();
		userSession.firstName = userMgmtGetUserDataResponse.getFirstName();
		userSession.lastName = userMgmtGetUserDataResponse.getLastName();
		userSession.organization = userMgmtGetUserDataResponse.getOrganization();
		userSession.enabled = userMgmtGetUserDataResponse.isEnabled();
		userSession.globalAdminUser = userMgmtGetUserDataResponse.isGlobalAdminUser();
		return this;
	}
	
	
	/**
	 * @param userMgmtCreateAccountRequest
	 * @return
	 */
	public UserSessionBuilder fromUserMgmtCreateAccountRequest( UserMgmtCreateAccountRequest userMgmtCreateAccountRequest ) {

		userSession.actualUser = true;
		
		userSession.username = userMgmtCreateAccountRequest.getUsername();
		userSession.email = userMgmtCreateAccountRequest.getEmail();
		userSession.firstName = userMgmtCreateAccountRequest.getFirstName();
		userSession.lastName = userMgmtCreateAccountRequest.getLastName();
		userSession.organization = userMgmtCreateAccountRequest.getOrganization();
		return this;
	}

	public UserSessionBuilder setAuthUserId(int authUserId) {
		userSession.authUserId = authUserId;
		return this;
	}
	public UserSessionBuilder setUserMgmtUserId(int userMgmtUserId) {
		userSession.userMgmtUserId = userMgmtUserId;
		return this;
	}
	public UserSessionBuilder setUserAccessLevel(Integer userAccessLevel) {
		userSession.userAccessLevel = userAccessLevel;
		return this;
	}
	public UserSessionBuilder setEnabledAppSpecific(boolean enabledAppSpecific) {
		userSession.enabledAppSpecific = enabledAppSpecific;
		return this;
	}
	public UserSessionBuilder setUserMgmtSessionKey(String userMgmtSessionKey) {
		userSession.userMgmtSessionKey = userMgmtSessionKey;
		return this;
	}
	public UserSessionBuilder setUsername(String username) {
		userSession.username = username;
		return this;
	}
	public UserSessionBuilder setEmail(String email) {
		userSession.email = email;
		return this;
	}
	public UserSessionBuilder setFirstName(String firstName) {
		userSession.firstName = firstName;
		return this;
	}
	public UserSessionBuilder setLastName(String lastName) {
		userSession.lastName = lastName;
		return this;
	}
	public UserSessionBuilder setOrganization(String organization) {
		userSession.organization = organization;
		return this;
	}
	public UserSessionBuilder setEnabled(boolean enabled) {
		userSession.enabled = enabled;
		return this;
	}
	public UserSessionBuilder setGlobalAdminUser(boolean globalAdminUser) {
		userSession.globalAdminUser = globalAdminUser;
		return this;
	}
	public UserSessionBuilder addAllowedReadAccessProjectId( int projectId ) {
		if ( userSession.allowedReadAccessProjectIds == null ) {
			userSession.allowedReadAccessProjectIds = new HashSet<>();
		}
		userSession.allowedReadAccessProjectIds.add( projectId );
		return this;
	}
	public UserSessionBuilder addAllowedReadAccessProjectPublicAccessCode( String projectPublicAccessCode ) {
		if ( userSession.allowedReadAccessProjectPublicAccessCodes == null ) {
			userSession.allowedReadAccessProjectPublicAccessCodes = new HashSet<>();
		}
		userSession.allowedReadAccessProjectPublicAccessCodes.add( projectPublicAccessCode );
		return this;
	}
}
