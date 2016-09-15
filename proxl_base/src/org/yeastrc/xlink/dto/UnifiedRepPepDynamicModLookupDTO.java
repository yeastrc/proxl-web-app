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
}
