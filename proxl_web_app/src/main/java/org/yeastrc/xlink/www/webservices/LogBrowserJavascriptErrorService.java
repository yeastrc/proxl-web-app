package org.yeastrc.xlink.www.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.common.AccessControl_GetUserSession_RefreshAccessEnabled;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_session_management.UserSession;


/**
 * Log errors sent from the Browser Javascript code
 *
 */
@Path("/log_error")
public class LogBrowserJavascriptErrorService {

	private static final Logger log = LoggerFactory.getLogger(  LogBrowserJavascriptErrorService.class );

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/logBrowserJavascriptError")
	public LogBrowserJavascriptErrorResult logBrowserJavascriptError(
			LogBrowserJavascriptErrorsRequest logBrowserJavascriptErrorsRequest,
			@Context HttpServletRequest request ) throws Exception {

		if ( ! logBrowserJavascriptErrorsRequest.fdajklweRWOIUOPOP ) {
			
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  return 400 error
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
					.build()
					);	
		}

		String userSessionUsername = "";
		
		String username = null;
		
		try {
			username = getUsername( request );
		} catch ( Exception e ) {
			log.error( "Error getting username" );
		}
		
		if ( username != null ) {
			userSessionUsername = "\t, session username: \t" + username;
		}
		
		log.error( "Error encountered on browser: message: " + logBrowserJavascriptErrorsRequest.errorMsg
				+ "\n stack: " + logBrowserJavascriptErrorsRequest.stackString
				+ "\n userAgent: " + logBrowserJavascriptErrorsRequest.userAgent
				+ "\n browserURL: " + logBrowserJavascriptErrorsRequest.browserURL
				+ "\n Remote IP: " + request.getRemoteAddr()
				+ userSessionUsername );
		
		LogBrowserJavascriptErrorResult logBrowserJavascriptErrorResult = new LogBrowserJavascriptErrorResult();
		logBrowserJavascriptErrorResult.status = true;
		return logBrowserJavascriptErrorResult;
	}

	/**
	 * @param httpRequest
	 * @return - null if no username
	 * @throws Exception 
	 */
	private String getUsername( HttpServletRequest httpRequest ) throws Exception {

		UserSession userSession =
				AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
				.getUserSession_RefreshAccessEnabled( httpRequest );
		
		if ( userSession == null ) {
			//  No User session 
			return null;
		}
		return userSession.getUsername();
	}
	
	public static class LogBrowserJavascriptErrorsRequest {
		
		private String errorMsg;
		private String stackString;
		private String userAgent;
		private String browserURL;
		private boolean fdajklweRWOIUOPOP;
		
		
		public String getErrorMsg() {
			return errorMsg;
		}
		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}
		public String getStackString() {
			return stackString;
		}
		public void setStackString(String stackString) {
			this.stackString = stackString;
		}
		public boolean isFdajklweRWOIUOPOP() {
			return fdajklweRWOIUOPOP;
		}
		public void setFdajklweRWOIUOPOP(boolean fdajklweRWOIUOPOP) {
			this.fdajklweRWOIUOPOP = fdajklweRWOIUOPOP;
		}
		public String getUserAgent() {
			return userAgent;
		}
		public void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}
		public String getBrowserURL() {
			return browserURL;
		}
		public void setBrowserURL(String browserURL) {
			this.browserURL = browserURL;
		}
	}

	public static class LogBrowserJavascriptErrorResult {
		
		private boolean status;

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}
		
	}
}
