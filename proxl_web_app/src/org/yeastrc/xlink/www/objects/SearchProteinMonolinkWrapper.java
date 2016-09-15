package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

public class SearchProteinMonolinkWrapper extends SortDisplayRecordsWrapperBase {

	private SearchProteinMonolink searchProteinMonolink;



	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@Override
	public int getFinalSortOrderKey() {

		return searchProteinMonolink.getProtein().getProteinSequenceObject().getProteinSequenceId();
	}

	/////////////////
	
	//   Getters and Setters for access to wrapped object
	

	public List<String> getPsmAnnotationValueList() {
		return searchProteinMonolink.getPsmAnnotationValueList();
	}


	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		searchProteinMonolink.setPsmAnnotationValueList( psmAnnotationValueList );
	}

	public List<String> getPeptideAnnotationValueList() {
		return searchProteinMonolink.getPeptideAnnotationValueList();
	}


	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		searchProteinMonolink.setPeptideAnnotationValueList( peptideAnnotationValueList );
	}



	
	/////////////////
	
	//   Getters and Setters
	

	public SearchProteinMonolink getSearchProteinMonolink() {
		return searchProteinMonolink;
	}

	public void setSearchProteinMonolink(
			SearchProteinMonolink searchProteinMonolink) {
		this.searchProteinMonolink = searchProteinMonolink;
	}
	
	
	
	
}
