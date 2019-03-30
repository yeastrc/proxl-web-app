package org.yeastrc.xlink.www.form_utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.A_QueryBase_JSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;

/**
 * Update A_QueryBase_JSONRoot object For Current Project Search Ids
 *
 * Remove entries in A_QueryBase_JSONRoot object not in Current Project Search Ids
 * 
 *  Add entries to A_QueryBase_JSONRoot that are only in Current Project Search Ids with default cutoffs
 */
public class Update__A_QueryBase_JSONRoot__ForCurrentSearchIds {

	private static final Logger log = LoggerFactory.getLogger( Update__A_QueryBase_JSONRoot__ForCurrentSearchIds.class);
	/**
	 * Static get instance
	 * @return
	 */
	public static Update__A_QueryBase_JSONRoot__ForCurrentSearchIds getInstance() {
		return new Update__A_QueryBase_JSONRoot__ForCurrentSearchIds(); 
	}
	//  constructor
	private Update__A_QueryBase_JSONRoot__ForCurrentSearchIds() { }
	
	/**
	 * Update A_QueryBase_JSONRoot object For Current Project Search Ids
	 * 
	 * @param a_QueryBase_JSONRoot
	 * @param mapProjectSearchIdToSearchId
	 * @throws Exception
	 */
	public void update__A_QueryBase_JSONRoot__ForCurrentSearchIds( 
			A_QueryBase_JSONRoot a_QueryBase_JSONRoot,
			Map<Integer,Integer> mapProjectSearchIdToSearchId ) throws Exception {
		
		CutoffValuesRootLevel cutoffs = a_QueryBase_JSONRoot.getCutoffs();
//		a_QueryBase_JSONRoot.getAnnTypeIdDisplay();
		
		updateCutoffs(mapProjectSearchIdToSearchId, cutoffs);
		
	}
	
	/**
	 * @param mapProjectSearchIdToSearchId
	 * @param cutoffs
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	private void updateCutoffs( Map<Integer, Integer> mapProjectSearchIdToSearchId, CutoffValuesRootLevel cutoffs )	throws ProxlWebappDataException, Exception {
		
		/**
		 * Key is Project Search Id
		 */
		Map<String,CutoffValuesSearchLevel> cutoffsForSearches = cutoffs.getSearches();

		Set<String> projectSearchIdsKeysInMap = new HashSet<String>( cutoffsForSearches.keySet() );
		
		//  remove entries in cutoffsForSearches not in mapProjectSearchIdToSearchId
		for ( String projectSearchIdString : projectSearchIdsKeysInMap ) {
			Integer projectSearchId = null;
			try {
				projectSearchId = Integer.valueOf( projectSearchIdString );
			} catch ( Exception e ) {
				String msg = "Failed to parse as int key in cutoffs.getSearches(): key (projectSearchIdString): |" 
						+ projectSearchIdString + "|.";
				log.error(msg);
				throw new ProxlWebappDataException(msg);
			}
			if ( ! mapProjectSearchIdToSearchId.containsKey( projectSearchId ) ) {
				// projectSearchId not in current projectSearchIds so remove entry
				cutoffsForSearches.remove( projectSearchIdString );
			}
		}
		
		List<Map.Entry<Integer,Integer>> mapProjectSearchIdToSearchId_Entries_ToGetDefaultValuesFor = new ArrayList<>( mapProjectSearchIdToSearchId.size() );
		
		for ( Map.Entry<Integer,Integer> entry : mapProjectSearchIdToSearchId.entrySet() ) {
			Integer projectSearchId = entry.getKey();
			String projectSearchIdString = projectSearchId.toString();
			if ( ! cutoffsForSearches.containsKey( projectSearchIdString ) ) {
				// projectSearchIdString NOT IN map of cutoffs so add value with default cutoffs
				mapProjectSearchIdToSearchId_Entries_ToGetDefaultValuesFor.add( entry );
			}
		}
		
		if ( ! mapProjectSearchIdToSearchId_Entries_ToGetDefaultValuesFor.isEmpty() ) {
			// Have to get some default cutoffs add to map cutoffsForSearches
			//  Create collections to send to GetDefaultPsmPeptideCutoffs
			Map<Integer,Integer> mapProjectSearchIdToSearchId_Subset = new HashMap<>();
			Collection<Integer> projectSearchIds = new HashSet<>();
			Collection<Integer> searchIds = new HashSet<>();
			for ( Map.Entry<Integer,Integer> entry :  mapProjectSearchIdToSearchId_Entries_ToGetDefaultValuesFor ) {
				mapProjectSearchIdToSearchId_Subset.put( entry.getKey(), entry.getValue() );
				projectSearchIds.add( entry.getKey() );
				searchIds.add( entry.getValue() );
			}
			CutoffValuesRootLevel cutoffValuesRootLevel_defaults = GetDefaultPsmPeptideCutoffs.getInstance().getDefaultPsmPeptideCutoffs( projectSearchIds, searchIds, mapProjectSearchIdToSearchId_Subset );
			// copy cutoffValuesRootLevel_defaults to cutoffs
			Map<String,CutoffValuesSearchLevel> cutoffsForSearches_defaults = cutoffValuesRootLevel_defaults.getSearches();
			for ( Map.Entry<String,CutoffValuesSearchLevel> entryDefaults : cutoffsForSearches_defaults.entrySet() ) {
				cutoffsForSearches.put( entryDefaults.getKey(), entryDefaults.getValue() );
			}
			
		}
	}
}
