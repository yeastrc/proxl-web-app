package org.yeastrc.proxl.import_xml_to_db.dto;


/**
 * table search_reported_peptide
 *
 */
public class SearchReportedPeptideDTO {

	
	private int searchId;
	private int reportedPeptideId;
	private int linkType;

	@Override
	public String toString() {
		return "SearchReportedPeptideDTO [searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId + ", linkType="
				+ linkType + "]";
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
	public int getLinkType() {
		return linkType;
	}
	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	
}
