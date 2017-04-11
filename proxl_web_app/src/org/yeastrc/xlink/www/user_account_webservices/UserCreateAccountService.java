package org.yeastrc.xlink.www.user_account_webservices;

import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.www.database_update_with_transaction_services.AddNewUserUsingDBTransactionService;
import org.yeastrc.xlink.www.dto.TermsOfServiceUserAcceptedVersionHistoryDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.dto.ZzUserDataMirrorDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants;
import org.yeastrc.xlink.www.captcha_google_api.CaptchaGoogleValidateUserResponseToken;
import org.yeastrc.xlink.www.captcha_google_api.IsGoogleRecaptchaConfigured;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.FieldLengthConstants;
import org.yeastrc.xlink.www.constants.UserSignupConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.TermsOfServiceTextVersionsDAO;
import org.yeastrc.xlink.www.dao.TermsOfServiceUserAcceptedVersionHistoryDAO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.CreateAccountResult;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCreateAccountRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCreateAccountResponse;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtLoginRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtLoginResponse;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserInviteTrackingCode;
import org.yeastrc.xlink.www.web_utils.GetMessageTextFromKeyFrom_web_app_application_properties;

/**
 * Create User Account
 * 
 * 2 methods:
 * 
 * 	Create account without invite code - requires config for allow user signup without invite
 * 
 * 	Create account with invite code
 * 
 * 
 *
 */
@Path("/user")
public class UserCreateAccountService {

	private static final Logger log = Logger.getLogger(UserCreateAccountService.class);
	
	private static enum CreateAccountUsingAdminUserAccount { YES, NO }

