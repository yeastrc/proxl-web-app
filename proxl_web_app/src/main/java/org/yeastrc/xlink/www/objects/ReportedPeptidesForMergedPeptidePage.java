package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.dto.ReportedPeptideDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 *
 */
public class ReportedPeptidesForMergedPeptidePage implements SearchPeptideCommonLinkWebserviceResultIF {

	private String searchName;
	
	private int projectSearchId;
	private int searchId;
	
	//  Use WebReportedPeptide
	private WebReportedPeptide webReportedPeptide;
	
	private List<String> peptideAnnotationValues;
	
	private List<String> psmAnnotationValues;
	
	//  Defer to webReportedPeptide
	public ReportedPeptideDTO getReportedPeptide() throws Exception {
		return webReportedPeptide.getReportedPeptide();
	}
	public int getNumPSMs() throws Exception {
		return webReportedPeptide.getNumPsms();
	}

	@JsonIgnore
	public WebReportedPeptide getWebReportedPeptide() {
		return webReportedPeptide;
	}
	public void setWebReportedPeptide(WebReportedPeptide webReportedPeptide) {
		this.webReportedPeptide = webReportedPeptide;
	}

	
	@Override
	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValues;
	}
	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValues = psmAnnotationValueList;
	}
	@Override
	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValues;
	}
	@Override
	public void setPeptideAnnotationValueList(List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValues = peptideAnnotationValueList;
	}
	
	public List<String> getPeptideAnnotationValues() {
		return peptideAnnotationValues;
	}
	public void setPeptideAnnotationValues(List<String> peptideAnnotationValues) {
		this.peptideAnnotationValues = peptideAnnotationValues;
	}
	public List<String> getPsmAnnotationValues() {
		return psmAnnotationValues;
	}
	public void setPsmAnnotationValues(List<String> psmAnnotationValues) {
		this.psmAnnotationValues = psmAnnotationValues;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}


	
}
