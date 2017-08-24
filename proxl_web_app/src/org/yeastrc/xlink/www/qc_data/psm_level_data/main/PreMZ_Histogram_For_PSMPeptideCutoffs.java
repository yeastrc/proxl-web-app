package org.yeastrc.xlink.www.qc_data.psm_level_data.main;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PreMZ_Histogram_For_PSMPeptideCutoffsResults;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PreMZ_Histogram_For_PSMPeptideCutoffsResults.PreMZ_Histogram_For_PSMPeptideCutoffsResultsChartBucket;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PreMZ_Histogram_For_PSMPeptideCutoffsResults.PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType;
import org.yeastrc.xlink.www.searcher.PreMZ_For_PSMPeptideCutoffsSearcher;
import org.yeastrc.xlink.www.searcher.PreMZ_For_PSMPeptideCutoffsSearcher.PreMZ_For_PSMPeptideCutoffsResult;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Get PreMZ Histogram from Scans where associated PSMs meet on criteria, including Reported Peptide and PSM cutoffs
 *
 */
public class PreMZ_Histogram_For_PSMPeptideCutoffs {
	
	private static final Logger log = Logger.getLogger(PreMZ_Histogram_For_PSMPeptideCutoffs.class);
	
	/**
	 * private constructor
	 */
	private PreMZ_Histogram_For_PSMPeptideCutoffs(){}
	public static PreMZ_Histogram_For_PSMPeptideCutoffs getInstance( ) throws Exception {
		PreMZ_Histogram_For_PSMPeptideCutoffs instance = new PreMZ_Histogram_For_PSMPeptideCutoffs();
		return instance;
	}
		
