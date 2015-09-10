package org.yeastrc.xlink.dto;

/*
	CREATE TABLE percolator_search_reported_peptide (
		search_id INT UNSIGNED NOT NULL,
		reported_peptide_id INT UNSIGNED NOT NULL,
		svm_score DOUBLE NOT NULL,
		q_value DOUBLE NOT NULL,
		pep DOUBLE NOT NULL,
		calc_mass DOUBLE NOT NULL,
		p_value DOUBLE NOT NULL
	);
 */
public class PercolatorSearchReportedPeptideDTO {

	@Override
	public String toString() {
		return "PercolatorSearchReportedPeptideDTO [searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId + ", svmScore="
				+ svmScore + ", qValue=" + qValue + ", pep=" + pep
				+ ", calcMass=" + calcMass + ", pValue=" + pValue + "]";
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
	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
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
	public double getPep() {
		return pep;
	}
	public void setPep(double pep) {
		this.pep = pep;
	}
	public double getCalcMass() {
		return calcMass;
	}
	public void setCalcMass(double calcMass) {
		this.calcMass = calcMass;
	}
	public double getpValue() {
		return pValue;
	}
	public void setpValue(double pValue) {
		this.pValue = pValue;
	}
	
	private int searchId;
	private int reportedPeptideId;
	private double svmScore;
	private double qValue;
	private double pep;
	private double calcMass;
	private double pValue;
	
}
