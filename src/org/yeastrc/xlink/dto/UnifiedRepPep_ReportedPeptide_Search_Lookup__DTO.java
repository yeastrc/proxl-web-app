package org.yeastrc.xlink.dto;

/**
 * table unified_rep_pep__reported_peptide__search_lookup
 *
 */
public class UnifiedRepPep_ReportedPeptide_Search_Lookup__DTO {

	private int unifiedReportedPeptideId;
	private int reportedPeptideId;
	private int searchId;
	private int linkType;
	
	private Double peptideQValue;
	private Double bestPsmQValue;
	
	private boolean hasDynamicModifications;
	private boolean hasMonolinks;
	
	private int psmNumAtPt01QvalueCutoff;
	
	
	public boolean isHasMonolinks() {
		return hasMonolinks;
	}
	public void setHasMonolinks(boolean hasMonolinks) {
		this.hasMonolinks = hasMonolinks;
	}
	public boolean isHasDynamicModifications() {
		return hasDynamicModifications;
	}
	public void setHasDynamicModifications(boolean hasDynamicModifications) {
		this.hasDynamicModifications = hasDynamicModifications;
	}
	public int getLinkType() {
		return linkType;
	}
	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}
	public int getPsmNumAtPt01QvalueCutoff() {
		return psmNumAtPt01QvalueCutoff;
	}
	public void setPsmNumAtPt01QvalueCutoff(int psmNumAtPt01QvalueCutoff) {
		this.psmNumAtPt01QvalueCutoff = psmNumAtPt01QvalueCutoff;
	}
	public int getUnifiedReportedPeptideId() {
		return unifiedReportedPeptideId;
	}
	public void setUnifiedReportedPeptideId(int unifiedReportedPeptideId) {
		this.unifiedReportedPeptideId = unifiedReportedPeptideId;
	}
	public int getReportedPeptideId() {
		return reportedPeptideId;
	}
	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public Double getPeptideQValue() {
		return peptideQValue;
	}
	public void setPeptideQValue(Double peptideQValue) {
		this.peptideQValue = peptideQValue;
	}
	public Double getBestPsmQValue() {
		return bestPsmQValue;
	}
	public void setBestPsmQValue(Double bestPsmQValue) {
		this.bestPsmQValue = bestPsmQValue;
	}
}
