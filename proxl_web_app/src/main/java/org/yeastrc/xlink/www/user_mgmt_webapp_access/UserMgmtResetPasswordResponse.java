package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtResetPasswordResponse {
	private boolean success;
	private boolean userIdNotValid;
	private String errorMessage;
	
	
	@Override
	public String toString() {
		return "UserMgmtResetPasswordResponse [success=" + success + ", userIdNotValid=" + userIdNotValid
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
	
}
