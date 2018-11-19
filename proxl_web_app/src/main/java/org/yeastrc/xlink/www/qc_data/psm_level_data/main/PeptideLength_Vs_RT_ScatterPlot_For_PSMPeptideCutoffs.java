package org.yeastrc.xlink.www.qc_data.psm_level_data.main;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
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
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Get Peptide Length Vs Retention Time Scatter Plot where associated Reported Peptides meet criteria, including Reported Peptide and PSM cutoffs
 * 
 * Bin Retention Time to the minute
 *
 * Only 1 link type allowed.  Exception if > 1 link type requested
 */
public class PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs {

	private static final Logger log = Logger.getLogger(PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs.class);

	public enum ForDownload { YES, NO }

	
	/**
	 * private constructor
	 */
	private PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs(){}
	public static PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs getInstance( ) throws Exception {
		PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs instance = new PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs();
		return instance;
	}


	/**
	 * Response from call to getPeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs(...)
	 *
	 */
	public static class PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_Method_Response {
		
		PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults;
		
		Map<Integer,List<BigDecimal>> retentionTime_KeyedByPeptideLength;
		String linkTypeDisplay;

		public PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults getPeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults() {
			return peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults;
		}

		public void setPeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults(
				PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults) {
			this.peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults = peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults;
		}

		public Map<Integer, List<BigDecimal>> getRetentionTime_KeyedByPeptideLength() {
			return retentionTime_KeyedByPeptideLength;
		}

		public void setRetentionTime_KeyedByPeptideLength(Map<Integer, List<BigDecimal>> retentionTime_KeyedByPeptideLength) {
			this.retentionTime_KeyedByPeptideLength = retentionTime_KeyedByPeptideLength;
		}

		public String getLinkTypeDisplay() {
			return linkTypeDisplay;
		}

		public void setLinkTypeDisplay(String linkTypeDisplay) {
			this.linkTypeDisplay = linkTypeDisplay;
		}
	}
	
