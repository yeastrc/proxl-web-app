package org.yeastrc.xlink.dto;

public class MonolinkDTO {

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
	public int getProteinPosition() {
		return proteinPosition;
	}
	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
	}
	public int getPeptideId() {
		return peptideId;
	}
	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}
	public int getPeptidePosition() {
		return peptidePosition;
	}
	public void setPeptidePosition(int peptidePosition) {
		this.peptidePosition = peptidePosition;
	}

	
	
	
	@Override
	public String toString() {
		return "MonolinkDTO [id=" + id + ", psm=" + psm + ", protein="
				+ protein + ", proteinPosition=" + proteinPosition
				+ ", peptideId=" + peptideId + ", peptidePosition="
				+ peptidePosition + "]";
	}



	private int id;

	private PsmDTO psm;
	private NRProteinDTO protein;
	private int proteinPosition;
	private int peptideId;
	private int peptidePosition;
}
