package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;

/**
 * table monolink__rep_pept__search__generic_lookup
 *
 */
public class MonolinkRepPeptSearchGenericLookupDTO {


	private int id;
	private int searchId;
	private int reportedPeptideId;

	private int proteinId;
	private int proteinPosition;

	private boolean allRelatedPeptidesUniqueForSearch;

	private int psmNumAtDefaultCutoff;

	/**
	 * Not applicable if there are no Peptide filterable annotations
	 */
	private Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs;

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProteinId() {
		return proteinId;
	}

	public void setProteinId(int proteinId) {
		this.proteinId = proteinId;
	}

	public int getProteinPosition() {
		return proteinPosition;
	}

	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
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

	public Yes_No__NOT_APPLICABLE_Enum getPeptideMeetsDefaultCutoffs() {
		return peptideMeetsDefaultCutoffs;
	}

	public void setPeptideMeetsDefaultCutoffs(
			Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs) {
		this.peptideMeetsDefaultCutoffs = peptideMeetsDefaultCutoffs;
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
