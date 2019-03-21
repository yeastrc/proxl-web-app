package org.yeastrc.xlink.dto;



/**
 * Table search_linker_per_side_definition_tbl
 *
 * 2 records per linker per search
 */
public class SearchLinkerPerSideDefinitionDTO {
	
	private int id;
	private int searchLinkerId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchLinkerId() {
		return searchLinkerId;
	}
	public void setSearchLinkerId(int searchLinkerId) {
		this.searchLinkerId = searchLinkerId;
	}
	
	
}
