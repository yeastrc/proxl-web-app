package org.yeastrc.xlink.dto;

/**
 * table linker_per_search_monolink_mass_tbl
 *
 */
public class LinkerPerSearchMonolinkMassDTO {

	private int id;
	private int searchLinkerId;
	private int searchId;
	private double monolinkMassDouble;
	private String monolinkMassString;
	
	@Override
	public String toString() {
		return "LinkerPerSearchMonolinkMassDTO [id=" + id + ", searchLinkerId="
				+ searchLinkerId + ", searchId=" + searchId + ", monolinkMassDouble="
				+ monolinkMassDouble + ", monolinkMassString="
				+ monolinkMassString + "]";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getSearchLinkerId() {
		return searchLinkerId;
	}
	public void setSearchLinkerId(int searchLinkerId) {
		this.searchLinkerId = searchLinkerId;
	}
}
