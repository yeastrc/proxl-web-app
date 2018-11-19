package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication_objects;

/**
 * Sub object of send to Upload Submit
 * 
 * 
 * Must match class in web app class ProxlXMLFileImportUploadSubmitService
 *
 */
public class UploadSubmitRequestFileItem {


	private String uploadedFilename;
	private Integer fileType;
	private Integer fileIndex;
	private Boolean isProxlXMLFile;
	

	//  Following are only for submitting on same machine
	
	private String filenameOnDiskWithPathSubSameMachine;
	
	
	public String getUploadedFilename() {
		return uploadedFilename;
	}
	public void setUploadedFilename(String uploadedFilename) {
		this.uploadedFilename = uploadedFilename;
	}
	public Integer getFileType() {
		return fileType;
	}
	public void setFileType(Integer fileType) {
		this.fileType = fileType;
	}
	public Integer getFileIndex() {
		return fileIndex;
	}
	public void setFileIndex(Integer fileIndex) {
		this.fileIndex = fileIndex;
	}
	public Boolean getIsProxlXMLFile() {
		return isProxlXMLFile;
	}
	public void setIsProxlXMLFile(Boolean isProxlXMLFile) {
		this.isProxlXMLFile = isProxlXMLFile;
	}
	public String getFilenameOnDiskWithPathSubSameMachine() {
		return filenameOnDiskWithPathSubSameMachine;
	}
	public void setFilenameOnDiskWithPathSubSameMachine(
			String filenameOnDiskWithPathSubSameMachine) {
		this.filenameOnDiskWithPathSubSameMachine = filenameOnDiskWithPathSubSameMachine;
	}

}
