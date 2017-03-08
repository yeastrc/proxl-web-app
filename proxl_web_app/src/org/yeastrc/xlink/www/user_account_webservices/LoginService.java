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
import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants;
import org.yeastrc.xlink.www.dao.TermsOfServiceTextVersionsDAO;
import org.yeastrc.xlink.www.dao.TermsOfServiceUserAcceptedVersionHistoryDAO;
import org.yeastrc.xlink.www.database_update_with_transaction_services.AddNewUserUsingDBTransactionService;
import org.yeastrc.xlink.www.dto.TermsOfServiceTextVersionsDTO;
import org.yeastrc.xlink.www.dto.TermsOfServiceUserAcceptedVersionHistoryDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.dto.ZzUserDataMirrorDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.UserSignupConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtLoginRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtLoginResponse;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserInviteTrackingCode;


@Path("/user")
public class LoginService {

	private static final Logger log = Logger.getLogger(LoginService.class);
	
	private static final String RETURN_TOS_TRUE = "true";
	
	/**
	 * @param username
	 * @param password
	 * @param returnTOS
	 * @param tosAcceptedKey
	 * @param inviteTrackingCode - Only when came to Sign in page from Clicking "Sign in" on Invite landing page
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login") 
	public Response loginService(   
			@FormParam( "username" ) String username,
			@FormParam( "password" ) String password,
			@FormParam( "return_tos" ) String returnTOS,
			@FormParam( "tos_key" ) String tosAcceptedKey,
			@FormParam( "inviteTrackingCode" ) String inviteTrackingCode,
			@Context HttpServletRequest request )
	throws Exception {

		LoginResult loginResult = loginServiceLocal( username, password, returnTOS, tosAcceptedKey, inviteTrackingCode,request );
//		.cookie(new NewCookie("name", "Hello, world!"))
		return Response.ok(loginResult).build();
	}
	
	/**
	 * @param username
	 * @param password
	 * @param returnTOS
	 * @param tosAcceptedKey
	 * @param request
	 * @return
	 */
	private LoginResult loginServiceLocal(   
			String username,
			String password,
			String returnTOS,
			String tosAcceptedKey,
			String inviteTrackingCode,
			HttpServletRequest request ) {
	
		LoginResult loginResult = new LoginResult();
		if ( StringUtils.isEmpty( username ) ) {
			log.warn( "LoginService:  username empty: " + username );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
		if ( StringUtils.isEmpty( password ) ) {
			log.warn( "LoginService:  password empty: " + password );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);  //  Early Exit with Data Exception
		}
//		if (true)
//		throw new Exception("Forced Error");
		try {
			// Get their session first.  
			HttpSession session = request.getSession();
			
			UserMgmtLoginRequest userMgmtLoginRequest = new UserMgmtLoginRequest();
			userMgmtLoginRequest.setUsername( username );
			userMgmtLoginRequest.setPassword( password );
			userMgmtLoginRequest.setRemoteIP(  request.getRemoteAddr() );
			
			UserMgmtLoginResponse userMgmtLoginResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().userLogin( userMgmtLoginRequest );
			
			if ( ! userMgmtLoginResponse.isSuccess() ) {
				
				if ( userMgmtLoginResponse.isUsernameNotFound() || userMgmtLoginResponse.isPasswordInvalid() ) {
			        loginResult.setInvalidUserOrPassword(true);
					return loginResult;  //  Early Exit
				}
				if ( userMgmtLoginResponse.isUserDisabled() ) {
			        loginResult.setDisabledUser(true);
					return loginResult;  //  Early Exit
				}
			}

			//  Marked as logged in in User Mgmt Web app even if don't accept TOS
			
			int userMgmtUserId = userMgmtLoginResponse.getUserId();
			
			//  Get full user data
			
			UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
			userMgmtGetUserDataRequest.setSessionKey( userMgmtLoginResponse.getSessionKey() );
			userMgmtGetUserDataRequest.setUserId( userMgmtUserId );
			
			UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
			
			if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
				String msg = "Failed to get Full user data from User Mgmt Webapp for user id: " + userMgmtUserId;
				log.error( msg );
		        loginResult.setInvalidUserOrPassword(true); //  TODO Set different error
				return loginResult;  //  Early Exit
			}

			Integer proxlAuthUserId = AuthUserDAO.getInstance().getIdForUserMgmtUserId( userMgmtUserId );
			if ( proxlAuthUserId == null ) {
				// No account in proxl for this user id.  
				// Create one
				AuthUserDTO authUserDTO = new AuthUserDTO();
				authUserDTO.setUserMgmtUserId( userMgmtUserId );
				if ( userMgmtGetUserDataResponse.isGlobalAdminUser() ) {
					//  User is marked Global Admin User so create account with full admin rights
					authUserDTO.setUserAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN );
				} else {
					//  User is not Global Admin User 
					
					//  Only create account if user signup without invite is allowed
					//  or has valid invite code
					
					//  Check config for if invite is required
					String userSignupAllowWithoutInviteConfigValue =
							ConfigSystemCaching.getInstance()
							.getConfigValueForConfigKey( ConfigSystemsKeysConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY );
					if ( ! UserSignupConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY__TRUE.equals( userSignupAllowWithoutInviteConfigValue ) ) {
						//  Invite required 
						if ( StringUtils.isEmpty( inviteTrackingCode ) ) {
							//  Invite required so don't create account
					        loginResult.setNoProxlAccount(true);
							return loginResult;  //  Early Exit
						}
						// Validate Invite code
						ValidateUserInviteTrackingCode validateUserInviteTrackingCode = 
								ValidateUserInviteTrackingCode.getInstance( inviteTrackingCode );
						if ( ! validateUserInviteTrackingCode.validateInviteTrackingCode() ) {
							loginResult.setInvalidInviteTrackingCode(true);
							return loginResult;  //  Early Exit
						}
					}
					authUserDTO.setUserAccessLevel( AuthAccessLevelConstants.ACCESS_LEVEL_CREATE_NEW_PROJECT_AKA_USER );
				}
				ZzUserDataMirrorDTO zzUserDataMirrorDTO = new ZzUserDataMirrorDTO();
				// zzUserDataMirrorDTO.setAuthUserId( XXX );  AuthUserId set later
				zzUserDataMirrorDTO.setUsername( userMgmtGetUserDataResponse.getUsername() );
				zzUserDataMirrorDTO.setEmail( userMgmtGetUserDataResponse.getEmail() );
				zzUserDataMirrorDTO.setFirstName( userMgmtGetUserDataResponse.getFirstName() );
				zzUserDataMirrorDTO.setLastName( userMgmtGetUserDataResponse.getLastName() );
				zzUserDataMirrorDTO.setOrganization( userMgmtGetUserDataResponse.getOrganization() );
				try {
					AddNewUserUsingDBTransactionService.getInstance().addNewUser( authUserDTO, zzUserDataMirrorDTO );
				} catch ( Exception e ) {
					String msg = "Failed to add new user for userId in User Mgmt but not in Proxl.  userMgmtUserId: " + userMgmtUserId;
					log.error( msg, e );
					throw e;
				}
				
				proxlAuthUserId = authUserDTO.getId();
			}

