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
	public Double getBestPeptideQValue() {
		return bestPeptideQValue;
	}
	public void setBestPeptideQValue(Double bestPeptideQValue) {
		this.bestPeptideQValue = bestPeptideQValue;
	}
	public int getNumPsmAtPt01QCutoff() {
		return numPsmAtPt01QCutoff;
	}
	public void setNumPsmAtPt01QCutoff(int numPsmAtPt01QCutoff) {
		this.numPsmAtPt01QCutoff = numPsmAtPt01QCutoff;
	}
	public int getNumLinkedPeptidesAtPt01QCutoff() {
		return numLinkedPeptidesAtPt01QCutoff;
	}
	public void setNumLinkedPeptidesAtPt01QCutoff(int numLinkedPeptidesAtPt01QCutoff) {
		this.numLinkedPeptidesAtPt01QCutoff = numLinkedPeptidesAtPt01QCutoff;
	}
	public int getNumUniqueLinkedPeptidesAtPt01QCutoff() {
		return numUniqueLinkedPeptidesAtPt01QCutoff;
	}
	public void setNumUniqueLinkedPeptidesAtPt01QCutoff(
			int numUniqueLinkedPeptidesAtPt01QCutoff) {
		this.numUniqueLinkedPeptidesAtPt01QCutoff = numUniqueLinkedPeptidesAtPt01QCutoff;
	}


	
	private int nrseqId1;
	private int nrseqId2;
	private int searchId;
	private int protein1Position;
	private int protein2Position;
	
	private double bestPSMQValue;
	private Double bestPeptideQValue;
	
	private int numPsmAtPt01QCutoff;
	private int numLinkedPeptidesAtPt01QCutoff;
	private int numUniqueLinkedPeptidesAtPt01QCutoff;
	
}
