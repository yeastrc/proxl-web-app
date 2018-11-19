package org.yeastrc.xlink.www.lorikeet_dto;

/**
 * Static Modification for Lorikeet
 * 
 */
public class LorikeetStaticMod {
	
	/**
	 * Amino Acid 
	 */
	private String aminoAcid;

	/**
	 * mass of the Static Modification
	 */
	private double modMass;
	
	

	/**
	 * mass of the Static Modification
	 * @return
	 */
	public double getModMass() {
		return modMass;
	}

	/**
	 * mass of the Static Modification
	 * 
	 * @param modMass
	 */
	public void setModMass(double modMass) {
		this.modMass = modMass;
	}

	/**
	 * Amino Acid for the Static Modification
	 * 
	 * @return
	 */
	public String getAminoAcid() {
		return aminoAcid;
	}

	/**
	 * Amino Acid for the Static Modification
	 * 
	 * @param aminoAcid
	 */
	public void setAminoAcid(String aminoAcid) {
		this.aminoAcid = aminoAcid;
	}
	
}

