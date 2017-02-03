package org.yeastrc.proxl.import_xml_to_db.dto;

/**
 * Table project_search
 * 
 * Not all fields
 */
public class ProjectSearchDTO {

	private int id;
	private int projectId;
	private int searchId;
	private String searchName;
	
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
