package org.yeastrc.xlink.dto;

import java.math.BigDecimal;

/**
 * Table psm
 *
 */
public class PsmDTO {
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getReportedPeptideId() {
		return reportedPeptideId;
	}
	public void setReportedPeptideId(int peptideId) {
		this.reportedPeptideId = peptideId;
	}
	public Integer getScanId() {
		return scanId;
	}
	public void setScanId(Integer scanId) {
		this.scanId = scanId;
	}
	public int getCharge() {
		return charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
	}
	public BigDecimal getLinkerMass() {
		return linkerMass;
	}
	public void setLinkerMass(BigDecimal linkerMass) {
		this.linkerMass = linkerMass;
	}
	public Integer getScanNumber() {
		return scanNumber;
	}
	public void setScanNumber(Integer scanNumber) {
		this.scanNumber = scanNumber;
	}
	public Integer getSearchScanFilenameId() {
		return searchScanFilenameId;
	}
	public void setSearchScanFilenameId(Integer searchScanFilenameId) {
		this.searchScanFilenameId = searchScanFilenameId;
	}
	public BigDecimal getPrecursor_RetentionTime() {
		return precursor_RetentionTime;
	}
	public void setPrecursor_RetentionTime(BigDecimal precursor_RetentionTime) {
		this.precursor_RetentionTime = precursor_RetentionTime;
	}
	public BigDecimal getPrecursor_MZ() {
		return precursor_MZ;
	}
	public void setPrecursor_MZ(BigDecimal precursor_MZ) {
		this.precursor_MZ = precursor_MZ;
	}



	
	private int id;
	private int searchId;
	private Integer scanId;
	private int reportedPeptideId;
	private int charge;
	private BigDecimal linkerMass;
	private Integer scanNumber;
	private Integer searchScanFilenameId;
	private BigDecimal precursor_RetentionTime; // precursor_retention_time
	private BigDecimal precursor_MZ;            // precursor_m_z

}
