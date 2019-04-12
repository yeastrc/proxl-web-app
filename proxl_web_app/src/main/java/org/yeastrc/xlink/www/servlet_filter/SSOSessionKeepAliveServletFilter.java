package org.yeastrc.xlink.www.servlet_filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * This filter will be the first called for a request
 * 
 * This pings the SSO once an hour to keep the session alive
 *
 */
public class SSOSessionKeepAliveServletFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(  SSOSessionKeepAliveServletFilter.class );
	
	private static final long HOUR_IN_MILLISECONDS = 1 * 60 * 60 * 1000;
	//  TODO  TEMP 1 second
//	private static final long HOUR_IN_MILLISECONDS = 1 * 1000;
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

//		HttpServletRequest httpRequest = (HttpServletRequest) request;
//		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		//  IF uncomment this, use class UserSessionManager to get and update the session
//
//
//		if ( userSession != null ) {
//			if ( userSession.getUserLoginSessionKey() != null ) {
//				//  User session key from SSO server is not null
//				
//				long currentTime = System.currentTimeMillis();
//				if ( ( userSession.getLastPingToSSOServer() + HOUR_IN_MILLISECONDS ) < currentTime ) {
//					userSession.setLastPingToSSOServer( currentTime );
//					UserMgmtSessionKeyAliveWebserviceRequest userMgmtSessionKeyAliveWebserviceRequest = new UserMgmtSessionKeyAliveWebserviceRequest();
//					userMgmtSessionKeyAliveWebserviceRequest.setSessionKey( userSession.getUserLoginSessionKey() );
//					try {
//						//  return status is false if not alive
//						UserMgmtSessionKeyAliveWebserviceResponse userMgmtSessionKeyAliveWebserviceResponse =
//								UserMgmtCentralWebappWebserviceAccess.getInstance().sessionKeyAlive( userMgmtSessionKeyAliveWebserviceRequest );
//						
//					} catch ( Exception e ) {
//						String loggedInUserId = "UNKNOWN";
//						try {
//							loggedInUserId = Integer.toString( userSession.getAuthUserId() );
//						} catch( Exception e2 ) {
//							
//						}
//						String msg = "Exception from Call to keep session alive on SSO server for user id: " + loggedInUserId;
//						log.error( msg, e );
//						//  Not throw exception since Proxl is still running fine
////						throw new ServletException( msg, e );
//					}
//				}
//			}
//		}

		chain.doFilter(request, response);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
		
		
	}
	
	

}
