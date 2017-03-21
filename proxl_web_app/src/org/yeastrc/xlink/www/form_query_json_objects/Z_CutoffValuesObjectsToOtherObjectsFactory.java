package org.yeastrc.xlink.www.form_query_json_objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;

/**
 * Create SearcherCutoffValues... Objects from objects in package org.yeastrc.xlink.www.form_query_json_objects
 *
 */
public class Z_CutoffValuesObjectsToOtherObjectsFactory {
	
	private static final Logger log = Logger.getLogger( Z_CutoffValuesObjectsToOtherObjectsFactory.class );
	
	/**
	 * create SearcherCutoffValuesRootLevel object and children from 
	 * 
	 * @return SearcherCutoffValuesRootLevel object and children
	 * @throws Exception 
	 */
	public static Z_CutoffValuesObjectsToOtherObjects_RootResult createSearcherCutoffValuesRootLevel( 
			Collection<Integer> searchIds,
			CutoffValuesRootLevel inputItem ) throws ProxlWebappDataException, Exception {
		
		// input
		Map<String, CutoffValuesSearchLevel> inputSearches = inputItem.getSearches();
		//  outputs
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = new SearcherCutoffValuesRootLevel();
		//  Get Annotation Type records for PSM and Peptide
		//  Get  Annotation Type records for PSM
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
		//  Get  Annotation Type records for Reported Peptides
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIds );
		
