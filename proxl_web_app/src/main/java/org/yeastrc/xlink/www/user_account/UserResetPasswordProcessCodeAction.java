package org.yeastrc.xlink.www.user_account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserResetPasswordCode;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;

/**
 * 
 *
 */
public class UserResetPasswordProcessCodeAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( UserResetPasswordProcessCodeAction.class);

	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {
			UserSessionManager.getSinglesonInstance().invalidateUserSession( request );

			String resetPasswordTrackingCode = request.getParameter( WebConstants.PARAMETER_RESET_PASSWORD_CODE );
			
			ValidateUserResetPasswordCode validateUserResetPasswordCode = ValidateUserResetPasswordCode.getInstance( resetPasswordTrackingCode );
			
			if ( ! validateUserResetPasswordCode.validateResetPasswordCode() ) {
			
				String errorMsgKey = validateUserResetPasswordCode.getErrorMsgKey();
	
				ActionMessages messages = new ActionMessages();
				messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( errorMsgKey ) );
				saveErrors( request, messages );
				return mapping.findForward("Failure");
			}

			request.setAttribute( "resetPasswordTrackingCode", resetPasswordTrackingCode );

			return mapping.findForward("Success");
			
			
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}

	}

		
}
