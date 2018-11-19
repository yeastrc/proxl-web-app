package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * 
 * Result object from ReportedPeptides_For_AllProteins_Service  for All Proteins page
 */
public class GetNoLinkInfoReportedPeptidesServiceResult {

	private List<SearchPeptideNoLinkInfoWebserviceResult> searchPeptideNoLinkInfoList;



	private List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList;
	
	private List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList;
	

	public List<AnnotationDisplayNameDescription> getPeptideAnnotationDisplayNameDescriptionList() {
		return peptideAnnotationDisplayNameDescriptionList;
	}

	public void setPeptideAnnotationDisplayNameDescriptionList(
			List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList) {
		this.peptideAnnotationDisplayNameDescriptionList = peptideAnnotationDisplayNameDescriptionList;
	}

	public List<AnnotationDisplayNameDescription> getPsmAnnotationDisplayNameDescriptionList() {
		return psmAnnotationDisplayNameDescriptionList;
	}

	public void setPsmAnnotationDisplayNameDescriptionList(
			List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList) {
		this.psmAnnotationDisplayNameDescriptionList = psmAnnotationDisplayNameDescriptionList;
	}

	public List<SearchPeptideNoLinkInfoWebserviceResult> getSearchPeptideNoLinkInfoList() {
		return searchPeptideNoLinkInfoList;
	}

	public void setSearchPeptideNoLinkInfoList(
			List<SearchPeptideNoLinkInfoWebserviceResult> searchPeptideNoLinkInfoList) {
		this.searchPeptideNoLinkInfoList = searchPeptideNoLinkInfoList;
	}


}