		for ( Map.Entry<String,CutoffValuesSearchLevel> entry : inputSearches.entrySet() ) {
			String projectSearchIdString = entry.getKey();
			CutoffValuesSearchLevel cutoffValuesSearchLevel = entry.getValue();
			Integer projectSearchId = null;
			try {
				projectSearchId = Integer.valueOf( projectSearchIdString );
			} catch ( Exception e ) {
				String msg = "Failed to parse Project Search Id: " + projectSearchIdString;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			Integer searchId =
					MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
			if ( searchId == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
			}
			Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap =
					srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap =
					srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			if ( srchPgmFilterablePsmAnnotationTypeDTOMap == null ) {
				srchPgmFilterablePsmAnnotationTypeDTOMap = new HashMap<>();
				String msg = "Failed to get Filterable PSM annotionation type records for Search Id: " + searchId
						+ ", projectSearchId: " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap == null ) {
				srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
//				String msg = "Failed to get Filterable Reported Peptide annotation type records for Search Id: " + searchIdString;
//				log.error( msg );
//				throw new ProxlWebappDataException(msg);
			}
			Z_CutoffValuesObjectsToOtherObjects_PerSearchResult cutoffValuesObjectsToOtherObjects_PerSearchResult = 
					createSearcherCutoffValuesSearchLevel( searchIds, cutoffValuesSearchLevel );
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					cutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();
			searcherCutoffValuesRootLevel.addPerSearchCutoffs( searcherCutoffValuesSearchLevel );
		}
		Z_CutoffValuesObjectsToOtherObjects_RootResult outputItem = new Z_CutoffValuesObjectsToOtherObjects_RootResult();
		outputItem.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );
		return outputItem;
	}
	
	/**
	 * @param searchIds
	 * @param inputItem
	 * @return
	 * @throws Exception
	 */
	public static Z_CutoffValuesObjectsToOtherObjects_PerSearchResult createSearcherCutoffValuesSearchLevel( 
			Collection<Integer> searchIds,
			CutoffValuesSearchLevel inputItem ) throws Exception {
		
		Integer projectSearchId = inputItem.getProjectSearchId();
		Integer searchId =
				MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
		if ( searchId == null ) {
			String msg = ": No searchId found for projectSearchId: " + projectSearchId;
			log.warn( msg );
		    throw new ProxlWebappDataException( msg );
		}
		//  Get Annotation Type records for PSM and Peptide
		//  Get  Annotation Type records for PSM
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
		//  Get  Annotation Type records for Reported Peptides
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIds );
		Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap = 
				srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
		Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = 
				srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap.get( searchId );
		if ( srchPgmFilterablePsmAnnotationTypeDTOMap == null ) {
			srchPgmFilterablePsmAnnotationTypeDTOMap = new HashMap<Integer, AnnotationTypeDTO>();
		}
		if ( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap == null ) {
			srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = new HashMap<Integer, AnnotationTypeDTO>();
		}
		return createSearcherCutoffValuesSearchLevel_Internal( 
				inputItem, 
				srchPgmFilterablePsmAnnotationTypeDTOMap, 
				srchPgmFilterableReportedPeptideAnnotationTypeDTOMap );
	}
	
	/**
	 * @param inputItem
	 * @return
	 * @throws Exception 
	 */
	public static Z_CutoffValuesObjectsToOtherObjects_PerSearchResult createSearcherCutoffValuesSearchLevel_Internal( 
			CutoffValuesSearchLevel inputItem,
			Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap,
			Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap
			) throws Exception {
		// inputs
		int projectSearchId = inputItem.getProjectSearchId();  //  Actually returns projectSearchId
		Map<String,CutoffValuesAnnotationLevel> psmCutoffValues = inputItem.getPsmCutoffValues();
		Map<String,CutoffValuesAnnotationLevel> peptideCutoffValues = inputItem.getPeptideCutoffValues();
		//  outputs
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
		searcherCutoffValuesSearchLevel.setProjectSearchId( projectSearchId );
		//////////////////////
		//  Process Input
		for ( Map.Entry<String, CutoffValuesAnnotationLevel> entry : psmCutoffValues.entrySet() ) {
			CutoffValuesAnnotationLevel cutoffValuesAnnotationLevel = entry.getValue();
			Integer typeId = cutoffValuesAnnotationLevel.getId();
			if ( StringUtils.isNotEmpty( cutoffValuesAnnotationLevel.getValue() ) ) {
				//  Only add to the output data structure if the input cutoff value is not empty 
				SearcherCutoffValuesAnnotationLevel output = 
						createSearcherCutoffValuesAnnotationLevel( 
								typeId, 
								cutoffValuesAnnotationLevel, 
								srchPgmFilterablePsmAnnotationTypeDTOMap );
				searcherCutoffValuesSearchLevel.addPsmPerAnnotationCutoffs( output );
			}
		}
		for ( Map.Entry<String, CutoffValuesAnnotationLevel> entry : peptideCutoffValues.entrySet() ) {
			CutoffValuesAnnotationLevel cutoffValuesAnnotationLevel = entry.getValue();
			Integer typeId = cutoffValuesAnnotationLevel.getId();
			if ( StringUtils.isNotEmpty( cutoffValuesAnnotationLevel.getValue() ) ) {
				//  Only add to the output data structure if the input cutoff value is not empty 
				SearcherCutoffValuesAnnotationLevel output = 
						createSearcherCutoffValuesAnnotationLevel( 
								typeId, 
								cutoffValuesAnnotationLevel, 
								srchPgmFilterableReportedPeptideAnnotationTypeDTOMap );
				searcherCutoffValuesSearchLevel.addPeptidePerAnnotationCutoffs( output );
			}
		}
		Z_CutoffValuesObjectsToOtherObjects_PerSearchResult outputItem = new Z_CutoffValuesObjectsToOtherObjects_PerSearchResult();
		outputItem.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
		return outputItem;
	}
	
	/**
	 * @param inputItem
	 * @return
	 * @throws Exception 
	 */
	private static SearcherCutoffValuesAnnotationLevel createSearcherCutoffValuesAnnotationLevel( 
			Integer typeId,
			CutoffValuesAnnotationLevel inputItem,
			Map<Integer, AnnotationTypeDTO> annotationTypeDTOMap  ) throws Exception {
		AnnotationTypeDTO annotationTypeDTO = annotationTypeDTOMap.get( typeId );
		if ( annotationTypeDTO == null ) {
			String msg = "Failed to find annotationTypeDTO for type id: " + typeId;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		String annotationCutoffValueString = inputItem.getValue();
		SearcherCutoffValuesAnnotationLevel outputItem = new SearcherCutoffValuesAnnotationLevel();
		outputItem.setAnnotationTypeId( inputItem.getId() );
		try {
			outputItem.setAnnotationCutoffValue( Double.parseDouble( annotationCutoffValueString ) );
		} catch ( Exception e ) {
			String msg = "Failed to parse Annotation Cutoff Value as decimal (double) for type id: " + typeId + ". Annotation cutoff value: " + annotationCutoffValueString ;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		outputItem.setAnnotationTypeDTO( annotationTypeDTO );
		return outputItem;
	}
	
	/**
	 * Results from createSearcherCutoffValuesRootLevel(...)
	 *
	 */
	public static class Z_CutoffValuesObjectsToOtherObjects_RootResult {
		private SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;
		public SearcherCutoffValuesRootLevel getSearcherCutoffValuesRootLevel() {
			return searcherCutoffValuesRootLevel;
		}
		public void setSearcherCutoffValuesRootLevel(
				SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) {
			this.searcherCutoffValuesRootLevel = searcherCutoffValuesRootLevel;
		}
	}
	
	/**
	 * Results from createSearcherCutoffValuesSearchLevel(...)
	 *
	 */
	public static class Z_CutoffValuesObjectsToOtherObjects_PerSearchResult {
		private SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;
		public SearcherCutoffValuesSearchLevel getSearcherCutoffValuesSearchLevel() {
			return searcherCutoffValuesSearchLevel;
		}
		public void setSearcherCutoffValuesSearchLevel(
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel) {
			this.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;
		}
	}
}