	/**
	 * @param filterCriteriaJSON
	 * @param searches
	 * @param searchesMapOnSearchId
	 * @return
	 * @throws Exception
	 */
	public PreMZ_Histogram_For_PSMPeptideCutoffsResults getPreMZ_Histogram_For_PSMPeptideCutoffs( 			
			String filterCriteriaJSON, 
			List<SearchDTO> searches
			) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
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

		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = mergedPeptideQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		return getResultsForParams(
				searches, mergedPeptideQueryJSONRoot, searcherCutoffValuesRootLevel);
	}
	
	public PreMZ_Histogram_For_PSMPeptideCutoffsResults getResultsForParams(
			List<SearchDTO> searches, 
			MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot, 
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel)
			throws Exception, ProxlWebappDataException {

		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( mergedPeptideQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();
		
		Set<String> selectedLinkTypes = new HashSet<>();
		
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
		
		//  Get Lists of preMZ mapped by link type
		Map<String,List<BigDecimal>> allSearchesCombinedPreMZList_Map_KeyedOnLinkType = 
				getAllSearchesCombinedPreMZList_Map_KeyedOnLinkType(searches, linkTypesForDBQuery, modsForDBQuery, searcherCutoffValuesRootLevel);
		
		
		Map<String, PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType> preMZInChartBuckets_KeyedOnLinkType = 
				getPreMZInChartBuckets_KeyedOnLinkType( allSearchesCombinedPreMZList_Map_KeyedOnLinkType );
		
		PreMZ_Histogram_For_PSMPeptideCutoffsResults preMZ_Histogram_For_PSMPeptideCutoffsResults = new PreMZ_Histogram_For_PSMPeptideCutoffsResults();
		
		//  copy map to array for output, in a specific order
		List<PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsPerLinkTypeList = new ArrayList<>( 3 );
		
		addToOutputListForLinkType( XLinkUtils.CROSS_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, preMZInChartBuckets_KeyedOnLinkType );
		addToOutputListForLinkType( XLinkUtils.LOOP_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, preMZInChartBuckets_KeyedOnLinkType );
		addToOutputListForLinkType( XLinkUtils.UNLINKED_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, preMZInChartBuckets_KeyedOnLinkType );
		
		preMZ_Histogram_For_PSMPeptideCutoffsResults.setDataForChartPerLinkTypeList( resultsPerLinkTypeList );
		
		return preMZ_Histogram_For_PSMPeptideCutoffsResults;
	}
	

	/**
	 * @param linkType
	 * @param countForLinkTypeList
	 * @param countForLinkType_ByLinkType
	 */
	private void addToOutputListForLinkType( 
			String linkType, 
			Set<String> selectedLinkTypes, 
			List<PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsPerLinkTypeList,
			Map<String, PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType> preMZInChartBuckets_KeyedOnLinkType ) {
		
		if ( ! selectedLinkTypes.contains( linkType ) ) {
			//  link type not selected
			return; // EARLY EXIT
		}
		
		PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType valueForLinkType = 
				preMZInChartBuckets_KeyedOnLinkType.get( linkType );
		
		if ( valueForLinkType == null ) {
			//  No data for link type so create empty entry
			valueForLinkType = new PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
			valueForLinkType.setLinkType( linkType );
			resultsPerLinkTypeList.add( valueForLinkType );
			return; // EARLY EXIT
		}
		
		resultsPerLinkTypeList.add( valueForLinkType );
	}
	
	/**
	 * @param allSearchesCombinedPreMZList_Map_KeyedOnLinkType
	 * @return
	 */
	private Map<String, PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType>getPreMZInChartBuckets_KeyedOnLinkType( 
			Map<String,List<BigDecimal>> allSearchesCombinedPreMZList_Map_KeyedOnLinkType ) {
		
		Map<String, PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType> preMZInChartBuckets_KeyedOnLinkType = new HashMap<>();
		
		for ( Map.Entry<String,List<BigDecimal>> entry : allSearchesCombinedPreMZList_Map_KeyedOnLinkType.entrySet() ) {
			
			PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType preMZ_HistogramData =
					getpreMZ_HistogramData( entry.getValue() );
			preMZ_HistogramData.setLinkType( entry.getKey() );
			preMZInChartBuckets_KeyedOnLinkType.put( entry.getKey(), preMZ_HistogramData );
		}
		
		
		return preMZInChartBuckets_KeyedOnLinkType;
	}

	/**
	 * @param preMZList
	 * @return
	 */
	private PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType getpreMZ_HistogramData( 
			List<BigDecimal> preMZList ) {
		
		int numScans = preMZList.size();
		boolean firstOverallpreMZEntry = true;
		//  Find max and min values
		BigDecimal preMZMin = null;
		BigDecimal preMZMax =  null;
		for ( BigDecimal preMZ : preMZList ) {
			if ( firstOverallpreMZEntry  ) {
				firstOverallpreMZEntry = false;
				preMZMin = preMZ;
				preMZMax = preMZ;
			} else {
				if ( preMZ.compareTo( preMZMin ) < 0 ) {
					preMZMin = preMZ;
				}
				if ( preMZ.compareTo( preMZMax ) > 0  ) {
					preMZMax = preMZ;
				}
			}
		}
		BigDecimal preMZMaxMinusMin = preMZMax.subtract( preMZMin );
		
		double preMZMinAsDouble = preMZMin.doubleValue();
		double preMZMaxMinusMinAsDouble = preMZMaxMinusMin.doubleValue();
		
		//  Process data into bins
		int binCount = (int) ( Math.sqrt( preMZList.size() ) );
		int[] preMZCounts = new int[ binCount ];
		double binSizeAsDouble = ( preMZMaxMinusMinAsDouble ) / binCount;
		
		for ( BigDecimal preMZ : preMZList ) {
			double preMZFraction = ( preMZ.doubleValue() - preMZMinAsDouble ) / preMZMaxMinusMinAsDouble;
			int bin = (int) ( (  preMZFraction ) * binCount );
			if ( bin < 0 ) {
				bin = 0;
			} else if ( bin >= binCount ) {
				bin = binCount - 1;
			} 
			preMZCounts[ bin ]++;
		}
		
		List<PreMZ_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets = new ArrayList<>();
		double binHalf = binSizeAsDouble / 2 ;
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		for ( int binIndex = 0; binIndex < preMZCounts.length; binIndex++ ) {
			PreMZ_Histogram_For_PSMPeptideCutoffsResultsChartBucket chartBucket = new PreMZ_Histogram_For_PSMPeptideCutoffsResultsChartBucket();
			chartBuckets.add( chartBucket );
			int preMZCount = preMZCounts[ binIndex ];
			double binStartDouble = ( ( binIndex * binSizeAsDouble ) ) + preMZMinAsDouble;
			if ( binIndex == 0 && binStartDouble < 0.1 ) {
				chartBucket.setBinStart( 0 );
			} else { 
				int binStart = (int)Math.round( binStartDouble );
				chartBucket.setBinStart( binStart );
			}
			int binEnd = (int)Math.round( ( ( binIndex + 1 ) * binSizeAsDouble ) + preMZMinAsDouble );
			chartBucket.setBinEnd( binEnd );
			double binMiddleDouble = binStartDouble + binHalf;
			chartBucket.setBinCenter( binMiddleDouble );
			chartBucket.setCount( preMZCount );
		}
		
		PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType result = new PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
		
		result.setChartBuckets( chartBuckets );
		result.setNumScans( numScans );
		result.setPreMZMax(preMZMax);
		result.setPreMZMin( preMZMin );
		
		return result;
	}
	
	
	/**
	 * @param searches
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param searcherCutoffValuesRootLevel
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	private Map<String,List<BigDecimal>> getAllSearchesCombinedPreMZList_Map_KeyedOnLinkType(
			List<SearchDTO> searches, 
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) throws ProxlWebappDataException, Exception {
		
		/**
		 * Map <{Link Type},List<{preMZ}>>
		 */
		Map<String,List<BigDecimal>> allSearchesCombinedPreMZList_Map_KeyedOnLinkType = null;

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
			
			PreMZ_For_PSMPeptideCutoffsResult preMZ_For_PSMPeptideCutoffsResult = 
					PreMZ_For_PSMPeptideCutoffsSearcher.getInstance()
					.getPreMZ_For_PSMPeptideCutoffs( searchId, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery );
			
			/**
			 * Map <{Link Type},List<{preMZ}>>
			 */
			Map<String,List<BigDecimal>> preMZList_Map_KeyedOnLinkType =
					preMZ_For_PSMPeptideCutoffsResult.getResultsPreMZList_Map_KeyedOnLinkType();
			
			//  Link Type includes 'dimer' which has be combined with 'unlinked'
			combineDimerListIntoUnlinkedList( preMZList_Map_KeyedOnLinkType );
			
			//  Combine with values from other searches
			if ( allSearchesCombinedPreMZList_Map_KeyedOnLinkType == null ) {
				//  No values from other searches so just assign it.
				allSearchesCombinedPreMZList_Map_KeyedOnLinkType = 
						preMZList_Map_KeyedOnLinkType;
			} else {
				combineListsWithAllSearchesLists( preMZList_Map_KeyedOnLinkType, allSearchesCombinedPreMZList_Map_KeyedOnLinkType );
			}
			
		}
		
		return allSearchesCombinedPreMZList_Map_KeyedOnLinkType;
	}

	/**
	 * Combine Counts With All Searches Counts
	 * @param thisSearchChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue
	 * @param allSearchesCombinedChargeCountMap_KeyedOnLinkType_KeyedOnChargeValue
	 */
	private void combineListsWithAllSearchesLists( 
			Map<String,List<BigDecimal>> thisSearchPreMZList_Map_KeyedOnLinkType,
			Map<String,List<BigDecimal>> allSearchesCombinedPreMZList_Map_KeyedOnLinkType ) {

		Set<String> allSearchesChargeCountMap_Keyset_Copy = new HashSet<>( allSearchesCombinedPreMZList_Map_KeyedOnLinkType.keySet() );
		Set<String> thisSearchChargeCountMap_Keyset_Copy = new HashSet<>( thisSearchPreMZList_Map_KeyedOnLinkType.keySet() );
		
		for ( String allSearchesLinkType : allSearchesChargeCountMap_Keyset_Copy ) {
			List<BigDecimal> allSearchesPreMZList = allSearchesCombinedPreMZList_Map_KeyedOnLinkType.get( allSearchesLinkType );
			
			List<BigDecimal> thisSearchPreMZList = 
					thisSearchPreMZList_Map_KeyedOnLinkType.get( allSearchesLinkType );
			if ( thisSearchPreMZList != null ) {
				//  combine this search with all searches for this link type
				allSearchesPreMZList.addAll( thisSearchPreMZList );
				thisSearchChargeCountMap_Keyset_Copy.remove( allSearchesLinkType );
			}
			
		}
		//  Next add any entries in thisSearch Keyset copy not in allSearches Map 
		//  (removed keys from this search keyset copy that are in both maps above)
		for ( String thisSearchLinkType : thisSearchChargeCountMap_Keyset_Copy ) {
			List<BigDecimal> thisSearchChargeCountMap_KeyedOnChargeValue = thisSearchPreMZList_Map_KeyedOnLinkType.get( thisSearchLinkType );
			allSearchesCombinedPreMZList_Map_KeyedOnLinkType.put( thisSearchLinkType, thisSearchChargeCountMap_KeyedOnChargeValue );
		}
	}

	/**
	 * Combine Dimer Counts Into Unlinked Counts
	 * @param chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue
	 */
	private void combineDimerListIntoUnlinkedList( Map<String,List<BigDecimal>> preMZList_Map_KeyedOnLinkType ) {
		
		List<BigDecimal> dimerValuesList = preMZList_Map_KeyedOnLinkType.get( XLinkUtils.DIMER_TYPE_STRING );
		if ( dimerValuesList == null ) {
			//  No Dimer values so skip
			return;  //  EARLY EXIT
		}
		
		List<BigDecimal> unlinkedValuesList = preMZList_Map_KeyedOnLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
		if ( unlinkedValuesList == null ) {
			//  No Unlinked values so simply copy dimer to unlinked and remove dimer
			preMZList_Map_KeyedOnLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, dimerValuesList );
			preMZList_Map_KeyedOnLinkType.remove( XLinkUtils.DIMER_TYPE_STRING );
			return;  //  EARLY EXIT
		}
		
		unlinkedValuesList.addAll( dimerValuesList );
		preMZList_Map_KeyedOnLinkType.remove( XLinkUtils.DIMER_TYPE_STRING );
		
	}
	
}
