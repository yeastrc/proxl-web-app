package org.yeastrc.xlink.www.user_mgmt_webapp_access;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.CreateAccountFacadeRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.CreateAccountFacadeResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.GetAccountEnabledForIdAccountFacadeRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.GetAccountEnabledForIdAccountFacadeResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.GetUserDataForIdAccountFacadeRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.GetUserDataForIdAccountFacadeResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.LoginAccountFacadeRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.LoginAccountFacadeResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.ManageAccountFacadeRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.ManageAccountFacadeResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.PasswordChangeAccountFacadeRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.PasswordChangeAccountFacadeResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.PasswordResetAccountFacadeRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.PasswordResetAccountFacadeResponse;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.SearchUserDataAccountFacadeRequest;
import org.yeastrc.user_mgmt_central.shared_server_client.webservice_request_response.SearchUserDataAccountFacadeResponse;
import org.yeastrc.user_mgmt_central.user_mgmt_central__embed_code.UserMgmtCentral_Embedded_Facade_Builder;
import org.yeastrc.user_mgmt_central.user_mgmt_central__embed_code.UserMgmtCentral_Embedded_Facade_IF;
import org.yeastrc.user_mgmt_central.user_mgmt_central__embed_code.UserMgmtEmbed_SQL_Connection_Provider_IF;

/**
 * Singleton instance
 * 
 * Central access point to the User Mgmt Central Webapp
 *
 */
public class UserMgmtCentralWebappWebserviceAccess {
	private static final Logger log = LoggerFactory.getLogger(  UserMgmtCentralWebappWebserviceAccess.class );
	
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
	
	private UserMgmtCentral_Embedded_Facade_IF userMgmtCentral_Embedded_Facade;
	
	private boolean instanceInitialized;
	
	/**
	 * Initialize the Singleton instance
	 * @throws Exception
	 */
	public void init( UserMgmtEmbed_SQL_Connection_Provider_IF userMgmtEmbed_SQL_Connection_Provider ) {
		try {
			
			userMgmtCentral_Embedded_Facade = 
					UserMgmtCentral_Embedded_Facade_Builder
					.getBuilder()
					.setUserMgmtEmbed_SQL_Connection_Provider_IF( userMgmtEmbed_SQL_Connection_Provider )
					.build();
				
		} catch (Exception e) {
			String msg = "Failed to initialize the code to access the User Mgmt Webapp";
			log.error(msg, e);
			throw new RuntimeException( msg, e );
		} 
		instanceInitialized = true;
	}
	
	/**
	 * Only for setting a password (create account, change password, reset password).  NOT for sign in.
	 * 
	 * @return Maximum password length in UTF-8 bytes
	 */
	public int getMaxPasswordLength_UTF8_Bytes() {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		return userMgmtCentral_Embedded_Facade.getMaxPasswordLength_UTF8_Bytes();
	}
	

