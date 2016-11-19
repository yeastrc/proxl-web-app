package org.yeastrc.xlink.www.url_shortner_share_page.webservices;


import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.URLShortenerDAO;
import org.yeastrc.xlink.www.dao.URLShortenerDAO.LogDuplicateSQLException;
import org.yeastrc.xlink.www.dto.URLShortenerDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.yeastrc.xlink.www.user_account.UserSessionObject;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Longs;



@Path("/sharePageShortenURL")
public class SharePageURLShortenerCreateAndSaveWebService {

	private static final Logger log = Logger.getLogger(SharePageURLShortenerCreateAndSaveWebService.class);
	
	private static final int RETRY_COUNT_MAX_ON_DUPLICATE_SHORT_KEY = 10;
	
	/**
	 * Webservice result
	 *
	 */
	public static class SharePageURLShortenerCreateAndSaveWebServiceResult {
		
		private boolean status;
		private String shortenedURLKey;
		private String shortenedURL;
		
		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
		}
		public String getShortenedURLKey() {
			return shortenedURLKey;
		}
		public void setShortenedURLKey(String shortenedURLKey) {
			this.shortenedURLKey = shortenedURLKey;
		}
		public String getShortenedURL() {
			return shortenedURL;
		}
		public void setShortenedURL(String shortenedURL) {
			this.shortenedURL = shortenedURL;
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/createAndSaveShortenedURL")
	public SharePageURLShortenerCreateAndSaveWebServiceResult createAndSaveSharePageURLShortenerWebService( 
//			@FormParam("searchId") List<Integer> searchId, 
			@FormParam("pageUrl") String pageUrl, 
			@Context HttpServletRequest httpServletRequest ) throws Exception {


		SharePageURLShortenerCreateAndSaveWebServiceResult webserviceResult = new SharePageURLShortenerCreateAndSaveWebServiceResult();

		try {

			String contextPathJSVar = CurrentContext.getCurrentWebAppContext();
			
			String contextPathJSVarWithTrailingSlash = contextPathJSVar + "/"; // Add to correctly find context in URL

			int contextStart = pageUrl.indexOf( contextPathJSVarWithTrailingSlash );
			int pageUrlAfterContextStartPosition = contextStart + contextPathJSVarWithTrailingSlash.length();

			String firstCharAfterContext = pageUrl.substring( pageUrlAfterContextStartPosition, pageUrlAfterContextStartPosition + 1 );

			if ( "/".equals(firstCharAfterContext ) ) {

				pageUrlAfterContextStartPosition++;  //  increment to move past "/"
			}

			String pageUrlAfterContext = pageUrl.substring( pageUrlAfterContextStartPosition );
			
			String urlUpToAndIncludingContext = pageUrl.substring(0, pageUrlAfterContextStartPosition );

			
			URLShortenerDTO urlShortenerDTO = URLShortenerDAO.getInstance().getForURL( pageUrlAfterContext );
			
			if ( urlShortenerDTO == null ) {
				
				urlShortenerDTO = new URLShortenerDTO();

				Integer authUserId = null;

				HttpSession session = httpServletRequest.getSession();
				UserSessionObject userSessionObject  =
						(UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
				if ( userSessionObject != null ) {
					XLinkUserDTO  xlinkUserDTO = userSessionObject.getUserDBObject();
					if ( xlinkUserDTO != null ) {
						AuthUserDTO authUser = xlinkUserDTO.getAuthUser();
						if ( authUser != null ) {
							authUserId = authUser.getId();
						}
					}
				}
				
				urlShortenerDTO.setUrl( pageUrlAfterContext );
				urlShortenerDTO.setAuthUserId( authUserId );
				
				boolean saveSuccessful = false;
				int saveAttemptCounter = 0;
				
				while ( ( ! saveSuccessful ) ) {

					saveAttemptCounter++;
					String shortenedUrlKey = null;
					try {
						//  Do retries since may create shortenedUrlKey that collides with existing records
						
						shortenedUrlKey = getShortenedKey();
						
						urlShortenerDTO.setShortenedUrlKey(shortenedUrlKey);
						
						LogDuplicateSQLException logDuplicateSQLException = LogDuplicateSQLException.FALSE;

						if ( saveAttemptCounter >=  RETRY_COUNT_MAX_ON_DUPLICATE_SHORT_KEY ) {
							logDuplicateSQLException = LogDuplicateSQLException.TRUE;
						}
						//  Only log insert Duplicate errors if last try
						URLShortenerDAO.getInstance().save( urlShortenerDTO, logDuplicateSQLException );

						saveSuccessful = true;

					} catch ( SQLException sqlException ) {
						
						String exceptionMessage = sqlException.getMessage();
						
						if ( exceptionMessage != null && exceptionMessage.startsWith( "Duplicate entry" ) ) {

							if ( saveAttemptCounter >=  RETRY_COUNT_MAX_ON_DUPLICATE_SHORT_KEY ) {
								String msg = "Exceeded max number of attempts to insert and get Duplicate Key error."
										+ "  Max # = " + RETRY_COUNT_MAX_ON_DUPLICATE_SHORT_KEY
										+ ", current shortenedUrlKey: " + shortenedUrlKey;
								log.error( msg, sqlException );
								throw new ProxlWebappInternalErrorException(msg);
							}
						}
					}
				}
			}

			webserviceResult.shortenedURLKey = urlShortenerDTO.getShortenedUrlKey();
		
			String shortenedURL = urlUpToAndIncludingContext + "go?" + webserviceResult.shortenedURLKey;

			webserviceResult.setStatus(true);
			webserviceResult.shortenedURL = shortenedURL;

			return webserviceResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}

	}
	
	/**
	 * @return
	 */
	private String getShortenedKey() {

		StringBuilder randomStringSB = new StringBuilder( 16 );
		
		for ( int j = 0; j < 2; j++ ) {
			double tosKeyMultiplier = Math.random();

			if ( tosKeyMultiplier < 0.5 ) {

				tosKeyMultiplier += 0.5;
			}

			long tosKeyLong = (long) ( System.currentTimeMillis() * tosKeyMultiplier );

			// Google Guava classes BaseEncoding and Longs
			String encodedLong = BaseEncoding.base64().encode( Longs.toByteArray(tosKeyLong) );

			// Drop first 6 characters and last character
			String encodedLongExtract = encodedLong.substring( 6, encodedLong.length() - 1 );
			randomStringSB.append( encodedLongExtract );
		}
		String randomString = randomStringSB.toString();
		
		return randomString;
	}
	
}
