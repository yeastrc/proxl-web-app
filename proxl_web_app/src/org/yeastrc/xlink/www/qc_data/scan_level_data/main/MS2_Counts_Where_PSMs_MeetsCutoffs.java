package org.yeastrc.xlink.www.qc_data.scan_level_data.main;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.MS2_Counts_Where_PSMs_MeetsCutoffsResults;
import org.yeastrc.xlink.www.searcher.Scan_CountsPerLinkTypeForSearchScanFileSearcher;
import org.yeastrc.xlink.www.searcher.Scan_CountsPerLinkTypeForSearchScanFileSearcher.PSM_CountsPerLinkTypeForSearchScanFileResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * MS2 counts per link type where assoc PSMs meet cutoffs
 *
 */
public class MS2_Counts_Where_PSMs_MeetsCutoffs {

	private static final Logger log = Logger.getLogger(MS2_Counts_Where_PSMs_MeetsCutoffs.class);
	
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
			String filterCriteriaJSON, 
			int projectSearchId,
			int searchId,
			int scanFileId ) throws Exception {
		
		Collection<Integer> searchIds = new HashSet<>();
		searchIds.add( searchId );
		
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
				searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
		if ( searcherCutoffValuesSearchLevel == null ) {
			String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		PSM_CountsPerLinkTypeForSearchScanFileResult psm_CountsPerLinkTypeForSearchScanFileResult =
				Scan_CountsPerLinkTypeForSearchScanFileSearcher.getInstance()
				.getPSM_CountsPerLinkTypeForSearchScanFile( 
						searchId, scanFileId, searcherCutoffValuesSearchLevel, modsForDBQuery );

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
