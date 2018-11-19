package org.yeastrc.xlink.www.dto;

public class PDBAlignmentDTO {

	private int id;
	
	private int pdbFileId;
	private String chainId;
	private int proteinSequenceVersionId;
	private String alignedPDBSequence;
	private String alignedExperimentalSequence;
	
	public int getPdbFileId() {
		return pdbFileId;
	}
	public void setPdbFileId(int pdbFileId) {
		this.pdbFileId = pdbFileId;
	}
	public String getChainId() {
		return chainId;
	}
	public void setChainId(String chainId) {
		this.chainId = chainId;
	}
	public String getAlignedPDBSequence() {
		return alignedPDBSequence;
	}
	public void setAlignedPDBSequence(String alignedPDBSequence) {
		this.alignedPDBSequence = alignedPDBSequence;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAlignedExperimentalSequence() {
		return alignedExperimentalSequence;
	}
	public void setAlignedExperimentalSequence(String alignedExperimentalSequence) {
		this.alignedExperimentalSequence = alignedExperimentalSequence;
	}
	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}
	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}

	
	
}
