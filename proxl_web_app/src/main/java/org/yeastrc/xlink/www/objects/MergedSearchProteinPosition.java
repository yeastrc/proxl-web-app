package org.yeastrc.xlink.www.objects;

public class MergedSearchProteinPosition {

	/**
	 * Returns true if the protein and position are the same
	 */
	@Override
	public boolean equals( Object o ) {
		if( !( o instanceof MergedSearchProteinPosition ) ) return false;
		MergedSearchProteinPosition mrpp = (MergedSearchProteinPosition) o;
		
		if( this.getPosition() != mrpp.getPosition() ) return false;
		if( this.getProtein().getProteinSequenceVersionObject() != mrpp.getProtein().getProteinSequenceVersionObject() ) return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return ( "" + position + this.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() ).hashCode();
	}
	
	@Override
	public String toString() {
		try {
			return protein.getName() + "(" + position + ")";
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
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
	private MergedSearchProtein protein;
	private int position;
	
}
