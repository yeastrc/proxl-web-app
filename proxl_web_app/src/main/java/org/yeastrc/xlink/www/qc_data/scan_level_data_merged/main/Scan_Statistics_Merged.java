package org.yeastrc.xlink.www.qc_data.scan_level_data_merged.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScanLevelSummaryData_SubResponse;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.qc_data.a_enums.ForDownload_Enum;
import org.yeastrc.xlink.www.qc_data.scan_level_data_merged.objects.Scan_Statistics_Merged_Results;
import org.yeastrc.xlink.www.qc_data.scan_level_data_merged.objects.Scan_Statistics_Merged_Results.Scan_Statistics_PerSearch;
import org.yeastrc.xlink.www.searcher.Scan_CountsPerLinkTypeForSearchScanFileSearcher;
import org.yeastrc.xlink.www.searcher.ScanFileIdsForSearchSearcher;
import org.yeastrc.xlink.www.searcher.Scan_CountsPerLinkTypeForSearchScanFileSearcher.PSM_CountsPerLinkTypeForSearchScanFileResult;
import org.yeastrc.xlink.www.spectral_storage_service_interface.Call_Get_GetSummaryDataPerScanLevel_FromSpectralStorageService;

/**
 * QC Merged page, "Ion Current Statistics" Section, "Scan Statistics" table
 *
 */
public class Scan_Statistics_Merged {

	private static final Logger log = LoggerFactory.getLogger( Scan_Statistics_Merged.class);
	
	/**
	 * private constructor
	 */
	private Scan_Statistics_Merged(){}
	public static Scan_Statistics_Merged getInstance( ) throws Exception {
		Scan_Statistics_Merged instance = new Scan_Statistics_Merged();
		return instance;
	}
	
	/**
	 * @param filterCriteriaJSON
	 * @param searches
	 * @return
	 * @throws Exception
	 */
	public Scan_Statistics_Merged_Results getScan_Statistics_Merged( 
			//  One and only 1 of requestJSONBytes and requestJSONString can be not null
			byte[] requestJSONBytes,  //  Contents of POST to webservice.  Only used here for caching
			String requestJSONString,  //  Contents of JSON field in POST to download.  Only used here for caching
			ForDownload_Enum forDownload,
			QCPageQueryJSONRoot qcPageQueryJSONRoot, 
			List<SearchDTO> searches ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
		}
		
		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
//		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( qcPageQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = qcPageQueryJSONRoot.getMods();
		
		List<Integer> includeProteinSeqVIdsDecodedArray = qcPageQueryJSONRoot.getIncludeProteinSeqVIdsDecodedArray();

		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = qcPageQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		
		Map<Integer, Scan_Statistics_PerSearch> dataPerSearchMap_KeyProjectSearchId = new HashMap<>();
		boolean haveData = false;
		
