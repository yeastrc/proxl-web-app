package org.yeastrc.xlink.www.user_account;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.UserSignupConstants;
import org.yeastrc.xlink.www.dao.TermsOfServiceTextVersionsDAO;
import org.yeastrc.xlink.www.dto.TermsOfServiceTextVersionsDTO;

/**
 * 
 *
 */
public class UserSignupPageInitAction  extends Action {
	
	private static final Logger log = Logger.getLogger(UserSignupPageInitAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {

//			String getRequestURL = request.getRequestURL().toString();
//			String getRequestURI = request.getRequestURI();
//			String getProtocol = request.getProtocol();
			
			String userSignupAllowWithoutInviteConfigValue =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY );

			if ( ! UserSignupConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY__TRUE.equals( userSignupAllowWithoutInviteConfigValue ) ) {
				
				return mapping.findForward( "generalError" ); //  Config not allow this page so show general error
			}
			

			//  Is terms of service enabled?
			String termsOfServiceEnabledString =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.TERMS_OF_SERVICE_ENABLED );
			
			boolean termsOfServiceEnabled = false;
			
			if ( ConfigSystemsValuesSharedConstants.TRUE.equals(termsOfServiceEnabledString) ) {
				
				termsOfServiceEnabled = true;
			}

			if ( termsOfServiceEnabled ) {

				TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTO = 
						TermsOfServiceTextVersionsDAO.getInstance().getLatest();
			
				request.setAttribute( "termsOfServiceTextVersion", termsOfServiceTextVersionsDTO );
			}
			
			
			return mapping.findForward( "Success" );


		} catch ( Exception e ) {

			String msg = "Exception caught: " + e.toString();

			log.error( msg, e );

			throw e;
		}
	}

		
}
