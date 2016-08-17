package org.yeastrc.xlink.www.proxl_xml_file_import.display_objects;

import java.util.List;

import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;


/**
 * 
 *
 */
public class ProxlXMLFileImportTrackingDisplay {
	
	private int trackingId;

	private ProxlXMLFileImportStatus statusEnum;
	
	private String status;
	
	
	
	/**
	 * Optional item on XSD
	 */
	private String searchName;
	
	private String uploadedFilename;
	
	private String nameOfUploadUser;
	
	private String uploadDateTime;
	private String lastUpdatedDateTime;
	
	private List<String> scanFilenames;
	
	public boolean isStatusQueued() {
		return statusEnum == ProxlXMLFileImportStatus.QUEUED;
	}
	public boolean isStatusReQueued() {
		return statusEnum == ProxlXMLFileImportStatus.RE_QUEUED;
	}
	public boolean isStatusStarted() {
		return statusEnum == ProxlXMLFileImportStatus.STARTED;
	}
	public boolean isStatusComplete() {
		return statusEnum == ProxlXMLFileImportStatus.COMPLETE;
	}
	public boolean isStatusFailed() {
		return statusEnum == ProxlXMLFileImportStatus.FAILED;
	}
	
	public int getStatusId() {
		
		return statusEnum.value();
	}

	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUploadDateTime() {
		return uploadDateTime;
	}
	public void setUploadDateTime(String uploadDateTime) {
		this.uploadDateTime = uploadDateTime;
	}
	public String getLastUpdatedDateTime() {
		return lastUpdatedDateTime;
	}
	public void setLastUpdatedDateTime(String lastUpdatedDateTime) {
		this.lastUpdatedDateTime = lastUpdatedDateTime;
	}
	public String getUploadedFilename() {
		return uploadedFilename;
	}
	public void setUploadedFilename(String uploadedFilename) {
		this.uploadedFilename = uploadedFilename;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public ProxlXMLFileImportStatus getStatusEnum() {
		return statusEnum;
	}
	public void setStatusEnum(ProxlXMLFileImportStatus statusEnum) {
		this.statusEnum = statusEnum;
	}
	public int getTrackingId() {
		return trackingId;
	}
	public void setTrackingId(int trackingId) {
		this.trackingId = trackingId;
	}
	public String getNameOfUploadUser() {
		return nameOfUploadUser;
	}
	public void setNameOfUploadUser(String nameOfUploadUser) {
		this.nameOfUploadUser = nameOfUploadUser;
	}
	public List<String> getScanFilenames() {
		return scanFilenames;
	}
	public void setScanFilenames(List<String> scanFilenames) {
		this.scanFilenames = scanFilenames;
	}

}
