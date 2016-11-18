package org.yeastrc.xlink.www.url_shortner_share_page.url_handler_servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.URLShortenerDAO;
import org.yeastrc.xlink.www.dto.URLShortenerDTO;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;

/**
 * 
 *
 */
public class SharePageURLHandlerServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( SharePageURLHandlerServlet.class );
	
	private static final long serialVersionUID = 1L;
	
	private static final String REDIRECT_ON_SHORTCUT_NOT_FOUND = "shortcutNotFound.do";

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		
		try {
			String queryString = httpServletRequest.getQueryString();

			URLShortenerDTO urlShortenerDTO = URLShortenerDAO.getInstance().getForShortenedURLKey(queryString);
			String urlToRedirectTo = null;
			if ( urlShortenerDTO != null ) {
				urlToRedirectTo = urlShortenerDTO.getUrl();
			}

			StringBuilder redirectURLSB = new StringBuilder( 1000 );

			redirectURLSB.append( CurrentContext.getCurrentWebAppContext() );
			redirectURLSB.append( "/" );

			if ( urlToRedirectTo != null ) {
				redirectURLSB.append( urlToRedirectTo );
			} else {
				redirectURLSB.append( REDIRECT_ON_SHORTCUT_NOT_FOUND );
			}

			String redirectURL = redirectURLSB.toString();

			httpServletResponse.sendRedirect( redirectURL );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in servlet";
			log.error( msg, e );
			
			throw new ServletException(e);
		}
	}


}
