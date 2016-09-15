package org.yeastrc.xlink.www.form_page_objects;

/**
 * Display of cutoff data per annotation
 *
 */
public class CutoffPageDisplayAnnotationLevel {

	private int annotationTypeId;
	private String annotationName;
	private String annotationDescription;
	
	private String annotationDefaultValue;
	
	private Integer sortOrder;
	private Integer displayOrder;
	
	
	private String searchProgramDisplayName;
	
//	/**
//	 * This annotation name is duplicate across search programs
//	 */
//	private boolean annNameDupsAcrossSrchPgms;
//	
//	
//	/**
//	 * This annotation name is duplicate across search programs
//	 * @return
//	 */
//	public boolean isAnnNameDupsAcrossSrchPgms() {
//		return annNameDupsAcrossSrchPgms;
//	}
//	/**
//	 * This annotation name is duplicate across search programs
//	 * @param annNameDupsAcrossSrchPgms
//	 */
//	public void setAnnNameDupsAcrossSrchPgms(boolean annNameDupsAcrossSrchPgms) {
//		this.annNameDupsAcrossSrchPgms = annNameDupsAcrossSrchPgms;
//	}
	
	
	public String getSearchProgramDisplayName() {
		return searchProgramDisplayName;
	}
	public void setSearchProgramDisplayName(String searchProgramDisplayName) {
		this.searchProgramDisplayName = searchProgramDisplayName;
	}

	public String getAnnotationDefaultValue() {
		return annotationDefaultValue;
	}
	public void setAnnotationDefaultValue(String annotationDefaultValue) {
		this.annotationDefaultValue = annotationDefaultValue;
	}
	public String getAnnotationDescription() {
		return annotationDescription;
	}
	public void setAnnotationDescription(String annotationDescription) {
		this.annotationDescription = annotationDescription;
	}
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getAnnotationName() {
		return annotationName;
	}
	public void setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
	}
	public int getAnnotationTypeId() {
		return annotationTypeId;
	}
	public void setAnnotationTypeId(int annotationTypeId) {
		this.annotationTypeId = annotationTypeId;
	}

}
