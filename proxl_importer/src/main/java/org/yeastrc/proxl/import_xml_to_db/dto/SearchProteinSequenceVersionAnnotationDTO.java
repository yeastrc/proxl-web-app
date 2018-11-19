package org.yeastrc.proxl.import_xml_to_db.dto;

/**
 * 
 * table search__protein_sequence_version__annotation
 */
public class SearchProteinSequenceVersionAnnotationDTO {

	private int id;
	private int searchId;
	private int proteinSequenceVersionId;
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
	public int getAnnotationId() {
		return annotationId;
	}
	public void setAnnotationId(int annotationId) {
		this.annotationId = annotationId;
	}
	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}
	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}

}
