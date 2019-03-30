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
 * 
 *
 */
public class UserLogoutAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( UserLogoutAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {


			// Get their session first.  
			HttpSession session = request.getSession();

			session.removeAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

			session.invalidate();



			return mapping.findForward( "Success" );
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}

		
}
