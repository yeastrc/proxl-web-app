package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs;

import java.math.BigDecimal;

import org.yeastrc.xlink.enum_classes.FilterDirectionType;


public class DropPeptidePSMCutoffValue {

	private String annotationName;
	private BigDecimal cutoffValue;
	
	private FilterDirectionType annotationTypeFilterDirection;
	
	public String getAnnotationName() {
		return annotationName;
	}
	public void setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
	}
	public BigDecimal getCutoffValue() {
		return cutoffValue;
	}
	public void setCutoffValue(BigDecimal cutoffValue) {
		this.cutoffValue = cutoffValue;
	}
	public FilterDirectionType getAnnotationTypeFilterDirection() {
		return annotationTypeFilterDirection;
	}
	public void setAnnotationTypeFilterDirection(
			FilterDirectionType annotationTypeFilterDirection) {
		this.annotationTypeFilterDirection = annotationTypeFilterDirection;
	}
}
