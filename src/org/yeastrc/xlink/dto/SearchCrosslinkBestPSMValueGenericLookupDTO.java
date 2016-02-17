package org.yeastrc.xlink.dto;


/**
 * table  search_crosslink_best_psm_value_generic_lookup
 *
 */
public class SearchCrosslinkBestPSMValueGenericLookupDTO {	
	

	private int id;
	private int searchCrosslinkGenericLookup;
	
	private int nrseqId1;
	private int nrseqId2;
	private int searchId;
	private int protein1Position;
	private int protein2Position;
	
	private int psmFilterableAnnotationTypeId;
	
	private double bestPsmValueForAnnTypeId;
	private String bestPsmValueStringForAnnTypeId;
	
	

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
