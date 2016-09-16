package org.yeastrc.xlink.dto;

/**
 * table linker_per_search_crosslink_mass
 *
 */
public class LinkerPerSearchCrosslinkMassDTO {

	private int id;
	private int linkerId;
	private int searchId;
	private double crosslinkMassDouble;
	private String crosslinkMassString;
	
	
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
	public double getCrosslinkMassDouble() {
		return crosslinkMassDouble;
	}
	public void setCrosslinkMassDouble(double crosslinkMassDouble) {
		this.crosslinkMassDouble = crosslinkMassDouble;
	}
	public String getCrosslinkMassString() {
		return crosslinkMassString;
	}
	public void setCrosslinkMassString(String crosslinkMassString) {
		this.crosslinkMassString = crosslinkMassString;
	}
	@Override
	public String toString() {
		return "LinkerPerSearchCrosslinkMassDTO [id=" + id + ", linkerId="
				+ linkerId + ", searchId=" + searchId
				+ ", crosslinkMassDouble=" + crosslinkMassDouble
				+ ", crosslinkMassString=" + crosslinkMassString + "]";
	}
}
