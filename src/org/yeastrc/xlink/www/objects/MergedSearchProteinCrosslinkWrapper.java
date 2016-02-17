package org.yeastrc.xlink.www.objects;

import java.util.List;
import java.util.Map;


public class MergedSearchProteinCrosslinkWrapper {	

	private MergedSearchProteinCrosslink mergedSearchProteinCrosslink;
	private List<SearchBooleanWrapper> searchContainsCrosslink;
	
	

	private Map<Integer, SearchProteinCrosslinkWrapper> searchProteinCrosslinkWrapperMapOnSearchId;

	private int proteinId_1;
	private int proteinId_2;
	
	private int protein_1_Position;
	private int protein_2_Position;
	
	
	
	public Map<Integer, SearchProteinCrosslinkWrapper> getSearchProteinCrosslinkWrapperMapOnSearchId() {
		return searchProteinCrosslinkWrapperMapOnSearchId;
	}
	public void setSearchProteinCrosslinkWrapperMapOnSearchId(
			Map<Integer, SearchProteinCrosslinkWrapper> searchProteinCrosslinkWrapperMapOnSearchId) {
		this.searchProteinCrosslinkWrapperMapOnSearchId = searchProteinCrosslinkWrapperMapOnSearchId;
	}
	public int getProteinId_1() {
		return proteinId_1;
	}
	public void setProteinId_1(int proteinId_1) {
		this.proteinId_1 = proteinId_1;
	}
	public int getProteinId_2() {
		return proteinId_2;
	}
	public void setProteinId_2(int proteinId_2) {
		this.proteinId_2 = proteinId_2;
	}
	public int getProtein_1_Position() {
		return protein_1_Position;
	}
	public void setProtein_1_Position(int protein_1_Position) {
		this.protein_1_Position = protein_1_Position;
	}
	public int getProtein_2_Position() {
		return protein_2_Position;
	}
	public void setProtein_2_Position(int protein_2_Position) {
		this.protein_2_Position = protein_2_Position;
	}
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
	
}
