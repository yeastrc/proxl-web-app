package org.yeastrc.xlink.www.objects;

/**
 * This is returned from the web service ResetPasswordService for Change Password
 *
 */
public class ResetPasswordChangePasswordResult {

	private boolean status = false;
	
	private boolean passwordTooLong = false;
	
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isPasswordTooLong() {
		return passwordTooLong;
	}
	public void setPasswordTooLong(boolean passwordTooLong) {
		this.passwordTooLong = passwordTooLong;
	}

}
