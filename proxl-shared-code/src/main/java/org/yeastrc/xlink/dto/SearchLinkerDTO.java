package org.yeastrc.xlink.dto;



/**
 * Table search_linker_tbl
 *
 */
public class SearchLinkerDTO {
	
	private int id;
	private int searchId;
	
	private String linkerAbbr;
	private Double spacerArmLength;
	private String spacerArmLengthString;
	
	@Override
	public String toString() {
		return "SearchLinkerDTO [id=" + id + ", searchId=" + searchId + ", linkerAbbr=" + linkerAbbr
				+ ", spacerArmLength=" + spacerArmLength + ", spacerArmLengthString=" + spacerArmLengthString + "]";
	}
	
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLinkerAbbr() {
		return linkerAbbr;
	}
	public void setLinkerAbbr(String linkerAbbr) {
		this.linkerAbbr = linkerAbbr;
	}
	public Double getSpacerArmLength() {
		return spacerArmLength;
	}
	public void setSpacerArmLength(Double spacerArmLength) {
		this.spacerArmLength = spacerArmLength;
	}
	public String getSpacerArmLengthString() {
		return spacerArmLengthString;
	}
	public void setSpacerArmLengthString(String spacerArmLengthString) {
		this.spacerArmLengthString = spacerArmLengthString;
	}
	
}
