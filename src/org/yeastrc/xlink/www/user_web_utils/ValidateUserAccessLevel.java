package org.yeastrc.xlink.www.user_web_utils;

import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;

/**
 * 
 *
 */
public class ValidateUserAccessLevel {

	public static enum UserAccessLevelTypes { GLOBAL, PROJECT }
	
	
	/**
	 * Validates that that an access level is one of the ones coded for in this application
	 * 
	 * @param userAccessLevel
	 * @param isProjectLevelAccess
	 * @return
	 */
	public static boolean validateUserAccessLevel( int userAccessLevel, UserAccessLevelTypes userAccessLevelType ) {
		
		if ( userAccessLevelType == UserAccessLevelTypes.GLOBAL ) {
			
			if ( userAccessLevel == AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN 
					|| userAccessLevel == AuthAccessLevelConstants.ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER ) {
				
				return true;
			}
			
			
		} else {
			
			if ( userAccessLevel == AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER 
					|| userAccessLevel == AuthAccessLevelConstants.ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER ) {
				
				return true;
			}
		}
		

		return false;
		
	}
}
