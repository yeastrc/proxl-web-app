package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataDefaultDisplayInDisplayOrder;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.form_page_objects.AnnotationTypeDisplayData;
import org.yeastrc.xlink.www.objects.AnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.objects.AnnotationDisplayUserSelectionDetailsData.DefaultAnnTypeIdDisplay;
import org.yeastrc.xlink.www.objects.AnnotationDisplayUserSelectionDetailsPerSearch;
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;
import org.yeastrc.xlink.www.objects.AnnotationDisplayUserSelectionDetailsData.DefaultAnnTypesPerSearch;
import org.yeastrc.xlink.www.search_programs_per_search_utils.GetSearchProgramsPerSearchData;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is for putting data in the "request" scope for the 
 * Annotation Types Details for User selection of which annotation types are displayed
 *
 */
public class GetAnnotationDisplayUserSelectionDetailsData {

	private static final Logger log = Logger.getLogger( GetAnnotationDisplayUserSelectionDetailsData.class );
			
	private static final GetAnnotationDisplayUserSelectionDetailsData instance = new GetAnnotationDisplayUserSelectionDetailsData();
	
	private GetAnnotationDisplayUserSelectionDetailsData() { }
	public static GetAnnotationDisplayUserSelectionDetailsData getInstance() { return instance; }


	/**
	 * @param search
	 * @param request
	 * @throws Exception
	 */
	public void getSearchDetailsData( SearchDTO search, HttpServletRequest request ) throws Exception {

		List<SearchDTO> searches = new ArrayList<>( 1 );
		searches.add(search);
		
		getSearchDetailsData( searches, request );
	}
		

	/**
	 * @param searches
	 * @param request
	 * @throws Exception
	 */
	public void getSearchDetailsData( List<SearchDTO> searches, HttpServletRequest request ) throws Exception {
		
		//  Jackson JSON Mapper object for JSON deserialization and serialization
		
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

		
		AnnotationDisplayUserSelectionDetailsData annotationDisplayUserSelectionDetailsData = new AnnotationDisplayUserSelectionDetailsData();
		
		List<AnnotationDisplayUserSelectionDetailsPerSearch> annotationDisplayUserSelectionDetailsPerSearchList = new ArrayList<>( searches.size() );
		annotationDisplayUserSelectionDetailsData.setAnnotationDisplayUserSelectionDetailsPerSearchList( annotationDisplayUserSelectionDetailsPerSearchList );
		
		DefaultAnnTypeIdDisplay defaultAnnTypeIdDisplay = new DefaultAnnTypeIdDisplay();
		annotationDisplayUserSelectionDetailsData.setDefaultAnnTypeIdDisplay( defaultAnnTypeIdDisplay );
		
		Map<Integer, DefaultAnnTypesPerSearch> defaultAnnTypesPerSearchKeySearchIdMap = new HashMap<>();
		defaultAnnTypeIdDisplay.setSearches( defaultAnnTypesPerSearchKeySearchIdMap );
		
		Set<Integer> searchIdsSet = new HashSet<>( );

		for ( SearchDTO search : searches ) {
		
			searchIdsSet.add( search.getId() );
		}

		Map<Integer, AnnotationTypeDTOListForSearchId> psmAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap = 
				GetAnnotationTypeDataDefaultDisplayInDisplayOrder.getInstance()
				.getPsmAnnotationTypeDataDefaultDisplayInDisplayOrder( searchIdsSet );

		Map<Integer, AnnotationTypeDTOListForSearchId> peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap = 
				GetAnnotationTypeDataDefaultDisplayInDisplayOrder.getInstance()
				.getPeptideAnnotationTypeDataDefaultDisplayInDisplayOrder( searchIdsSet );
		
		
		Map<Integer, List<AnnotationTypeDisplayData>> psmAnnotationDisplayDataAllSearchIds = 
				getForAnnType( PsmPeptideAnnotationType.PSM, searchIdsSet );

		Map<Integer, List<AnnotationTypeDisplayData>> peptideAnnotationDisplayDataAllSearchIds = 
				getForAnnType( PsmPeptideAnnotationType.PEPTIDE, searchIdsSet );
		
		for ( SearchDTO search : searches ) {
		
			Integer searchId = search.getId();
			
			//  Populate Default Display Data
			
			List<Integer> psmDefaultAnnotationTypeIdList = 
					getAnnotationTypeIdListFrom_AnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap( 
							"PSM", searchId, psmAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap );
			
			List<Integer> peptideDefaultAnnotationTypeIdList =
					getAnnotationTypeIdListFrom_AnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap( 
							"Peptide", searchId, peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap );
			
			DefaultAnnTypesPerSearch defaultAnnTypesPerSearch = new DefaultAnnTypesPerSearch();
			
			defaultAnnTypesPerSearch.setPsmDefaultAnnotationTypeIdList( psmDefaultAnnotationTypeIdList );
			defaultAnnTypesPerSearch.setPeptideDefaultAnnotationTypeIdList( peptideDefaultAnnotationTypeIdList );
			
			defaultAnnTypesPerSearchKeySearchIdMap.put( searchId, defaultAnnTypesPerSearch );
			
			////////////////
			
			//  Populate All Annotation Type Data
			
			AnnotationDisplayUserSelectionDetailsPerSearch annotationDisplayUserSelectionDetailsPerSearch = new AnnotationDisplayUserSelectionDetailsPerSearch();
			
			annotationDisplayUserSelectionDetailsPerSearch.setSearchDTO(search);

			
			List<AnnotationTypeDisplayData> psmAnnotationTypeDisplayData =
					psmAnnotationDisplayDataAllSearchIds.get( searchId );

			if ( psmAnnotationTypeDisplayData == null || psmAnnotationTypeDisplayData.isEmpty() ) {
			} else {
				annotationDisplayUserSelectionDetailsPerSearch.setAllPsmAnnotationTypeDisplay( psmAnnotationTypeDisplayData );
			}
			
			List<AnnotationTypeDisplayData> peptideAnnotationTypeDisplayData =
					peptideAnnotationDisplayDataAllSearchIds.get( searchId );

			if ( peptideAnnotationTypeDisplayData == null || peptideAnnotationTypeDisplayData.isEmpty() ) {
			} else {
				annotationDisplayUserSelectionDetailsPerSearch.setAllPeptideAnnotationTypeDisplay( peptideAnnotationTypeDisplayData );
			}
			
			annotationDisplayUserSelectionDetailsPerSearchList.add(annotationDisplayUserSelectionDetailsPerSearch);
		}
		
		String defaultAnnTypeIdDisplayJSONString = jacksonJSON_Mapper.writeValueAsString( defaultAnnTypeIdDisplay );

		annotationDisplayUserSelectionDetailsData.setDefaultAnnTypeIdDisplayJSONString( defaultAnnTypeIdDisplayJSONString );

		
		request.setAttribute( WebConstants.PARAMETER_ANNOTATION_DISPLAY_DATA_USER_SELECTION_REQUEST_KEY, annotationDisplayUserSelectionDetailsData );

	}
	
