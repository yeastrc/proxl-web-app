package org.yeastrc.xlink.www.user_account_webservices;

import java.nio.charset.StandardCharsets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dto.ZzUserDataMirrorDTO;
import org.yeastrc.xlink.www.constants.FieldLengthConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ZzUserDataMirrorDAO;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.user_session_management.UserSessionBuilder;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtChangePasswordRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtChangePasswordResponse;
import org.yeastrc.xlink.www.user_web_utils.PasswordTooLong_Check;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtManageAccountRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtManageAccountResponse;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;


@Path("/user")
public class AccountMaintService {
	private static final Logger log = LoggerFactory.getLogger( AccountMaintService.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/changeFirstName") 
	public AccountMaintResult changeFirstNameService(   
			@FormParam( "firstName" ) String firstName,
			@Context HttpServletRequest request )
	throws Exception {
		AccountMaintResult accountMaintResult = new AccountMaintResult();
		if ( StringUtils.isEmpty( firstName ) ) {
			log.warn( "AccountMaintService:  firstName empty: " + firstName );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( firstName.length() > FieldLengthConstants.FIRST_NAME_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  firstName too long: " + firstName );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
//		if (true)
//		throw new Exception("Forced Error");
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				//  No Access Allowed if not a logged in user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setUserMgmtUserId( userSession.getUserMgmtUserId() );
			userMgmtManageAccountRequest.setFirstName( firstName );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {

				accountMaintResult.setStatus(false);
				
				endSession_IfUserDisabledOrNotFound( userMgmtManageAccountResponse.isUserDisabled(), userMgmtManageAccountResponse.isUserIdNotFound(), userSession, request );
				return accountMaintResult; // EARLY RETURN
			}

			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userSession.getAuthUserId() );
			zzUserDataMirrorDTO.setFirstName( firstName );
			ZzUserDataMirrorDAO.getInstance().updateRecord( zzUserDataMirrorDTO );
			
			// Update session with new value for firstName
			UserSession userSession_Updated =
					UserSessionBuilder.getBuilder()
					.fromUserSession( userSession )
					.setFirstName( firstName )
					.build();
			UserSessionManager.getSinglesonInstance().setUserSession( userSession_Updated, request );
			
	        accountMaintResult.setStatus(true);
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/changeLastName") 
	public AccountMaintResult changeLastNameService(   
			@FormParam( "lastName" ) String lastName,
			@Context HttpServletRequest request )
	throws Exception {
		AccountMaintResult accountMaintResult = new AccountMaintResult();
		if ( StringUtils.isEmpty( lastName ) ) {
			log.warn( "AccountMaintService:  lastName empty: " + lastName );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( lastName.length() > FieldLengthConstants.LAST_NAME_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  lastName too long: " + lastName );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				//  No Access Allowed if not a logged in user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setUserMgmtUserId( userSession.getUserMgmtUserId() );
			userMgmtManageAccountRequest.setLastName( lastName );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {

				endSession_IfUserDisabledOrNotFound( userMgmtManageAccountResponse.isUserDisabled(), userMgmtManageAccountResponse.isUserIdNotFound(), userSession, request );
				accountMaintResult.setStatus(false);
				return accountMaintResult; // EARLY RETURN
			}

			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userSession.getAuthUserId() );
			zzUserDataMirrorDTO.setLastName( lastName );
			ZzUserDataMirrorDAO.getInstance().updateRecord( zzUserDataMirrorDTO );

			// Update session with new value for lastName
			UserSession userSession_Updated =
					UserSessionBuilder.getBuilder()
					.fromUserSession( userSession )
					.setLastName( lastName )
					.build();
			UserSessionManager.getSinglesonInstance().setUserSession( userSession_Updated, request );
			
			accountMaintResult.setStatus(true);
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/changeOrganization") 
	public AccountMaintResult changeOrganizationService(   
			@FormParam( "organization" ) String organization,
			@Context HttpServletRequest request )
	throws Exception {
		AccountMaintResult accountMaintResult = new AccountMaintResult();
		if ( StringUtils.isEmpty( organization ) ) {
			log.warn( "AccountMaintService:  organization empty: " + organization );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( organization.length() > FieldLengthConstants.ORGANIZATION_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  organization too long: " + organization );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				//  No Access Allowed if not a logged in user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setUserMgmtUserId( userSession.getUserMgmtUserId() );
			userMgmtManageAccountRequest.setOrganization( organization );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {

				endSession_IfUserDisabledOrNotFound( userMgmtManageAccountResponse.isUserDisabled(), userMgmtManageAccountResponse.isUserIdNotFound(), userSession, request );
				accountMaintResult.setStatus(false);
				return accountMaintResult; // EARLY RETURN
			}

			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userSession.getAuthUserId() );
			zzUserDataMirrorDTO.setOrganization( organization );
			ZzUserDataMirrorDAO.getInstance().updateRecord( zzUserDataMirrorDTO );
			
			// Update session with new value for organization
			UserSession userSession_Updated =
					UserSessionBuilder.getBuilder()
					.fromUserSession( userSession )
					.setOrganization( organization )
					.build();
			UserSessionManager.getSinglesonInstance().setUserSession( userSession_Updated, request );
			
	        accountMaintResult.setStatus(true);
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/changeEmail") 
	public AccountMaintResult changeEmailService(   
			@FormParam( "email" ) String email,
			@FormParam( "current_password" ) String currentPassword,  //  Required by User Mgmt to change the email.  Not length checked.
			@Context HttpServletRequest request )
	throws Exception {
		AccountMaintResult accountMaintResult = new AccountMaintResult();
		if ( StringUtils.isEmpty( email ) ) {
			log.warn( "AccountMaintService:  email empty: " + email );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( email.length() > FieldLengthConstants.EMAIL_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  email too long: " + email );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				//  No Access Allowed if not a logged in user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setUserMgmtUserId( userSession.getUserMgmtUserId() );
			userMgmtManageAccountRequest.setEmail( email );
			userMgmtManageAccountRequest.setCurrentPassword( currentPassword );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {

				endSession_IfUserDisabledOrNotFound( userMgmtManageAccountResponse.isUserDisabled(), userMgmtManageAccountResponse.isUserIdNotFound(), userSession, request );
				accountMaintResult.setStatus(false);
				if ( userMgmtManageAccountResponse.isCurrentPasswordRequired() ) {
					accountMaintResult.setCurrentPasswordRequired(true);
				}
				if ( userMgmtManageAccountResponse.isCurrentPasswordInvalid() ) {
					accountMaintResult.setCurrentPasswordInvalid(true);
				}
				if ( userMgmtManageAccountResponse.isDuplicateEmail() ) {
					accountMaintResult.setValueAlreadyExists(true);
				}
				return accountMaintResult;   //  EARLY EXIT
			}

			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userSession.getAuthUserId() );
			zzUserDataMirrorDTO.setEmail( email );
			ZzUserDataMirrorDAO.getInstance().updateRecord( zzUserDataMirrorDTO );
			
	        accountMaintResult.setStatus(true);
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/changeUsername") 
	public AccountMaintResult changeUsernameService(   
			@FormParam( "username" ) String username,
			@FormParam( "current_password" ) String currentPassword,  //  Required by User Mgmt to change the username.  Not length checked.
			@Context HttpServletRequest request )
	throws Exception {
		AccountMaintResult accountMaintResult = new AccountMaintResult();
		if ( StringUtils.isEmpty( username ) ) {
			log.warn( "AccountMaintService:  username empty: " + username );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( username.length() > FieldLengthConstants.USERNAME_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  username too long: " + username );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				//  No Access Allowed if not a logged in user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setUserMgmtUserId( userSession.getUserMgmtUserId() );
			userMgmtManageAccountRequest.setUsername( username );
			userMgmtManageAccountRequest.setCurrentPassword( currentPassword );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {

				endSession_IfUserDisabledOrNotFound( userMgmtManageAccountResponse.isUserDisabled(), userMgmtManageAccountResponse.isUserIdNotFound(), userSession, request );
				accountMaintResult.setStatus(false);
				if ( userMgmtManageAccountResponse.isCurrentPasswordRequired() ) {
					accountMaintResult.setCurrentPasswordRequired(true);
				}
				if ( userMgmtManageAccountResponse.isCurrentPasswordInvalid() ) {
					accountMaintResult.setCurrentPasswordInvalid(true);
				}
				if ( userMgmtManageAccountResponse.isDuplicateUsername() ) {
					accountMaintResult.setValueAlreadyExists(true);
				}
				return accountMaintResult;   //  EARLY EXIT
			}

			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userSession.getAuthUserId() );
			zzUserDataMirrorDTO.setUsername( username );
			ZzUserDataMirrorDAO.getInstance().updateRecord( zzUserDataMirrorDTO );

			// Update session with new value for username
			UserSession userSession_Updated =
					UserSessionBuilder.getBuilder()
					.fromUserSession( userSession )
					.setUsername( username )
					.build();
			UserSessionManager.getSinglesonInstance().setUserSession( userSession_Updated, request );
			
	        accountMaintResult.setStatus(true);
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	// TODO  Update Change Password
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/changePassword") 
	public AccountMaintResult changePasswordService(   
			@FormParam( "password" ) String password,
			@FormParam( "old_password" ) String oldPassword,
			@Context HttpServletRequest request )
	throws Exception {
		
		AccountMaintResult accountMaintResult = new AccountMaintResult();
		if ( StringUtils.isEmpty( oldPassword ) ) {
			log.warn( "AccountMaintService:  old_password empty" );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( StringUtils.isEmpty( password ) ) {
			log.warn( "AccountMaintService:  password empty" );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( PasswordTooLong_Check.getInstance().isPasswordTooLong( password ) ) {
			log.warn( "AccountMaintService:  password too long, length (characters): " + password.length()
					+ ", length (UTF-8 bytes): " + password.getBytes( StandardCharsets.UTF_8 ).length );
			accountMaintResult.setPasswordTooLong(true);
			accountMaintResult.setStatus(false);
			return accountMaintResult; //  EARLY EXIT
		}
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSession ) ) {
				//  No Access Allowed if not a logged in user
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			UserMgmtChangePasswordRequest userMgmtChangePasswordRequest = new UserMgmtChangePasswordRequest();
			userMgmtChangePasswordRequest.setUserMgmtUserId( userSession.getUserMgmtUserId() );
			userMgmtChangePasswordRequest.setOldPassword( oldPassword );
			userMgmtChangePasswordRequest.setNewPassword( password );
			userMgmtChangePasswordRequest.setUserRemoteIP( request.getRemoteAddr() );
			
			UserMgmtChangePasswordResponse userMgmtChangePasswordResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().changePassword( userMgmtChangePasswordRequest );
			
			if ( ! userMgmtChangePasswordResponse.isSuccess() ) {

				endSession_IfUserDisabledOrNotFound( userMgmtChangePasswordResponse.isUserDisabled(), userMgmtChangePasswordResponse.isUserIdNotFound(), userSession, request );
				
				accountMaintResult.setStatus(false);
				if ( userMgmtChangePasswordResponse.isOldPasswordNotValid() ) {
					accountMaintResult.setStatus(false);
					accountMaintResult.setOldPasswordInvalid(true);
					return accountMaintResult; //  EARLY EXIT
				}
				if ( userMgmtChangePasswordResponse.isPasswordTooLong() ) {
					accountMaintResult.setStatus(false);
					accountMaintResult.setPasswordTooLong(true);
					return accountMaintResult; //  EARLY EXIT
				}
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			accountMaintResult.setStatus(true);
			return accountMaintResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "changePasswordService(...) Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	


	/**
	 * If User Mgmt reports the user disabled or the user id not found:  End the session and return HTTP status for no session.
	 * 
	 * @param userDisabled
	 * @param userIdNotFound
	 * @param userSession
	 * @param request
	 */
	private void endSession_IfUserDisabledOrNotFound( boolean userDisabled, boolean userIdNotFound, UserSession userSession, HttpServletRequest request ) {
		
		if ( userDisabled ) {
			log.info( "User session ended:  User Mgmt reports the user is disabled.  authUserId: " + userSession.getAuthUserId() );
		} else if ( userIdNotFound ) {
			log.error( "User session ended:  User Mgmt reports the user id is not found.  authUserId: " + userSession.getAuthUserId()
					+ ", userMgmtUserId: " + userSession.getUserMgmtUserId() );
		} else {
			return; // EARLY RETURN
		}
		
		//  Invalidate session
		UserSessionManager.getSinglesonInstance().invalidateUserSession( request );
		
		//  Return HTTP status so JS reloads page and user is sent to sign in
		throw new WebApplicationException(
				Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
				.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
				.build()
				);
	}

	/**
	 * This is returned from the web service AccountMaintService
	 *
	 */
	public static class AccountMaintResult {

		private boolean status = false;

		private boolean valueAlreadyExists = false;
		private boolean oldPasswordInvalid = false;
		private boolean passwordTooLong = false;
		private boolean currentPasswordRequired = false;
		private boolean currentPasswordInvalid = false;

		public boolean isOldPasswordInvalid() {
			return oldPasswordInvalid;
		}

		public void setOldPasswordInvalid(boolean oldPasswordInvalid) {
			this.oldPasswordInvalid = oldPasswordInvalid;
		}

		public boolean isValueAlreadyExists() {
			return valueAlreadyExists;
		}

		public void setValueAlreadyExists(boolean valueAlreadyExists) {
			this.valueAlreadyExists = valueAlreadyExists;
		}

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		public boolean isPasswordTooLong() {
			return passwordTooLong;
		}
		public void setPasswordTooLong(boolean passwordTooLong) {
			this.passwordTooLong = passwordTooLong;
		}
		public boolean isCurrentPasswordRequired() {
			return currentPasswordRequired;
		}
		public void setCurrentPasswordRequired(boolean currentPasswordRequired) {
			this.currentPasswordRequired = currentPasswordRequired;
		}
		public boolean isCurrentPasswordInvalid() {
			return currentPasswordInvalid;
		}
		public void setCurrentPasswordInvalid(boolean currentPasswordInvalid) {
			this.currentPasswordInvalid = currentPasswordInvalid;
		}
	}

}
