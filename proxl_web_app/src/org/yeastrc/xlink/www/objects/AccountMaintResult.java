package org.yeastrc.xlink.www.objects;

/**
 * This is returned from the web service AccountMaintService
 *
 */
public class AccountMaintResult {

	private boolean status = false;
	
	private boolean valueAlreadyExists = false;

	public boolean isValueAlreadyExists() {
		return valueAlreadyExists;
	}

	public void setValueAlreadyExists(boolean valueAlreadyExists) {
		this.valueAlreadyExists = valueAlreadyExists;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
