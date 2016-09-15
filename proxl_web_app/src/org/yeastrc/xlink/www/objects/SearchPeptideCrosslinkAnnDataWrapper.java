package org.yeastrc.xlink.www.objects;


import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

/**
 * Wrapper for SearchPeptideCrosslink that includes Annotation data 
 *
 * Supports sorting 
 * 
 */
public class SearchPeptideCrosslinkAnnDataWrapper extends SortDisplayRecordsWrapperBase {

	/**
	 * Wrapped display data
	 */
	private SearchPeptideCrosslink searchPeptideCrosslink;

	
	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@Override
	public int getFinalSortOrderKey() {
		
		return searchPeptideCrosslink.getReportedPeptideId();
	}
	

	
	/////////////////
	
	//   Getters and Setters
	

	public SearchPeptideCrosslink getSearchPeptideCrosslink() {
		return searchPeptideCrosslink;
	}

	public void setSearchPeptideCrosslink(
			SearchPeptideCrosslink searchPeptideCrosslink) {
		this.searchPeptideCrosslink = searchPeptideCrosslink;
	}

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

	
	
}
