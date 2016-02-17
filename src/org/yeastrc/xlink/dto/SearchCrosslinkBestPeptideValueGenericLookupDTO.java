package org.yeastrc.xlink.dto;


/**
 * table  search_crosslink_best_peptide_value_generic_lookup
 *
 */
public class SearchCrosslinkBestPeptideValueGenericLookupDTO {	
	

	private int id;
	private int searchCrosslinkGenericLookup;
	

	private int nrseqId1;
	private int nrseqId2;
	private int searchId;
	private int protein1Position;
	private int protein2Position;
	
	private int peptideFilterableAnnotationTypeId;
	
	private double bestPeptideValueForAnnTypeId;
	private String bestPeptideValueStringForAnnTypeId;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchCrosslinkGenericLookup() {
		return searchCrosslinkGenericLookup;
	}
	public void setSearchCrosslinkGenericLookup(int searchCrosslinkGenericLookup) {
		this.searchCrosslinkGenericLookup = searchCrosslinkGenericLookup;
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
	public int getProtein1Position() {
		return protein1Position;
	}
	public void setProtein1Position(int protein1Position) {
		this.protein1Position = protein1Position;
	}
	public int getProtein2Position() {
		return protein2Position;
	}
	public void setProtein2Position(int protein2Position) {
		this.protein2Position = protein2Position;
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
