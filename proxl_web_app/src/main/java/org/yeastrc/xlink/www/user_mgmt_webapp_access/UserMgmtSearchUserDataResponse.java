package org.yeastrc.xlink.www.user_mgmt_webapp_access;

import java.util.List;

/**
 * 
 *
 */
public class UserMgmtSearchUserDataResponse {

	private boolean success;
	
	private List<Integer> userIdList;

	private String errorMessage;

	@Override
	public String toString() {
		return "UserMgmtSearchUserDataResponse [success=" + success
				+ ", userIdList=" + userIdList + ", errorMessage=" + errorMessage + "]";
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<Integer> getUserIdList() {
		return userIdList;
	}

	public void setUserIdList(List<Integer> userIdList) {
		this.userIdList = userIdList;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	
}
