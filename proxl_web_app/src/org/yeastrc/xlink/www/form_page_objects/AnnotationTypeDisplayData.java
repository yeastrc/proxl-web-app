package org.yeastrc.xlink.www.form_page_objects;

import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;

/**
 * Display object for a single annotation type
 *
 */
public class AnnotationTypeDisplayData {

	private AnnotationTypeDTO annotationTypeDTO;
	private SearchProgramsPerSearchDTO searchProgramPerSearchDTO;
	
	public AnnotationTypeDTO getAnnotationTypeDTO() {
		return annotationTypeDTO;
	}
	public void setAnnotationTypeDTO(AnnotationTypeDTO annotationTypeDTO) {
		this.annotationTypeDTO = annotationTypeDTO;
	}
	public SearchProgramsPerSearchDTO getSearchProgramPerSearchDTO() {
		return searchProgramPerSearchDTO;
	}
	public void setSearchProgramPerSearchDTO(
			SearchProgramsPerSearchDTO searchProgramPerSearchDTO) {
		this.searchProgramPerSearchDTO = searchProgramPerSearchDTO;
	}
}
