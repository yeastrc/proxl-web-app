package org.yeastrc.xlink.www.objects;

import java.util.List;


public class MergedSearchPeptideLooplinkWrapper {	
	
	public MergedSearchPeptideLooplink getMergedSearchPeptideLooplink() {
		return mergedSearchPeptideLooplink;
	}
	public void setMergedSearchPeptideLooplink(
			MergedSearchPeptideLooplink mergedSearchPeptideLooplink) {
		this.mergedSearchPeptideLooplink = mergedSearchPeptideLooplink;
	}
	public List<SearchBooleanWrapper> getSearchContainsPeptide() {
		return searchContainsPeptide;
	}
	public void setSearchContainsPeptide(List<SearchBooleanWrapper> searchContainsPeptide) {
		this.searchContainsPeptide = searchContainsPeptide;
	}
	
	private MergedSearchPeptideLooplink mergedSearchPeptideLooplink;
	private List<SearchBooleanWrapper> searchContainsPeptide;
	
}
