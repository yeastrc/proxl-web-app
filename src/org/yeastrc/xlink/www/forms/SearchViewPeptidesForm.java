package org.yeastrc.xlink.www.forms;

import org.apache.struts.action.ActionForm;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;

public class SearchViewPeptidesForm extends ActionForm {
	
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
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public String[] getLinkType() {
		return linkType;
	}
	public void setLinkType(String[] linkType) {
		this.linkType = linkType;
	}
	public String getProject_id() {
		return project_id;
	}
	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}
	public String[] getModMassFilter() {
		return modMassFilter;
	}
	public void setModMassFilter(String[] modMassFilter) {
		this.modMassFilter = modMassFilter;
	}

	


	private double psmQValueCutoff = 0.01;
	private double peptideQValueCutoff = 0.01;
	private int searchId;
	private String project_id;	


	private String[] modMassFilter = null;


	private String[] linkType = { PeptideViewLinkTypesConstants.CROSSLINK_PSM };

}
