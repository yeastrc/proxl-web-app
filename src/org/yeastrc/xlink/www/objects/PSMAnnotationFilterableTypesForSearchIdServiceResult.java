package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.dto.AnnotationTypeDTO;

/**
 * result from PSMAnnotationFilterableTypesForSearchIdService
 *
 */
public class PSMAnnotationFilterableTypesForSearchIdServiceResult {

	private List<AnnotationTypeDTO> annotationTypeDTOList;

	public List<AnnotationTypeDTO> getAnnotationTypeDTOList() {
		return annotationTypeDTOList;
	}

	public void setAnnotationTypeDTOList(
			List<AnnotationTypeDTO> annotationTypeDTOList) {
		this.annotationTypeDTOList = annotationTypeDTOList;
	}
}
