package org.yeastrc.xlink.dto;

/**
 * search__search_program table
 */
public class SearchSearchProgramDTO {

	private int searchId;
	private int searchProgramId;

	private String version;

	
	
	
	@Override
	public String toString() {
		return "SearchSearchProgramDTO [searchId=" + searchId
				+ ", searchProgramId=" + searchProgramId + ", version="
				+ version + "]";
	}

	public int getSearchId() {
		return searchId;
	}

	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	public int getSearchProgramId() {
		return searchProgramId;
	}

	public void setSearchProgramId(int searchProgramId) {
		this.searchProgramId = searchProgramId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
		
}

