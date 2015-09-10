package org.yeastrc.xlink.dto;

public class PDBAlignmentDTO {

	private int id;
	
	private int pdbFileId;
	private String chainId;
	private String alignedPDBSequence;
	
	private int nrseqId;
	private String alignedNrseqSequence;
	
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
	public int getNrseqId() {
		return nrseqId;
	}
	public void setNrseqId(int nrseqId) {
		this.nrseqId = nrseqId;
	}
	public String getAlignedNrseqSequence() {
		return alignedNrseqSequence;
	}
	public void setAlignedNrseqSequence(String alignedNrseqSequence) {
		this.alignedNrseqSequence = alignedNrseqSequence;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
