package org.yeastrc.xlink.www.lorikeet_dto;

/**
 * Variable Modification for Lorikeet,  aka: Dynamic Modification
 *
 * Lorikeet supports more properties ("losses" being one of them).
 *     Need to research them before adding them.
 * 
 */
public class LorikeetVariableMod {
	
	/**
	 * Variable Modification Position, One (1) based. aka: Dynamic Modification
	 */
	private int index;
	/**
	 * mass of the Variable Modification, aka: Dynamic Modification
	 */
	private double modMass;
	
	/**
	 * Amino Acid at the position, One (1) based
	 */
	private String aminoAcid;
	

	/**
	 * Variable Modification Position, One (1) based, aka: Dynamic Modification
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Variable Modification Position, One (1) based, aka: Dynamic Modification
	 * 
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * mass of the Variable Modification, aka: Dynamic Modification
	 * @return
	 */
	public double getModMass() {
		return modMass;
	}

	/**
	 * mass of the Variable Modification, aka: Dynamic Modification
	 * 
	 * @param modMass
	 */
	public void setModMass(double modMass) {
		this.modMass = modMass;
	}

	/**
	 * Amino Acid at the position for the Variable Modification, aka: Dynamic Modification
	 * 
	 * @return
	 */
	public String getAminoAcid() {
		return aminoAcid;
	}

	/**
	 * Amino Acid at the position for the Variable Modification, aka: Dynamic Modification
	 * 
	 * @param aminoAcid
	 */
	public void setAminoAcid(String aminoAcid) {
		this.aminoAcid = aminoAcid;
	}
	
}

