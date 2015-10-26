package org.yeastrc.xlink.www.lorikeet_dto;

import java.math.BigDecimal;

/**
 * Data for a Cross Link - Cross Links Project
 *
 */
public class LorikeetCrossLinkData {

	private LorikeetPerPeptideData peptideData1;
	private int crossLinkPos1;

	private LorikeetPerPeptideData peptideData2;
	private int crossLinkPos2;
	
	private BigDecimal linkerMass;


	public int getCrossLinkPos1() {
		return crossLinkPos1;
	}

	public void setCrossLinkPos1(int crossLinkPos1) {
		this.crossLinkPos1 = crossLinkPos1;
	}

	public int getCrossLinkPos2() {
		return crossLinkPos2;
	}

	public void setCrossLinkPos2(int crossLinkPos2) {
		this.crossLinkPos2 = crossLinkPos2;
	}

	public BigDecimal getLinkerMass() {
		return linkerMass;
	}

	public void setLinkerMass(BigDecimal linkerMass) {
		this.linkerMass = linkerMass;
	}
	
	public LorikeetPerPeptideData getPeptideData1() {
		return peptideData1;
	}

	public void setPeptideData1(LorikeetPerPeptideData peptideData1) {
		this.peptideData1 = peptideData1;
	}

	public LorikeetPerPeptideData getPeptideData2() {
		return peptideData2;
	}

	public void setPeptideData2(LorikeetPerPeptideData peptideData2) {
		this.peptideData2 = peptideData2;
	}

}
