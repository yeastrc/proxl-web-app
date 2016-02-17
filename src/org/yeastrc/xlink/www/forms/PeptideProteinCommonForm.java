package org.yeastrc.xlink.www.forms;

import org.apache.struts.action.ActionForm;

/**
 * Common for MergedSearchViewProteinsForm and SearchViewProteinsForm
 *
 */
public class PeptideProteinCommonForm  extends ActionForm {

	private static final long serialVersionUID = 1L;

	private int[] searchIds = { };
	
	/**
	 * JSON with the rest of the parameters
	 */
	private String queryJSON;
	
	
	public int[] getSearchIds() {
		return searchIds;
	}

	public void setSearchIds(int[] searchIds) {
		this.searchIds = searchIds;
	}

	public String getQueryJSON() {
		return queryJSON;
	}

	public void setQueryJSON(String queryJSON) {
		this.queryJSON = queryJSON;
	}


	

	//  Add to handle requests with "searchId" in the query string

	public void setSearchId(int searchId) {
		
		this.searchIds = new int[ 1 ];
		
		this.searchIds[ 0 ] = searchId;
		
		int z = 0;
	}

	//  Add to handle requests with "searchId" in the query string

	public int getSearchId() {
		
		if ( this.searchIds == null || this.searchIds.length == 0 ) {
		
			return 0;
		}
		
		return this.searchIds[ 0 ];
	}
	
	
	
}
