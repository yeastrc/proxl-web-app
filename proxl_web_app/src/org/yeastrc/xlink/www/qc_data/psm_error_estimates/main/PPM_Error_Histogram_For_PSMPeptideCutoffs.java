package org.yeastrc.xlink.www.qc_data.psm_error_estimates.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
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
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.main.PPM_Error_Histogram_For_PSMPeptideCutoffs_CachedResultManager.PPM_Error_Histogram_For_PSMPeptideCutoffs_CachedResultManager_Result;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.objects.PPM_Error_Histogram_For_PSMPeptideCutoffs_Result;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.objects.PPM_Error_Histogram_For_PSMPeptideCutoffs_Result.PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.objects.PPM_Error_Histogram_For_PSMPeptideCutoffs_Result.PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType;
import org.yeastrc.xlink.www.searcher.IsotopeLabelSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptDynamicModSearcher;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult;
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
 * 
 * Compute PPM Error Histogram
 */
public class PPM_Error_Histogram_For_PSMPeptideCutoffs {

	private static final Logger log = Logger.getLogger(PPM_Error_Histogram_For_PSMPeptideCutoffs.class);

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	static final int VERSION_FOR_CACHING = 2;
	
	
	private static final int REMOVE_OUTLIERS_FIRST_QUARTER_PERCENTILE = 25;
	private static final int REMOVE_OUTLIERS_THIRD_QUARTER_PERCENTILE = 75;
	
	//  number of IQRs to add
	private static final double OUTLIER_FACTOR = 1.5;
	
	/**
	 * private constructor
	 */
	private PPM_Error_Histogram_For_PSMPeptideCutoffs(){}
	public static PPM_Error_Histogram_For_PSMPeptideCutoffs getInstance( ) throws Exception {
		PPM_Error_Histogram_For_PSMPeptideCutoffs instance = new PPM_Error_Histogram_For_PSMPeptideCutoffs();
		return instance;
	}
	
	/**
	 * @param requestQueryString
	 * @param filterCriteriaJSON
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public byte[] getPPM_Error_Histogram_For_PSMPeptideCutoffs( 
			String requestQueryString, // query string from request URL
			String filterCriteriaJSON, 
			SearchDTO search ) throws Exception {
		
		List<Integer> searchIdsList = new ArrayList<>( 1 );
		searchIdsList.add( search.getSearchId() );
		
		MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder =
				getMergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder( filterCriteriaJSON, searchIdsList );

		//  Only applicable if search cutoffs are defaults
		boolean searchOnlyHasDefaultCutoffs = get_searchOnlyHasDefaultCutoffs( search, mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder);

		if ( searchOnlyHasDefaultCutoffs ) {
			byte[] resultsAsBytes = 
					retrieveDataFromCacheAndMatchCutoffs( search, mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder, requestQueryString );
	
			if ( resultsAsBytes != null ) {
				//  Have Cached data so return it
				return resultsAsBytes;  //  EARLY RETURN
			}
		}

		Map<String, List<Double>> ppmErrorListForLinkType_ByLinkType = 
				createppmErrorListForLinkType_ByLinkTypeMap( mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder, search );
		
		//  Combine the Dimer into the Unlinked
		
		List<Double> ppmErrorListForDimer = ppmErrorListForLinkType_ByLinkType.get( XLinkUtils.DIMER_TYPE_STRING );
		if ( ppmErrorListForDimer != null ) {
			if ( ! ppmErrorListForDimer.isEmpty() ) {
				List<Double> ppmErrorListForUnlinked = ppmErrorListForLinkType_ByLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
				ppmErrorListForUnlinked.addAll( ppmErrorListForDimer );
			}
			ppmErrorListForLinkType_ByLinkType.remove( XLinkUtils.DIMER_TYPE_STRING );
		}

		//  Build a new map, removing outliers from each list
		ppmErrorListForLinkType_ByLinkType = removeOutliers( ppmErrorListForLinkType_ByLinkType );
		
		PPM_Error_Histogram_For_PSMPeptideCutoffs_Result resultsObject = 
				getPPM_Error_Histogram_For_PSMPeptideCutoffs_Result( ppmErrorListForLinkType_ByLinkType );
		
		byte[] result = getResultsByteArray( resultsObject, search.getSearchId() );
		
		if ( searchOnlyHasDefaultCutoffs ) {
			cacheResult( result, search, requestQueryString );
		}
				
		return result;
	}
	
	/**
	 * @param chartJSONAsBytes
	 * @param search
	 * @param requestQueryString
	 * @throws Exception
	 */
	private void cacheResult( 
			byte[] chartJSONAsBytes, 
			SearchDTO search, 
			String requestQueryString ) throws Exception {

		PPM_Error_Histogram_For_PSMPeptideCutoffs_CachedResultManager.getSingletonInstance()
		.saveDataToCache( search.getProjectSearchId(), chartJSONAsBytes, requestQueryString );
	}
	
