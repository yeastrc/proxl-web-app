package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 * One entry in the map of reported peptides per search for the Merged Peptide Page
 *
 */
public class ReportedPeptidesPerSearchForMergedPeptidePageResultEntry {


	private List<ReportedPeptidesForMergedPeptidePage> reportedPepides;
	

	private List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList;

	private List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList;

	
	
	
	
	
	public List<AnnotationDisplayNameDescription> getPeptideAnnotationDisplayNameDescriptionList() {
		return peptideAnnotationDisplayNameDescriptionList;
	}

	public void setPeptideAnnotationDisplayNameDescriptionList(
			List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList) {
		this.peptideAnnotationDisplayNameDescriptionList = peptideAnnotationDisplayNameDescriptionList;
	}

	public List<AnnotationDisplayNameDescription> getPsmAnnotationDisplayNameDescriptionList() {
		return psmAnnotationDisplayNameDescriptionList;
	}

	public void setPsmAnnotationDisplayNameDescriptionList(
			List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList) {
		this.psmAnnotationDisplayNameDescriptionList = psmAnnotationDisplayNameDescriptionList;
	}

	

	public List<ReportedPeptidesForMergedPeptidePage> getReportedPepides() {
		return reportedPepides;
	}

	public void setReportedPepides(
			List<ReportedPeptidesForMergedPeptidePage> reportedPepides) {
		this.reportedPepides = reportedPepides;
	}
}
