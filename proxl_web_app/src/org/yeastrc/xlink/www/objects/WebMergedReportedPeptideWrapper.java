package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.yeastrc.xlink.www.dto.SearchDTO;

public class WebMergedReportedPeptideWrapper {
	

	private int unifiedReportedPeptideId = -999;


	private WebMergedReportedPeptide webMergedReportedPeptide;
	
	private Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperMapOnSearchId;

	private Collection<SearchDTO> searches = new ArrayList<>();
	
	

	public Collection<SearchDTO> getSearches() {
		return searches;
	}

	public void setSearches(Collection<SearchDTO> searches) {
		this.searches = searches;
	}

	public int getUnifiedReportedPeptideId() {
		return unifiedReportedPeptideId;
	}

	public void setUnifiedReportedPeptideId(int unifiedReportedPeptideId) {
		this.unifiedReportedPeptideId = unifiedReportedPeptideId;
	}

	public Map<Integer, WebReportedPeptideWrapper> getWebReportedPeptideWrapperMapOnSearchId() {
		return webReportedPeptideWrapperMapOnSearchId;
	}

	public void setWebReportedPeptideWrapperMapOnSearchId(
			Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperMapOnSearchId) {
		this.webReportedPeptideWrapperMapOnSearchId = webReportedPeptideWrapperMapOnSearchId;
	}

	public WebMergedReportedPeptide getWebMergedReportedPeptide() {
		return webMergedReportedPeptide;
	}

	public void setWebMergedReportedPeptide(
			WebMergedReportedPeptide webMergedReportedPeptide) {
		this.webMergedReportedPeptide = webMergedReportedPeptide;
	}
	
	
}
