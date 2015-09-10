package org.yeastrc.xlink.dto;

import java.math.BigDecimal;

public class StaticModDTO {
	
	@Override
	public String toString() {
		return "StaticModDTO [id=" + id + ", search_id=" + search_id
				+ ", residue=" + residue + ", mass=" + mass + "]";
	}
	
	
	private int id;
	private int search_id;
	private String residue;
	private BigDecimal mass;
	
	
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
}
