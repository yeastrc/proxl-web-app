package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.www.dto.ProjectDTO;

public class ProjectWithUserAccessLevel {

	private ProjectDTO project;
	private boolean canDelete;
	
	
	public ProjectDTO getProject() {
		return project;
	}
	public void setProject(ProjectDTO project) {
		this.project = project;
	}
	public boolean isCanDelete() {
		return canDelete;
	}
	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
}