		for ( SearchDTO search : searches ) {
			int searchId = search.getSearchId();
			int projectSearchId = search.getProjectSearchId();

			//  Get cutoffs for this project search id
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
			if ( searcherCutoffValuesSearchLevel == null ) {
				String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			Scan_Statistics_PerSearch scan_Statistics_PerSearch = new Scan_Statistics_PerSearch();
			dataPerSearchMap_KeyProjectSearchId.put( projectSearchId, scan_Statistics_PerSearch );
			
			scan_Statistics_PerSearch.setSearchId( searchId );
			
			if ( ! search.isHasScanData() ) {
				scan_Statistics_PerSearch.setHaveScanData( false );
				continue;  //  EARLY CONTINE
			}
			
			haveData = true;  //  Any data for all searches

			//  Overall statistics
			boolean haveSscanOverallData = true;
			
			long ms_1_ScanCount = 0;
			double ms_1_ScanIntensitiesSummed = 0;
			long ms_2_ScanCount = 0;
			double ms_2_ScanIntensitiesSummed = 0;
			
			//  MS2 Scans that meet PSM/Peptide Cutoff
			long crosslinkCount = 0;
			long looplinkCount = 0;
			/**
			 * includes dimers
			 */
			long unlinkedCount = 0;
			
			List<Integer> scanFileIdsForSearchId = ScanFileIdsForSearchSearcher.getInstance().getScanFileIdsForSearchId( searchId );

			for ( int scanFileId : scanFileIdsForSearchId ) {

				//  Overall statistics for scan file

				String scanFileAPIKey = ScanFileDAO.getInstance().getSpectralStorageAPIKeyById( scanFileId );
				if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
					String msg = "No value for scanFileAPIKey for scan file id: " + scanFileId;
					log.warn( msg );

					haveSscanOverallData = false; //  If no data for any scan file, then no scan overall data
				} else {

					List<SingleScanLevelSummaryData_SubResponse> scanSummaryPerScanLevelList = 
							Call_Get_GetSummaryDataPerScanLevel_FromSpectralStorageService.getSingletonInstance()
							.get_GetSummaryDataPerScanLevel_All( scanFileAPIKey );

					for ( SingleScanLevelSummaryData_SubResponse singleScanLevelSummaryData_SubResponse : scanSummaryPerScanLevelList ) {
						if ( singleScanLevelSummaryData_SubResponse.getScanLevel() == 1 ) {
							ms_1_ScanCount += singleScanLevelSummaryData_SubResponse.getNumberOfScans();
							ms_1_ScanIntensitiesSummed += singleScanLevelSummaryData_SubResponse.getTotalIonCurrent();
						} else if ( singleScanLevelSummaryData_SubResponse.getScanLevel() == 2 ) {
							ms_2_ScanCount += singleScanLevelSummaryData_SubResponse.getNumberOfScans();
							ms_2_ScanIntensitiesSummed += singleScanLevelSummaryData_SubResponse.getTotalIonCurrent();
						}
					}
					
				}

//				///   MS2 counts For Cutoffs 

				PSM_CountsPerLinkTypeForSearchScanFileResult psm_CountsPerLinkTypeForSearchScanFileResult =
						Scan_CountsPerLinkTypeForSearchScanFileSearcher.getInstance()
						.getPSM_CountsPerLinkTypeForSearchScanFile( 
								searchId, scanFileId, searcherCutoffValuesSearchLevel, modsForDBQuery, includeProteinSeqVIdsDecodedArray );

				Map<String,Long> resultsMS2CountMap_KeyedOnLinkType =
						psm_CountsPerLinkTypeForSearchScanFileResult.getResultsMS2CountMap_KeyedOnLinkType();


				//  Link Type includes 'dimer' which has be combined with 'unlinked'
				Long dimerCount = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.DIMER_TYPE_STRING );
				if ( dimerCount != null ) {
					Long unlinkedCountForScanFileId = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
					if ( unlinkedCountForScanFileId == null ) {
						resultsMS2CountMap_KeyedOnLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, dimerCount );
					} else {
						Long unlinkedAndDimerCount = unlinkedCountForScanFileId.longValue() + dimerCount.longValue();
						resultsMS2CountMap_KeyedOnLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, unlinkedAndDimerCount );
					}
				}
				
				Long crosslinkCountForScanFileId = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.CROSS_TYPE_STRING );
				Long looplinkCountForScanFileId = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.LOOP_TYPE_STRING );
				Long unlinkedCountForScanFileId = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
				
				if ( crosslinkCountForScanFileId != null ) {
					crosslinkCount += crosslinkCountForScanFileId;
				}
				if ( looplinkCountForScanFileId != null ) {
					looplinkCount += looplinkCountForScanFileId;
				}
				if ( unlinkedCountForScanFileId != null ) {
					unlinkedCount += unlinkedCountForScanFileId;
				}
			}
			
			scan_Statistics_PerSearch.setHaveScanData( true );
			scan_Statistics_PerSearch.setHaveSscanOverallData( haveSscanOverallData );
			scan_Statistics_PerSearch.setMs_1_ScanCount( ms_1_ScanCount );
			scan_Statistics_PerSearch.setMs_1_ScanIntensitiesSummed( ms_1_ScanIntensitiesSummed );
			scan_Statistics_PerSearch.setMs_2_ScanCount( ms_2_ScanCount );
			scan_Statistics_PerSearch.setMs_2_ScanIntensitiesSummed( ms_2_ScanIntensitiesSummed );

			scan_Statistics_PerSearch.setCrosslinkCount( crosslinkCount );
			scan_Statistics_PerSearch.setLooplinkCount( looplinkCount );
			scan_Statistics_PerSearch.setUnlinkedCount( unlinkedCount );
		}
		
		Scan_Statistics_Merged_Results results = new Scan_Statistics_Merged_Results();
		results.setDataPerSearchMap_KeyProjectSearchId( dataPerSearchMap_KeyProjectSearchId );
		results.setHaveData( haveData );
		
		return results;
	}
}
