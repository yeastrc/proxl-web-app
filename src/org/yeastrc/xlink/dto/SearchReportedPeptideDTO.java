package org.yeastrc.xlink.dto;

/*
	CREATE TABLE search_reported_peptide (
		search_id INT UNSIGNED NOT NULL,
		reported_peptide_id INT UNSIGNED NOT NULL,
		q_value DOUBLE NOT NULL,
	);
 */
public class SearchReportedPeptideDTO {

	
	private int searchId;
	private int reportedPeptideId;
	private double qValue;
	
	

	
	@Override
	public String toString() {
		return "SearchReportedPeptideDTO [searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId + ", qValue="
				+ qValue + "]";
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
	public double getqValue() {
		return qValue;
	}
	public void setqValue(double qValue) {
		this.qValue = qValue;
	}
	
}
