package org.yeastrc.proxl.import_xml_to_db.dto;


/**
 * table unified_rp__search__rep_pept__best_psm_value_generic_lookup
 *
 */
public class UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO {

	private int unifiedReportedPeptideId;
	private int reportedPeptideId;
	private int searchId;
	private int annotationTypeId;
	private int linkType;
	
	
	private boolean hasDynamicModifications;
	private boolean hasMonolinks;
	
	private double bestPsmValueForAnnTypeId;
	
	private int psmIdForBestValue;
	
	
	//  Constructors
	
	public UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO() { }
	
	public UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO( UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO ) {
		

		this.unifiedReportedPeptideId = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.getUnifiedReportedPeptideId();
		this.reportedPeptideId = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.getReportedPeptideId();
		this.searchId = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.getSearchId();
		this.linkType = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.getLinkType();
		
		
		this.hasDynamicModifications = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.isHasDynamicModifications();
		this.hasMonolinks = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.isHasMonolinks();
	}
	

	@Override
	public String toString() {
		return "UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO [unifiedReportedPeptideId="
				+ unifiedReportedPeptideId
				+ ", reportedPeptideId="
				+ reportedPeptideId
				+ ", searchId="
				+ searchId
				+ ", annotationTypeId="
				+ annotationTypeId
				+ ", linkType="
				+ linkType
				+ ", hasDynamicModifications="
				+ hasDynamicModifications
				+ ", hasMonolinks="
				+ hasMonolinks
				+ ", bestPsmValueForAnnTypeId="
				+ bestPsmValueForAnnTypeId
				+ ", psmIdForBestValue=" + psmIdForBestValue + "]";
	}
	
	
	

	public int getUnifiedReportedPeptideId() {
		return unifiedReportedPeptideId;
	}

	public void setUnifiedReportedPeptideId(int unifiedReportedPeptideId) {
		this.unifiedReportedPeptideId = unifiedReportedPeptideId;
	}

	public int getReportedPeptideId() {
		return reportedPeptideId;
	}

	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}

	public int getSearchId() {
		return searchId;
	}

	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}


	public int getLinkType() {
		return linkType;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	public boolean isHasDynamicModifications() {
		return hasDynamicModifications;
	}

	public void setHasDynamicModifications(boolean hasDynamicModifications) {
		this.hasDynamicModifications = hasDynamicModifications;
	}

	public boolean isHasMonolinks() {
		return hasMonolinks;
	}

	public void setHasMonolinks(boolean hasMonolinks) {
		this.hasMonolinks = hasMonolinks;
	}

	public double getBestPsmValueForAnnTypeId() {
		return bestPsmValueForAnnTypeId;
	}

	public void setBestPsmValueForAnnTypeId(double bestPsmValueForAnnTypeId) {
		this.bestPsmValueForAnnTypeId = bestPsmValueForAnnTypeId;
	}

	public int getAnnotationTypeId() {
		return annotationTypeId;
	}

	public void setAnnotationTypeId(int annotationTypeId) {
		this.annotationTypeId = annotationTypeId;
	}

	public int getPsmIdForBestValue() {
		return psmIdForBestValue;
	}

	public void setPsmIdForBestValue(int psmIdForBestValue) {
		this.psmIdForBestValue = psmIdForBestValue;
	}

	
}
