package org.yeastrc.xlink.dto;


/**
 * table srch_rep_pept__pept__dynamic_mod
 *
 */
public class SrchRepPeptPeptDynamicModDTO {

	private int id;
	private int searchReportedPeptidepeptideId;
	private int position;
	private double mass;
	private boolean isMonolink;
	
	@Override
	public String toString() {
		return "SrchRepPeptPeptDynamicModDTO [id=" + id
				+ ", searchReportedPeptidepeptideId="
				+ searchReportedPeptidepeptideId + ", position=" + position
				+ ", mass=" + mass + ", isMonolink=" + isMonolink + "]";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchReportedPeptidepeptideId() {
		return searchReportedPeptidepeptideId;
	}
	public void setSearchReportedPeptidepeptideId(int searchReportedPeptidepeptideId) {
		this.searchReportedPeptidepeptideId = searchReportedPeptidepeptideId;
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

	
	

}
