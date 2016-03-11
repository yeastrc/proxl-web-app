package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;


/**
 * table looplink__rep_pept__search__generic_lookup
 *
 */
public class LooplinkRepPeptSearchGenericLookupDTO {
	
	
	private int id;
	private int searchId;
	private int reportedPeptideId;

	private int proteinId;
	private int proteinPosition_1;
	private int proteinPosition_2;

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

	public int getProteinPosition_1() {
		return proteinPosition_1;
	}

	public void setProteinPosition_1(int proteinPosition_1) {
		this.proteinPosition_1 = proteinPosition_1;
	}

	public int getProteinPosition_2() {
		return proteinPosition_2;
	}

	public void setProteinPosition_2(int proteinPosition_2) {
		this.proteinPosition_2 = proteinPosition_2;
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
