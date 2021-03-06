package org.yeastrc.xlink.www.objects;

public class MergedSearchProteinDoublePosition {

	/**
	 * Returns true if the protein and position are the same
	 */
	@Override
	public boolean equals( Object o ) {
		if( !( o instanceof MergedSearchProteinDoublePosition ) ) return false;
		MergedSearchProteinDoublePosition mrpp = (MergedSearchProteinDoublePosition) o;
		
		if( this.getPosition1() != mrpp.getPosition1() ) return false;
		if( this.getPosition2() != mrpp.getPosition2() ) return false;
		if( this.getProtein().getProteinSequenceVersionObject() != mrpp.getProtein().getProteinSequenceVersionObject() ) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return ( "" + position1 + position2 + this.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() ).hashCode();
	}
	
	@Override
	public String toString() {
		try {
			return protein.getName() + "(" + position1 + "," + position2 + ")";
		} catch( Exception e ) {
			return "Error with protein: " + protein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
		}
	}
	
	public MergedSearchProtein getProtein() {
		return protein;
	}
	public void setProtein(MergedSearchProtein protein) {
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
	
	
	
	private MergedSearchProtein protein;
	private int position1;
	private int position2;
	
}
