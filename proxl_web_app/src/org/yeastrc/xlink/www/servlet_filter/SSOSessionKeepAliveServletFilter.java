package org.yeastrc.xlink.www.servlet_filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSessionKeyAliveWebserviceRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSessionKeyAliveWebserviceResponse;

/**
 * This filter will be the first called for a request
 * 
 * This pings the SSO once an hour to keep the session alive
 *
 */
public class SSOSessionKeepAliveServletFilter implements Filter {

	private static final Logger log = Logger.getLogger( SSOSessionKeepAliveServletFilter.class );
	
	private static final long HOUR_IN_MILLISECONDS = 1 * 60 * 60 * 1000;
	//  TODO  TEMP 5 seconds
//	private static final long HOUR_IN_MILLISECONDS = 5 * 1000;
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
//		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		HttpSession httpSession = httpRequest.getSession();

		UserSessionObject userSessionObject 
		= (UserSessionObject) httpSession.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

		if ( userSessionObject != null ) {
			if ( userSessionObject.getUserLoginSessionKey() != null ) {
				//  User session key from SSO server is not null
				
				long currentTime = System.currentTimeMillis();
				if ( ( userSessionObject.getLastPingToSSOServer() + HOUR_IN_MILLISECONDS ) < currentTime ) {
					userSessionObject.setLastPingToSSOServer( currentTime );
					UserMgmtSessionKeyAliveWebserviceRequest userMgmtSessionKeyAliveWebserviceRequest = new UserMgmtSessionKeyAliveWebserviceRequest();
					userMgmtSessionKeyAliveWebserviceRequest.setSessionKey( userSessionObject.getUserLoginSessionKey() );
					try {
						//  return status is false if not alive
						UserMgmtSessionKeyAliveWebserviceResponse userMgmtSessionKeyAliveWebserviceResponse =
								UserMgmtCentralWebappWebserviceAccess.getInstance().sessionKeyAlive( userMgmtSessionKeyAliveWebserviceRequest );
						
					} catch ( Exception e ) {
						String loggedInUserId = "UNKNOWN";
						try {
							loggedInUserId = Integer.toString( userSessionObject.getUserDBObject().getAuthUser().getId() );
						} catch( Exception e2 ) {
							
						}
						String msg = "Exception from Call to keep session alive on SSO server for user id: " + loggedInUserId;
						log.error( msg, e );
						//  Not throw exception since Proxl is still running fine
//						throw new ServletException( msg, e );
					}
				}
			}
		}

		chain.doFilter(request, response);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
		
		
	}
	
	

}
