package org.yeastrc.xlink.dto;


/**
 * scan_file_header table entry
 *
 */
public class ScanFileHeaderDTO {

	private int id;
	private int scanFileId;
	private String header;
	private String value;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(int scanFileId) {
		this.scanFileId = scanFileId;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

//id INT UNSIGNED NOT NULL AUTO_INCREMENT ,
//ms2_file_id INT UNSIGNED NOT NULL ,
//header VARCHAR(255) NOT NULL ,
//value TEXT NULL ,
