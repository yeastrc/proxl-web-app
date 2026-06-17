package org.yeastrc.xlink.www.spring_controllers__logic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
/**
 * 
 *
 */
public class ListProjectsAction {
	
	private static final Logger log = LoggerFactory.getLogger( ListProjectsAction.class);
	
		public String execute( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request, response );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return SpringMvcGlobalForwardNames.NO_USER_SESSION;
			}
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( userSession == null ) {
				//  No User session 
				return SpringMvcGlobalForwardNames.NO_USER_SESSION;
			}
			if ( ( ! userSession.isActualUser() ) ) {
			//  No Actual User Logged On  
				return SpringMvcGlobalForwardNames.NO_USER_SESSION;
			}
			//  Test access to application no project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				return SpringMvcGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE;
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );

			Boolean userEnabledAppSpecific = AuthUserDAO.getInstance().getUserEnabledAppSpecific( userSession.getAuthUserId() );
			if ( userEnabledAppSpecific == null ) {
				//  No Access Allowed since not a logged in user
				return SpringMvcGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE;
			}
			if ( ! userEnabledAppSpecific.booleanValue() ) {
				//  No Access Allowed since user is disabled
				return SpringMvcGlobalForwardNames.ACCOUNT_DISABLED;
			}

			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithoutProjectId( request );
			return "Success";
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
