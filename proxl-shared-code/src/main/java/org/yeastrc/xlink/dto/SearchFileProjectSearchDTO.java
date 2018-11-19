package org.yeastrc.xlink.dto;

/**
 * search_file__project_search table
 */
public class SearchFileProjectSearchDTO {

	private int id;
	private int projectSearchId;
	private int searchFileId;
	private String displayFilename;

	@Override
	public String toString() {
		return "SearchFileProjectSearchDTO [id=" + id + ", projectSearchId=" + projectSearchId + ", searchFileId="
				+ searchFileId + ", displayFilename=" + displayFilename + "]";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
	public int getSearchFileId() {
		return searchFileId;
	}
	public void setSearchFileId(int searchFileId) {
		this.searchFileId = searchFileId;
	}
	public String getDisplayFilename() {
		return displayFilename;
	}
	public void setDisplayFilename(String displayFilename) {
		this.displayFilename = displayFilename;
	}

		
}

