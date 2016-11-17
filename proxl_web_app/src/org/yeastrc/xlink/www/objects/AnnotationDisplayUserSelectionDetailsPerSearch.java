package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.form_page_objects.AnnotationTypeDisplayData;


/**
 * 
 *
 */
public class AnnotationDisplayUserSelectionDetailsPerSearch {

	private SearchDTO searchDTO;

	private List<AnnotationTypeDisplayData> allPsmAnnotationTypeDisplay;
	private List<AnnotationTypeDisplayData> allPeptideAnnotationTypeDisplay;


	public SearchDTO getSearchDTO() {
		return searchDTO;
	}


	public void setSearchDTO(SearchDTO searchDTO) {
		this.searchDTO = searchDTO;
	}


	public List<AnnotationTypeDisplayData> getAllPsmAnnotationTypeDisplay() {
		return allPsmAnnotationTypeDisplay;
	}


	public void setAllPsmAnnotationTypeDisplay(
			List<AnnotationTypeDisplayData> allPsmAnnotationTypeDisplay) {
		this.allPsmAnnotationTypeDisplay = allPsmAnnotationTypeDisplay;
	}


	public List<AnnotationTypeDisplayData> getAllPeptideAnnotationTypeDisplay() {
		return allPeptideAnnotationTypeDisplay;
	}


	public void setAllPeptideAnnotationTypeDisplay(
			List<AnnotationTypeDisplayData> allPeptideAnnotationTypeDisplay) {
		this.allPeptideAnnotationTypeDisplay = allPeptideAnnotationTypeDisplay;
	}
	


}
