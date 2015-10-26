package org.yeastrc.xlink.www.user_account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.constants.WebConstants;

/**
 *  action to init insufficient_access_privilege.jsp
 *  
 *  If user in session and user disabled, forwards to /account_disabled.do
 *
 */
public class UserInsufficientAccessPrivilegePageInitAction extends Action {

	private static final Logger log = Logger.getLogger(UserInsufficientAccessPrivilegePageInitAction.class);
	

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
				
		try {

			// Get the session first.  
			HttpSession session = request.getSession();


			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

			if ( userSessionObject != null 
					&& userSessionObject.getUserDBObject() != null 
					&& userSessionObject.getUserDBObject().getAuthUser() != null ) {
				
				if ( ! userSessionObject.getUserDBObject().getAuthUser().isEnabled() ) {
				
					//  User is Disabled 

					return mapping.findForward( "UserDisabled" );
				}
				
				request.setAttribute( "userLoggedIn", true );
			}
			

			return mapping.findForward( "Success" );
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}
}
