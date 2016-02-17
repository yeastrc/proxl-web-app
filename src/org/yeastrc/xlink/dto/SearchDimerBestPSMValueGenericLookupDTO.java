package org.yeastrc.xlink.dto;


/**
 * table  search_dimer_best_psm_value_generic_lookup
 *
 */
public class SearchDimerBestPSMValueGenericLookupDTO {	
	

	private int id;
	private int searchDimerGenericLookup;
	
	private int nrseqId1;
	private int nrseqId2;
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