			//  Get user Access level at account level from proxl db
			Boolean userEnabledAppSpecific = AuthUserDAO.getInstance().getUserEnabledAppSpecific( proxlAuthUserId );
			if ( userEnabledAppSpecific == null ) {
				String msg = "Failed to get userEnabledAppSpecific from proxl auth_user table for user id: " + proxlAuthUserId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			if ( ! userEnabledAppSpecific ) {
				String msg = "Failed to get userEnabledAppSpecific from proxl auth_user table for user id: " + proxlAuthUserId;
				log.error( msg );
		        loginResult.setDisabledUser(true);
				return loginResult;  //  Early Exit
			}
			
			//  Get user Access level at account level from proxl db
			Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel( proxlAuthUserId );
			if ( userAccessLevel == null ) {
				String msg = "Failed to get userAccessLevel from proxl auth_user table for user id: " + proxlAuthUserId;
				log.error( msg );
		        loginResult.setInvalidUserOrPassword(true); //  TODO Set different error
				return loginResult;  //  Early Exit
			}
			
			//  Is terms of service enabled?
			String termsOfServiceEnabledString =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.TERMS_OF_SERVICE_ENABLED );
			boolean termsOfServiceEnabled = false;
			if ( ConfigSystemsValuesSharedConstants.TRUE.equals(termsOfServiceEnabledString) ) {
				termsOfServiceEnabled = true;
			}
			
