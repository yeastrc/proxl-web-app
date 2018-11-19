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
	private int proteinSequenceVersionId_1;
	private int proteinSequenceVersionId_2;
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
	public int getProteinSequenceVersionId_1() {
		return proteinSequenceVersionId_1;
	}
	public void setProteinSequenceVersionId_1(int proteinSequenceVersionId_1) {
		this.proteinSequenceVersionId_1 = proteinSequenceVersionId_1;
	}
	public int getProteinSequenceVersionId_2() {
		return proteinSequenceVersionId_2;
	}
	public void setProteinSequenceVersionId_2(int proteinSequenceVersionId_2) {
		this.proteinSequenceVersionId_2 = proteinSequenceVersionId_2;
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
