package org.yeastrc.xlink.www.user_account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;
/**
 *  action to init account_disabled.jsp
 *  
 *  If user in session, remove it
 *
 */
public class UserDisabledPageInitAction {
	
	private static final Logger log = LoggerFactory.getLogger( UserDisabledPageInitAction.class);
	
	/* (non-Javadoc)
	 */
		public String execute( HttpServletRequest request, HttpServletResponse response )
					  throws Exception {
		try {
			UserSessionManager.getSinglesonInstance().invalidateUserSession(request);
			return "Success";
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
