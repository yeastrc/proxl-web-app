package org.yeastrc.xlink.www.objects;

/**
 * Display of data from table cutoffs_applied_on_import combined with annotation_type
 *
 */
public class CutoffsAppliedOnImportWebDisplay {
	
	private String annotationName;
	private String cutoffValue;
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

}
