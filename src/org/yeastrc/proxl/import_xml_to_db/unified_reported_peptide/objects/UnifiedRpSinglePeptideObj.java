package org.yeastrc.proxl.import_xml_to_db.unified_reported_peptide.objects;

import java.util.List;

import org.yeastrc.xlink.dto.PeptideDTO;

/**
 * One peptide out of the reported peptide, along with it's dynamic mods
 *
 */
public class UnifiedRpSinglePeptideObj {


	/**
	 * peptide sequence with no dynamic mods, only the sequence letters
	 */
	private PeptideDTO peptideDTO;

	/**
	 * link positions populated 
	 */
	private int[] linkPositions;

	private List<UnifiedRpSinglePeptideDynamicMod> dynamicModList;

	
	
	/**
	 * peptide sequence with no dynamic mods, only the sequence letters
	 */
	public PeptideDTO getPeptideDTO() {
		return peptideDTO;
	}

	/**
	 * peptide sequence with no dynamic mods, only the sequence letters
	 */
	public void setPeptideDTO(PeptideDTO peptideDTO) {
		this.peptideDTO = peptideDTO;
	}
	

	public int[] getLinkPositions() {
		return linkPositions;
	}

	public void setLinkPositions(int[] linkPositions) {
		this.linkPositions = linkPositions;
	}

	
	public List<UnifiedRpSinglePeptideDynamicMod> getDynamicModList() {
		return dynamicModList;
	}

	public void setDynamicModList(
			List<UnifiedRpSinglePeptideDynamicMod> dynamicModList) {
		this.dynamicModList = dynamicModList;
	}
	

}
