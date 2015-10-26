package org.yeastrc.xlink.www.lorikeet_dto;

/**
 * Data for a Dimer - Cross Links Project
 *
 */
public class LorikeetDimerData {

	private LorikeetPerPeptideData peptideData1;

	private LorikeetPerPeptideData peptideData2;



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
