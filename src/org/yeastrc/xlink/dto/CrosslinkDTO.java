package org.yeastrc.xlink.dto;

import java.math.BigDecimal;

/**
 * table crosslink
 *
 */
public class CrosslinkDTO {
	


	
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
	public int getPeptide1Position() {
		return peptide1Position;
	}
	public void setPeptide1Position(int peptide1Position) {
		this.peptide1Position = peptide1Position;
	}
	public int getPeptide2Position() {
		return peptide2Position;
	}
	public void setPeptide2Position(int peptide2Position) {
		this.peptide2Position = peptide2Position;
	}
	public int getPeptide1MatchedPeptideId() {
		return peptide1MatchedPeptideId;
	}
	public void setPeptide1MatchedPeptideId(int peptide1MatchedPeptideId) {
		this.peptide1MatchedPeptideId = peptide1MatchedPeptideId;
	}
	public int getPeptide2MatchedPeptideId() {
		return peptide2MatchedPeptideId;
	}
	public void setPeptide2MatchedPeptideId(int peptide2MatchedPeptideId) {
		this.peptide2MatchedPeptideId = peptide2MatchedPeptideId;
	}
	public BigDecimal getLinkerMass() {
		return linkerMass;
	}
	public void setLinkerMass(BigDecimal linkerMass) {
		this.linkerMass = linkerMass;
	}
	
	@Override
	public String toString() {
		return "CrosslinkDTO [id=" + id + ", psm=" + psm + ", protein1="
				+ protein1 + ", protein2=" + protein2 + ", protein1Position="
				+ protein1Position + ", protein2Position=" + protein2Position
				+ ", peptide1Id=" + peptide1Id + ", peptide2Id=" + peptide2Id
				+ ", peptide1Position=" + peptide1Position
				+ ", peptide2Position=" + peptide2Position
				+ ", peptide1MatchedPeptideId=" + peptide1MatchedPeptideId
				+ ", peptide2MatchedPeptideId=" + peptide2MatchedPeptideId
				+ ", linkerMass=" + linkerMass + "]";
	}

	private int id;
	private PsmDTO psm;
	private NRProteinDTO protein1;
	private NRProteinDTO protein2;
	private int protein1Position;
	private int protein2Position;
	private int peptide1Id;
	private int peptide2Id;
	private int peptide1Position;
	private int peptide2Position;
	private int peptide1MatchedPeptideId;
	private int peptide2MatchedPeptideId;

	private BigDecimal linkerMass;


}
