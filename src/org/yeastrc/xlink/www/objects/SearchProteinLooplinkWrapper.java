package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

public class SearchProteinLooplinkWrapper extends SortDisplayRecordsWrapperBase {

	private SearchProteinLooplink searchProteinLooplink;



	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@Override
	public int getFinalSortOrderKey() {

		return searchProteinLooplink.getProtein().getNrProtein().getNrseqId();
	}

	/////////////////
	
	//   Getters and Setters for access to wrapped object
	

	public List<String> getPsmAnnotationValueList() {
		return searchProteinLooplink.getPsmAnnotationValueList();
	}


	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		searchProteinLooplink.setPsmAnnotationValueList( psmAnnotationValueList );
	}

	public List<String> getPeptideAnnotationValueList() {
		return searchProteinLooplink.getPeptideAnnotationValueList();
	}


	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		searchProteinLooplink.setPeptideAnnotationValueList( peptideAnnotationValueList );
	}



	
	/////////////////
	
	//   Getters and Setters
	

	public SearchProteinLooplink getSearchProteinLooplink() {
		return searchProteinLooplink;
	}

	public void setSearchProteinLooplink(
			SearchProteinLooplink searchProteinLooplink) {
		this.searchProteinLooplink = searchProteinLooplink;
	}
	
	
	
	
}
