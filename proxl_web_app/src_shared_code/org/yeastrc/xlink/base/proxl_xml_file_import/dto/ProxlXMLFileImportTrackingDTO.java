package org.yeastrc.xlink.base.proxl_xml_file_import.dto;

import java.util.Date;

import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;

/**
 * 
 * table proxl_xml_file_import_tracking
 * 
 */
public class ProxlXMLFileImportTrackingDTO {


	private int id;
	private int projectId;
	private int priority;
	private int authUserId;
	private ProxlXMLFileImportStatus status;
	
	private String remoteUserIpAddress;
	private boolean markedForDeletion;
	private String searchName;
	private String searchPath;
	private String insertRequestURL;
	
	private Date recordInsertDateTime;
	private Date importStartDateTime;
	private Date importEndDateTime;
	private Date lastUpdatedDateTime;

	private Integer deletedByAuthUserId;
	private Date deletedDateTime;
	
	@Override
	public String toString() {
		return "ProxlXMLFileImportTrackingDTO [id=" + id + ", projectId="
				+ projectId + ", priority=" + priority + ", authUserId="
				+ authUserId + ", status=" + status + ", remoteUserIpAddress="
				+ remoteUserIpAddress + ", markedForDeletion="
				+ markedForDeletion + ", searchName=" + searchName
				+ ", searchPath=" + searchPath + ", insertRequestURL="
				+ insertRequestURL + ", recordInsertDateTime="
				+ recordInsertDateTime + ", importStartDateTime="
				+ importStartDateTime + ", importEndDateTime="
				+ importEndDateTime + ", lastUpdatedDateTime="
				+ lastUpdatedDateTime + ", deletedByAuthUserId="
				+ deletedByAuthUserId + ", deletedDateTime=" + deletedDateTime
				+ "]";
	}

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getInsertRequestURL() {
		return insertRequestURL;
	}
	public void setInsertRequestURL(String insertRequestURL) {
		this.insertRequestURL = insertRequestURL;
	}
	public Date getLastUpdatedDateTime() {
		return lastUpdatedDateTime;
	}
	public void setLastUpdatedDateTime(Date lastUpdatedDateTime) {
		this.lastUpdatedDateTime = lastUpdatedDateTime;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public int getAuthUserId() {
		return authUserId;
	}
	public void setAuthUserId(int authUserId) {
		this.authUserId = authUserId;
	}


	public ProxlXMLFileImportStatus getStatus() {
		return status;
	}
	public void setStatus(ProxlXMLFileImportStatus status) {
		this.status = status;
	}


	public String getSearchName() {
		return searchName;
	}


	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}


	public boolean isMarkedForDeletion() {
		return markedForDeletion;
	}


	public void setMarkedForDeletion(boolean markedForDeletion) {
		this.markedForDeletion = markedForDeletion;
	}
	public Integer getDeletedByAuthUserId() {
		return deletedByAuthUserId;
	}
	public void setDeletedByAuthUserId(Integer deletedByAuthUserId) {
		this.deletedByAuthUserId = deletedByAuthUserId;
	}
	public Date getDeletedDateTime() {
		return deletedDateTime;
	}
	public void setDeletedDateTime(Date deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}
	public String getRemoteUserIpAddress() {
		return remoteUserIpAddress;
	}
	public void setRemoteUserIpAddress(String remoteUserIpAddress) {
		this.remoteUserIpAddress = remoteUserIpAddress;
	}
	public Date getRecordInsertDateTime() {
		return recordInsertDateTime;
	}
	public void setRecordInsertDateTime(Date recordInsertDateTime) {
		this.recordInsertDateTime = recordInsertDateTime;
	}
	public Date getImportStartDateTime() {
		return importStartDateTime;
	}
	public void setImportStartDateTime(Date importStartDateTime) {
		this.importStartDateTime = importStartDateTime;
	}
	public Date getImportEndDateTime() {
		return importEndDateTime;
	}
	public void setImportEndDateTime(Date importEndDateTime) {
		this.importEndDateTime = importEndDateTime;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}



	public String getSearchPath() {
		return searchPath;
	}



	public void setSearchPath(String searchPath) {
		this.searchPath = searchPath;
	}

}
