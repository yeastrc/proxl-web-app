package org.yeastrc.xlink.dto;

public class PsmProteinDTO {
	
	public int getPsmId() {
		return psmId;
	}
	public void setPsmId(int psmId) {
		this.psmId = psmId;
	}
	public int getNrseqId() {
		return nrseqId;
	}
	public void setNrseqId(int nrseqId) {
		this.nrseqId = nrseqId;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getPeptideId() {
		return peptideId;
	}
	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


	private int psmId;
	private int nrseqId;
	private int position;
	private int peptideId;
	private int id;
}
