package org.yeastrc.proxl.import_xml_to_db.unified_reported_peptide.main;

import java.util.List;

import org.yeastrc.xlink.dto.UnifiedReportedPeptideLookupDTO;

/**
 * 
 *
 */
class Z_Internal_UnifiedReportedPeptide_Holder {

	private UnifiedReportedPeptideLookupDTO unifiedReportedPeptideDTO;
	
	private List<Z_Internal_UnifiedRpMatchedPeptide_Holder> z_Internal_UnifiedRpMatchedPeptide_HolderList;

	
	
	public List<Z_Internal_UnifiedRpMatchedPeptide_Holder> getZ_Internal_UnifiedRpMatchedPeptide_HolderList() {
		return z_Internal_UnifiedRpMatchedPeptide_HolderList;
	}

	public void setZ_Internal_UnifiedRpMatchedPeptide_HolderList(
			List<Z_Internal_UnifiedRpMatchedPeptide_Holder> z_Internal_UnifiedRpMatchedPeptide_HolderList) {
		this.z_Internal_UnifiedRpMatchedPeptide_HolderList = z_Internal_UnifiedRpMatchedPeptide_HolderList;
	}

	public UnifiedReportedPeptideLookupDTO getUnifiedReportedPeptideDTO() {
		return unifiedReportedPeptideDTO;
	}

	public void setUnifiedReportedPeptideDTO(
			UnifiedReportedPeptideLookupDTO unifiedReportedPeptideDTO) {
		this.unifiedReportedPeptideDTO = unifiedReportedPeptideDTO;
	}

}