	/**
	 * @param userMgmtCreateAccountRequest
	 * @return
	 * @throws Exception
	 */
	public UserMgmtCreateAccountResponse createUser( UserMgmtCreateAccountRequest userMgmtCreateAccountRequest ) throws Exception {
		
		CreateAccountFacadeRequest facadeRequest = new CreateAccountFacadeRequest();
		
		facadeRequest.setEmail( userMgmtCreateAccountRequest.getEmail() );
		facadeRequest.setUsername( userMgmtCreateAccountRequest.getUsername() );
		facadeRequest.setFirstName( userMgmtCreateAccountRequest.getFirstName() );
		facadeRequest.setLastName( userMgmtCreateAccountRequest.getLastName() );
		facadeRequest.setOrganization( userMgmtCreateAccountRequest.getOrganization() );
		facadeRequest.setPassword( userMgmtCreateAccountRequest.getPassword() );
		facadeRequest.setUserRemoteIP( userMgmtCreateAccountRequest.getUserRemoteIP() );
		
		CreateAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.createAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
		
		UserMgmtCreateAccountResponse userMgmtCreateAccountResponse = new UserMgmtCreateAccountResponse();
		userMgmtCreateAccountResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtCreateAccountResponse.setCreatedUserId( facadeResponse.getCreatedUserId() );
		userMgmtCreateAccountResponse.setDuplicateEmail( facadeResponse.isDuplicateEmail() );
		userMgmtCreateAccountResponse.setDuplicateUsername( facadeResponse.isDuplicateUsername() );
		userMgmtCreateAccountResponse.setPasswordTooLong( facadeResponse.isPasswordTooLong() );
		userMgmtCreateAccountResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		
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
		
		LoginAccountFacadeRequest facadeRequest = new LoginAccountFacadeRequest();
		facadeRequest.setUsername( userMgmtLoginRequest.getUsername() );
		facadeRequest.setPasswordUserMgmtQQW( userMgmtLoginRequest.getPassword() );
		facadeRequest.setRemoteIP( userMgmtLoginRequest.getRemoteIP() );
		
		LoginAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.loginAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
		
		UserMgmtLoginResponse userMgmtLoginResponse = new UserMgmtLoginResponse();
		userMgmtLoginResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtLoginResponse.setUsernameNotFound( facadeResponse.isUsernameNotFound() );
		userMgmtLoginResponse.setPasswordInvalid( facadeResponse.isPasswordInvalid() );
		userMgmtLoginResponse.setUserDisabled( facadeResponse.isUserDisabled() );
		userMgmtLoginResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		userMgmtLoginResponse.setUserId( facadeResponse.getUserId() );
		
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
		
		PasswordChangeAccountFacadeRequest facadeRequest = new PasswordChangeAccountFacadeRequest();
		facadeRequest.setUserId( userMgmtChangePasswordRequest.getUserMgmtUserId() );
		facadeRequest.setOldPasswordFDSJKLIEOW( userMgmtChangePasswordRequest.getOldPassword() );
		facadeRequest.setNewPasswordVCMVLSJ( userMgmtChangePasswordRequest.getNewPassword() );
		facadeRequest.setUserRemoteIP( userMgmtChangePasswordRequest.getUserRemoteIP() );
		
		PasswordChangeAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.passwordChangeAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
		
		UserMgmtChangePasswordResponse userMgmtChangePasswordResponse = new UserMgmtChangePasswordResponse();
		userMgmtChangePasswordResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtChangePasswordResponse.setUserIdNotFound( facadeResponse.isUserIdNotFound() );
		userMgmtChangePasswordResponse.setUserDisabled( facadeResponse.isUserDisabled() );
		userMgmtChangePasswordResponse.setOldPasswordNotValid( facadeResponse.isOldPasswordNotValid() );
		userMgmtChangePasswordResponse.setPasswordTooLong( facadeResponse.isPasswordTooLong() );
		userMgmtChangePasswordResponse.setErrorMessage( facadeResponse.getErrorMessage() );

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
		
		PasswordResetAccountFacadeRequest facadeRequest = new PasswordResetAccountFacadeRequest();
		facadeRequest.setUserMgmtUserIdWUERxcvmEWURIO( userMgmtResetPasswordRequest.getUserMgmtUserId() );
		facadeRequest.setNewPasswordVCMVLSJ( userMgmtResetPasswordRequest.getNewPassword() );
		facadeRequest.setUserRemoteIP( userMgmtResetPasswordRequest.getUserRemoteIP() );

		PasswordResetAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.passwordResetAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
		
		UserMgmtResetPasswordResponse userMgmtResetPasswordResponse = new UserMgmtResetPasswordResponse();
		userMgmtResetPasswordResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtResetPasswordResponse.setUserIdNotValid( facadeResponse.isUserIdNotValid() );
		userMgmtResetPasswordResponse.setUserDisabled( facadeResponse.isUserDisabled() );
		userMgmtResetPasswordResponse.setPasswordTooLong( facadeResponse.isPasswordTooLong() );
		userMgmtResetPasswordResponse.setErrorMessage( facadeResponse.getErrorMessage() );

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
		GetUserDataForIdAccountFacadeRequest facadeRequest = new GetUserDataForIdAccountFacadeRequest();
		facadeRequest.setUserId( userMgmtGetUserDataRequest.getUserId() );
		
		GetUserDataForIdAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.getUserDataForIdAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
				
		UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = new UserMgmtGetUserDataResponse();
		userMgmtGetUserDataResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtGetUserDataResponse.setUserIdNotFound( facadeResponse.isUserIdNotFound() );
		userMgmtGetUserDataResponse.setUsername( facadeResponse.getUsername() );
		userMgmtGetUserDataResponse.setEmail( facadeResponse.getEmail() );
		userMgmtGetUserDataResponse.setFirstName( facadeResponse.getFirstName() );
		userMgmtGetUserDataResponse.setLastName( facadeResponse.getLastName() );
		userMgmtGetUserDataResponse.setOrganization( facadeResponse.getOrganization() );
		userMgmtGetUserDataResponse.setEnabled( facadeResponse.isEnabled() );
		userMgmtGetUserDataResponse.setGlobalAdminUser( facadeResponse.isGlobalAdminUser() );
		userMgmtGetUserDataResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		
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
		GetAccountEnabledForIdAccountFacadeRequest facadeRequest = new GetAccountEnabledForIdAccountFacadeRequest();
		facadeRequest.setUserId( userMgmtGetAccountEnabledRequest.getUserId() );
		
		GetAccountEnabledForIdAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.getAccountEnabledForIdAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
				
		UserMgmtGetAccountEnabledResponse userMgmtGetAccountEnabledResponse = new UserMgmtGetAccountEnabledResponse();
		userMgmtGetAccountEnabledResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtGetAccountEnabledResponse.setUserIdNotFound( facadeResponse.isUserIdNotFound() );
		userMgmtGetAccountEnabledResponse.setEnabled( facadeResponse.isEnabled() );
		userMgmtGetAccountEnabledResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		
		return userMgmtGetAccountEnabledResponse;
	}

