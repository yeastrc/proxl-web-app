package org.yeastrc.xlink.www.form_query_json_objects;


/**
 * The Root object for the query for the Search Peptide Page
 *
 */
public class PeptideQueryJSONRoot extends A_QueryBase_JSONRoot {

	
	//  A_QueryBase_JSONRoot contains the PSM and Peptide cutoffs
	
	
	
	private String[] linkTypes;
	
	/**
	 * null when all mods and 'No Mods' selected 
	 */
	private String[] mods;
	
	
	/**
	 * null when all mods and 'No Mods' selected
	 * @return
	 */
	public String[] getMods() {
		return mods;
	}
	/**
	 * null when all mods and 'No Mods' selected
	 * @param mods
	 */
	public void setMods(String[] mods) {
		this.mods = mods;
	}
	
	public String[] getLinkTypes() {
		return linkTypes;
	}
	public void setLinkTypes(String[] linkTypes) {
		this.linkTypes = linkTypes;
	}

	
}
