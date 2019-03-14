package org.yeastrc.xlink.dto;

/**
 * table linker_per_search_cleaved_crosslink_mass
 *
 */
public class LinkerPerSearchCleavedCrosslinkMassDTO {

	private int id;
	private int searchLinkerId;
	private int searchId;
	private double cleavedCrosslinkMassDouble;
	private String cleavedCrosslinkMassString;
	
	@Override
	public String toString() {
		return "LinkerPerSearchCleavedCrosslinkMassDTO [id=" + id + ", searchLinkerId=" + searchLinkerId + ", searchId=" + searchId
				+ ", cleavedCrosslinkMassDouble=" + cleavedCrosslinkMassDouble + ", cleavedCrosslinkMassString="
				+ cleavedCrosslinkMassString + "]";
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
	public double getCleavedCrosslinkMassDouble() {
		return cleavedCrosslinkMassDouble;
	}
	public void setCleavedCrosslinkMassDouble(double cleavedCrosslinkMassDouble) {
		this.cleavedCrosslinkMassDouble = cleavedCrosslinkMassDouble;
	}
	public String getCleavedCrosslinkMassString() {
		return cleavedCrosslinkMassString;
	}
	public void setCleavedCrosslinkMassString(String cleavedCrosslinkMassString) {
		this.cleavedCrosslinkMassString = cleavedCrosslinkMassString;
	}

	public int getSearchLinkerId() {
		return searchLinkerId;
	}

	public void setSearchLinkerId(int searchLinkerId) {
		this.searchLinkerId = searchLinkerId;
	}

}
