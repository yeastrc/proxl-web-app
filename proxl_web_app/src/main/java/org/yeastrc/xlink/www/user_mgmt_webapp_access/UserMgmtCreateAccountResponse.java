package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtCreateAccountResponse {

	private boolean success;
	
	private Integer createdUserId;

	private boolean duplicateUsername = false;
	private boolean duplicateEmail = false;
	
	private String errorMessage;

	@Override
	public String toString() {
		return "UserMgmtCreateAccountResponse [success=" + success + ", createdUserId=" + createdUserId
				+ ", duplicateUsername=" + duplicateUsername + ", duplicateEmail=" + duplicateEmail + ", errorMessage="
				+ errorMessage + "]";
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Integer getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(Integer createdUserId) {
		this.createdUserId = createdUserId;
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	
}
