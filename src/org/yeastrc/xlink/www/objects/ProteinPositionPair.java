package org.yeastrc.xlink.www.objects;

public class ProteinPositionPair implements Comparable<ProteinPositionPair> {

	public ProteinPositionPair( int iprotein1, int iposition1, int iprotein2, int iposition2 ) {
		
		if( iprotein1 <= iprotein2 ) {
			this.protein1 = iprotein1;
			this.protein2 = iprotein2;
			
			if( iprotein1 == iprotein2 ) {
				if( iposition1 <= iposition2 ) {
					this.position1 = iposition1;
					this.position2 = iposition2;
				} else {
					this.position1 = iposition2;
					this.position2 = iposition1;
				}
			} else {
				this.position1 = iposition1;
				this.position2 = iposition2;
			}
		} else {
			this.protein1 = iprotein2;
			this.protein2 = iprotein1;
			this.position1 = iposition2;
			this.position2 = iposition1;
		}
	}
	
	@Override
	public String toString() {
		
		if( this.protein1 == this.protein2 ) {
			if( this.position1 < this.position2 ) {
				return this.protein1 + ":" + this.position1 + "-" + this.protein2 + ":" + this.position2;
			}
		}

		if( this.protein1 < this.protein2 ) {
			return this.protein1 + ":" + this.position1 + "-" + this.protein2 + ":" + this.position2;
		}
		
		
		return this.protein2 + ":" + this.position2 + "-" + this.protein1 + ":" + this.position1;
		
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public boolean equals( Object o ) {
		
		if (!(o instanceof ProteinPositionPair))
			return false;
	       
		if (o == this)
			return true;
		
		return this.toString().equals( ((ProteinPositionPair)o).toString() );
	}
	
	@Override
	public int compareTo(ProteinPositionPair o) {
		return this.toString().compareTo( o.toString() );
	}
	
	public int getProtein1() {
		return protein1;
	}

	public int getProtein2() {
		return protein2;
	}

	public int getPosition1() {
		return position1;
	}

	public int getPosition2() {
		return position2;
	}

	
	private int protein1;
	private int protein2;
	private int position1;
	private int position2;

	
}
