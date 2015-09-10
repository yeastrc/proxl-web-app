package org.yeastrc.xlink.dto;

import java.math.BigDecimal;


/**
 * table scan_retention_time
 *
 */
public class ScanRetentionTimeDTO {
	
	private int id;
	private int scanFileId;
	private int scanNumber;
	private int precursorScanNumber = -1;
	private int scanLevel;
	private BigDecimal retentionTime;
    
    
    
    public int getPrecursorScanNumber() {
		return precursorScanNumber;
	}
	public void setPrecursorScanNumber(int precursorScanNumber) {
		this.precursorScanNumber = precursorScanNumber;
	}
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
	public int getScanLevel() {
		return scanLevel;
	}
	public void setScanLevel(int scanLevel) {
		this.scanLevel = scanLevel;
	}
	public int getScanNumber() {
		return scanNumber;
	}
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	public BigDecimal getRetentionTime() {
		return retentionTime;
	}
	public void setRetentionTime(BigDecimal retentionTime) {
		this.retentionTime = retentionTime;
	}
    
}

//	CREATE TABLE scan_retention_time (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  scan_file_id INT UNSIGNED NOT NULL,
//			  scan_number INT NOT NULL,
//			  precursor_scan_number INT NULL,
//			  scan_level INT NOT NULL,
//			  retention_time DECIMAL(18,9) NOT NULL,
