package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;
import java.util.HashSet;

import org.yeastrc.xlink.utils.ProteinSequenceUtils;

/*
 * For the Sulfo-SMCC cross-linker
 * From Kai Cai: Sulfo-SMCC;C12H13NO3;K{;C
 * Info at: https://tools.thermofisher.com/content/sfs/manuals/MAN0011295_SMCC_SulfoSMCC_UG.pdf
 */
public class SulfoSMCC implements ILinker {

	@Override
	public String toString() {
		return "Sulfo-SMCC";
	}
	
	@Override
	public Collection<Integer> getLinkablePositions(String proteinSequence) throws Exception {

		// add in lysines
		Collection<Integer> linkablePositions = ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "K" );

		// add in cysteines
		linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "C" ) );
		
		// add in the two n-terminal residues
		if( !linkablePositions.contains( 1 ) )
			linkablePositions.add( 1 );
		
		if( !linkablePositions.contains( 2 ) )
			linkablePositions.add( 2 );


		return linkablePositions;	
		
	}

	@Override
	public Collection<Integer> getLinkablePositions(String querySequence, String subjectSequence, int subjectPosition)
			throws Exception {

		String subjectResidue = subjectSequence.substring( subjectPosition - 1, subjectPosition );
		Collection<Integer> linkablePositions = new HashSet<Integer>();
		
		if( subjectResidue.equals( "K" ) ) {
			
			// lysines interact with C
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "C" ) );

		} else if( subjectResidue.equals( "C" ) ) {
			
			// C interact with K and the two N-terminal residues
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "K" ) );
			linkablePositions.add( 1 );
			linkablePositions.add( 2 );
		}
		
		// if we're at one of the two N-terminal residues, add in all links to C
		if( subjectPosition == 1 || subjectPosition == 2 ) {
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "C" ) );
		}
		
		return linkablePositions;
		
	}

	@Override
	public double getLinkerLength() {
		return 8.3;
	}

	@Override
	public Collection<String> getCrosslinkFormulas() {

		Collection<String> formulas = new HashSet<>();
		formulas.add( "C12H13NO3" );
		
		return formulas;
		
	}

	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "C12H13NO3";			// from 
	}

	@Override
	public boolean isCleavable() {
		return false;
	}

}
