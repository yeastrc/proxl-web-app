package org.yeastrc.xlink.www.constants;

public class WebConstants {

	
	//////////    Values stored in the Application Context and accessible across the application
	
	
	/**
	 * The context of the current web app 
	 */
	public static final String APP_CONTEXT_CONTEXT_PATH = "contextPath";
	

	
	/**
	 * Something to append to JS and CSS file query string to force re-download on web app startup
	 */
	public static final String APP_CONTEXT_JS_CSS_CACHE_BUST = "cacheBustValue";
	

	/**
	 * specific config_system table values
	 */
	public static final String CONFIG_SYSTEM_VALUES_HTML_KEY = "configSystemValues";
	
	

	//////////  Cookie Key values

	public static final String COOKIE_PROXL_DATA = "proxl_data";


	
	
	//////////    Values stored in the HTTP Session

	//  Main user for standard login
	public static final String SESSION_CONTEXT_USER_LOGGED_IN = "user";
	
	//  User object stored for Forgot Password Processing
	public static final String SESSION_CONTEXT_USER_FORGOT_RESET_PROCESSING = "user_forgotPassword";
	
	
	//////////     Values stored in the HTTP Request
	
	

	public static final String REQUEST_FULL_URL_WITH_QUERY_STRING = "intialIncomingURL";

	public static final String REQUEST_URL_ONLY_UP_TO_WEB_APP_CONTEXT = "intialIncomingURLUpToWebAppContext";

	
	/**
	 * The auth access level for this page, see object AuthAccessLevelForPage
	 */
	public static final String REQUEST_AUTH_ACCESS_LEVEL = "authAccessLevel";
	
	
	public static final String REQUEST_LOGGED_IN_USER_ID = "loggedInUserId";
	
	public static final String REQUEST_PROJECT_ID = "projectId";
	
	public static final String REQUEST_SHOW_STRUCTURE_LINK = "showStructureLink";
	

	
	/////////     Request Parameter Names
	
	public static final String PARAMETER_PROJECT_ID = "project_id";
	
	public static final String PARAMETER_ORIGINAL_REQUESTED_URL = "requestedURL";
	
	public static final String PARAMETER_RESET_PASSWORD_CODE = "code";
	
	
	//  This is required to match the field 'code' in the class UserInviteCreateUserForm
	public static final String PARAMETER_INVITE_CODE = "code";

	
	public static final String PARAMETER_PROJECT_READ_CODE = "code";
	
	public static final String PARAMETER_REDIRECT_AFTER_PROCESS_PROJECT_READ_CODE = "redirect";
	
	public static final String PARAMETER_REDIRECT_AFTER_PROCESS_PROJECT_READ_CODE_TRUE = "true";
	

	public static final String PARAMETER_SEARCHES_DETAILS_LIST_REQUEST_KEY = "searches_details_list";
	
	public static final String PARAMETER_CUTOFF_PAGE_DISPLAY_ROOT_REQUEST_ENTRY = "cutoffPageDisplayRootRequestEntry";

	
	/////////    Redirect URLs
	
	/**
	 * This must start with a "/"
	 * 
	 * What the filter will redirect to if there is no UserSessionObject in SESSION_CONTEXT_USER_LOGGED_IN
	 */
//	public static final String REDIRECT_URL_NO_WEB_SESSION = "/user_noSession.do";
}
