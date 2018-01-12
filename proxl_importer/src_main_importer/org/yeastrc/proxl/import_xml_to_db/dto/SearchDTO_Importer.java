package org.yeastrc.proxl.import_xml_to_db.dto;

import org.joda.time.DateTime;

/**
 * Table search
 *
 */
public class SearchDTO_Importer { 
	
	private int id;
	private String path;
	private DateTime load_time;
	private String fastaFilename;
	private String directoryName;
	private boolean hasScanData;
	private boolean hasIsotopeLabel;
	private Integer createdByUserId;

	//  Setters and Getters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDirectoryName() {
		return directoryName;
	}
	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}
	public boolean isHasScanData() {
		return hasScanData;
	}
	public void setHasScanData(boolean hasScanData) {
		this.hasScanData = hasScanData;
	}
	public Integer getCreatedByUserId() {
		return createdByUserId;
	}
	public void setCreatedByUserId(Integer createdByUserId) {
		this.createdByUserId = createdByUserId;
	}
	public boolean isHasIsotopeLabel() {
		return hasIsotopeLabel;
	}
	public void setHasIsotopeLabel(boolean hasIsotopeLabel) {
		this.hasIsotopeLabel = hasIsotopeLabel;
	}

}
