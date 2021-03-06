package org.yeastrc.xlink.www.objects;

public class SearchProteinPosition {
	
	@Override
	public String toString() {
		try {
			return protein.getName() + "(" + position + ")";
		} catch( Exception e ) {
			return "Error with protein: " + protein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
		}
	}
	
	public SearchProtein getProtein() {
		return protein;
	}
	public void setProtein(SearchProtein protein) {
		this.protein = protein;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + position;
		result = prime * result + protein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
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
		SearchProteinPosition other = (SearchProteinPosition) obj;
		if (position != other.position)
			return false;
		if (protein.getProteinSequenceVersionObject().getProteinSequenceVersionId() != other.protein.getProteinSequenceVersionObject().getProteinSequenceVersionId()) {
			return false;
		}
		return true;
	}
	

	private SearchProtein protein;
	private int position;
	
}
