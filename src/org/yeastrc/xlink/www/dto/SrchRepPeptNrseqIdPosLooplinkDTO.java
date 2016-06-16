package org.yeastrc.xlink.www.dto;


/**
 * Looplink
 * 
 * table srch_rep_pept__nrseq_id_pos_looplink
 *
 */
public class SrchRepPeptNrseqIdPosLooplinkDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int searchReportedPeptidepeptideId;
	private int nrseqId;
	private int nrseqPosition_1; 
	private int nrseqPosition_2; 
	
	@Override
	public String toString() {
		return "SrchRepPeptNrseqIdPosDTO [id=" + id + ", searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId
				+ ", searchReportedPeptidepeptideId="
				+ searchReportedPeptidepeptideId + ", nrseqId=" + nrseqId
				+ ", nrseqPosition_1=" + nrseqPosition_1 + ", nrseqPosition_2="
				+ nrseqPosition_2 + "]";
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

	public int getNrseqPosition_1() {
		return nrseqPosition_1;
	}

	public void setNrseqPosition_1(int nrseqPosition_1) {
		this.nrseqPosition_1 = nrseqPosition_1;
	}

	public int getNrseqPosition_2() {
		return nrseqPosition_2;
	}

	public void setNrseqPosition_2(int nrseqPosition_2) {
		this.nrseqPosition_2 = nrseqPosition_2;
	}
		
}
