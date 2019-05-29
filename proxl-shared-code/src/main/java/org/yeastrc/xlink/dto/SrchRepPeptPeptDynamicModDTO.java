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
	private boolean is_N_Terminal;
	private boolean is_C_Terminal;
	
	@Override
	public String toString() {
		return "SrchRepPeptPeptDynamicModDTO [id=" + id + ", searchReportedPeptidepeptideId="
				+ searchReportedPeptidepeptideId + ", position=" + position + ", mass=" + mass + ", isMonolink="
				+ isMonolink + ", is_N_Terminal=" + is_N_Terminal + ", is_C_Terminal=" + is_C_Terminal + "]";
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
	public boolean isIs_N_Terminal() {
		return is_N_Terminal;
	}
	public void setIs_N_Terminal(boolean is_N_Terminal) {
		this.is_N_Terminal = is_N_Terminal;
	}
	public boolean isIs_C_Terminal() {
		return is_C_Terminal;
	}
	public void setIs_C_Terminal(boolean is_C_Terminal) {
		this.is_C_Terminal = is_C_Terminal;
	}

	

}