	///////////////////////////////////////////
	//    Create Account from Page NOT using Invite Code
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/createAccountUsingAdminUserAccount") 
	public CreateAccountResult createAccountUsingAdminUserAccount(
			@FormParam( "firstName" ) String firstName,
			@FormParam( "lastName" ) String lastName,
			@FormParam( "organization" ) String organization,
			@FormParam( "email" ) String email,
			@FormParam( "username" ) String username,
			@FormParam( "password" ) String password,
			@FormParam( "accessLevel" ) String accessLevelString,
			@Context HttpServletRequest request )
	throws Exception {
		
		if ( StringUtils.isEmpty( accessLevelString ) ) {
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
		Integer accessLevel = null;
		try {
			accessLevel = Integer.valueOf( accessLevelString );
		} catch ( Exception e ) {
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
		if ( ! ( AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN == accessLevel
				|| AuthAccessLevelConstants.ACCESS_LEVEL_DEFAULT_USER_CREATED_VIA_PROJECT_INVITE == accessLevel ) ) {
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
		//  Restricted to users with ACCESS_LEVEL_ADMIN or better
		// Get the session first.  
//		HttpSession session = request.getSession();
		AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
				GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
		if ( accessAndSetupWebSessionResult.isNoSession() ) {
			//  No User session 
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
					.build()
					);
		}
//		UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
		//  Test access at global level
		AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
		if ( ! authAccessLevel.isAdminAllowed() ) {
			//  No Access Allowed 
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		////////   Auth complete
		//////////////////////////////////////////
		
		return createAccountCommonInternal( 
				null /* inviteCode */, 
				null /* tosAcceptedKey */, 
				firstName, lastName, organization, email, username, password, 
				accessLevel, 
				null /* recaptchaValue */, 
				CreateAccountUsingAdminUserAccount.YES, 
				request );
	}
	
	///////////////////////////////////////////
	//    Create Account from Page NOT using Invite Code
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/createAccountNoInvite") 
	public CreateAccountResult createAccountWithoutInviteService(
			@FormParam( "firstName" ) String firstName,
			@FormParam( "lastName" ) String lastName,
			@FormParam( "organization" ) String organization,
			@FormParam( "email" ) String email,
			@FormParam( "username" ) String username,
			@FormParam( "password" ) String password,
			@FormParam( "tos_key" ) String tosAcceptedKey,
			@FormParam( "recaptchaValue" ) String recaptchaValue,
			@Context HttpServletRequest request )
	throws Exception {

		String userSignupAllowWithoutInviteConfigValue =
				ConfigSystemCaching.getInstance()
				.getConfigValueForConfigKey( ConfigSystemsKeysConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY );
		if ( ! UserSignupConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY__TRUE.equals( userSignupAllowWithoutInviteConfigValue ) ) {
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( IsGoogleRecaptchaConfigured.getInstance().isGoogleRecaptchaConfigured() ) {
			if ( StringUtils.isEmpty( recaptchaValue ) ) {
				log.warn( "AccountMaintService:  recaptchaValue empty" );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
		}
		return createAccountCommonInternal( 
				null /* inviteCode */, tosAcceptedKey, 
				firstName, lastName, organization, email, username, password, 
				null /* accessLevel */, 
				recaptchaValue, 
				CreateAccountUsingAdminUserAccount.NO,
				request );
	}
	
	///////////////////////////////////////////
	
	//            Create Account From Invite Code
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/createAccountFromInvite") 
	public CreateAccountResult createAccountFromInviteService(
			@FormParam( "inviteCode" ) String inviteCode,
			@FormParam( "tos_key" ) String tosAcceptedKey,
			@FormParam( "firstName" ) String firstName,
			@FormParam( "lastName" ) String lastName,
			@FormParam( "organization" ) String organization,
			@FormParam( "email" ) String email,
			@FormParam( "username" ) String username,
			@FormParam( "password" ) String password,
			@Context HttpServletRequest request )
	throws Exception {

		if ( StringUtils.isEmpty( inviteCode ) ) {
			log.warn( "AccountMaintService:  inviteCode empty" );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		return createAccountCommonInternal( 
				inviteCode, tosAcceptedKey, 
				firstName, lastName, organization, email, username, password, 
				null /* accessLevel */, 
				null /* recaptchaValue */, 
				CreateAccountUsingAdminUserAccount.NO,
				request );
	}
	
	/**
	 * Common create account code
	 * 
	 * @param inviteCode
	 * @param firstName
	 * @param lastName
	 * @param organization
	 * @param email
	 * @param username
	 * @param password
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private CreateAccountResult createAccountCommonInternal(
			String inviteCode,
			String tosAcceptedKey,
			String firstName,
			String lastName,
			String organization,
			String email,
			String username,
			String password,
			Integer accessLevel,
			String recaptchaValue,  //  Only for without invite code
			CreateAccountUsingAdminUserAccount createAccountUsingAdminUserAccount,
			@Context HttpServletRequest request )
	throws Exception {

		CreateAccountResult createAccountResult = new CreateAccountResult();
		if ( StringUtils.isEmpty( firstName ) ) {
			log.warn( "AccountMaintService:  firstName empty" );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( StringUtils.isEmpty( lastName ) ) {
			log.warn( "AccountMaintService:  lastName empty" );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( StringUtils.isEmpty( organization ) ) {
			log.warn( "AccountMaintService:  organization empty" );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( StringUtils.isEmpty( email ) ) {
			log.warn( "AccountMaintService:  email empty" );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( StringUtils.isEmpty( username ) ) {
			log.warn( "AccountMaintService:  username empty" );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( StringUtils.isEmpty( password ) ) {
			log.warn( "AccountMaintService:  password empty" );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		////////////
		if ( firstName.length() > FieldLengthConstants.FIRST_NAME_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  firstName too long: " + firstName );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( lastName.length() > FieldLengthConstants.LAST_NAME_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  lastName too long: " + lastName );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( organization.length() > FieldLengthConstants.ORGANIZATION_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  organization too long: " + organization );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( email.length() > FieldLengthConstants.EMAIL_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  email too long: " + email );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( username.length() > FieldLengthConstants.USERNAME_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  username too long: " + username );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( password.length() > FieldLengthConstants.PASSWORD_MAX_LENGTH ) {
			log.warn( "AccountMaintService:  password too long: " + password );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		//  Pre-check for username and email already exist
		{
//			AuthUserDAO authUserDAO = AuthUserDAO.getInstance();
//			AuthUserDTO authUserDTOFromEmail = authUserDAO.getAuthUserDTOForEmail( email );
//			if ( authUserDTOFromEmail != null ) {
//				createAccountResult.setStatus(false);
//				createAccountResult.setDuplicateEmail(true);
//				return createAccountResult;  //  !!!!!  EARLY EXIT
//			}
//			AuthUserDTO authUserDTOFromUsername = authUserDAO.getAuthUserDTOForUsername( username );
//			if ( authUserDTOFromUsername != null ) {
//				createAccountResult.setStatus(false);
//				createAccountResult.setDuplicateUsername(true);
//				return createAccountResult;  //  !!!!!  EARLY EXIT
//			}
		}
		if( StringUtils.isNotEmpty( recaptchaValue ) ) {
			createAccountResult.setUserTestValidated( true );
			if ( ! CaptchaGoogleValidateUserResponseToken.getInstance().isCaptchaUserResponseTokenValid( recaptchaValue, request.getRemoteHost() ) ) {
				String errorMessage = "captcha validation failed";
				createAccountResult.setStatus(false);
				createAccountResult.setErrorMessage( errorMessage );
				return createAccountResult;  //  !!!!!  EARLY EXIT
			}
		}
		

		try {

			AuthUserInviteTrackingDTO authUserInviteTrackingDTO = null;

			Integer termsOfServiceVersionIdForIdString = null;

			//  Is terms of service enabled?
			String termsOfServiceEnabledString =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.TERMS_OF_SERVICE_ENABLED );
			boolean termsOfServiceEnabled = false;
			if ( ConfigSystemsValuesSharedConstants.TRUE.equals(termsOfServiceEnabledString) ) {
				termsOfServiceEnabled = true;
			}

			if ( createAccountUsingAdminUserAccount == CreateAccountUsingAdminUserAccount.NO ) {

				if ( StringUtils.isNotEmpty( inviteCode ) ) {
					//  Only process if have invite code
					ValidateUserInviteTrackingCode validateUserInviteTrackingCode = ValidateUserInviteTrackingCode.getInstance( inviteCode );
					if ( ! validateUserInviteTrackingCode.validateInviteTrackingCode() ) {
						String errorMsgKey = validateUserInviteTrackingCode.getErrorMsgKey();
						String errorMessage = GetMessageTextFromKeyFrom_web_app_application_properties.getInstance().getMessageForKey( errorMsgKey );
						createAccountResult.setStatus(false);
						createAccountResult.setErrorMessage( errorMessage );
						return createAccountResult;  //  !!!!!  EARLY EXIT
					}
					authUserInviteTrackingDTO = validateUserInviteTrackingCode.getAuthUserInviteTrackingDTO();
				}

				if ( termsOfServiceEnabled ) {
					// terms of service is enabled
					//  Version of TOS user accepted
					termsOfServiceVersionIdForIdString =
							TermsOfServiceTextVersionsDAO.getInstance().getVersionIdForIdString( tosAcceptedKey );
					if ( termsOfServiceVersionIdForIdString == null ) {
						String msg = "No record for tosAcceptedKey: " + tosAcceptedKey;
						log.warn( msg );
						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
								.build() );  //  Early Exit with Data Exception
					}
				}
			}

			//  New User Mgmt
			
			UserMgmtCreateAccountRequest userMgmtCreateAccountRequest = new UserMgmtCreateAccountRequest();
			
			userMgmtCreateAccountRequest.setUsername( username );
			userMgmtCreateAccountRequest.setEmail( email );
			userMgmtCreateAccountRequest.setPassword( password );
			userMgmtCreateAccountRequest.setFirstName( firstName );
			userMgmtCreateAccountRequest.setLastName( lastName );
			userMgmtCreateAccountRequest.setOrganization( organization );
			userMgmtCreateAccountRequest.setUserRemoteIP( request.getRemoteAddr() );
			
			UserMgmtCreateAccountResponse userMgmtCreateAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().createUser( userMgmtCreateAccountRequest );
			
			if ( ! userMgmtCreateAccountResponse.isSuccess() ) {
				
				if ( userMgmtCreateAccountResponse.isDuplicateEmail() ) {
					createAccountResult.setDuplicateEmail(true);
					createAccountResult.setStatus(false);
					return createAccountResult;   //  EARLY EXIT
				}
				if ( userMgmtCreateAccountResponse.isDuplicateUsername() ) {
					createAccountResult.setDuplicateUsername(true);
					createAccountResult.setStatus(false);
					return createAccountResult;   //  EARLY EXIT
				}
			}
			
			int createdUserMgmtUserId = userMgmtCreateAccountResponse.getCreatedUserId();
			
			AuthUserDTO authUserDTO = new AuthUserDTO();
			
			// pass in created user id from User Mgmt Webapp
			authUserDTO.setUserMgmtUserId( createdUserMgmtUserId );

			if ( createAccountUsingAdminUserAccount == CreateAccountUsingAdminUserAccount.NO ) {

				if ( authUserInviteTrackingDTO != null && authUserInviteTrackingDTO.getInvitedSharedObjectId() == null ) {
					//  Invite not tied to a project so the access level in the invite is used as the user level
					authUserDTO.setUserAccessLevel( authUserInviteTrackingDTO.getInvitedUserAccessLevel() );
				} else {
					//  The InvitedUserAccessLevel is tied to the project so at the user level this default is used.
					authUserDTO.setUserAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_DEFAULT_USER_CREATED_VIA_PROJECT_INVITE  );
				}
				
			} else {
				//  Set Level provided by user for CreateAccountUsingAdminUserAccount.YES
				authUserDTO.setUserAccessLevel( accessLevel );
			}
			
			//  After user added to User Mgmt, add to proxl DB
			
			XLinkUserDTO userDatabaseRecord = null;
			
			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			// zzUserDataMirrorDTO.setAuthUserId( XXX );  AuthUserId set later
			zzUserDataMirrorDTO.setUsername( username );
			zzUserDataMirrorDTO.setEmail( email );
			zzUserDataMirrorDTO.setFirstName( firstName );
			zzUserDataMirrorDTO.setLastName( lastName );
			zzUserDataMirrorDTO.setOrganization( organization );

			try {
				if ( authUserInviteTrackingDTO != null ) {
					if ( authUserInviteTrackingDTO.getInvitedSharedObjectId() == null ) {
						//  user is not linked to a project so just add the user
						AddNewUserUsingDBTransactionService.getInstance()
						.addNewUserForUserInvite( authUserDTO, zzUserDataMirrorDTO, authUserInviteTrackingDTO );
					} else {
						//  A shared object id is associated so create and save records for that as well
						AuthSharedObjectUsersDTO authSharedObjectUsersDTO = new AuthSharedObjectUsersDTO();
						authSharedObjectUsersDTO.setSharedObjectId( authUserInviteTrackingDTO.getInvitedSharedObjectId() );
						authSharedObjectUsersDTO.setAccessLevel( authUserInviteTrackingDTO.getInvitedUserAccessLevel() );
						AddNewUserUsingDBTransactionService.getInstance()
						.addNewUserAddAuthSharedObjectUsersDTOForUserInvite( 
								authUserDTO, zzUserDataMirrorDTO, authSharedObjectUsersDTO, authUserInviteTrackingDTO );
					}
				} else {
					//  user is not linked to a project so just add the user
					AddNewUserUsingDBTransactionService.getInstance().addNewUserForUserInvite( authUserDTO, zzUserDataMirrorDTO, null /* authUserInviteTrackingDTO */ );
				}
				
				userDatabaseRecord = new XLinkUserDTO();
				userDatabaseRecord.setAuthUser(authUserDTO);
				
				authUserDTO.setUsername( username );
				authUserDTO.setEmail( email );
				
				userDatabaseRecord.setFirstName( firstName );
				userDatabaseRecord.setLastName( lastName );
				userDatabaseRecord.setOrganization( organization );
				
			} catch ( SQLException sqlException ) {
				
				throw sqlException;
			}

			if ( createAccountUsingAdminUserAccount == CreateAccountUsingAdminUserAccount.NO ) {

				if ( termsOfServiceEnabled ) {
					// terms of service is enabled, save user acceptance
					int authUserId = authUserDTO.getId();
					TermsOfServiceUserAcceptedVersionHistoryDTO tosUserAccepted = new TermsOfServiceUserAcceptedVersionHistoryDTO();
					tosUserAccepted.setAuthUserId( authUserId );
					tosUserAccepted.setTermsOfServiceVersionId( termsOfServiceVersionIdForIdString );
					TermsOfServiceUserAcceptedVersionHistoryDAO.getInstance().save( tosUserAccepted );
				}

				UserMgmtLoginRequest userMgmtLoginRequest = new UserMgmtLoginRequest();
				userMgmtLoginRequest.setUsername( username );
				userMgmtLoginRequest.setPassword( password );
				userMgmtLoginRequest.setRemoteIP(  request.getRemoteAddr() );

				UserMgmtLoginResponse userMgmtLoginResponse = 
						UserMgmtCentralWebappWebserviceAccess.getInstance().userLogin( userMgmtLoginRequest );

				if ( ! userMgmtLoginResponse.isSuccess() ) {
					String msg = null;
					if ( userMgmtLoginResponse.isUsernameNotFound() || userMgmtLoginResponse.isPasswordInvalid() ) {
						msg = "Fail to log into account just created for username not found or password is invalid.  username: " + username;
					} else if ( userMgmtLoginResponse.isUserDisabled() ) {
						msg = "Fail to log into account just created for user is disabled.  username: " + username;
					}
					msg = "Fail to log into account just created.  username: " + username;
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}

				UserSessionObject userSessionObject = new UserSessionObject();
				userSessionObject.setUserDBObject( userDatabaseRecord );
				userSessionObject.setUserLoginSessionKey( userMgmtLoginResponse.getSessionKey() );
				// Get their session   
				HttpSession session = request.getSession();
				session.setAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN, userSessionObject );

			}

			createAccountResult.setStatus(true);
			return createAccountResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "createAccountCommonInternal(...) Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
}
