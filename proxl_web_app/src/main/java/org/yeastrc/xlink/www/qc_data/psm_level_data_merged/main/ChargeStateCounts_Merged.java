package org.yeastrc.xlink.www.qc_data.psm_level_data_merged.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.ChargeStateCounts_Merged_Results;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.ChargeStateCounts_Merged_Results.ChargeStateCountsResultsForChargeValue;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.ChargeStateCounts_Merged_Results.ChargeStateCountsResultsForLinkType;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.ChargeStateCounts_Merged_Results.ChargeStateCountsResultsForSearchId;
import org.yeastrc.xlink.www.searcher.PSM_DistinctChargeStatesSearcher;
import org.yeastrc.xlink.www.searcher.PSM_DistinctChargeStatesSearcher.PSM_DistinctChargeStatesResult;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

/**
 * 
 *
 */
public class ChargeStateCounts_Merged {

	private static final Logger log = LoggerFactory.getLogger( ChargeStateCounts_Merged.class);

	/**
	 * private constructor
	 */
	private ChargeStateCounts_Merged(){}
	public static ChargeStateCounts_Merged getInstance( ) throws Exception {
		ChargeStateCounts_Merged instance = new ChargeStateCounts_Merged();
		return instance;
	}
		
	/**
	 * @param filterCriteriaJSON
	 * @param searches
	 * @return
	 * @throws Exception
	 */
	public ChargeStateCounts_Merged_Results getChargeStateCounts_Merged( 			
			QCPageQueryJSONRoot qcPageQueryJSONRoot, 
			List<SearchDTO> searches ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}

		String[] linkTypesFromURL = qcPageQueryJSONRoot.getLinkTypes();
		
