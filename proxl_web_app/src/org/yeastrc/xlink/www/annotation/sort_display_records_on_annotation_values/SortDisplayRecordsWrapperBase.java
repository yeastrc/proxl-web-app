package org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values;

import java.util.List;
import java.util.Map;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;

/**
 * Base class that web display classes wrappers must extend to use SortDisplayRecordsOnAnnotationValues
 */
public abstract class SortDisplayRecordsWrapperBase {
	
	/**
	 * PSM annotation data 
	 * 
	 * Map keyed on annotation type id of annotation data 
	 */
	private Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap;
	/**
	 * Peptide annotation data
	 * 
	 * Map keyed on annotation type id of annotation data 
	 */
	private Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap;
	/////////////
	//  Abstact methods for accessing lists in inherited classes 
	public abstract List<String> getPsmAnnotationValueList();
	public abstract void setPsmAnnotationValueList(List<String> psmAnnotationValueList);
	public abstract List<String> getPeptideAnnotationValueList();
	public abstract void setPeptideAnnotationValueList(List<String> peptideAnnotationValueList);
	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	public abstract int getFinalSortOrderKey();
	////////
	///  Setters and Getters
	public Map<Integer, AnnotationDataBaseDTO> getPsmAnnotationDTOMap() {
		return psmAnnotationDTOMap;
	}
	public void setPsmAnnotationDTOMap(
			Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap) {
		this.psmAnnotationDTOMap = psmAnnotationDTOMap;
	}
	public Map<Integer, AnnotationDataBaseDTO> getPeptideAnnotationDTOMap() {
		return peptideAnnotationDTOMap;
	}
	public void setPeptideAnnotationDTOMap(
			Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap) {
		this.peptideAnnotationDTOMap = peptideAnnotationDTOMap;
	}
}
