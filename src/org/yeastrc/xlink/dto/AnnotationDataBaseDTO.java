package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;

/**
 * Base Class for PsmAnnotationDTO and 
 *
 */
public class AnnotationDataBaseDTO {

	protected int id;
	protected FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType;
	protected int annotationTypeId;
	protected double valueDouble;
	protected String valueString;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public FilterableDescriptiveAnnotationType getFilterableDescriptiveAnnotationType() {
		return filterableDescriptiveAnnotationType;
	}
	public void setFilterableDescriptiveAnnotationType(
			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType) {
		this.filterableDescriptiveAnnotationType = filterableDescriptiveAnnotationType;
	}
	public int getAnnotationTypeId() {
		return annotationTypeId;
	}
	public void setAnnotationTypeId(int annotationTypeId) {
		this.annotationTypeId = annotationTypeId;
	}
	public double getValueDouble() {
		return valueDouble;
	}
	public void setValueDouble(double valueDouble) {
		this.valueDouble = valueDouble;
	}
	public String getValueString() {
		return valueString;
	}
	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

}