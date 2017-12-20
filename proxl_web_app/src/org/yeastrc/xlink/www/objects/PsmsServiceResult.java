package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * Result object from PsmsService
 *
 */
public class PsmsServiceResult {

	private boolean searchHasScanData; //  From search record 
	
	private boolean searchHasPsmPerPeptideAnnTypes;
	
	private List<AnnotationDisplayNameDescription> annotationDisplayNameDescriptionList;
	
	private List<PsmWebDisplayWebServiceResult> psmWebDisplayList;
	
	
	public List<AnnotationDisplayNameDescription> getAnnotationDisplayNameDescriptionList() {
		return annotationDisplayNameDescriptionList;
	}
	public void setAnnotationDisplayNameDescriptionList(
			List<AnnotationDisplayNameDescription> annotationDisplayNameDescriptionList) {
		this.annotationDisplayNameDescriptionList = annotationDisplayNameDescriptionList;
	}
	public List<PsmWebDisplayWebServiceResult> getPsmWebDisplayList() {
		return psmWebDisplayList;
	}
	public void setPsmWebDisplayList(List<PsmWebDisplayWebServiceResult> psmWebDisplayList) {
		this.psmWebDisplayList = psmWebDisplayList;
	}
	public boolean isSearchHasScanData() {
		return searchHasScanData;
	}
	public void setSearchHasScanData(boolean searchHasScanData) {
		this.searchHasScanData = searchHasScanData;
	}
	public boolean isSearchHasPsmPerPeptideAnnTypes() {
		return searchHasPsmPerPeptideAnnTypes;
	}
	public void setSearchHasPsmPerPeptideAnnTypes(boolean searchHasPsmPerPeptideAnnTypes) {
		this.searchHasPsmPerPeptideAnnTypes = searchHasPsmPerPeptideAnnTypes;
	}
}
