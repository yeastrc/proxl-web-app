package org.yeastrc.xlink.www.dto;

/**
 * project_level_default_fltr_min_psms_tbl table
 *
 */
public class ProjectLevelDefaultFltr_MinPSMs_DTO {


	private int id;
	private int projectId;
	private int minPSMs;
	private int createdAuthUserId;
	private int lastUpdatedAuthUserId;
	
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
	public int getMinPSMs() {
		return minPSMs;
	}
	public void setMinPSMs(int minPSMs) {
		this.minPSMs = minPSMs;
	}
	public int getCreatedAuthUserId() {
		return createdAuthUserId;
	}
	public void setCreatedAuthUserId(int createdAuthUserId) {
		this.createdAuthUserId = createdAuthUserId;
	}
	public int getLastUpdatedAuthUserId() {
		return lastUpdatedAuthUserId;
	}
	public void setLastUpdatedAuthUserId(int lastUpdatedAuthUserId) {
		this.lastUpdatedAuthUserId = lastUpdatedAuthUserId;
	}
	
}
