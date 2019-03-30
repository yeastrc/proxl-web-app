package org.yeastrc.xlink.www.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;

/**
 * 
 *
 */
public class CacheDataClearAllAction extends Action {

	private static final Logger log = LoggerFactory.getLogger(  CacheDataClearAllAction.class );
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
				GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request, response );

		if ( accessAndSetupWebSessionResult.isNoSession() ) {
			//  No User session 
			response.setStatus( 401 );
			return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
		}
		UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
		if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSessionObject ) ) {
			//  No User session 
			response.setStatus( 401 );
			return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
		}
		AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
		if ( authAccessLevel == null || ( ! authAccessLevel.isAdminAllowed() ) ) {
			response.setStatus( 403 );
			return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
		}
		
		try {
			log.warn("CacheDataClearAllAction execute called");
			CachedDataCentralRegistry.getInstance().clearAllCacheData();
			return mapping.findForward( "Success" );
			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			response.setStatus( 500 );
			return mapping.findForward( StrutsGlobalForwardNames.GENERAL_ERROR );
		}
		
	}
}
