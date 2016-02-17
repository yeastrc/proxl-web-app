package org.yeastrc.xlink.www.objects;


import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

/**
 * Wrapper for SearchPeptideMonolink that includes Annotation data 
 *
 * Supports sorting and populating annotation display values
 * 
 */
public class SearchPeptideMonolinkAnnDataWrapper extends SortDisplayRecordsWrapperBase {

	
	/**
	 * Wrapped display data
	 */
	private SearchPeptideMonolink searchPeptideMonolink;

	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@Override
	public int getFinalSortOrderKey() {
		
		return searchPeptideMonolink.getReportedPeptide().getId();
	}
	

	@Override
	public List<String> getPsmAnnotationValueList() {

		return searchPeptideMonolink.getPsmAnnotationValueList();
	}

	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {

		searchPeptideMonolink.setPsmAnnotationValueList( psmAnnotationValueList );
		
	}

	@Override
	public List<String> getPeptideAnnotationValueList() {

		return searchPeptideMonolink.getPeptideAnnotationValueList();
	}

	@Override
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {

		searchPeptideMonolink.setPeptideAnnotationValueList( peptideAnnotationValueList );
	}
	
		
	/////////////////
	
	//   Getters and Setters
	

	
	public SearchPeptideMonolink getSearchPeptideMonolink() {
		return searchPeptideMonolink;
	}

	public void setSearchPeptideMonolink(SearchPeptideMonolink searchPeptideMonolink) {
		this.searchPeptideMonolink = searchPeptideMonolink;
	}


	
	
}
