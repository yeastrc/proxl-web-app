package org.yeastrc.auth.dto;

/**
 * table auth_user record - specific to Proxl
 *
 */
public class AuthUserDTO {

	private int id;
	private int userMgmtUserId;
	/**
	 * Null if not set
	 */
	private Integer userAccessLevel;
	
	/**
	 * Not always set
	 */
	private String lastLoginIP;
	
	/**
	 * Default to true unless set to false
	 */
	private boolean enabledAppSpecific = true;
	
	
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
	/**
	 * Not always set
	 * @return
	 */
	public String getLastLoginIP() {
		return lastLoginIP;
	}
	/**
	 * Not always set
	 * @param lastLoginIP
	 */
	public void setLastLoginIP(String lastLoginIP) {
		this.lastLoginIP = lastLoginIP;
	}

}
