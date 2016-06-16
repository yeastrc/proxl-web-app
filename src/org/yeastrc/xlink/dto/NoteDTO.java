package org.yeastrc.xlink.dto;

import java.util.Date;

/**
 * table note
 *
 */
public class NoteDTO {


	private int id;
	private int projectId;
	private int authUserIdCreated;
	private Date createdDateTime;
	private int authUserIdLastUpdated ;
	private Date lastUpdatedDateTime;
	private String noteText;
	
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
		NoteDTO other = (NoteDTO) obj;
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
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public int getAuthUserIdCreated() {
		return authUserIdCreated;
	}
	public void setAuthUserIdCreated(int authUserIdCreated) {
		this.authUserIdCreated = authUserIdCreated;
	}
	public Date getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public int getAuthUserIdLastUpdated() {
		return authUserIdLastUpdated;
	}
	public void setAuthUserIdLastUpdated(int authUserIdLastUpdated) {
		this.authUserIdLastUpdated = authUserIdLastUpdated;
	}
	public Date getLastUpdatedDateTime() {
		return lastUpdatedDateTime;
	}
	public void setLastUpdatedDateTime(Date lastUpdatedDateTime) {
		this.lastUpdatedDateTime = lastUpdatedDateTime;
	}
	public String getNoteText() {
		return noteText;
	}
	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}
	
}
