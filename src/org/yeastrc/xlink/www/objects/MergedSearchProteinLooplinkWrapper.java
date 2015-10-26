package org.yeastrc.xlink.www.objects;

import java.util.List;


public class MergedSearchProteinLooplinkWrapper {	
	
	public MergedSearchProteinLooplink getMergedSearchProteinLooplink() {
		return mergedSearchProteinLooplink;
	}
	public void setMergedSearchPeptideLooplink(
			MergedSearchProteinLooplink mergedSearchProteinLooplink) {
		this.mergedSearchProteinLooplink = mergedSearchProteinLooplink;
	}
	public List<SearchBooleanWrapper> getSearchContainsLooplink() {
		return searchContainsLooplink;
	}
	public void setSearchContainsLooplink(List<SearchBooleanWrapper> searchContainsLooplink) {
		this.searchContainsLooplink = searchContainsLooplink;
	}
	
	private MergedSearchProteinLooplink mergedSearchProteinLooplink;
	private List<SearchBooleanWrapper> searchContainsLooplink;
	
}