			if ( termsOfServiceEnabled ) {
				// terms of service is enabled
				//  Has user accepted latest version
				Integer tosLatestVersionId = TermsOfServiceTextVersionsDAO.getInstance().getLatestVersionId();
				if ( tosLatestVersionId == null ) {
					String msg = "Config/Terms Of Service Error. terms of service is enabled but no terms of service text record ";
					log.error( msg );
					throw new ProxlWebappConfigException(msg);
				} else {
					TermsOfServiceUserAcceptedVersionHistoryDTO tosUserAccepted =
							TermsOfServiceUserAcceptedVersionHistoryDAO.getInstance()
							.getForAuthUserIdTermsOfServiceVersionId( proxlAuthUserId, tosLatestVersionId );
					if ( tosUserAccepted == null ) {
						if ( StringUtils.isEmpty( tosAcceptedKey ) ) {
							//  User has not accepted latest TOS.
							//   User is not logged in.
							loginResult.setTermsOfServiceAcceptanceRequired(true);
							if ( RETURN_TOS_TRUE.equals( returnTOS ) ) {
								TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTO = 
										TermsOfServiceTextVersionsDAO.getInstance().getLatest();
								loginResult.setTermsOfServiceKey( termsOfServiceTextVersionsDTO.getIdString() );
								loginResult.setTermsOfServiceText( termsOfServiceTextVersionsDTO.getTermsOfServiceText() );
							}
							return loginResult;  //  Early Exit
						} else {
							Integer termsOfServiceVersionIdForIdString =
									TermsOfServiceTextVersionsDAO.getInstance().getVersionIdForIdString( tosAcceptedKey );
							if ( termsOfServiceVersionIdForIdString == null ) {
								String msg = "No record for tosAcceptedKey: " + tosAcceptedKey;
								log.warn( msg );
								throw new WebApplicationException(
										Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
										.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
										.build() );  //  Early Exit with Data Exception
							}
							tosUserAccepted = new TermsOfServiceUserAcceptedVersionHistoryDTO();
							tosUserAccepted.setAuthUserId( proxlAuthUserId );
							tosUserAccepted.setTermsOfServiceVersionId( termsOfServiceVersionIdForIdString );
							TermsOfServiceUserAcceptedVersionHistoryDAO.getInstance().save( tosUserAccepted );
							//  Next validate that user accepted the LATEST Terms of Service
							if ( termsOfServiceVersionIdForIdString != tosLatestVersionId ) {
								//  User has not accepted latest TOS.
								//   User is not logged in.
								loginResult.setTermsOfServiceAcceptanceRequired(true);
								if ( RETURN_TOS_TRUE.equals( returnTOS ) ) {
									TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTONewLatest = 
											TermsOfServiceTextVersionsDAO.getInstance().getLatest();
									loginResult.setTermsOfServiceKey( termsOfServiceTextVersionsDTONewLatest.getIdString() );
									loginResult.setTermsOfServiceText( termsOfServiceTextVersionsDTONewLatest.getTermsOfServiceText() );
								}
								return loginResult;  //  Early Exit
							}
						}
					}
				}
			}
			
