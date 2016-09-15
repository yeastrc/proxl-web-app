package org.yeastrc.proxl.import_xml_to_db.dto;

import java.math.BigDecimal;

/**
 * Table psm
 *
 */
public class PsmDTO {
	

	@Override
	public String toString() {
		return "PsmDTO [id=" + id + ", searchId=" + searchId + ", scanId="
				+ scanId + ", reportedPeptideId=" + reportedPeptideId
				+ ", charge=" + charge + ", linkerMass=" + linkerMass
				+ ", scanNumber=" + scanNumber + ", searchScanFilenameId="
				+ searchScanFilenameId + "]";
	}
	
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
	public Integer getCharge() {
		return charge;
	}
	public void setCharge(Integer charge) {
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
	


	
	private int id;
	private int searchId;
	private Integer scanId;
	private int reportedPeptideId;
	private Integer charge;
	private BigDecimal linkerMass;
	private Integer scanNumber;
	private Integer searchScanFilenameId;

}
