package org.yeastrc.xlink.www.objects;

/**
 * This is returned from the web service ResetPasswordService for Gen Email
 *
 */
public class ResetPasswordGenEmailResult {

	private boolean status = false;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
