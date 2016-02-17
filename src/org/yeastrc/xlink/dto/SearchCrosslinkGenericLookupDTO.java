package org.yeastrc.xlink.dto;


/**
 * table  search_crosslink_generic_lookup
 *
 */
public class SearchCrosslinkGenericLookupDTO {	
	

	private int id;

	private int nrseqId1;
	private int nrseqId2;
	private int searchId;
	private int protein1Position;
	private int protein2Position;
	
	
	private int numPsmAtDefaultCutoff;
	private int numLinkedPeptidesAtDefaultCutoff;
	private int numUniqueLinkedPeptidesAtDefaultCutoff;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
