package org.yeastrc.xlink.www.objects;

import java.util.List;

public class ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult {
	
	private int projectSearchId;
	private String searchName;
	
	private int numPeptides;
	private int numUniquePeptides;
	private int numPsms;

	/**
	 * Used for display on web page
	 */
	private List<String> psmAnnotationValueList;
	/**
	 * Used for display on web page
	 */
	private List<String> peptideAnnotationValueList;

	//  Getters and setters
	
	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValueList;
	}
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValueList = psmAnnotationValueList;
	}
	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValueList;
	}
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValueList = peptideAnnotationValueList;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public int getNumPeptides() {
		return numPeptides;
	}
	public void setNumPeptides(int numPeptides) {
		this.numPeptides = numPeptides;
	}
	public int getNumUniquePeptides() {
		return numUniquePeptides;
	}
	public void setNumUniquePeptides(int numUniquePeptides) {
		this.numUniquePeptides = numUniquePeptides;
	}
	public int getNumPsms() {
		return numPsms;
	}
	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
	}
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}

}
