package org.yeastrc.proxl.import_xml_to_db.dto;


/**
 * table unified_rp__search_reported_peptide_fltbl_value_generic_lookup
 *
 */
public class UnifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO {

	private int searchId;
	private int reportedPeptideId;
	private int annotationTypeId;
	private int unifiedReportedPeptideId;

	private int linkType;
	
	
	private boolean hasDynamicModifications;
	private boolean hasMonolinks;
	private boolean hasIsotopeLabels;
	
	private double peptideValueForAnnTypeId;
	
	

	//  Constructors
	
	public UnifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO() { }
	
	public UnifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO( UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO ) {
		

		this.unifiedReportedPeptideId = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.getUnifiedReportedPeptideId();
		this.reportedPeptideId = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.getReportedPeptideId();
		this.searchId = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.getSearchId();
		this.linkType = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.getLinkType();
		
		
		this.hasDynamicModifications = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.isHasDynamicModifications();
		this.hasMonolinks = unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO.isHasMonolinks();
	}

	@Override
	public String toString() {
		return "UnifiedRepPep_Search_ReportedPeptide_PeptideValue_Generic_Lookup__DTO [searchId="
				+ searchId
				+ ", reportedPeptideId="
				+ reportedPeptideId
				+ ", annotationTypeId="
				+ annotationTypeId
				+ ", unifiedReportedPeptideId="
				+ unifiedReportedPeptideId
				+ ", linkType="
				+ linkType
				+ ", hasDynamicModifications="
				+ hasDynamicModifications
				+ ", hasMonolinks="
				+ hasMonolinks
				+ ", peptideValueForAnnTypeId="
				+ peptideValueForAnnTypeId
				+ "]";
	}

	public int getSearchId() {
		return searchId;
	}

	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	public int getReportedPeptideId() {
		return reportedPeptideId;
	}

	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}

	public int getAnnotationTypeId() {
		return annotationTypeId;
	}

	public void setAnnotationTypeId(int annotationTypeId) {
		this.annotationTypeId = annotationTypeId;
	}

	public int getUnifiedReportedPeptideId() {
		return unifiedReportedPeptideId;
	}

	public void setUnifiedReportedPeptideId(int unifiedReportedPeptideId) {
		this.unifiedReportedPeptideId = unifiedReportedPeptideId;
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

	public double getPeptideValueForAnnTypeId() {
		return peptideValueForAnnTypeId;
	}

	public void setPeptideValueForAnnTypeId(double peptideValueForAnnTypeId) {
		this.peptideValueForAnnTypeId = peptideValueForAnnTypeId;
	}

	public boolean isHasIsotopeLabels() {
		return hasIsotopeLabels;
	}

	public void setHasIsotopeLabels(boolean hasIsotopeLabels) {
		this.hasIsotopeLabels = hasIsotopeLabels;
	}
	
}
