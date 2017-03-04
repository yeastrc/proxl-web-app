package org.yeastrc.xlink.www.objects;


/**
 * display a project in the header
 * 
 * Placed in request scope by GetPageHeaderData
 *
 */
public class ProjectTitleHeaderDisplay {

	private ProjectTblSubPartsForProjectLists projectTblData;
	
	/**
	 * Truncated as needed
	 */
	private String titleHeaderDisplay;

	/**
	 * Truncated as needed, for when no user.  Will be truncated Longer
	 */
	private String titleHeaderDisplayNonUser;
	
	
	public String getTitleHeaderDisplayNonUser() {
		return titleHeaderDisplayNonUser;
	}
	public void setTitleHeaderDisplayNonUser(String titleHeaderDisplayNonUser) {
		this.titleHeaderDisplayNonUser = titleHeaderDisplayNonUser;
	}
	public String getTitleHeaderDisplay() {
		return titleHeaderDisplay;
	}
	public void setTitleHeaderDisplay(String titleHeaderDisplay) {
		this.titleHeaderDisplay = titleHeaderDisplay;
	}
	public ProjectTblSubPartsForProjectLists getProjectTblData() {
		return projectTblData;
	}
	public void setProjectTblData(ProjectTblSubPartsForProjectLists projectTblData) {
		this.projectTblData = projectTblData;
	}

}
