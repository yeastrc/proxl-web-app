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
	private int authUserId;
	private String hashIdentifier;
	private ProxlXMLFileImportStatus status;
	private String remoteUserIpAddress;
	private boolean markedForDeletion;
	private String searchName;
	private String insertRequestURL;
	
	private Date recordInsertDateTime;
	private Date uploadDateTime;
	private Date lastUpdatedDateTime;

	private Integer deletedByAuthUserId;
	private Date deletedDateTime;
	
	@Override
	public String toString() {
		return "ProxlXMLFileImportTrackingDTO [id=" + id + ", projectId="
				+ projectId + ", authUserId=" + authUserId
				+ ", hashIdentifier=" + hashIdentifier + ", status=" + status
				+ ", remoteUserIpAddress=" + remoteUserIpAddress
				+ ", markedForDeletion=" + markedForDeletion + ", searchName="
				+ searchName + ", insertRequestURL=" + insertRequestURL
				+ ", recordInsertDateTime=" + recordInsertDateTime
				+ ", uploadDateTime=" + uploadDateTime
				+ ", lastUpdatedDateTime=" + lastUpdatedDateTime
				+ ", deletedByAuthUserId=" + deletedByAuthUserId
				+ ", deletedDateTime=" + deletedDateTime + "]";
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
	public Date getUploadDateTime() {
		return uploadDateTime;
	}
	public void setUploadDateTime(Date uploadDateTime) {
		this.uploadDateTime = uploadDateTime;
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
	public String getHashIdentifier() {
		return hashIdentifier;
	}
	public void setHashIdentifier(String hashIdentifier) {
		this.hashIdentifier = hashIdentifier;
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



}
