package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * Parts from SearchDTO serialized to JSON and sent to JS code on Image and Structure Pages 
 *
 */
public class SearchDTO_PartsForImageStructureWebservices {

	/**
	 * projectSearchId
	 */
	private int id;
	
	private int searchId;
	
	/**
	 * Used in Structure page
	 */
	private List<LinkerData> linkers;
	
	/**
	 * @return projectSearchId
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id - projectSearchId
	 */
	public void setId(int id) {
		this.id = id;
	}
	public List<LinkerData> getLinkers() {
		return linkers;
	}
	public void setLinkers(List<LinkerData> linkers) {
		this.linkers = linkers;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	
	/**
	 * Linker Data
	 *
	 */
	public static class LinkerData {
		
		private String abbr;

		public String getAbbr() {
			return abbr;
		}

		public void setAbbr(String abbr) {
			this.abbr = abbr;
		}
	}
}
