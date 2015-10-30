package org.yeastrc.xlink.dto;

public class SearchMonolinkLookupDTO {			

	public int getNrseqId() {
		return nrseqId;
	}
	public void setNrseqId(int nrseqId) {
		this.nrseqId = nrseqId;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getProteinPosition() {
		return proteinPosition;
	}
	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
	}
	public double getBestPSMQValue() {
		return bestPSMQValue;
	}
	public void setBestPSMQValue(double bestPSMQValue) {
		this.bestPSMQValue = bestPSMQValue;
	}
	public Double getBestPeptideQValue() {
		return bestPeptideQValue;
	}
	public void setBestPeptideQValue(Double bestPeptideQValue) {
		this.bestPeptideQValue = bestPeptideQValue;
	}
	
	private int nrseqId;
	private int searchId;
	private int proteinPosition;
	private double bestPSMQValue;
	private Double bestPeptideQValue;
	
}
