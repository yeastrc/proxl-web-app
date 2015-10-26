package org.yeastrc.xlink.www.objects;

public class MoveSearchesRequest {

	private Integer projectId;
	private Integer moveToProjectId;
	private int[] searchesToMoveToOtherProject;
	
	
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public Integer getMoveToProjectId() {
		return moveToProjectId;
	}
	public void setMoveToProjectId(Integer moveToProjectId) {
		this.moveToProjectId = moveToProjectId;
	}
	public int[] getSearchesToMoveToOtherProject() {
		return searchesToMoveToOtherProject;
	}
	public void setSearchesToMoveToOtherProject(int[] searchesToMoveToOtherProject) {
		this.searchesToMoveToOtherProject = searchesToMoveToOtherProject;
	}
}
