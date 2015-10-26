package org.yeastrc.xlink.www.objects;

import org.apache.commons.lang3.StringUtils;

/**
 * For Crosslink and Looplink
 *
 */
public class WebMergedProteinPosition {

	

	private MergedSearchProtein protein;
	
	private String position1;
	private String position2;
	
	
	
	public String toString() {
		
		if ( protein == null ) {
			
			String msg = "protein == null";
			
			throw new RuntimeException( msg );
		}
		
		String proteinName = null;
		try {
			proteinName = protein.getName();
		} catch( Exception e ) {
			return "Error with protein: " + protein.getNrProtein().getNrseqId();
		}
		
		if ( StringUtils.isNotEmpty( position2 ) )
			return proteinName + "(" + position1 + "," + position2 + ")";
		
		if ( StringUtils.isNotEmpty( position1 ) )
			return proteinName + "(" + position1 + ")";

		return proteinName;

	}
	
	
	public MergedSearchProtein getProtein() {
		return protein;
	}
	public void setProtein(MergedSearchProtein protein) {
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