		if ( linkTypesFromURL == null || linkTypesFromURL.length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
		
		//  Create link types in lower case for display and upper case for being like the selection from web page if came from other place
		List<String> linkTypesList = new ArrayList<String>( linkTypesFromURL.length );
		{
			String[] linkTypesFromURLUpdated = new String[ linkTypesFromURL.length ];
			int linkTypesFromURLUpdatedIndex = 0;

			for ( String linkTypeFromWeb : linkTypesFromURL ) {
				String linkTypeRequestUpdated = null;
				String linkTypeDisplay = null;
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromWeb ) || XLinkUtils.CROSS_TYPE_STRING.equals( linkTypeFromWeb ) ) {
					linkTypeRequestUpdated = PeptideViewLinkTypesConstants.CROSSLINK_PSM;
					linkTypeDisplay = XLinkUtils.CROSS_TYPE_STRING;
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromWeb ) || XLinkUtils.LOOP_TYPE_STRING.equals( linkTypeFromWeb ) ) {
					linkTypeRequestUpdated = PeptideViewLinkTypesConstants.LOOPLINK_PSM;
					linkTypeDisplay = XLinkUtils.LOOP_TYPE_STRING;
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromWeb ) || XLinkUtils.UNLINKED_TYPE_STRING.equals( linkTypeFromWeb ) ) {
					linkTypeRequestUpdated = PeptideViewLinkTypesConstants.UNLINKED_PSM;
					linkTypeDisplay = XLinkUtils.UNLINKED_TYPE_STRING;
				} else {
					String msg = "linkType is invalid, linkTypeFromWeb: " + linkTypeFromWeb;
					log.error( msg );
					throw new Exception( msg );
				}
				linkTypesList.add( linkTypeDisplay );
				linkTypesFromURLUpdated[ linkTypesFromURLUpdatedIndex ] = linkTypeRequestUpdated;
				linkTypesFromURLUpdatedIndex++;
			}
			linkTypesFromURL = linkTypesFromURLUpdated;
			qcPageQueryJSONRoot.setLinkTypes( linkTypesFromURLUpdated );
		}
		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( qcPageQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = qcPageQueryJSONRoot.getMods();

		List<Integer> includeProteinSeqVIdsDecodedArray = qcPageQueryJSONRoot.getIncludeProteinSeqVIdsDecodedArray();
		
		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = qcPageQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		//  Get Maps of Charge values mapped to count, mapped by search id then link type
		//  Map<[link type], Map<[search id],Map<[charge value],[count of charge value]>>>
		Map<String,Map<Integer,Map<Integer,Long>>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType = 
				getAllSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType(
						searches, linkTypesForDBQuery, modsForDBQuery, includeProteinSeqVIdsDecodedArray, searcherCutoffValuesRootLevel);

		//  Get Maps of PSM count mapped by search id and then link type
		//  Map<[link type], Map<[search id],[count of PSMs]>>
		Map<String,Map<Integer,Long>> allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType = 
				getAllSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType (
						allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType );

		ChargeStateCounts_Merged_Results results =
				getPerChartData_KeyedOnLinkType( 
						allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType, 
						allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType,
						linkTypesList, 
						searches,
						searchIdsListDeduppedSorted );
		
		return results;
	}


	/**
	 * @param allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType
	 * @param allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType
	 * @param linkTypesList
	 * @param searches
	 * @param searchIdsListDeduppedSorted
	 * @return
	 */
	private ChargeStateCounts_Merged_Results getPerChartData_KeyedOnLinkType( 
			// Map<[link type], Map<[search id],Map<[charge value],[count of charge value]>>>
			Map<String,Map<Integer,Map<Integer,Long>>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType,
		//  Map<[link type], Map<[search id],[count of PSMs]>>
			Map<String,Map<Integer,Long>> allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType,
			
			List<String> linkTypesList,
			List<SearchDTO> searches,
			List<Integer> searchIdsListDeduppedSorted ) {
		
		List<ChargeStateCountsResultsForLinkType> dataForChartPerLinkTypeList = new ArrayList<>( linkTypesList.size() );
		boolean foundData = false;

		for ( String linkType : linkTypesList ) {
			Map<Integer,Map<Integer,Long>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId =
					allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
			
			if ( allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId == null ) {
				ChargeStateCountsResultsForLinkType resultForLinkType =  new ChargeStateCountsResultsForLinkType();
				resultForLinkType.setLinkType( linkType );
				resultForLinkType.setDataFound( false );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			} else {
				Map<Integer,Long> allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId =
						allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );

				ChargeStateCountsResultsForLinkType resultForLinkType =
						getSingleChartData_ForLinkType( 
								allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId, 
								allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId, 
								searches );
				resultForLinkType.setLinkType( linkType );
				dataForChartPerLinkTypeList.add( resultForLinkType );
				foundData = true;
			}
		}
		
		ChargeStateCounts_Merged_Results results = new ChargeStateCounts_Merged_Results();
		results.setResultsPerLinkTypeList( dataForChartPerLinkTypeList );
		results.setSearchIds( searchIdsListDeduppedSorted );
		results.setFoundData( foundData );
		
		return results;
	}
	
	/**
	 * @param allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId
	 * @param searchIdsListDeduppedSorted
	 * @return
	 */
	private ChargeStateCountsResultsForLinkType getSingleChartData_ForLinkType( 
			// Map<[search id],Map<[charge value],[count of charge value]>>
			Map<Integer,Map<Integer,Long>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId,
			// Map<[search id],[count of PSMs]>
			Map<Integer,Long> allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId,
			List<SearchDTO> searches ) {
		
		//  First, reprocess maps into  Map<[charge value],Map<[search id],[count of charge value]>>
		Map<Integer,Map<Integer,Long>> allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue = new HashMap<>();
		
		for ( Map.Entry<Integer,Map<Integer,Long>> entryKeySearchId : allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId.entrySet() ) {
			Integer searchId = entryKeySearchId.getKey();
			Map<Integer,Long> allSearchesCombinedChargeValueCountMap = entryKeySearchId.getValue();
			for ( Map.Entry<Integer,Long> entryKeyChargeValue : allSearchesCombinedChargeValueCountMap.entrySet() ) {
				Integer chargeValue = entryKeyChargeValue.getKey();
				Long count = entryKeyChargeValue.getValue();
				
				Map<Integer,Long> allSearchesCombinedSearchIdCountMap = allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue.get( chargeValue );
				if ( allSearchesCombinedSearchIdCountMap == null ) {
					allSearchesCombinedSearchIdCountMap = new HashMap<>();
					allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue.put( chargeValue, allSearchesCombinedSearchIdCountMap );
				}
				allSearchesCombinedSearchIdCountMap.put( searchId, count );
			}
		}
		
		//   Create output object for creating a chart
		
		boolean dataFound = false;
		
		List<ChargeStateCountsResultsForChargeValue> dataForChartPerChargeValueList = new ArrayList<>( allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue.size() );
		
		for ( Map.Entry<Integer,Map<Integer,Long>> allSearchesCombinedSearchIdCountMapEntry : allSearchesCombinedSearchIdCountMap_Map_KeyedOnChargeValue.entrySet() ) {
			
			ChargeStateCountsResultsForChargeValue chargeStateCountsResultsForChargeValue = new ChargeStateCountsResultsForChargeValue();
			dataForChartPerChargeValueList.add( chargeStateCountsResultsForChargeValue );
			chargeStateCountsResultsForChargeValue.setCharge( allSearchesCombinedSearchIdCountMapEntry.getKey() );
			
			Map<Integer, ChargeStateCountsResultsForSearchId> countPerSearchIdMap_KeyProjectSearchId = new HashMap<>();
			chargeStateCountsResultsForChargeValue.setCountPerSearchIdMap_KeyProjectSearchId( countPerSearchIdMap_KeyProjectSearchId );
			
			Map<Integer,Long> allSearchesCombinedSearchIdCount = allSearchesCombinedSearchIdCountMapEntry.getValue();
			
			for ( SearchDTO search : searches ) {
				Long chargeCount = allSearchesCombinedSearchIdCount.get( search.getSearchId() );
				ChargeStateCountsResultsForSearchId resultForSearchId = new ChargeStateCountsResultsForSearchId();
				resultForSearchId.setSearchId( search.getSearchId() );
				if ( chargeCount == null ) {
					resultForSearchId.setCount( 0 );
				} else {
					resultForSearchId.setCount( chargeCount );
					dataFound = true;
				}
				
				Long totalCount = allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId.get( search.getSearchId() );
				if ( totalCount == null ) {
					resultForSearchId.setTotalCount( 0 );
				} else {
					resultForSearchId.setTotalCount( totalCount );
				}
				
				countPerSearchIdMap_KeyProjectSearchId.put( search.getProjectSearchId(), resultForSearchId );
			}
		};
		
		// Sort in charge value order
		Collections.sort( dataForChartPerChargeValueList, new Comparator<ChargeStateCountsResultsForChargeValue>() {
			@Override
			public int compare(ChargeStateCountsResultsForChargeValue o1, ChargeStateCountsResultsForChargeValue o2) {
				return o1.getCharge() - o2.getCharge();
			}
		});
		
		ChargeStateCountsResultsForLinkType result = new ChargeStateCountsResultsForLinkType();
		result.setDataForChartPerChargeValueList( dataForChartPerChargeValueList );
		result.setDataFound( dataFound );
		
		return result;
	}
	

	
	/////////////////////////////////////
	
	
	/**
	 * Return Map<[link type], Map<[search id],[count of PSMs]>>>
	 * 
	 * @param allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType
	 * @return
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	private Map<String,Map<Integer,Long>> getAllSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType (
		//  Get Maps of Charge values mapped to count, mapped by search id then link type
			//  Map<[link type], Map<[search id],Map<[charge value],[count of charge value]>>>
			Map<String,Map<Integer,Map<Integer,Long>>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType 
			) throws ProxlWebappDataException, Exception {
	
		//  Map<[link type], Map<[search id],[count of PSMs]>>
		Map<String,Map<Integer,Long>> output_allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType = new HashMap<>();

		for ( Map.Entry<String,Map<Integer,Map<Integer,Long>>> entryOnLinkType : allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType.entrySet() ) {
			Map<Integer,Long> output_allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId = new HashMap<>();
			output_allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType.put( entryOnLinkType.getKey(), output_allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId );
			
			for ( Map.Entry<Integer,Map<Integer,Long>> entryOnSearchId : entryOnLinkType.getValue().entrySet() ) {
				
				long psmTotalForChargeEntries = 0;
				
				for ( Map.Entry<Integer,Long> entryOnCharge : entryOnSearchId.getValue().entrySet() ) {
					psmTotalForChargeEntries += entryOnCharge.getValue();
				}
				output_allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId.put( entryOnSearchId.getKey(), psmTotalForChargeEntries );
			}
		}
		
		return output_allSearchesCombinedPSMCountMap_Map_KeyedOnSearchId_KeyedOnLinkType;
	}
	
	
	/////////////////////////////////////
	
	
	/**
	 * Return Map<[link type], Map<[search id],Map<[charge value],[count of charge value]>>>
	 * @param searches
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param searcherCutoffValuesRootLevel
	 * @return
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	private Map<String,Map<Integer,Map<Integer,Long>>> getAllSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType (
			List<SearchDTO> searches, 
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery,
			List<Integer> includeProteinSeqVIdsDecodedArray,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) throws ProxlWebappDataException, Exception {
	
		//  Map<[link type], Map<[search id],Map<[charge value],[count of charge value]>>>
		Map<String,Map<Integer,Map<Integer,Long>>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType = new HashMap<>();

		for ( SearchDTO searchDTO : searches ) {
			Integer projectSearchId = searchDTO.getProjectSearchId();
			Integer searchId = searchDTO.getSearchId();
			
			//  Get cutoffs for this project search id
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
			if ( searcherCutoffValuesSearchLevel == null ) {
				String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			PSM_DistinctChargeStatesResult psm_DistinctChargeStatesResult = 
					PSM_DistinctChargeStatesSearcher.getInstance()
					.getPSM_DistinctChargeStates( searchId, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, includeProteinSeqVIdsDecodedArray );
			
			/**
			 * Map <{Link Type},Map<{Charge Value},{count}>>
			 */
			Map<String,Map<Integer,Long>> chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue =
					psm_DistinctChargeStatesResult.getResultsChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue();
			
			//  Link Type includes 'dimer' which has be combined with 'unlinked'
			combineDimerCountsIntoUnlinkedCounts( chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue );
			
			//  Copy into overall map
			
			for ( Map.Entry<String,Map<Integer,Long>> entry : chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.entrySet() ) {
				Map<Integer,Map<Integer,Long>> allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId = 
						allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType.get( entry.getKey() );
				if ( allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId == null ) {
					allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId = new HashMap<>();
					allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType.put( entry.getKey(), allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId );
				}
				allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId.put( searchId, entry.getValue() );
			}
			
		}
		
		return allSearchesCombinedChargeValueCountMap_Map_KeyedOnSearchId_KeyedOnLinkType;
	}

	
	/**
	 * Combine Dimer Counts Into Unlinked Counts
	 * @param chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue
	 */
	private void combineDimerCountsIntoUnlinkedCounts( Map<String,Map<Integer,Long>> chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue ) {
		
		Map<Integer,Long> dimerValuesMap = chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.get( XLinkUtils.DIMER_TYPE_STRING );
		if ( dimerValuesMap == null ) {
			//  No Dimer values so skip
			return;  //  EARLY EXIT
		}
		
		Map<Integer,Long> unlinkedValuesMap = chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.get( XLinkUtils.UNLINKED_TYPE_STRING );
		if ( unlinkedValuesMap == null ) {
			//  No Unlinked values so simply copy dimer to unlinked and remove dimer
			chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.put( XLinkUtils.UNLINKED_TYPE_STRING, dimerValuesMap );
			chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.remove( XLinkUtils.DIMER_TYPE_STRING );
			return;  //  EARLY EXIT
		}
		
		Map<Integer,Long> unlinkedDimerCombinedValuesMap = 
				combineChargeCountsIntoUnlinkedCounts( unlinkedValuesMap, dimerValuesMap );
		chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.put( XLinkUtils.UNLINKED_TYPE_STRING, unlinkedDimerCombinedValuesMap );
		chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.remove( XLinkUtils.DIMER_TYPE_STRING );
		
	}
	

	/**
	 * Returns combined counts.
	 * @param chargeCountMap_KeyedOnChargeValue_1
	 * @param chargeCountMap_KeyedOnChargeValue_2
	 * @return combined counts
	 */
	private Map<Integer,Long> combineChargeCountsIntoUnlinkedCounts( 
			Map<Integer,Long> chargeCountMap_KeyedOnChargeValue_1,
			Map<Integer,Long> chargeCountMap_KeyedOnChargeValue_2 ) {
		
		Map<Integer,Long> resultCountMap_KeyedOnChargeValue = new HashMap<>();
		
		Set<Integer> chargeCountKeysCopy_2 = new HashSet<>( chargeCountMap_KeyedOnChargeValue_2.keySet() );
		
		//  First process unlinkedValuesMap
		for ( Map.Entry<Integer,Long> chargeValueEntry_1 : chargeCountMap_KeyedOnChargeValue_1.entrySet() ) {
			Long newChargeCount = chargeValueEntry_1.getValue();
			Long chargeCountEntry_2 = chargeCountMap_KeyedOnChargeValue_2.get( chargeValueEntry_1.getKey() );
			if ( chargeCountEntry_2 != null ) {
				//  Add chargeCountEntry_2 count to chargeValueEntry_1 count
				newChargeCount = chargeValueEntry_1.getValue().longValue() + chargeCountEntry_2.longValue();
				//  Remove entry from chargeCountKeys_2 copy of keyset
				chargeCountKeysCopy_2.remove( chargeValueEntry_1.getKey() );
			}
			resultCountMap_KeyedOnChargeValue.put( chargeValueEntry_1.getKey(), newChargeCount );
		}
		//  Next add any entries in chargeCountMap_KeyedOnChargeValue_2 not in chargeCountMap_KeyedOnChargeValue_1
		// (removed from chargeCountKeysCopy_2 the keys that are in both maps in previous loop)
		if ( ! chargeCountKeysCopy_2.isEmpty() ) {
			for ( Integer chargeCountKeysCopy_2_Entry : chargeCountKeysCopy_2 ) {
				Long chargeCount_2 = chargeCountMap_KeyedOnChargeValue_2.get( chargeCountKeysCopy_2_Entry );
				resultCountMap_KeyedOnChargeValue.put( chargeCountKeysCopy_2_Entry, chargeCount_2 );
			}
		}
		
		return resultCountMap_KeyedOnChargeValue;
	}


}
