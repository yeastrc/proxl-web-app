package org.yeastrc.xlink.www.user_account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.constants.WebConstants;
/**
 *  action to init account_disabled.jsp
 *  
 *  If user in session, remove it
 *
 */
public class UserDisabledPageInitAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( UserDisabledPageInitAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
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
					&& userSessionObject.getUserDBObject() != null ) {
				//  Remove user from session 
				userSessionObject.setUserDBObject(null);
			}
			return mapping.findForward( "Success" );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
