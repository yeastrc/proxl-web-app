package org.yeastrc.xlink.www.objects;

/**
 * This is returned from the web service LoginService
 *
 */
public class LoginResult {

	private boolean status = false;
	
	private boolean invalidUserOrPassword = false;
	private boolean disabledUser = false;

	public boolean isInvalidUserOrPassword() {
		return invalidUserOrPassword;
	}

	public void setInvalidUserOrPassword(boolean invalidUserOrPassword) {
		this.invalidUserOrPassword = invalidUserOrPassword;
	}

	public boolean isDisabledUser() {
		return disabledUser;
	}

	public void setDisabledUser(boolean disabledUser) {
		this.disabledUser = disabledUser;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
