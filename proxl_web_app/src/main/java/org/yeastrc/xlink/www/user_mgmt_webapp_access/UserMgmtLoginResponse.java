package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtLoginResponse {

	private boolean success;
	
	private int userId;
	private String sessionKey;
	
	private boolean usernameNotFound;
	private boolean passwordInvalid;
	private boolean userDisabled;

	private String errorMessage;

	////////////
	
	@Override
	public String toString() {
		return "UserMgmtLoginResponse [success=" + success + ", userId=" + userId + ", sessionKey=" + sessionKey
				+ ", usernameNotFound=" + usernameNotFound + ", passwordInvalid=" + passwordInvalid + ", userDisabled="
				+ userDisabled + ", errorMessage=" + errorMessage + "]";
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public boolean isUsernameNotFound() {
		return usernameNotFound;
	}

	public void setUsernameNotFound(boolean usernameNotFound) {
		this.usernameNotFound = usernameNotFound;
	}

	public boolean isPasswordInvalid() {
		return passwordInvalid;
	}

	public void setPasswordInvalid(boolean passwordInvalid) {
		this.passwordInvalid = passwordInvalid;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isUserDisabled() {
		return userDisabled;
	}

	public void setUserDisabled(boolean userDisabled) {
		this.userDisabled = userDisabled;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
