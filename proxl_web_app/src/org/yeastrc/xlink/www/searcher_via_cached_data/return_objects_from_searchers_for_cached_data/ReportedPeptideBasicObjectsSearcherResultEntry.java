package org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data;

import java.util.Map;

import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;

/**
 * Single Entry Result from ReportedPeptideBasicObjectsSearcher
 *
 */
public class ReportedPeptideBasicObjectsSearcherResultEntry {
	
	private int linkType;
	private int reportedPeptideId;
	private int unifiedReportedPeptideId;

	//  These are null if not computed
	private Integer numPsms;
	private Integer numUniquePsms;
	
//  These are null if not retrieved
	private Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap;
	private Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap;
	
	
	public int getLinkType() {
		return linkType;
	}
	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}
	public int getReportedPeptideId() {
		return reportedPeptideId;
	}
	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}
	public int getUnifiedReportedPeptideId() {
		return unifiedReportedPeptideId;
	}
	public void setUnifiedReportedPeptideId(int unifiedReportedPeptideId) {
		this.unifiedReportedPeptideId = unifiedReportedPeptideId;
	}
	public Integer getNumPsms() {
		return numPsms;
	}
	public void setNumPsms(Integer numPsms) {
		this.numPsms = numPsms;
	}
	public Integer getNumUniquePsms() {
		return numUniquePsms;
	}
	public void setNumUniquePsms(Integer numUniquePsms) {
		this.numUniquePsms = numUniquePsms;
	}
	public Map<Integer, AnnotationDataBaseDTO> getPsmAnnotationDTOMap() {
		return psmAnnotationDTOMap;
	}
	public void setPsmAnnotationDTOMap(Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap) {
		this.psmAnnotationDTOMap = psmAnnotationDTOMap;
	}
	public Map<Integer, AnnotationDataBaseDTO> getPeptideAnnotationDTOMap() {
		return peptideAnnotationDTOMap;
	}
	public void setPeptideAnnotationDTOMap(Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap) {
		this.peptideAnnotationDTOMap = peptideAnnotationDTOMap;
	}
	
}
