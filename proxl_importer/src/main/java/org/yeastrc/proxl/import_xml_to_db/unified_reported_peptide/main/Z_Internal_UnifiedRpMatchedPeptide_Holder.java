package org.yeastrc.proxl.import_xml_to_db.unified_reported_peptide.main;

import java.util.List;

import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;

/**
 * 
 *
 */
class Z_Internal_UnifiedRpMatchedPeptide_Holder {
	
	private UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptideDTO;
	private String formattedPeptideString;
	private String peptideStringWithModsAndIsotopeLabels;


	private List<Z_Internal_UnifiedRpDynamicMod_Holder> z_Internal_UnifiedRpDynamicMod_Holder_List;

	private List<Z_Internal_UnifiedRpIsotopeLabel_Holder> z_Internal_UnifiedRpIsotopeLabel_Holder_List;
	

	public String getPeptideStringWithModsAndIsotopeLabels() {
		return peptideStringWithModsAndIsotopeLabels;
	}

	public void setPeptideStringWithModsAndIsotopeLabels(String peptideStringWithModsAndIsotopeLabels) {
		this.peptideStringWithModsAndIsotopeLabels = peptideStringWithModsAndIsotopeLabels;
	}
	public String getFormattedPeptideString() {
		return formattedPeptideString;
	}

	public void setFormattedPeptideString(String formattedPeptideString) {
		this.formattedPeptideString = formattedPeptideString;
	}

	public UnifiedRepPepMatchedPeptideLookupDTO getUnifiedRpMatchedPeptideDTO() {
		return unifiedRpMatchedPeptideDTO;
	}

	public void setUnifiedRpMatchedPeptideDTO(
			UnifiedRepPepMatchedPeptideLookupDTO unifiedRpMatchedPeptideDTO) {
		this.unifiedRpMatchedPeptideDTO = unifiedRpMatchedPeptideDTO;
	}

	public List<Z_Internal_UnifiedRpDynamicMod_Holder> getZ_Internal_UnifiedRpDynamicMod_Holder_List() {
		return z_Internal_UnifiedRpDynamicMod_Holder_List;
	}

	public void setZ_Internal_UnifiedRpDynamicMod_Holder_List(
			List<Z_Internal_UnifiedRpDynamicMod_Holder> z_Internal_UnifiedRpDynamicMod_Holder_List) {
		this.z_Internal_UnifiedRpDynamicMod_Holder_List = z_Internal_UnifiedRpDynamicMod_Holder_List;
	}

	public List<Z_Internal_UnifiedRpIsotopeLabel_Holder> getZ_Internal_UnifiedRpIsotopeLabel_Holder_List() {
		return z_Internal_UnifiedRpIsotopeLabel_Holder_List;
	}

	public void setZ_Internal_UnifiedRpIsotopeLabel_Holder_List(
			List<Z_Internal_UnifiedRpIsotopeLabel_Holder> z_Internal_UnifiedRpIsotopeLabel_Holder_List) {
		this.z_Internal_UnifiedRpIsotopeLabel_Holder_List = z_Internal_UnifiedRpIsotopeLabel_Holder_List;
	}

	
}
