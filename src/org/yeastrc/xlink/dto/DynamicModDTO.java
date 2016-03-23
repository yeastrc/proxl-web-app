package org.yeastrc.xlink.dto;

public class DynamicModDTO {
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMatched_peptide_id() {
		return matched_peptide_id;
	}
	public void setMatched_peptide_id(int matched_peptide_id) {
		this.matched_peptide_id = matched_peptide_id;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass = mass;
	}
	public boolean isMonolink() {
		return isMonolink;
	}
	public void setMonolink(boolean isMonolink) {
		this.isMonolink = isMonolink;
	}

	
	
	private int id;
	private int matched_peptide_id;

	private int position;
	private double mass;
	private boolean isMonolink;
}
