package org.yeastrc.xlink.www.user_account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.common.AccessControl_GetUserSession_RefreshAccessEnabled;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;
/**
 * 
 *
 */
public class ManageUsersPageInitAction {
	
	private static final Logger log = LoggerFactory.getLogger( ManageUsersPageInitAction.class);
	
	/* (non-Javadoc)
	 */
		public String execute( HttpServletRequest request, HttpServletResponse response )
					  throws Exception {
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request, response );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return SpringMvcGlobalForwardNames.NO_USER_SESSION;
			}

			UserSession userSession =
					AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
					.getUserSession_RefreshAccessEnabled( request );
			
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				//  No User session 
				return SpringMvcGlobalForwardNames.NO_USER_SESSION;
			}
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( authAccessLevel == null || ( ! authAccessLevel.isAdminAllowed() ) ) {
				return SpringMvcGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE;
			}
			GetPageHeaderData.getInstance().getPageHeaderDataWithoutProjectId( request );
			return "Success";
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
