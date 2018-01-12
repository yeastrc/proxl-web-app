package org.yeastrc.proxl.import_xml_to_db.dto;


/**
 * Crosslink  (Half of a Crosslink)
 * 
 * table srch_rep_pept__prot_seq_id_pos_crosslink
 *
 */
public class SrchRepPeptProtSeqIdPosCrosslinkDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int searchReportedPeptidepeptideId;
	private int proteinSequenceVersionId;
	private int proteinSequencePosition;
	
	@Override
	public String toString() {
		return "SrchRepPeptProteinSequenceIdPosCrosslinkDTO [id=" + id + ", searchId="
				+ searchId + ", reportedPeptideId=" + reportedPeptideId
				+ ", searchReportedPeptidepeptideId="
				+ searchReportedPeptidepeptideId + ", proteinSequenceVersionId=" + proteinSequenceVersionId
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

	public int getProteinSequencePosition() {
		return proteinSequencePosition;
	}
	public void setProteinSequencePosition(int proteinSequencePosition) {
		this.proteinSequencePosition = proteinSequencePosition;
	}

	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}

	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}

		
}
