package org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search;

import java.util.List;

public class SearchLinkerPerSideDefinitionObj {

	private List<SearchLinkerPerSideLinkableProteinTerminiObj> proteinTerminiList;
	private List<String> residues;
	public List<String> getResidues() {
		return residues;
	}
	public void setResidues(List<String> residues) {
		this.residues = residues;
	}
	public List<SearchLinkerPerSideLinkableProteinTerminiObj> getProteinTerminiList() {
		return proteinTerminiList;
	}
	public void setProteinTerminiList(List<SearchLinkerPerSideLinkableProteinTerminiObj> proteinTerminiList) {
		this.proteinTerminiList = proteinTerminiList;
	}
}
