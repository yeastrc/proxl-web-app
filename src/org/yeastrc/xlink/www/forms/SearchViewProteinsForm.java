package org.yeastrc.xlink.www.forms;

import org.apache.struts.action.ActionForm;

public class SearchViewProteinsForm extends ActionForm {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	public String getProject_id() {
		return project_id;
	}
	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}


	
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




	private double psmQValueCutoff = 0.01;
	private double peptideQValueCutoff = 0.01;
	private int[] excludeTaxonomy = {};
	private boolean filterNonUniquePeptides = false;
	private boolean filterOnlyOnePSM = false;
	private boolean filterOnlyOnePeptide = false;



	private int searchId;
	private String project_id;	
	

	private int[] excludeProtein = {};
}
