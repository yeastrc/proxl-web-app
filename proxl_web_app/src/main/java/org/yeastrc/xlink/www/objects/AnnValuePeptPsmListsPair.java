package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * Pair of lists for Peptide and PSM values
 *
 */
public class AnnValuePeptPsmListsPair {
	
	private int searchId;

	private List<String> psmAnnotationValueList;
	private List<String> peptideAnnotationValueList;
	
	
	
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValueList;
	}
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValueList = psmAnnotationValueList;
	}
	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValueList;
	}
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValueList = peptideAnnotationValueList;
	}

}
