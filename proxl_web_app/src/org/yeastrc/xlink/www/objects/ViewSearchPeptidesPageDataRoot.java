package org.yeastrc.xlink.www.objects;


import java.util.List;


/**
 * Root object of everything placed on the ViewSearchPeptides page by the JSP
 *
 */
public class ViewSearchPeptidesPageDataRoot {

	//  projectId  not included
	
	private int projectSearchId;
	private int searchId;
	
	private  List<String> modMassFilterList;

	private List<WebReportedPeptide> peptideList;
	private int peptideListSize;
	

	private List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList;

	private List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList;

	private boolean showNumberUniquePSMs;
	
	private String queryJSONToForm;
	
	
//  Maybe need to address these
//	request.setAttribute( "queryString",  request.getQueryString() );
//	request.setAttribute( "mergedQueryString", request.getQueryString().replaceAll( "searchId=", "searchIds=" ) );

	
	public List<WebReportedPeptide> getPeptideList() {
		return peptideList;
	}
	public void setPeptideList(List<WebReportedPeptide> peptideList) {
		this.peptideList = peptideList;
	}
	public int getPeptideListSize() {
		return peptideListSize;
	}
	public void setPeptideListSize(int peptideListSize) {
		this.peptideListSize = peptideListSize;
	}

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
	
	public boolean isShowNumberUniquePSMs() {
		return showNumberUniquePSMs;
	}
	public void setShowNumberUniquePSMs(boolean showNumberUniquePSMs) {
		this.showNumberUniquePSMs = showNumberUniquePSMs;
	}
	public String getQueryJSONToForm() {
		return queryJSONToForm;
	}
	public void setQueryJSONToForm(String queryJSONToForm) {
		this.queryJSONToForm = queryJSONToForm;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public List<String> getModMassFilterList() {
		return modMassFilterList;
	}
	public void setModMassFilterList(List<String> modMassFilterList) {
		this.modMassFilterList = modMassFilterList;
	}
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
	

}
