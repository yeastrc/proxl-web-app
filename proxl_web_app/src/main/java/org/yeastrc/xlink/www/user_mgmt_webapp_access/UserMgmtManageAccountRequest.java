package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtManageAccountRequest {

	private Integer userMgmtUserId;
	/**
	 * Required by User Mgmt when the email or username changes
	 */
	private String currentPassword;
	
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String organization;
	
	private boolean assignOrganizationNull;
	

	@Override
	public String toString() {
		return "UserMgmtManageAccountRequest [userMgmtUserId=" + userMgmtUserId + ", currentPassword=NOT SHOWN, username=" + username + ", email=" + email
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", organization=" + organization
				+ ", assignOrganizationNull=" + assignOrganizationNull + "]";
	}


	public Integer getUserMgmtUserId() {
		return userMgmtUserId;
	}

	public void setUserMgmtUserId(Integer userMgmtUserId) {
		this.userMgmtUserId = userMgmtUserId;
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

	public boolean isAssignOrganizationNull() {
		return assignOrganizationNull;
	}

	public void setAssignOrganizationNull(boolean assignOrganizationNull) {
		this.assignOrganizationNull = assignOrganizationNull;
	}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
}
