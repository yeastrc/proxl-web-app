package org.yeastrc.xlink.dto;

public class SearchLooplinkLookupDTO {		
	
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
	public double getBestPSMQValue() {
		return bestPSMQValue;
	}
	public void setBestPSMQValue(double bestPSMQValue) {
		this.bestPSMQValue = bestPSMQValue;
	}
	public double getBestPeptideQValue() {
		return bestPeptideQValue;
	}
	public void setBestPeptideQValue(double bestPeptideQValue) {
		this.bestPeptideQValue = bestPeptideQValue;
	}
	
	private int nrseqId;
	private int searchId;
	private int proteinPosition1;
	private int proteinPosition2;
	private double bestPSMQValue;
	private double bestPeptideQValue;
	
}
