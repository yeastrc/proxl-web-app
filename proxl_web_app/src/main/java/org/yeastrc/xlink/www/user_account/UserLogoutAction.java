package org.yeastrc.xlink.www.user_account;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;

/**
 * 
 *
 */
public class UserLogoutAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( UserLogoutAction.class);

	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {
			UserSessionManager.getSinglesonInstance().invalidateUserSession( request );

			return mapping.findForward( "Success" );
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}

		
}
