package org.yeastrc.xlink.www.objects;


import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

/**
 * Wrapper for SearchPeptideNoLinkInfo that includes Annotation data 
 *
 * Supports sorting 
 * 
 * No Link info (crosslink, etc) so display Reported Peptide for Protein All Page
 * 
 */
public class SearchPeptideNoLinkInfoAnnDataWrapper extends SortDisplayRecordsWrapperBase implements SearchPeptideCommonLinkAnnDataWrapperIF {

	/**
	 * Wrapped display data
	 */
	private SearchPeptideNoLinkInfo searchPeptideNoLinkInfo;

	/**
	 * 
	 * @return
	 */
	@Override
	public int getReportedPeptideId() {
		
		return searchPeptideNoLinkInfo.getReportedPeptideId();
	}
	
	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@Override
	public int getFinalSortOrderKey() {
		
		return searchPeptideNoLinkInfo.getReportedPeptideId();
	}
	
	/////////////////
	//   Getters and Setters

	@Override
	public List<String> getPsmAnnotationValueList() {

		return null;
	}

	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getPeptideAnnotationValueList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		// TODO Auto-generated method stub
	}

	public SearchPeptideNoLinkInfo getSearchPeptideNoLinkInfo() {
		return searchPeptideNoLinkInfo;
	}

	public void setSearchPeptideNoLinkInfo(SearchPeptideNoLinkInfo searchPeptideNoLinkInfo) {
		this.searchPeptideNoLinkInfo = searchPeptideNoLinkInfo;
	}

}
