package org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.proteomics.peptide.peptide.PeptideUtils;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.IsotopeLabelDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.dto.StaticModDTO;
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
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.main.PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager.PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager_Result;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.objects.PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.objects.PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results.PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.objects.PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results.PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils.GetBoxPlotValuesResult;
import org.yeastrc.xlink.www.searcher.IsotopeLabelSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptDynamicModSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import org.yeastrc.xlink.www.web_utils.PSMMassCalculator;
import org.yeastrc.xlink.www.web_utils.PSMMassCalculatorParams;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Compute PPM Error Chart for > 1 search
 *
 */
public class PPM_Error_Chart_For_PSMPeptideCutoffs_Merged {

	private static final Logger log = LoggerFactory.getLogger( PPM_Error_Chart_For_PSMPeptideCutoffs_Merged.class);

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	static final int VERSION_FOR_CACHING = 1;
	
	public enum ForDownload { YES, NO }
	
	/**
	 * private constructor
	 */
	private PPM_Error_Chart_For_PSMPeptideCutoffs_Merged(){}
	public static PPM_Error_Chart_For_PSMPeptideCutoffs_Merged getInstance( ) throws Exception {
		PPM_Error_Chart_For_PSMPeptideCutoffs_Merged instance = new PPM_Error_Chart_For_PSMPeptideCutoffs_Merged();
		return instance;
	}

	/**
	 * Response from call to getPreMZ_Chart_For_PSMPeptideCutoffs_Merged(...)
	 *
	 */
	public static class PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response {

		private byte[] resultsAsBytes; //  ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results as JSON
		private PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results;
		/**
		 * Lists of PPM Error mapped by search id then link type
		 * Map <{Link Type},,Map<<Search id>,List<{PPM Error}>>>
		 */
		private Map<String,Map<Integer,List<Double>>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType;
		
		public PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results getPpm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results() {
			return ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results;
		}
		public void setPpm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results(
				PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results) {
			this.ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results = ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results;
		}
		/**
		 * Lists of PPM Error mapped by search id then link type
		 * Map <{Link Type},,Map<<Search id>,List<{PPM Error}>>>
		 * @return
		 */
		public Map<String, Map<Integer, List<Double>>> getAllSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType() {
			return allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType;
		}
		public void setAllSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType(
				Map<String, Map<Integer, List<Double>>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType) {
			this.allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType = allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType;
		}
		public byte[] getResultsAsBytes() {
			return resultsAsBytes;
		}
		public void setResultsAsBytes(byte[] resultsAsBytes) {
			this.resultsAsBytes = resultsAsBytes;
		}

	}
	
	/**
	 * @param filterCriteriaJSON
	 * @param searches
	 * @return
	 * @throws Exception
	 */
	public PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response getPPM_Error_Chart_For_PSMPeptideCutoffs_Merged( 
			ForDownload forDownload,
			String filterCriteriaJSON, 
			String requestQueryString,
			List<SearchDTO> searches ) throws Exception {


		String cacheKey = null;
		
		List<Integer> searchIds = new ArrayList<Integer>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
		}

		if ( forDownload != ForDownload.YES ) {
			
			//  Only if not download, get from Cache on disk
			
			cacheKey = requestQueryString + filterCriteriaJSON;
			
			PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager_Result ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager_Result =
					PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager.getSingletonInstance()
					.retrieveDataFromCache( searchIds, cacheKey );
			
			if ( ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager_Result != null ) {
				byte[] chartJSONAsBytes = ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager_Result.getChartJSONAsBytes();
				if ( chartJSONAsBytes != null ) {
					
					//  Data in Cache on disk so return it
					
					PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response response = new PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response();
					response.resultsAsBytes = chartJSONAsBytes;
					
					return response;  // EARLY EXIT
				}
			}
		}
		
