package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtCreateAccountRequest {

	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String organization;
	
	private String password;
	
	private String userRemoteIP;
	
	@Override
	public String toString() {
		return "UserMgmtCreateAccountRequest [username=" + username + ", email=" + email + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", organization=" + organization
				 + ", userRemoteIP=" + userRemoteIP + ", password=NOT SHOWN" + "]";
	}

	public String getUserRemoteIP() {
		return userRemoteIP;
	}

	public void setUserRemoteIP(String userRemoteIP) {
		this.userRemoteIP = userRemoteIP;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
