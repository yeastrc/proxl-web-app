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
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.FieldLengthConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.CreateAccountResult;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserInviteTrackingCode;
import org.yeastrc.xlink.www.web_utils.GetMessageTextFromKeyFrom_web_app_application_properties;



@Path("/user")
public class UserInviteCreateAccountService {

	private static final Logger log = Logger.getLogger(UserInviteCreateAccountService.class);
	
	
	
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
		
		CreateAccountResult createAccountResult = new CreateAccountResult();


		if ( StringUtils.isEmpty( inviteCode ) ) {

			log.warn( "AccountMaintService:  inviteCode empty" );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

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

		

		try {

			
			ValidateUserInviteTrackingCode validateUserInviteTrackingCode = ValidateUserInviteTrackingCode.getInstance( inviteCode );

			if ( ! validateUserInviteTrackingCode.validateInviteTrackingCode() ) {

				String errorMsgKey = validateUserInviteTrackingCode.getErrorMsgKey();
	

				String errorMessage = GetMessageTextFromKeyFrom_web_app_application_properties.getInstance().getMessageForKey( errorMsgKey );


				createAccountResult.setStatus(false);
		        
				createAccountResult.setErrorMessage( errorMessage );
				
				return createAccountResult;  //  !!!!!  EARLY EXIT

			}
			
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO = validateUserInviteTrackingCode.getAuthUserInviteTrackingDTO();
			
			
			String hashedPassword = HashedPasswordProcessing.getInstance().createNewHashedPasswordHex( password );


			AuthUserDTO authUserDTO = new AuthUserDTO();

			//		authUserDTO.setPasswordHashedHex( hashedPassword );

			authUserDTO.setEmail( email );

			if ( authUserInviteTrackingDTO.getInvitedSharedObjectId() == null ) {
				
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
			
			String msg = "changePasswordService(...) Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}

}
