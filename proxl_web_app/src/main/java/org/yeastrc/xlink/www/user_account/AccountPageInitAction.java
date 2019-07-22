package org.yeastrc.xlink.www.user_account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSessionKeyAliveWebserviceRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtSessionKeyAliveWebserviceResponse;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;
/**
 * 
 *
 */
public class AccountPageInitAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( AccountPageInitAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request, response );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			{
				UserMgmtSessionKeyAliveWebserviceRequest userMgmtSessionKeyAliveWebserviceRequest = new UserMgmtSessionKeyAliveWebserviceRequest();
				userMgmtSessionKeyAliveWebserviceRequest.setSessionKey( userSession.getUserMgmtSessionKey() );
				
				UserMgmtSessionKeyAliveWebserviceResponse userMgmtSessionKeyAliveWebserviceResponse =
						UserMgmtCentralWebappWebserviceAccess.getInstance().sessionKeyAlive(userMgmtSessionKeyAliveWebserviceRequest);
				if ( userMgmtSessionKeyAliveWebserviceResponse.isSessionKeyNotValid() ) {
					
					//  Session Key not valid in User Mgmt.  Force User to log back in to create new session in User Mgmt
					
					//  Invalidate session
					UserSessionManager.getSinglesonInstance().invalidateUserSession( request );

					// Send to login page
					return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
				}
			
				if ( ! userMgmtSessionKeyAliveWebserviceResponse.isSuccess() ) {
					throw new ProxlWebappInternalErrorException( "Response from User Mgmt Not Success" );
				}
			}
			GetPageHeaderData.getInstance().getPageHeaderDataWithoutProjectId( request );
			
			UserDataForPage userDataForPage = new UserDataForPage();
			userDataForPage.firstName = userSession.getFirstName();
			userDataForPage.lastName = userSession.getLastName();
			userDataForPage.email = userSession.getEmail();
			userDataForPage.organization = userSession.getOrganization();
			userDataForPage.username = userSession.getUsername();
			
			request.setAttribute( "loggedInUser", userDataForPage );
			return mapping.findForward( "Success" );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class UserDataForPage {
		
		private String firstName;
		private String lastName;
		private String email;
		private String organization;
		private String username;
		
		public String getFirstName() {
			return firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public String getEmail() {
			return email;
		}
		public String getOrganization() {
			return organization;
		}
		public String getUsername() {
			return username;
		}
		
	}
}
