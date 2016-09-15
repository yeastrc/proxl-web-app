package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * 
 * Result object from ReportedPeptidesService for Monolinks
 */
public class GetMonolinkReportedPeptidesServiceResult {

	private List<SearchPeptideMonolinkWebserviceResult> searchPeptideMonolinkList;



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

	public List<SearchPeptideMonolinkWebserviceResult> getSearchPeptideMonolinkList() {
		return searchPeptideMonolinkList;
	}

	public void setSearchPeptideMonolinkList(
			List<SearchPeptideMonolinkWebserviceResult> searchPeptideMonolinkList) {
		this.searchPeptideMonolinkList = searchPeptideMonolinkList;
	}


}
