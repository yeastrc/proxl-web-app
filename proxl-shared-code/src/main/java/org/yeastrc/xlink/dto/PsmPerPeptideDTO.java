package org.yeastrc.xlink.dto;

import java.math.BigDecimal;

/**
 * Table psm_per_peptide
 *
 */
public class PsmPerPeptideDTO {
	
	private int id;
	private int psmId;
	private int srchRepPeptPeptideId;
	private Integer scanId;
	private Integer charge;
	private BigDecimal linkerMass;
	private Integer scanNumber;
	private Integer searchScanFilenameId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPsmId() {
		return psmId;
	}
	public void setPsmId(int psmId) {
		this.psmId = psmId;
	}
	public int getSrchRepPeptPeptideId() {
		return srchRepPeptPeptideId;
	}
	public void setSrchRepPeptPeptideId(int srchRepPeptPeptideId) {
		this.srchRepPeptPeptideId = srchRepPeptPeptideId;
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

}
