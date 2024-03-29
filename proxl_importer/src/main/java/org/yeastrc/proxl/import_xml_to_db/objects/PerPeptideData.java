package org.yeastrc.proxl.import_xml_to_db.objects;

import java.util.List;

import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptide_IsotopeLabel_DTO;
import org.yeastrc.xlink.dto.PeptideDTO;


/**
 * Importer Internal
 * 
 * Data from processing Peptide data.
 *
 * All of these entries will be updated with the IDs in other objects before they are saved 
 * on a per Reported Peptide basis
 * 
 */
public class PerPeptideData {
	
	private PeptideDTO peptideDTO;
	
	/**
	 * from 'peptide' record in proxl XML
	 */
	private String uniqueId;

	private SrchRepPeptPeptideDTO srchRepPeptPeptideDTO;
		
	private List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_Peptide;
	
	private List<MonolinkDataFromModificationContainer> monolinkDataFromModificationContainerList;
	
	private List<MonolinkContainer> monolinkContainerList;
	
	private List<SrchRepPeptPeptide_IsotopeLabel_DTO> srchRepPeptPeptide_IsotopeLabel_DTOList_Peptide;
	
	/**
	 * Only maps to 1 protein
	 */
	private boolean peptideIdMapsToOnlyOneProtein;
	

	/**
	 * 'unique_id' from 'peptide' record in proxl XML
	 * @return
	 */
	public String getUniqueId() {
		return uniqueId;
	}

	/**
	 * 'unique_id' from 'peptide' record in proxl XML
	 * @param uniqueId
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	/**
	 * Only maps to 1 protein
	 * @return
	 */
	public boolean isPeptideIdMapsToOnlyOneProtein() {
		return peptideIdMapsToOnlyOneProtein;
	}

	/**
	 * Only maps to 1 protein
	 * 
	 * @param peptideIdMapsToOnlyOneProtein
	 */
	public void setPeptideIdMapsToOnlyOneProtein(boolean peptideIdMapsToOnlyOneProtein) {
		this.peptideIdMapsToOnlyOneProtein = peptideIdMapsToOnlyOneProtein;
	}


	public PeptideDTO getPeptideDTO() {
		return peptideDTO;
	}

	public void setPeptideDTO(PeptideDTO peptideDTO) {
		this.peptideDTO = peptideDTO;
	}

	public List<SrchRepPeptPeptDynamicModDTO> getSrchRepPeptPeptDynamicModDTOList_Peptide() {
		return srchRepPeptPeptDynamicModDTOList_Peptide;
	}

	public void setSrchRepPeptPeptDynamicModDTOList_Peptide(
			List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_Peptide) {
		this.srchRepPeptPeptDynamicModDTOList_Peptide = srchRepPeptPeptDynamicModDTOList_Peptide;
	}

	public SrchRepPeptPeptideDTO getSrchRepPeptPeptideDTO() {
		return srchRepPeptPeptideDTO;
	}

	public void setSrchRepPeptPeptideDTO(SrchRepPeptPeptideDTO srchRepPeptPeptideDTO) {
		this.srchRepPeptPeptideDTO = srchRepPeptPeptideDTO;
	}

	public List<MonolinkContainer> getMonolinkContainerList() {
		return monolinkContainerList;
	}

	public void setMonolinkContainerList(
			List<MonolinkContainer> monolinkContainerList) {
		this.monolinkContainerList = monolinkContainerList;
	}

	public List<SrchRepPeptPeptide_IsotopeLabel_DTO> getSrchRepPeptPeptide_IsotopeLabel_DTOList_Peptide() {
		return srchRepPeptPeptide_IsotopeLabel_DTOList_Peptide;
	}

	public void setSrchRepPeptPeptide_IsotopeLabel_DTOList_Peptide(
			List<SrchRepPeptPeptide_IsotopeLabel_DTO> srchRepPeptPeptide_IsotopeLabel_DTOList_Peptide) {
		this.srchRepPeptPeptide_IsotopeLabel_DTOList_Peptide = srchRepPeptPeptide_IsotopeLabel_DTOList_Peptide;
	}

	public List<MonolinkDataFromModificationContainer> getMonolinkDataFromModificationContainerList() {
		return monolinkDataFromModificationContainerList;
	}

	public void setMonolinkDataFromModificationContainerList(
			List<MonolinkDataFromModificationContainer> monolinkDataFromModificationContainerList) {
		this.monolinkDataFromModificationContainerList = monolinkDataFromModificationContainerList;
	}

	
}