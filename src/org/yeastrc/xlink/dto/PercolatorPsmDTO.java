package org.yeastrc.xlink.dto;

/*
CREATE TABLE percolator_psm (
    psm_id INT UNSIGNED NOT NULL PRIMARY KEY,
    percolator_file_id INT UNSIGNED NOT NULL,
    perc_psm_id VARCHAR(2000) NOT NULL,
    svm_score DOUBLE,
    q_value DOUBLE,
    calc_mass DOUBLE,
    pep DOUBLE,
);
 */

public class PercolatorPsmDTO {
	
	
	/**
	 * psm_id
	 */
	private int psmDBId;
	
	private int percolatorFileId;
	/**
	 * perc_psm_id
	 */
	private String psmId;
	private double svmScore;
	private double qValue;
	private double calcMass;
	private double pep;
	
	
	/**
	 * psm_id
	 * @return psm_id
	 */
	public int getPsmDBId() {
		return psmDBId;
	}
	/**
	 * psm_id
	 * @param psmDBId
	 */
	public void setPsmDBId(int psmDBId) {
		this.psmDBId = psmDBId;
	}
	/**
	 * perc_psm_id
	 * @return
	 */
	public String getPsmId() {
		return psmId;
	}
	/**
	 * perc_psm_id
	 * @param psmId
	 */
	public void setPsmId(String psmId) {
		this.psmId = psmId;
	}
	
	public int getPercolatorFileId() {
		return percolatorFileId;
	}
	public void setPercolatorFileId(int percolatorFileId) {
		this.percolatorFileId = percolatorFileId;
	}

	public double getSvmScore() {
		return svmScore;
	}
	public void setSvmScore(double svmScore) {
		this.svmScore = svmScore;
	}
	public double getqValue() {
		return qValue;
	}
	public void setqValue(double qValue) {
		this.qValue = qValue;
	}
	public double getCalcMass() {
		return calcMass;
	}
	public void setCalcMass(double calcMass) {
		this.calcMass = calcMass;
	}
	public double getPep() {
		return pep;
	}
	public void setPep(double pep) {
		this.pep = pep;
	}


}
