package org.yeastrc.xlink.www.project_short_name_label.handler_servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.searcher.ProjectIdForProjectShortNameSearcher;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
/**
 * 
 *
 */
public class ProjectLabelServlet extends HttpServlet {
	
	private static final Logger log = LoggerFactory.getLogger(  ProjectLabelServlet.class );
	private static final long serialVersionUID = 1L;
	
	//  Remove this from start of pathInfo to get custom label
	private static final String PATH_SEPARATOR_AFTER_SERVLET_URL_PATH = "/";

	private static final String REQUEST_PROJECT_ID_FROM_VIEW_PROJECT_ACTION = "projectId_FromViewProjectAction";
	private static final String REQUEST_ADMIN_EMAIL_ADDRESS = "adminEmailAddress";
	
//	private static final String REDIRECT_ON_LABEL_NOT_FOUND = "labelNotFound.do";
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		try {
			//  pathInfo is == to whatever comes after the "/<characters>" in the <url-pattern>
			//  So for:  <url-pattern>/p/*</url-pattern>
			//    It is what comes after the "/p".
			//    If nothings comes after the "/p" it is null.
			//    Otherwise it always starts with "/", which is to be ignored since that is a delimiter.
			
			String pathInfo = httpServletRequest.getPathInfo();
			
			if ( StringUtils.isEmpty( pathInfo ) ) {
				//  No custom label
				//  Redirect or Forward to error page
				throw new RuntimeException("pathInfo is empty");
			}

			if ( ! pathInfo.startsWith( PATH_SEPARATOR_AFTER_SERVLET_URL_PATH ) ) {
				//  Path after url-pattern does not start with "/"
				//  Redirect or Forward to error page
				throw new RuntimeException("Path after url-pattern does not start with '/'");
			}
			
			String projectShortName = pathInfo.substring( 1 ); // Chop off leading "/"
			
			Integer projectId =
					ProjectIdForProjectShortNameSearcher.getInstance().getProjectIdForProjectShortName( projectShortName );
			
			if ( projectId != null ) {
				// Found Project Id so redirect to it
				StringBuilder redirectURLSB = new StringBuilder( 1000 );
				redirectURLSB.append( CurrentContext.getCurrentWebAppContext() );
				redirectURLSB.append( "/viewProject.do?project_id=" );
				redirectURLSB.append( projectId.toString() );
				String redirectURL = redirectURLSB.toString();
				httpServletResponse.sendRedirect( redirectURL );
				
				return;  // EARLY EXIT
			}
			
			//  projectShortName not found.  Show error page

			httpServletRequest.setAttribute( REQUEST_PROJECT_ID_FROM_VIEW_PROJECT_ACTION, projectShortName );

			String adminEmailAddress =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.ADMIN_EMAIL_ADDRESS_KEY );
			httpServletRequest.setAttribute( REQUEST_ADMIN_EMAIL_ADDRESS, adminEmailAddress );
			
			httpServletRequest
			.getRequestDispatcher("/WEB-INF/jsp-pages/projectNotFound.jsp")
			.forward( httpServletRequest, httpServletResponse );
			
//			throw new Exception("NOT FOUND");
			
			//  Look up customProjectLabel and redirect to main Project Page
			
//			int z = 0;
			
//			URLShortenerDTO urlShortenerDTO = URLShortenerDAO.getInstance().getForShortenedURLKey(queryString);
//			String urlToRedirectTo = null;
//			if ( urlShortenerDTO != null ) {
//				urlToRedirectTo = urlShortenerDTO.getUrl();
//			}
//			StringBuilder redirectURLSB = new StringBuilder( 1000 );
//			redirectURLSB.append( CurrentContext.getCurrentWebAppContext() );
//			redirectURLSB.append( "/" );
//			if ( urlToRedirectTo != null ) {
//				redirectURLSB.append( urlToRedirectTo );
//			} else {
//				throw new ProxlWebappDataException( "Label Not Found: "  );
////				redirectURLSB.append( REDIRECT_ON_LABEL_NOT_FOUND );
//			}
//			String redirectURL = redirectURLSB.toString();
//			httpServletResponse.sendRedirect( redirectURL );
		} catch ( Exception e ) {
			String msg = "Exception in servlet";
			log.error( msg, e );
			throw new ServletException(e);
		}
	}
}
