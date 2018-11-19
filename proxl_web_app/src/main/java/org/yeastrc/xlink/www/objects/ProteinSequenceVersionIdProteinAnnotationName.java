package org.yeastrc.xlink.www.objects;

/**
 * proteinSequenceVersionId
 * annotationName
 */
public class ProteinSequenceVersionIdProteinAnnotationName {

	private int proteinSequenceVersionId;
	private String annotationName;
	
	@Override
	public String toString() {
		return "proteinSequenceVersionIdProteinAnnotationName [proteinSequenceVersionId=" + proteinSequenceVersionId + ", annotationName="
				+ annotationName + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotationName == null) ? 0 : annotationName.hashCode());
		result = prime * result + proteinSequenceVersionId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProteinSequenceVersionIdProteinAnnotationName other = (ProteinSequenceVersionIdProteinAnnotationName) obj;
		if (annotationName == null) {
			if (other.annotationName != null)
				return false;
		} else if (!annotationName.equals(other.annotationName))
			return false;
		if (proteinSequenceVersionId != other.proteinSequenceVersionId)
			return false;
		return true;
	}
	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}
	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}
	public String getAnnotationName() {
		return annotationName;
	}
	public void setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
	}

}
