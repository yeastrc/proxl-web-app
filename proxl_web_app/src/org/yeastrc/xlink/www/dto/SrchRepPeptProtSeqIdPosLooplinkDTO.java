package org.yeastrc.xlink.www.dto;


/**
 * Looplink
 * 
 * table srch_rep_pept__prot_seq_id_pos_looplink
 *
 */
public class SrchRepPeptProtSeqIdPosLooplinkDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int searchReportedPeptidepeptideId;
	private int proteinSequenceId;
	private int proteinSequencePosition_1; 
	private int proteinSequencePosition_2; 
	
	@Override
	public String toString() {
		return "SrchRepPeptProtSeqIdPosDTO [id=" + id + ", searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId
				+ ", searchReportedPeptidepeptideId="
				+ searchReportedPeptidepeptideId + ", proteinSequenceId=" + proteinSequenceId
				+ ", proteinSequencePosition_1=" + proteinSequencePosition_1 + ", proteinSequencePosition_2="
				+ proteinSequencePosition_2 + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSearchId() {
		return searchId;
	}

	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	public int getReportedPeptideId() {
		return reportedPeptideId;
	}

	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}

	public int getSearchReportedPeptidepeptideId() {
		return searchReportedPeptidepeptideId;
	}

	public void setSearchReportedPeptidepeptideId(int searchReportedPeptidepeptideId) {
		this.searchReportedPeptidepeptideId = searchReportedPeptidepeptideId;
	}

	public int getProteinSequenceId() {
		return proteinSequenceId;
	}

	public void setProteinSequenceId(int proteinSequenceId) {
		this.proteinSequenceId = proteinSequenceId;
	}

	public int getProteinSequencePosition_1() {
		return proteinSequencePosition_1;
	}

	public void setProteinSequencePosition_1(int proteinSequencePosition_1) {
		this.proteinSequencePosition_1 = proteinSequencePosition_1;
	}

	public int getProteinSequencePosition_2() {
		return proteinSequencePosition_2;
	}

	public void setProteinSequencePosition_2(int proteinSequencePosition_2) {
		this.proteinSequencePosition_2 = proteinSequencePosition_2;
	}
		
}
