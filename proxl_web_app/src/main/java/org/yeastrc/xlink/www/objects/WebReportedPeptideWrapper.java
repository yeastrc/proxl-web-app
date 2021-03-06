package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;

/**
 * Wrapper for class for data for row in Peptide page
 * 
 * Supports sorting and populating annotation display values
 *
 */
public class WebReportedPeptideWrapper extends SortDisplayRecordsWrapperBase implements SearchPeptideCommonLinkAnnDataWrapperIF {


	/**
	 * Wrapped display data
	 */
	private WebReportedPeptide webReportedPeptide;





	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@Override
	public int getFinalSortOrderKey() {

		return webReportedPeptide.getReportedPeptideId();
	}
	
	
	/////////////////
	
	//   Getters and Setters for access to wrapped object
	

	@Override
	public List<String> getPsmAnnotationValueList() {
		return webReportedPeptide.getPsmAnnotationValueList();
	}


	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		webReportedPeptide.setPsmAnnotationValueList( psmAnnotationValueList );
	}

	@Override
	public List<String> getPeptideAnnotationValueList() {
		return webReportedPeptide.getPeptideAnnotationValueList();
	}


	@Override
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		webReportedPeptide.setPeptideAnnotationValueList( peptideAnnotationValueList );
	}


	@Override
	public int getReportedPeptideId() throws Exception {

		return webReportedPeptide.getReportedPeptide().getId();
	}


	

	
	/////////////////
	
	//   Getters and Setters
	

	

	public WebReportedPeptide getWebReportedPeptide() {
		return webReportedPeptide;
	}


	public void setWebReportedPeptide(WebReportedPeptide webReportedPeptide) {
		this.webReportedPeptide = webReportedPeptide;
	}

	
}
