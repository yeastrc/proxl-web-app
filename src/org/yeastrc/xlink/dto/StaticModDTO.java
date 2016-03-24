package org.yeastrc.xlink.dto;

import java.math.BigDecimal;

public class StaticModDTO {
	

	
	
	private int id;
	private int search_id;
	private String residue;
	private BigDecimal mass;
	private String massString;
	
	
	public String getResidue() {
		return residue;
	}
	public void setResidue(String residue) {
		this.residue = residue;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearch_id() {
		return search_id;
	}
	public void setSearch_id(int search_id) {
		this.search_id = search_id;
	}
	public BigDecimal getMass() {
		return mass;
	}
	public void setMass(BigDecimal mass) {
		this.mass = mass;
	}
	public String getMassString() {
		return massString;
	}
	public void setMassString(String massString) {
		this.massString = massString;
	}
	@Override
	public String toString() {
		return "StaticModDTO [id=" + id + ", search_id=" + search_id
				+ ", residue=" + residue + ", mass=" + mass + ", massString="
				+ massString + "]";
	}
}
