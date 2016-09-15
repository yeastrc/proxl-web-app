package org.yeastrc.xlink.www.pre_generic_url_handling;

import org.apache.struts.action.ActionForm;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;

/**
 * This form is for processing old URLs before change to Generic
 *
 */
public class PreGenericPeptideForm extends ActionForm {

	private static final long serialVersionUID = 1L;

	
	private double psmQValueCutoff = 0.01;
	private double peptideQValueCutoff = 0.01;
	private int[] searchIds = {};
	
	private String[] modMassFilter = null;



	private String[] linkType = { PeptideViewLinkTypesConstants.CROSSLINK_PSM };
	
	

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

	public int[] getSearchIds() {
		return searchIds;
	}

	public void setSearchIds(int[] searchIds) {
		this.searchIds = searchIds;
	}

	public String[] getModMassFilter() {
		return modMassFilter;
	}

	public void setModMassFilter(String[] modMassFilter) {
		this.modMassFilter = modMassFilter;
	}

	public String[] getLinkType() {
		return linkType;
	}

	public void setLinkType(String[] linkType) {
		this.linkType = linkType;
	}


}
