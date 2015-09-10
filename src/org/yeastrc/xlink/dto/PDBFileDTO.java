package org.yeastrc.xlink.dto;

import org.joda.time.DateTime;

/**
 * Represents a PDB file as it was uploaded into the proxl database.
 * @author Michael Riffle
 *
 */
public class PDBFileDTO {

	private int id;
	private String name;
	private String description;
	private String content;
	private DateTime uploadDate;
	private int uploadedBy;
	private int projectId;
	private String visibility;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public DateTime getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(DateTime uploadDate) {
		this.uploadDate = uploadDate;
	}
	public int getUploadedBy() {
		return uploadedBy;
	}
	public void setUploadedBy(int uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	
	
}
