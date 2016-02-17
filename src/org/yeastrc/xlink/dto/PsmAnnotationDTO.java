package org.yeastrc.xlink.dto;


/**
 * table psm_annotation
 *
 */
public class PsmAnnotationDTO extends AnnotationDataBaseDTO {

	private int psmId;

	
	@Override
	public String toString() {
		return "PsmAnnotationDTO [id=" + id + ", psmId=" + psmId
				+ ", filterableDescriptiveAnnotationType="
				+ filterableDescriptiveAnnotationType + ", annotationTypeId="
				+ annotationTypeId + ", valueDouble=" + valueDouble
				+ ", valueString=" + valueString + "]";
	}

	public int getPsmId() {
		return psmId;
	}
	public void setPsmId(int psmId) {
		this.psmId = psmId;
	}

	
	
}
