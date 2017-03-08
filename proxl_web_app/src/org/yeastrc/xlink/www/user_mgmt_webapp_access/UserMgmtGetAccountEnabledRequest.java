package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtGetAccountEnabledRequest {

	private int userId;
	private String sessionKey;
	
	@Override
	public String toString() {
		return "UserMgmtGetAccountEnabledRequest [userId=" + userId + ", sessionKey=" + sessionKey + "]";
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

}
