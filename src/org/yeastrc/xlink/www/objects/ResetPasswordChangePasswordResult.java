package org.yeastrc.xlink.www.objects;

/**
 * This is returned from the web service ResetPasswordService for Change Password
 *
 */
public class ResetPasswordChangePasswordResult {

	private boolean status = false;
	
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

}
