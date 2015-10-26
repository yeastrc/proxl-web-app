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
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.auth.hash_password.HashedPasswordProcessing;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.constants.FieldLengthConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.AccountMaintResult;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.TestIsUserSignedIn;



@Path("/user")
public class AccountMaintService {

	private static final Logger log = Logger.getLogger(AccountMaintService.class);
	
	
	
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

			// Get their session first.  
			HttpSession session = request.getSession();



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
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSessionObject ) ) {

				//  No Access Allowed if not a logged in user

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();

			AuthUserDTO authUserDTO = userDBObject.getAuthUser();
			
			int authUserId = authUserDTO.getId();
			
			XLinkUserDAO.getInstance().updateFirstName(authUserId, firstName);

			userDBObject.setFirstName( firstName );

	        accountMaintResult.setStatus(true);
			
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
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
		
//		if (true)
//		throw new Exception("Forced Error");
		
		try {

			// Get their session first.  
			HttpSession session = request.getSession();


			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

			if ( userSessionObject == null ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSessionObject ) ) {

				//  No Access Allowed if not a logged in user

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();

			AuthUserDTO authUserDTO = userDBObject.getAuthUser();
			
			int authUserId = authUserDTO.getId();
			
			XLinkUserDAO.getInstance().updateLastName(authUserId, lastName);

			userDBObject.setLastName( lastName );

	        accountMaintResult.setStatus(true);
			
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
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
		
//		if (true)
//		throw new Exception("Forced Error");
		
		try {

			// Get their session first.  
			HttpSession session = request.getSession();


			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

			if ( userSessionObject == null ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSessionObject ) ) {

				//  No Access Allowed if not a logged in user

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();

			AuthUserDTO authUserDTO = userDBObject.getAuthUser();
			
			int authUserId = authUserDTO.getId();
			
			XLinkUserDAO.getInstance().updateOrganization(authUserId, organization);

			userDBObject.setOrganization( organization );

	        accountMaintResult.setStatus(true);
			
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}
	


	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/changeEmail") 
	public AccountMaintResult changeEmailService(   
			@FormParam( "email" ) String email,
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
		
//		if (true)
//		throw new Exception("Forced Error");
		
		try {

			// Get their session first.  
			HttpSession session = request.getSession();


			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

			if ( userSessionObject == null ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSessionObject ) ) {

				//  No Access Allowed if not a logged in user

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();

			AuthUserDTO authUserDTO = userDBObject.getAuthUser();
			
			int authUserId = authUserDTO.getId();
			
			try {

				AuthUserDAO.getInstance().updateEmail(authUserId, email);

			} catch ( SQLException sqlException ) {

				String exceptionMessage = sqlException.getMessage();

				if ( exceptionMessage != null && exceptionMessage.startsWith( "Duplicate entry" ) ) {

					accountMaintResult.setStatus(false);
					accountMaintResult.setValueAlreadyExists(true);
					
					
					return accountMaintResult;   //  EARLY EXIT

				} else {

					throw sqlException;
				}

			}

	        accountMaintResult.setStatus(true);
			
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}
	
	


	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/changeUsername") 
	public AccountMaintResult changeUsernameService(   
			@FormParam( "username" ) String username,
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
		
//		if (true)
//		throw new Exception("Forced Error");
		
		try {

			// Get their session first.  
			HttpSession session = request.getSession();


			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

			if ( userSessionObject == null ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSessionObject ) ) {

				//  No Access Allowed if not a logged in user

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();

			AuthUserDTO authUserDTO = userDBObject.getAuthUser();
			
			int authUserId = authUserDTO.getId();
			
			try {

				AuthUserDAO.getInstance().updateUsername(authUserId, username);

			} catch ( SQLException sqlException ) {

				String exceptionMessage = sqlException.getMessage();

				if ( exceptionMessage != null && exceptionMessage.startsWith( "Duplicate entry" ) ) {

					accountMaintResult.setStatus(false);
					accountMaintResult.setValueAlreadyExists(true);
					
					
					return accountMaintResult;   //  EARLY EXIT

				} else {

					throw sqlException;
				}

			}

			authUserDTO.setUsername( username );

	        accountMaintResult.setStatus(true);
			
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}
	

	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/changePassword") 
	public AccountMaintResult changePasswordService(   
			@FormParam( "password" ) String password,
			@Context HttpServletRequest request )
	throws Exception {
		
		AccountMaintResult accountMaintResult = new AccountMaintResult();


		if ( StringUtils.isEmpty( password ) ) {

			log.warn( "AccountMaintService:  password empty: " + password );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
		

		if ( password.length() > FieldLengthConstants.PASSWORD_MAX_LENGTH ) {

			log.warn( "AccountMaintService:  password too long, length: " + password.length() );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		
//		if (true)
//		throw new Exception("Forced Error");
		
		try {

			// Get their session first.  
			HttpSession session = request.getSession();


			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );

			if ( userSessionObject == null ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			if ( ! TestIsUserSignedIn.getInstance().testIsUserSignedIn( userSessionObject ) ) {

				//  No Access Allowed if not a logged in user

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();

			AuthUserDTO authUserDTO = userDBObject.getAuthUser();
			
			int authUserId = authUserDTO.getId();
				
			String passwordHashed = HashedPasswordProcessing.getInstance().createNewHashedPasswordHex( password );

			AuthUserDAO.getInstance().updatePasswordHashed( authUserId, passwordHashed );

			accountMaintResult.setStatus(true);
			
			return accountMaintResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "changePasswordService(...) Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}

}
