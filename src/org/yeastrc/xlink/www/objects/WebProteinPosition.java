package org.yeastrc.xlink.www.objects;

/**
 * For Crosslink and Looplink
 *
 */
public class WebProteinPosition {

	

	private SearchProtein protein;
	
	private String position1;
	private String position2;
	
	
	public SearchProtein getProtein() {
		return protein;
	}
	public void setProtein(SearchProtein protein) {
		this.protein = protein;
	}
	public String getPosition1() {
		return position1;
	}
	public void setPosition1(String position1) {
		this.position1 = position1;
	}
	public String getPosition2() {
		return position2;
	}
	public void setPosition2(String position2) {
		this.position2 = position2;
	}
}