	private List<Integer> getAnnotationTypeIdListFrom_AnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap(
			String psmPeptideType,
			Integer searchId,
			Map<Integer, AnnotationTypeDTOListForSearchId> annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap
			) throws ProxlWebappDataException {
		

		AnnotationTypeDTOListForSearchId annotationTypeDTO_DefaultDisplay_DisplayOrder_ListObj =
				annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.get( searchId );
		
		if ( annotationTypeDTO_DefaultDisplay_DisplayOrder_ListObj == null ) {
			String msg = "Failed to get " + psmPeptideType 
					+ " annotationTypeDTO_DefaultDisplay_DisplayOrder_ListObj (is == null) for search id: " + searchId;
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}

		List<Integer> defaultDisplayAnnTypeIdList = new ArrayList<>( annotationTypeDTO_DefaultDisplay_DisplayOrder_ListObj.getAnnotationTypeDTOList().size() );
		
		for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDTO_DefaultDisplay_DisplayOrder_ListObj.getAnnotationTypeDTOList() ) {

			defaultDisplayAnnTypeIdList.add( annotationTypeDTO.getId() );
		}
		
		return defaultDisplayAnnTypeIdList;
	}
	
	
	
	/**
	 * @param psmPeptideAnnotationType
	 * @param searchIdsSet
	 * @param annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, List<AnnotationTypeDisplayData>> getForAnnType( 
			
			PsmPeptideAnnotationType psmPeptideAnnotationType,
			Set<Integer> searchIdsSet
			) throws Exception {


		Map<Integer, List<AnnotationTypeDisplayData>> annotationTypeDisplayDataAllSearchIds = new HashMap<>();
		
		Map<Integer, List<AnnotationTypeDTO>> annotationTypeDataSortAnnotationNameAllSearchIds = new HashMap<>();

		addForAnnType( searchIdsSet, 
				psmPeptideAnnotationType, 
				FilterableDescriptiveAnnotationType.FILTERABLE, 
				annotationTypeDataSortAnnotationNameAllSearchIds );
		
		addForAnnType( searchIdsSet, 
				psmPeptideAnnotationType, 
				FilterableDescriptiveAnnotationType.DESCRIPTIVE, 
				annotationTypeDataSortAnnotationNameAllSearchIds );
		
		
		for ( Map.Entry<Integer, List<AnnotationTypeDTO>> entry :
			annotationTypeDataSortAnnotationNameAllSearchIds.entrySet() ) {
			
			List<AnnotationTypeDTO> annotationTypeAllFiltDescList = entry.getValue();
			
			Collections.sort( annotationTypeAllFiltDescList, new Comparator<AnnotationTypeDTO>() {

				@Override
				public int compare(AnnotationTypeDTO o1,
						AnnotationTypeDTO o2) {

					return o1.getName().compareTo( o2.getName() );
				}} );
			
			List<AnnotationTypeDisplayData> annotationTypeDisplayList = new ArrayList<>( annotationTypeAllFiltDescList.size() );
			
			for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeAllFiltDescList ) {
				
				//  Get Search Programs per Search record
				
				SearchProgramsPerSearchDTO searchProgramPerSearchDTO = 
						GetSearchProgramsPerSearchData.getInstance().getSearchProgramsPerSearchDTO( annotationTypeDTO.getSearchProgramsPerSearchId() );
				
				//  Build Annotation Type Display record for insert on web page
				
				AnnotationTypeDisplayData annotationTypeDisplayData = new AnnotationTypeDisplayData();
				
				annotationTypeDisplayData.setAnnotationTypeDTO( annotationTypeDTO );
				annotationTypeDisplayData.setSearchProgramPerSearchDTO( searchProgramPerSearchDTO );
								
				annotationTypeDisplayList.add( annotationTypeDisplayData );
			}
			
			Collections.sort( annotationTypeDisplayList, new Comparator<AnnotationTypeDisplayData>() {

				@Override
				public int compare(AnnotationTypeDisplayData o1,
						AnnotationTypeDisplayData o2) {

					return o1.getAnnotationTypeDTO().getName().compareToIgnoreCase( o2.getAnnotationTypeDTO().getName() );
				} } );
			
			annotationTypeDisplayDataAllSearchIds.put( entry.getKey(), annotationTypeDisplayList );
		}
	
		return annotationTypeDisplayDataAllSearchIds;
	}
	
	
	
	
	/**
	 * @param searchIdsSet
	 * @param psmPeptideAnnotationType
	 * @param filterableDescriptiveAnnotationType
	 * @param annotationTypeDataSortAnnotationNameAllSearchIds
	 * @throws Exception
	 */
	private void addForAnnType( 
			Set<Integer> searchIdsSet,
			PsmPeptideAnnotationType psmPeptideAnnotationType,
			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType,
			Map<Integer, List<AnnotationTypeDTO>> annotationTypeDataSortAnnotationNameAllSearchIds ) throws Exception {

		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeDataAllSearchIds = 
				GetAnnotationTypeData.getInstance()
				.getAnnTypeForSearchIdsFiltDescPsmPeptide( 
						searchIdsSet, 
						filterableDescriptiveAnnotationType, 
						psmPeptideAnnotationType );
		
		for ( Map.Entry<Integer, Map<Integer, AnnotationTypeDTO>> entry : annotationTypeDataAllSearchIds.entrySet() ) {
			
			List<AnnotationTypeDTO> annotationTypeDataSortAnnotationName = 
					annotationTypeDataSortAnnotationNameAllSearchIds.get( entry.getKey() );
			
			if ( annotationTypeDataSortAnnotationName == null ) {
				
				annotationTypeDataSortAnnotationName = new ArrayList<>();
				annotationTypeDataSortAnnotationNameAllSearchIds.put( entry.getKey(), annotationTypeDataSortAnnotationName );
			}
			
			for ( Map.Entry<Integer, AnnotationTypeDTO> annTypeEntry : entry.getValue().entrySet() ) {
				
				annotationTypeDataSortAnnotationName.add( annTypeEntry.getValue() );
			}
		}
		
		
	}
	
}
