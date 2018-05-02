package org.yeastrc.xlink.www.dto;

public class CustomProteinRegionAnnotationDTO {

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomProteinRegionAnnotationDTO [proteinSequenceVersionId=" + proteinSequenceVersionId
				+ ", startPosition=" + startPosition + ", endPosition=" + endPosition + ", annotationColor="
				+ annotationColor + ", annotationText=" + annotationText + ", projectId=" + projectId + ", createdBy="
				+ createdBy + "]";
	}
	
	private int proteinSequenceVersionId;
	private int startPosition;
	private int endPosition;
	private String annotationColor;
	private String annotationText;
	private int projectId;
	private int createdBy;
	
	/**
	 * @return the proteinSequenceVersionId
	 */
	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}
	/**
	 * @param proteinSequenceVersionId the proteinSequenceVersionId to set
	 */
	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}
	/**
	 * @return the startPosition
	 */
	public int getStartPosition() {
		return startPosition;
	}
	/**
	 * @param startPosition the startPosition to set
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	/**
	 * @return the endPosition
	 */
	public int getEndPosition() {
		return endPosition;
	}
	/**
	 * @param endPosition the endPosition to set
	 */
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}
	/**
	 * @return the annotationText
	 */
	public String getAnnotationText() {
		return annotationText;
	}
	/**
	 * @param annotationText the annotationText to set
	 */
	public void setAnnotationText(String annotationText) {
		this.annotationText = annotationText;
	}
	/**
	 * @return the projectId
	 */
	public int getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return the createdBy
	 */
	public int getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the annotationColor
	 */
	public String getAnnotationColor() {
		return annotationColor;
	}
	/**
	 * @param annotationColor the annotationColor to set
	 */
	public void setAnnotationColor(String annotationColor) {
		this.annotationColor = annotationColor;
	}
	
	
	
}
