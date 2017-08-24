package org.yeastrc.xlink.www.qc_data.psm_level_data_merged.main;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.PreMZ_Chart_For_PSMPeptideCutoffs_Merged_Results;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.PreMZ_Chart_For_PSMPeptideCutoffs_Merged_Results.PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.PreMZ_Chart_For_PSMPeptideCutoffs_Merged_Results.PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils.GetBoxPlotValuesResult;
import org.yeastrc.xlink.www.searcher.PreMZ_For_PSMPeptideCutoffsSearcher;
import org.yeastrc.xlink.www.searcher.PreMZ_For_PSMPeptideCutoffsSearcher.PreMZ_For_PSMPeptideCutoffsResult;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class PreMZ_Chart_For_PSMPeptideCutoffs_Merged {

	private static final Logger log = Logger.getLogger(PreMZ_Chart_For_PSMPeptideCutoffs_Merged.class);
	
	/**
	 * private constructor
	 */
	private PreMZ_Chart_For_PSMPeptideCutoffs_Merged(){}
	public static PreMZ_Chart_For_PSMPeptideCutoffs_Merged getInstance( ) throws Exception {
		PreMZ_Chart_For_PSMPeptideCutoffs_Merged instance = new PreMZ_Chart_For_PSMPeptideCutoffs_Merged();
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
	public PreMZ_Chart_For_PSMPeptideCutoffs_Merged_Results getPreMZ_Chart_For_PSMPeptideCutoffs_Merged( 			
			String filterCriteriaJSON, 
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId ) throws Exception {

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
		} 

		List<String> linkTypesList = new ArrayList<String>( mergedPeptideQueryJSONRoot.getLinkTypes().length );

		for ( String linkTypeFromWeb : mergedPeptideQueryJSONRoot.getLinkTypes() ) {
			String linkType = null;
			if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromWeb ) ) {
				linkType = XLinkUtils.CROSS_TYPE_STRING;
			} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromWeb ) ) {
				linkType = XLinkUtils.LOOP_TYPE_STRING;
			} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromWeb ) ) {
				linkType = XLinkUtils.UNLINKED_TYPE_STRING;
			} else {
				String msg = "linkType is invalid, linkTypeFromWeb: " + linkTypeFromWeb;
				log.error( msg );
				throw new Exception( msg );
			}
			linkTypesList.add( linkType );
		}

		
		//  Get Lists of preMZ mapped by search id then link type
		Map<String,Map<Integer,List<BigDecimal>>> allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType = 
				getAllSearchesCombinedPreMZList_Map_KeyedOnLinkType(searches, linkTypesForDBQuery, modsForDBQuery, searcherCutoffValuesRootLevel);
		
		PreMZ_Chart_For_PSMPeptideCutoffs_Merged_Results results = 
				getPerChartData_KeyedOnLinkType( 
						allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType, 
						linkTypesList, 
						searchIdsListDeduppedSorted );

		return results;
	}
	
	/**
	 * @param allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType
	 * @param linkTypesList
	 * @param searchIdsListDeduppedSorted
	 * @return
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PreMZ_Chart_For_PSMPeptideCutoffs_Merged_Results getPerChartData_KeyedOnLinkType( 
			Map<String,Map<Integer,List<BigDecimal>>> allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType, 
			List<String> linkTypesList,
			List<Integer> searchIdsListDeduppedSorted ) throws ProxlWebappInternalErrorException {
		
		List<PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList = new ArrayList<>( linkTypesList.size() );

		for ( String linkType : linkTypesList ) {
			Map<Integer,List<BigDecimal>> allSearchesCombinedPreMZList_Map_KeyedOnSearchId =
					allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
			
			if ( allSearchesCombinedPreMZList_Map_KeyedOnSearchId == null ) {
				PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType resultForLinkType =  new PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType();
				resultForLinkType.setLinkType( linkType );
				resultForLinkType.setDataFound( false );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			} else {
				PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType resultForLinkType =
						getSingleChartData_ForLinkType( allSearchesCombinedPreMZList_Map_KeyedOnSearchId, searchIdsListDeduppedSorted );
				resultForLinkType.setLinkType( linkType );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			}
		}
		
		PreMZ_Chart_For_PSMPeptideCutoffs_Merged_Results results = new PreMZ_Chart_For_PSMPeptideCutoffs_Merged_Results();
		results.setDataForChartPerLinkTypeList( dataForChartPerLinkTypeList );
		
		return results;
	}
	
	/**
	 * @param allSearchesCombinedPreMZList_Map_KeyedOnSearchId
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType getSingleChartData_ForLinkType( 
			Map<Integer,List<BigDecimal>> allSearchesCombinedPreMZList_Map_KeyedOnSearchId,
			List<Integer> searchIdsListDeduppedSorted ) throws ProxlWebappInternalErrorException {
		
		//   Create output object for creating a chart
		
		boolean dataFound = false;
		
		List<PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId> dataForChartPerSearchIdList = new ArrayList<>( searchIdsListDeduppedSorted.size() );
		
		for ( Integer searchId : searchIdsListDeduppedSorted ) {
			List<BigDecimal> preMZList = allSearchesCombinedPreMZList_Map_KeyedOnSearchId.get( searchId );
			if ( preMZList == null ) {
				PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId resultForSearchId = new PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId();
				resultForSearchId.setSearchId( searchId );
				resultForSearchId.setDataFound( false );
				dataForChartPerSearchIdList.add( resultForSearchId );
			} else {
				dataFound = true;
				PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId resultForSearchId = 
						getSingleChartData_ForSearchId( preMZList );
				resultForSearchId.setSearchId( searchId );
				resultForSearchId.setDataFound( true );
				dataForChartPerSearchIdList.add( resultForSearchId );
			}
		}
		
		PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType result = new PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType();
		result.setDataForChartPerSearchIdList( dataForChartPerSearchIdList );
		result.setDataFound( dataFound );
		
		return result;
	}

	

	/**
	 * @param allSearchesCombinedPreMZList_Map_KeyedOnSearchId
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId getSingleChartData_ForSearchId( List<BigDecimal> preMZList ) throws ProxlWebappInternalErrorException {
		
		List<Double> values = new ArrayList<>( preMZList.size() );
		
		for( BigDecimal value : preMZList ) {
			values.add( value.doubleValue() );
		}
		
		GetBoxPlotValuesResult getBoxPlotValuesResult =
				BoxPlotUtils.getInstance().getBoxPlotValues( values );
		
		double chartIntervalMax = getBoxPlotValuesResult.getChartIntervalMax();
		double chartIntervalMin = getBoxPlotValuesResult.getChartIntervalMin();
		
		// Add the preMZ that are not within max (highcutoff) and min (lowcutoff) to a list and send to web app
		List<Double> preMZ_outliers = new ArrayList<Double>( preMZList.size() );
		for( BigDecimal preMZ : preMZList ) {
			double preMZDouble = preMZ.doubleValue();
			if ( preMZDouble < chartIntervalMin || preMZDouble > chartIntervalMax ) {
				preMZ_outliers.add( preMZDouble );
			}
		}
		
		PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId result = new PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId();
		
		result.setChartIntervalMax( getBoxPlotValuesResult.getChartIntervalMax() );
		result.setChartIntervalMin( getBoxPlotValuesResult.getChartIntervalMin() );
		result.setFirstQuartile( getBoxPlotValuesResult.getFirstQuartile() );
		result.setThirdQuartile( getBoxPlotValuesResult.getThirdQuartile() );
		result.setMedian( getBoxPlotValuesResult.getMedian() );
		
		result.setPreMZ_outliers( preMZ_outliers );
		
		result.setDataFound(true);
		
		return result;
	}
			
	///////////////////////////////////
	
	/**
	 * Get Lists of preMZ mapped by search id then link type
	 * @param searches
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param searcherCutoffValuesRootLevel
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	private Map<String,Map<Integer,List<BigDecimal>>> getAllSearchesCombinedPreMZList_Map_KeyedOnLinkType(
			List<SearchDTO> searches, 
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) throws ProxlWebappDataException, Exception {
		
		//  Get Lists of preMZ mapped by search id then link type
		/**
		 * Map <{Link Type},,Map<<Search id>,List<{preMZ}>>>
		 */
		Map<String,Map<Integer,List<BigDecimal>>> allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType = new HashMap<>();


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
			
			for ( Map.Entry<String,List<BigDecimal>> preMZList_Map_Entry : preMZList_Map_KeyedOnLinkType.entrySet() ) {
				String linkType = preMZList_Map_Entry.getKey();
				Map<Integer,List<BigDecimal>> allSearchesCombinedPreMZList_Map_KeyedOnSearchId = 
						allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
				if ( allSearchesCombinedPreMZList_Map_KeyedOnSearchId == null ) {
					allSearchesCombinedPreMZList_Map_KeyedOnSearchId = new HashMap<>();
					allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType.put( linkType, allSearchesCombinedPreMZList_Map_KeyedOnSearchId );
				}
				allSearchesCombinedPreMZList_Map_KeyedOnSearchId.put( searchId, preMZList_Map_Entry.getValue() );
			}
		}
		
		return allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType;
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
