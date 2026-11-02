package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtManageAccountResponse {

	private boolean success;
	private boolean userIdNotFound;
	private boolean userDisabled;
	private boolean currentPasswordRequired;
	private boolean currentPasswordInvalid;
	

	private boolean duplicateUsername = false;
	private boolean duplicateEmail = false;
	
	private String errorMessage;

	@Override
	public String toString() {
		return "UserMgmtManageAccountResponse [success=" + success + ", userIdNotFound=" + userIdNotFound + ", userDisabled=" + userDisabled
				+ ", currentPasswordRequired=" + currentPasswordRequired + ", currentPasswordInvalid=" + currentPasswordInvalid
				+ ", duplicateUsername=" + duplicateUsername + ", duplicateEmail=" + duplicateEmail + ", errorMessage="
				+ errorMessage + "]";
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
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

	public boolean isUserIdNotFound() {
		return userIdNotFound;
	}
	public void setUserIdNotFound(boolean userIdNotFound) {
		this.userIdNotFound = userIdNotFound;
	}
	public boolean isUserDisabled() {
		return userDisabled;
	}
	public void setUserDisabled(boolean userDisabled) {
		this.userDisabled = userDisabled;
	}
	public boolean isCurrentPasswordRequired() {
		return currentPasswordRequired;
	}
	public void setCurrentPasswordRequired(boolean currentPasswordRequired) {
		this.currentPasswordRequired = currentPasswordRequired;
	}
	public boolean isCurrentPasswordInvalid() {
		return currentPasswordInvalid;
	}
	public void setCurrentPasswordInvalid(boolean currentPasswordInvalid) {
		this.currentPasswordInvalid = currentPasswordInvalid;
	}
}
