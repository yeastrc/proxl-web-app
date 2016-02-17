package org.yeastrc.xlink.dto;


/**
 * table  search_unlinked_best_psm_value_generic_lookup
 *
 */
public class SearchUnlinkedBestPSMValueGenericLookupDTO {	
	

	private int id;
	private int searchUnlinkedGenericLookup;
	

	private int nrseqId;
	private int searchId;
	
	private int psmFilterableAnnotationTypeId;
	
	private double bestPsmValueForAnnTypeId;
	private String bestPsmValueStringForAnnTypeId;
	

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
		return psmFilterableAnnotationTypeId;
	}
	public void setPsmFilterableAnnotationTypeId(int psmFilterableAnnotationTypeId) {
		this.psmFilterableAnnotationTypeId = psmFilterableAnnotationTypeId;
	}
	public double getBestPsmValueForAnnTypeId() {
		return bestPsmValueForAnnTypeId;
	}
	public void setBestPsmValueForAnnTypeId(double bestPsmValueForAnnTypeId) {
		this.bestPsmValueForAnnTypeId = bestPsmValueForAnnTypeId;
	}
	public String getBestPsmValueStringForAnnTypeId() {
		return bestPsmValueStringForAnnTypeId;
	}
	public void setBestPsmValueStringForAnnTypeId(
			String bestPsmValueStringForAnnTypeId) {
		this.bestPsmValueStringForAnnTypeId = bestPsmValueStringForAnnTypeId;
	}
	
	
}
