package org.yeastrc.xlink.www.qc_data.scan_level_data_merged.main;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ScanFileStatisticsDAO;
import org.yeastrc.xlink.dto.ScanFileStatisticsDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.qc_data.scan_level_data_merged.objects.Scan_Statistics_Merged_Results;
import org.yeastrc.xlink.www.qc_data.scan_level_data_merged.objects.Scan_Statistics_Merged_Results.Scan_Statistics_PerSearch;
import org.yeastrc.xlink.www.searcher.Scan_CountsPerLinkTypeForSearchScanFileSearcher;
import org.yeastrc.xlink.www.searcher.ScanFileIdsForSearchSearcher;
import org.yeastrc.xlink.www.searcher.Scan_CountsPerLinkTypeForSearchScanFileSearcher.PSM_CountsPerLinkTypeForSearchScanFileResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * QC Merged page, "Ion Current Statistics" Section, "Scan Statistics" table
 *
 */
public class Scan_Statistics_Merged {

	private static final Logger log = Logger.getLogger(Scan_Statistics_Merged.class);
	
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
			String filterCriteriaJSON, 
			List<SearchDTO> searches ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
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
//		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( mergedPeptideQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();

		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = mergedPeptideQueryJSONRoot.getCutoffs();
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

				ScanFileStatisticsDTO scanFileStatisticsDTO = 
						ScanFileStatisticsDAO.getInstance().getScanFileStatisticsDTOForScanFileId( scanFileId ); 
				
				if ( scanFileStatisticsDTO == null ) {

					haveSscanOverallData = false; //  If no data for any scan file, then no scan overall data
				} else {
					
					ms_1_ScanCount += scanFileStatisticsDTO.getMs_1_ScanCount();
					ms_1_ScanIntensitiesSummed += scanFileStatisticsDTO.getMs_1_ScanIntensitiesSummed();
					ms_2_ScanCount += scanFileStatisticsDTO.getMs_2_ScanCount();
					ms_2_ScanIntensitiesSummed += scanFileStatisticsDTO.getMs_2_ScanIntensitiesSummed();
				}

//				///   MS2 counts For Cutoffs 

				PSM_CountsPerLinkTypeForSearchScanFileResult psm_CountsPerLinkTypeForSearchScanFileResult =
						Scan_CountsPerLinkTypeForSearchScanFileSearcher.getInstance()
						.getPSM_CountsPerLinkTypeForSearchScanFile( 
								searchId, scanFileId, searcherCutoffValuesSearchLevel, modsForDBQuery );

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
