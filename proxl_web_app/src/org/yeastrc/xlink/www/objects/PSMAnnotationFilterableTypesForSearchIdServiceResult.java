package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;

/**
 * result from PSMAnnotationFilterableTypesForSearchIdService
 *
 */
public class PSMAnnotationFilterableTypesForSearchIdServiceResult {

	private List<PSMAnnotationFilterableTypesForSearchIdServiceResultEntry> annotationTypeList;

	
	public List<PSMAnnotationFilterableTypesForSearchIdServiceResultEntry> getAnnotationTypeList() {
		return annotationTypeList;
	}


	public void setAnnotationTypeList(
			List<PSMAnnotationFilterableTypesForSearchIdServiceResultEntry> annotationTypeList) {
		this.annotationTypeList = annotationTypeList;
	}


	public static class PSMAnnotationFilterableTypesForSearchIdServiceResultEntry {
		
		private AnnotationTypeDTO annotationTypeDTO;
		private SearchProgramsPerSearchDTO searchProgramsPerSearchDTO;
		
		
		public AnnotationTypeDTO getAnnotationTypeDTO() {
			return annotationTypeDTO;
		}
		public void setAnnotationTypeDTO(AnnotationTypeDTO annotationTypeDTO) {
			this.annotationTypeDTO = annotationTypeDTO;
		}
		public SearchProgramsPerSearchDTO getSearchProgramsPerSearchDTO() {
			return searchProgramsPerSearchDTO;
		}
		public void setSearchProgramsPerSearchDTO(
				SearchProgramsPerSearchDTO searchProgramsPerSearchDTO) {
			this.searchProgramsPerSearchDTO = searchProgramsPerSearchDTO;
		}
		
	}
}
