package org.yeastrc.auth.dto;

/**
 * table auth_user record - specific to Proxl
 *
 * Includes other properties for data that used to be in the auth_user table
 */
public class AuthUserDTO {

	private int id;
	private int userMgmtUserId;

	/**
	 * Null if not set
	 */
	private Integer userAccessLevel;

	/**
	 * Default to true unless set to false
	 */
	private boolean enabledAppSpecific = true;
	
	
	
	//  These used to be in the auth_user table but now come from the User Mgmt Webapp
	
	private String username;
	private String email;

	/**
	 * Default to true unless set to false
	 */
	private boolean enabledUserMgmtGlobalLevel = true;
	
	

	public boolean isEnabledUserMgmtGlobalLevel() {
		return enabledUserMgmtGlobalLevel;
	}
	public void setEnabledUserMgmtGlobalLevel(boolean enabledUserMgmtGlobalLevel) {
		this.enabledUserMgmtGlobalLevel = enabledUserMgmtGlobalLevel;
	}
	public int getUserMgmtUserId() {
		return userMgmtUserId;
	}
	public void setUserMgmtUserId(int userMgmtUserId) {
		this.userMgmtUserId = userMgmtUserId;
	}
	public boolean isEnabledAppSpecific() {
		return enabledAppSpecific;
	}
	public void setEnabledAppSpecific(boolean enabledAppSpecific) {
		this.enabledAppSpecific = enabledAppSpecific;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getUserAccessLevel() {
		return userAccessLevel;
	}
	public void setUserAccessLevel(Integer userAccessLevel) {
		this.userAccessLevel = userAccessLevel;
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

}
