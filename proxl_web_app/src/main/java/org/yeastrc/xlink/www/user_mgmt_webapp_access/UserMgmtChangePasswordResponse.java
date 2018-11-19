package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtChangePasswordResponse {
	
	private boolean success;
	
	private boolean sessionKeyNotValid;
	private boolean oldPasswordNotValid;

	private String errorMessage;
	

	@Override
	public String toString() {
		return "UserMgmtChangePasswordResponse [success=" + success + ", sessionKeyNotValid=" + sessionKeyNotValid
				+ ", oldPasswordNotValid=" + oldPasswordNotValid + ", errorMessage=" + errorMessage + "]";
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
}
