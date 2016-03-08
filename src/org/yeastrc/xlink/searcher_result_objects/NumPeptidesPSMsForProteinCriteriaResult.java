package org.yeastrc.xlink.searcher_result_objects;

/**
 * result from NumPeptidesPSMsForProteinCriteriaSearcher
 *
 */
public class NumPeptidesPSMsForProteinCriteriaResult {

	private int numPeptides;
	private int numUniquePeptides;
	private int numPSMs;
	
	
	public int getNumPeptides() {
		return numPeptides;
	}
	public void setNumPeptides(int numPeptides) {
		this.numPeptides = numPeptides;
	}
	public int getNumUniquePeptides() {
		return numUniquePeptides;
	}
	public void setNumUniquePeptides(int numUniquePeptides) {
		this.numUniquePeptides = numUniquePeptides;
	}
	public int getNumPSMs() {
		return numPSMs;
	}
	public void setNumPSMs(int numPSMs) {
		this.numPSMs = numPSMs;
	}

}
