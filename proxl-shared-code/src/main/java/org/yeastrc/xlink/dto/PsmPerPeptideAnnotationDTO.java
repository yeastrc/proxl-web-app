package org.yeastrc.xlink.dto;


/**
 * table psm_per_peptide_annotation
 *
 */
public class PsmPerPeptideAnnotationDTO extends AnnotationDataBaseDTO {

	private int psmId;
	private int srchRepPeptPeptideId;

	
	public int getPsmId() {
		return psmId;
	}
	public void setPsmId(int psmId) {
		this.psmId = psmId;
	}
	public int getSrchRepPeptPeptideId() {
		return srchRepPeptPeptideId;
	}
	public void setSrchRepPeptPeptideId(int srchRepPeptPeptideId) {
		this.srchRepPeptPeptideId = srchRepPeptPeptideId;
	}
	@Override
	public String toString() {
		return "PsmPerPeptideAnnotationDTO [psmId=" + psmId + ", srchRepPeptPeptideId=" + srchRepPeptPeptideId + ", id="
				+ id + ", filterableDescriptiveAnnotationType=" + filterableDescriptiveAnnotationType
				+ ", annotationTypeId=" + annotationTypeId + ", annotationValueLocation=" + annotationValueLocation
				+ ", valueDouble=" + valueDouble + ", valueString=" + valueString + "]";
	}

}
