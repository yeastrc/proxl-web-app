package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtChangePasswordRequest {

	private String sessionKey;
	
	private String oldPassword;
	private String newPassword;
	private String userRemoteIP;
	

	@Override
	public String toString() {
		return "UserMgmtChangePasswordRequest [sessionKey=" + sessionKey 
				+ ", userRemoteIP=" + userRemoteIP
				+ ", oldPassword=NOT SHOWN"
				+ ", newPassword=NOT SHOWN]";
	}
	public String getUserRemoteIP() {
		return userRemoteIP;
	}
	public void setUserRemoteIP(String userRemoteIP) {
		this.userRemoteIP = userRemoteIP;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	

}
