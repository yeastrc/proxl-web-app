package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtGetAccountEnabledRequest {

	private int userId;
	
	@Override
	public String toString() {
		return "UserMgmtGetAccountEnabledRequest [userId=" + userId + "]";
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

}
