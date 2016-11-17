package org.yeastrc.xlink.www.objects;


import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

/**
 * Wrapper for SearchPeptideLooplink that includes Annotation data 
 *
 * Supports sorting and populating annotation display values
 * 
 */
public class SearchPeptideLooplinkAnnDataWrapper extends SortDisplayRecordsWrapperBase implements SearchPeptideCommonLinkAnnDataWrapperIF {

	
	/**
	 * Wrapped display data
	 */
	private SearchPeptideLooplink searchPeptideLooplink;


	/**
	 * 
	 * @return
	 */
	@Override
	public int getReportedPeptideId() {
		
		return searchPeptideLooplink.getReportedPeptideId();
	}
	

	@Override
	public int getFinalSortOrderKey() {
		
		return searchPeptideLooplink.getReportedPeptideId();
	}
	

	@Override
	public List<String> getPsmAnnotationValueList() {

		return searchPeptideLooplink.getPsmAnnotationValueList();
	}

	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {

		searchPeptideLooplink.setPsmAnnotationValueList( psmAnnotationValueList );
		
	}

	@Override
	public List<String> getPeptideAnnotationValueList() {

		return searchPeptideLooplink.getPeptideAnnotationValueList();
	}

	@Override
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {

		searchPeptideLooplink.setPeptideAnnotationValueList( peptideAnnotationValueList );
	}
	
	
	
	/////////////////
	
	//   Getters and Setters
	

	
	public SearchPeptideLooplink getSearchPeptideLooplink() {
		return searchPeptideLooplink;
	}

	public void setSearchPeptideLooplink(SearchPeptideLooplink searchPeptideLooplink) {
		this.searchPeptideLooplink = searchPeptideLooplink;
	}

	
}
