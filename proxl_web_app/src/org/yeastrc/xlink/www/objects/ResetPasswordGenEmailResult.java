package org.yeastrc.xlink.www.objects;

/**
 * This is returned from the web service ResetPasswordService for Gen Email
 *
 */
public class ResetPasswordGenEmailResult {

	private boolean status = false;
	
	private boolean invalidUsernameOrEmail = false;

	public boolean isInvalidUsernameOrEmail() {
		return invalidUsernameOrEmail;
	}

	public void setInvalidUsernameOrEmail(boolean invalidUsernameOrEmail) {
		this.invalidUsernameOrEmail = invalidUsernameOrEmail;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
