package org.yeastrc.xlink.www.dto;

import org.yeastrc.auth.dto.AuthUserDTO;

/**
 * table xl_user
 *
 */
public class XLinkUserDTO {

	/**
	 * From project Auth_Library
	 */
	private AuthUserDTO authUser;
	
	private String firstName;
	private String lastName;
	private String organization;
	
	
	/**
	 * From project Auth_Library
	 * @return
	 */
	public AuthUserDTO getAuthUser() {
		return authUser;
	}
	/**
	 * From project Auth_Library
	 * @param authUser
	 */
	public void setAuthUser(AuthUserDTO authUser) {
		this.authUser = authUser;
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
	
	
	
	
}

//CREATE TABLE IF NOT EXISTS crosslinks.xl_user (
//		  auth_user_id INT UNSIGNED NOT NULL,
//		  first_name VARCHAR(255) NOT NULL,
//		  last_name VARCHAR(255) NOT NULL,
//		  Organization VARCHAR(2000) NULL,
