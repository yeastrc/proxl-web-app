package org.yeastrc.xlink.dto;

/**
 * table psm_filterable_annotation__generic_lookup
 *
 */
public class PsmFilterableAnnotationGenericLookupDTO {

	private int psmAnnotationId;
	private int psmId;
	private int annotationTypeId;
	private double valueDouble;
	private String valueString;
	
	private int searchId;
	private int type;
	private int reportedPeptideId;
	
	
	public int getPsmAnnotationId() {
		return psmAnnotationId;
	}
	public void setPsmAnnotationId(int psmAnnotationId) {
		this.psmAnnotationId = psmAnnotationId;
	}
	public int getPsmId() {
		return psmId;
	}
	public void setPsmId(int psmId) {
		this.psmId = psmId;
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
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getReportedPeptideId() {
		return reportedPeptideId;
	}
	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}


}
