package org.yeastrc.xlink.www.objects;

public class ChainProteinPositionPair implements Comparable<ChainProteinPositionPair> {

	public ChainProteinPositionPair( String ichain1, int iprotein1, int iposition1, String ichain2, int iprotein2, int iposition2 ) {
		
		// ensure the "lesser" of the two chains is always listed first
		if( ichain1.compareTo( ichain2 ) < 0 ) {
			
			this.chain1 = ichain2;
			this.chain2 = ichain1;
			
			this.protein1 = iprotein2;
			this.protein2 = iprotein1;
			
			this.position1 = iposition2;
			this.position2 = iposition1;
			
		} else {
			
			
			
		}
		
		
	}
	
	@Override
	public String toString() {	
		return this.chain1 + ":" + this.protein1 + ":" + this.position1 + "-" + this.chain2 + ":" + this.protein2 + ":" + this.position2;
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
	public int compareTo(ChainProteinPositionPair o) {
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
	public String getChain1() {
		return chain1;
	}
	public String getChain2() {
		return chain2;
	}


	private int protein1;
	private int protein2;
	private int position1;
	private int position2;
	private String chain1;
	private String chain2;
	
}
