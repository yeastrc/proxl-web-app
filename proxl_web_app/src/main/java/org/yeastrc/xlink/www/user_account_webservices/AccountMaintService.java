package org.yeastrc.xlink.www.user_account_webservices;

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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.dto.ZzUserDataMirrorDTO;
import org.yeastrc.xlink.www.constants.FieldLengthConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ZzUserDataMirrorDAO;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtChangePasswordRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtChangePasswordResponse;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtManageAccountRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtManageAccountResponse;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
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
			// Get their session first.  
//			HttpSession session = request.getSession();
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
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setSessionKey( userSessionObject.getUserLoginSessionKey() );
			userMgmtManageAccountRequest.setFirstName( firstName );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {
				accountMaintResult.setStatus(false);
				return accountMaintResult; // EARLY RETURN
			}

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();
			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userDBObject.getAuthUser().getId() );
			zzUserDataMirrorDTO.setFirstName( firstName );
			ZzUserDataMirrorDAO.getInstance().updateRecord( zzUserDataMirrorDTO );
			
			userDBObject.setFirstName( firstName );
			
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
			// Get their session first.  
			HttpSession session = request.getSession();
			UserSessionObject userSessionObject = (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
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
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setSessionKey( userSessionObject.getUserLoginSessionKey() );
			userMgmtManageAccountRequest.setLastName( lastName );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {
				accountMaintResult.setStatus(false);
				return accountMaintResult; // EARLY RETURN
			}

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();
			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userDBObject.getAuthUser().getId() );
			zzUserDataMirrorDTO.setLastName( lastName );
			ZzUserDataMirrorDAO.getInstance().updateRecord( zzUserDataMirrorDTO );
			
			userDBObject.setLastName( lastName );
	        
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
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setSessionKey( userSessionObject.getUserLoginSessionKey() );
			userMgmtManageAccountRequest.setOrganization( organization );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {
				accountMaintResult.setStatus(false);
				return accountMaintResult; // EARLY RETURN
			}

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();
			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userDBObject.getAuthUser().getId() );
			zzUserDataMirrorDTO.setOrganization( organization );
			ZzUserDataMirrorDAO.getInstance().updateRecord( zzUserDataMirrorDTO );
			
			userDBObject.setOrganization( organization );
			
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
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setSessionKey( userSessionObject.getUserLoginSessionKey() );
			userMgmtManageAccountRequest.setEmail( email );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {
				accountMaintResult.setStatus(false);
				if ( userMgmtManageAccountResponse.isDuplicateEmail() ) {
					accountMaintResult.setValueAlreadyExists(true);
				}
				return accountMaintResult;   //  EARLY EXIT
			}

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();
			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userDBObject.getAuthUser().getId() );
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
			UserMgmtManageAccountRequest userMgmtManageAccountRequest = new UserMgmtManageAccountRequest();
			userMgmtManageAccountRequest.setSessionKey( userSessionObject.getUserLoginSessionKey() );
			userMgmtManageAccountRequest.setUsername( username );
			UserMgmtManageAccountResponse userMgmtManageAccountResponse =
					UserMgmtCentralWebappWebserviceAccess.getInstance().
					manageUserData( userMgmtManageAccountRequest );
			if ( ! userMgmtManageAccountResponse.isSuccess() ) {
				accountMaintResult.setStatus(false);
				if ( userMgmtManageAccountResponse.isDuplicateUsername() ) {
					accountMaintResult.setValueAlreadyExists(true);
				}
				return accountMaintResult;   //  EARLY EXIT
			}

			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();
			ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
			zzUserDataMirrorDTO.setAuthUserId( userDBObject.getAuthUser().getId() );
			zzUserDataMirrorDTO.setUsername( username );
			ZzUserDataMirrorDAO.getInstance().updateRecord( zzUserDataMirrorDTO );
			
			AuthUserDTO authUserDTO = userDBObject.getAuthUser();
			authUserDTO.setUsername( username );
			
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
			log.warn( "AccountMaintService:  old_password empty: " + password );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
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
			
			UserMgmtChangePasswordRequest userMgmtChangePasswordRequest = new UserMgmtChangePasswordRequest();
			userMgmtChangePasswordRequest.setSessionKey( userSessionObject.getUserLoginSessionKey() );
			userMgmtChangePasswordRequest.setOldPassword( oldPassword );
			userMgmtChangePasswordRequest.setNewPassword( password );
			userMgmtChangePasswordRequest.setUserRemoteIP( request.getRemoteAddr() );
			
			UserMgmtChangePasswordResponse userMgmtChangePasswordResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().changePassword( userMgmtChangePasswordRequest );
			
			if ( ! userMgmtChangePasswordResponse.isSuccess() ) {
				accountMaintResult.setStatus(false);
				if ( userMgmtChangePasswordResponse.isSessionKeyNotValid() ) {
					//  No User session 
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				if ( userMgmtChangePasswordResponse.isOldPasswordNotValid() ) {
					accountMaintResult.setStatus(false);
					accountMaintResult.setOldPasswordInvalid(true);
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
	 * This is returned from the web service AccountMaintService
	 *
	 */
	public static class AccountMaintResult {

		private boolean status = false;

		private boolean valueAlreadyExists = false;
		private boolean oldPasswordInvalid = false;

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

	}

}
