package org.yeastrc.xlink.www.qc_data.psm_level_data_merged.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results.PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType;
import org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects.PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results.PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils.GetBoxPlotValuesResult;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Peptide Lengths VS PSM Count Merged Boxplot Chart
 *
 */
public class PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged {

	private static final Logger log = Logger.getLogger(PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged.class);

	public enum ForDownload { YES, NO }
	
	/**
	 * private constructor
	 */
	private PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged(){}
	public static PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged getInstance( ) throws Exception {
		PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged instance = new PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged();
		return instance;
	}
	

	/**
	 * Response from call to getPeptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged(...)
	 *
	 */
	public static class PeptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Method_Response {
		
		PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results peptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Results;

		//  Get PSM Count mapped by peptideLength then search id then link type
		/**
		 * Map <{Link Type},Map<<Search id>,Map<{peptideLength},{PSM Count}>>
		 */
		Map<String,Map<Integer,Map<Integer,MutableInt>>> allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType; 
				
		public PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results getPeptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Results() {
			return peptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Results;
		}

		public void setPeptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Results(
				PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results peptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Results) {
			this.peptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Results = peptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Results;
		}

		/**
		 * Map <{Link Type},Map<<Search id>,Map<{peptideLength},{PSM Count}>>
		 * @return
		 */
		public Map<String, Map<Integer, Map<Integer, MutableInt>>> getAllSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType() {
			return allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType;
		}

		public void setAllSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType(
				Map<String, Map<Integer, Map<Integer, MutableInt>>> allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType) {
			this.allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType = allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType;
		}
	}
	

	/**
	 * @param forDownload
	 * @param filterCriteriaJSON
	 * @param searches
	 * @return
	 * @throws Exception
	 */
	public PeptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Method_Response getPeptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged( 			
			ForDownload forDownload,
			String filterCriteriaJSON, 
			List<SearchDTO> searches ) throws Exception {

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

		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} 
		String[] linkTypesFromURL = mergedPeptideQueryJSONRoot.getLinkTypes();
		
		
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
			mergedPeptideQueryJSONRoot.setLinkTypes( linkTypesFromURLUpdated );
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
			
		//  Get PSM Count mapped by peptideLength then search id then link type
		/**
		 * Map <{Link Type},Map<<Search id>,Map<{peptideLength},{PSM Count}>>
		 */
		Map<String,Map<Integer,Map<Integer,MutableInt>>> allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType = 
				getAllSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnLinkType(searches, linkTypesForDBQuery, modsForDBQuery, searcherCutoffValuesRootLevel);
		
		PeptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Method_Response methodResponse = new PeptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Method_Response();
		
		methodResponse.allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType = allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType;
		
		if ( forDownload == ForDownload.YES ) {
			
			return methodResponse;  //  EARLY RETURN
		}
		
		PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results peptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results = 
				getPerChartData_KeyedOnLinkType( 
						allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType, 
						linkTypesList, 
						searches );

		methodResponse.peptideLengthVsPSMCount_Chart_For_PSMPeptideCutoffs_Merged_Results = peptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results;
		
