package org.yeastrc.xlink.www.forms;

import org.apache.struts.action.ActionForm;

/**
 * Common for MergedSearchViewProteinsForm and SearchViewProteinsForm
 *
 */
public class PeptideProteinCommonForm  extends ActionForm {

	private static final long serialVersionUID = 1L;

	private int[] projectSearchId = { };
	
	/**
	 * JSON with the rest of the parameters
	 */
	private String queryJSON;
	
	/**
	 * Assumes there is only one element in projectSearchId array
	 * @return first element in projectSearchId, or 0 if projectSearchId is null or empty
	 */
	public int getProjectSearchIdSingle() {
		if ( this.projectSearchId == null || this.projectSearchId.length == 0 ) {
			return 0;
		}
		return this.projectSearchId[ 0 ];
	}

	//  OLD Setters and Getters to support legacy URLs with "searchId" and "searchIds"
	
	//  Add to handle requests with "searchId" in the query string
	public void setSearchId(int searchId) {
		this.projectSearchId = new int[ 1 ];
		this.projectSearchId[ 0 ] = searchId;
	}
	//  Add to handle requests with "searchId" in the query string or Form
	public int getSearchId() {
		if ( this.projectSearchId == null || this.projectSearchId.length == 0 ) {
			return 0;
		}
		return this.projectSearchId[ 0 ];
	}
	//  Removed since not needed
//	public int[] getSearchIds() {
//		return projectSearchId;
//	}
	//  Add to handle requests with "searchIds" in the query string or Form
	public void setSearchIds(int[] searchIds) {
		this.projectSearchId = searchIds;
	}
	
	//  Setters and Getters

	public String getQueryJSON() {
		return queryJSON;
	}
	public void setQueryJSON(String queryJSON) {
		this.queryJSON = queryJSON;
	}
	public int[] getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int[] projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
	
}
