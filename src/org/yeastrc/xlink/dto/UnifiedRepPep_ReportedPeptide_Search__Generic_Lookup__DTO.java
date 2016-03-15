package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;

/**
 * table unified_rp__rep_pept__search__generic_lookup
 *
 */
public class UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO {


	private int unifiedReportedPeptideId;
	private int reportedPeptideId;
	private int searchId;
	private int linkType;
	
	
	private boolean hasDynamicModifications;
	private boolean hasMonolinks;
	private boolean allRelatedPeptidesUniqueForSearch;

	private int psmNumAtDefaultCutoff;
	private int numUniquePsmAtDefaultCutoff;

	private int samplePsmId;
	
	/**
	 * Not applicable if there are no Peptide filterable annotations
	 */
	private Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs;

	

	@Override
	public String toString() {
		return "UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO [unifiedReportedPeptideId="
				+ unifiedReportedPeptideId
				+ ", reportedPeptideId="
				+ reportedPeptideId
				+ ", searchId="
				+ searchId
				+ ", linkType="
				+ linkType
				+ ", hasDynamicModifications="
				+ hasDynamicModifications
				+ ", hasMonolinks="
				+ hasMonolinks
				+ ", allRelatedPeptidesUniqueForSearch="
				+ allRelatedPeptidesUniqueForSearch
				+ ", psmNumAtDefaultCutoff="
				+ psmNumAtDefaultCutoff
				+ ", samplePsmId="
				+ samplePsmId
				+ ", peptideMeetsDefaultCutoffs="
				+ peptideMeetsDefaultCutoffs
				+ "]";
	}


	/**
	 * Not applicable if there are no Peptide filterable annotations
	 * @return
	 */
	public Yes_No__NOT_APPLICABLE_Enum getPeptideMeetsDefaultCutoffs() {
		return peptideMeetsDefaultCutoffs;
	}


	/**
	 * Not applicable if there are no Peptide filterable annotations
	 * @param peptideMeetsDefaultCutoffs
	 */
	public void setPeptideMeetsDefaultCutoffs(
			Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs) {
		this.peptideMeetsDefaultCutoffs = peptideMeetsDefaultCutoffs;
	}

	public boolean isAllRelatedPeptidesUniqueForSearch() {
		return allRelatedPeptidesUniqueForSearch;
	}


	public void setAllRelatedPeptidesUniqueForSearch(
			boolean allRelatedPeptidesUniqueForSearch) {
		this.allRelatedPeptidesUniqueForSearch = allRelatedPeptidesUniqueForSearch;
	}

	
	public int getPsmNumAtDefaultCutoff() {
		return psmNumAtDefaultCutoff;
	}
	public void setPsmNumAtDefaultCutoff(int psmNumAtDefaultCutoff) {
		this.psmNumAtDefaultCutoff = psmNumAtDefaultCutoff;
	}
	
	public int getSamplePsmId() {
		return samplePsmId;
	}
	public void setSamplePsmId(int samplePsmId) {
		this.samplePsmId = samplePsmId;
	}
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

	public int getNumUniquePsmAtDefaultCutoff() {
		return numUniquePsmAtDefaultCutoff;
	}


	public void setNumUniquePsmAtDefaultCutoff(int numUniquePsmAtDefaultCutoff) {
		this.numUniquePsmAtDefaultCutoff = numUniquePsmAtDefaultCutoff;
	}
}