	/**
	 * @param forDownload
	 * @param filterCriteriaJSON
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_Method_Response getPeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs( 			
			ForDownload forDownload,
			String filterCriteriaJSON, 
			SearchDTO search ) throws Exception {
		
		Collection<Integer> searchIds = new HashSet<>();
		
		searchIds.add( search.getSearchId() );
		
//		List<SearchDTO> searches = new ArrayList<>( 1 );
//		searches.add( search );

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

		String[] linkTypesFromURL = mergedPeptideQueryJSONRoot.getLinkTypes();
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( linkTypesFromURL == null || linkTypesFromURL.length != 1 ) {
			String msg = "Exactly one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} 
		
		String linkTypeFromURL = linkTypesFromURL[ 0 ];
		
		
		String linkTypeRequestUpdated = null;
		String linkTypeDisplay = null;
		int requestedLinkTypeInt = -1;
		
		if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromURL ) || XLinkUtils.CROSS_TYPE_STRING.equals( linkTypeFromURL ) ) {
			linkTypeRequestUpdated = PeptideViewLinkTypesConstants.CROSSLINK_PSM;
			linkTypeDisplay = XLinkUtils.CROSS_TYPE_STRING;
			requestedLinkTypeInt = XLinkUtils.TYPE_CROSSLINK;
		} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromURL ) || XLinkUtils.LOOP_TYPE_STRING.equals( linkTypeFromURL ) ) {
			linkTypeRequestUpdated = PeptideViewLinkTypesConstants.LOOPLINK_PSM;
			linkTypeDisplay = XLinkUtils.LOOP_TYPE_STRING;
			requestedLinkTypeInt = XLinkUtils.TYPE_LOOPLINK;
		} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromURL ) || XLinkUtils.UNLINKED_TYPE_STRING.equals( linkTypeFromURL ) ) {
			linkTypeRequestUpdated = PeptideViewLinkTypesConstants.UNLINKED_PSM;
			linkTypeDisplay = XLinkUtils.UNLINKED_TYPE_STRING;
			requestedLinkTypeInt = XLinkUtils.TYPE_UNLINKED;
		} else {
			String msg = "linkType is invalid, linkTypeFromURL: " + linkTypeFromURL;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		linkTypesFromURL[ 0 ] = linkTypeRequestUpdated;
		
		mergedPeptideQueryJSONRoot.setLinkTypes( linkTypesFromURL );

		
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

		//  Get cutoffs for this project search id
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
				searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getProjectSearchId() );
		if ( searcherCutoffValuesSearchLevel == null ) {
			String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + search.getProjectSearchId();
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}


		Map<Integer,List<BigDecimal>> retentionTime_KeyedByPeptideLength = 
				getRetentionTime_KeyedOnPeptideLength( requestedLinkTypeInt, search, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery );
			
		PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_Method_Response methodResponse = new PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_Method_Response();
		
		methodResponse.linkTypeDisplay = linkTypeDisplay;
		methodResponse.retentionTime_KeyedByPeptideLength = retentionTime_KeyedByPeptideLength;

		if ( forDownload == ForDownload.YES ) {
			return methodResponse;  //  EARY RETURN
		}

		PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults = 
				getPeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults( retentionTime_KeyedByPeptideLength );
				
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setLinkType( linkTypeDisplay );

		methodResponse.peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults = peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults;
		
		return methodResponse;
	}
	
	/**
	 * @param retentionTime_KeyedByPeptideLength
	 * @return
	 */
	private PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults getPeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults(
			Map<Integer,List<BigDecimal>> retentionTime_KeyedByPeptideLength
			) {
		
		boolean peptideLengthMinMaxSet = false;
		boolean retentionTimeBinMinMaxSet = false;
		
		int numScans = 0;
		int peptideLengthMin = 0;
		int peptideLengthMax = 0;
		int retentionTimeBinMin = 0;
		int retentionTimeBinMax = 0;
		
		//  bin entries on retention time and sum a count on retention time / peptide length
		//  Map<{Peptide Length},Map<{Retention Time},{Count}>>
		Map<Integer,Map<Integer,MutableInt>> count_KeyedOn_RetentionTime_KeyedOn_PeptideLength = new HashMap<Integer, Map<Integer,MutableInt>>();

		for ( Map.Entry<Integer,List<BigDecimal>> entryPerPeptideLength : retentionTime_KeyedByPeptideLength.entrySet() ) {
			Integer peptideLength = entryPerPeptideLength.getKey();
			Map<Integer,MutableInt> count__KeyedOn_RetentionTime = new HashMap<>();
			count_KeyedOn_RetentionTime_KeyedOn_PeptideLength.put( peptideLength, count__KeyedOn_RetentionTime );
			if ( ! peptideLengthMinMaxSet ) {
				peptideLengthMinMaxSet = true;
				peptideLengthMin = peptideLength;
				peptideLengthMax = peptideLength;
			} else {
				if ( peptideLength < peptideLengthMin ) {
					peptideLengthMin = peptideLength;
				}
				if ( peptideLength > peptideLengthMax ) {
					peptideLengthMax = peptideLength;
				}
			}
			for ( BigDecimal retentionTime : entryPerPeptideLength.getValue() ) {
				numScans++;
				Integer retentionTimeInMinutesBin = (int) ( retentionTime.intValue() / 60 ); // retentionTime is in seconds
				if ( ! retentionTimeBinMinMaxSet ) {
					retentionTimeBinMinMaxSet = true;
					retentionTimeBinMin = retentionTimeInMinutesBin;
					retentionTimeBinMax = retentionTimeInMinutesBin;
				} else {
					if ( retentionTimeInMinutesBin < retentionTimeBinMin ) {
						retentionTimeBinMin = retentionTimeInMinutesBin;
					}
					if ( retentionTimeInMinutesBin > retentionTimeBinMax ) {
						retentionTimeBinMax = retentionTimeInMinutesBin;
					}
				}
				MutableInt count = count__KeyedOn_RetentionTime.get( retentionTimeInMinutesBin );
				if ( count == null ) {
					count__KeyedOn_RetentionTime.put( retentionTimeInMinutesBin, new MutableInt( 1 ) ); // new entry
				} else {
					count.increment();
				}
			}
		}
		
		int retentionTimePossibleMax = retentionTimeBinMin + 1; // possible max since Bin is 'floor' of decimal number
		
		//  Flip map to key on peptide length then retention time
		
		//  Map<{Retention Time},Map<{Peptide Length},{Count}>>
		Map<Integer,Map<Integer,MutableInt>> count_KeyedOn_PeptideLength_KeyedOn_RetentionTime = new HashMap<>();

		for ( Map.Entry<Integer,Map<Integer,MutableInt>> entryKeyOnPeptideLength : count_KeyedOn_RetentionTime_KeyedOn_PeptideLength.entrySet() ) {
			Integer peptideLength = entryKeyOnPeptideLength.getKey();
			for  ( Map.Entry<Integer,MutableInt> entryKeyOnRetentionTime : entryKeyOnPeptideLength.getValue().entrySet() ) {
				Integer retentionTime = entryKeyOnRetentionTime.getKey();
				Map<Integer,MutableInt> count_KeyedOn_PeptideLength = count_KeyedOn_PeptideLength_KeyedOn_RetentionTime.get( retentionTime );
				if ( count_KeyedOn_PeptideLength == null ) {
					count_KeyedOn_PeptideLength = new HashMap<>();
					count_KeyedOn_PeptideLength_KeyedOn_RetentionTime.put( retentionTime, count_KeyedOn_PeptideLength );
				}
				MutableInt count = count_KeyedOn_PeptideLength.get( peptideLength );
				if ( count == null ) {
					count_KeyedOn_PeptideLength.put( peptideLength, entryKeyOnRetentionTime.getValue() );
				} else {
					count.add( entryKeyOnRetentionTime.getValue().intValue() ); // Unlikely to get here
					throw new IllegalStateException( "count != null" );
				}
			}
		}
 
		//  Create output objects
		
		// Used for getting Percentiles of all Count Values:  Get a DescriptiveStatistics instance - Apache Commons
		DescriptiveStatistics statsForCountValues = new DescriptiveStatistics();
		
		List<PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket> retentionTimeBuckets = new ArrayList<>();
		
		for ( Map.Entry<Integer,Map<Integer,MutableInt>> entryOnRetentionTime : count_KeyedOn_PeptideLength_KeyedOn_RetentionTime.entrySet() ) {
			Map<Integer,MutableInt> count_KeyedOn_PeptideLength = entryOnRetentionTime.getValue();
			int retentionTimeStart = entryOnRetentionTime.getKey();
			int retentionTimeEnd = retentionTimeStart + 1;
			
			PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket retentionTimeBucket = new PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket();
			retentionTimeBuckets.add( retentionTimeBucket );
			
			retentionTimeBucket.setRetentionTimeStart( retentionTimeStart );
			retentionTimeBucket.setRetentionTimeEnd( retentionTimeEnd );
			List<PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> chartBuckets = new ArrayList<>( count_KeyedOn_PeptideLength.size() );
			retentionTimeBucket.setChartBuckets( chartBuckets );
			
			for ( Map.Entry<Integer,MutableInt> entryOnPeptideLength : count_KeyedOn_PeptideLength.entrySet() ) {
				int peptideLength = entryOnPeptideLength.getKey();
				
				PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket chartBucket = new PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket();
				chartBuckets.add( chartBucket );
				
				chartBucket.setRetentionTimeStart( retentionTimeStart );
				chartBucket.setRetentionTimeEnd( retentionTimeEnd );
				chartBucket.setPeptideLength( peptideLength );
				chartBucket.setCount( entryOnPeptideLength.getValue().intValue() );
				
				statsForCountValues.addValue( entryOnPeptideLength.getValue().intValue() );
			}
		}
		
		// Compute some statistics on counts
		int countValuePercentile25 = (int) statsForCountValues.getPercentile( 25 );
		int countValuePercentile50 = (int) statsForCountValues.getPercentile( 50 );
		int countValuePercentile75 = (int) statsForCountValues.getPercentile( 75 );

		//  Create output object
		
		PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults = new PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults();
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setNumScans( numScans );
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setPeptideLengthMax( peptideLengthMax );
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setPeptideLengthMin( peptideLengthMin );
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setRetentionTimeBinMax( retentionTimeBinMax );
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setRetentionTimeBinMin( retentionTimeBinMin );
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setRetentionTimePossibleMax( retentionTimePossibleMax );
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setRetentionTimeBuckets( retentionTimeBuckets );
		
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setCountValuePercentile25( countValuePercentile25 );
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setCountValuePercentile50( countValuePercentile50 );
		peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults.setCountValuePercentile75( countValuePercentile75 );
		
		return peptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults;
	}
	
