package org.yeastrc.xlink.www.annotation_display;

import java.util.List;

/**
 * 
 *
 */
public class AnnTypeIdDisplayJSON_PerSearch {

	private List<Integer> psm;
	private List<Integer> peptide;
	
	public List<Integer> getPsm() {
		return psm;
	}
	public void setPsm(List<Integer> psm) {
		this.psm = psm;
	}
	public List<Integer> getPeptide() {
		return peptide;
	}
	public void setPeptide(List<Integer> peptide) {
		this.peptide = peptide;
	}
}