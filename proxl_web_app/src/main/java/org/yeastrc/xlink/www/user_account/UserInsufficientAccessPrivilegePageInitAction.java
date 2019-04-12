package org.yeastrc.xlink.www.user_account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;

/**
 *  action to init insufficient_access_privilege.jsp
 *  
 *  If user in session and user disabled, forwards to /account_disabled.do
 *
 */
public class UserInsufficientAccessPrivilegePageInitAction extends Action {

	private static final Logger log = LoggerFactory.getLogger( UserInsufficientAccessPrivilegePageInitAction.class);
	
	private static final String REQUEST_ADMIN_EMAIL_ADDRESS = "adminEmailAddress";
	

	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
				
		try {

			UserSession userSession = UserSessionManager.getSinglesonInstance().getUserSession(request);

			if ( userSession != null ) {
				
				if ( ( ! userSession.isEnabledAppSpecific() )
						|| ( ! userSession.isEnabled() ) ) {
				
					//  User is Disabled 

					return mapping.findForward( "UserDisabled" );
				}
				
				request.setAttribute( "userLoggedIn", true );
			}

			String adminEmailAddress =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.ADMIN_EMAIL_ADDRESS_KEY );
			request.setAttribute( REQUEST_ADMIN_EMAIL_ADDRESS, adminEmailAddress );

			return mapping.findForward( "Success" );
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}
}
