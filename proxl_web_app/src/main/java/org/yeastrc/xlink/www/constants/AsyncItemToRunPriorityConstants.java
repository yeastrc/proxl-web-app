package org.yeastrc.xlink.www.constants;

/**
 * All Priorities assigned to AsyncItemToRun objects
 * 
 * The priority is not currently used but is implemented as a centralized place to alter them
 *
 */
public class AsyncItemToRunPriorityConstants {

	private static final int DEFAULT_PRIORITY = 10;
	
	public static final int PRIORITY_WHEN_NOT_SPECIFIED = DEFAULT_PRIORITY;
	
	public static final int PRIORITY_AUTH_USER_UPDATE_LAST_LOGIN_IP = DEFAULT_PRIORITY;
}
