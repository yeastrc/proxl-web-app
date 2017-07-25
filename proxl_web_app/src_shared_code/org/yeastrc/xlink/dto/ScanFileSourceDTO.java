package org.yeastrc.xlink.dto;


/**
 * scan_file_source table entry
 *
 */
public class ScanFileSourceDTO {

	private int scanFileId;
	private String path;
	private String canonicalFilename_W_Path_OnSubmitMachine;
	private String absoluteFilename_W_Path_OnSubmitMachine;
	
	@Override
	public String toString() {
		return "ScanFileSourceDTO [scanFileId=" + scanFileId + ", path=" + path
				+ ", canonicalFilename_W_Path_OnSubmitMachine=" + canonicalFilename_W_Path_OnSubmitMachine
				+ ", absoluteFilename_W_Path_OnSubmitMachine=" + absoluteFilename_W_Path_OnSubmitMachine + "]";
	}

	
	public int getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(int scanFileId) {
		this.scanFileId = scanFileId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
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

