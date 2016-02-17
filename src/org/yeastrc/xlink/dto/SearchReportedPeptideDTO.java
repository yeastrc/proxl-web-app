package org.yeastrc.xlink.dto;

/*
	CREATE TABLE search_reported_peptide (
		search_id INT UNSIGNED NOT NULL,
		reported_peptide_id INT UNSIGNED NOT NULL,
	);
 */
public class SearchReportedPeptideDTO {

	
	private int searchId;
	private int reportedPeptideId;

	
	@Override
	public String toString() {
		return "SearchReportedPeptideDTO [searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId + "]";
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

	
}
