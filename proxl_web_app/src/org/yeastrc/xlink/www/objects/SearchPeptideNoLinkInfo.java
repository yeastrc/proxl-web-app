package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * No Link info (crosslink, etc) so display Reported Peptide for Protein All Page
 * 
 */
public class SearchPeptideNoLinkInfo {

	public WebReportedPeptide getWebReportedPeptide() {
		return webReportedPeptide;
	}

	public void setWebReportedPeptide(WebReportedPeptide webReportedPeptide) {
		this.webReportedPeptide = webReportedPeptide;
	}

	public int getReportedPeptideId() {
		return webReportedPeptide.getReportedPeptideId();
	}

	public ReportedPeptideDTO getReportedPeptide() throws Exception {
		return webReportedPeptide.getReportedPeptide();
	}

	public int getNumPsms() throws Exception {
		return webReportedPeptide.getNumPsms();
	}

	/**
	 * @return null when no scan data for search
	 * @throws Exception
	 */
	public Integer getNumNonUniquePsms() throws Exception {
		if ( ! searchHasScanData ) {
			return null;
		}
		return webReportedPeptide.getNumNonUniquePsms();
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

	public String getLinkType() {
		return linkType;
	}
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public void setSearchHasScanData(boolean searchHasScanData) {
		this.searchHasScanData = searchHasScanData;
	}

	
	private WebReportedPeptide webReportedPeptide;
	
	private String linkType;
	
	private boolean searchHasScanData;
	
	/**
	 * Used for display on web page
	 */
	private List<String> psmAnnotationValueList;
	/**
	 * Used for display on web page
	 */
	private List<String> peptideAnnotationValueList;


}
