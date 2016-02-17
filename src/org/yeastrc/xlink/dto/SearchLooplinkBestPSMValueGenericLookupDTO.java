package org.yeastrc.xlink.dto;


/**
 * table  search_looplink_best_psm_value_generic_lookup
 *
 */
public class SearchLooplinkBestPSMValueGenericLookupDTO {	
	

	private int id;
	private int searchLooplinkGenericLookup;
	

	private int nrseqId;
	private int searchId;
	private int proteinPosition1;
	private int proteinPosition2;
	
	private int psmFilterableAnnotationTypeId;
	
	private double bestPsmValueForAnnTypeId;
	private String bestPsmValueStringForAnnTypeId;
	

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
