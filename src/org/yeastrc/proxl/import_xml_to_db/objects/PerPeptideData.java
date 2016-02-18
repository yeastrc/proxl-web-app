package org.yeastrc.proxl.import_xml_to_db.objects;

import java.util.List;

import org.yeastrc.xlink.dto.DynamicModDTO;
import org.yeastrc.xlink.dto.MatchedPeptideDTO;
import org.yeastrc.xlink.dto.MonolinkDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;

/**
 * Importer Internal
 * 
 * Data from processing Peptide data.
 *
 * All of these entries will be updated with the IDs in other objects before they are saved 
 * on a per PSM basis
 * 
 */
public class PerPeptideData {
	
	private PsmDTO psmDTO;

	private PeptideDTO peptideDTO;
	
	private MatchedPeptideDTO matchedPeptideDTO;
	
	private List<DynamicModDTO> dynamicModDTOList_Peptide;
	
	private List<Integer> monolinkPositionList;
	
	private List<MonolinkDTO> monolinkDTOList;

	
	
	public List<MonolinkDTO> getMonolinkDTOList() {
		return monolinkDTOList;
	}

	public void setMonolinkDTOList(List<MonolinkDTO> monolinkDTOList) {
		this.monolinkDTOList = monolinkDTOList;
	}

	public List<Integer> getMonolinkPositionList() {
		return monolinkPositionList;
	}

	public void setMonolinkPositionList(List<Integer> monolinkPositionList) {
		this.monolinkPositionList = monolinkPositionList;
	}

	public PsmDTO getPsmDTO() {
		return psmDTO;
	}

	public void setPsmDTO(PsmDTO psmDTO) {
		this.psmDTO = psmDTO;
	}

	public PeptideDTO getPeptideDTO() {
		return peptideDTO;
	}

	public void setPeptideDTO(PeptideDTO peptideDTO) {
		this.peptideDTO = peptideDTO;
	}

	public MatchedPeptideDTO getMatchedPeptideDTO() {
		return matchedPeptideDTO;
	}

	public void setMatchedPeptideDTO(MatchedPeptideDTO matchedPeptideDTO) {
		this.matchedPeptideDTO = matchedPeptideDTO;
	}

	public List<DynamicModDTO> getDynamicModDTOList_Peptide() {
		return dynamicModDTOList_Peptide;
	}

	public void setDynamicModDTOList_Peptide(
			List<DynamicModDTO> dynamicModDTOList_Peptide) {
		this.dynamicModDTOList_Peptide = dynamicModDTOList_Peptide;
	}
	
	
}