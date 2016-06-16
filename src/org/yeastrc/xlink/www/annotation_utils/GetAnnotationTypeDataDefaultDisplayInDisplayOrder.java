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
public class GetAnnotationTypeDataDefaultDisplayInDisplayOrder {


	private static final Logger log = Logger.getLogger(GetAnnotationTypeDataDefaultDisplayInDisplayOrder.class);


	/**
	 * Static get instance
	 * @return
	 */
	public static GetAnnotationTypeDataDefaultDisplayInDisplayOrder getInstance() {
		return new GetAnnotationTypeDataDefaultDisplayInDisplayOrder(); 
	}
	
	//  constructor
	private GetAnnotationTypeDataDefaultDisplayInDisplayOrder() { }
	
	
	/**
	 * PSM AnnotationTypeDTO keyed by searchId, lists sorted by display order
	 * 
	 * @param searchIds
	 * @return map keyed on search id
	 * @throws Exception
	 */
	public Map<Integer, AnnotationTypeDTOListForSearchId>  getPsmAnnotationTypeDataDefaultDisplayInDisplayOrder( Collection<Integer> searchIdsCollection ) throws Exception {

		return _getAnnotationTypeDataInDefaultDisplayInDisplayOrder( searchIdsCollection, PsmPeptideAnnotationType.PSM );
	}
	

	/**
	 * Peptide AnnotationTypeDTO keyed by searchId, lists sorted by display order
	 * 
	 * @param searchIds
	 * @return map keyed on search id
	 * @throws Exception
	 */
	public Map<Integer, AnnotationTypeDTOListForSearchId>  getPeptide_AnnotationTypeDataDefaultDisplayInDisplayOrder( Collection<Integer> searchIdsCollection ) throws Exception {

		return _getAnnotationTypeDataInDefaultDisplayInDisplayOrder( searchIdsCollection, PsmPeptideAnnotationType.PEPTIDE );
	}
	
	
	
	/**
	 * @param searchIdsCollection
	 * @param psmPeptideAnnotationType
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, AnnotationTypeDTOListForSearchId>  _getAnnotationTypeDataInDefaultDisplayInDisplayOrder( 
			
			Collection<Integer> searchIdsCollection,
			PsmPeptideAnnotationType psmPeptideAnnotationType
			) throws Exception {


		// get rid of dupes
		Set<Integer> searchIdsSet = new HashSet<>( searchIdsCollection );
		
		//  put in list so can sort
		List<Integer> searchIdsList = new ArrayList<>( searchIdsSet );
		
		Collections.sort( searchIdsList );
		
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeDataAllSearchIdsFilterable = 
				GetAnnotationTypeData.getInstance()
				.getAnnTypeForSearchIdsFiltDescPsmPeptide( 
						searchIdsList, 
						FilterableDescriptiveAnnotationType.FILTERABLE, 
						psmPeptideAnnotationType );
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeDataAllSearchIdsDescriptive = 
				GetAnnotationTypeData.getInstance()
				.getAnnTypeForSearchIdsFiltDescPsmPeptide( 
						searchIdsList, 
						FilterableDescriptiveAnnotationType.DESCRIPTIVE, 
						psmPeptideAnnotationType );

		
		Map<Integer, AnnotationTypeDTOListForSearchId> annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap = new TreeMap<>();

		for ( Integer searchId : searchIdsList ) {

			Map<Integer, AnnotationTypeDTO> annotationTypeDataFilterable = annotationTypeDataAllSearchIdsFilterable.get( searchId );

			Map<Integer, AnnotationTypeDTO> annotationTypeDataDescriptive = annotationTypeDataAllSearchIdsDescriptive.get( searchId );
			

			if ( annotationTypeDataFilterable == null ) {
				
				if ( psmPeptideAnnotationType == PsmPeptideAnnotationType.PSM ) {

					String msg = "No filterable annotation type records found for psmPeptide: " + psmPeptideAnnotationType.value()
							+ ", searchId: " + searchId;
					log.warn( msg );
				}

				//   Create empty list and put in map

				List<AnnotationTypeDTO> annotationTypeDTO_SortOrder_PerSearchIdList = new ArrayList<>( );

				AnnotationTypeDTOListForSearchId annotationTypeDTOListForSearchId = new AnnotationTypeDTOListForSearchId();

				annotationTypeDTOListForSearchId.setSearchId( searchId );
				annotationTypeDTOListForSearchId.setAnnotationTypeDTOList( annotationTypeDTO_SortOrder_PerSearchIdList );

				annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.put( searchId, annotationTypeDTOListForSearchId );
				
				continue;  //  EARLY Continue  
			}

			
			List<AnnotationTypeDTO> annotationTypeDTO_DefaultDisplay_DisplayOrder_PerSearchIdList = 
					new ArrayList<>( annotationTypeDataFilterable.size() * 2 );

			
			
			for ( Map.Entry<Integer, AnnotationTypeDTO> entry : annotationTypeDataFilterable.entrySet() ) {

				AnnotationTypeDTO item = entry.getValue();

				if ( item.isDefaultVisible() ) {
					
					annotationTypeDTO_DefaultDisplay_DisplayOrder_PerSearchIdList.add( item );
				}
			}
			

			if ( annotationTypeDataDescriptive != null ) {

				for ( Map.Entry<Integer, AnnotationTypeDTO> entry : annotationTypeDataDescriptive.entrySet() ) {

					AnnotationTypeDTO item = entry.getValue();

					if ( item.isDefaultVisible() ) {

						annotationTypeDTO_DefaultDisplay_DisplayOrder_PerSearchIdList.add( item );
					}
				}
			}

			//  Sort Ann Type records on display order


			Collections.sort( annotationTypeDTO_DefaultDisplay_DisplayOrder_PerSearchIdList, new Comparator<AnnotationTypeDTO>() {

				@Override
				public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {

					if ( o1.getDisplayOrder() == null && o2.getDisplayOrder() == null ) {
						
						return o1.getName().compareTo( o2.getName() );
					}
					
					if ( o1.getDisplayOrder() == null ) {
						
						return 1;  // sort o1 after o2 
					}

					if ( o2.getDisplayOrder() == null ) {
						
						return 1;  // sort o1 before o2 
					}
					
					return o1.getDisplayOrder() - o2.getDisplayOrder();
				}
			});
			
			AnnotationTypeDTOListForSearchId annotationTypeDTOListForSearchId = new AnnotationTypeDTOListForSearchId();

			annotationTypeDTOListForSearchId.setSearchId( searchId );
			annotationTypeDTOListForSearchId.setAnnotationTypeDTOList( annotationTypeDTO_DefaultDisplay_DisplayOrder_PerSearchIdList );
			
			annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.put( searchId, annotationTypeDTOListForSearchId );
		}
		
		return annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap;
	}
	

}
