package org.yeastrc.xlink.www.objects;


/**
 * This is returned from any web service that doesn't return a value
 */
public class GenericWebserviceResult {

	private boolean status;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
