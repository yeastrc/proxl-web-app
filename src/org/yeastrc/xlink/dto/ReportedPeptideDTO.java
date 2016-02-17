package org.yeastrc.xlink.dto;

public class ReportedPeptideDTO {
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	private int id;
	private String sequence;

	
}
