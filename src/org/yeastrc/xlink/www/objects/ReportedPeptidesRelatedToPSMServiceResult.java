package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * Result object from ReportedPeptidesRelatedToPSMService
 *
 */
public class ReportedPeptidesRelatedToPSMServiceResult {

	
	private List<AnnotationDisplayNameDescription> annotationDisplayNameDescriptionList;
	
	private List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperList;

	
	
	public List<AnnotationDisplayNameDescription> getAnnotationDisplayNameDescriptionList() {
		return annotationDisplayNameDescriptionList;
	}

	public void setAnnotationDisplayNameDescriptionList(
			List<AnnotationDisplayNameDescription> annotationDisplayNameDescriptionList) {
		this.annotationDisplayNameDescriptionList = annotationDisplayNameDescriptionList;
	}

	public List<WebReportedPeptideWebserviceWrapper> getWebReportedPeptideWebserviceWrapperList() {
		return webReportedPeptideWebserviceWrapperList;
	}

	public void setWebReportedPeptideWebserviceWrapperList(
			List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperList) {
		this.webReportedPeptideWebserviceWrapperList = webReportedPeptideWebserviceWrapperList;
	} 
	
	
}
