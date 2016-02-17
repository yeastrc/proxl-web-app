package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;


/**
 * table annotation_type
 *
 */
public class AnnotationTypeDTO {

	private int id;
	private int searchId;
	private int searchProgramsPerSearchId;
	
	private PsmPeptideAnnotationType psmPeptideAnnotationType;
	private FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType;
	
	private String name;

	private boolean defaultVisible;
	private Integer displayOrder;

	private String description;
	
	
	private AnnotationTypeFilterableDTO annotationTypeFilterableDTO;
	

	public AnnotationTypeFilterableDTO getAnnotationTypeFilterableDTO() {
		return annotationTypeFilterableDTO;
	}

	public void setAnnotationTypeFilterableDTO(
			AnnotationTypeFilterableDTO annotationTypeFilterableDTO) {
		this.annotationTypeFilterableDTO = annotationTypeFilterableDTO;
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

	public int getSearchProgramsPerSearchId() {
		return searchProgramsPerSearchId;
	}

	public void setSearchProgramsPerSearchId(int searchProgramsPerSearchId) {
		this.searchProgramsPerSearchId = searchProgramsPerSearchId;
	}

	public PsmPeptideAnnotationType getPsmPeptideAnnotationType() {
		return psmPeptideAnnotationType;
	}

	public void setPsmPeptideAnnotationType(
			PsmPeptideAnnotationType psmPeptideAnnotationType) {
		this.psmPeptideAnnotationType = psmPeptideAnnotationType;
	}

	public FilterableDescriptiveAnnotationType getFilterableDescriptiveAnnotationType() {
		return filterableDescriptiveAnnotationType;
	}

	public void setFilterableDescriptiveAnnotationType(
			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType) {
		this.filterableDescriptiveAnnotationType = filterableDescriptiveAnnotationType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefaultVisible() {
		return defaultVisible;
	}

	public void setDefaultVisible(boolean defaultVisible) {
		this.defaultVisible = defaultVisible;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
