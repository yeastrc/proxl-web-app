package org.yeastrc.xlink.dto;

public class MatchedPeptideDTO {
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPsm_id() {
		return psm_id;
	}
	public void setPsm_id(int psm_id) {
		this.psm_id = psm_id;
	}
	public int getPeptide_id() {
		return peptide_id;
	}
	public void setPeptide_id(int peptide_id) {
		this.peptide_id = peptide_id;
	}
	
	private int id;
	private int psm_id;
	private int peptide_id;
}

