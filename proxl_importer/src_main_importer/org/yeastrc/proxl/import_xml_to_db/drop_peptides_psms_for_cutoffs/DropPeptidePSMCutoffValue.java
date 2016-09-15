package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs;

import java.math.BigDecimal;

import org.yeastrc.xlink.enum_classes.FilterDirectionType;


public class DropPeptidePSMCutoffValue {

	/**
	 * Not initially populated for cutoffs from the command line
	 */
	private String searchProgramName;
	
	private String annotationName;
	private BigDecimal cutoffValue;
	
	private FilterDirectionType annotationTypeFilterDirection;

	
	
	//  equals and hashCode based on searchProgramName, annotationName

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotationName == null) ? 0 : annotationName.hashCode());
		result = prime
				* result
				+ ((searchProgramName == null) ? 0 : searchProgramName
						.hashCode());
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
		DropPeptidePSMCutoffValue other = (DropPeptidePSMCutoffValue) obj;
		if (annotationName == null) {
			if (other.annotationName != null)
				return false;
		} else if (!annotationName.equals(other.annotationName))
			return false;
		if (searchProgramName == null) {
			if (other.searchProgramName != null)
				return false;
		} else if (!searchProgramName.equals(other.searchProgramName))
			return false;
		return true;
	}
	
	/**
	 * Not initially populated for cutoffs from the command line
	 * @return
	 */
	public String getSearchProgramName() {
		return searchProgramName;
	}
	/**
	 * Not initially populated for cutoffs from the command line
	 * @param searchProgramName
	 */
	public void setSearchProgram(String searchProgramName) {
		this.searchProgramName = searchProgramName;
	}

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
