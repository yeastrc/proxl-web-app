package org.yeastrc.xlink.www.objects;

public class MergedSearchProteinPosition {

	/**
	 * Returns true if the protein and position are the same
	 */
	public boolean equals( Object o ) {
		if( !( o instanceof MergedSearchProteinPosition ) ) return false;
		MergedSearchProteinPosition mrpp = (MergedSearchProteinPosition) o;
		
		if( this.getPosition() != mrpp.getPosition() ) return false;
		if( this.getProtein().getNrProtein() != mrpp.getProtein().getNrProtein() ) return false;
		
		return true;
	}
	
	public int hashCode() {
		return ( "" + position + this.getProtein().getNrProtein().getNrseqId() ).hashCode();
	}
	
	public String toString() {
		try {
			return protein.getName() + "(" + position + ")";
		} catch( Exception e ) {
			return "Error with protein: " + protein.getNrProtein().getNrseqId();
		}
	}
	
	public MergedSearchProtein getProtein() {
		return protein;
	}
	public void setProtein(MergedSearchProtein protein) {
		this.protein = protein;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
	private MergedSearchProtein protein;
	private int position;
	
}
