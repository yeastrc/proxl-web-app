package org.yeastrc.xlink.dto;

/**
// * table search__reported_peptide__dynamic_mod_lookup
 *
 */
public class SearchReportedPeptideDynamicModLookupDTO {

	
	private int searchId;
	private int reportedPeptideId;
	private double dynamicModMass;
	private int linkType;


	
	public double getDynamicModMass() {
		return dynamicModMass;
	}
	public void setDynamicModMass(double dynamicModMass) {
		this.dynamicModMass = dynamicModMass;
	}
	public int getLinkType() {
		return linkType;
	}
	public void setLinkType(int linkType) {
		this.linkType = linkType;
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