	/**
	 * Only for the user enabled re-check (UserMgmt_UserEnabled_RecheckCache).  Uses a short timeout when User Mgmt is called over the network.
	 * 
	 * @param userMgmtGetAccountEnabledRequest
	 * @param totalTimeoutMillis
	 * @return
	 * @throws Exception
	 */
	public UserMgmtGetAccountEnabledResponse getAccountEnabled_ForRecheck( UserMgmtGetAccountEnabledRequest userMgmtGetAccountEnabledRequest, int totalTimeoutMillis ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		GetAccountEnabledForIdAccountFacadeRequest facadeRequest = new GetAccountEnabledForIdAccountFacadeRequest();
		facadeRequest.setUserId( userMgmtGetAccountEnabledRequest.getUserId() );
		
		GetAccountEnabledForIdAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.getAccountEnabledForIdAccount_ShortTimeout( facadeRequest, totalTimeoutMillis );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
				
		UserMgmtGetAccountEnabledResponse userMgmtGetAccountEnabledResponse = new UserMgmtGetAccountEnabledResponse();
		userMgmtGetAccountEnabledResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtGetAccountEnabledResponse.setUserIdNotFound( facadeResponse.isUserIdNotFound() );
		userMgmtGetAccountEnabledResponse.setEnabled( facadeResponse.isEnabled() );
		userMgmtGetAccountEnabledResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		
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
		ManageAccountFacadeRequest facadeRequest = new ManageAccountFacadeRequest();
		facadeRequest.setUserId( userMgmtManageAccountRequest.getUserMgmtUserId() );
		facadeRequest.setCurrentPasswordQWZNVKSD( userMgmtManageAccountRequest.getCurrentPassword() );
		facadeRequest.setEmail( userMgmtManageAccountRequest.getEmail() );
		facadeRequest.setUsername( userMgmtManageAccountRequest.getUsername() );
		facadeRequest.setFirstName( userMgmtManageAccountRequest.getFirstName() );
		facadeRequest.setLastName( userMgmtManageAccountRequest.getLastName() );
		facadeRequest.setOrganization( userMgmtManageAccountRequest.getOrganization() );
		facadeRequest.setAssignOrganizationNull( userMgmtManageAccountRequest.isAssignOrganizationNull() );
		
		ManageAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.manageAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
				
		UserMgmtManageAccountResponse userMgmtManageAccountResponse = new UserMgmtManageAccountResponse();
		userMgmtManageAccountResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtManageAccountResponse.setUserIdNotFound( facadeResponse.isUserIdNotFound() );
		userMgmtManageAccountResponse.setUserDisabled( facadeResponse.isUserDisabled() );
		userMgmtManageAccountResponse.setCurrentPasswordRequired( facadeResponse.isCurrentPasswordRequired() );
		userMgmtManageAccountResponse.setCurrentPasswordInvalid( facadeResponse.isCurrentPasswordInvalid() );
		userMgmtManageAccountResponse.setDuplicateEmail( facadeResponse.isDuplicateEmail() );
		userMgmtManageAccountResponse.setDuplicateUsername( facadeResponse.isDuplicateUsername() );
		userMgmtManageAccountResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		
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
		SearchUserDataAccountFacadeRequest facadeRequest = new SearchUserDataAccountFacadeRequest();
		facadeRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		facadeRequest.setSearchStringExactMatch( userMgmtSearchUserDataRequest.isSearchStringExactMatch() );

		SearchUserDataAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.searchUserDataByLastNameAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( facadeResponse.getUserIdList() );
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
		SearchUserDataAccountFacadeRequest facadeRequest = new SearchUserDataAccountFacadeRequest();
		facadeRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		facadeRequest.setSearchStringExactMatch( userMgmtSearchUserDataRequest.isSearchStringExactMatch() );

		SearchUserDataAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.searchUserDataByEmailAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( facadeResponse.getUserIdList() );
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
		SearchUserDataAccountFacadeRequest facadeRequest = new SearchUserDataAccountFacadeRequest();
		facadeRequest.setSearchStringExactMatch(true);
		facadeRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		
		SearchUserDataAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.searchUserDataByEmailAccount( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( facadeResponse.getUserIdList() );
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
		SearchUserDataAccountFacadeRequest facadeRequest = new SearchUserDataAccountFacadeRequest();
		facadeRequest.setSearchStringExactMatch(true);
		facadeRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		
		SearchUserDataAccountFacadeResponse facadeResponse = userMgmtCentral_Embedded_Facade.searchUserDataByUserName( facadeRequest );
		UserMgmt_UserEnabled_RecheckCache.getInstance().receivedIntervalFromUserMgmt( facadeResponse.getUserEnabledRecheckIntervalSeconds() );
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( facadeResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setErrorMessage( facadeResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( facadeResponse.getUserIdList() );
		return userMgmtSearchUserDataResponse;
	}

}
