package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

public class SearchProteinDimerWrapper extends SortDisplayRecordsWrapperBase {

	private SearchProteinDimer searchProteinDimer;

	



	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@Override
	public int getFinalSortOrderKey() {

		return searchProteinDimer.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
	}
	
	
	/////////////////
	
	//   Getters and Setters for access to wrapped object
	
	@Override
	public List<String> getPsmAnnotationValueList() {
		return searchProteinDimer.getPsmAnnotationValueList();
	}

	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		searchProteinDimer.setPsmAnnotationValueList( psmAnnotationValueList );
	}

	@Override
	public List<String> getPeptideAnnotationValueList() {
		return searchProteinDimer.getPeptideAnnotationValueList();
	}

	@Override
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		searchProteinDimer.setPeptideAnnotationValueList( peptideAnnotationValueList );
	}



	
	/////////////////
	
	//   Getters and Setters
	

	public SearchProteinDimer getSearchProteinDimer() {
		return searchProteinDimer;
	}

	public void setSearchProteinDimer(
			SearchProteinDimer searchProteinDimer) {
		this.searchProteinDimer = searchProteinDimer;
	}

	
	
}
