package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.www.dto.ProjectDTO;

/**
 * display a project in the header
 * 
 * Placed in request scope by GetPageHeaderData
 *
 */
public class ProjectTitleAbstractHeaderDisplay {

	private ProjectDTO projectDTO;
	
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
	public ProjectDTO getProjectDTO() {
		return projectDTO;
	}
	public void setProjectDTO(ProjectDTO projectDTO) {
		this.projectDTO = projectDTO;
	}
	public String getTitleHeaderDisplay() {
		return titleHeaderDisplay;
	}
	public void setTitleHeaderDisplay(String titleHeaderDisplay) {
		this.titleHeaderDisplay = titleHeaderDisplay;
	}

}
