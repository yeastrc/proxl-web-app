package org.yeastrc.xlink.www.forms;

import org.apache.struts.action.ActionForm;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;

public class MergedSearchViewPeptidesForm extends ActionForm {
	
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
	public int[] getSearchIds() {
		return searchIds;
	}
	public void setSearchIds(int[] searchIds) {
		this.searchIds = searchIds;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	private int[] searchIds = {};
	private String project_id;
	
	private String[] modMassFilter = null;



	private String[] linkType = { PeptideViewLinkTypesConstants.CROSSLINK_PSM };


	
}
