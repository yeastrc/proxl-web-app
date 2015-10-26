package org.yeastrc.xlink.www.constants;

public class WebServiceErrorMessageConstants {

	public static final String NO_SESSION_TEXT = "no_session";
	public static final javax.ws.rs.core.Response.Status NO_SESSION_STATUS_CODE = javax.ws.rs.core.Response.Status.UNAUTHORIZED;
	
	public static final String NOT_AUTHORIZED_TEXT = "not_authorized";
	public static final javax.ws.rs.core.Response.Status NOT_AUTHORIZED_STATUS_CODE = javax.ws.rs.core.Response.Status.FORBIDDEN;
	
	public static final String INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT = "invalid_search_list_across_projects_text";
	public static final javax.ws.rs.core.Response.Status 
		INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE = javax.ws.rs.core.Response.Status.FORBIDDEN;

	public static final String INVALID_SEARCH_LIST_NOT_IN_DB_TEXT = "invalid_search_list_not_in_db_text";
	public static final javax.ws.rs.core.Response.Status INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE = javax.ws.rs.core.Response.Status.BAD_REQUEST;
	
	public static final String INVALID_PARAMETER_TEXT = "invalid_parameter";
	public static final javax.ws.rs.core.Response.Status INVALID_PARAMETER_STATUS_CODE = javax.ws.rs.core.Response.Status.BAD_REQUEST;
	
	public static final String INTERNAL_SERVER_ERROR_TEXT = "INTERNAL_SERVER_ERROR";
	public static final javax.ws.rs.core.Response.Status INTERNAL_SERVER_ERROR_STATUS_CODE = javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
	
	
}
