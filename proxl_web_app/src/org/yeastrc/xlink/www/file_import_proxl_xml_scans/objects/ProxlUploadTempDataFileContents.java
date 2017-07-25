package org.yeastrc.xlink.www.file_import_proxl_xml_scans.objects;

import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportFileType;

/**
 * Contents of file ProxlXMLFileUploadWebConstants.UPLOAD_FILE_DATA_FILE_PREFIX
 *
 */
@XmlRootElement(name="ProxlUploadTempDataFileContents")
public class ProxlUploadTempDataFileContents {

	private String uploadedFilename;
	private String savedToDiskFilename;

	private String canonicalFilename_W_Path_OnSubmitMachine;
	private String absoluteFilename_W_Path_OnSubmitMachine;
	
	private ProxlXMLFileImportFileType fileType;
	private int fileIndex;
	
	private String searchNameInProxlXMLFile;
	
	
	public String getUploadedFilename() {
		return uploadedFilename;
	}
	public void setUploadedFilename(String uploadedFilename) {
		this.uploadedFilename = uploadedFilename;
	}
	public String getSavedToDiskFilename() {
		return savedToDiskFilename;
	}
	public void setSavedToDiskFilename(String savedToDiskFilename) {
		this.savedToDiskFilename = savedToDiskFilename;
	}
	public ProxlXMLFileImportFileType getFileType() {
		return fileType;
	}
	public void setFileType(ProxlXMLFileImportFileType fileType) {
		this.fileType = fileType;
	}
	public int getFileIndex() {
		return fileIndex;
	}
	public void setFileIndex(int fileIndex) {
		this.fileIndex = fileIndex;
	}
	public String getSearchNameInProxlXMLFile() {
		return searchNameInProxlXMLFile;
	}
	public void setSearchNameInProxlXMLFile(String searchNameInProxlXMLFile) {
		this.searchNameInProxlXMLFile = searchNameInProxlXMLFile;
	}
	public String getCanonicalFilename_W_Path_OnSubmitMachine() {
		return canonicalFilename_W_Path_OnSubmitMachine;
	}
	public void setCanonicalFilename_W_Path_OnSubmitMachine(String canonicalFilename_W_Path_OnSubmitMachine) {
		this.canonicalFilename_W_Path_OnSubmitMachine = canonicalFilename_W_Path_OnSubmitMachine;
	}
	public String getAbsoluteFilename_W_Path_OnSubmitMachine() {
		return absoluteFilename_W_Path_OnSubmitMachine;
	}
	public void setAbsoluteFilename_W_Path_OnSubmitMachine(String absoluteFilename_W_Path_OnSubmitMachine) {
		this.absoluteFilename_W_Path_OnSubmitMachine = absoluteFilename_W_Path_OnSubmitMachine;
	}

}
