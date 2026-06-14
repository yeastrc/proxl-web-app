package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;

/**
 * Translates a (Struts-style) forward NAME returned by a de-Strutsed action into the Spring target
 * (view name, "forward:/x.do", or "redirect:/x.do") using a per-action map supplied by the
 * controller. No dependency on the Struts API.
 *
 * <p>Used by controllers whose action logic is a plain class whose execute(...) returns a String
 * forward name. A null name means the action wrote the response itself (download) -> return null
 * (request handled).
 */
public final class SpringForwardResolver {

	private static final Logger log = LoggerFactory.getLogger( SpringForwardResolver.class );

	private SpringForwardResolver() {}

	public static String resolve( String forwardName, Map<String,String> forwardNameToSpringTarget ) {
		if ( forwardName == null ) {
			return null;   //  action handled the response (e.g. wrote a download/redirect)
		}
		String target = forwardNameToSpringTarget.get( forwardName );
		if ( target == null ) {
			log.error( "Unmapped forward name from de-Strutsed action: " + forwardName );
			return "generalError";
		}
		return target;
	}

	//  ====== Download-action support (the global forwards the download actions can return) ======
	//  Struts global-forward name -> Spring forward:/redirect: string (was the struts-config <global-forwards>).

	private static final Map<String,String> GLOBAL_DOWNLOAD_FORWARDS = new HashMap<>();
	static {
		GLOBAL_DOWNLOAD_FORWARDS.put( SpringMvcGlobalForwardNames.INVALID_REQUEST_DATA, SpringMvcForwards.INVALID_REQUEST_DATA );
		GLOBAL_DOWNLOAD_FORWARDS.put( SpringMvcGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS, SpringMvcForwards.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
		GLOBAL_DOWNLOAD_FORWARDS.put( SpringMvcGlobalForwardNames.NO_USER_SESSION, SpringMvcForwards.NO_USER_SESSION );
		GLOBAL_DOWNLOAD_FORWARDS.put( SpringMvcGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE, SpringMvcForwards.INSUFFICIENT_ACCESS_PRIVILEGE );
		GLOBAL_DOWNLOAD_FORWARDS.put( SpringMvcGlobalForwardNames.ACCOUNT_DISABLED, SpringMvcForwards.ACCOUNT_DISABLED );
		GLOBAL_DOWNLOAD_FORWARDS.put( SpringMvcGlobalForwardNames.HOME, SpringMvcForwards.HOME );
		GLOBAL_DOWNLOAD_FORWARDS.put( SpringMvcGlobalForwardNames.GENERAL_ERROR, "generalError" );  //  view name -> /WEB-INF/jsp-pages/generalError.jsp
	}

	/**
	 * Resolve the forward NAME returned by a de-Strutsed download action (those that write the file
	 * to the response and return null on success, or a global-forward name on error/auth failure).
	 * null name -> null (response already handled); unmapped name -> INVALID_REQUEST_DATA (logged).
	 */
	public static String resolveDownload( String forwardName ) {
		if ( forwardName == null ) {
			return null;   //  download / redirect written to the response; nothing for Spring to render
		}
		String target = GLOBAL_DOWNLOAD_FORWARDS.get( forwardName );
		if ( target == null ) {
			log.error( "Unmapped Struts forward name from de-Strutsed download action: " + forwardName );
			return SpringMvcForwards.INVALID_REQUEST_DATA;
		}
		return target;
	}
}
