package org.yeastrc.xlink.www.dto;

/**
 * table project
 *
 */
public class ProjectDTO {

	private int id;
	private int authShareableObjectId;
	
	private String title;
	private String abstractText;
	
	private boolean enabled;
	private boolean markedForDeletion;
	
	private boolean projectLocked;
	
	private Integer publicAccessLevel;
	private boolean publicAccessLocked;
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectDTO other = (ProjectDTO) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAuthShareableObjectId() {
		return authShareableObjectId;
	}
	public void setAuthShareableObjectId(int authShareableObjectId) {
		this.authShareableObjectId = authShareableObjectId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbstractText() {
		return abstractText;
	}
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}
	
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isMarkedForDeletion() {
		return markedForDeletion;
	}
	public void setMarkedForDeletion(boolean markedForDeletion) {
		this.markedForDeletion = markedForDeletion;
	}
	

	
	public boolean isProjectLocked() {
		return projectLocked;
	}
	public void setProjectLocked(boolean projectLocked) {
		this.projectLocked = projectLocked;
	}
	public Integer getPublicAccessLevel() {
		return publicAccessLevel;
	}
	public void setPublicAccessLevel(Integer publicAccessLevel) {
		this.publicAccessLevel = publicAccessLevel;
	}
	public boolean isPublicAccessLocked() {
		return publicAccessLocked;
	}
	public void setPublicAccessLocked(boolean publicAccessLocked) {
		this.publicAccessLocked = publicAccessLocked;
	}
	
}

//CREATE TABLE project (
//		  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//		  auth_shareable_object_id INT UNSIGNED NOT NULL,
//		  title VARCHAR(255) NULL,
//		  abstract TEXT NULL,
//		  enabled TINYINT UNSIGNED NOT NULL DEFAULT 1,
//		  marked_for_deletion TINYINT UNSIGNED NOT NULL DEFAULT 0,
//		  project_locked TINYINT NOT NULL DEFAULT 0,
//		  public_access_level SMALLINT NULL,
//		  public_access_locked TINYINT NULL DEFAULT 0,
