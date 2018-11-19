package org.yeastrc.xlink.www.objects;

/**
 * table project
 * 
 * properties needed for project lists
 * 
 * equals and hashCode for Cache
 */
public class ProjectTblSubPartsForProjectLists {

	private int id;
	private String title;
	private boolean projectLocked;
	
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
		ProjectTblSubPartsForProjectLists other = (ProjectTblSubPartsForProjectLists) obj;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isProjectLocked() {
		return projectLocked;
	}
	public void setProjectLocked(boolean projectLocked) {
		this.projectLocked = projectLocked;
	}
}
