package org.yeastrc.xlink.dto;


/**
 * table srch__rep_pept__annotation
 *
 */
public class SearchReportedPeptideAnnotationDTO  extends AnnotationDataBaseDTO {

	private int searchId;
	private int reportedPeptideId;

	@Override
	public String toString() {
		return "SearchReportedPeptideAnnotationDTO [id=" + id + ", searchId="
				+ searchId + ", reportedPeptideId=" + reportedPeptideId
				+ ", filterableDescriptiveAnnotationType="
				+ filterableDescriptiveAnnotationType + ", annotationTypeId="
				+ annotationTypeId + ", valueDouble=" + valueDouble
				+ ", valueString=" + valueString + "]";
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
}
