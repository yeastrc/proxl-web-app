package org.yeastrc.xlink.www.servlet_filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.access_control.common.AccessControl_GetUserSession_RefreshAccessEnabled;
import org.yeastrc.xlink.www.browser_type_checking.IsBrowserIsInternetExplorer;
import org.yeastrc.xlink.www.constants.StrutsActionPathsConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.yeastrc.xlink.www.user_session_management.UserSession;

/**
 * This filter will be the first called for a request and does initial setup
 *
 */
public class InitialServletFilter implements Filter {
	
	private static final Logger log = LoggerFactory.getLogger( InitialServletFilter.class );
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String requestURL = httpRequest.getRequestURL().toString();
		String queryString = httpRequest.getQueryString();
		if (queryString == null){
			queryString = "";
		}
		String urlWithQueryString = requestURL + "?" + queryString;
		httpRequest.setAttribute( WebConstants.REQUEST_FULL_URL_WITH_QUERY_STRING, urlWithQueryString );
		//  find the URL up to and including the web app context.
		String webAppContext = CurrentContext.getCurrentWebAppContext();
		String webAppContextSearchString = webAppContext + "/";  // require trailing "/"
		int indexWebAppContextInRequestURL = requestURL.indexOf( webAppContextSearchString );
		int indexPlusOneEndOfWebAppContextInRequestURL  = indexWebAppContextInRequestURL + webAppContext.length();
		//  Does NOT include slash after web app context
		String requestURLIncludingWebAppContext = requestURL.substring(0, indexPlusOneEndOfWebAppContextInRequestURL);
		httpRequest.setAttribute( WebConstants.REQUEST_URL_ONLY_UP_TO_WEB_APP_CONTEXT, requestURLIncludingWebAppContext );

		UserSession userSession;
		try {
			userSession = AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
			.getUserSession_RefreshAccessEnabled( httpRequest );
		} catch (Exception e) {
			log.error( "Fail calling AccessControl_GetUserSession_RefreshAccessEnabled ", e );
			throw new ServletException( e );
		}
		
		if ( userSession != null ) {
			/////  If a user is logged in, copy the user id to a request variable for placement on all pages 
			if ( userSession.getAuthUserId() != null ) {
				int loggedInUserId = userSession.getAuthUserId();
				httpRequest.setAttribute( WebConstants.REQUEST_LOGGED_IN_USER_ID, loggedInUserId );
			}
		}
		
		if ( requestURL.contains(".do") ) {
			// Only do for struts actions
			if ( IsBrowserIsInternetExplorer.getSingletonInstance().isBrowserIsInternetExplorer( request ) ) {

				if ( ! requestURL.contains( StrutsActionPathsConstants.BROWSER_INTERNET_EXPLORER_NOT_SUPPORTED_MESSAGE_PAGE ) )  {
					//  Only if URL not what redirecting to
					final String redirectURL = 
							httpRequest.getContextPath() 
							+ StrutsActionPathsConstants.BROWSER_INTERNET_EXPLORER_NOT_SUPPORTED_MESSAGE_PAGE;
					httpResponse.sendRedirect( redirectURL );
					return;
				}
			}
		}
				
		chain.doFilter(request, response);
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
