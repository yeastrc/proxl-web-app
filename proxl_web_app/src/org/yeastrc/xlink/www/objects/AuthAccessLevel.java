package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;

/**
 * 
 * This holds the auth level a given request, it is created in the action
 */
public class AuthAccessLevel {

	private int authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL_NONE;
	
	/**
	 * Preserve access level if the project was not locked
	 */
	private int authAccessLevelIfNotLocked = AuthAccessLevelConstants.ACCESS_LEVEL_NONE;

	/**
	 * Constuctor
	 */
	public AuthAccessLevel(  ) {
		
	}
	
	
	/**
	 * Constuctor
	 * @param authAccessLevel
	 */
	public AuthAccessLevel( int authAccessLevel ) {
		
		this.authAccessLevel = authAccessLevel;
		this.authAccessLevelIfNotLocked = authAccessLevel;
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
		
		if ( authAccessLevelIfNotLocked <= AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN ) {
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
		
		if ( authAccessLevelIfNotLocked <= AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER ) {
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
		
		if ( authAccessLevelIfNotLocked <= AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER ) {
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
	
	
	
	//////////////////////////////////////////////////////////////
	
	
	public int getAuthAccessLevel() {
		return authAccessLevel;
	}

	public void setAuthAccessLevel(int authAccessLevel) {
		this.authAccessLevel = authAccessLevel;
		this.authAccessLevelIfNotLocked = authAccessLevel;
	}
	

	public int getAuthAccessLevelIfNotLocked() {
		return authAccessLevelIfNotLocked;
	}


	public void setAuthAccessLevelIfNotLocked(int authAccessLevelIfNotLocked) {
		this.authAccessLevelIfNotLocked = authAccessLevelIfNotLocked;
	}


}
