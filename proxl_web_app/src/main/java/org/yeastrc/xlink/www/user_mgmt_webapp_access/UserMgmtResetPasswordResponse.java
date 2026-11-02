package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtResetPasswordResponse {
	private boolean success;
	private boolean userIdNotValid;
	private boolean userDisabled;
	private boolean passwordTooLong;
	private String errorMessage;
	
	
	@Override
	public String toString() {
		return "UserMgmtResetPasswordResponse [success=" + success + ", userIdNotValid=" + userIdNotValid + ", userDisabled=" + userDisabled
				+ ", passwordTooLong=" + passwordTooLong
				+ ", errorMessage=" + errorMessage + "]";
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public boolean isUserIdNotValid() {
		return userIdNotValid;
	}
	public void setUserIdNotValid(boolean userIdNotValid) {
		this.userIdNotValid = userIdNotValid;
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

	public boolean isUserDisabled() {
		return userDisabled;
	}
	public void setUserDisabled(boolean userDisabled) {
		this.userDisabled = userDisabled;
	}
}
