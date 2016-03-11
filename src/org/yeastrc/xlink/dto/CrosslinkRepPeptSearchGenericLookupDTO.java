package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;


/**
 * table crosslink__rep_pept__search__generic_lookup
 *
 */
public class CrosslinkRepPeptSearchGenericLookupDTO {
	
	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int proteinId_1;
	private int proteinId_2;
	private int protein_1_position;
	private int protein_2_position;

	private boolean allRelatedPeptidesUniqueForSearch;

	private int psmNumAtDefaultCutoff;
	
	/**
	 * Not applicable if there are no Peptide filterable annotations
	 */
	private Yes_No__NOT_APPLICABLE_Enum peptideMeetsDefaultCutoffs;

	
	
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

	public int getProtein_1_position() {
		return protein_1_position;
	}

	public void setProtein_1_position(int protein_1_position) {
		this.protein_1_position = protein_1_position;
	}

	public int getProtein_2_position() {
		return protein_2_position;
	}

	public void setProtein_2_position(int protein_2_position) {
		this.protein_2_position = protein_2_position;
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

}
