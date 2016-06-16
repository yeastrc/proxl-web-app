package org.yeastrc.proxl.import_xml_to_db.dto;

/**
// * table search__reported_peptide__dynamic_mod_lookup
 *
 */
public class SearchReportedPeptideDynamicModLookupDTO {

	
	private int searchId;
	private int reportedPeptideId;
	private double dynamicModMass;
	private int linkType;

	//  Must have equals(...) and hashCode()
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(dynamicModMass);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + linkType;
		result = prime * result + reportedPeptideId;
		result = prime * result + searchId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchReportedPeptideDynamicModLookupDTO other = (SearchReportedPeptideDynamicModLookupDTO) obj;
		if (Double.doubleToLongBits(dynamicModMass) != Double
				.doubleToLongBits(other.dynamicModMass))
			return false;
		if (linkType != other.linkType)
			return false;
		if (reportedPeptideId != other.reportedPeptideId)
			return false;
		if (searchId != other.searchId)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "SearchReportedPeptideDynamicModLookupDTO [searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId
				+ ", dynamicModMass=" + dynamicModMass + ", linkType="
				+ linkType + "]";
	}

	
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
