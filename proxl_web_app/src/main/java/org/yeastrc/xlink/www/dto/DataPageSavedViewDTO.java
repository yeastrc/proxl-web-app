package org.yeastrc.xlink.www.dto;

import java.util.Date;

/**
 * table data_page_saved_view_tbl
 *
 */
public class DataPageSavedViewDTO {
	
	private int id;
	private int projectId;
	private String pageName;
	
	private String label;
	private String urlStartAtPageName;
	private String pageQueryJSONString;

	private int authUserIdCreated;
	private int authUserIdLastUpdated;
	
	private Date dateCreated;
	private Date dateLastUpdated;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUrlStartAtPageName() {
		return urlStartAtPageName;
	}
	public void setUrlStartAtPageName(String urlStartAtPageName) {
		this.urlStartAtPageName = urlStartAtPageName;
	}
	public String getPageQueryJSONString() {
		return pageQueryJSONString;
	}
	public void setPageQueryJSONString(String pageQueryJSONString) {
		this.pageQueryJSONString = pageQueryJSONString;
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
