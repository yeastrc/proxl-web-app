package org.yeastrc.proxl.import_xml_to_db.dto;

/**
 * table cutoffs_applied_on_import
 *
 */
public class CutoffsAppliedOnImportDTO {

	private int id;
	private int searchId;
	private int annotationTypeId;
	private String cutoffValueString;
	private Double cutoffValueDouble;
	
	@Override
	public String toString() {
		return "CutoffsAppliedOnImportDTO [id=" + id + ", searchId=" + searchId
				+ ", annotationTypeId=" + annotationTypeId
				+ ", cutoffValueString=" + cutoffValueString
				+ ", cutoffValueDouble=" + cutoffValueDouble + "]";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getAnnotationTypeId() {
		return annotationTypeId;
	}
	public void setAnnotationTypeId(int annotationTypeId) {
		this.annotationTypeId = annotationTypeId;
	}
	public String getCutoffValueString() {
		return cutoffValueString;
	}
	public void setCutoffValueString(String cutoffValueString) {
		this.cutoffValueString = cutoffValueString;
	}
	public Double getCutoffValueDouble() {
		return cutoffValueDouble;
	}
	public void setCutoffValueDouble(Double cutoffValueDouble) {
		this.cutoffValueDouble = cutoffValueDouble;
	}

}
