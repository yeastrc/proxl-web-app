package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

public class SearchProteinCrosslinkWrapper extends SortDisplayRecordsWrapperBase {

	private SearchProteinCrosslink searchProteinCrosslink;

	



	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@Override
	public int getFinalSortOrderKey() {

		return searchProteinCrosslink.getProtein1().getNrProtein().getNrseqId();
	}
	
	
	/////////////////
	
	//   Getters and Setters for access to wrapped object
	
	@Override
	public List<String> getPsmAnnotationValueList() {
		return searchProteinCrosslink.getPsmAnnotationValueList();
	}

	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		searchProteinCrosslink.setPsmAnnotationValueList( psmAnnotationValueList );
	}

	@Override
	public List<String> getPeptideAnnotationValueList() {
		return searchProteinCrosslink.getPeptideAnnotationValueList();
	}

	@Override
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		searchProteinCrosslink.setPeptideAnnotationValueList( peptideAnnotationValueList );
	}



	
	/////////////////
	
	//   Getters and Setters
	

	public SearchProteinCrosslink getSearchProteinCrosslink() {
		return searchProteinCrosslink;
	}

	public void setSearchProteinCrosslink(
			SearchProteinCrosslink searchProteinCrosslink) {
		this.searchProteinCrosslink = searchProteinCrosslink;
	}

	
	
}
