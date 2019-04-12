package org.yeastrc.xlink.www.objects;
import java.util.Map;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
/**
 * Result from ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher
 *
 */
public class ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem implements SearchPeptideCommonLinkAnnDataWrapperIF {

	private WebReportedPeptide webReportedPeptide;
	
	/**
	 * PSM annotation data 
	 * Map keyed on annotation type id of annotation data 
	 */
	private Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap;
	/**
	 * Peptide annotation data
	 * Map keyed on annotation type id of annotation data 
	 */
	private Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap;
	
	@Override
	public int getReportedPeptideId() throws Exception {
		return webReportedPeptide.getReportedPeptideId();
	}
	
	public WebReportedPeptide getWebReportedPeptide() {
		return webReportedPeptide;
	}
	public void setWebReportedPeptide(WebReportedPeptide webReportedPeptide) {
		this.webReportedPeptide = webReportedPeptide;
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
	@Override
	public Map<Integer, AnnotationDataBaseDTO> getPeptideAnnotationDTOMap() {
		return peptideAnnotationDTOMap;
	}
	@Override
	public void setPeptideAnnotationDTOMap(
			Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap) {
		this.peptideAnnotationDTOMap = peptideAnnotationDTOMap;
	}
}
