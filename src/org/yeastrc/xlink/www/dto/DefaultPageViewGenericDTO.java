package org.yeastrc.xlink.www.dto;

/**
 * table default_page_view_generic
 *
 */
public class DefaultPageViewGenericDTO {

	private int searchId;
	private String pageName;
	
	private int authUserId;
	private String url;
	private String queryJSON;
	
	
	
	public String getQueryJSON() {
		return queryJSON;
	}
	public void setQueryJSON(String queryJSON) {
		this.queryJSON = queryJSON;
	}
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
