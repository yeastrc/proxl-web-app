package org.yeastrc.xlink.www.objects;

/**
 * proteinSequenceId
 * annotationName
 */
public class ProteinSequenceIdProteinAnnotationName {

	private int proteinSequenceId;
	private String annotationName;
	
	@Override
	public String toString() {
		return "ProteinSequenceIdProteinAnnotationName [proteinSequenceId=" + proteinSequenceId + ", annotationName="
				+ annotationName + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotationName == null) ? 0 : annotationName.hashCode());
		result = prime * result + proteinSequenceId;
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
		ProteinSequenceIdProteinAnnotationName other = (ProteinSequenceIdProteinAnnotationName) obj;
		if (annotationName == null) {
			if (other.annotationName != null)
				return false;
		} else if (!annotationName.equals(other.annotationName))
			return false;
		if (proteinSequenceId != other.proteinSequenceId)
			return false;
		return true;
	}
	public int getProteinSequenceId() {
		return proteinSequenceId;
	}
	public void setProteinSequenceId(int proteinSequenceId) {
		this.proteinSequenceId = proteinSequenceId;
	}
	public String getAnnotationName() {
		return annotationName;
	}
	public void setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
	}

}
