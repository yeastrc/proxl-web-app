package org.yeastrc.xlink.dto;

/**
 * table unified_rp__rep_pept__search__best_psm_value_generic_lookup
 *
 */
public class UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO {

	private int unifiedReportedPeptideId;
	private int reportedPeptideId;
	private int searchId;
	private int psmFilterableAnnotationTypeId;
	private int linkType;
	
	
	private boolean hasDynamicModifications;
	private boolean hasMonolinks;
	
	/**
	 * The PSM count for the psmFilterableAnnotationTypeId.  null if not a default filter
	 */
	private Integer psmNumForAnnTypeIdAtDefaultCutoff;
	
	private int samplePsmId;
	
	private double bestPsmValueForAnnTypeId;
	private String bestPsmValueStringForAnnTypeId;
	
	
	//  Constructors
	
	public UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO() { }
	
	public UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO( UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO ) {
		

		this.unifiedReportedPeptideId = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getUnifiedReportedPeptideId();
		this.reportedPeptideId = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getReportedPeptideId();
		this.searchId = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getSearchId();
		this.linkType = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getLinkType();
		
		
		this.hasDynamicModifications = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.isHasDynamicModifications();
		this.hasMonolinks = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.isHasMonolinks();
		
		this.samplePsmId = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getSamplePsmId();
		
	}
	
	//   toString()

	@Override
	public String toString() {
		return "UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO [unifiedReportedPeptideId="
				+ unifiedReportedPeptideId
				+ ", reportedPeptideId="
				+ reportedPeptideId
				+ ", searchId="
				+ searchId
				+ ", psmFilterableAnnotationTypeId="
				+ psmFilterableAnnotationTypeId
				+ ", linkType="
				+ linkType
				+ ", hasDynamicModifications="
				+ hasDynamicModifications
				+ ", hasMonolinks="
				+ hasMonolinks
				+ ", psmNumForAnnTypeIdAtDefaultCutoff="
				+ psmNumForAnnTypeIdAtDefaultCutoff
				+ ", samplePsmId="
				+ samplePsmId
				+ ", bestPsmValueForAnnTypeId="
				+ bestPsmValueForAnnTypeId
				+ ", bestPsmValueStringForAnnTypeId="
				+ bestPsmValueStringForAnnTypeId + "]";
	}
	
	
	
	//  Getters and Setters
	
	public int getAnnotationTypeId() {
		return psmFilterableAnnotationTypeId;
	}
	public void setPsmFilterableAnnotationTypeId(int psmFilterableAnnotationTypeId) {
		this.psmFilterableAnnotationTypeId = psmFilterableAnnotationTypeId;
	}
	public double getBestPsmValueForAnnTypeId() {
		return bestPsmValueForAnnTypeId;
	}
	public void setBestPsmValueForAnnTypeId(double bestPsmValueForAnnTypeId) {
		this.bestPsmValueForAnnTypeId = bestPsmValueForAnnTypeId;
	}
	public String getBestPsmValueStringForAnnTypeId() {
		return bestPsmValueStringForAnnTypeId;
	}
	public void setBestPsmValueStringForAnnTypeId(
			String bestPsmValueStringForAnnTypeId) {
		this.bestPsmValueStringForAnnTypeId = bestPsmValueStringForAnnTypeId;
	}
	public int getSamplePsmId() {
		return samplePsmId;
	}
	public void setSamplePsmId(int samplePsmId) {
		this.samplePsmId = samplePsmId;
	}
	public boolean isHasMonolinks() {
		return hasMonolinks;
	}
	public void setHasMonolinks(boolean hasMonolinks) {
		this.hasMonolinks = hasMonolinks;
	}
	public boolean isHasDynamicModifications() {
		return hasDynamicModifications;
	}
	public void setHasDynamicModifications(boolean hasDynamicModifications) {
		this.hasDynamicModifications = hasDynamicModifications;
	}
	public int getLinkType() {
		return linkType;
	}
	public void setLinkType(int linkType) {
		this.linkType = linkType;
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
	public Integer getPsmNumForAnnTypeIdAtDefaultCutoff() {
		return psmNumForAnnTypeIdAtDefaultCutoff;
	}
	public void setPsmNumForAnnTypeIdAtDefaultCutoff(Integer psmNumForAnnTypeIdAtDefaultCutoff) {
		this.psmNumForAnnTypeIdAtDefaultCutoff = psmNumForAnnTypeIdAtDefaultCutoff;
	}

}
