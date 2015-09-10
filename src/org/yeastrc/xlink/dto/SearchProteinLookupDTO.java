package org.yeastrc.xlink.dto;

/**
 * Represents a row in the cache table search_protein_lookup
 * Caches information to greatly speed up retrieval of proteins relevant to certain searches
 * @author Michael Riffle
 *
 */
public class SearchProteinLookupDTO {

	private int searchId;
	private int nrseqId;
	
	private Double bestCrosslinkPSMQValue;
	private Double bestCrosslinkPeptideQValue;
	
	private Double bestLooplinkPSMQValue;
	private Double bestLooplinkPeptideQValue;
	
	private Double bestMonolinkPSMQValue;
	private Double bestMonolinkPeptideQValue;
	
	private Double bestDimerPSMQValue;
	private Double bestDimerPeptideQValue;
	
	private Double bestUnlinkedPSMQValue;
	private Double bestUnlinkedPeptideQValue;
	
	
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getNrseqId() {
		return nrseqId;
	}
	public void setNrseqId(int nrseqId) {
		this.nrseqId = nrseqId;
	}
	public Double getBestCrosslinkPSMQValue() {
		return bestCrosslinkPSMQValue;
	}
	public void setBestCrosslinkPSMQValue(Double bestCrosslinkPSMQValue) {
		this.bestCrosslinkPSMQValue = bestCrosslinkPSMQValue;
	}
	public Double getBestCrosslinkPeptideQValue() {
		return bestCrosslinkPeptideQValue;
	}
	public void setBestCrosslinkPeptideQValue(Double bestCrosslinkPeptideQValue) {
		this.bestCrosslinkPeptideQValue = bestCrosslinkPeptideQValue;
	}
	public Double getBestLooplinkPSMQValue() {
		return bestLooplinkPSMQValue;
	}
	public void setBestLooplinkPSMQValue(Double bestLooplinkPSMQValue) {
		this.bestLooplinkPSMQValue = bestLooplinkPSMQValue;
	}
	public Double getBestLooplinkPeptideQValue() {
		return bestLooplinkPeptideQValue;
	}
	public void setBestLooplinkPeptideQValue(Double bestLooplinkPeptideQValue) {
		this.bestLooplinkPeptideQValue = bestLooplinkPeptideQValue;
	}
	public Double getBestMonolinkPSMQValue() {
		return bestMonolinkPSMQValue;
	}
	public void setBestMonolinkPSMQValue(Double bestMonolinkPSMQValue) {
		this.bestMonolinkPSMQValue = bestMonolinkPSMQValue;
	}
	public Double getBestMonolinkPeptideQValue() {
		return bestMonolinkPeptideQValue;
	}
	public void setBestMonolinkPeptideQValue(Double bestMonolinkPeptideQValue) {
		this.bestMonolinkPeptideQValue = bestMonolinkPeptideQValue;
	}
	public Double getBestDimerPSMQValue() {
		return bestDimerPSMQValue;
	}
	public void setBestDimerPSMQValue(Double bestDimerPSMQValue) {
		this.bestDimerPSMQValue = bestDimerPSMQValue;
	}
	public Double getBestDimerPeptideQValue() {
		return bestDimerPeptideQValue;
	}
	public void setBestDimerPeptideQValue(Double bestDimerPeptideQValue) {
		this.bestDimerPeptideQValue = bestDimerPeptideQValue;
	}
	public Double getBestUnlinkedPSMQValue() {
		return bestUnlinkedPSMQValue;
	}
	public void setBestUnlinkedPSMQValue(Double bestUnlinkedPSMQValue) {
		this.bestUnlinkedPSMQValue = bestUnlinkedPSMQValue;
	}
	public Double getBestUnlinkedPeptideQValue() {
		return bestUnlinkedPeptideQValue;
	}
	public void setBestUnlinkedPeptideQValue(Double bestUnlinkedPeptideQValue) {
		this.bestUnlinkedPeptideQValue = bestUnlinkedPeptideQValue;
	}
	
	

	
}
