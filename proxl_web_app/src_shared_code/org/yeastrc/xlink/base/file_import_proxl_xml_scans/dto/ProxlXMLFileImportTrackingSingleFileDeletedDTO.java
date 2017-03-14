package org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto;

import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLImportSingleFileUploadStatus;

/**
 * table  file_import_proxl_xml_scans_tracking_single_file
 *
 */
public class ProxlXMLFileImportTrackingSingleFileDeletedDTO {

	private int id;

	private int proxlXmlFileImportTrackingId;
	
	private ProxlXMLFileImportFileType fileType;
	private ProxlXMLImportSingleFileUploadStatus fileUploadStatus; 
	
	private String filenameInUpload;
	private String filenameOnDisk;

	private String filenameOnDiskWithPathSubSameMachine;

	private String sha1Sum;
	private Long fileSize;
	
	@Override
	public String toString() {
		return "ProxlXMLFileImportTrackingSingleFileDeletedDTO [id=" + id
				+ ", proxlXmlFileImportTrackingId="
				+ proxlXmlFileImportTrackingId + ", fileType=" + fileType
				+ ", fileUploadStatus=" + fileUploadStatus
				+ ", filenameInUpload=" + filenameInUpload
				+ ", filenameOnDisk=" + filenameOnDisk
				+ ", filenameOnDiskWithPathSubSameMachine="
				+ filenameOnDiskWithPathSubSameMachine + ", sha1Sum=" + sha1Sum
				+ ", fileSize=" + fileSize + "]";
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProxlXmlFileImportTrackingId() {
		return proxlXmlFileImportTrackingId;
	}
	public void setProxlXmlFileImportTrackingId(int proxlXmlFileImportTrackingId) {
		this.proxlXmlFileImportTrackingId = proxlXmlFileImportTrackingId;
	}
	public String getFilenameInUpload() {
		return filenameInUpload;
	}
	public void setFilenameInUpload(String filenameInUpload) {
		this.filenameInUpload = filenameInUpload;
	}
	public String getFilenameOnDisk() {
		return filenameOnDisk;
	}
	public void setFilenameOnDisk(String filenameOnDisk) {
		this.filenameOnDisk = filenameOnDisk;
	}

	public ProxlXMLFileImportFileType getFileType() {
		return fileType;
	}

	public void setFileType(ProxlXMLFileImportFileType fileType) {
		this.fileType = fileType;
	}

	public ProxlXMLImportSingleFileUploadStatus getFileUploadStatus() {
		return fileUploadStatus;
	}

	public void setFileUploadStatus(
			ProxlXMLImportSingleFileUploadStatus fileUploadStatus) {
		this.fileUploadStatus = fileUploadStatus;
	}

	public String getSha1Sum() {
		return sha1Sum;
	}

	public void setSha1Sum(String sha1Sum) {
		this.sha1Sum = sha1Sum;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFilenameOnDiskWithPathSubSameMachine() {
		return filenameOnDiskWithPathSubSameMachine;
	}
	public void setFilenameOnDiskWithPathSubSameMachine(
			String filenameOnDiskWithPathSubSameMachine) {
		this.filenameOnDiskWithPathSubSameMachine = filenameOnDiskWithPathSubSameMachine;
	}


}
