package org.yeastrc.xlink.www.objects;

public class SearchProteinDoublePosition {
	
	public String toString() {
		try {
			return protein.getName() + "(" + position1 + "," + position2 + ")";
		} catch( Exception e ) {
			return "Error with protein: " + protein.getProteinSequenceObject().getProteinSequenceId();
		}
	}
	
	public SearchProtein getProtein() {
		return protein;
	}
	public void setProtein(SearchProtein protein) {
		this.protein = protein;
	}
	public int getPosition1() {
		return position1;
	}
	public void setPosition1(int position) {
		this.position1 = position;
	}
	public int getPosition2() {
		return position2;
	}
	public void setPosition2(int position) {
		this.position2 = position;
	}
	
	private SearchProtein protein;
	private int position1;
	private int position2;
	
}
