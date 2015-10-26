package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * 
 *
 */
public class ReportedPeptidesForUnifiedPeptIdMergedPeptidePage {

	private ReportedPeptideDTO reportedPeptide;
	
	private String searchName;
	
	private int searchId;
	private Double peptideQValue;
	private Double peptidePValue;
	private Double peptidePEP;
	private Double peptideSVMScore;
	
	private int numPSMs;
	
	
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public ReportedPeptideDTO getReportedPeptide() {
		return reportedPeptide;
	}
	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public Double getPeptideQValue() {
		return peptideQValue;
	}
	public void setPeptideQValue(Double peptideQValue) {
		this.peptideQValue = peptideQValue;
	}
	public Double getPeptidePValue() {
		return peptidePValue;
	}
	public void setPeptidePValue(Double peptidePValue) {
		this.peptidePValue = peptidePValue;
	}
	public Double getPeptidePEP() {
		return peptidePEP;
	}
	public void setPeptidePEP(Double peptidePEP) {
		this.peptidePEP = peptidePEP;
	}
	public Double getPeptideSVMScore() {
		return peptideSVMScore;
	}
	public void setPeptideSVMScore(Double peptideSVMScore) {
		this.peptideSVMScore = peptideSVMScore;
	}
	public int getNumPSMs() {
		return numPSMs;
	}
	public void setNumPSMs(int numPSMs) {
		this.numPSMs = numPSMs;
	}
	
	
}
