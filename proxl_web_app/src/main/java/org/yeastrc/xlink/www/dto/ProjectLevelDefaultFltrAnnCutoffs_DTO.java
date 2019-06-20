package org.yeastrc.xlink.www.dto;

import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;

/**
 * project_level_default_fltr_ann_cutoffs_tbl table
 *
 */
public class ProjectLevelDefaultFltrAnnCutoffs_DTO {

	private int id;
	private int projectId;
	private PsmPeptideAnnotationType psmPeptideAnnotationType;
	private String searchProgramName;
	private String annotationTypeName;
	private double annotationCutoffValue;
	private int createdAuthUserId;
	private int lastUpdatedAuthUserId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getSearchProgramName() {
		return searchProgramName;
	}
	public void setSearchProgramName(String searchProgramName) {
		this.searchProgramName = searchProgramName;
	}
	public String getAnnotationTypeName() {
		return annotationTypeName;
	}
	public void setAnnotationTypeName(String annotationTypeName) {
		this.annotationTypeName = annotationTypeName;
	}
	public double getAnnotationCutoffValue() {
		return annotationCutoffValue;
	}
	public void setAnnotationCutoffValue(double annotationCutoffValue) {
		this.annotationCutoffValue = annotationCutoffValue;
	}
	public PsmPeptideAnnotationType getPsmPeptideAnnotationType() {
		return psmPeptideAnnotationType;
	}
	public void setPsmPeptideAnnotationType(PsmPeptideAnnotationType psmPeptideAnnotationType) {
		this.psmPeptideAnnotationType = psmPeptideAnnotationType;
	}
	public int getCreatedAuthUserId() {
		return createdAuthUserId;
	}
	public void setCreatedAuthUserId(int createdAuthUserId) {
		this.createdAuthUserId = createdAuthUserId;
	}
	public int getLastUpdatedAuthUserId() {
		return lastUpdatedAuthUserId;
	}
	public void setLastUpdatedAuthUserId(int lastUpdatedAuthUserId) {
		this.lastUpdatedAuthUserId = lastUpdatedAuthUserId;
	}
}
