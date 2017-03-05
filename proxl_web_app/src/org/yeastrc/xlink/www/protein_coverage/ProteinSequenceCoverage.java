package org.yeastrc.xlink.www.protein_coverage;

import java.util.Set;

import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

/**
 * 
 *
 */
public class ProteinSequenceCoverage {

	public ProteinSequenceCoverage( ProteinSequenceObject protein ) {
		this.protein = protein;
	}
	
	/**
	 * @return a copy of this object
	 */
	public ProteinSequenceCoverage copy() {
		ProteinSequenceObject ps = ProteinSequenceObjectFactory.getProteinSequenceObject( this.protein.getProteinSequenceId() );
		ProteinSequenceCoverage returnedProteinCoverage = new ProteinSequenceCoverage( ps );
		for( Range<Integer> range : this.getRanges() ) {
			returnedProteinCoverage.addStartEndBoundary( range.lowerEndpoint(), range.upperEndpoint() );
		}
		return returnedProteinCoverage;
	}
	
	
	/**
	 * Add the supplied start and end coordinates as a sequence coverage range
	 * @param start
	 * @param end
	 */
	public void addStartEndBoundary( int start, int end ) {

		if( this.ranges == null )
			this.ranges = TreeRangeSet.create();
		
		Range<Integer> r = Range.closed( start, end );
		this.ranges.add( r );
	}
	
	/**
	 * Add another protein sequence coverage object's ranges to this one's
	 * 
	 * @param coverageToAdd
	 */
	public void addSequenceCoverageObject( ProteinSequenceCoverage coverageToAdd ) throws Exception {
		
		if( this.ranges == null )
			this.ranges = TreeRangeSet.create();
		
		if( this.getProtein().getProteinSequenceId() != coverageToAdd.getProtein().getProteinSequenceId() )
			throw new ProxlWebappInternalErrorException( "Attempted to add two coverage objects that do not describe the same protein." );
		
		if( coverageToAdd.getRanges() == null )
			return;
		
		for( Range<Integer> r : coverageToAdd.getRanges() ) {
			this.ranges.add( r );
		}
	}
	
	/**
	 * Get the ranges of this protein's sequence that are covered by the
	 * peptides that have been added
	 * @return
	 */
	public Set<Range<Integer>> getRanges() {
		
		if( this.ranges == null )
			this.ranges = TreeRangeSet.create();
		
		return ranges.asRanges();
	}

	/**
	 * Get the sequence coverage of this protein given the peptides that have
	 * been added
	 * @return
	 */
	public Double getSequenceCoverage() throws Exception {
		int totalResidues = 0;

		if( this.ranges == null )
			this.ranges = TreeRangeSet.create();
		
		for( Range<Integer> r : this.ranges.asRanges() ) {
			totalResidues += r.upperEndpoint() - r.lowerEndpoint() + 1;
		}
		
		return (double)totalResidues / (double)this.getProtein().getSequence().length();
	}
	
	/**
	 * Get the protein covered by this sequence coverage
	 * @return
	 */
	public ProteinSequenceObject getProtein() {
		return protein;
	}


	private final ProteinSequenceObject protein;
	private RangeSet<Integer> ranges;
}
