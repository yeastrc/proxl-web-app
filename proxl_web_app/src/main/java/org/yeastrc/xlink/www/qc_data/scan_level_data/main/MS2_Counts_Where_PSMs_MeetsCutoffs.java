package org.yeastrc.xlink.www.qc_data.scan_level_data.main;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.MS2_Counts_Where_PSMs_MeetsCutoffsResults;
import org.yeastrc.xlink.www.searcher.Scan_CountsPerLinkTypeForSearchScanFileSearcher;
import org.yeastrc.xlink.www.searcher.Scan_CountsPerLinkTypeForSearchScanFileSearcher.PSM_CountsPerLinkTypeForSearchScanFileResult;

/**
 * MS2 counts per link type where assoc PSMs meet cutoffs
 *
 */
public class MS2_Counts_Where_PSMs_MeetsCutoffs {

	private static final Logger log = LoggerFactory.getLogger( MS2_Counts_Where_PSMs_MeetsCutoffs.class);
	
	/**
	 * private constructor
	 */
	private MS2_Counts_Where_PSMs_MeetsCutoffs(){}
	public static MS2_Counts_Where_PSMs_MeetsCutoffs getInstance( ) throws Exception {
		MS2_Counts_Where_PSMs_MeetsCutoffs instance = new MS2_Counts_Where_PSMs_MeetsCutoffs();
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
	public MS2_Counts_Where_PSMs_MeetsCutoffsResults getMS2_Counts_Where_PSMs_MeetsCutoffs( 			
			QCPageQueryJSONRoot qcPageQueryJSONRoot, 
			int projectSearchId,
			int searchId,
			int scanFileId ) throws Exception {
		
		Collection<Integer> searchIds = new HashSet<>();
		searchIds.add( searchId );
		
		///////////////////////////////////////////////////
		//   Mods for DB Query
		String[] modsForDBQuery = qcPageQueryJSONRoot.getMods();
		
		
		List<Integer> includeProteinSeqVIdsDecodedArray = qcPageQueryJSONRoot.getIncludeProteinSeqVIdsDecodedArray();
		
		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = qcPageQueryJSONRoot.getCutoffs();
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
		
		PSM_CountsPerLinkTypeForSearchScanFileResult psm_CountsPerLinkTypeForSearchScanFileResult =
				Scan_CountsPerLinkTypeForSearchScanFileSearcher.getInstance()
				.getPSM_CountsPerLinkTypeForSearchScanFile( 
						searchId, scanFileId, searcherCutoffValuesSearchLevel, modsForDBQuery, includeProteinSeqVIdsDecodedArray );

		Map<String,Long> resultsMS2CountMap_KeyedOnLinkType =
				psm_CountsPerLinkTypeForSearchScanFileResult.getResultsMS2CountMap_KeyedOnLinkType();
		
		
		//  Link Type includes 'dimer' which has be combined with 'unlinked'
		Long dimerCount = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.DIMER_TYPE_STRING );
		if ( dimerCount != null ) {
			Long unlinkedCount = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
			if ( unlinkedCount == null ) {
				resultsMS2CountMap_KeyedOnLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, dimerCount );
			} else {
				Long unlinkedAndDimerCount = unlinkedCount.longValue() + dimerCount.longValue();
				resultsMS2CountMap_KeyedOnLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, unlinkedAndDimerCount );
			}
		}
		
		Long crosslinkCount = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.CROSS_TYPE_STRING );
		Long looplinkCount = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.LOOP_TYPE_STRING );
		Long unlinkedCount = resultsMS2CountMap_KeyedOnLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );

		MS2_Counts_Where_PSMs_MeetsCutoffsResults ms2CountsResults = new MS2_Counts_Where_PSMs_MeetsCutoffsResults();
		
		if ( crosslinkCount != null ) {
			ms2CountsResults.setCrosslinkCount(crosslinkCount);
		}
		if ( looplinkCount != null ) {
			ms2CountsResults.setLooplinkCount( looplinkCount );
		}
		if ( unlinkedCount != null ) {
			ms2CountsResults.setUnlinkedCount( unlinkedCount );
		}
		
		return ms2CountsResults;
	}

}
