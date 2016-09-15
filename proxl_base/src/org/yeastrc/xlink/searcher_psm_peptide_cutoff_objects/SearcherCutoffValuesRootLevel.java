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
	 * Key is Search Id
	 */
	private Map<Integer,SearcherCutoffValuesSearchLevel> searchLevelCutoffsPerSearchId = new HashMap<>();
	
	private List<SearcherCutoffValuesSearchLevel> searchLevelCutoffsPerSearchIdList = null;

	
	/**
	 * Get SearcherCutoffValuesSearchLevel object for search id
	 * @param searchId
	 * @return
	 */
	public SearcherCutoffValuesSearchLevel getPerSearchCutoffs( Integer searchId ) {
		return searchLevelCutoffsPerSearchId.get( searchId );
	}

	/**
	 * @param perSearchCutoffs
	 */
	public void addPerSearchCutoffs( SearcherCutoffValuesSearchLevel perSearchCutoffs ) {
		
		this.searchLevelCutoffsPerSearchId.put( perSearchCutoffs.getSearchId(), perSearchCutoffs);
		
		searchLevelCutoffsPerSearchIdList = null;  // ensure cached list is null
	}

	/**
	 * Get Per Search Cutoffs in list form
	 * @return
	 */
	public List<SearcherCutoffValuesSearchLevel> getPerSearchCutoffsList() {
		
		if ( searchLevelCutoffsPerSearchIdList == null ) {
			
			searchLevelCutoffsPerSearchIdList = new ArrayList<>( searchLevelCutoffsPerSearchId.size() );
			
			for ( Map.Entry<Integer,SearcherCutoffValuesSearchLevel> entry : searchLevelCutoffsPerSearchId.entrySet() ) {
				
				searchLevelCutoffsPerSearchIdList.add( entry.getValue() );
			}
			
		}
		
		return searchLevelCutoffsPerSearchIdList;
	}
}
