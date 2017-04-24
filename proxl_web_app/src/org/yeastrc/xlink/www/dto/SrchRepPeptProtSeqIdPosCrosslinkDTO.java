package org.yeastrc.xlink.www.dto;


/**
 * Crosslink  (Half of a Crosslink)
 * 
 * table srch_rep_pept__prot_seq_id_pos_crosslink
 *
 */
public class SrchRepPeptProtSeqIdPosCrosslinkDTO implements SrchRepPeptProtSeqIdPosCommonIF {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int searchReportedPeptidepeptideId;
	private int proteinSequenceId;
	private int proteinSequencePosition;
	
	@Override
	public String toString() {
		return "SrchRepPeptProtSeqIdPosCrosslinkDTO [id=" + id + ", searchId="
				+ searchId + ", reportedPeptideId=" + reportedPeptideId
				+ ", searchReportedPeptidepeptideId="
				+ searchReportedPeptidepeptideId + ", proteinSequenceId=" + proteinSequenceId
				+ ", proteinSequencePosition=" + proteinSequencePosition + "]";
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
	public int getProteinSequencePosition() {
		return proteinSequencePosition;
	}
	public void setProteinSequencePosition(int proteinSequencePosition) {
		this.proteinSequencePosition = proteinSequencePosition;
	}
		
}
