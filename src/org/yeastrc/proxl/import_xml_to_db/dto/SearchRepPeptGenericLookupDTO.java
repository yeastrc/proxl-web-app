package org.yeastrc.proxl.import_xml_to_db.dto;

import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;

/**
 * table search__rep_pept__generic_lookup
 *
 */
public class SearchRepPeptGenericLookupDTO {

	private int searchId;
	private int reportedPeptideId;
	private int linkType;
	
	
	private boolean hasDynamicModifications;
	private boolean hasMonolinks;
	private boolean allRelatedPeptidesUniqueForSearch;

	private int psmNumAtDefaultCutoff;
	private int numUniquePsmAtDefaultCutoff;

	
	/**
	 * Not applicable if there are no Peptide filterable annotations
	 */
	private Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs;
	
	
	
	@Override
	public String toString() {
		return "SearchRepPeptGenericLookupDTO [searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId + ", linkType="
				+ linkType + ", hasDynamicModifications="
				+ hasDynamicModifications + ", hasMonolinks=" + hasMonolinks
				+ ", allRelatedPeptidesUniqueForSearch="
				+ allRelatedPeptidesUniqueForSearch
				+ ", psmNumAtDefaultCutoff=" + psmNumAtDefaultCutoff
				+ ", numUniquePsmAtDefaultCutoff="
				+ numUniquePsmAtDefaultCutoff + ", peptideMeetsDefaultCutoffs="
				+ peptideMeetsDefaultCutoffs + "]";
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


	public int getNumUniquePsmAtDefaultCutoff() {
		return numUniquePsmAtDefaultCutoff;
	}


	public void setNumUniquePsmAtDefaultCutoff(int numUniquePsmAtDefaultCutoff) {
		this.numUniquePsmAtDefaultCutoff = numUniquePsmAtDefaultCutoff;
	}


	public Yes_No__NOT_APPLICABLE_Enum getPeptideMeetsDefaultCutoffs() {
		return peptideMeetsDefaultCutoffs;
	}


	public void setPeptideMeetsDefaultCutoffs(
			Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs) {
		this.peptideMeetsDefaultCutoffs = peptideMeetsDefaultCutoffs;
	}


	
}

