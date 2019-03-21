package org.yeastrc.xlink.dto;



/**
 * Table search_linker_per_side_linkable_residues_tbl
 *
 * FK: search_linker_per_side_definition_tbl
 */
public class SearchLinkerPerSideLinkableResiduesDTO {
	
	private int id;
	private int searchLinkerPerSideDefinitionId;
	private String residue;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getResidue() {
		return residue;
	}
	public void setResidue(String residue) {
		this.residue = residue;
	}
	public int getSearchLinkerPerSideDefinitionId() {
		return searchLinkerPerSideDefinitionId;
	}
	public void setSearchLinkerPerSideDefinitionId(int searchLinkerPerSideDefinitionId) {
		this.searchLinkerPerSideDefinitionId = searchLinkerPerSideDefinitionId;
	}

	
	
}
