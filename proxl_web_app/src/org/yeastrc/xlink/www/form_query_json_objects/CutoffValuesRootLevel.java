package org.yeastrc.xlink.www.form_query_json_objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Root level of Cutoff Values
 *
 */
public class CutoffValuesRootLevel {
	/**
	 * Key is Project Search Id
	 */
	private Map<String,CutoffValuesSearchLevel> searches;
	/**
	 * Key is Project Search Id
	 * 
	 * @return
	 */
	public Map<String, CutoffValuesSearchLevel> getSearches() {
		if ( searches == null ) {
			searches = new HashMap<>();
		}
		return searches;
	}
	/**
	 * Key is Project Search Id
	 * 
	 * @param searches
	 */
	public void setSearches(Map<String, CutoffValuesSearchLevel> searches) {
		this.searches = searches;
	}
}
