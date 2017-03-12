package org.yeastrc.xlink.www.cutoff_processing_web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDBDataOutOfSyncException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplayAnnotationLevel;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplayRoot;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplaySearchLevel;
import org.yeastrc.xlink.www.objects.CutoffsAppliedOnImportWebDisplay;
import org.yeastrc.xlink.www.search_programs_per_search_utils.GetSearchProgramsPerSearchData;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_CutoffsAppliedOnImportWebDisplay;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.Cached_CutoffsAppliedOnImportWebDisplay_Result;

/**
 * 
 *  Get Page display of all filterable type annotations provided
 */
public class GetCutoffPageDisplayRoot {
	
	private static final Logger log = Logger.getLogger( GetCutoffPageDisplayRoot.class );
	//  private constructor
	private GetCutoffPageDisplayRoot() { }
	/**
	 * @return newly created instance
	 */
	public static GetCutoffPageDisplayRoot getInstance() { 
		return new GetCutoffPageDisplayRoot(); 
	}	
	
	/**
	 * Get Cutoff Display data and put in request scope
	 * 
	 * @param projectSearchId
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public CutoffPageDisplayRoot getCutoffPageDisplayRootSingleSearchId(
			int projectSearchId,
			int searchId,
			HttpServletRequest request
			) throws Exception {
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		mapProjectSearchIdToSearchId.put( projectSearchId, searchId );
		Collection<Integer> searchIdsCollection = new HashSet<>();
		searchIdsCollection.add( searchId );
		return getCutoffPageDisplayRoot( mapProjectSearchIdToSearchId, searchIdsCollection, request );
	}
	
	/**
	 * @param mapProjectSearchIdToSearchId
	 * @param searchIdsCollection
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public CutoffPageDisplayRoot getCutoffPageDisplayRoot(
			Map<Integer,Integer> mapProjectSearchIdToSearchId,
			Collection<Integer> searchIdsCollection,
			HttpServletRequest request ) throws Exception {
		
		Set<Integer> searchIdsSet = new HashSet<>( searchIdsCollection );  // put in set to remove dups;
		List<Integer> searchIdsList= new ArrayList<>( searchIdsSet );  // put in list so can sort
		Collections.sort( searchIdsList );
		//  Cutoff  Values,  Match with Annotation Type records from DB to get more info for display on the page
		//  CutoffPageDisplayRoot used for building the display of the cutoffs on the page 
		CutoffPageDisplayRoot cutoffPageDisplayRoot = new CutoffPageDisplayRoot();
		//  Get Annotation Type records for PSM and Peptide
		//  Get  Annotation Type records for PSM
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsList );
		//  Get  Annotation Type records for Reported Peptides
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsList );
		// Process Per SearchId
		for ( Map.Entry<Integer,Integer> entry : mapProjectSearchIdToSearchId.entrySet() ) {
			Integer projectSearchId = entry.getKey();
			Integer searchId = entry.getValue(); 
			Cached_CutoffsAppliedOnImportWebDisplay_Result cached_CutoffsAppliedOnImportWebDisplay_Result =
			Cached_CutoffsAppliedOnImportWebDisplay.getInstance()
			.getCached_CutoffsAppliedOnImportWebDisplay_Result( searchId );
			List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList = 
					cached_CutoffsAppliedOnImportWebDisplay_Result.getCutoffsAppliedOnImportList();
			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel = new CutoffPageDisplaySearchLevel();
			cutoffPageDisplayRoot.addCutoffPageDisplaySearchLevel( cutoffPageDisplaySearchLevel );
			cutoffPageDisplaySearchLevel.setProjectSearchId( projectSearchId );
			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = 
					srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap = 
					srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			if ( srchPgm_Filterable_Psm_AnnotationType_DTOMap == null ) {
				srchPgm_Filterable_Psm_AnnotationType_DTOMap = new HashMap<>();
//				String msg = "srchPgm_Filterable_Psm_AnnotationType_DTOMap == null for searchId: " + searchId;
//				log.error( msg );
//				throw new ProxlWebappDataException(msg);
			}
			if ( srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap == null ) {
				srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap = new HashMap<>();
//				String msg = "srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap == null for searchId: " + searchId;
//				log.error( msg );
//				throw new ProxlWebappDataException(msg);
			}
			processPSMs( srchPgm_Filterable_Psm_AnnotationType_DTOMap, 
					cutoffPageDisplaySearchLevel, 
					cutoffsAppliedOnImportList );
			processPeptides( 
					srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap, 
					cutoffPageDisplaySearchLevel, 
					cutoffsAppliedOnImportList );
		}
		request.setAttribute( WebConstants.PARAMETER_CUTOFF_PAGE_DISPLAY_ROOT_REQUEST_ENTRY, cutoffPageDisplayRoot );
		return cutoffPageDisplayRoot;
	}
	
	/**
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap
	 * @param cutoffPageDisplaySearchLevel
	 * @throws Exception
	 */
	private void processPeptides(
			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap,
			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel,
			List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList ) throws Exception {
		
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap.entrySet() ) {
			AnnotationTypeDTO annotationTypeDTO = entry.getValue();
			CutoffPageDisplayAnnotationLevel cutoffPageDisplayAnnotationLevel =
			processAnnotationTypeObject(
					annotationTypeDTO,
					cutoffsAppliedOnImportList );
			cutoffPageDisplaySearchLevel.addPeptideCutoffPageDisplayAnnotationLevel( cutoffPageDisplayAnnotationLevel );
		}
	}
	
	/**
	 * @param srchPgmFilterablePsmAnnotationTypeDTOMap
	 * @param cutoffPageDisplaySearchLevel
	 * @param psmCutoffValuesMap
	 * @throws Exception
	 */
	public void processPSMs(
			Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap,
			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel,
			List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList )
					throws Exception {
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry :  srchPgmFilterablePsmAnnotationTypeDTOMap.entrySet() ) {
			AnnotationTypeDTO annotationTypeDTO = entry.getValue();
			CutoffPageDisplayAnnotationLevel cutoffPageDisplayAnnotationLevel =
					processAnnotationTypeObject( 
							annotationTypeDTO,
							cutoffsAppliedOnImportList );
			cutoffPageDisplaySearchLevel.addPsmCutoffPageDisplayAnnotationLevel( cutoffPageDisplayAnnotationLevel );
		}
	}
	
	/**
	 * @param annotationTypeDTO
	 * @param cutoffsAppliedOnImportList
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 * @throws ProxlWebappDBDataOutOfSyncException
	 */
	private CutoffPageDisplayAnnotationLevel processAnnotationTypeObject(
			AnnotationTypeDTO annotationTypeDTO,
			List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList ) 
					throws Exception, ProxlWebappDataException, ProxlWebappDBDataOutOfSyncException {
		if ( annotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
			String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + annotationTypeDTO.getId();
			log.error( msg );
			throw new Exception(msg);
		}
		//  Add Display object
		CutoffPageDisplayAnnotationLevel cutoffPageDisplayAnnotationLevel = new CutoffPageDisplayAnnotationLevel();
		cutoffPageDisplayAnnotationLevel.setAnnotationTypeId( annotationTypeDTO.getId() );
		cutoffPageDisplayAnnotationLevel.setAnnotationName( annotationTypeDTO.getName() );
		cutoffPageDisplayAnnotationLevel.setAnnotationDescription( annotationTypeDTO.getDescription() );
		AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
		if ( annotationTypeFilterableDTO == null ) {
			String msg = "No annotationTypeFilterableDTO for AnnotationTypeId. "
					+ "  AnnotationTypeId: " + annotationTypeDTO.getId();
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		FilterDirectionType filterDirectionType = annotationTypeFilterableDTO.getFilterDirectionType();
		if ( filterDirectionType == null ) {
			String msg = "No filterDirectionType for AnnotationTypeId. "
					+ "  AnnotationTypeId: " + annotationTypeDTO.getId();
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		if ( annotationTypeFilterableDTO.isDefaultFilter() ) {
			cutoffPageDisplayAnnotationLevel.setAnnotationDefaultValue( annotationTypeDTO.getAnnotationTypeFilterableDTO().getDefaultFilterValueString() );
		}
		cutoffPageDisplayAnnotationLevel.setAnnotationFilterDirection( filterDirectionType.value() );
		if ( filterDirectionType == FilterDirectionType.ABOVE ) {
			cutoffPageDisplayAnnotationLevel.setAnnotationFilterDirectionAbove(true);
		}
		cutoffPageDisplayAnnotationLevel.setSortOrder( annotationTypeFilterableDTO.getSortOrder() );
		cutoffPageDisplayAnnotationLevel.setDisplayOrder( annotationTypeDTO.getDisplayOrder() );
		Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
		SearchProgramsPerSearchDTO searchProgramsPerSearchDTO =
				GetSearchProgramsPerSearchData.getInstance().getSearchProgramsPerSearchDTO( searchProgramsPerSearchId );
		if ( searchProgramsPerSearchDTO == null ) {
			String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
			log.error( msg );
			throw new ProxlWebappDBDataOutOfSyncException( msg );
		}
		cutoffPageDisplayAnnotationLevel.setSearchProgramDisplayName( searchProgramsPerSearchDTO.getDisplayName() );
		for ( CutoffsAppliedOnImportWebDisplay item : cutoffsAppliedOnImportList ) {
			if ( cutoffPageDisplayAnnotationLevel.getAnnotationTypeId() == item.getAnnotationTypeId() ) {
				cutoffPageDisplayAnnotationLevel.setAnnotationCutoffOnImportValue( item.getCutoffValue() );
				break;
			}
		}
		return cutoffPageDisplayAnnotationLevel;
	}
}