	/**
	 * @param resultsObject
	 * @param searchId
	 * @return
	 * @throws IOException
	 */
	private byte[] getResultsByteArray( PPM_Error_Histogram_For_PSMPeptideCutoffs_Result resultsObject, int searchId ) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();
		//   serialize 
		try {
			jacksonJSON_Mapper.writeValue( baos, resultsObject );
		} catch ( JsonParseException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonParseException.  "
					+ ". searchId: " + searchId;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonMappingException.  "
					+ ". searchId: " + searchId;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to serialize 'resultsObject', IOException.  "
					+ ". searchId: " + searchId;
			log.error( msg, e );
			throw e;
		}
		
		return baos.toByteArray();
	}

	/**
	 * @param searches
	 * @param mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder
	 * @return
	 * @throws Exception
	 */
	private byte[] retrieveDataFromCacheAndMatchCutoffs( SearchDTO search,
			MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder,
			String requestQueryString )
			throws Exception {

		PPM_Error_Histogram_For_PSMPeptideCutoffs_CachedResultManager_Result cachedDataResult =
				PPM_Error_Histogram_For_PSMPeptideCutoffs_CachedResultManager.getSingletonInstance()
				.retrieveDataFromCache( search.getProjectSearchId(), requestQueryString );

		if ( cachedDataResult == null ) {
			//  No Cached results so return null
			return null;  //  EARLY RETURN
		}
		
		byte[] chartJSONAsBytes = cachedDataResult.getChartJSONAsBytes();
		return chartJSONAsBytes;
	}
	
	/**
	 * @param search
	 * @param mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder
	 * @return
	 * @throws Exception
	 */
	private boolean get_searchOnlyHasDefaultCutoffs( SearchDTO search,
			MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder)
			throws Exception {
		

		boolean searchOnlyHasDefaultCutoffs = false;

		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder.searcherCutoffValuesRootLevel;

		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getProjectSearchId() );
		DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult result =
				DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.getInstance()
				.defaultCutoffsExactlyMatchAnnTypeDataToSearchData( search.getSearchId(), searcherCutoffValuesSearchLevel );
		if ( result.isDefaultCutoffsExactlyMatchAnnTypeDataToSearchData() ) {
			searchOnlyHasDefaultCutoffs = true;
		}
		
		return searchOnlyHasDefaultCutoffs;
	}
	
	private static class MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder {
		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot;
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;
	}
	
	/**
	 * @param filterCriteriaJSON
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	private MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder 
		getMergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder( String filterCriteriaJSON, Collection<Integer> searchIds ) throws Exception {

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
		
		MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder holder = new MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder();
		holder.mergedPeptideQueryJSONRoot = mergedPeptideQueryJSONRoot;
		holder.searcherCutoffValuesRootLevel = searcherCutoffValuesRootLevel;
		
		return holder;
	}
	
	/**
	 * @param ppmErrorListForLinkType_ByLinkType
	 * @return
	 */
	private Map<String, List<Double>> removeOutliers( Map<String, List<Double>> ppmErrorListForLinkType_ByLinkType ) {
		
		//  output map
		Map<String, List<Double>> ppmErrorListForLinkType_ByLinkType_Result = new HashMap<>();
		
		// Process for each link type
		
		for ( Map.Entry<String, List<Double>> entry : ppmErrorListForLinkType_ByLinkType.entrySet() ) {
			String linkType = entry.getKey();
			List<Double> ppmErrorListBeforeRemoveOutliers = entry.getValue();

			// Get a DescriptiveStatistics instance - Apache Commons
			DescriptiveStatistics stats = new DescriptiveStatistics();
			
			// Add the PPM Error data
			for( Double ppmError : ppmErrorListBeforeRemoveOutliers ) {
				stats.addValue( ppmError );
			}

			// Compute some statistics
			double firstquarter = stats.getPercentile( REMOVE_OUTLIERS_FIRST_QUARTER_PERCENTILE );
			double thirdquarter = stats.getPercentile( REMOVE_OUTLIERS_THIRD_QUARTER_PERCENTILE );

			double interQuartileRegion = thirdquarter - firstquarter;
			double lowcutoff = firstquarter - ( OUTLIER_FACTOR * interQuartileRegion );
			double highcutoff = thirdquarter + ( OUTLIER_FACTOR * interQuartileRegion );
			
			//  Build a new list removing values < lowcutoff and > highcutoff 
			List<Double> ppmErrorList_After_RemoveOutliers = new ArrayList<>( ppmErrorListBeforeRemoveOutliers.size() );
			for( Double ppmError : ppmErrorListBeforeRemoveOutliers ) {
				if ( ppmError >= lowcutoff && ppmError <= highcutoff ) {
					ppmErrorList_After_RemoveOutliers.add( ppmError );
				}
			}
			//  Insert new list into new hash
			ppmErrorListForLinkType_ByLinkType_Result.put( linkType, ppmErrorList_After_RemoveOutliers );
		}
		
		return ppmErrorListForLinkType_ByLinkType_Result;
	}
	
	
	/**
	 * @param ppmErrorListForLinkType_ByLinkType
	 * @return
	 */
	private PPM_Error_Histogram_For_PSMPeptideCutoffs_Result getPPM_Error_Histogram_For_PSMPeptideCutoffs_Result(
			Map<String,List<Double>> ppmErrorListForLinkType_ByLinkType ) {
		
		Map<String,PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsByLinkTypeMap = new HashMap<>();

		for ( Map.Entry<String,List<Double>> ppmErrorListForLinkTypeEntry : ppmErrorListForLinkType_ByLinkType.entrySet() ) {
			String linkType = ppmErrorListForLinkTypeEntry.getKey();
			List<Double> ppmErrorList = ppmErrorListForLinkTypeEntry.getValue();
			PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType resultsForLinkType =
					getPPM_Error_HistogramData_ForLinkType( ppmErrorList );
			resultsForLinkType.setLinkType( linkType );
			resultsByLinkTypeMap.put( linkType, resultsForLinkType );
		}
		
		List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList = new ArrayList<>( 5 );
		
		//  copy map to array for output, in a specific order
		
		addToOutputListForLinkType( XLinkUtils.CROSS_TYPE_STRING, dataForChartPerLinkTypeList, resultsByLinkTypeMap );
		addToOutputListForLinkType( XLinkUtils.LOOP_TYPE_STRING, dataForChartPerLinkTypeList, resultsByLinkTypeMap );
		addToOutputListForLinkType( XLinkUtils.UNLINKED_TYPE_STRING, dataForChartPerLinkTypeList, resultsByLinkTypeMap );
		

		PPM_Error_Histogram_For_PSMPeptideCutoffs_Result result = new PPM_Error_Histogram_For_PSMPeptideCutoffs_Result();
		result.setDataForChartPerLinkTypeList( dataForChartPerLinkTypeList );
		
		return result;
	}
	
	/**
	 * @param linkType
	 * @param dataForChartPerLinkTypeList
	 * @param resultsByLinkTypeMap
	 */
	private void addToOutputListForLinkType( 
			String linkType, 
			List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList, 
			Map<String,PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsByLinkTypeMap ) {
		PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType item = resultsByLinkTypeMap.get( linkType );
		if ( item != null ) {
			dataForChartPerLinkTypeList.add( item );
		}
	}
	

	/**
	 * @param ppmErrorList
	 * @return
	 */
	private PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType getPPM_Error_HistogramData_ForLinkType( 
			List<Double> ppmErrorList ) {
		
		{
			if ( ppmErrorList == null || ppmErrorList.isEmpty() ) {
				PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType result = new PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
				return result;
			}
		}
		
		int numScans = ppmErrorList.size();
		boolean firstOverallpreMZEntry = true;
		//  Find max and min values
		double ppmErrorMin = Double.MAX_VALUE;
		double ppmErrorMax =  Double.MIN_VALUE;
		for ( double ppmErrorEntry : ppmErrorList ) {
			if ( firstOverallpreMZEntry  ) {
				firstOverallpreMZEntry = false;
				ppmErrorMin = ppmErrorEntry;
				ppmErrorMax = ppmErrorEntry;
			} else {
				if ( ppmErrorEntry < ppmErrorMin ) {
					ppmErrorMin = ppmErrorEntry;
				}
				if ( ppmErrorEntry > ppmErrorMax ) {
					ppmErrorMax = ppmErrorEntry;
				}
			}
		}

		//  Process data into bins
		int binCount = (int) ( Math.sqrt( ppmErrorList.size() ) );
		
		if ( ppmErrorMax > 0 && ppmErrorMin < 0 ) {
			//  Change Max and Min so the center of a bin is at zero
			{
				//  Initial 'extend' Min and Max by a bin  
				double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
				double binSize = ( ppmErrorMaxMinusMin ) / binCount;
				ppmErrorMax += binSize;
				ppmErrorMin -= binSize;
				
				//  Since the new bin size will be larger than the old bin size, 
				//  adding half a bin is possibly not enough to keep from  
				//  cutting into the existing data points when shifting the bins to center a bin on zero.
				
				//  WAS:
				//  Initial 'extend' Min and Max by half a bin  
//				double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
//				double binSize = ( ppmErrorMaxMinusMin ) / binCount;
//				double halfBinSize = binSize * 0.5;
//				ppmErrorMax += halfBinSize;
//				ppmErrorMin -= halfBinSize;
			}
//			
			
			
			double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
			double binSize = ( ppmErrorMaxMinusMin ) / binCount;
//			double halfBinSize = binSize * 0.5;
			// The bin that contains position zero
			int binIndexContainZero = (int) ( - ( ppmErrorMin / binSize ) );
			// The start of the bin that contains position zero			
			double binStartContainZero =  ppmErrorMin + ( binIndexContainZero * binSize );
			// Center of bin that contains zero
			double binStartContainZeroPlusHalfBin = binStartContainZero + ( binSize * 0.5 );
			//  
			double shift = binStartContainZeroPlusHalfBin;
			
			if ( binStartContainZeroPlusHalfBin > 0 ) {
				//  Center of bin is 'right' of zero, so shift left
				ppmErrorMin -= ( shift ); //  binStartContainZeroPlusHalfBin is positive here
				ppmErrorMax -= ( shift ); //  binStartContainZeroPlusHalfBin is positive here
			} else {
				//  Center of bin is 'left' of zero, so shift right
				ppmErrorMin += ( - shift ); //  binStartContainZeroPlusHalfBin is negative here
				ppmErrorMax += ( - shift ); //  binStartContainZeroPlusHalfBin is negative here
			}
		}
		
		//  Debugging code
//		{
//			//  Get center of bin that contains zero
//			double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
//			double binSize = ( ppmErrorMaxMinusMin ) / binCount;
//			// The bin that contains position zero
//			int binIndexContainZero = (int) ( - ( ppmErrorMin / binSize ) );
//			// The start of the bin that contains position zero			
//			double binStartContainZero =  ppmErrorMin + ( binIndexContainZero * binSize );
//			// Center of bin that contains zero
//			double binStartContainZeroPlusHalfBin = binStartContainZero + ( binSize * 0.5 );
//
//			int z = 0;
//		}
		
		double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
		
		
		//  Allocate bins
		int[] ppmErrorCounts = new int[ binCount ];
		//  Bin Size
		double binSize = ( ppmErrorMaxMinusMin ) / binCount;
		
		for ( double ppmErrorEntry : ppmErrorList ) {
			double preMZFraction = ( ppmErrorEntry - ppmErrorMin ) / ppmErrorMaxMinusMin;
			int bin = (int) ( (  preMZFraction ) * binCount );
			if ( bin < 0 ) {
				bin = 0;
			} else if ( bin >= binCount ) {
				bin = binCount - 1;
			} 
			ppmErrorCounts[ bin ]++;
		}
		
		//    Exclude first contiguous and last contiguous bins where count is zero
		//  Find first and last bins where count != 0
		int firstBinIndexCountNotZero = 0;
		int lastBinIndexCountNotZero = ppmErrorCounts.length - 1;
		while ( ppmErrorCounts[ firstBinIndexCountNotZero ] == 0 && firstBinIndexCountNotZero < lastBinIndexCountNotZero ) {
			firstBinIndexCountNotZero++;
		}
		while ( ppmErrorCounts[ lastBinIndexCountNotZero ] == 0 && firstBinIndexCountNotZero < lastBinIndexCountNotZero ) {
			lastBinIndexCountNotZero--;
		}
		
		List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets = new ArrayList<>();
		double binHalf = binSize / 2 ;
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		for ( int binIndex = firstBinIndexCountNotZero; binIndex <= lastBinIndexCountNotZero; binIndex++ ) {
			int preMZCount = ppmErrorCounts[ binIndex ];
			double binStart = ( ( binIndex * binSize ) ) + ppmErrorMin;
			double binEnd = ( ( binIndex + 1 ) * binSize ) + ppmErrorMin;
			double binMiddleDouble = binStart + binHalf;
			PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket chartBucket = new PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket();
			chartBucket.setBinEnd( binEnd );
			chartBucket.setBinStart( binStart );
			chartBuckets.add( chartBucket );
			chartBucket.setBinCenter( binMiddleDouble );
			chartBucket.setCount( preMZCount );
		}
		
		PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType result = new PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
		
		result.setChartBuckets( chartBuckets );
		result.setNumScans( numScans );
		result.setPpmErrorMax( ppmErrorMax );
		result.setPpmErrorMin( ppmErrorMin );
		
		return result;
	}
	
	
	
	/**
	 * @param filterCriteriaJSON
	 * @param searches
	 * @param searchIds
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	private Map<String, List<Double>> createppmErrorListForLinkType_ByLinkTypeMap(
			MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder, 
			SearchDTO searchDTO )
			throws JsonParseException, JsonMappingException, IOException, Exception, ProxlWebappDataException {

		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder.mergedPeptideQueryJSONRoot;
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder.searcherCutoffValuesRootLevel;
		
		//  Reported Peptide Ids Skipped For Error Calculating MZ
		List<Integer> reportedPeptideIdsSkippedForErrorCalculatingMZ = new ArrayList<>( 100 );
		
		//  Internal use for tracking data used to compute PPM Error for entries with highest PPM Error
//		List<PPM_Error_ComputeEntry> ppm_Error_ComputeEntryList = new ArrayList<>( 10 );
		
		
		//   Map of List of PPM Error by Link Type
		
		Map<String,List<Double>> ppmErrorListForLinkType_ByLinkType = new HashMap<>();

		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( mergedPeptideQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} else {
			for ( String linkTypeFromWeb : mergedPeptideQueryJSONRoot.getLinkTypes() ) {
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromWeb ) ) {
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.CROSS_TYPE_STRING, new ArrayList<>() );
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromWeb ) ) {
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.LOOP_TYPE_STRING, new ArrayList<>() );
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromWeb ) ) {
					//  Add lists for Unlinked and Dimer
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, new ArrayList<>() );
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.DIMER_TYPE_STRING, new ArrayList<>() );
				} else {
					String msg = "linkType is invalid, linkType: " + linkTypeFromWeb;
					log.error( msg );
					throw new Exception( msg );
				}
			}
		}
		
		//  Cache peptideDTO ById locally
		Map<Integer,PeptideDTO> peptideDTO_MappedById = new HashMap<>();
		
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

				//  validation for crosslink
				if ( srchRepPeptPeptideDTOList.size() != 2 ) {
					String msg = "For Crosslink: List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + searchId
							+ ", ReportedPeptideId: " + reportedPeptideId ;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
					if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  == null 
							|| srchRepPeptPeptideDTO.getPeptidePosition_1() == SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						String msg = 
								"For Crosslink: srchRepPeptPeptideDTO.getPeptidePosition_1() not populated "
										+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
										+ ", reportedPeptideId: " + reportedPeptideId
										+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
							&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						String msg = 
								"For Crosslink: srchRepPeptPeptideDTO.getPeptidePosition_2() is populated "
										+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
										+ ", reportedPeptideId: " + reportedPeptideId
										+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
				}

			} else if ( webReportedPeptide.getSearchPeptideLooplink() != null ) {
				//  Process a looplink
				linkType = XLinkUtils.LOOP_TYPE_STRING;

				//  validation for looplink
				if ( srchRepPeptPeptideDTOList.size() != 1 ) {
					String msg = "For Looplink: List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " + searchId
							+ ", ReportedPeptideId: " + reportedPeptideId ;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
					if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  == null 
							|| srchRepPeptPeptideDTO.getPeptidePosition_1() == SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						String msg = 
								"For Looplink: srchRepPeptPeptideDTO.getPeptidePosition_1() not populated "
										+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
										+ ", reportedPeptideId: " + reportedPeptideId
										+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					if ( srchRepPeptPeptideDTO.getPeptidePosition_2() == null
							|| srchRepPeptPeptideDTO.getPeptidePosition_2() == SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						String msg = 
								"For Looplink: srchRepPeptPeptideDTO.getPeptidePosition_2() not populated "
										+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
										+ ", reportedPeptideId: " + reportedPeptideId
										+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
				}

			} else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
				//  Process a unlinked
				linkType = XLinkUtils.UNLINKED_TYPE_STRING;

				//  validation for unlinked
				if ( srchRepPeptPeptideDTOList.size() != 1 ) {
					String msg = "For Unlinked: List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " + searchId
							+ ", ReportedPeptideId: " + reportedPeptideId ;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
					if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  != null 
							&& srchRepPeptPeptideDTO.getPeptidePosition_1() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						String msg = 
								"For Unlinked: srchRepPeptPeptideDTO.getPeptidePosition_1() is populated "
										+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
										+ ", reportedPeptideId: " + reportedPeptideId
										+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
							&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						String msg = 
								"For Unlinked: srchRepPeptPeptideDTO.getPeptidePosition_2() is populated "
										+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
										+ ", reportedPeptideId: " + reportedPeptideId
										+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
				}
			} else if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
				//  Process a dimer
				linkType = XLinkUtils.UNLINKED_TYPE_STRING;  //  Lump in with unlinked reported peptides

				//  validation for dimer
				if ( srchRepPeptPeptideDTOList.size() != 2 ) {
					String msg = "For Dimer: List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + searchId
							+ ", ReportedPeptideId: " + reportedPeptideId ;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
					if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  != null 
							&& srchRepPeptPeptideDTO.getPeptidePosition_1() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						String msg = 
								"For Dimer: srchRepPeptPeptideDTO.getPeptidePosition_1() is populated "
										+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
										+ ", reportedPeptideId: " + reportedPeptideId
										+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
							&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
						String msg = 
								"For Dimer: srchRepPeptPeptideDTO.getPeptidePosition_2() is populated "
										+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
										+ ", reportedPeptideId: " + reportedPeptideId
										+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
				}
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
				String msg = "In processing Reported Peptides, link type not found: " + linkType;
				log.error( msg );
				throw new Exception(msg);
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


				//  Specific debugging

				//					if ( searchId == 188 && reportedPeptideId == 1408748 ) {
				//						log.warn( "searchId == 188 && reportedPeptideId == 1408748:  srchRepPeptPeptDynamicModDTOList: " 
				//								+ srchRepPeptPeptDynamicModDTOList );
				//					}



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


			/*
			 * Testing: this works
			 * 
				System.out.println( webReportedPeptide.getReportedPeptide().getSequence() + ":" );
				System.out.println( "\t" + peptide_1.getSequence() + "\t" + ( ( isotopeLabel_1 == null ? "null" : isotopeLabel_1.getName() ) ) );

				if( peptide_2 != null )
					System.out.println( "\t" + peptide_2.getSequence() + "\t" + ( ( isotopeLabel_2 == null ? "null" : isotopeLabel_2.getName() ) ) );

			 */


			//  To confirm that peptide sequences do not contain invalid amino acid characters
			if( !PeptideUtils.isValidPeptideSequence( peptide_1.getSequence() ) || ( peptide_2 != null && !PeptideUtils.isValidPeptideSequence( peptide_2.getSequence() ) ) ) {

				// invalid peptide sequence(s), note it and skip this reported peptide
				reportedPeptideIdsSkippedForErrorCalculatingMZ.add( reportedPeptideId );
				continue;
			}





			// process PSMs for this Reported Peptide

			List<PsmWebDisplayWebServiceResult> psmWebDisplayList = 
					PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( searchId, reportedPeptideId, searcherCutoffValuesSearchLevel );

			//  Returns null if insufficient info to get the precursor mass (scan file id or scan number is null, ...)
			//				Map<Integer,Map<Integer,SingleScan_SubResponse>> scanFromSpectalStorage_Key_ScanNumber_Key_ScanFileId = 
			//						getPrecursorMassFromSpectralStorageService( psmWebDisplayList );


			for ( PsmWebDisplayWebServiceResult psmWebDisplayWebServiceResult : psmWebDisplayList ) {

				//					psmWebDisplayWebServiceResult.getRetentionTime();

				double scanPreMZasDouble = 0;
				boolean scanPreMZasDoubleSet = false;

				//  Use Scan Precursor M/Z from proxl.scan table instead of from Spectral Storage since is too slow as currently implemented in Proxl (need to batch more) 

				//  Comment out call to Spectral Storage

				//					if ( scanFromSpectalStorage_Key_ScanNumber_Key_ScanFileId == null ) {
				//						
				//						log.error( "scanFromSpectalStorage_Key_ScanNumber_Key_ScanFileId is null. " );
				//
				//					} else {
				//						
				//						Map<Integer,SingleScan_SubResponse> scanFromSpectalStorage_Key_ScanNumber =
				//								scanFromSpectalStorage_Key_ScanNumber_Key_ScanFileId.get( psmWebDisplayWebServiceResult.getScanFileId() );
				//						
				//						if ( scanFromSpectalStorage_Key_ScanNumber == null ) {
				//							log.error( "No entry in scanFromSpectalStorage_Key_ScanNumber_Key_ScanFileId for scan file id: " 
				//									+ psmWebDisplayWebServiceResult.getScanFileId() );
				//						} else {
				//							SingleScan_SubResponse singleScan_SubResponse = 
				//									scanFromSpectalStorage_Key_ScanNumber.get( psmWebDisplayWebServiceResult.getScanNumber() );
				//							if ( singleScan_SubResponse == null ) {
				//								log.error( "No entry in scanFromSpectalStorage_Key_ScanNumber for scan number: "
				//										+ psmWebDisplayWebServiceResult.getScanNumber()
				//										+ ", scan file id: " 
				//										+ psmWebDisplayWebServiceResult.getScanFileId() );
				//							} else {
				//								scanPreMZasDoubleSet = true;
				//								scanPreMZasDouble = singleScan_SubResponse.getPrecursor_M_Over_Z();
				//							}
				//						}
				//					}

				BigDecimal scanPreMZ = psmWebDisplayWebServiceResult.getPreMZ(); // from scan table

				if ( ! scanPreMZasDoubleSet ) {
					scanPreMZasDouble = scanPreMZ.doubleValue();
					scanPreMZasDoubleSet = true;
				}

				PsmDTO psmDTO = psmWebDisplayWebServiceResult.getPsmDTO();
				Integer charge = psmDTO.getCharge();
				BigDecimal linkerMass = psmDTO.getLinkerMass();

				Double linkerMassAsDouble = null;

				if ( linkerMass != null ) {
					linkerMassAsDouble = linkerMass.doubleValue();
				}

				if ( charge != null && scanPreMZasDoubleSet ) {

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
		
		if ( ! reportedPeptideIdsSkippedForErrorCalculatingMZ.isEmpty() ) {
			
			log.warn( "Number of Reported Peptides Skipped For Error Calculating MZ: " + reportedPeptideIdsSkippedForErrorCalculatingMZ.size()
					+ ", search id: " + searchDTO.getSearchId()
					+ ", List of Reported Peptide Ids: " + reportedPeptideIdsSkippedForErrorCalculatingMZ );
		}
		
		return ppmErrorListForLinkType_ByLinkType;
	}
	
//	/**
//	 * @param psmWebDisplayList
//	 * @return null if cannot get scan data from Spectral Storage Service for all entries (scan file id or scan number is null)
//	 * @throws Exception 
//	 */
//	private Map<Integer,Map<Integer,SingleScan_SubResponse>> getPrecursorMassFromSpectralStorageService( List<PsmWebDisplayWebServiceResult> psmWebDisplayList ) throws Exception {
//
//		//  Get scan precursor mass (Scan Pre MZ) from Spectral Storage Service
//		
//		Map<Integer,Map<Integer,SingleScan_SubResponse>> scanFromSpectralStorage_Key_ScanNumber_Key_ScanFileId = new HashMap<>();
//		
//		//  Get scan numbers per scan file id
//		
//		Map<Integer,Set<Integer>> scanNumbersKeyedOnScanFileId = new HashMap<>();
//		int prevScanFileId = -1;
//		Set<Integer> scanNumbersSet_Current = null; // optimization
//
//		for ( PsmWebDisplayWebServiceResult psmWebDisplayWebServiceResult : psmWebDisplayList ) {
//
//			if ( psmWebDisplayWebServiceResult.getScanFileId() == null 
//					|| psmWebDisplayWebServiceResult.getScanNumber() == null ) {
//				
//				return null;  // Early exit since cannot get scan data from Spectral Storage Service for all entries
//			}
//			
//			if ( scanNumbersSet_Current == null ) {  // First entry
//				scanNumbersSet_Current = new HashSet<>();
//				scanNumbersKeyedOnScanFileId.put( psmWebDisplayWebServiceResult.getScanFileId(), scanNumbersSet_Current );
//				prevScanFileId = psmWebDisplayWebServiceResult.getScanFileId();
//			} else if ( prevScanFileId != psmWebDisplayWebServiceResult.getScanFileId() ) {
//				//  Scan file id diff from prev record in list
//				scanNumbersSet_Current = scanNumbersKeyedOnScanFileId.get( psmWebDisplayWebServiceResult.getScanFileId() );
//				if ( scanNumbersSet_Current == null ) {
//					scanNumbersSet_Current = new HashSet<>();
//					scanNumbersKeyedOnScanFileId.put( psmWebDisplayWebServiceResult.getScanFileId(), scanNumbersSet_Current );
//				}
//				prevScanFileId = psmWebDisplayWebServiceResult.getScanFileId();
//			}
//			scanNumbersSet_Current.add( psmWebDisplayWebServiceResult.getScanNumber() );
//		}
//		
//		Call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice =
//				Call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice.getSingletonInstance();
//		
//		//  Process scan numbers per scan file id
//		
//		for ( Map.Entry<Integer,Set<Integer>> entry : scanNumbersKeyedOnScanFileId.entrySet() ) {
//			Integer scanFileId = entry.getKey();
//			Set<Integer> scanNumbersSet = entry.getValue();
//			
//			Map<Integer,SingleScan_SubResponse> scanFromSpectralStorage_Key_ScanNumber_Key =
//					get_precursorMass_Key_ScanNumber_Key_ForScanFileIdScanNumbers( 
//							scanFileId, scanNumbersSet, call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice );
//			if ( scanFromSpectralStorage_Key_ScanNumber_Key == null ) {
//				return null; // EARLY EXIT since cannot get data
//			}
//			scanFromSpectralStorage_Key_ScanNumber_Key_ScanFileId.put( scanFileId, scanFromSpectralStorage_Key_ScanNumber_Key );
//		}
//		
//		return scanFromSpectralStorage_Key_ScanNumber_Key_ScanFileId;
//	}
//	
//	/**
//	 * @param scanFileId
//	 * @param scanNumbersSet
//	 * @param call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice
//	 * @return null if not all data found or matched up
//	 * @throws Exception 
//	 */
//	private Map<Integer,SingleScan_SubResponse> get_precursorMass_Key_ScanNumber_Key_ForScanFileIdScanNumbers( 
//			Integer scanFileId, Set<Integer> scanNumbersSet,Call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice ) throws Exception {
//		
//		String spectralStorageAPIKey = ScanFileDAO.getInstance().getSpectralStorageAPIKeyById( scanFileId );
//		if ( StringUtils.isEmpty( spectralStorageAPIKey ) ) {
//			String msg = "spectralStorageAPIKey not found for scan file id: " + scanFileId;
//			log.error( msg );
//			return null; // EARLY EXIT since cannot get data
//		}
//		
//		Map<Integer,SingleScan_SubResponse> scanFromSpectralStorage_Key_ScanNumber_Key = new HashMap<>();
//		
//		List<Integer> scanNumbersList = new ArrayList<>( scanNumbersSet );
//		Collections.sort( scanNumbersList );
//		
//		List<SingleScan_SubResponse> scansFromSpectralStorage =
//				call_Get_ScanDataFromScanNumbers_SpectralStorageWebservice.getScanDataFromSpectralStorageService( 
//						scanNumbersList, 
//						Get_ScanDataFromScanNumbers_IncludeParentScans.NO, 
//						Get_ScanData_ExcludeReturnScanPeakData.YES, 
//						spectralStorageAPIKey );
//		
//		Set<Integer> scanNumbersSetValidateReturnedValues = new HashSet<>( scanNumbersSet );
//		
//		for ( SingleScan_SubResponse singleScan_SubResponse : scansFromSpectralStorage ) {
//			if ( ! scanNumbersSetValidateReturnedValues.remove( singleScan_SubResponse.getScanNumber() ) ) {
//				// scan number returned not in request set
//				String msg = "scan number not in request set: " + singleScan_SubResponse.getScanNumber()
//				+ ", for scan file id: " + scanFileId;
//				log.error( msg );
//				return null; // EARLY EXIT since cannot get data
//			}
//			scanFromSpectralStorage_Key_ScanNumber_Key.put( singleScan_SubResponse.getScanNumber(), singleScan_SubResponse );
//		}
//		
//		if ( ! scanNumbersSetValidateReturnedValues.isEmpty() ) {
//			String msg = "the following scan numbers in request not in result from spectral storage: "  
//					+ StringUtils.join( scanNumbersSetValidateReturnedValues )
//					+ ", for scan file id: " + scanFileId;
//			log.error( msg );
//			return null; // EARLY EXIT since cannot get data
//		}
//		
//		return scanFromSpectralStorage_Key_ScanNumber_Key;
//	}

}
