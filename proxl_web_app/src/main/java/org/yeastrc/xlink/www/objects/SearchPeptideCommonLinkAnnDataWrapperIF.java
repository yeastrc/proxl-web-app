package org.yeastrc.xlink.www.objects;

import java.util.Map;

import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;

/**
 * Common methods across the SearchPeptide...linkAnnDataWrapper classes
 *   for Crosslink, Looplink and Monolink
 *
 */
public interface SearchPeptideCommonLinkAnnDataWrapperIF {

	/**
	 * @return
	 */
	public int getReportedPeptideId() throws Exception;
	

	/**
	 * From base class of SearchPeptide...linkAnnDataWrapper:  SortDisplayRecordsWrapperBase
	 * 
	 * @return
	 */
	public Map<Integer, AnnotationDataBaseDTO> getPsmAnnotationDTOMap();


	/**
	 * From base class of SearchPeptide...linkAnnDataWrapper:  SortDisplayRecordsWrapperBase
	 * 
	 * @param psmAnnotationDTOMap
	 */
	public void setPsmAnnotationDTOMap(
			Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap);


	/**
	 * From base class of SearchPeptide...linkAnnDataWrapper:  SortDisplayRecordsWrapperBase
	 * 
	 * @return
	 */
	public Map<Integer, AnnotationDataBaseDTO> getPeptideAnnotationDTOMap();

	/**
	 * From base class of SearchPeptide...linkAnnDataWrapper:  SortDisplayRecordsWrapperBase
	 * 
	 * @param peptideAnnotationDTOMap
	 */
	public void setPeptideAnnotationDTOMap(
			Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap);

	
}
