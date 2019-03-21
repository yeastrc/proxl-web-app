package org.yeastrc.xlink.dto;

/**
 * table linker_per_search_crosslink_mass_tbl
 *
 */
public class LinkerPerSearchCrosslinkMassDTO {

	private int id;
	private int searchLinkerId;
	private int searchId;
	private double crosslinkMassDouble;
	private String crosslinkMassString;
	private String chemicalFormula;
	
	@Override
	public String toString() {
		return "LinkerPerSearchCrosslinkMassDTO [id=" + id + ", searchLinkerId=" + searchLinkerId + ", searchId="
				+ searchId + ", crosslinkMassDouble=" + crosslinkMassDouble + ", crosslinkMassString="
				+ crosslinkMassString + ", chemicalFormula=" + chemicalFormula + "]";
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
	public int getSearchLinkerId() {
		return searchLinkerId;
	}
	public void setSearchLinkerId(int searchLinkerId) {
		this.searchLinkerId = searchLinkerId;
	}

	public String getChemicalFormula() {
		return chemicalFormula;
	}

	public void setChemicalFormula(String chemicalFormula) {
		this.chemicalFormula = chemicalFormula;
	}

}
