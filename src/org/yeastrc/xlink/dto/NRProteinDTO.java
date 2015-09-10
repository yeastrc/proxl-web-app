package org.yeastrc.xlink.dto;

import org.yeastrc.xlink.utils.YRC_NRSEQUtils;

public class NRProteinDTO {

	private int nrseqId;
	private String sequence;
	private int taxonomyId = -1;
	
	public int getNrseqId() {
		return nrseqId;
	}

	public void setNrseqId(int nrseqId) {
		this.nrseqId = nrseqId;
	}
	
	public int hashCode() {
		return nrseqId;
	}
	
	public boolean equals( Object o ) {
		if( !( o instanceof NRProteinDTO ) ) return false;
		
		if( ((NRProteinDTO)o).getNrseqId() == this.getNrseqId() )
			return true;
		
		return false;
	}
	
	public String getSequence() throws Exception {
		if( this.sequence == null )
			this.sequence = YRC_NRSEQUtils.getSequence( this.getNrseqId() );
		
		return this.sequence;
	}
	
	public int getTaxonomyId() throws Exception {
		if( this.taxonomyId == -1 )
			this.taxonomyId = YRC_NRSEQUtils.getTaxonomyId( this.getNrseqId() );
		
		return this.taxonomyId;
	}
	
	
}