	/**
	 * @param searchDTO
	 * @param searcherCutoffValuesSearchLevel
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @return
	 * @throws Exception
	 */
	private Map<Integer,List<BigDecimal>> getRetentionTime_KeyedOnPeptideLength(
			int requestedLinkTypeInt,
			SearchDTO searchDTO,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			String[] linkTypesForDBQuery,
			String[] modsForDBQuery
			) throws Exception {
		
		int searchId = searchDTO.getSearchId();
		
		Map<Integer,List<BigDecimal>> retentionTime_KeyedByPeptideLength = new HashMap<>();

		//  Cache peptideDTO ById locally
		Map<Integer,PeptideDTO> peptideDTO_MappedById = new HashMap<>();

		///////////////////////////////////////////////
		//  Get peptides for this search from the DATABASE
		List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =
				PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
						searchDTO, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, 
						PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );
		
		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
			int reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
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
				if ( requestedLinkTypeInt != XLinkUtils.TYPE_CROSSLINK ) {
					String msg = "Link Type of result reported peptide is not same as requested link type."
							+ "  requested link type: " + linkTypesForDBQuery
							+ ", link type of result is " + XLinkUtils.CROSS_TYPE_STRING;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}

			} else if ( webReportedPeptide.getSearchPeptideLooplink() != null ) {
				//  Process a looplink
				if ( requestedLinkTypeInt != XLinkUtils.TYPE_LOOPLINK ) {
					String msg = "Link Type of result reported peptide is not same as requested link type."
							+ "  requested link type: " + linkTypesForDBQuery
							+ ", link type of result is " + XLinkUtils.LOOP_TYPE_STRING;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				
			} else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
				//  Process a unlinked
				if ( requestedLinkTypeInt != XLinkUtils.TYPE_UNLINKED ) {
					String msg = "Link Type of result reported peptide is not same as requested link type."
							+ "  requested link type: " + linkTypesForDBQuery
							+ ", link type of result is " + XLinkUtils.UNLINKED_TYPE_STRING;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				
			} else if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
				//  Process a dimer
				if ( requestedLinkTypeInt != XLinkUtils.TYPE_UNLINKED ) {
					String msg = "Link Type of result reported peptide is not same as requested link type."
							+ "  requested link type: " + linkTypesForDBQuery
							+ ", link type of result is " + XLinkUtils.DIMER_TYPE_STRING
							+ " which is treated like " + XLinkUtils.UNLINKED_TYPE_STRING;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}

			} else {
				String msg = 
						"Link type unkown"
						+ " for reportedPeptideId: " + reportedPeptideId
						+ ", searchId: " + searchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
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
								+ ", searchId: " + searchDTO.getSearchId();
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					peptideDTO_MappedById.put( srchRepPeptPeptideDTO.getPeptideId(), peptide );
				}
				//  peptide length is sum of peptides for reproted peptide
				peptideLength += peptide.getSequence().length();
			}
			
			Integer peptideLengthObj = peptideLength;
			


			// process PSMs for this Reported Peptide

			List<PsmWebDisplayWebServiceResult> psmWebDisplayList = 
					PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( searchId, reportedPeptideId, searcherCutoffValuesSearchLevel );

			List<BigDecimal> retentionTimeListForPeptideLength = retentionTime_KeyedByPeptideLength.get( peptideLengthObj );
			if ( retentionTimeListForPeptideLength == null ) {
				retentionTimeListForPeptideLength = new ArrayList<>( psmWebDisplayList.size() * 2 );
				retentionTime_KeyedByPeptideLength.put( peptideLengthObj, retentionTimeListForPeptideLength );
			}
			
			//  Add for these PSMs
			for ( PsmWebDisplayWebServiceResult psmWebDisplayWebServiceResult : psmWebDisplayList ) {
				retentionTimeListForPeptideLength.add( psmWebDisplayWebServiceResult.getRetentionTime() );
			}
			
		}
		
		return retentionTime_KeyedByPeptideLength;
		
	}
}
