package org.yeastrc.xlink.dto;

/**
 * table unified_rep_pep_dynamic_mod_lookup
 *
 */
public class UnifiedRepPepDynamicModLookupDTO {
	
	private int id;
	private int rpMatchedPeptideId;

	private int position;
	private double mass;
	private double massRounded;
	private String massRoundedString;
	private int massRoundingPlaces;
	private int modOrder;
	private boolean is_N_Terminal;
	private boolean is_C_Terminal;
	
	public String getMassRoundedString() {
		return massRoundedString;
	}
	public void setMassRoundedString(String massRoundedString) {
		this.massRoundedString = massRoundedString;
	}
	public int getMassRoundingPlaces() {
		return massRoundingPlaces;
	}
	public void setMassRoundingPlaces(int massRoundingPlaces) {
		this.massRoundingPlaces = massRoundingPlaces;
	}
	public int getModOrder() {
		return modOrder;
	}
	public void setModOrder(int modOrder) {
		this.modOrder = modOrder;
	}
	public double getMassRounded() {
		return massRounded;
	}
	public void setMassRounded(double massRounded) {
		this.massRounded = massRounded;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRpMatchedPeptideId() {
		return rpMatchedPeptideId;
	}
	public void setRpMatchedPeptideId(int rpMatchedPeptideId) {
		this.rpMatchedPeptideId = rpMatchedPeptideId;
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
