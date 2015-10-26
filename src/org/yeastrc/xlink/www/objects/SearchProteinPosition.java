package org.yeastrc.xlink.www.objects;

public class SearchProteinPosition {
	
	public String toString() {
		try {
			return protein.getName() + "(" + position + ")";
		} catch( Exception e ) {
			return "Error with protein: " + protein.getNrProtein().getNrseqId();
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
	
	private SearchProtein protein;
	private int position;
	
}
