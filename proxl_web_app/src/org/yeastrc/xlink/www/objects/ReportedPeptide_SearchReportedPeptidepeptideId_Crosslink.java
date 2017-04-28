package org.yeastrc.xlink.www.objects;

/**
 * Holds searchReportedPeptidepeptideIds for reported Peptide for Crosslink
 *
 */
public class ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink {

	private int reportedPeptideId;
	private int searchReportedPeptidepeptideId_1;
	private int searchReportedPeptidepeptideId_2;
	
	// Associated protein sequence ids
	private int proteinSequenceId_1;
	private int proteinSequenceId_2;
	private int proteinPosition_1;
	private int proteinPosition_2;
	
	
	public int getReportedPeptideId() {
		return reportedPeptideId;
	}
	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}
	public int getSearchReportedPeptidepeptideId_1() {
		return searchReportedPeptidepeptideId_1;
	}
	public void setSearchReportedPeptidepeptideId_1(int searchReportedPeptidepeptideId_1) {
		this.searchReportedPeptidepeptideId_1 = searchReportedPeptidepeptideId_1;
	}
	public int getSearchReportedPeptidepeptideId_2() {
		return searchReportedPeptidepeptideId_2;
	}
	public void setSearchReportedPeptidepeptideId_2(int searchReportedPeptidepeptideId_2) {
		this.searchReportedPeptidepeptideId_2 = searchReportedPeptidepeptideId_2;
	}
	public int getProteinSequenceId_1() {
		return proteinSequenceId_1;
	}
	public void setProteinSequenceId_1(int proteinSequenceId_1) {
		this.proteinSequenceId_1 = proteinSequenceId_1;
	}
	public int getProteinSequenceId_2() {
		return proteinSequenceId_2;
	}
	public void setProteinSequenceId_2(int proteinSequenceId_2) {
		this.proteinSequenceId_2 = proteinSequenceId_2;
	}
	public int getProteinPosition_1() {
		return proteinPosition_1;
	}
	public void setProteinPosition_1(int proteinPosition_1) {
		this.proteinPosition_1 = proteinPosition_1;
	}
	public int getProteinPosition_2() {
		return proteinPosition_2;
	}
	public void setProteinPosition_2(int proteinPosition_2) {
		this.proteinPosition_2 = proteinPosition_2;
	}
}
