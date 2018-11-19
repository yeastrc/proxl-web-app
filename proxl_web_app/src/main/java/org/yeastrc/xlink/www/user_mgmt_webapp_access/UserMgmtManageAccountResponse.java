package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtManageAccountResponse {

	private boolean success;
	
	private boolean sessionKeyNotValid;

	private boolean duplicateUsername = false;
	private boolean duplicateEmail = false;
	
	private String errorMessage;

	@Override
	public String toString() {
		return "UserMgmtManageAccountResponse [success=" + success + ", sessionKeyNotValid=" + sessionKeyNotValid
				+ ", duplicateUsername=" + duplicateUsername + ", duplicateEmail=" + duplicateEmail + ", errorMessage="
				+ errorMessage + "]";
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

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


}
