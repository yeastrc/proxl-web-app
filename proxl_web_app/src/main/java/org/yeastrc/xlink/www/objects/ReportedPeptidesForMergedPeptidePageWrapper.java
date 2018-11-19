package org.yeastrc.xlink.www.objects;

import java.util.Map;

import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;

public class ReportedPeptidesForMergedPeptidePageWrapper implements SearchPeptideCommonLinkAnnDataWrapperIF {

	private ReportedPeptidesForMergedPeptidePage reportedPeptidesForMergedPeptidePage;
	

	/**
	 * PSM annotation data 
	 * 
	 * Map keyed on annotation type id of annotation data 
	 */
	private Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap;
	
	/**
	 * Peptide annotation data 
	 * 
	 * Map keyed on annotation type id of annotation data 
	 */
	private Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap;


	@Override
	public int getReportedPeptideId() throws Exception {
		return reportedPeptidesForMergedPeptidePage.getReportedPeptide().getId();
	}

	@Override
	public Map<Integer, AnnotationDataBaseDTO> getPeptideAnnotationDTOMap() {
		//  No getPeptideAnnotationDTOMap() on contained object reportedPeptidesForMergedPeptidePage
		return peptideAnnotationDTOMap;
	}

	@Override
	public void setPeptideAnnotationDTOMap(Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap) {
		//  No setPeptideAnnotationDTOMap() on contained object reportedPeptidesForMergedPeptidePage
		this.peptideAnnotationDTOMap = peptideAnnotationDTOMap;
	}

	@Override
	public Map<Integer, AnnotationDataBaseDTO> getPsmAnnotationDTOMap() {
		return psmAnnotationDTOMap;
	}

	@Override
	public void setPsmAnnotationDTOMap(
			Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap) {
		this.psmAnnotationDTOMap = psmAnnotationDTOMap;
	}

	public ReportedPeptidesForMergedPeptidePage getReportedPeptidesForMergedPeptidePage() {
		return reportedPeptidesForMergedPeptidePage;
	}


	public void setReportedPeptidesForMergedPeptidePage(
			ReportedPeptidesForMergedPeptidePage reportedPeptidesForMergedPeptidePage) {
		this.reportedPeptidesForMergedPeptidePage = reportedPeptidesForMergedPeptidePage;
	}



}
