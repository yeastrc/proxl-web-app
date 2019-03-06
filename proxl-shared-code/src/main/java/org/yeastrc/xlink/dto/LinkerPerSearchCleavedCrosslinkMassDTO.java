package org.yeastrc.xlink.dto;

/**
 * table linker_per_search_cleaved_crosslink_mass
 *
 */
public class LinkerPerSearchCleavedCrosslinkMassDTO {

	private int id;
	private int linkerId;
	private int searchId;
	private double cleavedCrosslinkMassDouble;
	private String cleavedCrosslinkMassString;
	
	@Override
	public String toString() {
		return "LinkerPerSearchCleavedCrosslinkMassDTO [id=" + id + ", linkerId=" + linkerId + ", searchId=" + searchId
				+ ", cleavedCrosslinkMassDouble=" + cleavedCrosslinkMassDouble + ", cleavedCrosslinkMassString="
				+ cleavedCrosslinkMassString + "]";
	}
	
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

}
