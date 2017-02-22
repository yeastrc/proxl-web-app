package org.yeastrc.xlink.www.dto;

/**
 * folder_project_search table
 *
 */
public class FolderProjectSearchDTO {
	
	private int projectSearchId; // primary key
	private int folderId;
	
	public int getFolderId() {
		return folderId;
	}
	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}

}
