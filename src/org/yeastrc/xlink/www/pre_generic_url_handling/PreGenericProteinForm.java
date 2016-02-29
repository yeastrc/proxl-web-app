package org.yeastrc.xlink.www.pre_generic_url_handling;

import org.apache.struts.action.ActionForm;

/**
 * This form is for processing old URLs before change to Generic
 *
 */
public class PreGenericProteinForm extends ActionForm {

	private static final long serialVersionUID = 1L;


	

	private double psmQValueCutoff = 0.01;
	private double peptideQValueCutoff = 0.01;
	private int[] excludeTaxonomy = {};
	private boolean filterNonUniquePeptides = false;
	private boolean filterOnlyOnePSM = false;
	private boolean filterOnlyOnePeptide = false;



	private int[] searchIds = { };
	private int[] excludeProtein = {};
	

	//  Support shortened labels of "excP" to shorten the URL
	
	public int[] getExcP() {
		return excludeProtein;
	}
	public void setExcP(int[] excludeProtein) {
		this.excludeProtein = excludeProtein;
	}

	
	public int[] getExcludeProtein() {
		return excludeProtein;
	}
	public void setExcludeProtein(int[] excludeProtein) {
		this.excludeProtein = excludeProtein;
	}


	//  Add to handle requests with "searchId" in the query string

	public void setSearchId(int searchId) {
		
		this.searchIds = new int[ 1 ];
		
		this.searchIds[ 0 ] = searchId;
		
	}

	//  Add to handle requests with "searchId" in the query string

	public int getSearchId() {
		
		if ( this.searchIds == null || this.searchIds.length == 0 ) {
		
			return 0;
		}
		
		return this.searchIds[ 0 ];
	}
	


	public double getPsmQValueCutoff() {
		return psmQValueCutoff;
	}
	public void setPsmQValueCutoff(double psmQValueCutoff) {
		this.psmQValueCutoff = psmQValueCutoff;
	}
	public double getPeptideQValueCutoff() {
		return peptideQValueCutoff;
	}
	public void setPeptideQValueCutoff(double peptideQValueCutoff) {
		this.peptideQValueCutoff = peptideQValueCutoff;
	}
	public int[] getExcludeTaxonomy() {
		return excludeTaxonomy;
	}
	public void setExcludeTaxonomy(int[] excludeTaxonomy) {
		this.excludeTaxonomy = excludeTaxonomy;
	}
	public boolean isFilterNonUniquePeptides() {
		return filterNonUniquePeptides;
	}
	public void setFilterNonUniquePeptides(boolean filterNonUniquePeptides) {
		this.filterNonUniquePeptides = filterNonUniquePeptides;
	}
	public boolean isFilterOnlyOnePSM() {
		return filterOnlyOnePSM;
	}
	public void setFilterOnlyOnePSM(boolean filterOnlyOnePSM) {
		this.filterOnlyOnePSM = filterOnlyOnePSM;
	}
	public boolean isFilterOnlyOnePeptide() {
		return filterOnlyOnePeptide;
	}
	public void setFilterOnlyOnePeptide(boolean filterOnlyOnePeptide) {
		this.filterOnlyOnePeptide = filterOnlyOnePeptide;
	}
	public int[] getSearchIds() {
		return searchIds;
	}
	public void setSearchIds(int[] searchIds) {
		this.searchIds = searchIds;
	}
}
