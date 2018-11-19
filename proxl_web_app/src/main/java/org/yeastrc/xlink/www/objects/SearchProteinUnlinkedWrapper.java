package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

public class SearchProteinUnlinkedWrapper extends SortDisplayRecordsWrapperBase {

	private SearchProteinUnlinked searchProteinUnlinked;



	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@Override
	public int getFinalSortOrderKey() {

		return searchProteinUnlinked.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
	}

	/////////////////
	
	//   Getters and Setters for access to wrapped object
	

	public List<String> getPsmAnnotationValueList() {
		return searchProteinUnlinked.getPsmAnnotationValueList();
	}


	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		searchProteinUnlinked.setPsmAnnotationValueList( psmAnnotationValueList );
	}

	public List<String> getPeptideAnnotationValueList() {
		return searchProteinUnlinked.getPeptideAnnotationValueList();
	}


	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		searchProteinUnlinked.setPeptideAnnotationValueList( peptideAnnotationValueList );
	}



	
	/////////////////
	
	//   Getters and Setters
	

	public SearchProteinUnlinked getSearchProteinUnlinked() {
		return searchProteinUnlinked;
	}

	public void setSearchProteinUnlinked(
			SearchProteinUnlinked searchProteinUnlinked) {
		this.searchProteinUnlinked = searchProteinUnlinked;
	}
	
	
	
	
}
