package org.yeastrc.xlink.dto;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.xlink.base.spectrum.common.dto.Peak;


public class ScanDTO {
	
	private int id;
	private int scanFileId;

	private int level;
	
	private int startScanNumber;
	private int endScanNumber;
	
	private BigDecimal preMZ;
	
    private int precursorScanNum = -1;
    private int precursorScanId;

    private BigDecimal retentionTime;
    
    private int peakCount;
    private String fragmentationType;
    private String isCentroid;


	/**
	 * The data that gets put in the field spectrum_data
	 */
    private List<Peak> peakList;
    
	private String mzIntListAsString;

	

	public List<Peak> getPeakList() {
		return peakList;
	}
	public void setPeakList(List<Peak> peakList) {
		this.peakList = peakList;
	}


	public String getIsCentroid() {
		return isCentroid;
	}
	public void setIsCentroid(String isCentroid) {
		this.isCentroid = isCentroid;
	}
	public int getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(int scanFileId) {
		this.scanFileId = scanFileId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getStartScanNumber() {
		return startScanNumber;
	}
	public void setStartScanNumber(int startScanNumber) {
		this.startScanNumber = startScanNumber;
	}
	public int getEndScanNumber() {
		return endScanNumber;
	}
	public void setEndScanNumber(int endScanNumber) {
		this.endScanNumber = endScanNumber;
	}
	public BigDecimal getPreMZ() {
		return preMZ;
	}
	public void setPreMZ(BigDecimal preMZ) {
		this.preMZ = preMZ;
	}
//	public List<MzInt> getMzIntList() {
//		return mzIntList;
//	}
//	public void setMzIntList(List<MzInt> mzIntList) {
//		this.mzIntList = mzIntList;
//	}
	public String getMzIntListAsString() {
		return mzIntListAsString;
	}
	public void setMzIntListAsString(String mzIntListAsString) {
		this.mzIntListAsString = mzIntListAsString;
	}
	

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getPrecursorScanNum() {
		return precursorScanNum;
	}
	public void setPrecursorScanNum(int precursorScanNum) {
		this.precursorScanNum = precursorScanNum;
	}
	public int getPrecursorScanId() {
		return precursorScanId;
	}
	public void setPrecursorScanId(int precursorScanId) {
		this.precursorScanId = precursorScanId;
	}
	public BigDecimal getRetentionTime() {
		return retentionTime;
	}
	public void setRetentionTime(BigDecimal retentionTime) {
		this.retentionTime = retentionTime;
	}
	public String getFragmentationType() {
		return fragmentationType;
	}
	public void setFragmentationType(String fragmentationType) {
		this.fragmentationType = fragmentationType;
	}
	public int getPeakCount() {
		return peakCount;
	}
	public void setPeakCount(int peakCount) {
		this.peakCount = peakCount;
	}

}

//	CREATE TABLE scan (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  scan_file_id INT UNSIGNED NOT NULL,
//			  start_scan_number INT UNSIGNED NOT NULL,
//			  end_scan_number INT UNSIGNED NULL,
//			  level SMALLINT UNSIGNED NOT NULL,
//			  preMZ DECIMAL(18,9) NULL,
//			  precursor_scan_number INT NOT NULL,
//			  precursor_scan_id INT UNSIGNED NULL,
//			  retention_time DECIMAL(18,9) NULL,
//			  peak_count INT NOT NULL,
//			  fragmentation_type VARCHAR(45) NULL,
//			  is_centroid TINYINT(4) NULL DEFAULT NULL
