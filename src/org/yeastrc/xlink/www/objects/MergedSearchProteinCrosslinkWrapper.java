package org.yeastrc.xlink.www.objects;

import java.util.List;


public class MergedSearchProteinCrosslinkWrapper {	
	
	public MergedSearchProteinCrosslink getMergedSearchProteinCrosslink() {
		return mergedSearchProteinCrosslink;
	}
	public void setMergedSearchProteinCrosslink(
			MergedSearchProteinCrosslink mergedSearchProteinCrosslink) {
		this.mergedSearchProteinCrosslink = mergedSearchProteinCrosslink;
	}
	public List<SearchBooleanWrapper> getSearchContainsCrosslink() {
		return searchContainsCrosslink;
	}
	public void setSearchContainsCrosslink(List<SearchBooleanWrapper> searchContainsCrosslink) {
		this.searchContainsCrosslink = searchContainsCrosslink;
	}
	
	private MergedSearchProteinCrosslink mergedSearchProteinCrosslink;
	private List<SearchBooleanWrapper> searchContainsCrosslink;
	
}
