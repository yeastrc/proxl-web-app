package org.yeastrc.xlink.www.objects;

import java.util.List;
import java.util.Map;


public class MergedSearchProteinLooplinkWrapper {	
	

	private MergedSearchProteinLooplink mergedSearchProteinLooplink;

	private List<SearchBooleanWrapper> searchContainsLooplink;
	
	private Map<Integer, SearchProteinLooplinkWrapper> searchProteinLooplinkWrapperMapOnSearchId;

	private int proteinId;
	
	private int proteinPosition_1;
	private int proteinPosition_2;
	

	public Map<Integer, SearchProteinLooplinkWrapper> getSearchProteinLooplinkWrapperMapOnSearchId() {
		return searchProteinLooplinkWrapperMapOnSearchId;
	}
	public void setSearchProteinLooplinkWrapperMapOnSearchId(
			Map<Integer, SearchProteinLooplinkWrapper> searchProteinLooplinkWrapperMapOnSearchId) {
		this.searchProteinLooplinkWrapperMapOnSearchId = searchProteinLooplinkWrapperMapOnSearchId;
	}
	
	
	public int getProteinId() {
		return proteinId;
	}
	public void setProteinId(int proteinId) {
		this.proteinId = proteinId;
	}
	public int getProteinPosition_1() {
		return proteinPosition_1;
	}
	public void setProteinPosition_1(int proteinPosition_1) {
		this.proteinPosition_1 = proteinPosition_1;
	}
	public int getProteinPosition_2() {
		return proteinPosition_2;
	}
	public void setProteinPosition_2(int proteinPosition_2) {
		this.proteinPosition_2 = proteinPosition_2;
	}
	public MergedSearchProteinLooplink getMergedSearchProteinLooplink() {
		return mergedSearchProteinLooplink;
	}
	public void setMergedSearchProteinLooplink(
			MergedSearchProteinLooplink mergedSearchProteinLooplink) {
		this.mergedSearchProteinLooplink = mergedSearchProteinLooplink;
	}
	public List<SearchBooleanWrapper> getSearchContainsLooplink() {
		return searchContainsLooplink;
	}
	public void setSearchContainsLooplink(List<SearchBooleanWrapper> searchContainsLooplink) {
		this.searchContainsLooplink = searchContainsLooplink;
	}
	
}
