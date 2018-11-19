package org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Root level of Cutoff Values
 *
 */
public class SearcherCutoffValuesRootLevel {

	/**
	 * Key is Project Search Id
	 */
	private Map<Integer,SearcherCutoffValuesSearchLevel> searchLevelCutoffsPerProjectSearchId = new HashMap<>();
	
	private List<SearcherCutoffValuesSearchLevel> searchLevelCutoffsPerProjectSearchIdList = null;

	
	/**
	 * Get SearcherCutoffValuesSearchLevel object for projectSearchId
	 * @param searchId
	 * @return
	 */
	public SearcherCutoffValuesSearchLevel getPerSearchCutoffs( Integer projectSearchId ) {
		return searchLevelCutoffsPerProjectSearchId.get( projectSearchId );
	}

	/**
	 * @param perSearchCutoffs
	 */
	public void addPerSearchCutoffs( SearcherCutoffValuesSearchLevel perSearchCutoffs ) {
		
		this.searchLevelCutoffsPerProjectSearchId.put( perSearchCutoffs.getProjectSearchId(), perSearchCutoffs);
		
		searchLevelCutoffsPerProjectSearchIdList = null;  // ensure cached list is null
	}

	/**
	 * Get Per Search Cutoffs in list form
	 * @return
	 */
	public List<SearcherCutoffValuesSearchLevel> getPerSearchCutoffsList() {
		
		if ( searchLevelCutoffsPerProjectSearchIdList == null ) {
			
			searchLevelCutoffsPerProjectSearchIdList = new ArrayList<>( searchLevelCutoffsPerProjectSearchId.size() );
			
			for ( Map.Entry<Integer,SearcherCutoffValuesSearchLevel> entry : searchLevelCutoffsPerProjectSearchId.entrySet() ) {
				
				searchLevelCutoffsPerProjectSearchIdList.add( entry.getValue() );
			}
			
		}
		
		return searchLevelCutoffsPerProjectSearchIdList;
	}
}
