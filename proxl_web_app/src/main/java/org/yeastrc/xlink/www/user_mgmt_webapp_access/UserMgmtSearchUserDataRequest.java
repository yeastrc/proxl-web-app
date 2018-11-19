package org.yeastrc.xlink.www.user_mgmt_webapp_access;

/**
 * 
 *
 */
public class UserMgmtSearchUserDataRequest {

	private String sessionKey;
	private String searchString;
	/**
	 * if false, results start with searchString
	 */
	private boolean searchStringExactMatch;
	
	@Override
	public String toString() {
		return "UserMgmtSearchUserDataRequest [sessionKey=" + sessionKey + ", searchString=" + searchString
				+ ", searchStringExactMatch=" + searchStringExactMatch + "]";
	}
	public boolean isSearchStringExactMatch() {
		return searchStringExactMatch;
	}
	public void setSearchStringExactMatch(boolean searchStringExactMatch) {
		this.searchStringExactMatch = searchStringExactMatch;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
}