package org.yeastrc.xlink.www.dto;

/**
 * table protein_sequence_version
 *
 */
public class ProteinSequenceVersionDTO {

	private int id;
	private int proteinSequenceId;
	private int isotopeLabelId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getproteinSequenceId() {
		return proteinSequenceId;
	}
	public void setproteinSequenceId(int proteinSequenceId) {
		this.proteinSequenceId = proteinSequenceId;
	}
	public int getIsotopeLabelId() {
		return isotopeLabelId;
	}
	public void setIsotopeLabelId(int isotopeLabelId) {
		this.isotopeLabelId = isotopeLabelId;
	}
	

}
