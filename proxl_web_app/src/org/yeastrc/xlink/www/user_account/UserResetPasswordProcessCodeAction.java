package org.yeastrc.xlink.www.user_account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserResetPasswordCode;
import org.yeastrc.xlink.www.constants.WebConstants;

/**
 * 
 *
 */
public class UserResetPasswordProcessCodeAction extends Action {
	
	private static final Logger log = Logger.getLogger(UserResetPasswordProcessCodeAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {


			// Get their session first.  
			HttpSession session = request.getSession();

			session.removeAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

			
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
