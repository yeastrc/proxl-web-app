package org.yeastrc.xlink.www.url_shortner_share_page.webservices;

import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.constants.URLShortenerWhiteListConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.URLShortenerAssociatedProjectSearchIdDAO;
import org.yeastrc.xlink.www.dao.URLShortenerDAO;
import org.yeastrc.xlink.www.dao.URLShortenerDAO.LogDuplicateSQLException;
import org.yeastrc.xlink.www.dto.URLShortenerAssociatedProjectSearchIdDTO;
import org.yeastrc.xlink.www.dto.URLShortenerDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Longs;

/**
 * 
 *
 */
@Path("/sharePageShortenURL")
public class SharePageURLShortenerCreateAndSaveWebService {
	
	private static final Logger log = LoggerFactory.getLogger( SharePageURLShortenerCreateAndSaveWebService.class);
	
	private static final int RETRY_COUNT_MAX_ON_DUPLICATE_SHORT_KEY = 10;
	
	/**
	 * @param webServiceRequest
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	@Path("/createAndSaveShortenedURL")
	public WebServiceResult createAndSaveSharePageURLShortenerWebService( 
			WebServiceRequest webServiceRequest, 
			@Context HttpServletRequest httpServletRequest ) throws Exception {
		if ( webServiceRequest == null ) {
			String msg = "webServiceRequest == null. getRemoteAddr: " + httpServletRequest.getRemoteAddr();
			log.warn(msg);
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
		    	        .build()
		    	        );
		}
		String pageUrl = webServiceRequest.getPageUrl();
		if ( StringUtils.isEmpty( pageUrl ) ) {
			String msg = "'pageUrl' not provided.  getRemoteAddr: " + httpServletRequest.getRemoteAddr();
			log.warn(msg);
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
		    	        .build()
		    	        );
		}
		WebServiceResult webserviceResult = new WebServiceResult();
		try {
			String contextPathJSVar = CurrentContext.getCurrentWebAppContext();
			String contextPathJSVarWithTrailingSlash = contextPathJSVar + "/"; // Add to correctly find context in URL
			int contextStart = pageUrl.indexOf( contextPathJSVarWithTrailingSlash );
			if ( contextStart == -1 ) {
				String msg = "Context not found in URL Parameter, request rejected with 400 code.  contextPathJSVarWithTrailingSlash: " 
						+ contextPathJSVarWithTrailingSlash
						+ ", URL Parameter: " + pageUrl 
						+ ", getRemoteAddr: " + httpServletRequest.getRemoteAddr();
				log.warn(msg);
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
			    	        .build()
			    	        );
			}
			int pageUrlAfterContextStartPosition = contextStart + contextPathJSVarWithTrailingSlash.length();
			String firstCharAfterContext = pageUrl.substring( pageUrlAfterContextStartPosition, pageUrlAfterContextStartPosition + 1 );
			if ( "/".equals(firstCharAfterContext ) ) {
				pageUrlAfterContextStartPosition++;  //  increment to move past "/"
			}
			String pageUrlAfterContext = pageUrl.substring( pageUrlAfterContextStartPosition );
			boolean foundInWhiteList = false;
			for ( String whiteListEntry : URLShortenerWhiteListConstants.URL_SHORTENER_STARTS_WITH_WHITE_LIST ) {
				if ( pageUrlAfterContext.startsWith(whiteListEntry) ) {
					foundInWhiteList = true;
					break;
				}
			}
			if ( ! foundInWhiteList ) {
				String msg = "URL Parameter after context does not start with any string in white list, request rejected with 400 code.  contextPathJSVarWithTrailingSlash: " 
						+ contextPathJSVarWithTrailingSlash
						+ ", URL Parameter: " + pageUrl
						+ ", getRemoteAddr: " + httpServletRequest.getRemoteAddr();
				log.warn(msg);
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
			    	        .build()
			    	        );
			}
			String urlUpToAndIncludingContext = pageUrl.substring(0, pageUrlAfterContextStartPosition );
			//  Check if URL already has a shortened URL Key for it
			URLShortenerDTO urlShortenerDTO = URLShortenerDAO.getInstance().getForURL( pageUrlAfterContext );
			if ( urlShortenerDTO == null ) {
				//  No shortened URL Key so create one and insert it
				urlShortenerDTO = new URLShortenerDTO();
				//  Get Auth user Id if a user account is signed in
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
				//  Loop to do retries since may create shortenedUrlKey that collides with existing records
				while ( ( ! saveSuccessful ) ) {
					saveAttemptCounter++;
					String shortenedUrlKey = null;
					try {
						shortenedUrlKey = getShortenedKey();
						urlShortenerDTO.setShortenedUrlKey(shortenedUrlKey);
						//  Only log insert Duplicate error in DAO if last attempt
						LogDuplicateSQLException logDuplicateSQLException = LogDuplicateSQLException.FALSE;
						if ( saveAttemptCounter >=  RETRY_COUNT_MAX_ON_DUPLICATE_SHORT_KEY ) {
							logDuplicateSQLException = LogDuplicateSQLException.TRUE;
						}
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
				if ( webServiceRequest.projectSearchIdList != null ) {
					for ( int projectSearchId : webServiceRequest.projectSearchIdList ) {
						URLShortenerAssociatedProjectSearchIdDTO urlShortenerAssociatedProjectSearchIdDTO = new URLShortenerAssociatedProjectSearchIdDTO();
						urlShortenerAssociatedProjectSearchIdDTO.setUrlShortenerId( urlShortenerDTO.getId() );
						urlShortenerAssociatedProjectSearchIdDTO.setProjectSearchId( projectSearchId );
						URLShortenerAssociatedProjectSearchIdDAO.getInstance().save( urlShortenerAssociatedProjectSearchIdDTO );
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
		randomString = randomString.replace( '/', 'Z' ); // Replace all '/' since is a URL path separator
	    randomString = randomString.replace( '\\', 'X' ); // Replace all '\' Browser replaces it with '/' which is a URL path separator
		return randomString;
	}
	
	/**
	 * Webservice request JSON Mapping
	 *
	 */
	public static class WebServiceRequest {
		private String pageUrl;
		private List<Integer> projectSearchIdList; 
		public String getPageUrl() {
			return pageUrl;
		}
		public void setPageUrl(String pageUrl) {
			this.pageUrl = pageUrl;
		}
		public List<Integer> getProjectSearchIdList() {
			return projectSearchIdList;
		}
		public void setProjectSearchIdList(List<Integer> projectSearchIdList) {
			this.projectSearchIdList = projectSearchIdList;
		}
	}
	
	/**
	 * Webservice result
	 *
	 */
	public static class WebServiceResult {
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
}
