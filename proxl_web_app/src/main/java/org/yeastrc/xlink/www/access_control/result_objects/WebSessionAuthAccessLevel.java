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
package org.yeastrc.xlink.www.access_control.result_objects;

import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;

/**
 * 
 *
 */
public class WebSessionAuthAccessLevel {

	/**
	 * Worst access level across the projects, if there are project ids
	 */
	int authAccessLevel;
	
	/**
	 * If the project(s) were not locked, what the access level would be.
	 */
	int authAccessLevelForProjectIdsIfNotLocked;

	@Override
	public String toString() {
		return "WebSessionAuthAccessLevel [authAccessLevel=" + authAccessLevel
				+ ", authAccessLevelForProjectIdsIfNotLocked=" + authAccessLevelForProjectIdsIfNotLocked + "]";
	}
	
	/**
	 * Worst access level across the projects
	 * @return
	 */
	public int getAccessLevel() {
		return authAccessLevel;
	}

	/**
	 * If the project(s) were not locked, what the access level would be.
	 * Worst access level across the projects.
	 * @return
	 */
	public int getAuthAccessLevelForProjectIdsIfNotLocked() {
		return authAccessLevelForProjectIdsIfNotLocked;
	}
	


	public boolean isAdminAllowed() {
		
		if ( authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {
			return true;
		}
		return false;
	}
	

	/**
	 * @return true if authAccessLevelIfNotLocked <= AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN
	 */
	public boolean isAdminIfProjectNotLockedAllowed() {
		
		if ( authAccessLevelForProjectIdsIfNotLocked <= AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {
			return true;
		}
		return false;
	}
	
	
	public boolean isCreateNewProjectAllowed() {
		
		if ( authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER ) {
			return true;
		}
		return false;
	}
	
	
	

	/**
	 * @return true if authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER
	 */
	public boolean isProjectOwnerAllowed() {
		
		if ( authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER ) {
			return true;
		}
		return false;
	}
	
	

	/**
	 * @return true if authAccessLevelIfNotLocked <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER
	 */
	public boolean isProjectOwnerIfProjectNotLockedAllowed() {
		
		if ( authAccessLevelForProjectIdsIfNotLocked <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER ) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return true if authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER
	 */
	public boolean isAssistantProjectOwnerAllowed() {
		
		if ( authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER ) {
			return true;
		}
		return false;
	}
	
	

	/**
	 * @return true if authAccessLevelIfNotLocked <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER
	 */
	public boolean isAssistantProjectOwnerIfProjectNotLockedAllowed() {
		
		if ( authAccessLevelForProjectIdsIfNotLocked <= AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER ) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @return true if authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_WRITE
	 */
	public boolean isSearchDeleteAllowed() {
		
		if ( authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_SEARCH_DELETE ) {
			return true;
		}
		return false;
	}
	

	/**
	 * @return true if authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_WRITE
	 */
	public boolean isWriteAllowed() {
		
		if ( authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_WRITE ) {
			
			return true;
		}
		
		return false;
	}
	
	
	

	/**
	 * NO access to user session with public access code for this project.
	 * 
	 * Read only level that allows user session with user logged on with ACCESS_LEVEL_LOGGED_IN_USER_READ_ONLY.
	 * 
	 * @return true if authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_LOGGED_IN_USER_READ_ONLY
	 */
	public boolean isUserReadAllowed() {
		
		if ( authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL_LOGGED_IN_USER_READ_ONLY ) {
			
			return true;
		}
		
		return false;
	}

	/**
	 * Read only level that allows user session with public access code for this project 
	 * to access this data.  User logged on with ACCESS_LEVEL_LOGGED_IN_USER_READ_ONLY
	 * also has access. 
	 * 
	 * @return true if authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY
	 */
	public boolean isPublicAccessCodeReadAllowed() {
		
		if ( authAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY ) {
			
			return true;
		}
		
		return false;
	}
	
	//////////////////////////////////////////////////////////////
	
	///////////  This block is for checking the specific access level
	
	
	
	/**
	 * User session with public access code for this project. 
	 * 
	 * @return true if authAccessLevel == AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY
	 */
	public boolean isPublicAccessCodeReadAccessLevel() {
		
		if ( authAccessLevel == AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY ) {
			
			return true;
		}
		
		return false;
	}

	
}
