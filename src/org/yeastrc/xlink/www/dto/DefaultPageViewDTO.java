package org.yeastrc.xlink.www.dto;

/**
 * table default_page_view
 *
 */
public class DefaultPageViewDTO {

	private int searchId;
	private String pageName;
	
	private int authUserId;
	private String url;
	
	
	public int getAuthUserId() {
		return authUserId;
	}
	public void setAuthUserId(int authUserId) {
		this.authUserId = authUserId;
	}

	

	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}

//CREATE TABLE default_page_view (
//search_id INT UNSIGNED NOT NULL,
//page_name VARCHAR(80) NOT NULL,
//auth_user_id INT UNSIGNED NOT NULL,
//url VARCHAR(6000) NOT NULL,

