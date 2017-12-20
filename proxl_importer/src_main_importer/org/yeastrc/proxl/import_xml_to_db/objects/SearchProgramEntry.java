package org.yeastrc.proxl.import_xml_to_db.objects;

import java.util.Map;

import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;

/**
 * 
 *
 */
public class SearchProgramEntry {

	private SearchProgramsPerSearchDTO searchProgramsPerSearchDTO;
	
	
	/**
	 * Key is name
	 */
	private Map<String, AnnotationTypeDTO> reportedPeptideAnnotationTypeDTOMap;

	/**
	 * Key is name
	 */
	private Map<String, AnnotationTypeDTO> psmAnnotationTypeDTOMap;

	/**
	 * Key is name
	 */
	private Map<String, AnnotationTypeDTO> psmPerPeptideAnnotationTypeDTOMap;

	
	
	
	
	public SearchProgramsPerSearchDTO getSearchProgramsPerSearchDTO() {
		return searchProgramsPerSearchDTO;
	}

	public void setSearchProgramsPerSearchDTO(
			SearchProgramsPerSearchDTO searchProgramsPerSearchDTO) {
		this.searchProgramsPerSearchDTO = searchProgramsPerSearchDTO;
	}

	public Map<String, AnnotationTypeDTO> getReportedPeptideAnnotationTypeDTOMap() {
		return reportedPeptideAnnotationTypeDTOMap;
	}

	public void setReportedPeptideAnnotationTypeDTOMap(
			Map<String, AnnotationTypeDTO> reportedPeptideAnnotationTypeDTOMap) {
		this.reportedPeptideAnnotationTypeDTOMap = reportedPeptideAnnotationTypeDTOMap;
	}

	public Map<String, AnnotationTypeDTO> getPsmAnnotationTypeDTOMap() {
		return psmAnnotationTypeDTOMap;
	}

	public void setPsmAnnotationTypeDTOMap(
			Map<String, AnnotationTypeDTO> psmAnnotationTypeDTOMap) {
		this.psmAnnotationTypeDTOMap = psmAnnotationTypeDTOMap;
	}

	public Map<String, AnnotationTypeDTO> getPsmPerPeptideAnnotationTypeDTOMap() {
		return psmPerPeptideAnnotationTypeDTOMap;
	}

	public void setPsmPerPeptideAnnotationTypeDTOMap(Map<String, AnnotationTypeDTO> psmPerPeptideAnnotationTypeDTOMap) {
		this.psmPerPeptideAnnotationTypeDTOMap = psmPerPeptideAnnotationTypeDTOMap;
	}
		
}
