package org.yeastrc.xlink.www.objects;

/**
 * Display of data from table cutoffs_applied_on_import combined with annotation_type
 *
 */
public class CutoffsAppliedOnImportWebDisplay {
	
	private String annotationName;
	private int annotationTypeId;
	
	private String annotationFilterDirection;

	private String cutoffValue;
	private Double cutoffValueDouble;

	private boolean peptideCutoff;
	
	
	public String getAnnotationName() {
		return annotationName;
	}
	public void setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
	}
	public String getCutoffValue() {
		return cutoffValue;
	}
	public void setCutoffValue(String cutoffValue) {
		this.cutoffValue = cutoffValue;
	}
	public boolean isPeptideCutoff() {
		return peptideCutoff;
	}
	public void setPeptideCutoff(boolean peptideCutoff) {
		this.peptideCutoff = peptideCutoff;
	}
	public int getAnnotationTypeId() {
		return annotationTypeId;
	}
	public void setAnnotationTypeId(int annotationTypeId) {
		this.annotationTypeId = annotationTypeId;
	}
	public Double getCutoffValueDouble() {
		return cutoffValueDouble;
	}
	public void setCutoffValueDouble(Double cutoffValueDouble) {
		this.cutoffValueDouble = cutoffValueDouble;
	}
	public String getAnnotationFilterDirection() {
		return annotationFilterDirection;
	}
	public void setAnnotationFilterDirection(String annotationFilterDirection) {
		this.annotationFilterDirection = annotationFilterDirection;
	}

}
