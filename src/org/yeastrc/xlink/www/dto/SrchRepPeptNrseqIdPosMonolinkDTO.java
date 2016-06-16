package org.yeastrc.xlink.www.dto;


/**
 * Monolink
 * 
 * table srch_rep_pept__nrseq_id_pos_monolink
 *
 */
public class SrchRepPeptNrseqIdPosMonolinkDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int searchReportedPeptidepeptideId;
	private int nrseqId;
	private int nrseqPosition;
	
	@Override
	public String toString() {
		return "SrchRepPeptNrseqIdPosMonolinkDTO [id=" + id + ", searchId="
				+ searchId + ", reportedPeptideId=" + reportedPeptideId
				+ ", searchReportedPeptidepeptideId="
				+ searchReportedPeptidepeptideId + ", nrseqId=" + nrseqId
				+ ", nrseqPosition=" + nrseqPosition + "]";
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
	public int getNrseqId() {
		return nrseqId;
	}
	public void setNrseqId(int nrseqId) {
		this.nrseqId = nrseqId;
	}
	public int getNrseqPosition() {
		return nrseqPosition;
	}
	public void setNrseqPosition(int nrseqPosition) {
		this.nrseqPosition = nrseqPosition;
	}
		
}
