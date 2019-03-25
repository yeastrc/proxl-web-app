package org.yeastrc.xlink.www.browser_type_logging;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.internal_services.UpdateAuthUserUserAccessLevelEnabled;
import org.yeastrc.xlink.www.user_account.UserSessionObject;

public class LogIfBrowserIsInternetExplorer {

	private static final Logger log = Logger.getLogger(LogIfBrowserIsInternetExplorer.class);
	
	private static LogIfBrowserIsInternetExplorer _INSTANCE = new LogIfBrowserIsInternetExplorer();
	
	//  private constructor
	private LogIfBrowserIsInternetExplorer() { }
	/**
	 * @return Singleton instance
	 */
	public static LogIfBrowserIsInternetExplorer getSingletonInstance() { 
		return _INSTANCE; 
	}
	

	/**
	 * @param request
	 */
	public void logBrowserIsInternetExplorerIfConfigured( ServletRequest request ) {
		
		if ( log.isDebugEnabled() ) {
			try {
				
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				
				String userAgentString = httpRequest.getHeader("User-Agent");

				//  Works up to IE 10
				boolean isIE = userAgentString.contains("MSIE");
				
				//For IE 11
				boolean isIE11 = userAgentString.contains("rv:11.0");
				
				
				if ( isIE || isIE11 ) {
					
					String requestURL = httpRequest.getRequestURL().toString();
					
					if ( requestURL.contains(".do") ) {
						
						String userSessionUsername = "";
						
						String username = null;
						
						try {
							username = getUsername( httpRequest );
						} catch ( Exception e ) {
							log.error( "Error getting username" );
						}
						
						if ( username != null ) {
							userSessionUsername = "\t, session username: \t" + username;
						}
						
						log.debug( "Browser is Internet Explorer.  "
								+ "UserAgent: \t" + userAgentString
								+ "\t, requested URL: \t" + requestURL
								+ "\t, remote IP: \t" + request.getRemoteAddr()
								+ userSessionUsername );
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * @param httpRequest
	 * @return - null if no username
	 */
	private String getUsername( HttpServletRequest httpRequest ) {
	
		HttpSession session = httpRequest.getSession();
		UserSessionObject userSessionObject = (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
		if ( userSessionObject == null ) {
			//  No User session 
			return null;
		}
		if ( userSessionObject.getUserDBObject() != null && userSessionObject.getUserDBObject().getAuthUser() != null  ) {
			//  have a logged in user
			AuthUserDTO authUser = null;
			if ( userSessionObject.getUserDBObject() != null && userSessionObject.getUserDBObject().getAuthUser() != null ) {
				authUser = userSessionObject.getUserDBObject().getAuthUser();
				if ( authUser != null ) {
					return authUser.getUsername();
				}
			}
		}
		
		//  No User session 
		return null;
	}
	
}
