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
	public int getNumPeptidesAtPt01QCutoff() {
		return numPeptidesAtPt01QCutoff;
	}
	public void setNumPeptidesAtPt01QCutoff(int numPeptidesAtPt01QCutoff) {
		this.numPeptidesAtPt01QCutoff = numPeptidesAtPt01QCutoff;
	}
	public int getNumUniquePeptidesAtPt01QCutoff() {
		return numUniquePeptidesAtPt01QCutoff;
	}
	public void setNumUniquePeptidesAtPt01QCutoff(int numUniquePeptidesAtPt01QCutoff) {
		this.numUniquePeptidesAtPt01QCutoff = numUniquePeptidesAtPt01QCutoff;
	}
	
	private int nrseqId;
	private int searchId;
	private int proteinPosition1;
	private int proteinPosition2;

	private double bestPSMQValue;
	private Double bestPeptideQValue;
	
	private int numPsmAtPt01QCutoff;
	private int numPeptidesAtPt01QCutoff;
	private int numUniquePeptidesAtPt01QCutoff;
	
}
