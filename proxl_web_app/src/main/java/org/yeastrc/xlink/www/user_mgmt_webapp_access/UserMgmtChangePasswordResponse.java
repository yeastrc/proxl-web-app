package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtChangePasswordResponse {
	
	private boolean success;
	private boolean userIdNotFound;
	private boolean userDisabled;
	
	private boolean oldPasswordNotValid;
	private boolean passwordTooLong;

	private String errorMessage;
	

	@Override
	public String toString() {
		return "UserMgmtChangePasswordResponse [success=" + success + ", userIdNotFound=" + userIdNotFound + ", userDisabled=" + userDisabled
				+ ", oldPasswordNotValid=" + oldPasswordNotValid + ", passwordTooLong=" + passwordTooLong
				+ ", errorMessage=" + errorMessage + "]";
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isOldPasswordNotValid() {
		return oldPasswordNotValid;
	}

	public void setOldPasswordNotValid(boolean oldPasswordNotValid) {
		this.oldPasswordNotValid = oldPasswordNotValid;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isPasswordTooLong() {
		return passwordTooLong;
	}
	public void setPasswordTooLong(boolean passwordTooLong) {
		this.passwordTooLong = passwordTooLong;
	}

	public boolean isUserIdNotFound() {
		return userIdNotFound;
	}
	public void setUserIdNotFound(boolean userIdNotFound) {
		this.userIdNotFound = userIdNotFound;
	}
	public boolean isUserDisabled() {
		return userDisabled;
	}
	public void setUserDisabled(boolean userDisabled) {
		this.userDisabled = userDisabled;
	}
}
