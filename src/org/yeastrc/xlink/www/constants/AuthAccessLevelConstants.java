package org.yeastrc.xlink.www.constants;

/**
 * 
 *
 */
public class AuthAccessLevelConstants {
	

	public static final int ACCESS_LEVEL_ADMIN = 0;
	

	public static final int ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER = 25;
	
	
	//  There is an implicit level of "Project Level Access" where the field "auth_user.user_access_level" is set to NULL
	
	//      This implies that outside of the projects the user has access to, they have read only access.
	

	public static final int ACCESS_LEVEL_PROJECT_OWNER = 30;
	
	
//	Assistant project owner -  Used for access level label "Researcher"
//
//	Most project owner tasks can be done by the assistant. 
//	They cannot change the project owner. 
//	They can add/invite and remove users with the same or lower access.
//	They cannot activate the public access code or change it.

	public static final int ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER = 38;
	
	
	public static final int ACCESS_LEVEL_SEARCH_DELETE = 40;
	
	
	public static final int ACCESS_LEVEL_WRITE = 50;
	
	//  TODO    new level for create, modify, delete other people's comments
	//  TODO    new level for create, modify, delete own comments

	
	/**
	 * This is the access level used logged in user with read only access
	 */
	public static final int ACCESS_LEVEL_LOGGED_IN_USER_READ_ONLY = 90;
	

	
	/**
	 * This is the access level used Public Access Code and Public projects
	 * 
	 */
	public static final int ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY = 99;
	
	
	public static final int ACCESS_LEVEL_NONE = 9999;
	
	
	///////////////
	
	//////   Defaults
	
	
	
	public static final int ACCESS_LEVEL_DEFAULT_FOR_NO_AUTH_SHARED_OBJECT_RECORD = ACCESS_LEVEL_NONE;
	
	
	//  This is a Default for a user created via a project invite
	
	///        This can be set to INTEGER null, which is sort of an in between value
	
	public static final int ACCESS_LEVEL_DEFAULT_USER_CREATED_VIA_PROJECT_INVITE = ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER;
}
