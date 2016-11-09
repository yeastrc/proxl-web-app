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

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;


/**
 * Log errors sent from the Browser Javascript code
 *
 */
@Path("/log_error")
public class LogBrowserJavascriptErrorService {

	private static final Logger log = Logger.getLogger( LogBrowserJavascriptErrorService.class );

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
		
		log.error( "Error encountered on browser: message: " + logBrowserJavascriptErrorsRequest.errorMsg
				+ "\n stack: " + logBrowserJavascriptErrorsRequest.stackString
				+ "\n userAgent: " + logBrowserJavascriptErrorsRequest.userAgent
				+ "\n browserURL: " + logBrowserJavascriptErrorsRequest.browserURL );
		
		LogBrowserJavascriptErrorResult logBrowserJavascriptErrorResult = new LogBrowserJavascriptErrorResult();
		logBrowserJavascriptErrorResult.status = true;
		return logBrowserJavascriptErrorResult;
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
