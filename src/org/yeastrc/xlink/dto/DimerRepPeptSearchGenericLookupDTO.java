package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;

/**
 * table dimer__rep_pept__search__generic_lookup
 *
 */
public class DimerRepPeptSearchGenericLookupDTO {
	
	
	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int proteinId_1;
	private int proteinId_2;

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

	public int getProteinId_1() {
		return proteinId_1;
	}

	public void setProteinId_1(int proteinId_1) {
		this.proteinId_1 = proteinId_1;
	}

	public int getProteinId_2() {
		return proteinId_2;
	}

	public void setProteinId_2(int proteinId_2) {
		this.proteinId_2 = proteinId_2;
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
