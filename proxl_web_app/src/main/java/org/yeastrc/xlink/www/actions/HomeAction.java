package org.yeastrc.xlink.www.actions;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.RefreshAllowedReadAccessProjectIds;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;
/**
 * Home page action
 *
 */
public class HomeAction extends Action {
	
	private static final Logger log = Logger.getLogger(HomeAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			// Get the session first.  
			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request, response );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to application no project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( authAccessLevel == null ) {
				//  No Access Level provided
				String msg = "No Access Level provided (authAccessLevel == null), throwing exception";
				log.warn( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed 
				return mapping.findForward( StrutsGlobalForwardNames.LOGIN );
//				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			boolean accountLoggedIn = false; 
			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			// testIsUserSignedIn(...) method from A_TestUserLoggedInBaseAction
			if ( TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSessionObject ) ) {
				accountLoggedIn = true;
				request.setAttribute( "accountLoggedIn", true );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			if ( userSessionObject != null && ( ! accountLoggedIn ) ) {
				//  User is not logged in, use project ids from public access code 
				RefreshAllowedReadAccessProjectIds.refreshAllowedReadAccessProjectIds( userSessionObject );
				//  allow read access based on project id access code
				Set<Integer> allowedReadAccessProjectIds = userSessionObject.getAllowedReadAccessProjectIds();
				if ( ! allowedReadAccessProjectIds.isEmpty() ) {
					request.setAttribute( "allowedReadAccessProjectIdsNotEmpty", true );
				}
			}

			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			
			return mapping.findForward( "Success" );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			return mapping.findForward( StrutsGlobalForwardNames.GENERAL_ERROR );
		}
	}
}
