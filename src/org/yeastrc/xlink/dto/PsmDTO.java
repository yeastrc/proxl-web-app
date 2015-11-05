package org.yeastrc.xlink.dto;

/*
CREATE TABLE psm (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    search_id INT UNSIGNED NOT NULL,
    q_value DOUBLE,
    type ENUM('loop','crosslink', 'none' ) NOT NULL,
    peptide_id INT UNSIGNED NOT NULL,
    charge SMALLINT NULL
);
 */

public class PsmDTO {
	
	public String toString() {
		String str = ""
		
		+ "PSM:\n"
		+ "\tid: " + this.getId()
	    + "\tperc search id: " + this.getSearchId()
	    + "\tscan id: " + this.getScanId()
		+ "\tq value: " + this.getqValue()
		+ "\tcharge: " + this.getCharge()
		+ "\ttype: " + this.getType();
		
		return str;
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
	public double getqValue() {
		return qValue;
	}
	public void setqValue(double qValue) {
		this.qValue = qValue;
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
	public int getCharge() {
		return charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
		
		this.chargeSet = true;
	}
	public boolean isChargeSet() {
		return chargeSet;
	}
	

	public PercolatorPsmDTO getPercolatorPsm() {

		return percolatorPsm;
	}

	public void setPercolatorPsm(PercolatorPsmDTO percolatorPsm) {
		this.percolatorPsm = percolatorPsm;
	}


	
	private int id;
	private int searchId;
	private Integer scanId;
	private double qValue;
	private int type;
	private int reportedPeptideId;
	private int charge;
	
	private boolean chargeSet = false;


	private PercolatorPsmDTO percolatorPsm;


}
