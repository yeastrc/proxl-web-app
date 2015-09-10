package org.yeastrc.xlink.dto;

public class DimerDTO {
	
	
	public PsmDTO getPsm() {
		return psm;
	}
	public void setPsm(PsmDTO psm) {
		this.psm = psm;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public NRProteinDTO getProtein1() {
		return protein1;
	}
	public void setProtein1(NRProteinDTO protein1) {
		this.protein1 = protein1;
	}
	public NRProteinDTO getProtein2() {
		return protein2;
	}
	public void setProtein2(NRProteinDTO protein2) {
		this.protein2 = protein2;
	}
	public int getPeptide1Id() {
		return peptide1Id;
	}
	public void setPeptide1Id(int peptide1Id) {
		this.peptide1Id = peptide1Id;
	}
	public int getPeptide2Id() {
		return peptide2Id;
	}
	public void setPeptide2Id(int peptide2Id) {
		this.peptide2Id = peptide2Id;
	}

	private int id;
	private PsmDTO psm;
	private NRProteinDTO protein1;
	private NRProteinDTO protein2;
	private int peptide1Id;
	private int peptide2Id;

}
