package org.yeastrc.xlink.www.browser_type_checking;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.common.AccessControl_GetUserSession_RefreshAccessEnabled;
import org.yeastrc.xlink.www.user_session_management.UserSession;

public class IsBrowserIsInternetExplorer {

	private static final Logger log = LoggerFactory.getLogger( IsBrowserIsInternetExplorer.class);

	private static IsBrowserIsInternetExplorer _INSTANCE = new IsBrowserIsInternetExplorer();

	//  private constructor
	private IsBrowserIsInternetExplorer() { }
	/**
	 * @return Singleton instance
	 */
	public static IsBrowserIsInternetExplorer getSingletonInstance() { 
		return _INSTANCE; 
	}


	/**
	 * @param request
	 */
	public boolean isBrowserIsInternetExplorer( ServletRequest request ) {

		try {

			HttpServletRequest httpRequest = (HttpServletRequest) request;

			String userAgentString = httpRequest.getHeader("User-Agent");
			
			if ( userAgentString != null ) {

				//  Works up to IE 10
				boolean isIE = userAgentString.contains("MSIE");

				//For IE 11
				boolean isIE11 = userAgentString.contains("rv:11.0");


				if ( isIE || isIE11 ) {

					try {
						String requestURL = httpRequest.getRequestURL().toString();

						if ( requestURL.contains(".do") ) {

							//  If a struts action, log the access

							if ( log.isDebugEnabled() ) {

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
					} catch ( Throwable e ) {
						log.error( "Error getting username. Exception ignored. ", e );
						//  Swallow any exceptions getting username
					}

					return true;
				}
			}
			return false;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
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
	//  Username may or may not be populated
	return userSession.getUsername();
}

}
