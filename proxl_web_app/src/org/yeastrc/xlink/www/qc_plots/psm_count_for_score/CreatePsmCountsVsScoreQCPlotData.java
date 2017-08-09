package org.yeastrc.xlink.www.qc_plots.psm_count_for_score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.SearchProgramsPerSearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDBDataOutOfSyncException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ScoreCountFromPsmTblSearcher;
import org.yeastrc.xlink.www.searcher.ScoreCountFromPsmTblSearcher.LinkType;

/**
 * 
 *
 */
public class CreatePsmCountsVsScoreQCPlotData {

	private static final Logger log = Logger.getLogger(CreatePsmCountsVsScoreQCPlotData.class);
	
	private static final int BIN_COUNT = 100;  //  Number of points on the chart
	
	/**
	 * private constructor
	 */
	private CreatePsmCountsVsScoreQCPlotData(){}
	public static CreatePsmCountsVsScoreQCPlotData getInstance( ) throws Exception {
		CreatePsmCountsVsScoreQCPlotData instance = new CreatePsmCountsVsScoreQCPlotData();
		return instance;
	}
	
	/**
	 * @param projectSearchId
	 * @param scanFileId
	 * @param selectedLinkTypes
	 * @param annotationTypeId
	 * @param psmScoreCutoff
	 * @param proteinSequenceIdsToIncludeList
	 * @param proteinSequenceIdsToExcludeList
	 * @return
	 * @throws Exception
	 */
	public PsmCountsVsScoreQCPlotDataJSONRoot create( 
			int projectSearchId, 
			Integer scanFileId, 
			Set<String> selectedLinkTypes, 
			Integer annotationTypeId, 
			Double psmScoreCutoff,
			List<Integer> proteinSequenceIdsToIncludeList,
			List<Integer> proteinSequenceIdsToExcludeList ) throws Exception {
		
		if ( selectedLinkTypes == null || selectedLinkTypes.isEmpty() ) {
			String msg = "selectedLinkTypes cannot be empty.";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		Integer searchId =
				MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
		if ( searchId == null ) {
			String msg = ": No searchId found for projectSearchId: " + projectSearchId;
			log.warn( msg );
		    throw new ProxlWebappDataException( msg );
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
		
		AnnotationTypeDTO annotationTypeDTO = null;
		
		if ( annotationTypeId == null ) {
			//  Get Default annotationTypeId for search id
			// Use annotation type with smallest sort order, if any have sort order.
			// Otherwise, use annotation type with first sorted name, using compareToIgnoreCase.
			
			AnnotationTypeDTO annotationTypeDTOBasedOnSortOrder = null;
			AnnotationTypeDTO annotationTypeDTOBasedOnNameAlphabetical = null;
			
			for ( Map.Entry<Integer, AnnotationTypeDTO> entry : annotationTypeDataForSearchId.entrySet() ) {
				AnnotationTypeDTO annotationTypeDTOInEntry = entry.getValue();
				AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTOInEntry.getAnnotationTypeFilterableDTO();
				if ( annotationTypeFilterableDTO == null ) {
					String msg = "No annotationTypeFilterableDTO for Filterable PSM Annotation type for search id: " + searchId + ", annotationTypeId: " + annotationTypeId;
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
			if ( annotationTypeDTOBasedOnSortOrder != null ) {
				annotationTypeDTO = annotationTypeDTOBasedOnSortOrder;
			} else {
				annotationTypeDTO = annotationTypeDTOBasedOnNameAlphabetical;
			}
			annotationTypeId = annotationTypeDTO.getId();
		} else {
			annotationTypeDTO = annotationTypeDataForSearchId.get( annotationTypeId );
			if ( annotationTypeDTO == null ) {
				String msg = "No Filterable PSM Annotation type for search id: " + searchId + ", annotationTypeId: " + annotationTypeId;
				log.error(msg);
				throw new ProxlWebappDataException(msg);
			}
		}
		
		Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
		SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = SearchProgramsPerSearchDAO.getInstance().getSearchProgramDTOForId( searchProgramsPerSearchId ) ;
		if ( searchProgramsPerSearchDTO == null ) {
			String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
			log.error( msg );
			throw new ProxlWebappDBDataOutOfSyncException( msg );
		}
		
		AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
		if ( annotationTypeFilterableDTO == null ) {
			String msg = "No annotationTypeFilterableDTO for Filterable PSM Annotation type for search id: " + searchId + ", annotationTypeId: " + annotationTypeId;
			log.error(msg);
			throw new ProxlWebappDataException(msg);
		}
		FilterDirectionType filterDirectionType = annotationTypeFilterableDTO.getFilterDirectionType();
		if ( filterDirectionType != FilterDirectionType.ABOVE && filterDirectionType != FilterDirectionType.BELOW ) {
			String msg = "value for filterDirectionType is unexpected for search id: " + searchId + ", filterDirectionType: " + filterDirectionType.toString();
			log.error(msg);
			throw new ProxlWebappDBDataOutOfSyncException( msg );
		}		
		//  Keyed on Selected Link Type
		Map<String, List<Double>> scoreValuesForSelectedTypesMap = new HashMap<>();
		for ( String selectedLinkType : selectedLinkTypes ) {
			LinkType linkType = null;
			if ( PeptideViewLinkTypesConstants.ALL_PSM.equals( selectedLinkType ) ) {
				linkType = LinkType.ALL;
			} else if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( selectedLinkType ) ) {
				linkType = LinkType.CROSSLINK;
			} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( selectedLinkType ) ) {
				linkType = LinkType.LOOPLINK;
			} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( selectedLinkType ) ) {
				linkType = LinkType.UNLINKED;
			}
			//  Get data from DB for Link Type
			List<Double> scoreValuesForPSMsthatMeetCriteriaList = 
				ScoreCountFromPsmTblSearcher.getInstance().getScoreValues( 
						searchId, 
						scanFileId,  
						linkType, 
						annotationTypeId, 
						psmScoreCutoff, 
						proteinSequenceIdsToIncludeList,
						proteinSequenceIdsToExcludeList );
			scoreValuesForSelectedTypesMap.put( selectedLinkType, scoreValuesForPSMsthatMeetCriteriaList );
		}
		double maxScore = 0;
		double minScore = 0;
		boolean firstEntry = true;
		for ( Map.Entry<String, List<Double>> entry : scoreValuesForSelectedTypesMap.entrySet() ) {
			List<Double> scoreValuesForPSMsthatMeetCriteriaList = entry.getValue();
			for ( Double scoreValue : scoreValuesForPSMsthatMeetCriteriaList ) {
				if ( firstEntry ) {
					firstEntry = false;
					maxScore = 0;
					minScore = 0;
				} else {
					if ( maxScore < scoreValue ) {
						maxScore = scoreValue;
					}
					if ( minScore > scoreValue ) {
						minScore = scoreValue;
					}
				}
			}
		}
		double scoreMaxForPuttingInBins = maxScore;
		if ( psmScoreCutoff != null ) {
			scoreMaxForPuttingInBins = psmScoreCutoff;
		}
		double scoreMinForPuttingInBins = minScore;
		//  Process data into bins
		double binSizeAsDouble = ( scoreMaxForPuttingInBins - scoreMinForPuttingInBins ) / BIN_COUNT;
		PsmCountsVsScoreQCPlotDataJSONPerType crosslinkData = null;
		PsmCountsVsScoreQCPlotDataJSONPerType looplinkData = null;
		PsmCountsVsScoreQCPlotDataJSONPerType unlinkedData = null;
		PsmCountsVsScoreQCPlotDataJSONPerType alllinkData = null;
		int dataArraySize = 0;
		//  Process for each Selected Link Type
		for ( String selectedLinkType : selectedLinkTypes ) {
			//  Scores for selected link type being processed
			List<Double> scoreValuesForPSMsthatMeetCriteriaList = scoreValuesForSelectedTypesMap.get( selectedLinkType );
			int totalCountForType = scoreValuesForPSMsthatMeetCriteriaList.size();
			if ( psmScoreCutoff != null ) {
				//  Have score cutoff so need to get a total count for the type 
				//    without applying the cutoff but applying all other selections
				//  Get data from DB, total score count for selectedLinkType, search id, annotation type id
				LinkType linkType = null;
				if ( PeptideViewLinkTypesConstants.ALL_PSM.equals( selectedLinkType ) ) {
					linkType = LinkType.ALL;
				} else if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( selectedLinkType ) ) {
					linkType = LinkType.CROSSLINK;
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( selectedLinkType ) ) {
					linkType = LinkType.LOOPLINK;
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( selectedLinkType ) ) {
					linkType = LinkType.UNLINKED;
				}
				totalCountForType = ScoreCountFromPsmTblSearcher.getInstance()
						.getScoreCount( 
								searchId, 
								scanFileId, 
								linkType, 
								annotationTypeId, 
								proteinSequenceIdsToIncludeList,
								proteinSequenceIdsToExcludeList );
			}
			PsmCountsVsScoreQCPlotDataJSONPerType linkData = null;
			if ( PeptideViewLinkTypesConstants.ALL_PSM.equals( selectedLinkType ) ) {
				alllinkData = new PsmCountsVsScoreQCPlotDataJSONPerType();
				linkData = alllinkData;
			} else if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( selectedLinkType ) ) {
				crosslinkData = new PsmCountsVsScoreQCPlotDataJSONPerType();
				linkData = crosslinkData;
			} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( selectedLinkType ) ) {
				looplinkData = new PsmCountsVsScoreQCPlotDataJSONPerType();
				linkData = looplinkData;
			} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( selectedLinkType ) ) {
				unlinkedData = new PsmCountsVsScoreQCPlotDataJSONPerType();
				linkData = unlinkedData;
			}
			int scoreValueMinValueCount = 0;
			int[] scoreValueCounts = new int[ BIN_COUNT ];
			for ( double scoreValue : scoreValuesForPSMsthatMeetCriteriaList ) {
				double scoreValueFraction = ( scoreValue - scoreMinForPuttingInBins ) / ( scoreMaxForPuttingInBins - scoreMinForPuttingInBins );
				int bin = (int) ( (  scoreValueFraction ) * BIN_COUNT );
				if ( bin < 0 ) {
					bin = 0;
				} else if ( bin >= BIN_COUNT ) {
					bin = BIN_COUNT - 1;
				} 
				scoreValueCounts[ bin ]++;
				if ( scoreValue == scoreMinForPuttingInBins ) {
					scoreValueMinValueCount++;
				}
			}
			List<PsmCountsVsScoreQCPlotDataJSONChartBucket> chartBuckets = new ArrayList<>();
			if ( filterDirectionType == FilterDirectionType.BELOW && scoreMinForPuttingInBins == 0 ) {
				//  Special case bucket for left edge, originally for Q-Value value of zero
				//  Only create if FilterDirectionType.BELOW and min value is zero
				PsmCountsVsScoreQCPlotDataJSONChartBucket chartBucketMinValue = new PsmCountsVsScoreQCPlotDataJSONChartBucket();
				chartBucketMinValue.setBinStart( scoreMinForPuttingInBins );
				chartBucketMinValue.setBinEnd( scoreMinForPuttingInBins );
				chartBucketMinValue.setTotalCount( scoreValueMinValueCount );
				chartBuckets.add( chartBucketMinValue );
			}
			//  Take the data in the bins and  create "buckets" in the format required for the charting API
			int totalScoreValueCount = 0;
			// TODO
			if ( filterDirectionType == FilterDirectionType.ABOVE ) {
				totalScoreValueCount = totalCountForType;
			}
			for ( int binIndex = 0; binIndex < scoreValueCounts.length; binIndex++ ) {
				PsmCountsVsScoreQCPlotDataJSONChartBucket chartBucket = new PsmCountsVsScoreQCPlotDataJSONChartBucket();
				chartBuckets.add( chartBucket );
				int scoreValueCount = scoreValueCounts[ binIndex ];
				if ( filterDirectionType == FilterDirectionType.BELOW ) {
					//  Increment it before taking the value
					totalScoreValueCount += scoreValueCount;
				}
				double binStartDouble = ( ( binIndex * binSizeAsDouble ) + scoreMinForPuttingInBins );
				if ( binIndex == 0 && binStartDouble < scoreMinForPuttingInBins ) {
					chartBucket.setBinStart( scoreMinForPuttingInBins );
				} else { 
					chartBucket.setBinStart( binStartDouble );
				}
				double binEnd = ( binIndex + 1 ) * binSizeAsDouble + scoreMinForPuttingInBins ;
				chartBucket.setBinEnd( binEnd );
				chartBucket.setTotalCount( totalScoreValueCount );
				if ( filterDirectionType == FilterDirectionType.ABOVE ) {
					//  Decrement it after taking the value
					totalScoreValueCount -= scoreValueCount;
				}
			}
			linkData.setChartBuckets( chartBuckets );
			dataArraySize = chartBuckets.size();
			linkData.setTotalCountForType( totalCountForType );
		}
		//  Populate objects to generate JSON
		PsmCountsVsScoreQCPlotDataJSONRoot psmCountsVsScoreQCPlotDataJSONRoot = new PsmCountsVsScoreQCPlotDataJSONRoot();
		psmCountsVsScoreQCPlotDataJSONRoot.setAnnotationTypeId( annotationTypeId );
		psmCountsVsScoreQCPlotDataJSONRoot.setAnnotationTypeName( annotationTypeDTO.getName() );
		psmCountsVsScoreQCPlotDataJSONRoot.setSearchProgramName( searchProgramsPerSearchDTO.getDisplayName() );
		psmCountsVsScoreQCPlotDataJSONRoot.setDataArraySize( dataArraySize );
		psmCountsVsScoreQCPlotDataJSONRoot.setAlllinkData( alllinkData );
		psmCountsVsScoreQCPlotDataJSONRoot.setCrosslinkData( crosslinkData );
		psmCountsVsScoreQCPlotDataJSONRoot.setLooplinkData( looplinkData );
		psmCountsVsScoreQCPlotDataJSONRoot.setUnlinkedData( unlinkedData );
		if ( filterDirectionType == FilterDirectionType.ABOVE ) {
			psmCountsVsScoreQCPlotDataJSONRoot.setSortDirectionAbove(true);
		} else if ( filterDirectionType == FilterDirectionType.BELOW ) {
			psmCountsVsScoreQCPlotDataJSONRoot.setSortDirectionBelow(true);
		}
		return psmCountsVsScoreQCPlotDataJSONRoot;
	}
}
