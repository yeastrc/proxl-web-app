package org.yeastrc.xlink.www.searcher_utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;

/**
 * 
 * 
 * Can the counts in the table for at the default default cutoff be used
 * for the set of cutoffs provided.
 * 
 * The answer is yes if and only if exactly only and all the annotation types with a default cutoff 
 * are present in the list of cutoffs passed in 
 * and the cutoffs passed in match the default cutoffs for the annotation types
 */
public class DefaultCutoffsExactlyMatchAnnTypeDataToSearchData {

	private static final Logger log = Logger.getLogger(DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.class);

	private DefaultCutoffsExactlyMatchAnnTypeDataToSearchData() { }
	private static final DefaultCutoffsExactlyMatchAnnTypeDataToSearchData _INSTANCE = new DefaultCutoffsExactlyMatchAnnTypeDataToSearchData();
	public static DefaultCutoffsExactlyMatchAnnTypeDataToSearchData getInstance() { return _INSTANCE; }
	
	
	public DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult defaultCutoffsExactlyMatchAnnTypeDataToSearchData( 
			int searchId, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {
		
		DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult result = new DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult();
		
		boolean defaultCutoffsExactlyMatchAnnTypeDataToSearchData = true;
		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
		

		//  Get Filterable Annotation Type records for Reported Peptides and PSMs

		Collection<Integer> searchIdsCollection = new ArrayList<>( 1 );
		searchIdsCollection.add( searchId );
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideFilterableAnnotationTypesForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsCollection );

		Map<Integer, AnnotationTypeDTO> peptideFilterableAnnotationTypesForSearchId =
				peptideFilterableAnnotationTypesForSearchIds.get( searchId );

		Map<Integer, Map<Integer, AnnotationTypeDTO>> psmFilterableAnnotationTypesForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsCollection );

		Map<Integer, AnnotationTypeDTO> psmFilterableAnnotationTypesForSearchId =
				psmFilterableAnnotationTypesForSearchIds.get( searchId );

		//  Test Peptide Cutoffs
		if ( ! processPeptideOrPSM( peptideCutoffValuesList,  peptideFilterableAnnotationTypesForSearchId ) ) {
			defaultCutoffsExactlyMatchAnnTypeDataToSearchData = false;
		}

		//  Test PSM Cutoffs
		if ( ! processPeptideOrPSM( psmCutoffValuesList,  psmFilterableAnnotationTypesForSearchId ) ) {
			defaultCutoffsExactlyMatchAnnTypeDataToSearchData = false;
		}
		
		result.defaultCutoffsExactlyMatchAnnTypeDataToSearchData = defaultCutoffsExactlyMatchAnnTypeDataToSearchData;
		
		return result;
	}
	
	/**
	 * @param cutoffValuesList
	 * @param filterableAnnotationTypesForSearchId
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 */
	private boolean processPeptideOrPSM( 
			List<SearcherCutoffValuesAnnotationLevel> cutoffValuesList, 
			Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesForSearchId ) throws ProxlWebappInternalErrorException {
	
		Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesWithDefaultValues = getFilterableAnnTypesWithDefaultValues( filterableAnnotationTypesForSearchId );
		
		if ( cutoffValuesList == null || cutoffValuesList.isEmpty() ) {
			//  No cutoffs so must not be any default cutoffs in annotation types
			
			if ( filterableAnnotationTypesWithDefaultValues == null ) {
				//  Also no annotation type records with defaults so return true
				return true; //  EARLY EXIT
			}
			//  Found at least one default filter but no cutoff provided for it so return false;
			return false; //  EARLY EXIT
		}
		
		//  Process the cutoffs, ensure that ALL are default and all default in Annotation types is in the cutoffs
		
		for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : cutoffValuesList ) {
			//  Safe to do .remove(...) since this is a copy Map
			AnnotationTypeDTO annotationTypeDTOWithDefaultValue = filterableAnnotationTypesWithDefaultValues.remove( searcherCutoffValuesAnnotationLevel.getAnnotationTypeId() );
			if ( annotationTypeDTOWithDefaultValue != null ) { // Have ann type with default so make comparison
				if ( annotationTypeDTOWithDefaultValue.getAnnotationTypeFilterableDTO().getDefaultFilterValueAtDatabaseLoad().doubleValue() 
						!= searcherCutoffValuesAnnotationLevel.getAnnotationCutoffValue() ) {
					// Found a default ann type and the default values don't match
					return false; //  EARLY EXIT
				}
			}
		}
		if ( ! filterableAnnotationTypesWithDefaultValues.isEmpty() ) {
			//  There is at least one default filter and no cutoff provided for it so return false;
			return false; //  EARLY EXIT
		}
		
		return true;
	}
	
	/**
	 * @param filterableAnnotationTypesForSearchId
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 */
	private Map<Integer, AnnotationTypeDTO> getFilterableAnnTypesWithDefaultValues( Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesForSearchId ) throws ProxlWebappInternalErrorException {

		if ( filterableAnnotationTypesForSearchId == null || filterableAnnotationTypesForSearchId.isEmpty() ) {
			return null;
		}
		
		Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesWithDefaultValues = new HashMap<>();
		
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : filterableAnnotationTypesForSearchId.entrySet() ) {
			AnnotationTypeDTO annotationTypeDTO = entry.getValue();
			if ( annotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				String msg = "annotationTypeDTO.getAnnotationTypeFilterableDTO() == null on a Filterable annotation type, ann type id:"
						+ annotationTypeDTO.getId();
				log.error(msg);
				throw new ProxlWebappInternalErrorException(msg);
			}
			if ( annotationTypeDTO.getAnnotationTypeFilterableDTO().isDefaultFilter() ) {
				//  Found default filter 
				filterableAnnotationTypesWithDefaultValues.put( entry.getKey(), entry.getValue() );
			}
		}
		return filterableAnnotationTypesWithDefaultValues;
	}


	/**
	 * Result object
	 *
	 */
	public static class DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult {
		
		private boolean defaultCutoffsExactlyMatchAnnTypeDataToSearchData;

		public boolean isDefaultCutoffsExactlyMatchAnnTypeDataToSearchData() {
			return defaultCutoffsExactlyMatchAnnTypeDataToSearchData;
		}
		public void setDefaultCutoffsExactlyMatchAnnTypeDataToSearchData(boolean defaultCutoffsExactlyMatchAnnTypeDataToSearchData) {
			this.defaultCutoffsExactlyMatchAnnTypeDataToSearchData = defaultCutoffsExactlyMatchAnnTypeDataToSearchData;
		}

	}
	
}
