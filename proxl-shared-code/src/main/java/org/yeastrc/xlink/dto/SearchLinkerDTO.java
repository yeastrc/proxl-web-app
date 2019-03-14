package org.yeastrc.xlink.dto;



/**
 * Table search_linker_tbl
 *
 */
public class SearchLinkerDTO {
	
	private int id;
	private int searchId;
	
	private String linkerAbbr;
	private String linkerName;
	
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
	public String getLinkerName() {
		return linkerName;
	}
	public void setLinkerName(String linkerName) {
		this.linkerName = linkerName;
	}
	
	
}
