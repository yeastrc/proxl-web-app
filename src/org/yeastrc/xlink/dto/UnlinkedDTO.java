package org.yeastrc.xlink.dto;

public class UnlinkedDTO {
	
	

	private int id;
	private PsmDTO psm;
	private NRProteinDTO protein;
	private int peptideId;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public PsmDTO getPsm() {
		return psm;
	}
	public void setPsm(PsmDTO psm) {
		this.psm = psm;
	}
	public NRProteinDTO getProtein() {
		return protein;
	}
	public void setProtein(NRProteinDTO protein) {
		this.protein = protein;
	}
	public int getPeptideId() {
		return peptideId;
	}
	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}

}
