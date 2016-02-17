package org.yeastrc.xlink.dto;

/**
 * table unified_rp__rep_pept__search__peptide_fltbl_value_generic_lookup
 *
 */
public class UnifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO {

	private int unifiedReportedPeptideId;
	private int reportedPeptideId;
	private int searchId;
	private int annotationTypeId;
	private int linkType;
	
	
	private boolean hasDynamicModifications;
	private boolean hasMonolinks;
	
	private int samplePsmId;
	
	private double peptideValueForAnnTypeId;
	private String peptideValueStringForAnnTypeId;
	
	
	//  Constructors
	
	public UnifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO() { }
	
	public UnifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO( UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO ) {
		

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
		return "UnifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO [unifiedReportedPeptideId="
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
				+ ", samplePsmId="
				+ samplePsmId
				+ ", peptideValueForAnnTypeId="
				+ peptideValueForAnnTypeId
				+ ", peptideValueStringForAnnTypeId="
				+ peptideValueStringForAnnTypeId + "]";
	}

	
	//  Getters and Setters


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

	public int getAnnotationTypeId() {
		return annotationTypeId;
	}

	public void setAnnotationTypeId(
			int annotationTypeId) {
		this.annotationTypeId = annotationTypeId;
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

	public int getSamplePsmId() {
		return samplePsmId;
	}

	public void setSamplePsmId(int samplePsmId) {
		this.samplePsmId = samplePsmId;
	}

	public double getPeptideValueForAnnTypeId() {
		return peptideValueForAnnTypeId;
	}

	public void setPeptideValueForAnnTypeId(double peptideValueForAnnTypeId) {
		this.peptideValueForAnnTypeId = peptideValueForAnnTypeId;
	}

	public String getPeptideValueStringForAnnTypeId() {
		return peptideValueStringForAnnTypeId;
	}

	public void setPeptideValueStringForAnnTypeId(
			String peptideValueStringForAnnTypeId) {
		this.peptideValueStringForAnnTypeId = peptideValueStringForAnnTypeId;
	}


}
