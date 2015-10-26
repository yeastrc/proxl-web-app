package org.yeastrc.xlink.www.objects;

import java.util.Set;

import org.yeastrc.xlink.dto.NRProteinDTO;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class ProteinSequenceCoverage {

	public ProteinSequenceCoverage( NRProteinDTO protein ) {
		this.protein = protein;
		this.sequenceCoverage = 0.0;
	}
	
	/**
	 * Calculate the current sequence coverage of this protein given the peptides that have been
	 * added.
	 */
	private void calculateSequenceCoverage() throws Exception {
		int totalResidues = 0;
		
		for( Range<Integer> r : this.ranges.asRanges() ) {
			totalResidues += r.upperEndpoint() - r.lowerEndpoint() + 1;
		}
		
		this.sequenceCoverage = (double)totalResidues / (double)this.getProtein().getSequence().length();
	}
	
	
	/**
	 * Added the supplied peptide sequence to the peptides being used to determine
	 * this protein's sequence coverage
	 * @param sequence
	 * @throws Exception
	 */
	public void addPeptide( String peptideSequence ) throws Exception {
		
		// find all locations that this peptide maps onto the sequence and add those ranges to the RangeSet
		if( this.ranges == null )
			this.ranges = TreeRangeSet.create();
		
        // iterate over all matches of the peptide sequence in the protein sequence
        for (int i = -1; (i = this.protein.getSequence().indexOf(peptideSequence, i + 1)) != -1; ) {
        	Range<Integer> r = Range.closed( i + 1, i + peptideSequence.length() );
        	this.ranges.add( r );
        }
		
		this.calculateSequenceCoverage();
	}
	
	/**
	 * Get the ranges of this protein's sequence that are covered by the
	 * peptides that have been added
	 * @return
	 */
	public Set<Range<Integer>> getRanges() {
		return ranges.asRanges();
	}
	
	/**
	 * Get the sequence coverage of this protein given the peptides that have
	 * been added
	 * @return
	 */
	public Double getSequenceCoverage() {
		return sequenceCoverage;
	}
	
	/**
	 * Get the protein covered by this sequence coverage
	 * @return
	 */
	public NRProteinDTO getProtein() {
		return protein;
	}


	private final NRProteinDTO protein;
	private Double sequenceCoverage;
	private RangeSet<Integer> ranges;
}
