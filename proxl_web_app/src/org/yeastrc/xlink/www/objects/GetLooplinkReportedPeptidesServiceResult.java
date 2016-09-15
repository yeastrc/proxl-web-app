package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * 
 * Result object from ReportedPeptidesService for Looplinks
 */
public class GetLooplinkReportedPeptidesServiceResult {

	private List<SearchPeptideLooplinkWebserviceResult> searchPeptideLooplinkList;



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

	public List<SearchPeptideLooplinkWebserviceResult> getSearchPeptideLooplinkList() {
		return searchPeptideLooplinkList;
	}

	public void setSearchPeptideLooplinkList(
			List<SearchPeptideLooplinkWebserviceResult> searchPeptideLooplinkList) {
		this.searchPeptideLooplinkList = searchPeptideLooplinkList;
	}


}
