package org.yeastrc.proxl.import_xml_to_db.dto;

/**
 * 
 *
 */
public class SearchRepPeptBestPsmValueGenericLookupDTO {

	private int searchId;
	private int reportedPeptideId;
	private int annotationTypeId;
	private int linkType;
	
	
	private boolean hasDynamicModifications;
	private boolean hasMonolinks;
	
	private double bestPsmValueForAnnTypeId;


	@Override
	public String toString() {
		return "SearchRepPeptBestPsmValueGenericLookupDTO [searchId="
				+ searchId + ", reportedPeptideId=" + reportedPeptideId
				+ ", annotationTypeId="
				+ annotationTypeId + ", linkType=" + linkType
				+ ", hasDynamicModifications=" + hasDynamicModifications
				+ ", hasMonolinks=" + hasMonolinks
				+ ", bestPsmValueForAnnTypeId=" + bestPsmValueForAnnTypeId
				+ "]";
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

	public boolean isHasDynamicModifications() {
		return hasDynamicModifications;
	}

	public void setHasDynamicModifications(boolean hasDynamicModifications) {
		this.hasDynamicModifications = hasDynamicModifications;
	}

	public boolean isHasMonolinks() {
		return hasMonolinks;
	}

	public void setHasMonolinks(boolean hasMonolinks) {
		this.hasMonolinks = hasMonolinks;
	}

	public double getBestPsmValueForAnnTypeId() {
		return bestPsmValueForAnnTypeId;
	}

	public void setBestPsmValueForAnnTypeId(double bestPsmValueForAnnTypeId) {
		this.bestPsmValueForAnnTypeId = bestPsmValueForAnnTypeId;
	}

	public int getAnnotationTypeId() {
		return annotationTypeId;
	}

	public void setAnnotationTypeId(int annotationTypeId) {
		this.annotationTypeId = annotationTypeId;
	}
	
}
