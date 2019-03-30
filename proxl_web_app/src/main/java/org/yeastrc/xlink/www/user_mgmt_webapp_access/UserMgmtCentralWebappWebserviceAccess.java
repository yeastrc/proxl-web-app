package org.yeastrc.xlink.www.user_mgmt_webapp_access;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.user_mgmt_central.webservice_connect.main.CallUserAccountMgmtWebservice;
import org.yeastrc.user_mgmt_central.webservice_connect.main.CallUserAccountMgmtWebserviceInitParameters;
import org.yeastrc.user_mgmt_central.main_code.user_mgmt_embedded_facade.UserMgmtCentral_Embedded_Facade;
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
	
	private CallUserAccountMgmtWebservice callUserAccountMgmtWebservice;
	private UserMgmtCentral_Embedded_Facade userMgmtCentral_Embedded_Facade;
	
	private boolean instanceInitialized;
	
	/**
	 * Initialize the Singleton instance
	 * @throws Exception
	 */
	public void init() {
		try {
			String userAccountServerBaseURL = ProxlConfigFileValues.getInstance().getUserAccountServerURLandAppContext();
			
			if ( StringUtils.isEmpty( userAccountServerBaseURL ) ) {
				//  Use User Account tables in local database
				
				userMgmtCentral_Embedded_Facade = UserMgmtCentral_Embedded_Facade.getInstance();
				
			} else {
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
			}
			
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
		SessionKeyAliveWebserviceResponse sessionKeyAliveWebserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			sessionKeyAliveWebserviceResponse = userMgmtCentral_Embedded_Facade.callSessionKeyAliveWebservice( webserviceRequest );
		} else { 
			sessionKeyAliveWebserviceResponse = callUserAccountMgmtWebservice.callSessionKeyAliveWebservice( webserviceRequest );
		}
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
		
		CreateAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callCreateAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callCreateAccountWebservice( webserviceRequest );
		}
		
		UserMgmtCreateAccountResponse userMgmtCreateAccountResponse = new UserMgmtCreateAccountResponse();
		userMgmtCreateAccountResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtCreateAccountResponse.setCreatedUserId( webserviceResponse.getCreatedUserId() );
		userMgmtCreateAccountResponse.setDuplicateEmail( webserviceResponse.isDuplicateEmail() );
		userMgmtCreateAccountResponse.setDuplicateUsername( webserviceResponse.isDuplicateUsername() );
		userMgmtCreateAccountResponse.setErrorMessage( webserviceResponse.getErrorMessage() );
		
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
		
		LoginAccountWebserviceRequest webserviceRequest = new LoginAccountWebserviceRequest();
		webserviceRequest.setUsername( userMgmtLoginRequest.getUsername() );
		webserviceRequest.setPasswordUserMgmtQQW( userMgmtLoginRequest.getPassword() );
		webserviceRequest.setRemoteIP( userMgmtLoginRequest.getRemoteIP() );
		
		LoginAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callLoginAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callLoginAccountWebservice( webserviceRequest );
		}
		
		UserMgmtLoginResponse userMgmtLoginResponse = new UserMgmtLoginResponse();
		userMgmtLoginResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtLoginResponse.setSessionKey( webserviceResponse.getSessionKey() );
		userMgmtLoginResponse.setUsernameNotFound( webserviceResponse.isUsernameNotFound() );
		userMgmtLoginResponse.setPasswordInvalid( webserviceResponse.isPasswordInvalid() );
		userMgmtLoginResponse.setUserDisabled( webserviceResponse.isUserDisabled() );
		userMgmtLoginResponse.setErrorMessage( webserviceResponse.getErrorMessage() );
		userMgmtLoginResponse.setUserId( webserviceResponse.getUserId() );
		
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
		
		PasswordChangeAccountWebserviceRequest webserviceRequest = new PasswordChangeAccountWebserviceRequest();
		webserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtChangePasswordRequest.getSessionKey());
		webserviceRequest.setOldPasswordFDSJKLIEOW( userMgmtChangePasswordRequest.getOldPassword() );
		webserviceRequest.setNewPasswordVCMVLSJ( userMgmtChangePasswordRequest.getNewPassword() );
		webserviceRequest.setUserRemoteIP( userMgmtChangePasswordRequest.getUserRemoteIP() );
		
		PasswordChangeAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callPasswordChangeAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callPasswordChangeAccountWebservice( webserviceRequest );
		}
		
		UserMgmtChangePasswordResponse userMgmtChangePasswordResponse = new UserMgmtChangePasswordResponse();
		userMgmtChangePasswordResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtChangePasswordResponse.setSessionKeyNotValid( webserviceResponse.isSessionKeyNotValid() );
		userMgmtChangePasswordResponse.setOldPasswordNotValid( webserviceResponse.isOldPasswordNotValid() );
		userMgmtChangePasswordResponse.setErrorMessage( webserviceResponse.getErrorMessage() );

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
		
		PasswordResetAccountWebserviceRequest webserviceRequest = new PasswordResetAccountWebserviceRequest();
		webserviceRequest.setUserMgmtUserIdWUERxcvmEWURIO( userMgmtResetPasswordRequest.getUserMgmtUserId() );
		webserviceRequest.setNewPasswordVCMVLSJ( userMgmtResetPasswordRequest.getNewPassword() );
		webserviceRequest.setUserRemoteIP( userMgmtResetPasswordRequest.getUserRemoteIP() );

		PasswordResetAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callPasswordResetAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callPasswordResetAccountWebservice( webserviceRequest );
		}
		
		UserMgmtResetPasswordResponse userMgmtResetPasswordResponse = new UserMgmtResetPasswordResponse();
		userMgmtResetPasswordResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtResetPasswordResponse.setUserIdNotValid( webserviceResponse.isUserIdNotValid() );
		userMgmtResetPasswordResponse.setErrorMessage( webserviceResponse.getErrorMessage() );

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
		GetUserDataForIdAccountWebserviceRequest webserviceRequest = new GetUserDataForIdAccountWebserviceRequest();
		webserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtGetUserDataRequest.getSessionKey() );
		webserviceRequest.setUserId( userMgmtGetUserDataRequest.getUserId() );
		
		GetUserDataForIdAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callGetUserDataForIdAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callGetUserDataForIdAccountWebservice( webserviceRequest );
		}
				
		UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = new UserMgmtGetUserDataResponse();
		userMgmtGetUserDataResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtGetUserDataResponse.setSessionKeyNotValid( webserviceResponse.isSessionKeyNotValid() );
		userMgmtGetUserDataResponse.setUserIdNotFound( webserviceResponse.isUserIdNotFound() );
		userMgmtGetUserDataResponse.setUsername( webserviceResponse.getUsername() );
		userMgmtGetUserDataResponse.setEmail( webserviceResponse.getEmail() );
		userMgmtGetUserDataResponse.setFirstName( webserviceResponse.getFirstName() );
		userMgmtGetUserDataResponse.setLastName( webserviceResponse.getLastName() );
		userMgmtGetUserDataResponse.setOrganization( webserviceResponse.getOrganization() );
		userMgmtGetUserDataResponse.setEnabled( webserviceResponse.isEnabled() );
		userMgmtGetUserDataResponse.setGlobalAdminUser( webserviceResponse.isGlobalAdminUser() );
		userMgmtGetUserDataResponse.setErrorMessage( webserviceResponse.getErrorMessage() );
		
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
		GetAccountEnabledForIdAccountWebserviceRequest webserviceRequest = new GetAccountEnabledForIdAccountWebserviceRequest();
		webserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtGetAccountEnabledRequest.getSessionKey() );
		webserviceRequest.setUserId( userMgmtGetAccountEnabledRequest.getUserId() );
		
		GetAccountEnabledForIdAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callGetAccountEnabledForIdAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callGetAccountEnabledForIdAccountWebservice( webserviceRequest );
		}
				
		UserMgmtGetAccountEnabledResponse userMgmtGetAccountEnabledResponse = new UserMgmtGetAccountEnabledResponse();
		userMgmtGetAccountEnabledResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtGetAccountEnabledResponse.setSessionKeyNotValid( webserviceResponse.isSessionKeyNotValid() );
		userMgmtGetAccountEnabledResponse.setUserIdNotFound( webserviceResponse.isUserIdNotFound() );
		userMgmtGetAccountEnabledResponse.setEnabled( webserviceResponse.isEnabled() );
		userMgmtGetAccountEnabledResponse.setErrorMessage( webserviceResponse.getErrorMessage() );
		
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
		ManageAccountWebserviceRequest webserviceRequest = new ManageAccountWebserviceRequest();
		webserviceRequest.setSessionKeyFDSJKLUIOEWVCXM( userMgmtManageAccountRequest.getSessionKey() );
		webserviceRequest.setEmail( userMgmtManageAccountRequest.getEmail() );
		webserviceRequest.setUsername( userMgmtManageAccountRequest.getUsername() );
		webserviceRequest.setFirstName( userMgmtManageAccountRequest.getFirstName() );
		webserviceRequest.setLastName( userMgmtManageAccountRequest.getLastName() );
		webserviceRequest.setOrganization( userMgmtManageAccountRequest.getOrganization() );
		webserviceRequest.setAssignOrganizationNull( userMgmtManageAccountRequest.isAssignOrganizationNull() );
		
		ManageAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callManageAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callManageAccountWebservice( webserviceRequest );
		}
				
		UserMgmtManageAccountResponse userMgmtManageAccountResponse = new UserMgmtManageAccountResponse();
		userMgmtManageAccountResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtManageAccountResponse.setSessionKeyNotValid( webserviceResponse.isSessionKeyNotValid() );
		userMgmtManageAccountResponse.setDuplicateEmail( webserviceResponse.isDuplicateEmail() );
		userMgmtManageAccountResponse.setDuplicateUsername( webserviceResponse.isDuplicateUsername() );
		userMgmtManageAccountResponse.setErrorMessage( webserviceResponse.getErrorMessage() );
		
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
		SearchUserDataAccountWebserviceRequest webserviceRequest = new SearchUserDataAccountWebserviceRequest();
		webserviceRequest.setNoSessionKeyWURIPOWmvcxuozm(true);
		webserviceRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		webserviceRequest.setSearchStringExactMatch( userMgmtSearchUserDataRequest.isSearchStringExactMatch() );

		SearchUserDataAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callSearchUserDataByLastNameAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callSearchUserDataByLastNameAccountWebservice( webserviceRequest );
		}
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setSessionKeyNotValid( webserviceResponse.isSessionKeyNotValid() );
		userMgmtSearchUserDataResponse.setErrorMessage( webserviceResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( webserviceResponse.getUserIdList() );
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
		SearchUserDataAccountWebserviceRequest webserviceRequest = new SearchUserDataAccountWebserviceRequest();
		webserviceRequest.setNoSessionKeyWURIPOWmvcxuozm(true);
		webserviceRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		webserviceRequest.setSearchStringExactMatch( userMgmtSearchUserDataRequest.isSearchStringExactMatch() );

		SearchUserDataAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callSearchUserDataByEmailAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callSearchUserDataByEmailAccountWebservice( webserviceRequest );
		}
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setSessionKeyNotValid( webserviceResponse.isSessionKeyNotValid() );
		userMgmtSearchUserDataResponse.setErrorMessage( webserviceResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( webserviceResponse.getUserIdList() );
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
		SearchUserDataAccountWebserviceRequest webserviceRequest = new SearchUserDataAccountWebserviceRequest();
		webserviceRequest.setNoSessionKeyWURIPOWmvcxuozm(true);
		webserviceRequest.setSearchStringExactMatch(true);
		webserviceRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		
		SearchUserDataAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callSearchUserDataByEmailAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callSearchUserDataByEmailAccountWebservice( webserviceRequest );
		}
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setSessionKeyNotValid( webserviceResponse.isSessionKeyNotValid() );
		userMgmtSearchUserDataResponse.setErrorMessage( webserviceResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( webserviceResponse.getUserIdList() );
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
		SearchUserDataAccountWebserviceRequest webserviceRequest = new SearchUserDataAccountWebserviceRequest();
		webserviceRequest.setNoSessionKeyWURIPOWmvcxuozm(true);
		webserviceRequest.setSearchStringExactMatch(true);
		webserviceRequest.setSearchString( userMgmtSearchUserDataRequest.getSearchString() );
		
		SearchUserDataAccountWebserviceResponse webserviceResponse = null;
		if ( userMgmtCentral_Embedded_Facade != null ) {
			webserviceResponse = userMgmtCentral_Embedded_Facade.callSearchUserDataByUsernameAccountWebservice( webserviceRequest );
		} else {
			webserviceResponse = callUserAccountMgmtWebservice.callSearchUserDataByUsernameAccountWebservice( webserviceRequest );
		}
		
		UserMgmtSearchUserDataResponse userMgmtSearchUserDataResponse = new UserMgmtSearchUserDataResponse();
		userMgmtSearchUserDataResponse.setSuccess( webserviceResponse.isSuccess() );
		userMgmtSearchUserDataResponse.setSessionKeyNotValid( webserviceResponse.isSessionKeyNotValid() );
		userMgmtSearchUserDataResponse.setErrorMessage( webserviceResponse.getErrorMessage() );
		userMgmtSearchUserDataResponse.setUserIdList( webserviceResponse.getUserIdList() );
		return userMgmtSearchUserDataResponse;
	}

}
