package org.yeastrc.proxl.import_xml_to_db.dto;

/**
 * 
 * table protein_coverage
 */
public class ProteinCoverageDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int peptideIdInfoOnly;
	private int proteinSequenceVersionId;
	private int proteinStartPosition;
	private int proteinEndPosition;

	//  id is not part of equals or hashCode
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + peptideIdInfoOnly;
		result = prime * result + proteinEndPosition;
		result = prime * result + proteinSequenceVersionId;
		result = prime * result + proteinStartPosition;
		result = prime * result + reportedPeptideId;
		result = prime * result + searchId;
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
		ProteinCoverageDTO other = (ProteinCoverageDTO) obj;
		if (peptideIdInfoOnly != other.peptideIdInfoOnly)
			return false;
		if (proteinEndPosition != other.proteinEndPosition)
			return false;
		if (proteinSequenceVersionId != other.proteinSequenceVersionId)
			return false;
		if (proteinStartPosition != other.proteinStartPosition)
			return false;
		if (reportedPeptideId != other.reportedPeptideId)
			return false;
		if (searchId != other.searchId)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "PeptideProteinPositionDTO [id=" + id + ", searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId + ", peptideIdInfoOnly="
				+ peptideIdInfoOnly + ", proteinSequenceVersionId=" + proteinSequenceVersionId
				+ ", proteinStartPosition=" + proteinStartPosition
				+ ", proteinEndPosition=" + proteinEndPosition + "]";
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
	public int getProteinStartPosition() {
		return proteinStartPosition;
	}
	public void setProteinStartPosition(int proteinStartPosition) {
		this.proteinStartPosition = proteinStartPosition;
	}
	public int getProteinEndPosition() {
		return proteinEndPosition;
	}
	public void setProteinEndPosition(int proteinEndPosition) {
		this.proteinEndPosition = proteinEndPosition;
	}
	public int getPeptideIdInfoOnly() {
		return peptideIdInfoOnly;
	}
	public void setPeptideIdInfoOnly(int peptideIdInfoOnly) {
		this.peptideIdInfoOnly = peptideIdInfoOnly;
	}
	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}
	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}

	
}