		//  Reported Peptide Ids Skipped For Error Calculating MZ
		List<Integer> reportedPeptideIdsSkippedForErrorCalculatingMZ = new ArrayList<>( 100 );
		
		//  Internal use for tracking data used to compute PPM Error for entries with highest PPM Error
//		List<PPM_Error_ComputeEntry> ppm_Error_ComputeEntryList = new ArrayList<>( 10 );
		
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
			mergedPeptideQueryJSONRoot.setLinkTypes( linkTypesFromURLUpdated );
		}
		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = mergedPeptideQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		

		//  Get Lists of PPM Error mapped by search id then link type
		/**
		 * Map <{Link Type},,Map<<Search id>,List<{PPM Error}>>>
		 */
		Map<String,Map<Integer,List<Double>>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType = new HashMap<>();

		for ( SearchDTO search : searches ) {
			Integer searchId = search.getSearchId();
			Integer projectSearchId = search.getProjectSearchId();
			
			//  Get cutoffs for this project search id
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
			if ( searcherCutoffValuesSearchLevel == null ) {
				String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			Map<String, List<Double>> ppmErrorListForLinkType_ByLinkType = 
					create_PPM_ErrorListForLinkType_ByLinkTypeMap( 
							search, mergedPeptideQueryJSONRoot, searcherCutoffValuesSearchLevel, reportedPeptideIdsSkippedForErrorCalculatingMZ );

			//  Combine the Dimer into the Unlinked

			List<Double> ppmErrorListForDimer = ppmErrorListForLinkType_ByLinkType.get( XLinkUtils.DIMER_TYPE_STRING );
			if ( ppmErrorListForDimer != null ) {
				if ( ! ppmErrorListForDimer.isEmpty() ) {
					List<Double> ppmErrorListForUnlinked = ppmErrorListForLinkType_ByLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
					ppmErrorListForUnlinked.addAll( ppmErrorListForDimer );
				}
				ppmErrorListForLinkType_ByLinkType.remove( XLinkUtils.DIMER_TYPE_STRING );
			}

			//  Add to Map <{Link Type},,Map<<Search id>,List<{PPM Error}>>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType
			for ( Map.Entry<String,List<Double>> ppmErrorList_Map_Entry : ppmErrorListForLinkType_ByLinkType.entrySet() ) {
				String linkType = ppmErrorList_Map_Entry.getKey();
				Map<Integer,List<Double>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId = 
						allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
				if ( allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId == null ) {
					allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId = new HashMap<>();
					allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType.put( linkType, allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId );
				}
				allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId.put( searchId, ppmErrorList_Map_Entry.getValue() );
			}
		}

		PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response methodResult = new PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response();
		methodResult.allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType = allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType;
		
		if ( forDownload == ForDownload.YES ) {
			return methodResult; //  EARLY RETURN
		}
		//  Create output Results
		
		PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results = 
				getPPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results( 
						allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType,
						linkTypesList, 
						searches );
		
		methodResult.ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results = ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results;

		if ( ! reportedPeptideIdsSkippedForErrorCalculatingMZ.isEmpty() ) {
			log.warn( "Number of Reported Peptides Skipped For Error Calculating MZ: " 
					+ reportedPeptideIdsSkippedForErrorCalculatingMZ.size()
					+ ", search ids: " + searchIds.toString()
					+ ", List of Reported Peptide Ids: " + reportedPeptideIdsSkippedForErrorCalculatingMZ );
		}

		if ( forDownload != ForDownload.YES && cacheKey != null ) {
			//  Not for download so create the JSON and cache it to disk
			methodResult.resultsAsBytes = getResultsByteArray( methodResult.ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Results );
			
			PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager.getSingletonInstance()
			.saveDataToCache( searchIds, methodResult.resultsAsBytes, cacheKey );
		}
		
		return methodResult;
	}

	/**
	 * @param resultsObject
	 * @param searchId
	 * @return
	 * @throws IOException
	 */
	private byte[] getResultsByteArray( PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results resultsObject ) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();
		//   serialize 
		try {
			jacksonJSON_Mapper.writeValue( baos, resultsObject );
		} catch ( JsonParseException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonParseException.  " ;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonMappingException.  " ;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to serialize 'resultsObject', IOException. " ;
			log.error( msg, e );
			throw e;
		}
		
		return baos.toByteArray();
	}


	/**
	 * @param allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType
	 * @param linkTypesList
	 * @param searchIdsListDeduppedSorted
	 * @return
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results getPPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results( 

			// Map <{Link Type},,Map<<Search id>,List<{PPM Error}>>>
			Map<String,Map<Integer,List<Double>>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType,
			
			List<String> linkTypesList,
			List<SearchDTO> searches ) throws ProxlWebappInternalErrorException {
		
		List<PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList = new ArrayList<>( linkTypesList.size() );

		for ( String linkType : linkTypesList ) {
			Map<Integer,List<Double>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId =
					allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
			
			if ( allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId == null ) {
				PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType resultForLinkType =  new PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType();
				resultForLinkType.setLinkType( linkType );
				resultForLinkType.setDataFound( false );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			} else {
				PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType resultForLinkType =
						getSingleChartData_ForLinkType( allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId, searches );
				resultForLinkType.setLinkType( linkType );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			}
		}
		
		PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results results = new PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results();
		results.setDataForChartPerLinkTypeList( dataForChartPerLinkTypeList );
		
		return results;
	}
	
	/**
	 * @param allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType getSingleChartData_ForLinkType( 
			Map<Integer,List<Double>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId,
			List<SearchDTO> searches ) throws ProxlWebappInternalErrorException {
		
		//   Create output object for creating a chart
		
		boolean dataFound = false;
		
		Map<Integer, PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId> dataForChartPerSearchIdMap_KeyProjectSearchId = new HashMap<>();
		
		for ( SearchDTO search : searches ) {
			List<Double> ppmErrorList = allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId.get( search.getSearchId() );
			if ( ppmErrorList == null ) {
				PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId resultForSearchId = new PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId();
				resultForSearchId.setSearchId( search.getSearchId() );
				resultForSearchId.setDataFound( false );
				dataForChartPerSearchIdMap_KeyProjectSearchId.put( search.getProjectSearchId(), resultForSearchId );
			} else {
				dataFound = true;
				PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId resultForSearchId = 
						getSingleChartData_ForSearchId( ppmErrorList );
				resultForSearchId.setSearchId( search.getSearchId() );
				resultForSearchId.setDataFound( true );
				dataForChartPerSearchIdMap_KeyProjectSearchId.put( search.getProjectSearchId(), resultForSearchId );
			}
		}
		
		PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType result = new PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType();
		result.setDataForChartPerSearchIdMap_KeyProjectSearchId( dataForChartPerSearchIdMap_KeyProjectSearchId );
		result.setDataFound( dataFound );
		
		return result;
	}

	

	/**
	 * @param allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId getSingleChartData_ForSearchId( List<Double> ppmErrorList ) throws ProxlWebappInternalErrorException {
		
		//   Create output object for creating a chart

		GetBoxPlotValuesResult getBoxPlotValuesResult =
				BoxPlotUtils.getInstance().getBoxPlotValues( ppmErrorList );
		
		PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId result = new PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId();
		
		result.setChartIntervalMax( getBoxPlotValuesResult.getChartIntervalMax() );
		result.setChartIntervalMin( getBoxPlotValuesResult.getChartIntervalMin() );
		result.setFirstQuartile( getBoxPlotValuesResult.getFirstQuartile() );
		result.setThirdQuartile( getBoxPlotValuesResult.getThirdQuartile() );
		result.setMedian( getBoxPlotValuesResult.getMedian() );
						
		result.setDataFound(true);
		
		return result;
	}
			
	///////////////////////////////////
	
	
	/**
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	private Map<String, List<Double>> create_PPM_ErrorListForLinkType_ByLinkTypeMap(
			SearchDTO searchDTO, 
			MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			List<Integer> reportedPeptideIdsSkippedForErrorCalculatingMZ )
			throws Exception {
		
		int searchId = searchDTO.getSearchId();
		
		//   Map of List of PPM Error by Link Type
		
		Map<String,List<Double>> ppmErrorListForLinkType_ByLinkType = new HashMap<>();
		
		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( mergedPeptideQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();
	
		//  Cache peptideDTO ById locally
		Map<Integer,PeptideDTO> peptideDTO_MappedById = new HashMap<>();

		//  Get static mods for search id
		List<StaticModDTO> staticModDTOList = StaticModDAO.getInstance().getStaticModDTOForSearchId( searchId );

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

			// get from map for link type
			List<Double> ppmErrorListForLinkType = ppmErrorListForLinkType_ByLinkType.get( linkType );
			if ( ppmErrorListForLinkType == null ) {
				ppmErrorListForLinkType = new ArrayList<>();
				ppmErrorListForLinkType_ByLinkType.put( linkType, ppmErrorListForLinkType );
			}

			//  Collect the peptides and dynamic mods

			PeptideDTO peptide_1 =  null;
			PeptideDTO peptide_2 =  null;

			List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_1 = null;
			List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_2 = null;

			IsotopeLabelDTO isotopeLabel_1 = null;
			IsotopeLabelDTO isotopeLabel_2 = null;
			
			
			//  process srchRepPeptPeptideDTOList (Each peptide mapped to the reported peptide)
			for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
								
				//  Get Isotope Label
				IsotopeLabelDTO isotopeLabelDTO = IsotopeLabelSearcher.getInstance().getIsotopeLabelForSearchReportedPeptide_Peptide( srchRepPeptPeptideDTO );
				
				// get PeptideDTO, caching locally in peptideDTO_MappedById
				PeptideDTO peptide = peptideDTO_MappedById.get( srchRepPeptPeptideDTO.getPeptideId() );
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

				//					staticModDTOList

				List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_Original = 
						SrchRepPeptPeptDynamicModSearcher.getInstance()
						.getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );

				//  Remove duplicate dynamic mods for same position and both compared are monolink flag true
				//     logging error if mass is different

				List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList = new ArrayList<>( srchRepPeptPeptDynamicModDTOList_Original.size() );

				for ( SrchRepPeptPeptDynamicModDTO item_OriginalList : srchRepPeptPeptDynamicModDTOList_Original ) {

					//  Check if already in list 
					boolean alreadyInList = false;
					for ( SrchRepPeptPeptDynamicModDTO item_OutputList : srchRepPeptPeptDynamicModDTOList ) {

						if ( item_OriginalList.getPosition() == item_OutputList.getPosition()
								&& item_OriginalList.isMonolink() 
								&& item_OutputList.isMonolink() ) {

							alreadyInList = true;

							if ( item_OriginalList.getMass() != item_OutputList.getMass() ) {
								log.error( "Two SrchRepPeptPeptDynamicModDTO for same searchReportedPeptidepeptideId"
										+ " found with same position and both are monolink and have different massses."
										+ "  Entry 1: " + item_OriginalList
										+ ", Entry 2: " + item_OutputList
										+ ". searchId: " + searchId + ", reportedPeptideId: " + reportedPeptideId
										);
							}
							break;
						}
					}
					if ( ! alreadyInList ) {
						srchRepPeptPeptDynamicModDTOList.add( item_OriginalList );
					}
				}

				if ( peptide_1 == null ) {
					peptide_1 = peptide;
					isotopeLabel_1 = isotopeLabelDTO;
					srchRepPeptPeptDynamicModDTOList_1 = srchRepPeptPeptDynamicModDTOList;

				} else if ( peptide_2 == null ) {
					peptide_2 = peptide;
					isotopeLabel_2 = isotopeLabelDTO;
					srchRepPeptPeptDynamicModDTOList_2 = srchRepPeptPeptDynamicModDTOList;

				} else {
					String msg = 
							"peptide_1 and peptide_2 already have values"
									+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", for reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				
			}

			//  To confirm that peptide sequences do not contain invalid amino acid characters
			if( !PeptideUtils.isValidPeptideSequence( peptide_1.getSequence() ) || ( peptide_2 != null && !PeptideUtils.isValidPeptideSequence( peptide_2.getSequence() ) ) ) {

				// invalid peptide sequence(s), note it and skip this reported peptide
				reportedPeptideIdsSkippedForErrorCalculatingMZ.add( reportedPeptideId );
				continue;
			}


			// process PSMs for this Reported Peptide

			List<PsmWebDisplayWebServiceResult> psmWebDisplayList = 
					PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( searchId, reportedPeptideId, searcherCutoffValuesSearchLevel );

			for ( PsmWebDisplayWebServiceResult psmWebDisplayWebServiceResult : psmWebDisplayList ) {

				BigDecimal scanPreMZ = psmWebDisplayWebServiceResult.getPreMZ(); // from scan table
				double scanPreMZasDouble = scanPreMZ.doubleValue();

				PsmDTO psmDTO = psmWebDisplayWebServiceResult.getPsmDTO();
				Integer charge = psmDTO.getCharge();
				BigDecimal linkerMass = psmDTO.getLinkerMass();

				Double linkerMassAsDouble = null;

				if ( linkerMass != null ) {
					linkerMassAsDouble = linkerMass.doubleValue();
				}

				if ( charge != null && scanPreMZ != null ) {

					//  Compute PPM Error

					double ppmError = 0;
					try {
						
						PSMMassCalculatorParams params = new PSMMassCalculatorParams();
						params.setCharge( charge );
						params.setLinkerMass( linkerMassAsDouble );
						params.setPrecursorMZ( scanPreMZasDouble );
						
						params.setPeptide1( peptide_1 );
						params.setPeptide2( peptide_2 );
						
						params.setLabel1( isotopeLabel_1 );
						params.setLabel2( isotopeLabel_2 );
						
						params.setDynamicMods1( srchRepPeptPeptDynamicModDTOList_1 );
						params.setDynamicMods2( srchRepPeptPeptDynamicModDTOList_2 );
						
						params.setStaticMods( staticModDTOList );
						
						ppmError = PSMMassCalculator.calculatePPMEstimateForPSM( params );
						
						ppmErrorListForLinkType.add( ppmError );
						
					} catch ( Exception e ) {
						String msg = "PSMMassCalculator.calculatePPMEstimateForPSM(...) threw exception:"
								+ "\n linkType: " + linkType
								+ "\n scanPreMZasDouble: " + scanPreMZasDouble
								+ "\n search id: " + searchId
								+ "\n reported peptide id: " + reportedPeptideId
								+ "\n reported peptide: " + webReportedPeptide.getReportedPeptide().getSequence()
								+ "\n peptide_1: " + peptide_1 
								+ "\n peptide_2: " + peptide_2
								+ "\n label_1: " + isotopeLabel_1 
								+ "\n label_2: " + isotopeLabel_2
								+ "\n srchRepPeptPeptDynamicModDTOList_1: " + srchRepPeptPeptDynamicModDTOList_1
								+ "\n srchRepPeptPeptDynamicModDTOList_2: " + srchRepPeptPeptDynamicModDTOList_2
								+ "\n charge: " + charge
								+ "\n linkerMassAsDouble: " + linkerMassAsDouble
								+ "\n staticModDTOList: " + staticModDTOList;
						log.error( msg, e );
						throw e;
					}
				}
			}

		}

		return ppmErrorListForLinkType_ByLinkType;
	}
	
	
}
