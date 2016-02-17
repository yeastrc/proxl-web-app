package org.yeastrc.xlink.dto;


/**
 * table  search_looplink_generic_lookup
 *
 */
public class SearchLooplinkGenericLookupDTO {	
	

	private int id;

	private int nrseqId;
	private int searchId;
	private int proteinPosition1;
	private int proteinPosition2;
	
	
	private int numPsmAtDefaultCutoff;
	private int numLinkedPeptidesAtDefaultCutoff;
	private int numUniqueLinkedPeptidesAtDefaultCutoff;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getNumPsmAtDefaultCutoff() {
		return numPsmAtDefaultCutoff;
	}
	public void setNumPsmAtDefaultCutoff(int numPsmAtDefaultCutoff) {
		this.numPsmAtDefaultCutoff = numPsmAtDefaultCutoff;
	}
	public int getNumLinkedPeptidesAtDefaultCutoff() {
		return numLinkedPeptidesAtDefaultCutoff;
	}
	public void setNumLinkedPeptidesAtDefaultCutoff(
			int numLinkedPeptidesAtDefaultCutoff) {
		this.numLinkedPeptidesAtDefaultCutoff = numLinkedPeptidesAtDefaultCutoff;
	}
	public int getNumUniqueLinkedPeptidesAtDefaultCutoff() {
		return numUniqueLinkedPeptidesAtDefaultCutoff;
	}
	public void setNumUniqueLinkedPeptidesAtDefaultCutoff(
			int numUniqueLinkedPeptidesAtDefaultCutoff) {
		this.numUniqueLinkedPeptidesAtDefaultCutoff = numUniqueLinkedPeptidesAtDefaultCutoff;
	}
	
	}
