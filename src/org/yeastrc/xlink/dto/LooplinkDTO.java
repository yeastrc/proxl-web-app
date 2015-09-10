package org.yeastrc.xlink.dto;

import java.math.BigDecimal;

/**
 * table looplink
 *
 */
public class LooplinkDTO {
	
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
	public int getPeptideId() {
		return peptideId;
	}
	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}
	public int getPeptidePosition1() {
		return peptidePosition1;
	}
	public void setPeptidePosition1(int peptidePosition1) {
		this.peptidePosition1 = peptidePosition1;
	}
	public int getPeptidePosition2() {
		return peptidePosition2;
	}
	public void setPeptidePosition2(int peptidePosition2) {
		this.peptidePosition2 = peptidePosition2;
	}
	public BigDecimal getLinkerMass() {
		return linkerMass;
	}
	public void setLinkerMass(BigDecimal linkerMass) {
		this.linkerMass = linkerMass;
	}
	public int getLinkerId() {
		return linkerId;
	}
	public void setLinkerId(int linkerId) {
		this.linkerId = linkerId;
	}
	
	private int id;
	private PsmDTO psm;
	private NRProteinDTO protein;
	private int proteinPosition1;
	private int proteinPosition2;
	private int peptideId;
	private int peptidePosition1;
	private int peptidePosition2;
	private BigDecimal linkerMass;
	private int linkerId;
}
