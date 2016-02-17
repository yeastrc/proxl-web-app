package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.dto.AnnotationTypeDTO;

/**
 * 
 *
 */
public class AnnotationTypeDTOListForSearchId {
	
	int searchId;

	List<AnnotationTypeDTO> annotationTypeDTOList;

	
	
	public int getSearchId() {
		return searchId;
	}

	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	public List<AnnotationTypeDTO> getAnnotationTypeDTOList() {
		return annotationTypeDTOList;
	}

	public void setAnnotationTypeDTOList(
			List<AnnotationTypeDTO> annotationTypeDTOList) {
		this.annotationTypeDTOList = annotationTypeDTOList;
	}
}
