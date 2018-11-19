package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.dto.LinkerDTO;

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
	private List<LinkerDTO> linkers;
	
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
	public List<LinkerDTO> getLinkers() {
		return linkers;
	}
	public void setLinkers(List<LinkerDTO> linkers) {
		this.linkers = linkers;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
}
