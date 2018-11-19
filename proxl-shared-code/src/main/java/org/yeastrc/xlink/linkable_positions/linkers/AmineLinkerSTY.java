package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;
import java.util.HashSet;

import org.yeastrc.xlink.utils.ProteinSequenceUtils;

public abstract class AmineLinkerSTY implements ILinker {

	@Override
	public Collection<Integer> getLinkablePositions(String proteinSequence) throws Exception {
		
		Collection<Integer> linkablePositions = ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "K" );
		
		linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "S" ) );
		linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "T" ) );
		linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "Y" ) );
		
		
		// add the N-terminal residue, unless it's already been added
		if( !proteinSequence.startsWith( "K" ) 
				&& !proteinSequence.startsWith( "S" ) 
				&& !proteinSequence.startsWith( "T" ) 
				&& !proteinSequence.startsWith( "Y" ) ) {
			
			linkablePositions.add( 1 );
		}

		// it may be cleaved leaving the 2nd residue as the N-terminal protein. 
		if( !proteinSequence.substring(1, 2).equals( "K" ) 
				&& !proteinSequence.substring(1, 2).equals( "S" ) 
				&& !proteinSequence.substring(1, 2).equals( "T" ) 
				&& !proteinSequence.substring(1, 2).equals( "Y" ) ) {
		
			linkablePositions.add( 2 );
		}
		
		return linkablePositions;		
	}
	
	@Override
	public Collection<Integer> getLinkablePositions( String querySequence, String subjectSequence, int subjectPosition ) throws Exception {
		

		String subjectResidue = subjectSequence.substring( subjectPosition - 1, subjectPosition );
		Collection<Integer> linkablePositions = new HashSet<Integer>();
		
		if( subjectResidue.equals( "K" ) || subjectPosition == 1 || subjectPosition == 2  ) {
			
			// lysines or n-terminus interact with K, S, T, Y, n-terminus
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "K" ) );
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "S" ) );
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "T" ) );
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "Y" ) );
			linkablePositions.add( 1 );
			linkablePositions.add( 2 );

		} else if( subjectResidue.equals( "S" ) || subjectResidue.equals( "T" ) || subjectResidue.equals( "Y" ) ) {
			
			// S, T, and Y interact with K and the two N-terminal residues
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "K" ) );
			linkablePositions.add( 1 );
			linkablePositions.add( 2 );
		}		
		
		return linkablePositions;

	}

}
