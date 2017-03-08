package org.yeastrc.xlink.www.user_mgmt_webapp_access;

import java.util.List;

/**
 * 
 *
 */
public class UserMgmtSearchUserDataResponse {

	private boolean success;
	private boolean sessionKeyNotValid;
	
	private List<Integer> userIdList;

	private String errorMessage;

	@Override
	public String toString() {
		return "UserMgmtSearchUserDataResponse [success=" + success + ", sessionKeyNotValid=" + sessionKeyNotValid
				+ ", userIdList=" + userIdList + ", errorMessage=" + errorMessage + "]";
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isSessionKeyNotValid() {
		return sessionKeyNotValid;
	}

	public void setSessionKeyNotValid(boolean sessionKeyNotValid) {
		this.sessionKeyNotValid = sessionKeyNotValid;
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
