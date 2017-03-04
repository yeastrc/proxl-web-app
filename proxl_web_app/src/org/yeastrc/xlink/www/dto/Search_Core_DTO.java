package org.yeastrc.xlink.www.dto;

import org.joda.time.DateTime;

/**
 * Table search - Just raw data
 *
 */
public class Search_Core_DTO  {

	private int projectSearchId;
	private int searchId;
	private String path;
	private DateTime load_time;
	private String fastaFilename;
	private String name;
	private int projectId;
	private String directoryName;
	private int displayOrder;
	private boolean hasScanData;
	
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public DateTime getLoad_time() {
		return load_time;
	}
	public void setLoad_time(DateTime load_time) {
		this.load_time = load_time;
	}
	public String getFastaFilename() {
		return fastaFilename;
	}
	public void setFastaFilename(String fastaFilename) {
		this.fastaFilename = fastaFilename;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getDirectoryName() {
		return directoryName;
	}
	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}
	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	public boolean isHasScanData() {
		return hasScanData;
	}
	public void setHasScanData(boolean hasScanData) {
		this.hasScanData = hasScanData;
	}

}
