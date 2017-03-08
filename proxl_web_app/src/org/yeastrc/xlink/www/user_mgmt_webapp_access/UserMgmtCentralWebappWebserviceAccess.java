package org.yeastrc.xlink.www.user_mgmt_webapp_access;

import org.apache.log4j.Logger;
import org.yeastrc.user_mgmt_central.webservice_connect.main.CallUserAccountMgmtWebservice;
import org.yeastrc.user_mgmt_central.webservice_connect.main.CallUserAccountMgmtWebserviceInitParameters;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.CreateAccountWebserviceRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.CreateAccountWebserviceResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.GetAccountEnabledForIdAccountWebserviceRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.GetAccountEnabledForIdAccountWebserviceResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.GetUserDataForIdAccountWebserviceRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.GetUserDataForIdAccountWebserviceResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.LoginAccountWebserviceRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.LoginAccountWebserviceResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.ManageAccountWebserviceRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.ManageAccountWebserviceResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.PasswordChangeAccountWebserviceRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.PasswordChangeAccountWebserviceResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.PasswordResetAccountWebserviceRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.PasswordResetAccountWebserviceResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.SearchUserDataAccountWebserviceRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.SearchUserDataAccountWebserviceResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.SessionKeyAliveWebserviceRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.SessionKeyAliveWebserviceResponse;
import org.yeastrc.xlink.www.config_properties_file.ProxlConfigFileValues;

/**
 * Singleton instance
 * 
 * Central access point to the User Mgmt Central Webapp
 *
 */
public class UserMgmtCentralWebappWebserviceAccess {
	private static Logger log = Logger.getLogger( UserMgmtCentralWebappWebserviceAccess.class );
	
	private static final UserMgmtCentralWebappWebserviceAccess instance = new UserMgmtCentralWebappWebserviceAccess();
	
	/**
	 * @return Singleton instance
	 */
	public static UserMgmtCentralWebappWebserviceAccess getInstance() {
		return instance;
	}
	
	/**
	 * private constructor
	 */
	private UserMgmtCentralWebappWebserviceAccess() {}
	
	private CallUserAccountMgmtWebservice callUserAccountMgmtWebservice;
	
	private boolean instanceInitialized;
	
	/**
	 * Initialize the Singleton instance
	 * @throws Exception
	 */
	public void init() {
		try {
			String userAccountServerBaseURL = ProxlConfigFileValues.getInstance().getUserAccountServerURLandAppContext();
			String requestingWebappIdentifier = ProxlConfigFileValues.getInstance().getRequestingWebappIdentifier();
			String requestingWebappKey = ProxlConfigFileValues.getInstance().getRequestingWebappKey();
			String requestingEncryptionKey = ProxlConfigFileValues.getInstance().getRequestingEncryptionKey();

			CallUserAccountMgmtWebserviceInitParameters initParams = new CallUserAccountMgmtWebserviceInitParameters();
			initParams.setUserAccountServerBaseURL( userAccountServerBaseURL );
			initParams.setRequestingWebappIdentifier( requestingWebappIdentifier );
			initParams.setRequestingWebappKey( requestingWebappKey );
			initParams.setRequestingEncryptionKey( requestingEncryptionKey );
			callUserAccountMgmtWebservice = CallUserAccountMgmtWebservice.getInstance();
			callUserAccountMgmtWebservice.init( initParams );
		} catch (Exception e) {
			String msg = "Failed to initialize the code to access the User Mgmt Webapp";
			log.error(msg, e);
			throw new RuntimeException( msg, e );
		} 
		instanceInitialized = true;
	}
	