			AuthUserDAO.getInstance().updateLastLogin( proxlAuthUserId, request.getRemoteAddr() );
			
			XLinkUserDTO userDatabaseRecord = new XLinkUserDTO();
			AuthUserDTO authUserDTO = new AuthUserDTO();
			userDatabaseRecord.setAuthUser(authUserDTO);
			
			authUserDTO.setId( proxlAuthUserId );
			authUserDTO.setUserMgmtUserId( userMgmtUserId );
			authUserDTO.setUsername( userMgmtGetUserDataResponse.getUsername() );
			authUserDTO.setEmail( userMgmtGetUserDataResponse.getEmail() );
			authUserDTO.setUserAccessLevel( userAccessLevel );
			authUserDTO.setEnabledAppSpecific(true);
			authUserDTO.setEnabledUserMgmtGlobalLevel(true);
			
			userDatabaseRecord.setFirstName( userMgmtGetUserDataResponse.getFirstName() );
			userDatabaseRecord.setLastName( userMgmtGetUserDataResponse.getLastName() );
			userDatabaseRecord.setOrganization( userMgmtGetUserDataResponse.getOrganization() );
			
			long currentTime = System.currentTimeMillis();
			
			UserSessionObject userSessionObject = new UserSessionObject();
			userSessionObject.setLastPingToSSOServer( currentTime );
			userSessionObject.setUserLoginSessionKey( userMgmtLoginResponse.getSessionKey() );
			userSessionObject.setUserDBObject( userDatabaseRecord );
			session.setAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN, userSessionObject );
			loginResult.setStatus(true);
			
			return loginResult;
			
		} catch ( WebApplicationException e ) {
			throw e; //  Data exception so just rethrow
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
	

	/**
	 * This is returned from the web service LoginService
	 *
	 */
	public static class LoginResult {

		private boolean status = false;

		private boolean invalidUserOrPassword = false;
		private boolean disabledUser = false;
		/**
		 * No Proxl account and not auto create account since invite required
		 */
		private boolean noProxlAccount = false;
		private boolean invalidInviteTrackingCode = false;

		private boolean termsOfServiceAcceptanceRequired = false;

		private String termsOfServiceText;
		private String termsOfServiceKey;

		public boolean isInvalidUserOrPassword() {
			return invalidUserOrPassword;
		}

		public void setInvalidUserOrPassword(boolean invalidUserOrPassword) {
			this.invalidUserOrPassword = invalidUserOrPassword;
		}

		public boolean isDisabledUser() {
			return disabledUser;
		}

		public void setDisabledUser(boolean disabledUser) {
			this.disabledUser = disabledUser;
		}

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		public boolean isTermsOfServiceAcceptanceRequired() {
			return termsOfServiceAcceptanceRequired;
		}

		public void setTermsOfServiceAcceptanceRequired(
				boolean termsOfServiceAcceptanceRequired) {
			this.termsOfServiceAcceptanceRequired = termsOfServiceAcceptanceRequired;
		}

		public String getTermsOfServiceText() {
			return termsOfServiceText;
		}

		public void setTermsOfServiceText(String termsOfServiceText) {
			this.termsOfServiceText = termsOfServiceText;
		}

		public String getTermsOfServiceKey() {
			return termsOfServiceKey;
		}

		public void setTermsOfServiceKey(String termsOfServiceKey) {
			this.termsOfServiceKey = termsOfServiceKey;
		}
		public boolean isInvalidInviteTrackingCode() {
			return invalidInviteTrackingCode;
		}

		public void setInvalidInviteTrackingCode(boolean invalidInviteTrackingCode) {
			this.invalidInviteTrackingCode = invalidInviteTrackingCode;
		}
		public boolean isNoProxlAccount() {
			return noProxlAccount;
		}

		public void setNoProxlAccount(boolean noProxlAccount) {
			this.noProxlAccount = noProxlAccount;
		}
	}

}
