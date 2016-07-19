package org.yeastrc.proxl.import_xml_to_db.dto;

/**
 * 
 * table search_protein_sequence_annotation
 */
public class SearchProteinSequenceAnnotationDTO {

	private int id;
	private int searchId;
	private int proteinSequenceId;
	private int annotationId;
	
	
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
	public int getProteinSequenceId() {
		return proteinSequenceId;
	}
	public void setProteinSequenceId(int proteinSequenceId) {
		this.proteinSequenceId = proteinSequenceId;
	}
	public int getAnnotationId() {
		return annotationId;
	}
	public void setAnnotationId(int annotationId) {
		this.annotationId = annotationId;
	}

}
