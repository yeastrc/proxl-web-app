package org.yeastrc.proxl.import_xml_to_db.dto;


/**
 * Dimer (Half of a Dimer)
 * 
 * table srch_rep_pept__prot_seq_id_dimer
 *
 */
public class SrchRepPeptProtSeqIdPosDimerDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int searchReportedPeptidepeptideId;
	private int proteinSequenceVersionId;
	
	@Override
	public String toString() {
		return "SrchRepPeptProteinSequenceIdPosDimerDTO [id=" + id
				+ ", searchId=" + searchId + ", reportedPeptideId="
				+ reportedPeptideId + ", searchReportedPeptidepeptideId="
				+ searchReportedPeptidepeptideId + ", proteinSequenceVersionId=" + proteinSequenceVersionId
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
	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}
	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}
		
}
