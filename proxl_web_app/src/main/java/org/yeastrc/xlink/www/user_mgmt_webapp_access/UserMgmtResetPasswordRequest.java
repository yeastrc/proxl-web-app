package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtResetPasswordRequest {

	private int userMgmtUserId;
	private String newPassword;
	private String userRemoteIP;
	

	@Override
	public String toString() {
		return "UserMgmtChangePasswordRequest [userMgmtUserId=" + userMgmtUserId
				+ ", userRemoteIP=" + userRemoteIP
				+ ", newPassword=NOT SHOWN]";
	}
	public String getUserRemoteIP() {
		return userRemoteIP;
	}
	public void setUserRemoteIP(String userRemoteIP) {
		this.userRemoteIP = userRemoteIP;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public int getUserMgmtUserId() {
		return userMgmtUserId;
	}
	public void setUserMgmtUserId(int userMgmtUserId) {
		this.userMgmtUserId = userMgmtUserId;
	}

}
