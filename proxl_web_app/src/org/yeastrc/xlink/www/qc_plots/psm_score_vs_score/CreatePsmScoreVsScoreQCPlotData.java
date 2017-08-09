package org.yeastrc.xlink.www.qc_plots.psm_score_vs_score;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.SearchProgramsPerSearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDBDataOutOfSyncException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.PsmScoreVsScoreSearcherResults;
import org.yeastrc.xlink.www.searcher.PsmAnnotationScoreScoreSearcher;

/**
 * 
 *
 */
public class CreatePsmScoreVsScoreQCPlotData {

	private static final Logger log = Logger.getLogger(CreatePsmScoreVsScoreQCPlotData.class);

	private enum GetAnnotationTypeDTO_Type { SORT_ID_THEN_ALPHA, ALPHA_ONLY }

	/**
	 * private constructor
	 */
	private CreatePsmScoreVsScoreQCPlotData(){}
	public static CreatePsmScoreVsScoreQCPlotData getInstance( ) throws Exception {
		CreatePsmScoreVsScoreQCPlotData instance = new CreatePsmScoreVsScoreQCPlotData();
		return instance;
	}
	
	/**
	 * @param searchId
	 * @param scanFileId - Optional
	 * @param selectedLinkTypes
	 * @param annotationTypeId_1
	 * @param annotationTypeId_2
	 * @param psmScoreCutoff_1
	 * @param psmScoreCutoff_2
	 * @return
	 * @throws Exception
	 */
	public CreatePsmScoreVsScoreQCPlotDataResults createPsmScoreVsScoreQCPlotData( 
			int searchId,
			Integer scanFileId,
			Set<String> selectedLinkTypes,			
			Integer annotationTypeId_1,
			Integer annotationTypeId_2,
			Double psmScoreCutoff_1,
			Double psmScoreCutoff_2 ) throws Exception {

		CreatePsmScoreVsScoreQCPlotDataResults results = new CreatePsmScoreVsScoreQCPlotDataResults();
		
		if ( selectedLinkTypes == null || selectedLinkTypes.isEmpty() ) {
			String msg = "selectedLinkTypes cannot be empty.";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		List<Integer> searchIdsList = new ArrayList<>( 1 );
		searchIdsList.add( searchId );
		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeDataAllSearchIds = 
				GetAnnotationTypeData.getInstance()
				.getAll_Psm_Filterable_ForSearchIds( searchIdsList );
		Map<Integer, AnnotationTypeDTO> annotationTypeDataForSearchId = annotationTypeDataAllSearchIds.get( searchId );
		if ( annotationTypeDataForSearchId == null ) {
			String msg = "No Filterable PSM Annotation types for search id: " + searchId;
			log.error(msg);
			throw new ProxlWebappDataException(msg);
		}

		if ( annotationTypeId_1 == null ) {
			AnnotationTypeDTO annotationTypeDTO = 
					getAnnotationTypeDTO( searchId, annotationTypeDataForSearchId, GetAnnotationTypeDTO_Type.SORT_ID_THEN_ALPHA );
			
			Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
			SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = SearchProgramsPerSearchDAO.getInstance().getSearchProgramDTOForId( searchProgramsPerSearchId ) ;
			if ( searchProgramsPerSearchDTO == null ) {
				String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
				log.error( msg );
				throw new ProxlWebappDBDataOutOfSyncException( msg );
			}
			
			results.setAnnotationTypeId_1( annotationTypeDTO.getId() );
			results.setAnnotationTypeName_1( annotationTypeDTO.getName() );
			results.setSearchProgramName_1( searchProgramsPerSearchDTO.getDisplayName() );
			
			annotationTypeId_1 = annotationTypeDTO.getId();
		}

		if ( annotationTypeId_2 == null ) {
			AnnotationTypeDTO annotationTypeDTO = 
					getAnnotationTypeDTO( searchId, annotationTypeDataForSearchId, GetAnnotationTypeDTO_Type.ALPHA_ONLY );

			Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
			SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = SearchProgramsPerSearchDAO.getInstance().getSearchProgramDTOForId( searchProgramsPerSearchId ) ;
			if ( searchProgramsPerSearchDTO == null ) {
				String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
				log.error( msg );
				throw new ProxlWebappDBDataOutOfSyncException( msg );
			}
			
			results.setAnnotationTypeId_2( annotationTypeDTO.getId() );
			results.setAnnotationTypeName_2( annotationTypeDTO.getName() );
			results.setSearchProgramName_2( searchProgramsPerSearchDTO.getDisplayName() );
			
			annotationTypeId_2 = annotationTypeDTO.getId();
		}

		PsmScoreVsScoreSearcherResults searcherResults  = 
				PsmAnnotationScoreScoreSearcher.getInstance()
				.getPsmScoreVsScoreList( 
						searchId, 
						scanFileId,
						selectedLinkTypes,
						annotationTypeId_1, 
						psmScoreCutoff_1,
						annotationTypeId_2,
						psmScoreCutoff_2 );

		results.setCrosslinkChartData( searcherResults.getCrosslinkEntries() );
		results.setLooplinkChartData( searcherResults.getLooplinkEntries() );
		results.setUnlinkedChartData( searcherResults.getUnlinkedEntries() );
		
		return results;
	}
	
	/**
	 * @param searchId
	 * @param annotationTypeDataForSearchId
	 * @param GetAnnotationTypeDTO_Type
	 * @return
	 * @throws ProxlWebappDataException
	 */
	private AnnotationTypeDTO getAnnotationTypeDTO(
			int searchId, 
			Map<Integer, AnnotationTypeDTO> annotationTypeDataForSearchId,
			GetAnnotationTypeDTO_Type getAnnotationTypeDTO_Type
			) throws ProxlWebappDataException {
		
		AnnotationTypeDTO annotationTypeDTO_ann_id;
		//  Get Default annotationTypeId for search id
		// Use annotation type with smallest sort order, if any have sort order.
		// Otherwise, use annotation type with first sorted name, using compareToIgnoreCase.
		
		AnnotationTypeDTO annotationTypeDTOBasedOnSortOrder = null;
		AnnotationTypeDTO annotationTypeDTOBasedOnNameAlphabetical = null;
		
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : annotationTypeDataForSearchId.entrySet() ) {
			AnnotationTypeDTO annotationTypeDTOInEntry = entry.getValue();
			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTOInEntry.getAnnotationTypeFilterableDTO();
			if ( annotationTypeFilterableDTO == null ) {
				String msg = "No annotationTypeFilterableDTO for Filterable PSM Annotation type for search id: " + searchId + ", annotationTypeId: " + annotationTypeDTOInEntry.getId();
				log.error(msg);
				throw new ProxlWebappDataException(msg);
			}
			if ( annotationTypeDTOBasedOnNameAlphabetical == null ) {
				annotationTypeDTOBasedOnNameAlphabetical = annotationTypeDTOInEntry;
			} else {
				if ( annotationTypeDTOInEntry.getName()
						.compareToIgnoreCase( annotationTypeDTOBasedOnNameAlphabetical.getName() ) < 0 ) {
					annotationTypeDTOBasedOnNameAlphabetical = annotationTypeDTOInEntry;
				}
			}
			if ( annotationTypeDTOInEntry.getAnnotationTypeFilterableDTO().getSortOrder() == null ) {
				continue;  // EARLY CONTINUE
			}
			if ( annotationTypeDTOBasedOnSortOrder == null ) {
				annotationTypeDTOBasedOnSortOrder = annotationTypeDTOInEntry;
				continue;  // EARLY CONTINUE
			}
			if ( annotationTypeDTOInEntry.getAnnotationTypeFilterableDTO().getSortOrder() <
					annotationTypeDTOBasedOnSortOrder.getAnnotationTypeFilterableDTO().getSortOrder() ) {
				annotationTypeDTOBasedOnSortOrder = annotationTypeDTOInEntry;
				continue;  // EARLY CONTINUE
			}
		}
		if ( getAnnotationTypeDTO_Type == GetAnnotationTypeDTO_Type.SORT_ID_THEN_ALPHA
				&& annotationTypeDTOBasedOnSortOrder != null ) {
			annotationTypeDTO_ann_id = annotationTypeDTOBasedOnSortOrder;
		} else {
			annotationTypeDTO_ann_id = annotationTypeDTOBasedOnNameAlphabetical;
		}
		return annotationTypeDTO_ann_id;
	}
}
