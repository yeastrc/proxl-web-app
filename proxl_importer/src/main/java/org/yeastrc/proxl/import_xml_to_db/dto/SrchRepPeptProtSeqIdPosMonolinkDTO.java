package org.yeastrc.proxl.import_xml_to_db.dto;


/**
 * Monolink
 * 
 * table srch_rep_pept__prot_seq_id_pos_monolink
 * 
 * Add equals() and hashCode() based on logical unique key (currently all but property 'id').
 *   They are used in DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO by using this in a Set or key in a Map
 *     to not insert duplicates of the logical unique key.
 *
 */
public class SrchRepPeptProtSeqIdPosMonolinkDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int searchReportedPeptidepeptideId;
	private int peptidePosition;
	private int proteinSequenceVersionId;
	private int proteinSequencePosition;
	private boolean is_N_Terminal;
	private boolean is_C_Terminal;
	
	@Override
	public String toString() {
		return "SrchRepPeptProtSeqIdPosMonolinkDTO [id=" + id + ", searchId=" + searchId + ", reportedPeptideId="
				+ reportedPeptideId + ", searchReportedPeptidepeptideId=" + searchReportedPeptidepeptideId
				+ ", peptidePosition=" + peptidePosition + ", proteinSequenceVersionId=" + proteinSequenceVersionId
				+ ", proteinSequencePosition=" + proteinSequencePosition + ", is_N_Terminal=" + is_N_Terminal
				+ ", is_C_Terminal=" + is_C_Terminal + "]";
	}
	
	//  equals() and hashCode() based on logical unique key (currently all but property 'id').

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (is_C_Terminal ? 1231 : 1237);
		result = prime * result + (is_N_Terminal ? 1231 : 1237);
		result = prime * result + peptidePosition;
		result = prime * result + proteinSequencePosition;
		result = prime * result + proteinSequenceVersionId;
		result = prime * result + reportedPeptideId;
		result = prime * result + searchId;
		result = prime * result + searchReportedPeptidepeptideId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SrchRepPeptProtSeqIdPosMonolinkDTO other = (SrchRepPeptProtSeqIdPosMonolinkDTO) obj;
		if (is_C_Terminal != other.is_C_Terminal)
			return false;
		if (is_N_Terminal != other.is_N_Terminal)
			return false;
		if (peptidePosition != other.peptidePosition)
			return false;
		if (proteinSequencePosition != other.proteinSequencePosition)
			return false;
		if (proteinSequenceVersionId != other.proteinSequenceVersionId)
			return false;
		if (reportedPeptideId != other.reportedPeptideId)
			return false;
		if (searchId != other.searchId)
			return false;
		if (searchReportedPeptidepeptideId != other.searchReportedPeptidepeptideId)
			return false;
		return true;
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
	public int getPeptidePosition() {
		return peptidePosition;
	}
	public void setPeptidePosition(int peptidePosition) {
		this.peptidePosition = peptidePosition;
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
	public boolean isIs_N_Terminal() {
		return is_N_Terminal;
	}
	public void setIs_N_Terminal(boolean is_N_Terminal) {
		this.is_N_Terminal = is_N_Terminal;
	}
	public boolean isIs_C_Terminal() {
		return is_C_Terminal;
	}
	public void setIs_C_Terminal(boolean is_C_Terminal) {
		this.is_C_Terminal = is_C_Terminal;
	}

		
}
