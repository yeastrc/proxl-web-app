package org.yeastrc.xlink.dto;


/**
 * table  search_dimer_best_peptide_value_generic_lookup
 *
 */
public class SearchDimerBestPeptideValueGenericLookupDTO {	
	

	private int id;
	private int searchDimerGenericLookup;
	

	private int nrseqId1;
	private int nrseqId2;
	private int searchId;
	
	private int peptideFilterableAnnotationTypeId;
	
	private double bestPeptideValueForAnnTypeId;
	private String bestPeptideValueStringForAnnTypeId;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchDimerGenericLookup() {
		return searchDimerGenericLookup;
	}
	public void setSearchDimerGenericLookup(int searchDimerGenericLookup) {
		this.searchDimerGenericLookup = searchDimerGenericLookup;
	}
	
	public int getNrseqId1() {
		return nrseqId1;
	}
	public void setNrseqId1(int nrseqId1) {
		this.nrseqId1 = nrseqId1;
	}
	public int getNrseqId2() {
		return nrseqId2;
	}
	public void setNrseqId2(int nrseqId2) {
		this.nrseqId2 = nrseqId2;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getAnnotationTypeId() {
		return peptideFilterableAnnotationTypeId;
	}
	public void setPeptideFilterableAnnotationTypeId(
			int peptideFilterableAnnotationTypeId) {
		this.peptideFilterableAnnotationTypeId = peptideFilterableAnnotationTypeId;
	}
	public double getBestPeptideValueForAnnTypeId() {
		return bestPeptideValueForAnnTypeId;
	}
	public void setBestPeptideValueForAnnTypeId(double bestPeptideValueForAnnTypeId) {
		this.bestPeptideValueForAnnTypeId = bestPeptideValueForAnnTypeId;
	}
	public String getBestPeptideValueStringForAnnTypeId() {
		return bestPeptideValueStringForAnnTypeId;
	}
	public void setBestPeptideValueStringForAnnTypeId(
			String bestPeptideValueStringForAnnTypeId) {
		this.bestPeptideValueStringForAnnTypeId = bestPeptideValueStringForAnnTypeId;
	}
	
		
}
