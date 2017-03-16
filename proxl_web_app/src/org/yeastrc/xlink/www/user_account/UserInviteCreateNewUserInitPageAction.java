package org.yeastrc.xlink.www.user_account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dto.TermsOfServiceTextVersionsDTO;
import org.yeastrc.xlink.www.terms_of_service.GetTermsOfServiceTextForDisplay;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserInviteTrackingCode;

/**
 * 
 *
 */
public class UserInviteCreateNewUserInitPageAction extends Action {

	private static final Logger log = Logger.getLogger(UserInviteCreateNewUserInitPageAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {

			String inviteCode = request.getParameter( WebConstants.PARAMETER_INVITE_CODE );

			ValidateUserInviteTrackingCode validateUserInviteTrackingCode = ValidateUserInviteTrackingCode.getInstance( inviteCode );

			if ( ! validateUserInviteTrackingCode.validateInviteTrackingCode() ) {

				String errorMsgKey = validateUserInviteTrackingCode.getErrorMsgKey();
	
				ActionMessages messages = new ActionMessages();
				messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( errorMsgKey ) );
				saveErrors( request, messages );
				
				return mapping.findForward("Failure");
			}
			
			//  Is terms of service enabled?
			String termsOfServiceEnabledString =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.TERMS_OF_SERVICE_ENABLED );
			if ( ConfigSystemsValuesSharedConstants.TRUE.equals( termsOfServiceEnabledString ) ) {
				// Terms of service Is enabled, put on page
				TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTO = 
						GetTermsOfServiceTextForDisplay.getInstance().getLatestTermsOfServiceTextForDisplay();
				request.setAttribute( "termsOfServiceTextVersion", termsOfServiceTextVersionsDTO );
			}
			
			return mapping.findForward("Success");
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}

	}

}
