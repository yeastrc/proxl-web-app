package org.yeastrc.xlink.www.objects;

/**
 * This is returned from the web service 
 *
 */
public class CreateAccountResult {

	private boolean status = false;
	
	private boolean duplicateUsername = false;
	private boolean duplicateEmail = false;
	
	private boolean userTestValidated = false;

	private String errorMessage;
	
	public boolean isDuplicateUsername() {
		return duplicateUsername;
	}

	public void setDuplicateUsername(boolean duplicateUsername) {
		this.duplicateUsername = duplicateUsername;
	}

	public boolean isDuplicateEmail() {
		return duplicateEmail;
	}

	public void setDuplicateEmail(boolean duplicateEmail) {
		this.duplicateEmail = duplicateEmail;
	}

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

	public boolean isUserTestValidated() {
		return userTestValidated;
	}

	public void setUserTestValidated(boolean userTestValidated) {
		this.userTestValidated = userTestValidated;
	}

}
