package org.yeastrc.xlink.dto;

public class SearchCrosslinkLookupDTO {	
	
	public int getNrseqId1() {
		return nrseqId1;
	}
	public void setNrseqId1(int nrseqId1) {
		this.nrseqId1 = nrseqId1;
	}
	public int getNrseqId2() {
		return nrseqId2;
	}
	public void setNrseqId2(int nrseqId2) {
		this.nrseqId2 = nrseqId2;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId (int searchId) {
		this.searchId = searchId;
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
	
	private int nrseqId1;
	private int nrseqId2;
	private int searchId;
	private int protein1Position;
	private int protein2Position;
	private double bestPSMQValue;
	private double bestPeptideQValue;
	
}
