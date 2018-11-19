package org.yeastrc.xlink.www.annotation_display;

import java.util.Map;


/**
 * The root object for annotation type ids for what annotations to display
 *
 */
public class AnnTypeIdDisplayJSONRoot {

	/**
	 * Key is Search Id
	 */
	private Map<String,AnnTypeIdDisplayJSON_PerSearch> searches;

	/**
	 * Key is Search Id
	 * @return
	 */
	public Map<String, AnnTypeIdDisplayJSON_PerSearch> getSearches() {
		return searches;
	}

	/**
	 * Key is Search Id
	 * @param searches
	 */
	public void setSearches(Map<String, AnnTypeIdDisplayJSON_PerSearch> searches) {
		this.searches = searches;
	}

	
}

