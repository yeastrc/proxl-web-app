package org.yeastrc.xlink.dto;


/**
 * table  search_unlinked_best_peptide_value_generic_lookup
 *
 */
public class SearchUnlinkedBestPeptideValueGenericLookupDTO {	
	

	private int id;
	private int searchUnlinkedGenericLookup;
	
	private int searchId;

	private int nrseqId;
	
	private int peptideFilterableAnnotationTypeId;
	
	private double bestPeptideValueForAnnTypeId;
	private String bestPeptideValueStringForAnnTypeId;
	
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchUnlinkedGenericLookup() {
		return searchUnlinkedGenericLookup;
	}
	public void setSearchUnlinkedGenericLookup(int searchUnlinkedGenericLookup) {
		this.searchUnlinkedGenericLookup = searchUnlinkedGenericLookup;
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
