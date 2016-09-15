package org.yeastrc.xlink.www.dto;

import java.util.Date;

/**
 * table default_page_view_generic
 *
 */
public class DefaultPageViewGenericDTO {

	private int searchId;
	private String pageName;
	
	private int authUserIdCreated;
	private int authUserIdLastUpdated;
	
	private Date dateCreated;
	private Date dateLastUpdated;
	
	private String url;
	private String queryJSON;
	
	
	
	public String getQueryJSON() {
		return queryJSON;
	}
	public void setQueryJSON(String queryJSON) {
		this.queryJSON = queryJSON;
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
	public int getAuthUserIdCreated() {
		return authUserIdCreated;
	}
	public void setAuthUserIdCreated(int authUserIdCreated) {
		this.authUserIdCreated = authUserIdCreated;
	}
	public int getAuthUserIdLastUpdated() {
		return authUserIdLastUpdated;
	}
	public void setAuthUserIdLastUpdated(int authUserIdLastUpdated) {
		this.authUserIdLastUpdated = authUserIdLastUpdated;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Date getDateLastUpdated() {
		return dateLastUpdated;
	}
	public void setDateLastUpdated(Date dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}
	
	
}
