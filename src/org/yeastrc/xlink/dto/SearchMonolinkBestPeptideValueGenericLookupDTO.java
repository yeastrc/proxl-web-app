package org.yeastrc.xlink.dto;


/**
 * table  search_monolink_best_peptide_value_generic_lookup
 *
 */
public class SearchMonolinkBestPeptideValueGenericLookupDTO {	

	private int id;
	private int searchMonolinkGenericLookup;
	
	private int nrseqId;
	private int searchId;
	private int proteinPosition;
	
	private int peptideFilterableAnnotationTypeId;
	
	private double bestPeptideValueForAnnTypeId;
	private String bestPeptideValueStringForAnnTypeId;
	
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchMonolinkGenericLookup() {
		return searchMonolinkGenericLookup;
	}
	public void setSearchMonolinkGenericLookup(int searchMonolinkGenericLookup) {
		this.searchMonolinkGenericLookup = searchMonolinkGenericLookup;
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
	public int getProteinPosition() {
		return proteinPosition;
	}
	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
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
