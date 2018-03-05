package org.yeastrc.xlink.www.qc_plots.scan_retention_time;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.Single_ScanRetentionTime_ScanNumber_SubResponse;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.searcher.RetentionTimesForPSMCriteriaSearcher;
import org.yeastrc.xlink.www.searcher.ScanFileIdsForSearchSearcher;
import org.yeastrc.xlink.www.spectral_storage_service_interface.Call_Get_ScanRetentionTimes_FromSpectralStorageService;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import org.yeastrc.xlink.www.web_utils.RetentionTimeScalingAndRounding;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class CreateScanRetentionTimeQCPlotData {

	private static final Logger log = Logger.getLogger(CreateScanRetentionTimeQCPlotData.class);
	
	public enum ForDownload { YES, NO }
	
	private static final int BIN_COUNT = 100;  //  Number of bars on the chart
	private static final int EXCLUDE_SCAN_LEVEL_1 = 1;
	
	/**
	 * private constructor
	 */
	private CreateScanRetentionTimeQCPlotData(){}
	public static CreateScanRetentionTimeQCPlotData getInstance( ) throws Exception {
		CreateScanRetentionTimeQCPlotData instance = new CreateScanRetentionTimeQCPlotData();
		return instance;
	}
	
	/**
	 * Result from method create
	 *
	 */
	public static class CreateScanRetentionTimeQCPlotData_Result {
		
		ScanRetentionTimeJSONRoot scanRetentionTimeJSONRoot;
		
		List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList;
		List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanRetentionTime_AllScansExcludeScanLevel_1_List;

		public ScanRetentionTimeJSONRoot getScanRetentionTimeJSONRoot() {
			return scanRetentionTimeJSONRoot;
		}

		public void setScanRetentionTimeJSONRoot(ScanRetentionTimeJSONRoot scanRetentionTimeJSONRoot) {
			this.scanRetentionTimeJSONRoot = scanRetentionTimeJSONRoot;
		}

		public List<BigDecimal> getRetentionTimeForPSMsthatMeetCriteriaList() {
			return retentionTimeForPSMsthatMeetCriteriaList;
		}

		public void setRetentionTimeForPSMsthatMeetCriteriaList(List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList) {
			this.retentionTimeForPSMsthatMeetCriteriaList = retentionTimeForPSMsthatMeetCriteriaList;
		}

		public List<Single_ScanRetentionTime_ScanNumber_SubResponse> getScanRetentionTime_AllScansExcludeScanLevel_1_List() {
			return scanRetentionTime_AllScansExcludeScanLevel_1_List;
		}

		public void setScanRetentionTime_AllScansExcludeScanLevel_1_List(
				List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanRetentionTime_AllScansExcludeScanLevel_1_List) {
			this.scanRetentionTime_AllScansExcludeScanLevel_1_List = scanRetentionTime_AllScansExcludeScanLevel_1_List;
		}
		
	}
	
	/**
	 * @param projectSearchId
	 * @param scanFileIdList
	 * @param scanFileAll
	 * @param filterCriteria_JSONString
	 * @param retentionTimeInSecondsCutoff
	 * @return
	 * @throws Exception
	 */
	public CreateScanRetentionTimeQCPlotData_Result create( 
			ForDownload forDownload,
			int projectSearchId, 
			List<Integer> scanFileIdList, 
			boolean scanFileAll, 
			String filterCriteria_JSONString,
			Double retentionTimeInSecondsCutoff ) throws Exception {
		
		CreateScanRetentionTimeQCPlotData_Result result = new CreateScanRetentionTimeQCPlotData_Result();

		SearchDTO searchDTO = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
		if ( searchDTO == null ) {
			String msg = "projectSearchId '" + projectSearchId + "' not found in the database.";
			log.warn( msg );
			//  Search not found, the data on the page they are requesting does not exist.
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
		}
		
		if ( scanFileAll && ( scanFileIdList == null || scanFileIdList.isEmpty() ) ) {
			//  Get scan file list from db for projectSearchId
			scanFileIdList = ScanFileIdsForSearchSearcher.getInstance().getScanFileIdsForSearchId( searchDTO.getSearchId() );
		}

		if ( scanFileIdList.isEmpty() ) {
			//  Handle here if no scan files for search id
			ScanRetentionTimeJSONRoot scanRetentionTimeJSONRoot = new ScanRetentionTimeJSONRoot();
			scanRetentionTimeJSONRoot.setScanFileIdList( scanFileIdList );
			
			result.scanRetentionTimeJSONRoot = scanRetentionTimeJSONRoot;
			return result; //  EARLY EXIT
		}
		
		List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList =
					getRetentionTimeForPSMsthatMeetCriteriaList( 
							projectSearchId, 
							searchDTO,
							scanFileIdList, 
							scanFileAll, 
							filterCriteria_JSONString, 
							retentionTimeInSecondsCutoff );
		
		List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanRetentionTime_AllScansExcludeScanLevel_1_List = null;
		for ( int scanFileId : scanFileIdList ) {
			List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanPartsNoLevel_1_ForThisScanFileId_List = 
					getScanParts_NoLevel_1_WithRetentionTimesForScanFileId_retentionTimeInSecondsCutoff( scanFileId, retentionTimeInSecondsCutoff );
			if ( scanRetentionTime_AllScansExcludeScanLevel_1_List == null ) {
				scanRetentionTime_AllScansExcludeScanLevel_1_List = scanPartsNoLevel_1_ForThisScanFileId_List;
			} else {
				scanRetentionTime_AllScansExcludeScanLevel_1_List.addAll( scanPartsNoLevel_1_ForThisScanFileId_List );
			}
		}
		
		result.retentionTimeForPSMsthatMeetCriteriaList = retentionTimeForPSMsthatMeetCriteriaList;
		result.scanRetentionTime_AllScansExcludeScanLevel_1_List = scanRetentionTime_AllScansExcludeScanLevel_1_List;
		
		if ( forDownload == ForDownload.YES ) {
			return result;  //  EARLY EXIT
		}
		
		ScanRetentionTimeJSONRoot scanRetentionTimeJSONRoot = 
				createScanRetentionTimeQCPlotData( 
						scanFileIdList, 
						scanRetentionTime_AllScansExcludeScanLevel_1_List,
						retentionTimeForPSMsthatMeetCriteriaList );
		
		result.scanRetentionTimeJSONRoot = scanRetentionTimeJSONRoot;
		
		return result;
	}
	
	private List<Single_ScanRetentionTime_ScanNumber_SubResponse> getScanParts_NoLevel_1_WithRetentionTimesForScanFileId_retentionTimeInSecondsCutoff( 
			int scanFileId,
			Double retentionTimeInSecondsCutoff ) throws Exception {

		String scanFileAPIKey = ScanFileDAO.getInstance().getSpectralStorageAPIKeyById( scanFileId );
		if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
			String msg = "No value for scanFileAPIKey for scan file id: " + scanFileId;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}

		//  Filter out scan level 1 from Spectral Storage data
		
		final int EXCLUDED_SCAN_LEVEL_1 = 1;
		
		List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanPartsNoLevel_1 = 
				Call_Get_ScanRetentionTimes_FromSpectralStorageService.getSingletonInstance().get_ScanRetentionTimes_Exclude_ScanLevel( scanFileAPIKey, EXCLUDED_SCAN_LEVEL_1 );

		//  If retentionTimeInSecondsCutoff, filter for retentionTimeInSecondsCutoff
		
		if ( retentionTimeInSecondsCutoff != null ) {
			//  Filter for retentionTimeInSecondsCutoff
			List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanPartsFiltered = new ArrayList<>( scanPartsNoLevel_1.size() );
			for ( Single_ScanRetentionTime_ScanNumber_SubResponse scanPart : scanPartsNoLevel_1 ) {
				if ( scanPart.getRetentionTime() < retentionTimeInSecondsCutoff ) {
					scanPartsFiltered.add( scanPart );
				}
			}
			scanPartsNoLevel_1 = scanPartsFiltered;
		}
		
		return scanPartsNoLevel_1;
	}
	
	
	/**
	 * @param projectSearchId
	 * @param searchDTO
	 * @param scanFileIdList
	 * @param scanFileAll
	 * @param scansForSelectedLinkTypes
	 * @param filterCriteria_JSONString
	 * @param retentionTimeInSecondsCutoff
	 * @return
	 * @throws Exception
	 */
	private List<BigDecimal> getRetentionTimeForPSMsthatMeetCriteriaList( 
			int projectSearchId,
			SearchDTO searchDTO, 
			List<Integer> scanFileIdList, 
			boolean scanFileAll, 
			String filterCriteria_JSONString,
			Double retentionTimeInSecondsCutoff ) throws Exception {
		
		List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList = null;
		
		Collection<Integer> searchIds = new HashSet<>();
		searchIds.add( searchDTO.getSearchId() );

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		//   deserialize 
		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = null;
		try {
			mergedPeptideQueryJSONRoot = jacksonJSON_Mapper.readValue( filterCriteria_JSONString, MergedPeptideQueryJSONRoot.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', JsonParseException.  filterCriteriaJSON: " + filterCriteria_JSONString;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', JsonMappingException.  filterCriteriaJSON: " + filterCriteria_JSONString;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', IOException.  filterCriteriaJSON: " + filterCriteria_JSONString;
			log.error( msg, e );
			throw e;
		}
		//  Process retention times for psms that meet criteria
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
				searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
		if ( searcherCutoffValuesSearchLevel == null ) {
			String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( mergedPeptideQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();
		
		Integer scanFileId = null;
		if ( ! scanFileAll ) {
			scanFileId = scanFileIdList.get( 0 );
		}
		
		retentionTimeForPSMsthatMeetCriteriaList =
				RetentionTimesForPSMCriteriaSearcher.getInstance()
				.getRetentionTimes( 
						searchDTO.getSearchId(), 
						scanFileId, //  null if all requested 
						searcherCutoffValuesSearchLevel, 
						linkTypesForDBQuery, 
						modsForDBQuery,
						retentionTimeInSecondsCutoff );
		
		return retentionTimeForPSMsthatMeetCriteriaList;
	}
	
	/**
	 * @param scanFileIdList
	 * @param scanRetentionTime_AllScansExcludeScanLevel_1_List
	 * @param retentionTimeForPSMsthatMeetCriteriaList
	 * @return
	 * @throws Exception
	 */
	private ScanRetentionTimeJSONRoot createScanRetentionTimeQCPlotData( 
			List<Integer> scanFileIdList,
			List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanRetentionTime_AllScansExcludeScanLevel_1_List,
			List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList ) throws Exception {
		int numScans = 0;
		//  Find max and min values
		float retentionTimeMin = 0;
		float retentionTimeMax =  0;
		boolean firstOverallRetentionTimeEntry = true;
		for ( Single_ScanRetentionTime_ScanNumber_SubResponse single_ScanRetentionTime_ScanNumber_SubResponse : scanRetentionTime_AllScansExcludeScanLevel_1_List ) {
			float retentionTime = single_ScanRetentionTime_ScanNumber_SubResponse.getRetentionTime();
			float retentionTimeScaled = 
					RetentionTimeScalingAndRounding.retentionTimeToMinutes( retentionTime );
			if ( firstOverallRetentionTimeEntry  ) {
				firstOverallRetentionTimeEntry = false;
				retentionTimeMin = retentionTimeScaled;
				retentionTimeMax = retentionTimeScaled;
			} else {
				if ( retentionTimeScaled < retentionTimeMin ) {
					retentionTimeMin = retentionTimeScaled;
				}
				if ( retentionTimeScaled > retentionTimeMax  ) {
					retentionTimeMax = retentionTimeScaled;
				}
			}
		}
		float retentionTimeMaxMinusMin = retentionTimeMax - retentionTimeMin;
		//  Process data into bins
		float binSizeAsDouble = ( retentionTimeMax ) / BIN_COUNT;
		//   First process the retention times for all PSMs for the scan file into bins, excluding scan level 1 (EXCLUDE_SCAN_LEVEL_1)
		int[] retentionTimeCountsAllPSMs = new int[ BIN_COUNT ];
		for ( Single_ScanRetentionTime_ScanNumber_SubResponse single_ScanRetentionTime_ScanNumber_SubResponse : scanRetentionTime_AllScansExcludeScanLevel_1_List ) {
			float retentionTime = single_ScanRetentionTime_ScanNumber_SubResponse.getRetentionTime();
			float retentionTimeFraction = RetentionTimeScalingAndRounding.retentionTimeToMinutes( retentionTime ) / retentionTimeMax;
			int bin = (int) ( (  retentionTimeFraction ) * BIN_COUNT );
			if ( bin < 0 ) {
				bin = 0;
			} else if ( bin >= BIN_COUNT ) {
				bin = BIN_COUNT - 1;
			} 
			retentionTimeCountsAllPSMs[ bin ]++;
		}
		//  Second process the retention times for the PSMs that meet the selection criteria for the scan file into bins
		int[] retentionTimeForPsmthatMeetCriteriaCounts = new int[ BIN_COUNT ];
		if ( retentionTimeForPSMsthatMeetCriteriaList != null ) {
			for ( BigDecimal retentionTimeForPSMsthatMeetCriteria : retentionTimeForPSMsthatMeetCriteriaList ) {
				float retentionTimeFraction = 
						RetentionTimeScalingAndRounding.retentionTimeToMinutes(  retentionTimeForPSMsthatMeetCriteria ).floatValue() / retentionTimeMax;
				int bin = (int) ( (  retentionTimeFraction ) * BIN_COUNT );
				if ( bin < 0 ) {
					bin = 0;
				} else if ( bin >= BIN_COUNT ) {
					bin = BIN_COUNT - 1;
				} 
				retentionTimeForPsmthatMeetCriteriaCounts[ bin ]++;
			}
		}
		//  Populate objects to generate JSON
		ScanRetentionTimeJSONRoot scanRetentionTimeJSONRoot = new ScanRetentionTimeJSONRoot();
		scanRetentionTimeJSONRoot.setScanFileIdList( scanFileIdList );
		scanRetentionTimeJSONRoot.setNumScans( numScans );
		scanRetentionTimeJSONRoot.setRetentionTimeMax( retentionTimeMaxMinusMin );
		scanRetentionTimeJSONRoot.setRetentionTimeMin( retentionTimeMin );
		List<ScanRetentionTimeJSONChartBucket> chartBuckets = new ArrayList<>();
		scanRetentionTimeJSONRoot.setChartBuckets( chartBuckets );
		double binHalf = binSizeAsDouble / 2 ;
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		for ( int binIndex = 0; binIndex < retentionTimeCountsAllPSMs.length; binIndex++ ) {
			ScanRetentionTimeJSONChartBucket chartBucket = new ScanRetentionTimeJSONChartBucket();
			chartBuckets.add( chartBucket );
			int retentionTimeCount = retentionTimeCountsAllPSMs[ binIndex ];
			int retentionTimeForPsmsThatMeetCriteriaCount = retentionTimeForPsmthatMeetCriteriaCounts[ binIndex ];
			double binStartDouble = ( ( binIndex * binSizeAsDouble ) );
			if ( binIndex == 0 && binStartDouble < 0.1 ) {
				chartBucket.setBinStart( 0 );
			} else { 
				int binStart = (int)Math.round( binStartDouble );
				chartBucket.setBinStart( binStart );
			}
			int binEnd = (int)Math.round( ( binIndex + 1 ) * binSizeAsDouble );
			chartBucket.setBinEnd( binEnd );
			double binMiddleDouble = binStartDouble + binHalf;
			chartBucket.setBinCenter( binMiddleDouble );
			chartBucket.setTotalCount( retentionTimeCount );
			chartBucket.setCountForPsmsThatMeetCriteria( retentionTimeForPsmsThatMeetCriteriaCount );
		}
		return scanRetentionTimeJSONRoot;
	}
}
