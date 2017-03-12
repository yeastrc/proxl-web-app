package org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values;

import java.util.List;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;

/**
 * Output from SortOnAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId
 *
 */
public class SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt {

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
}