		return methodResponse;
	}
	

	/**
	 * @param allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType
	 * @param linkTypesList
	 * @param searchIdsListDeduppedSorted
	 * @return
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results getPerChartData_KeyedOnLinkType( 
			/**
			 * Map <{Link Type},Map<<Search id>,Map<{peptideLength},{PSM Count}>>
			 */
			Map<String,Map<Integer,Map<Integer,MutableInt>>> allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType, 
			List<String> linkTypesList,
			List<SearchDTO> searches ) throws ProxlWebappInternalErrorException {
		
		List<PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType> dataForChartPerLinkTypeList = new ArrayList<>( linkTypesList.size() );

		for ( String linkType : linkTypesList ) {
			Map<Integer,Map<Integer,MutableInt>> allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId =
					allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
			
			if ( allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId == null ) {
				PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType resultForLinkType =  new PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType();
				resultForLinkType.setLinkType( linkType );
				resultForLinkType.setDataFound( false );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			} else {
				PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType resultForLinkType =
						getSingleChartData_ForLinkType( allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId, searches );
				resultForLinkType.setLinkType( linkType );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			}
		}
		
		PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results results = new PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results();
		results.setDataForChartPerLinkTypeList( dataForChartPerLinkTypeList );
		
		return results;
	}

	/**
	 * @param allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_Map_KeyedOnSearchId
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType getSingleChartData_ForLinkType( 
			Map<Integer,Map<Integer,MutableInt>> allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_Map_KeyedOnSearchId,
			List<SearchDTO> searches ) throws ProxlWebappInternalErrorException {
		
		//   Create output object for creating a chart
		
		boolean dataFound = false;
		
		Map<Integer, PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId> dataForChartPerSearchIdMap_KeyProjectSearchId = new HashMap<>();
		
		for ( SearchDTO search : searches ) {
			Integer searchId = search.getSearchId();
			Integer projectSearchId = search.getProjectSearchId();
					
			Map<Integer,MutableInt> psmCount_Map_Keyed_PeptideLength = allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_Map_KeyedOnSearchId.get( searchId );
			if ( psmCount_Map_Keyed_PeptideLength == null ) {
				PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId resultForSearchId = new PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId();
				resultForSearchId.setSearchId( searchId );
				resultForSearchId.setDataFound( false );
				dataForChartPerSearchIdMap_KeyProjectSearchId.put( projectSearchId, resultForSearchId );
			} else {
				dataFound = true;
				PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId resultForSearchId = 
						getSingleChartData_ForSearchId( psmCount_Map_Keyed_PeptideLength );
				resultForSearchId.setSearchId( searchId );
				resultForSearchId.setDataFound( true );
				dataForChartPerSearchIdMap_KeyProjectSearchId.put( projectSearchId, resultForSearchId );
			}
		}
		
		PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType result = new PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType();
		result.setDataForChartPerSearchIdMap_KeyProjectSearchId( dataForChartPerSearchIdMap_KeyProjectSearchId );
		result.setDataFound( dataFound );
		
		return result;
	}

	

	/**
	 * @param psmCount_Map_Keyed_PeptideLength
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 */
	private PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId getSingleChartData_ForSearchId( Map<Integer,MutableInt> psmCount_Map_Keyed_PeptideLength ) throws ProxlWebappInternalErrorException {
		
		List<Double> values = new ArrayList<>( psmCount_Map_Keyed_PeptideLength.size() * 300 );
		
		//  Add peptide length to values List, 1 entry per psm
		for( Map.Entry<Integer,MutableInt> entry : psmCount_Map_Keyed_PeptideLength.entrySet() ) {
			Double peptideLength = entry.getKey().doubleValue();
			int psmCount = entry.getValue().intValue();
			for ( int counter = 0; counter < psmCount; counter++ ) {
				values.add( peptideLength );
			}
		}
		
		GetBoxPlotValuesResult getBoxPlotValuesResult =
				BoxPlotUtils.getInstance().getBoxPlotValues( values );
		
		double chartIntervalMax = getBoxPlotValuesResult.getChartIntervalMax();
		double chartIntervalMin = getBoxPlotValuesResult.getChartIntervalMin();
		
		// Add the peptideLength that are not within max (highcutoff) and min (lowcutoff) to a list and send to web app
		List<Integer> peptideLength_outliers = new ArrayList<>( psmCount_Map_Keyed_PeptideLength.size() );
		for( Map.Entry<Integer,MutableInt> entry : psmCount_Map_Keyed_PeptideLength.entrySet() ) {
			double peptideLengthDouble = entry.getKey().doubleValue();
			if ( peptideLengthDouble < chartIntervalMin || peptideLengthDouble > chartIntervalMax ) {
				peptideLength_outliers.add( (int)peptideLengthDouble );
			}
		}
		
		PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId result = new PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId();
		
		result.setChartIntervalMax( getBoxPlotValuesResult.getChartIntervalMax() );
		result.setChartIntervalMin( getBoxPlotValuesResult.getChartIntervalMin() );
		result.setFirstQuartile( getBoxPlotValuesResult.getFirstQuartile() );
		result.setThirdQuartile( getBoxPlotValuesResult.getThirdQuartile() );
		result.setMedian( getBoxPlotValuesResult.getMedian() );
		
		result.setPeptideLengths_outliers( peptideLength_outliers );
		
		result.setDataFound(true);
		
		return result;
	}

	///////////////////////////////////
	
	/**
	 * Get PSM Count mapped by peptideLength then search id then link type
	 * @param searches
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param searcherCutoffValuesRootLevel
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	private Map<String,Map<Integer,Map<Integer,MutableInt>>> getAllSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnLinkType(
			List<SearchDTO> searches, 
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) throws ProxlWebappDataException, Exception {
		
		//  Get PSM Count mapped by peptideLength then search id then link type
		/**
		 * Map <{Link Type},Map<<Search id>,Map<{peptideLength},{PSM Count}>>
		 */
		Map<String,Map<Integer,Map<Integer,MutableInt>>> allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType = new HashMap<>();

		//  Cache peptideDTO ById locally
		Map<Integer,PeptideDTO> peptideDTO_MappedById = new HashMap<>();

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
			
			// Map <{Link Type},Map<{peptideLength},<{PSM Count}>>
			Map<String, Map<Integer,MutableInt>> peptideLengthMap_Map_KeyedOnLinkType = 
					getPeptideLengths_SingleSearch(
							linkTypesForDBQuery, 
							modsForDBQuery, 
							peptideDTO_MappedById, 
							searchDTO, 
							searchId, 
							searcherCutoffValuesSearchLevel );
			
			//  Link Type includes 'dimer' which has be combined with 'unlinked'
			combineDimerListIntoUnlinkedList( peptideLengthMap_Map_KeyedOnLinkType );
			
			//  Add to output Map  <{Link Type},Map<<Search id>,Map<{peptideLength},{PSM Count}>>
			for ( Map.Entry<String,Map<Integer,MutableInt>> psmCount_MappedOnPeptideLength_Map_Entry : peptideLengthMap_Map_KeyedOnLinkType.entrySet() ) {
				String linkType = psmCount_MappedOnPeptideLength_Map_Entry.getKey();
				Map<Integer,Map<Integer,MutableInt>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId = 
						allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
				if ( allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId == null ) {
					allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId = new HashMap<>();
					allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType.put( linkType, allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId );
				}
				allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId.put( searchId, psmCount_MappedOnPeptideLength_Map_Entry.getValue() );
			}
		}
		
		return allSearchesCombined_PSMCount_Map_Keyed_PeptideLength_Map_KeyedOnSearchId_KeyedOnLinkType;
	}


	/**
	 * Combine Dimer Counts Into Unlinked Counts
	 * @param chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue
	 */
	private void combineDimerListIntoUnlinkedList( Map<String, Map<Integer,MutableInt>> peptideLengthList_Map_KeyedOnLinkType ) {
		
		Map<Integer,MutableInt> dimerValues = peptideLengthList_Map_KeyedOnLinkType.get( XLinkUtils.DIMER_TYPE_STRING );
		if ( dimerValues == null ) {
			//  No Dimer values so skip
			return;  //  EARLY EXIT
		}
		
		Map<Integer,MutableInt> unlinkedValues = peptideLengthList_Map_KeyedOnLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
		if ( unlinkedValues == null ) {
			//  No Unlinked values so simply copy dimer to unlinked and remove dimer
			peptideLengthList_Map_KeyedOnLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, dimerValues );
			peptideLengthList_Map_KeyedOnLinkType.remove( XLinkUtils.DIMER_TYPE_STRING );
			return;  //  EARLY EXIT
		}
		
		//  Add dimer PSM counts to unlinked entries
		for ( Map.Entry<Integer,MutableInt> unlinkedValuesEntry : unlinkedValues.entrySet() ) {
			MutableInt dimerValuePSMCount = dimerValues.get( unlinkedValuesEntry.getKey() );
			if ( dimerValuePSMCount != null ) {
				unlinkedValuesEntry.getValue().add( dimerValuePSMCount.intValue() );
			}
		}
		//  Add dimer entries to unlinked where no previous entry in unlinked
		for ( Map.Entry<Integer,MutableInt> dimerValuesEntry : dimerValues.entrySet() ) {
			if ( ! unlinkedValues.containsKey( dimerValuesEntry.getKey() ) ) {
				unlinkedValues.put( dimerValuesEntry.getKey(), dimerValuesEntry.getValue() );
			}
		}

		peptideLengthList_Map_KeyedOnLinkType.remove( XLinkUtils.DIMER_TYPE_STRING );
	}
	

	/**
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param peptideDTO_MappedById
	 * @param searchDTO
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	public Map<String, Map<Integer,MutableInt>> getPeptideLengths_SingleSearch(
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery,
			Map<Integer, PeptideDTO> peptideDTO_MappedById, 
			SearchDTO searchDTO, 
			Integer searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception, ProxlWebappDataException {
		
		Map<String, Map<Integer,MutableInt>> psmCount_Map_KeyedOnpeptideLength_Map_KeyedOnLinkType = new HashMap<>();
		
		///////////////////////////////////////////////
		//  Get peptides for this search from the DATABASE
		List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =
				PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
						searchDTO, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, 
						PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );

		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
			int reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
			String linkType = null;
			
			//  srchRepPeptPeptideDTOList: associated SrchRepPeptPeptideDTO for the link, one per associated peptide, populated per link type
			
			//  copied from SearchPeptideCrosslink, this way not load PeptideDTO in SearchPeptideCrosslink
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId( searchId );
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( reportedPeptideId );
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
					Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
					.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
			List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();

			if ( webReportedPeptide.getSearchPeptideCrosslink() != null ) {
				//  Process a crosslink
				linkType = XLinkUtils.CROSS_TYPE_STRING;
				
			} else if ( webReportedPeptide.getSearchPeptideLooplink() != null ) {
				//  Process a looplink
				linkType = XLinkUtils.LOOP_TYPE_STRING;
				
			} else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
				//  Process a unlinked
				linkType = XLinkUtils.UNLINKED_TYPE_STRING;
				
			} else if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
				//  Process a dimer
				linkType = XLinkUtils.UNLINKED_TYPE_STRING;  //  Lump in with unlinked reported peptides

			} else {
				String msg = 
						"Link type unkown"
						+ " for reportedPeptideId: " + reportedPeptideId
						+ ", searchId: " + searchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			// get object from map for link type
			Map<Integer,MutableInt> psmCount_Map_KeyedOnpeptideLengthForLinkType = psmCount_Map_KeyedOnpeptideLength_Map_KeyedOnLinkType.get( linkType );
			if ( psmCount_Map_KeyedOnpeptideLengthForLinkType == null ) {
				psmCount_Map_KeyedOnpeptideLengthForLinkType = new HashMap<>();
				psmCount_Map_KeyedOnpeptideLength_Map_KeyedOnLinkType.put( linkType, psmCount_Map_KeyedOnpeptideLengthForLinkType );
			}
			
			int peptideLength = 0; //  peptide length is sum of peptides for reproted peptide

			//  process srchRepPeptPeptideDTOList (Each peptide mapped to the reported peptide)
			for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
				// get PeptideDTO, caching locally in peptideDTO_MappedById
				PeptideDTO peptide = null;
				// get PeptideDTO, caching locally in peptideDTO_MappedById
				if ( peptideDTO_MappedById != null ) {
					peptide = peptideDTO_MappedById.get( srchRepPeptPeptideDTO.getPeptideId() );
				}
				if ( peptide == null ) {
					peptide = PeptideDAO.getInstance().getPeptideDTOFromDatabase( srchRepPeptPeptideDTO.getPeptideId() );
					//  To directly retrieve from DB:  PeptideDAO.getInstance().getPeptideDTOFromDatabaseActual( id )
					if ( peptide == null ) {
						String msg = 
								"PeptideDTO not found in DB for id: " + srchRepPeptPeptideDTO.getPeptideId()
								+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
								+ ", for reportedPeptideId: " + reportedPeptideId
								+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					peptideDTO_MappedById.put( srchRepPeptPeptideDTO.getPeptideId(), peptide );
				}
				//  peptide length is sum of peptides for reproted peptide
				peptideLength += peptide.getSequence().length();
			}
			
			int numPSMs = webReportedPeptide.getNumPsms();
			
			MutableInt psmCount = psmCount_Map_KeyedOnpeptideLengthForLinkType.get( peptideLength );
			if ( psmCount == null ) {
				psmCount_Map_KeyedOnpeptideLengthForLinkType.put( peptideLength, new MutableInt( numPSMs ) );
			} else {
				psmCount.add( numPSMs );
			}
		}
		
		return psmCount_Map_KeyedOnpeptideLength_Map_KeyedOnLinkType;
	}

}
