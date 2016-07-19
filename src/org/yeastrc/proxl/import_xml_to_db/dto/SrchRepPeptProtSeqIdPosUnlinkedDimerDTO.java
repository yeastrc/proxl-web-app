package org.yeastrc.proxl.import_xml_to_db.dto;


/**
 * Unlinked or Dimer (Half of a Dimer)
 * 
 * table srch_rep_pept__prot_seq_id_unlinked_dimer
 *
 */
public class SrchRepPeptProtSeqIdPosUnlinkedDimerDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int searchReportedPeptidepeptideId;
	private int proteinSequenceId;
	
	@Override
	public String toString() {
		return "SrchRepPeptProteinSequenceIdPosUnlinkedDimerDTO [id=" + id
				+ ", searchId=" + searchId + ", reportedPeptideId="
				+ reportedPeptideId + ", searchReportedPeptidepeptideId="
				+ searchReportedPeptidepeptideId + ", proteinSequenceId=" + proteinSequenceId
				+ "]";
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


		
}
