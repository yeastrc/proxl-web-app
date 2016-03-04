package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * Result object from ReportedPeptidesRelatedToPSMService
 *
 */
public class ReportedPeptidesRelatedToPSMServiceResult {

	

	private List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperList;

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


	public List<WebReportedPeptideWebserviceWrapper> getWebReportedPeptideWebserviceWrapperList() {
		return webReportedPeptideWebserviceWrapperList;
	}

	public void setWebReportedPeptideWebserviceWrapperList(
			List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperList) {
		this.webReportedPeptideWebserviceWrapperList = webReportedPeptideWebserviceWrapperList;
	} 
	
	
}
