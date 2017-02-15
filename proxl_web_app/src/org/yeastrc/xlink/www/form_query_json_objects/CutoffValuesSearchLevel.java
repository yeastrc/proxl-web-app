package org.yeastrc.xlink.www.form_query_json_objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Entry per Search
 *
 */
public class CutoffValuesSearchLevel {

	private int projectSearchId;

	private Map<String,CutoffValuesAnnotationLevel> psmCutoffValues;
	private Map<String,CutoffValuesAnnotationLevel> peptideCutoffValues;
	
	public Map<String, CutoffValuesAnnotationLevel> getPsmCutoffValues() {
		if ( psmCutoffValues == null ) {
			psmCutoffValues = new HashMap<>();
		}
		return psmCutoffValues;
	}
	public Map<String, CutoffValuesAnnotationLevel> getPeptideCutoffValues() {
		if ( peptideCutoffValues == null ) {
			peptideCutoffValues = new HashMap<>();
		}
		return peptideCutoffValues;
	}

	// added for backwards compatibility  public void setSearchId(int searchId) {
	/**
	 * This method supports old URLs with 'searchId=###'
	 * Actually projectSearchId
	 * @param searchId
	 */
	public void setSearchId(int searchId) {
		this.projectSearchId = searchId;
	}

	//  Not needed
//	public int getSearchId() {
//		return projectSearchId;
//	}
	
	
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
	
	
	public void setPsmCutoffValues(
			Map<String, CutoffValuesAnnotationLevel> psmCutoffValues) {
		this.psmCutoffValues = psmCutoffValues;
	}
	public void setPeptideCutoffValues(
			Map<String, CutoffValuesAnnotationLevel> peptideCutoffValues) {
		this.peptideCutoffValues = peptideCutoffValues;
	}
	

}
