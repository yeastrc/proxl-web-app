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
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.auth.hash_password.HashedPasswordProcessing;
import org.yeastrc.xlink.www.database_update_with_transaction_services.AddNewUserUsingDBTransactionService;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.captcha_google_api.CaptchaGoogleValidateUserResponseToken;
import org.yeastrc.xlink.www.captcha_google_api.IsGoogleRecaptchaConfigured;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.FieldLengthConstants;
import org.yeastrc.xlink.www.constants.UserSignupConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.CreateAccountResult;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
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
		
		
		
		return createAccountCommonInternal( null /* inviteCode */, firstName, lastName, organization, email, username, password, recaptchaValue, request );

		
	}
	
	
	///////////////////////////////////////////
	
	//            Create Account from Page using Invite Code

	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/createAccountFromInvite") 
	
	public CreateAccountResult createAccountFromInviteService(
			
			@FormParam( "inviteCode" ) String inviteCode,
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
		
		return createAccountCommonInternal( inviteCode, firstName, lastName, organization, email, username, password, null /* recaptchaValue */, request );
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
			String firstName,
			String lastName,
			String organization,
			String email,
			String username,
			String password,
			String recaptchaValue,  //  Only for without invite code
			@Context HttpServletRequest request )
	throws Exception {
		
		CreateAccountResult createAccountResult = new CreateAccountResult();



		if ( StringUtils.isEmpty( firstName ) ) {

			log.warn( "AccountMaintService:  firstName empty" );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( StringUtils.isEmpty( lastName ) ) {

			log.warn( "AccountMaintService:  lastName empty" );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( StringUtils.isEmpty( organization ) ) {

			log.warn( "AccountMaintService:  organization empty" );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( StringUtils.isEmpty( email ) ) {

			log.warn( "AccountMaintService:  email empty" );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		if ( StringUtils.isEmpty( username ) ) {

			log.warn( "AccountMaintService:  username empty" );

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
		
		////////////

		if ( firstName.length() > FieldLengthConstants.FIRST_NAME_MAX_LENGTH ) {

			log.warn( "AccountMaintService:  firstName too long: " + firstName );

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
		
		if ( organization.length() > FieldLengthConstants.ORGANIZATION_MAX_LENGTH ) {

			log.warn( "AccountMaintService:  organization too long: " + organization );

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
		

		if ( username.length() > FieldLengthConstants.USERNAME_MAX_LENGTH ) {

			log.warn( "AccountMaintService:  username too long: " + username );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		if ( password.length() > FieldLengthConstants.PASSWORD_MAX_LENGTH ) {

			log.warn( "AccountMaintService:  password too long: " + password );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
		//  Pre-check for username and email already exist
		
		{
			AuthUserDAO authUserDAO = AuthUserDAO.getInstance();

			AuthUserDTO authUserDTOFromEmail = authUserDAO.getAuthUserDTOForEmail( email );

			if ( authUserDTOFromEmail != null ) {

				createAccountResult.setStatus(false);
				createAccountResult.setDuplicateEmail(true);
				
				return createAccountResult;  //  !!!!!  EARLY EXIT
			}


			AuthUserDTO authUserDTOFromUsername = authUserDAO.getAuthUserDTOForUsername( username );

			if ( authUserDTOFromUsername != null ) {

				createAccountResult.setStatus(false);
				createAccountResult.setDuplicateUsername(true);
				
				return createAccountResult;  //  !!!!!  EARLY EXIT
			}
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
			
			String hashedPassword = HashedPasswordProcessing.getInstance().createNewHashedPasswordHex( password );


			AuthUserDTO authUserDTO = new AuthUserDTO();

			//		authUserDTO.setPasswordHashedHex( hashedPassword );

			authUserDTO.setEmail( email );

			if ( authUserInviteTrackingDTO != null && authUserInviteTrackingDTO.getInvitedSharedObjectId() == null ) {
				
				//  Invite not tied to a project so the access level in the invite is used as the user level

				authUserDTO.setUserAccessLevel( authUserInviteTrackingDTO.getInvitedUserAccessLevel() );
			} else {
				
				//  The InvitedUserAccessLevel is tied to the project so at the user level this default is used.
				
				authUserDTO.setUserAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_DEFAULT_USER_CREATED_VIA_PROJECT_INVITE  );
			}

			authUserDTO.setUsername( username );

			//		  AuthUserDAO.getInstance().save( authUserDTO );  //  Done in XLinkUserDAO.getInstance().save(item)

			XLinkUserDTO xLinkUserDTO = new XLinkUserDTO();

			xLinkUserDTO.setAuthUser(authUserDTO);
			xLinkUserDTO.setFirstName( firstName );
			xLinkUserDTO.setLastName( lastName );
			xLinkUserDTO.setOrganization( organization );


			try {
				
				if ( authUserInviteTrackingDTO != null ) {

					if ( authUserInviteTrackingDTO.getInvitedSharedObjectId() == null ) {

						//  user is not linked to a project so just add the user

						AddNewUserUsingDBTransactionService.getInstance().addNewUserForUserInvite( xLinkUserDTO, hashedPassword, authUserInviteTrackingDTO );

					} else {

						//  A shared object id is associated so create and save records for that as well

						AuthSharedObjectUsersDTO authSharedObjectUsersDTO = new AuthSharedObjectUsersDTO();

						authSharedObjectUsersDTO.setSharedObjectId( authUserInviteTrackingDTO.getInvitedSharedObjectId() );
						authSharedObjectUsersDTO.setAccessLevel( authUserInviteTrackingDTO.getInvitedUserAccessLevel() );

						AddNewUserUsingDBTransactionService.getInstance().addNewUserAddAuthSharedObjectUsersDTOForUserInvite( xLinkUserDTO, hashedPassword, authSharedObjectUsersDTO, authUserInviteTrackingDTO );
					}

				} else {

					//  user is not linked to a project so just add the user

					AddNewUserUsingDBTransactionService.getInstance().addNewUserForUserInvite( xLinkUserDTO, hashedPassword, null /* authUserInviteTrackingDTO */ );
				}
				

				UserSessionObject userSessionObject = new UserSessionObject();

				userSessionObject.setUserDBObject( xLinkUserDTO );

				// Get their session   
				HttpSession session = request.getSession();
				
				session.setAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN, userSessionObject );


			} catch ( SQLException sqlException ) {

				String exceptionMessage = sqlException.getMessage();

				if ( exceptionMessage != null && exceptionMessage.startsWith( "Duplicate entry" ) ) {
					
					AuthUserDAO authUserDAO = AuthUserDAO.getInstance();
					
					AuthUserDTO authUserDTOFromEmail = authUserDAO.getAuthUserDTOForEmail( email );

					if ( authUserDTOFromEmail != null ) {
					
						createAccountResult.setDuplicateEmail(true);
					}
					

					AuthUserDTO authUserDTOFromUsername = authUserDAO.getAuthUserDTOForUsername( username );

					if ( authUserDTOFromUsername != null ) {
					
						createAccountResult.setDuplicateUsername(true);
					}
					

					createAccountResult.setStatus(false);
					
					return createAccountResult;   //  EARLY EXIT

				} else {

					throw sqlException;
				}
			}

			createAccountResult.setStatus(true);
			
			return createAccountResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "createAccountCommonInternal(...) Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}

}
