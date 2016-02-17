package org.yeastrc.xlink.www.annotation_utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;

/**
 * 
 *
 */
public class GetAnnotationTypeDataInSortOrder {


	private static final Logger log = Logger.getLogger(GetAnnotationTypeDataInSortOrder.class);


	/**
	 * Static get instance
	 * @return
	 */
	public static GetAnnotationTypeDataInSortOrder getInstance() {
		return new GetAnnotationTypeDataInSortOrder(); 
	}
	
	//  constructor
	private GetAnnotationTypeDataInSortOrder() { }
	
	
	/**
	 * PSM AnnotationTypeDTO keyed by searchId, lists sorted by sort order
	 * 
	 * @param searchIds
	 * @return map keyed on search id
	 * @throws Exception
	 */
	public Map<Integer, AnnotationTypeDTOListForSearchId>  getPsmAnnotationTypeDataInSortOrder( Collection<Integer> searchIdsCollection ) throws Exception {

		return _getAnnotationTypeDataInSortOrder( searchIdsCollection, PsmPeptideAnnotationType.PSM );
	}
	

	/**
	 * Peptide AnnotationTypeDTO keyed by searchId, lists sorted by sort order
	 * 
	 * @param searchIds
	 * @return map keyed on search id
	 * @throws Exception
	 */
	public Map<Integer, AnnotationTypeDTOListForSearchId>  getPeptide_AnnotationTypeDataInSortOrder( Collection<Integer> searchIdsCollection ) throws Exception {

		return _getAnnotationTypeDataInSortOrder( searchIdsCollection, PsmPeptideAnnotationType.PEPTIDE );
	}
	
	
	
	/**
	 * @param searchIdsCollection
	 * @param psmPeptideAnnotationType
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, AnnotationTypeDTOListForSearchId>  _getAnnotationTypeDataInSortOrder( 
			
			Collection<Integer> searchIdsCollection,
			PsmPeptideAnnotationType psmPeptideAnnotationType
			) throws Exception {


		// get rid of dupes
		Set<Integer> searchIdsSet = new HashSet<>( searchIdsCollection );
		
		//  put in list so can sort
		List<Integer> searchIdsList = new ArrayList<>( searchIdsSet );
		
		Collections.sort( searchIdsList );
		
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeDataAllSearchIds = 
				GetAnnotationTypeData.getInstance()
				.getAnnTypeForSearchIdsFiltDescPsmPeptide( 
						searchIdsList, 
						FilterableDescriptiveAnnotationType.FILTERABLE, 
						psmPeptideAnnotationType );
		
		Map<Integer, AnnotationTypeDTOListForSearchId> annotationTypeDTO_SortOrder_MainMap = new TreeMap<>();

		for ( Integer searchId : searchIdsList ) {

			Map<Integer, AnnotationTypeDTO> annotationTypeData = annotationTypeDataAllSearchIds.get( searchId );
			
			if ( annotationTypeData == null ) {
				
				String msg = "No annotations found for psmPeptide: " + psmPeptideAnnotationType.value()
						+ ", searchId: " + searchId;
				log.warn( msg );

				//  TODO  probably should throw exception
				
				continue;  //  EARLY Continue  
			}
			
			List<AnnotationTypeDTO> annotationTypeDTO_SortOrder_PerSearchIdList = new ArrayList<>( annotationTypeData.size() );

			for ( Map.Entry<Integer, AnnotationTypeDTO> entry : annotationTypeData.entrySet() ) {

				AnnotationTypeDTO item = entry.getValue();

				if ( item.getAnnotationTypeFilterableDTO() != null 
						&& item.getAnnotationTypeFilterableDTO().getSortOrder() != null ) {

					annotationTypeDTO_SortOrder_PerSearchIdList.add( item );
				}
			}

			//  Sort Ann Type records on sort order


			Collections.sort( annotationTypeDTO_SortOrder_PerSearchIdList, new Comparator<AnnotationTypeDTO>() {

				@Override
				public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {

					return o1.getAnnotationTypeFilterableDTO().getSortOrder() - o2.getAnnotationTypeFilterableDTO().getSortOrder();
				}
			});
			
			AnnotationTypeDTOListForSearchId annotationTypeDTOListForSearchId = new AnnotationTypeDTOListForSearchId();

			annotationTypeDTOListForSearchId.setSearchId( searchId );
			annotationTypeDTOListForSearchId.setAnnotationTypeDTOList( annotationTypeDTO_SortOrder_PerSearchIdList );
			
			annotationTypeDTO_SortOrder_MainMap.put( searchId, annotationTypeDTOListForSearchId );
		}
		
		return annotationTypeDTO_SortOrder_MainMap;
	}
	

}
