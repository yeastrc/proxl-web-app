package org.yeastrc.xlink.www.objects;

import java.util.List;


/**
 * Pair of lists for Peptide and PSM Names and descriptions
 *
 */
public class AnnDisplayNameDescPeptPsmListsPair {
	
	private int projectSearchId;
	private int searchId;

	private List<AnnotationDisplayNameDescription> psmAnnotationNameDescriptionList;
	private List<AnnotationDisplayNameDescription> peptideAnnotationNameDescriptionList;
	
	
	
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public List<AnnotationDisplayNameDescription> getPsmAnnotationNameDescriptionList() {
		return psmAnnotationNameDescriptionList;
	}
	public void setPsmAnnotationNameDescriptionList(
			List<AnnotationDisplayNameDescription> psmAnnotationNameDescriptionList) {
		this.psmAnnotationNameDescriptionList = psmAnnotationNameDescriptionList;
	}
	public List<AnnotationDisplayNameDescription> getPeptideAnnotationNameDescriptionList() {
		return peptideAnnotationNameDescriptionList;
	}
	public void setPeptideAnnotationNameDescriptionList(
			List<AnnotationDisplayNameDescription> peptideAnnotationNameDescriptionList) {
		this.peptideAnnotationNameDescriptionList = peptideAnnotationNameDescriptionList;
	}
	public int getProjectSearchId() {
		return projectSearchId;
	}
	public void setProjectSearchId(int projectSearchId) {
		this.projectSearchId = projectSearchId;
	}
	
}
