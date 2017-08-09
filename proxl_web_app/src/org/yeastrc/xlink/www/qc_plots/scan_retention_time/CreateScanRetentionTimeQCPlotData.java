package org.yeastrc.xlink.www.qc_plots.scan_retention_time;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ScanRetentionTimeDAO;
import org.yeastrc.xlink.dto.ScanRetentionTimeDTO;
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
	 * @param projectSearchId
	 * @param scanFileIdList
	 * @param scanFileAll
	 * @param filterCriteria_JSONString
	 * @param retentionTimeInSecondsCutoff
	 * @return
	 * @throws Exception
	 */
	public ScanRetentionTimeJSONRoot create( 
			int projectSearchId, 
			List<Integer> scanFileIdList, 
			boolean scanFileAll, 
			List<String> scansForSelectedLinkTypes,
			String filterCriteria_JSONString,
			Double retentionTimeInSecondsCutoff ) throws Exception {

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
			ScanRetentionTimeJSONRoot result = new ScanRetentionTimeJSONRoot();
			result.setScanFileIdList( scanFileIdList );
			return result; //  EARLY EXIT
		}
		
		List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList = null;
		if ( ! scansForSelectedLinkTypes.isEmpty() ) {
			retentionTimeForPSMsthatMeetCriteriaList = 
					getRetentionTimeForPSMsthatMeetCriteriaList( 
							projectSearchId, 
							searchDTO,
							scanFileIdList, 
							scanFileAll, 
							scansForSelectedLinkTypes, 
							filterCriteria_JSONString, 
							retentionTimeInSecondsCutoff );
		}
		return createScanRetentionTimeQCPlotData( scanFileIdList, retentionTimeInSecondsCutoff, retentionTimeForPSMsthatMeetCriteriaList );
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
			List<String> scansForSelectedLinkTypes,	
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

		//  Copy link types requested into an array to pass to GetLinkTypesForSearchers
		String [] linkTypesUserRequestInOverlay = null;
		if ( scansForSelectedLinkTypes != null && ( ! scansForSelectedLinkTypes.isEmpty() ) ) {
			linkTypesUserRequestInOverlay = new String[ scansForSelectedLinkTypes.size() ];
			{
				int index = 0;
				for ( String linkType : scansForSelectedLinkTypes ) {
					linkTypesUserRequestInOverlay[ index ] = linkType;
					index++;
				}
			}
		}

		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( linkTypesUserRequestInOverlay );
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
	 * @param retentionTimeInSecondsCutoff
	 * @param retentionTimeForPSMsthatMeetCriteriaList
	 * @return
	 * @throws Exception
	 */
	private ScanRetentionTimeJSONRoot createScanRetentionTimeQCPlotData( 
			List<Integer> scanFileIdList, 
			Double retentionTimeInSecondsCutoff, 
			List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList ) throws Exception {
		int numScans = 0;
		List<ScanRetentionTimeDTO> scanRetentionTime_AllScansExcludeScanLevel_1_List = null;
		for ( int scanFileId : scanFileIdList ) {
			List<ScanRetentionTimeDTO> scanRetentionTime_ForThisScanFileId_List =
					ScanRetentionTimeDAO.getForScanFileIdExcludeScanLevel( scanFileId, retentionTimeInSecondsCutoff, EXCLUDE_SCAN_LEVEL_1 );
			if ( scanRetentionTime_AllScansExcludeScanLevel_1_List == null ) {
				scanRetentionTime_AllScansExcludeScanLevel_1_List = scanRetentionTime_ForThisScanFileId_List;
			} else {
				scanRetentionTime_AllScansExcludeScanLevel_1_List.addAll( scanRetentionTime_ForThisScanFileId_List );
			}
		}
		//  Find max and min values
		double retentionTimeMin = 0;
		double retentionTimeMax =  0;
		boolean firstOverallRetentionTimeEntry = true;
		for ( ScanRetentionTimeDTO scanRetentionTimeDTO : scanRetentionTime_AllScansExcludeScanLevel_1_List ) {
			BigDecimal retentionTime = scanRetentionTimeDTO.getRetentionTime();
			double retentionTimeScaled = 
					RetentionTimeScalingAndRounding.retentionTimeToMinutes( retentionTime ).doubleValue();
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
		double retentionTimeMaxMinusMin = retentionTimeMax - retentionTimeMin;
		//  Process data into bins
		double binSizeAsDouble = ( retentionTimeMax ) / BIN_COUNT;
		//   First process the retention times for all PSMs for the scan file into bins, excluding scan level 1 (EXCLUDE_SCAN_LEVEL_1)
		int[] retentionTimeCountsAllPSMs = new int[ BIN_COUNT ];
		for ( ScanRetentionTimeDTO scanRetentionTimeDTO : scanRetentionTime_AllScansExcludeScanLevel_1_List ) {
			BigDecimal retentionTime = scanRetentionTimeDTO.getRetentionTime();
			double retentionTimeFraction = RetentionTimeScalingAndRounding.retentionTimeToMinutes( retentionTime ).doubleValue() / retentionTimeMax;
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
				double retentionTimeFraction = 
						RetentionTimeScalingAndRounding.retentionTimeToMinutes(  retentionTimeForPSMsthatMeetCriteria ).doubleValue() / retentionTimeMax;
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
