package org.yeastrc.xlink.dto;

/**
 * Table psm
 *
 */
public class PsmDTO {
	

	
	@Override
	public String toString() {
		return "PsmDTO [id=" + id + ", searchId=" + searchId + ", scanId="
				+ scanId + ", type=" + type + ", reportedPeptideId="
				+ reportedPeptideId + ", charge=" + charge + "]";
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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


	
	private int id;
	private int searchId;
	private Integer scanId;
	private int type;
	private int reportedPeptideId;
	private Integer charge;

}
