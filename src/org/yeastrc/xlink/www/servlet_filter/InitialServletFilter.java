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

//import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.yeastrc.xlink.www.user_account.UserSessionObject;

/**
 * This filter will be the first called for a request and does initial setup
 *
 */
public class InitialServletFilter implements Filter {

//	private static final Logger log = Logger.getLogger(InitialServletFilter.class);
	
	
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
		
		
		UserSessionObject userSessionObject 
		= (UserSessionObject) httpSession.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

		if ( userSessionObject != null ) {

			/////  If a user is logged in, copy the user id to a request variable for placement on all pages 

			if ( userSessionObject.getUserDBObject() != null && userSessionObject.getUserDBObject().getAuthUser() != null ) {
			
				int loggedInUserId = userSessionObject.getUserDBObject().getAuthUser().getId();

				httpRequest.setAttribute( WebConstants.REQUEST_LOGGED_IN_USER_ID, loggedInUserId );
			}
		}

		chain.doFilter(request, response);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
		
		
	}
	
	

}
