package org.yeastrc.xlink.www.user_account;

import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.spring_controllers.SpringActionMessages;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.www.access_control.common.AccessControl_GetUserSession_RefreshAccessEnabled;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.database_update_with_transaction_services.AddOrUpdateProjectAccessExistingUserUsingDBTransactionService;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserInviteTrackingCode;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;
/**
 * 
 *
 */
public class UserInviteProcessCodeAction {

	private static final Logger log = LoggerFactory.getLogger( UserInviteProcessCodeAction.class);

	public String execute( HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			String userIP = request.getRemoteAddr();
			String inviteTrackingCode = request.getParameter( WebConstants.PARAMETER_INVITE_CODE );
			ValidateUserInviteTrackingCode validateUserInviteTrackingCode = ValidateUserInviteTrackingCode.getInstance( inviteTrackingCode );
			if ( ! validateUserInviteTrackingCode.validateInviteTrackingCode() ) {
				String errorMsgKey = validateUserInviteTrackingCode.getErrorMsgKey();
				SpringActionMessages.setErrorMessageKey( request, errorMsgKey );
				return "Failure";
			}
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO =  validateUserInviteTrackingCode.getAuthUserInviteTrackingDTO();
			authUserInviteTrackingDTO.setUseIP( userIP );
			
			UserSession userSession =
					AccessControl_GetUserSession_RefreshAccessEnabled.getSinglesonInstance()
					.getUserSession_RefreshAccessEnabled( request );
			
			if ( TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				if ( authUserInviteTrackingDTO.getInvitedSharedObjectId() != null ) {
					//  Logged in And a Project Id
					//  update access for this user for this project
					AuthSharedObjectUsersDAO authSharedObjectUsersDAO = AuthSharedObjectUsersDAO.getInstance();
					AddOrUpdateProjectAccessExistingUserUsingDBTransactionService addOrUpdateProjectAccessExistingUserUsingDBTransactionService = AddOrUpdateProjectAccessExistingUserUsingDBTransactionService.getInstance();
					AuthSharedObjectUsersDTO authSharedObjectUsersDTO = new AuthSharedObjectUsersDTO();
					authSharedObjectUsersDTO.setUserId( userSession.getAuthUserId() );
					authSharedObjectUsersDTO.setSharedObjectId( authUserInviteTrackingDTO.getInvitedSharedObjectId() );
					authSharedObjectUsersDTO.setAccessLevel( authUserInviteTrackingDTO.getInvitedUserAccessLevel() );
					try {
						addOrUpdateProjectAccessExistingUserUsingDBTransactionService.updateUserAddAuthSharedObjectUsersDTO( authSharedObjectUsersDTO, authUserInviteTrackingDTO );
					} catch ( SQLException sqlException ) {
						String exceptionMessage = sqlException.getMessage();
						if ( exceptionMessage != null && exceptionMessage.startsWith( "Duplicate entry" ) ) {
							AuthSharedObjectUsersDTO existingAuthSharedObjectUsersDTO = authSharedObjectUsersDAO.getAuthSharedObjectUsersDTOForSharedObjectIdAndUserId( authUserInviteTrackingDTO.getInvitedSharedObjectId(), userSession.getAuthUserId() );
							if ( existingAuthSharedObjectUsersDTO != null ) {
								if ( authUserInviteTrackingDTO.getInvitedUserAccessLevel()  < existingAuthSharedObjectUsersDTO.getAccessLevel() ) {
									//  New invite has better access level so update the access level
									addOrUpdateProjectAccessExistingUserUsingDBTransactionService.updateUserUpdateUserAccessLevel( authSharedObjectUsersDTO , authUserInviteTrackingDTO );
								} else {
									//  User already has access to this project.  Mark invite complete
									String msg = "User already has access to this project.  Mark invite complete: authUserInviteTrackingDTO.getId(): " + authUserInviteTrackingDTO.getId();
									log.warn( msg );
									int authUserIdUsingInvite = userSession.getAuthUserId();
									AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingDTO.getId(), authUserIdUsingInvite, userIP );
									return "GoToProjectList";
//									ActionErrors errors = new ActionErrors();
//									errors.add("username", new ActionMessage("error.invite.existing.user.already.have.access"));
//									saveErrors( request, errors );
//									
//									return "Failure";
								}
							}
						} else {
							String msg = "SQL Exception: ";
							log.error( msg, sqlException );
							throw sqlException;
						}
					}
					return "GoToProjectList";
				} else {
					//   Logged in and NO Project Id
					//  Do Nothing
					//  User already has access to this app.  Mark invite complete
					String msg = "User already has access to this App.  Mark invite complete: authUserInviteTrackingDTO.getId(): " + authUserInviteTrackingDTO.getId();
					log.warn( msg );
					int authUserIdUsingInvite = userSession.getAuthUserId();
					AuthUserInviteTrackingDAO.getInstance().updateUsedInviteFields( authUserInviteTrackingDTO.getId(), authUserIdUsingInvite, userIP );
					return "GoToProjectList";
				}
			}   
			//  Not Logged In
			if ( authUserInviteTrackingDTO.getInvitedSharedObjectId() != null ) {
				//  Not logged in and processing a project id
				return "ProjectInviteLandingPage";
			}
			//  Not Logged in and No project Id 
			return "AddNewUser";
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			SpringActionMessages.setErrorMessageKey( request, "error.invite.process.code.general" );
			return "Failure";
		}
	}
}
