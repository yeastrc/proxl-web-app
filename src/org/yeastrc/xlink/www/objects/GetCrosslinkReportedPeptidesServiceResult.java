package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * 
 * Result object from ReportedPeptidesService for Crosslinks
 */
public class GetCrosslinkReportedPeptidesServiceResult {


	private List<SearchPeptideCrosslinkWebserviceResult> searchPeptideCrosslinkList;


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

	public List<SearchPeptideCrosslinkWebserviceResult> getSearchPeptideCrosslinkList() {
		return searchPeptideCrosslinkList;
	}

	public void setSearchPeptideCrosslinkList(
			List<SearchPeptideCrosslinkWebserviceResult> searchPeptideCrosslinkList) {
		this.searchPeptideCrosslinkList = searchPeptideCrosslinkList;
	}

}
