package org.yeastrc.xlink.dto;

public class NrseqDatabasePeptideProteinDTO {
	

	public int getNrseqDatabaseId() {
		return nrseqDatabaseId;
	}
	public void setNrseqDatabaseId(int nrseqDatabaseId) {
		this.nrseqDatabaseId = nrseqDatabaseId;
	}
	public int getPeptideId() {
		return peptideId;
	}
	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}
	public int getNrseqId() {
		return nrseqId;
	}
	public void setNrseqId(int nrseqId) {
		this.nrseqId = nrseqId;
	}
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	private int nrseqDatabaseId;
	private int peptideId;
	private int nrseqId;
	private boolean unique;

	
}
