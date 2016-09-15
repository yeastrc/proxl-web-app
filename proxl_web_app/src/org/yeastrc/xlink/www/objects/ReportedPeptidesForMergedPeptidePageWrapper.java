package org.yeastrc.xlink.www.objects;

import java.util.Map;

import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;

public class ReportedPeptidesForMergedPeptidePageWrapper {

	private ReportedPeptidesForMergedPeptidePage reportedPeptidesForMergedPeptidePage;
	

	/**
	 * PSM annotation data 
	 * 
	 * Map keyed on annotation type id of annotation data 
	 */
	private Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap;


	public ReportedPeptidesForMergedPeptidePage getReportedPeptidesForMergedPeptidePage() {
		return reportedPeptidesForMergedPeptidePage;
	}


	public void setReportedPeptidesForMergedPeptidePage(
			ReportedPeptidesForMergedPeptidePage reportedPeptidesForMergedPeptidePage) {
		this.reportedPeptidesForMergedPeptidePage = reportedPeptidesForMergedPeptidePage;
	}


	public Map<Integer, AnnotationDataBaseDTO> getPsmAnnotationDTOMap() {
		return psmAnnotationDTOMap;
	}


	public void setPsmAnnotationDTOMap(
			Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap) {
		this.psmAnnotationDTOMap = psmAnnotationDTOMap;
	}


}
