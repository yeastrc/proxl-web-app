package org.yeastrc.xlink.www.objects;

import java.util.List;


public class MergedSearchPeptideCrosslinkWrapper {	
	
	public MergedSearchPeptideCrosslink getMergedSearchPeptideCrosslink() {
		return mergedSearchPeptideCrosslink;
	}
	public void setMergedSearchPeptideCrosslink(
			MergedSearchPeptideCrosslink mergedSearchPeptideCrosslink) {
		this.mergedSearchPeptideCrosslink = mergedSearchPeptideCrosslink;
	}
	public List<SearchBooleanWrapper> getSearchContainsPeptide() {
		return searchContainsPeptide;
	}
	public void setSearchContainsPeptide(List<SearchBooleanWrapper> searchContainsPeptide) {
		this.searchContainsPeptide = searchContainsPeptide;
	}
	
	private MergedSearchPeptideCrosslink mergedSearchPeptideCrosslink;
	private List<SearchBooleanWrapper> searchContainsPeptide;
	
}
