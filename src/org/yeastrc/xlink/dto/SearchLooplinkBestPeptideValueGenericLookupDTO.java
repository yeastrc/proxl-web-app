package org.yeastrc.xlink.dto;


/**
 * table  search_looplink_best_peptide_value_generic_lookup
 *
 */
public class SearchLooplinkBestPeptideValueGenericLookupDTO {	
	

	private int id;
	private int searchLooplinkGenericLookup;
	
	private int nrseqId;
	private int searchId;
	private int proteinPosition1;
	private int proteinPosition2;
	
	private int peptideFilterableAnnotationTypeId;
	
	private double bestPeptideValueForAnnTypeId;
	private String bestPeptideValueStringForAnnTypeId;
	
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchLooplinkGenericLookup() {
		return searchLooplinkGenericLookup;
	}
	public void setSearchLooplinkGenericLookup(int searchLooplinkGenericLookup) {
		this.searchLooplinkGenericLookup = searchLooplinkGenericLookup;
	}	
	public int getNrseqId() {
		return nrseqId;
	}
	public void setNrseqId(int nrseqId) {
		this.nrseqId = nrseqId;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getProteinPosition1() {
		return proteinPosition1;
	}
	public void setProteinPosition1(int proteinPosition1) {
		this.proteinPosition1 = proteinPosition1;
	}
	public int getProteinPosition2() {
		return proteinPosition2;
	}
	public void setProteinPosition2(int proteinPosition2) {
		this.proteinPosition2 = proteinPosition2;
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
