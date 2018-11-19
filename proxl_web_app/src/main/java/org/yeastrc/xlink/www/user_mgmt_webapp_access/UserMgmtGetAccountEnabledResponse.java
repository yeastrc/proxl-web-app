package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtGetAccountEnabledResponse {

	private boolean success;
	
	private boolean sessionKeyNotValid;
	private boolean userIdNotFound;
	
	private boolean enabled;
	
	private String errorMessage;
	
	@Override
	public String toString() {
		return "UserMgmtGetAccountEnabledResponse [success=" + success + ", sessionKeyNotValid=" + sessionKeyNotValid
				+ ", userIdNotFound=" + userIdNotFound + ", enabled=" + enabled + ", errorMessage=" + errorMessage
				+ "]";
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isSessionKeyNotValid() {
		return sessionKeyNotValid;
	}

	public void setSessionKeyNotValid(boolean sessionKeyNotValid) {
		this.sessionKeyNotValid = sessionKeyNotValid;
	}

	public boolean isUserIdNotFound() {
		return userIdNotFound;
	}

	public void setUserIdNotFound(boolean userIdNotFound) {
		this.userIdNotFound = userIdNotFound;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
