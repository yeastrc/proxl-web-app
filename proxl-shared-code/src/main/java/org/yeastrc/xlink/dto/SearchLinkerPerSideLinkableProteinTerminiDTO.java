package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.enum_classes.SearchLinkerProteinTerminusType;

/**
 * Table search_linker_per_side_linkable_protein_termini_tbl
 *
 * FK: search_linker_per_side_definition_tbl
 */
public class SearchLinkerPerSideLinkableProteinTerminiDTO {
	
	private int id;
	private int searchLinkerPerSideDefinitionId;
	private SearchLinkerProteinTerminusType proteinTerminus_c_n;
	private int distanceFromTerminus; // 0 indicates at the terminus
	
	/**
	 * 0 indicates at the terminus
	 * @return
	 */
	public int getDistanceFromTerminus() {
		return distanceFromTerminus;
	}
	/**
	 * 0 indicates at the terminus
	 * @param distanceFromTerminus
	 */
	public void setDistanceFromTerminus(int distanceFromTerminus) {
		this.distanceFromTerminus = distanceFromTerminus;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchLinkerPerSideDefinitionId() {
		return searchLinkerPerSideDefinitionId;
	}
	public void setSearchLinkerPerSideDefinitionId(int searchLinkerPerSideDefinitionId) {
		this.searchLinkerPerSideDefinitionId = searchLinkerPerSideDefinitionId;
	}
	public SearchLinkerProteinTerminusType getProteinTerminus_c_n() {
		return proteinTerminus_c_n;
	}
	public void setProteinTerminus_c_n(SearchLinkerProteinTerminusType proteinTerminus_c_n) {
		this.proteinTerminus_c_n = proteinTerminus_c_n;
	}
	
}
