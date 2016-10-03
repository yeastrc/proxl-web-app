package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication_objects;

import java.util.List;


/**
 * Root object of send to Upload Submit
 * 
 * Must match class in web app class ProxlXMLFileImportUploadSubmitService
 *
 */
public class UploadSubmitRequest {

	Integer projectId;
	String uploadKey;
	/**
	 * For submitting on same machine
	 */
	boolean submitterSameMachine;
	/**
	 * For submitting on same machine
	 */
	String submitterKey;
	
	String searchName;

	String searchPath;
	
	List<UploadSubmitRequestFileItem> fileItems;

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getUploadKey() {
		return uploadKey;
	}

	public void setUploadKey(String uploadKey) {
		this.uploadKey = uploadKey;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public List<UploadSubmitRequestFileItem> getFileItems() {
		return fileItems;
	}

	public void setFileItems(List<UploadSubmitRequestFileItem> fileItems) {
		this.fileItems = fileItems;
	}

	public String getSubmitterKey() {
		return submitterKey;
	}

	public void setSubmitterKey(String submitterKey) {
		this.submitterKey = submitterKey;
	}

	public boolean isSubmitterSameMachine() {
		return submitterSameMachine;
	}

	public void setSubmitterSameMachine(boolean submitterSameMachine) {
		this.submitterSameMachine = submitterSameMachine;
	}

	public String getSearchPath() {
		return searchPath;
	}

	public void setSearchPath(String searchPath) {
		this.searchPath = searchPath;
	}
}
