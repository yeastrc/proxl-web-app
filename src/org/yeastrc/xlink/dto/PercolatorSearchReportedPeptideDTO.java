package org.yeastrc.xlink.dto;

/*
	CREATE TABLE percolator_search_reported_peptide (
		search_id INT UNSIGNED NOT NULL,
		reported_peptide_id INT UNSIGNED NOT NULL,
		svm_score DOUBLE  NULL,
		q_value DOUBLE  NULL,
		pep DOUBLE  NULL,
		calc_mass DOUBLE  NULL,
		p_value DOUBLE  NULL
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
	public Double getSvmScore() {
		return svmScore;
	}
	public void setSvmScore(Double svmScore) {
		this.svmScore = svmScore;
	}
	public Double getqValue() {
		return qValue;
	}
	public void setqValue(Double qValue) {
		this.qValue = qValue;
	}
	public Double getPep() {
		return pep;
	}
	public void setPep(Double pep) {
		this.pep = pep;
	}
	public Double getCalcMass() {
		return calcMass;
	}
	public void setCalcMass(Double calcMass) {
		this.calcMass = calcMass;
	}
	public Double getpValue() {
		return pValue;
	}
	public void setpValue(Double pValue) {
		this.pValue = pValue;
	}
	
	private int searchId;
	private int reportedPeptideId;
	private Double svmScore;
	private Double qValue;
	private Double pep;
	private Double calcMass;
	private Double pValue;
	
}
