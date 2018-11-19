package org.yeastrc.xlink.www.user_mgmt_webapp_access;

public class UserMgmtSessionKeyAliveWebserviceRequest {
	private String sessionKey;

	@Override
	public String toString() {
		return "UserMgmtSessionKeyAliveWebserviceRequest [sessionKey=" + sessionKey + "]";
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
}
