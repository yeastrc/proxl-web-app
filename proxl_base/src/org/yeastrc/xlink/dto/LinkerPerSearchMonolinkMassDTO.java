package org.yeastrc.xlink.dto;

/**
 * table linker_per_search_monolink_mass
 *
 */
public class LinkerPerSearchMonolinkMassDTO {

	private int id;
	private int linkerId;
	private int searchId;
	private double monolinkMassDouble;
	private String monolinkMassString;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLinkerId() {
		return linkerId;
	}
	public void setLinkerId(int linkerId) {
		this.linkerId = linkerId;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public double getMonolinkMassDouble() {
		return monolinkMassDouble;
	}
	public void setMonolinkMassDouble(double monolinkMassDouble) {
		this.monolinkMassDouble = monolinkMassDouble;
	}
	public String getMonolinkMassString() {
		return monolinkMassString;
	}
	public void setMonolinkMassString(String monolinkMassString) {
		this.monolinkMassString = monolinkMassString;
	}
	@Override
	public String toString() {
		return "LinkerPerSearchMonolinkMassDTO [id=" + id + ", linkerId="
				+ linkerId + ", searchId=" + searchId + ", monolinkMassDouble="
				+ monolinkMassDouble + ", monolinkMassString="
				+ monolinkMassString + "]";
	}
}
