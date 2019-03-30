package org.yeastrc.xlink.www.qc_data.psm_level_data.main;

import java.io.IOException;
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
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.ChargeStateCountsResults;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.ChargeStateCountsResults.ChargeStateCountsResultsPerChargeValue;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.ChargeStateCountsResults.ChargeStateCountsResultsPerLinkType;
import org.yeastrc.xlink.www.searcher.PSM_DistinctChargeStatesSearcher;
import org.yeastrc.xlink.www.searcher.PSM_DistinctChargeStatesSearcher.PSM_DistinctChargeStatesResult;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Get Charge State Counts
 *
 */
public class ChargeStateCounts {
	
	private static final Logger log = LoggerFactory.getLogger( ChargeStateCounts.class);
	
	/**
	 * private constructor
	 */
	private ChargeStateCounts(){}
	public static ChargeStateCounts getInstance( ) throws Exception {
		ChargeStateCounts instance = new ChargeStateCounts();
		return instance;
	}
		
	/**
	 * @param filterCriteriaJSON
	 * @param projectSearchIdsListDeduppedSorted
	 * @param searches
	 * @param searchesMapOnSearchId
	 * @return
	 * @throws Exception
	 */
	public ChargeStateCountsResults getChargeStateCounts( 			
			String filterCriteriaJSON, 
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		Set<String> selectedLinkTypes = new HashSet<>();
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		//   deserialize 
		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = null;
		try {
			mergedPeptideQueryJSONRoot = jacksonJSON_Mapper.readValue( filterCriteriaJSON, MergedPeptideQueryJSONRoot.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', JsonParseException.  filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', JsonMappingException.  filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', IOException.  filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		}

		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( mergedPeptideQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();
		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = mergedPeptideQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} else {
			for ( String linkType : mergedPeptideQueryJSONRoot.getLinkTypes() ) {
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {
					selectedLinkTypes.add( XLinkUtils.CROSS_TYPE_STRING );
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {
					selectedLinkTypes.add( XLinkUtils.LOOP_TYPE_STRING );
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkType ) ) {
					selectedLinkTypes.add( XLinkUtils.UNLINKED_TYPE_STRING );
				} else {
					String msg = "linkType is invalid, linkType: " + linkType;
					log.error( msg );
					throw new Exception( msg );
				}
			}
		}
		

		/**
		 * Map <{Link Type},Map<{Charge Value},{count}>>
		 */
		Map<String,Map<Integer,Long>> allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue = null;

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
					.getPSM_DistinctChargeStates( searchId, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery );
			
			/**
			 * Map <{Link Type},Map<{Charge Value},{count}>>
			 */
			Map<String,Map<Integer,Long>> chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue =
					psm_DistinctChargeStatesResult.getResultsChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue();
			
			//  Link Type includes 'dimer' which has be combined with 'unlinked'
			combineDimerCountsIntoUnlinkedCounts( chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue );
			
			//  Combine with values from other searches
			if ( allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue == null ) {
				//  No values from other searches so just assign it.
				allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue = 
						chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue;
			} else {
				combineCountsWithAllSearchesCounts( chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue, allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue );
			}
			
		}
		
		ChargeStateCountsResults chargeStateCountsResults = new ChargeStateCountsResults();
		
		//  copy map to array for output, in a specific order
		List<ChargeStateCountsResultsPerLinkType> resultsPerLinkTypeList = new ArrayList<>( 3 );
		
		addToOutputListForLinkType( XLinkUtils.CROSS_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue );
		addToOutputListForLinkType( XLinkUtils.LOOP_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue );
		addToOutputListForLinkType( XLinkUtils.UNLINKED_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue );
		
		chargeStateCountsResults.setResultsPerLinkTypeList( resultsPerLinkTypeList );
		
		return chargeStateCountsResults;
	}

	
	/**
	 * @param linkType
	 * @param countForLinkTypeList
	 * @param countForLinkType_ByLinkType
	 */
	private void addToOutputListForLinkType( 
			String linkType, 
			Set<String> selectedLinkTypes, 
			List<ChargeStateCountsResultsPerLinkType> resultsPerLinkTypeList,
			Map<String,Map<Integer,Long>> allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue ) {
		
		if ( ! selectedLinkTypes.contains( linkType ) ) {
			//  link type not selected
			return; // EARLY EXIT
		}
		
		Map<Integer,Long> allSearchesCombinedChargeCountMap_KeyedOnChargeValue = 
				allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.get( linkType );
		
		if ( allSearchesCombinedChargeCountMap_KeyedOnChargeValue == null ) {
			//  No data for link type so create empty entry
			ChargeStateCountsResultsPerLinkType chargeStateCountsResultsPerLinkType = new ChargeStateCountsResultsPerLinkType();
			chargeStateCountsResultsPerLinkType.setLinkType( linkType );
			resultsPerLinkTypeList.add( chargeStateCountsResultsPerLinkType );
			return; // EARLY EXIT
		}
		
		ChargeStateCountsResultsPerLinkType chargeStateCountsResultsPerLinkType = new ChargeStateCountsResultsPerLinkType();
		chargeStateCountsResultsPerLinkType.setLinkType( linkType );
		resultsPerLinkTypeList.add( chargeStateCountsResultsPerLinkType );

		List<ChargeStateCountsResultsPerChargeValue> resultsPerChargeValueList = new ArrayList<>( allSearchesCombinedChargeCountMap_KeyedOnChargeValue.size() );
		chargeStateCountsResultsPerLinkType.setResultsPerChargeValueList( resultsPerChargeValueList );
		
		for ( Map.Entry<Integer,Long> entry : allSearchesCombinedChargeCountMap_KeyedOnChargeValue.entrySet() ) {
			ChargeStateCountsResultsPerChargeValue chargeStateCountsResultsPerChargeValue = new ChargeStateCountsResultsPerChargeValue();
			chargeStateCountsResultsPerChargeValue.setChargeValue( entry.getKey() );
			chargeStateCountsResultsPerChargeValue.setChargeCount( entry.getValue() );
			resultsPerChargeValueList.add( chargeStateCountsResultsPerChargeValue );
		}
		// Sort in charge value order
		Collections.sort( resultsPerChargeValueList, new Comparator<ChargeStateCountsResultsPerChargeValue>() {
			@Override
			public int compare(ChargeStateCountsResultsPerChargeValue o1, ChargeStateCountsResultsPerChargeValue o2) {
				return o1.getChargeValue() - o2.getChargeValue();
			}
		});
				
	}
	
	/**
	 * Combine Counts With All Searches Counts
	 * @param thisSearchChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue
	 * @param allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue
	 */
	private void combineCountsWithAllSearchesCounts( 
			Map<String,Map<Integer,Long>> thisSearchChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue,
			Map<String,Map<Integer,Long>> allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue ) {

		Set<String> allSearchesChargeCountMap_Keyset_Copy = new HashSet<>( allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.keySet() );
		Set<String> thisSearchChargeCountMap_Keyset_Copy = new HashSet<>( thisSearchChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.keySet() );
		
		for ( String allSearchesLinkType : allSearchesChargeCountMap_Keyset_Copy ) {
			Map<Integer,Long> allSearchesChargeCountMap_KeyedOnChargeValue = allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.get( allSearchesLinkType );
			
			Map<Integer,Long> thisSearchChargeCountMap_KeyedOnChargeValue = 
					thisSearchChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.get( allSearchesLinkType );
			if ( thisSearchChargeCountMap_KeyedOnChargeValue != null ) {
				//  combine this search with all searches for this link type

				Map<Integer,Long> chargeCountCombinedValuesMap = 
						combineChargeCountsIntoUnlinkedCounts( allSearchesChargeCountMap_KeyedOnChargeValue, thisSearchChargeCountMap_KeyedOnChargeValue );
				allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.put( allSearchesLinkType, chargeCountCombinedValuesMap );
				thisSearchChargeCountMap_Keyset_Copy.remove( allSearchesLinkType );
			}
			
		}
		//  Next add any entries in thisSearch Keyset copy not in allSearches Map 
		//  (removed keys from this search keyset copy that are in both maps above)
		for ( String thisSearchLinkType : thisSearchChargeCountMap_Keyset_Copy ) {
			Map<Integer,Long> thisSearchChargeCountMap_KeyedOnChargeValue = thisSearchChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.get( thisSearchLinkType );
			allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue.put( thisSearchLinkType, thisSearchChargeCountMap_KeyedOnChargeValue );
		}
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
