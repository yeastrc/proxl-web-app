package org.yeastrc.xlink.www.user_account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.spring_controllers.SpringActionMessages;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserResetPasswordCode;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;

/**
 * 
 *
 */
public class UserResetPasswordProcessCodeAction {

	private static final Logger log = LoggerFactory.getLogger( UserResetPasswordProcessCodeAction.class);

	public String execute( HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {
			UserSessionManager.getSinglesonInstance().invalidateUserSession( request );

			String resetPasswordTrackingCode = request.getParameter( WebConstants.PARAMETER_RESET_PASSWORD_CODE );
			
			ValidateUserResetPasswordCode validateUserResetPasswordCode = ValidateUserResetPasswordCode.getInstance( resetPasswordTrackingCode );
			
			if ( ! validateUserResetPasswordCode.validateResetPasswordCode() ) {
			
				String errorMsgKey = validateUserResetPasswordCode.getErrorMsgKey();

				SpringActionMessages.setErrorMessageKey( request, errorMsgKey );
				return "Failure";
			}

			request.setAttribute( "resetPasswordTrackingCode", resetPasswordTrackingCode );

			return "Success";
			
			
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}

	}

		
}
