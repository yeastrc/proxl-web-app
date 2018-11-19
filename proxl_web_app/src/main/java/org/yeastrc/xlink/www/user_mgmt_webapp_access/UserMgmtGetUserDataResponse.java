package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtGetUserDataResponse {

	private boolean success;
	
	private boolean sessionKeyNotValid;
	private boolean userIdNotFound;
	
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String organization;

	private boolean enabled;
	private boolean globalAdminUser;


	private String errorMessage;


	@Override
	public String toString() {
		return "UserMgmtGetUserDataResponse [success=" + success + ", sessionKeyNotValid=" + sessionKeyNotValid
				+ ", userIdNotFound=" + userIdNotFound + ", username=" + username + ", email=" + email + ", firstName="
				+ firstName + ", lastName=" + lastName + ", organization=" + organization + ", enabled=" + enabled
				+ ", globalAdminUser=" + globalAdminUser + ", errorMessage=" + errorMessage + "]";
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

	public boolean isUserIdNotFound() {
		return userIdNotFound;
	}

	public void setUserIdNotFound(boolean userIdNotFound) {
		this.userIdNotFound = userIdNotFound;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isGlobalAdminUser() {
		return globalAdminUser;
	}

	public void setGlobalAdminUser(boolean globalAdminUser) {
		this.globalAdminUser = globalAdminUser;
	}
}
