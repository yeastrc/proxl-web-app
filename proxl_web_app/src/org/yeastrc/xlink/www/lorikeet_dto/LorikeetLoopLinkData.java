package org.yeastrc.xlink.www.lorikeet_dto;

import java.math.BigDecimal;

/**
 * Data for a Loop Link - Cross Links Project
 *
 */
public class LorikeetLoopLinkData {

	private LorikeetPerPeptideData peptideData;
	
	private int loopLinkPos1;
	private int loopLinkPos2;

	private BigDecimal linkerMass;

	
	
	public int getLoopLinkPos1() {
		return loopLinkPos1;
	}

	public void setLoopLinkPos1(int loopLinkPos1) {
		this.loopLinkPos1 = loopLinkPos1;
	}

	public int getLoopLinkPos2() {
		return loopLinkPos2;
	}

	public void setLoopLinkPos2(int loopLinkPos2) {
		this.loopLinkPos2 = loopLinkPos2;
	}

	
	public LorikeetPerPeptideData getPeptideData() {
		return peptideData;
	}

	public void setPeptideData(LorikeetPerPeptideData peptideData) {
		this.peptideData = peptideData;
	}

	public BigDecimal getLinkerMass() {
		return linkerMass;
	}

	public void setLinkerMass(BigDecimal linkerMass) {
		this.linkerMass = linkerMass;
	}
}
