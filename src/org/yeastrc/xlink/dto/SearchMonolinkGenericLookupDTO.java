package org.yeastrc.xlink.dto;


/**
 * table  search_monolink_generic_lookup
 *
 */
public class SearchMonolinkGenericLookupDTO {	
	

	private int id;

	private int nrseqId;
	private int searchId;
	private int proteinPosition;
	
	
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
	public int getProteinPosition() {
		return proteinPosition;
	}
	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
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