	/**
	 * @param userMgmtSessionKeyAliveWebserviceRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtSessionKeyAliveWebserviceResponse sessionKeyAlive( UserMgmtSessionKeyAliveWebserviceRequest userMgmtSessionKeyAliveWebserviceRequest ) throws Exception {
		
		SessionKeyAliveWebserviceRequest webserviceRequest = new SessionKeyAliveWebserviceRequest();
		
		webserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtSessionKeyAliveWebserviceRequest.getSessionKey() );
		
		SessionKeyAliveWebserviceResponse sessionKeyAliveWebserviceResponse =
				callUserAccountMgmtWebservice.callSessionKeyAliveWebservice( webserviceRequest );
		
		UserMgmtSessionKeyAliveWebserviceResponse userMgmtSessionKeyAliveWebserviceResponse = new UserMgmtSessionKeyAliveWebserviceResponse();
		userMgmtSessionKeyAliveWebserviceResponse.setSuccess( sessionKeyAliveWebserviceResponse.isSuccess() );
		userMgmtSessionKeyAliveWebserviceResponse.setSessionKeyNotValid( sessionKeyAliveWebserviceResponse.isSessionKeyNotValid() );
		userMgmtSessionKeyAliveWebserviceResponse.setErrorMessage( sessionKeyAliveWebserviceResponse.getErrorMessage() );
		
		return userMgmtSessionKeyAliveWebserviceResponse;
	}
	
	/**
	 * @param userMgmtCreateAccountRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtCreateAccountResponse createUser( UserMgmtCreateAccountRequest userMgmtCreateAccountRequest ) throws Exception {
		
		CreateAccountWebserviceRequest webserviceRequest = new CreateAccountWebserviceRequest();
		
		webserviceRequest.setEmail( userMgmtCreateAccountRequest.getEmail() );
		webserviceRequest.setUsername( userMgmtCreateAccountRequest.getUsername() );
		webserviceRequest.setFirstName( userMgmtCreateAccountRequest.getFirstName() );
		webserviceRequest.setLastName( userMgmtCreateAccountRequest.getLastName() );
		webserviceRequest.setOrganization( userMgmtCreateAccountRequest.getOrganization() );
		webserviceRequest.setPassword( userMgmtCreateAccountRequest.getPassword() );
		webserviceRequest.setUserRemoteIP( userMgmtCreateAccountRequest.getUserRemoteIP() );
		
		CreateAccountWebserviceResponse createAccountWebserviceResponse =
				callUserAccountMgmtWebservice.callCreateAccountWebservice( webserviceRequest );
		
		UserMgmtCreateAccountResponse userMgmtCreateAccountResponse = new UserMgmtCreateAccountResponse();
		userMgmtCreateAccountResponse.setSuccess( createAccountWebserviceResponse.isSuccess() );
		userMgmtCreateAccountResponse.setCreatedUserId( createAccountWebserviceResponse.getCreatedUserId() );
		userMgmtCreateAccountResponse.setDuplicateEmail( createAccountWebserviceResponse.isDuplicateEmail() );
		userMgmtCreateAccountResponse.setDuplicateUsername( createAccountWebserviceResponse.isDuplicateUsername() );
		userMgmtCreateAccountResponse.setErrorMessage( createAccountWebserviceResponse.getErrorMessage() );
		
		return userMgmtCreateAccountResponse;
	}
	
	/**
	 * @param userMgmtLoginRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtLoginResponse userLogin( UserMgmtLoginRequest userMgmtLoginRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		
		LoginAccountWebserviceRequest loginAccountWebserviceRequest = new LoginAccountWebserviceRequest();
		loginAccountWebserviceRequest.setUsername( userMgmtLoginRequest.getUsername() );
		loginAccountWebserviceRequest.setPasswordUserMgmtQQW( userMgmtLoginRequest.getPassword() );
		loginAccountWebserviceRequest.setRemoteIP( userMgmtLoginRequest.getRemoteIP() );
		
		LoginAccountWebserviceResponse loginAccountWebserviceResponse =
				callUserAccountMgmtWebservice.callLoginAccountWebservice( loginAccountWebserviceRequest );
		
		UserMgmtLoginResponse userMgmtLoginResponse = new UserMgmtLoginResponse();
		userMgmtLoginResponse.setSuccess( loginAccountWebserviceResponse.isSuccess() );
		userMgmtLoginResponse.setSessionKey( loginAccountWebserviceResponse.getSessionKey() );
		userMgmtLoginResponse.setUsernameNotFound( loginAccountWebserviceResponse.isUsernameNotFound() );
		userMgmtLoginResponse.setPasswordInvalid( loginAccountWebserviceResponse.isPasswordInvalid() );
		userMgmtLoginResponse.setUserDisabled( loginAccountWebserviceResponse.isUserDisabled() );
		userMgmtLoginResponse.setErrorMessage( loginAccountWebserviceResponse.getErrorMessage() );
		userMgmtLoginResponse.setUserId( loginAccountWebserviceResponse.getUserId() );
		
		return userMgmtLoginResponse;
	}
	
	

	/**
	 * @param userMgmtChangePasswordRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtChangePasswordResponse changePassword( UserMgmtChangePasswordRequest userMgmtChangePasswordRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		
		PasswordChangeAccountWebserviceRequest passwordChangeAccountWebserviceRequest = new PasswordChangeAccountWebserviceRequest();
		passwordChangeAccountWebserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtChangePasswordRequest.getSessionKey());
		passwordChangeAccountWebserviceRequest.setOldPasswordFDSJKLIEOW( userMgmtChangePasswordRequest.getOldPassword() );
		passwordChangeAccountWebserviceRequest.setNewPasswordVCMVLSJ( userMgmtChangePasswordRequest.getNewPassword() );
		passwordChangeAccountWebserviceRequest.setUserRemoteIP( userMgmtChangePasswordRequest.getUserRemoteIP() );
		
		PasswordChangeAccountWebserviceResponse passwordChangeAccountWebserviceResponse =
				callUserAccountMgmtWebservice.callPasswordChangeAccountWebservice( passwordChangeAccountWebserviceRequest );
		
		UserMgmtChangePasswordResponse userMgmtChangePasswordResponse = new UserMgmtChangePasswordResponse();
		userMgmtChangePasswordResponse.setSuccess( passwordChangeAccountWebserviceResponse.isSuccess() );
		userMgmtChangePasswordResponse.setSessionKeyNotValid( passwordChangeAccountWebserviceResponse.isSessionKeyNotValid() );
		userMgmtChangePasswordResponse.setOldPasswordNotValid( passwordChangeAccountWebserviceResponse.isOldPasswordNotValid() );
		userMgmtChangePasswordResponse.setErrorMessage( passwordChangeAccountWebserviceResponse.getErrorMessage() );

		return userMgmtChangePasswordResponse;
	}
	

	/**
	 * @param userMgmtResetPasswordRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtResetPasswordResponse resetPassword( UserMgmtResetPasswordRequest userMgmtResetPasswordRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		
		PasswordResetAccountWebserviceRequest passwordResetAccountWebserviceRequest = new PasswordResetAccountWebserviceRequest();
		passwordResetAccountWebserviceRequest.setUserMgmtUserIdWUERxcvmEWURIO( userMgmtResetPasswordRequest.getUserMgmtUserId() );
		passwordResetAccountWebserviceRequest.setNewPasswordVCMVLSJ( userMgmtResetPasswordRequest.getNewPassword() );
		passwordResetAccountWebserviceRequest.setUserRemoteIP( userMgmtResetPasswordRequest.getUserRemoteIP() );

		PasswordResetAccountWebserviceResponse passwordResetAccountWebserviceResponse =
				callUserAccountMgmtWebservice.callPasswordResetAccountWebservice( passwordResetAccountWebserviceRequest );
		
		UserMgmtResetPasswordResponse userMgmtResetPasswordResponse = new UserMgmtResetPasswordResponse();
		userMgmtResetPasswordResponse.setSuccess( passwordResetAccountWebserviceResponse.isSuccess() );
		userMgmtResetPasswordResponse.setUserIdNotValid( passwordResetAccountWebserviceResponse.isUserIdNotValid() );
		userMgmtResetPasswordResponse.setErrorMessage( passwordResetAccountWebserviceResponse.getErrorMessage() );

		return userMgmtResetPasswordResponse;
	}
	
	/**
	 * @param userMgmtGetUserDataRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtGetUserDataResponse getUserData( UserMgmtGetUserDataRequest userMgmtGetUserDataRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		GetUserDataForIdAccountWebserviceRequest getUserDataForIdAccountWebserviceRequest = new GetUserDataForIdAccountWebserviceRequest();
		getUserDataForIdAccountWebserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtGetUserDataRequest.getSessionKey() );
		getUserDataForIdAccountWebserviceRequest.setUserId( userMgmtGetUserDataRequest.getUserId() );
		
		GetUserDataForIdAccountWebserviceResponse getUserDataForIdAccountWebserviceResponse =
				callUserAccountMgmtWebservice.callGetUserDataForIdAccountWebservice( getUserDataForIdAccountWebserviceRequest );
				
		UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = new UserMgmtGetUserDataResponse();
		userMgmtGetUserDataResponse.setSuccess( getUserDataForIdAccountWebserviceResponse.isSuccess() );
		userMgmtGetUserDataResponse.setSessionKeyNotValid( getUserDataForIdAccountWebserviceResponse.isSessionKeyNotValid() );
		userMgmtGetUserDataResponse.setUserIdNotFound( getUserDataForIdAccountWebserviceResponse.isUserIdNotFound() );
		userMgmtGetUserDataResponse.setUsername( getUserDataForIdAccountWebserviceResponse.getUsername() );
		userMgmtGetUserDataResponse.setEmail( getUserDataForIdAccountWebserviceResponse.getEmail() );
		userMgmtGetUserDataResponse.setFirstName( getUserDataForIdAccountWebserviceResponse.getFirstName() );
		userMgmtGetUserDataResponse.setLastName( getUserDataForIdAccountWebserviceResponse.getLastName() );
		userMgmtGetUserDataResponse.setOrganization( getUserDataForIdAccountWebserviceResponse.getOrganization() );
		userMgmtGetUserDataResponse.setEnabled( getUserDataForIdAccountWebserviceResponse.isEnabled() );
		userMgmtGetUserDataResponse.setGlobalAdminUser( getUserDataForIdAccountWebserviceResponse.isGlobalAdminUser() );
		userMgmtGetUserDataResponse.setErrorMessage( getUserDataForIdAccountWebserviceResponse.getErrorMessage() );
		
		return userMgmtGetUserDataResponse;
	}

	/**
	 * @param userMgmtGetAccountEnabledRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtGetAccountEnabledResponse getAccountEnabled( UserMgmtGetAccountEnabledRequest userMgmtGetAccountEnabledRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		GetAccountEnabledForIdAccountWebserviceRequest getAccountEnabledForIdAccountWebserviceRequest = new GetAccountEnabledForIdAccountWebserviceRequest();
		getAccountEnabledForIdAccountWebserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtGetAccountEnabledRequest.getSessionKey() );
		getAccountEnabledForIdAccountWebserviceRequest.setUserId( userMgmtGetAccountEnabledRequest.getUserId() );
		
		GetAccountEnabledForIdAccountWebserviceResponse getAccountEnabledForIdAccountWebserviceResponse =
				callUserAccountMgmtWebservice.callGetAccountEnabledForIdAccountWebservice( getAccountEnabledForIdAccountWebserviceRequest );
				
		UserMgmtGetAccountEnabledResponse userMgmtGetAccountEnabledResponse = new UserMgmtGetAccountEnabledResponse();
		userMgmtGetAccountEnabledResponse.setSuccess( getAccountEnabledForIdAccountWebserviceResponse.isSuccess() );
		userMgmtGetAccountEnabledResponse.setSessionKeyNotValid( getAccountEnabledForIdAccountWebserviceResponse.isSessionKeyNotValid() );
		userMgmtGetAccountEnabledResponse.setUserIdNotFound( getAccountEnabledForIdAccountWebserviceResponse.isUserIdNotFound() );
		userMgmtGetAccountEnabledResponse.setEnabled( getAccountEnabledForIdAccountWebserviceResponse.isEnabled() );
		userMgmtGetAccountEnabledResponse.setErrorMessage( getAccountEnabledForIdAccountWebserviceResponse.getErrorMessage() );
		
		return userMgmtGetAccountEnabledResponse;
	}
			


	/**
	 * @param userMgmtManageAccountRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtManageAccountResponse manageUserData( UserMgmtManageAccountRequest userMgmtManageAccountRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		ManageAccountWebserviceRequest manageAccountWebserviceRequest = new ManageAccountWebserviceRequest();
		manageAccountWebserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtManageAccountRequest.getSessionKey() );
		manageAccountWebserviceRequest.setEmail( userMgmtManageAccountRequest.getEmail() );
		manageAccountWebserviceRequest.setUsername( userMgmtManageAccountRequest.getUsername() );
		manageAccountWebserviceRequest.setFirstName( userMgmtManageAccountRequest.getFirstName() );
		manageAccountWebserviceRequest.setLastName( userMgmtManageAccountRequest.getLastName() );
		manageAccountWebserviceRequest.setOrganization( userMgmtManageAccountRequest.getOrganization() );
		manageAccountWebserviceRequest.setAssignOrganizationNull( userMgmtManageAccountRequest.isAssignOrganizationNull() );
		
		ManageAccountWebserviceResponse manageAccountWebserviceResponse =
				callUserAccountMgmtWebservice.callManageAccountWebservice( manageAccountWebserviceRequest );
				
		UserMgmtManageAccountResponse userMgmtManageAccountResponse = new UserMgmtManageAccountResponse();
		userMgmtManageAccountResponse.setSuccess( manageAccountWebserviceResponse.isSuccess() );
		userMgmtManageAccountResponse.setSessionKeyNotValid( manageAccountWebserviceResponse.isSessionKeyNotValid() );
		userMgmtManageAccountResponse.setDuplicateEmail( manageAccountWebserviceResponse.isDuplicateEmail() );
		userMgmtManageAccountResponse.setDuplicateUsername( manageAccountWebserviceResponse.isDuplicateUsername() );
		userMgmtManageAccountResponse.setErrorMessage( manageAccountWebserviceResponse.getErrorMessage() );
		
		return userMgmtManageAccountResponse;
	}
	

	/**
	 * @param userMgmtSearchUserDataRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtSearchUserDataResponse searchUserDataByLastName( UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		SearchUserDataAccountWebserviceRequest searchUserDataAccountWebserviceRequest = new SearchUserDataAccountWebserviceRequest();
		searchUserDataAccountWebserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtSearchUserDataRequest.getSessionKey() );
		searchUserDataAccountWebserviceRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		searchUserDataAccountWebserviceRequest.setSearchStringExactMatch( userMgmtSearchUserDataRequest.isSearchStringExactMatch() );

		SearchUserDataAccountWebserviceResponse searchUserDataAccountWebserviceResponse = 
				callUserAccountMgmtWebservice.callSearchUserDataByLastNameAccountWebservice( searchUserDataAccountWebserviceRequest );
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( searchUserDataAccountWebserviceResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setSessionKeyNotValid( searchUserDataAccountWebserviceResponse.isSessionKeyNotValid() );
		userMgmtSearchUserDataResponse.setErrorMessage( searchUserDataAccountWebserviceResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( searchUserDataAccountWebserviceResponse.getUserIdList() );
		return userMgmtSearchUserDataResponse;
	}
	

	/**
	 * @param userMgmtSearchUserDataRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtSearchUserDataResponse searchUserDataByEmail( UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		SearchUserDataAccountWebserviceRequest searchUserDataAccountWebserviceRequest = new SearchUserDataAccountWebserviceRequest();
		searchUserDataAccountWebserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtSearchUserDataRequest.getSessionKey() );
		searchUserDataAccountWebserviceRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		searchUserDataAccountWebserviceRequest.setSearchStringExactMatch( userMgmtSearchUserDataRequest.isSearchStringExactMatch() );

		SearchUserDataAccountWebserviceResponse searchUserDataAccountWebserviceResponse = 
				callUserAccountMgmtWebservice.callSearchUserDataByEmailAccountWebservice( searchUserDataAccountWebserviceRequest );
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( searchUserDataAccountWebserviceResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setSessionKeyNotValid( searchUserDataAccountWebserviceResponse.isSessionKeyNotValid() );
		userMgmtSearchUserDataResponse.setErrorMessage( searchUserDataAccountWebserviceResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( searchUserDataAccountWebserviceResponse.getUserIdList() );
		return userMgmtSearchUserDataResponse;
	}


	/**
	 * @param userMgmtSearchUserDataRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtSearchUserDataResponse searchUserDataByEmailExactMatchNoUserSession( UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		SearchUserDataAccountWebserviceRequest searchUserDataAccountWebserviceRequest = new SearchUserDataAccountWebserviceRequest();
		searchUserDataAccountWebserviceRequest.setNoSessionKeyWURIPOWmvcxuozm(true);
		searchUserDataAccountWebserviceRequest.setSearchStringExactMatch(true);
		searchUserDataAccountWebserviceRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		
		SearchUserDataAccountWebserviceResponse searchUserDataAccountWebserviceResponse = 
				callUserAccountMgmtWebservice.callSearchUserDataByEmailAccountWebservice( searchUserDataAccountWebserviceRequest );
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( searchUserDataAccountWebserviceResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setSessionKeyNotValid( searchUserDataAccountWebserviceResponse.isSessionKeyNotValid() );
		userMgmtSearchUserDataResponse.setErrorMessage( searchUserDataAccountWebserviceResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( searchUserDataAccountWebserviceResponse.getUserIdList() );
		return userMgmtSearchUserDataResponse;
	}


	/**
	 * @param userMgmtSearchUserDataRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtSearchUserDataResponse searchUserDataByUsernameExactMatchNoUserSession( UserMgmtSearchUserDataRequest userMgmtSearchUserDataRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		SearchUserDataAccountWebserviceRequest searchUserDataAccountWebserviceRequest = new SearchUserDataAccountWebserviceRequest();
		searchUserDataAccountWebserviceRequest.setNoSessionKeyWURIPOWmvcxuozm(true);
		searchUserDataAccountWebserviceRequest.setSearchStringExactMatch(true);
		searchUserDataAccountWebserviceRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		
		SearchUserDataAccountWebserviceResponse searchUserDataAccountWebserviceResponse = 
				callUserAccountMgmtWebservice.callSearchUserDataByUsernameAccountWebservice( searchUserDataAccountWebserviceRequest );
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( searchUserDataAccountWebserviceResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setSessionKeyNotValid( searchUserDataAccountWebserviceResponse.isSessionKeyNotValid() );
		userMgmtSearchUserDataResponse.setErrorMessage( searchUserDataAccountWebserviceResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( searchUserDataAccountWebserviceResponse.getUserIdList() );
		return userMgmtSearchUserDataResponse;
	}

}
