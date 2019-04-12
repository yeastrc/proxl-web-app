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

import java.util.Set;

/**
 * The User's Session stored in the session
 *
 */
public class UserSession {

	/**
	 * Set false if Public Access or something else
	 */
	boolean actualUser;
	
	Integer authUserId;
	Integer userMgmtUserId;
	Integer userAccessLevel;
	boolean enabledAppSpecific;
	
	Set<Integer> allowedReadAccessProjectIds;
	Set<String> allowedReadAccessProjectPublicAccessCodes;

	//  From User Mgmt

	String userMgmtSessionKey;

	String username;
	String email;
	String firstName;
	String lastName;
	String organization;
	boolean enabled;
	boolean globalAdminUser;

	@Override
	public String toString() {
		return "UserSession [actualUser=" + actualUser + ", authUserId=" + authUserId + ", userMgmtUserId="
				+ userMgmtUserId + ", userAccessLevel=" + userAccessLevel + ", enabledAppSpecific=" + enabledAppSpecific
				+ ", allowedReadAccessProjectIds=" + allowedReadAccessProjectIds
				+ ", allowedReadAccessProjectPublicAccessCodes=" + allowedReadAccessProjectPublicAccessCodes
				+ ", userMgmtSessionKey=" + userMgmtSessionKey + ", username=" + username + ", email=" + email
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", organization=" + organization
				+ ", enabled=" + enabled + ", globalAdminUser=" + globalAdminUser + "]";
	}

	/**
	 * @param projectId
	 */
	public boolean isProjectIdAllowedReadAccess( int projectId ) {
		if ( allowedReadAccessProjectIds == null ) {
			return false;
		}
		return allowedReadAccessProjectIds.contains( projectId );
	}
	
	/**
	 * @return
	 */
	UserSession makeClone() {
		
		UserSession newUserSession = new UserSession();
		
		newUserSession.actualUser = this.actualUser;
		
		newUserSession.authUserId = this.authUserId;
		newUserSession.userMgmtUserId = this.userMgmtUserId;
		newUserSession.userAccessLevel = this.userAccessLevel;
		newUserSession.enabledAppSpecific = this.enabledAppSpecific;
		
		newUserSession.userMgmtSessionKey = this.userMgmtSessionKey;
		
		newUserSession.username = this.username;
		newUserSession.email = this.email;
		newUserSession.firstName = this.firstName;
		newUserSession.lastName = this.lastName;
		newUserSession.organization = this.organization;
		newUserSession.enabled = this.enabled;
		newUserSession.globalAdminUser = this.globalAdminUser;
		
		return newUserSession;
	}


	// public getters
	
	/**
	 * Set false if Public Access or something else
	 * @return
	 */
	public boolean isActualUser() {
		if ( actualUser ) {
			return true;
		}
		if ( authUserId != null ) {
			return true;
		}
		return false;
	}
	
	public Integer getAuthUserId() {
		return authUserId;
	}
	public Integer getUserMgmtUserId() {
		return userMgmtUserId;
	}
	public Integer getUserAccessLevel() {
		return userAccessLevel;
	}
	public boolean isEnabledAppSpecific() {
		return enabledAppSpecific;
	}
	public String getUserMgmtSessionKey() {
		return userMgmtSessionKey;
	}
	public String getUsername() {
		return username;
	}
	public String getEmail() {
		return email;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getOrganization() {
		return organization;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public boolean isGlobalAdminUser() {
		return globalAdminUser;
	}
	public Set<Integer> getAllowedReadAccessProjectIds() {
		return allowedReadAccessProjectIds;
	}
	public Set<String> getAllowedReadAccessProjectPublicAccessCodes() {
		return allowedReadAccessProjectPublicAccessCodes;
	}


}
