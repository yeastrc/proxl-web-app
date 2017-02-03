package org.yeastrc.xlink.dto;

/**
 * Table project_search
 * 
 * Shared between web app and Importer
 */
public class ProjectSearch_Shared_DTO {

	private int id;
	private int projectId;
	private int searchId;
	private int statusId;
	private String searchName;
	private int searchDisplayOrder;

	/**
	 * Default constructor
	 */
	public ProjectSearch_Shared_DTO() { }
	/**
	 * Copy constructor
	 * @param item
	 */
	public ProjectSearch_Shared_DTO( ProjectSearch_Shared_DTO item ) {
		super();
		this.id = item.id;
		this.projectId = item.projectId;
		this.searchId = item.searchId;
		this.statusId = item.statusId;
		this.searchName = item.searchName;
		this.searchDisplayOrder = item.searchDisplayOrder;
	}
	
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
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public int getSearchDisplayOrder() {
		return searchDisplayOrder;
	}
	public void setSearchDisplayOrder(int searchDisplayOrder) {
		this.searchDisplayOrder = searchDisplayOrder;
	}
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	
}
