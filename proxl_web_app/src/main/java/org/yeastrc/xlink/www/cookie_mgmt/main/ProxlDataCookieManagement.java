package org.yeastrc.xlink.www.cookie_mgmt.main;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.JSONStringCharsetConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cookie_mgmt.dto.ProxlDataCookieRootDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 
 *
 */
public class ProxlDataCookieManagement {
	
	private static final Logger log = LoggerFactory.getLogger( ProxlDataCookieManagement.class);
	
	//   !!!!!!!!!!!!!!!!!!!   Rewrite to use JSON in Cookie   Using ProxlDataCookieRootDTO
	/////        Need to build the cookie string independently so the login web service can add the cookie string to the response
	//  private constructor
	
	private ProxlDataCookieManagement() { }
	/**
	 * @return newly created instance
	 */
	public static ProxlDataCookieManagement getInstance() { 
		return new ProxlDataCookieManagement(); 
	}
	
//	/**
//	 * @param projectPublicAccessCode
//	 * @param httpRequest
//	 * @throws Exception
//	 */
//	public void clearPublicAccessCodesFromCookie( HttpServletRequest request, HttpServletResponse response ) throws Exception {
//		
//		throw new UnsupportedOperationException();
//
//	}
	
	/**
	 * @param projectPublicAccessCode
	 * @param httpRequest
	 * @throws Exception
	 */
	public void addPublicAccessCodeToCookie( String projectPublicAccessCode, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		List<String> projectPublicAccessCodeCookieList = getPublicAccessCodesCookieList( request );
		if ( ! projectPublicAccessCodeCookieList.contains(projectPublicAccessCode)) {
			projectPublicAccessCodeCookieList.add(projectPublicAccessCode);
		}
		storePublicAccessCodesListToCookie( projectPublicAccessCodeCookieList, request, response );
	}
	
	/**
	 * @param projectPublicAccessCodeCookieList
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void storePublicAccessCodesListToCookie( List<String> projectPublicAccessCodeCookieList, HttpServletRequest request, HttpServletResponse response ) throws Exception {
		String projectPublicAccessCodesString = "";
		if ( projectPublicAccessCodeCookieList != null && ( ! projectPublicAccessCodeCookieList.isEmpty() ) ) {
			ProxlDataCookieRootDTO proxlDataCookieRootDTO = new ProxlDataCookieRootDTO();
			proxlDataCookieRootDTO.setCodes( projectPublicAccessCodeCookieList );
			// build the JSON data structure for searches
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			ByteArrayOutputStream cookieStringJSONBAOS = new ByteArrayOutputStream( 100000 );
			mapper.writeValue( cookieStringJSONBAOS, proxlDataCookieRootDTO ); // where first param can be File, OutputStream or Writer
			projectPublicAccessCodesString = cookieStringJSONBAOS.toString( JSONStringCharsetConstants.JSON_STRING_CHARSET_UTF_8 );
		}
		storeProxlDataStringToCookie( projectPublicAccessCodesString, request, response );
	}
	
	/**
	 * @param httpRequest
	 * @return
	 * @throws Exception
	 */
	public List<String> getPublicAccessCodesCookieList( HttpServletRequest httpRequest ) throws Exception {
		String projectPublicAccessCodeCookieString = getProxlDataCookieString( httpRequest );
		if ( StringUtils.isEmpty( projectPublicAccessCodeCookieString ) ) {
			return new ArrayList<>();  // EARLY EXIT
		}
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		ProxlDataCookieRootDTO proxlDataCookieRootDTO = null;
		try {
			proxlDataCookieRootDTO = mapper.readValue( projectPublicAccessCodeCookieString, ProxlDataCookieRootDTO.class);
		} catch ( Exception e ) {
			String msg = "Exception parsing cookie into ProxlDataCookieRootDTO.  Returning empty  ProxlDataCookieRootDTO object." + e.toString();
			log.error( msg, e );
			return new ArrayList<>();  // EARLY EXIT
		}
		if ( proxlDataCookieRootDTO == null ) {
			String msg = "Error parsing cookie into ProxlDataCookieRootDTO. Parse returned 'null'.  Returning empty  ProxlDataCookieRootDTO object.";
			log.error( msg );
			return new ArrayList<>();  // EARLY EXIT
		}
		List<String> projectPublicAccessCodeCookieList = proxlDataCookieRootDTO.getCodes();
		return projectPublicAccessCodeCookieList;
	}
	
	/**
	 * @param httpRequest
	 * @return
	 * @throws Exception
	 */
	private String getProxlDataCookieString( HttpServletRequest httpRequest ) throws Exception {
		String projectPublicAccessCodeCookieString = null;
		Cookie[] cookies = httpRequest.getCookies();
		if( cookies != null ) {
			for (int i = 0; i < cookies.length ; i++ ) {
				if ( log.isDebugEnabled() ) {
					log.debug( "doFilter(...) cookies[i].getName() = " + cookies[i].getName() );
				}
				if ( WebConstants.COOKIE_PROXL_DATA.equals(  cookies[i].getName() ) ) {
					projectPublicAccessCodeCookieString = String.valueOf ( cookies[i].getValue() );
					break;
				}
			}
		}
		return projectPublicAccessCodeCookieString;
	}
	
	/**
	 * @param projectPublicAccessCode
	 * @param httpRequest
	 * @throws Exception
	 */
	private void storeProxlDataStringToCookie( String projectPublicAccessCodesString, HttpServletRequest request, HttpServletResponse response ) throws Exception {
//		String requestURI = request.getRequestURI();
	    // Create a client cookie containing the keyHash
		Cookie cookie = new Cookie( WebConstants.COOKIE_PROXL_DATA, projectPublicAccessCodesString );
		cookie.setPath( "/" );
		cookie.setMaxAge( Integer.MAX_VALUE );  // Set so it basically never expires
//		cookie.setMaxAge( -1 );  // Set so it is only a session cookie, it only lives as long as the browser is open
		//  Unsure if this is needed
//		if ( requestURI.contains( WebConstants.COOKIE_DOMAIN ) ) {
//			cookie.setDomain( WebConstants.COOKIE_DOMAIN );
//		}
//		String cookieAsString = cookie.toString();
		response.addCookie ( cookie );
	}
}
