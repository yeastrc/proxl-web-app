package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;
import java.util.HashSet;

import org.yeastrc.xlink.utils.ProteinSequenceUtils;

public abstract class AmineLinker implements ILinker {

	@Override
	public Collection<Integer> getLinkablePositions(String proteinSequence) throws Exception {
		
		Collection<Integer> linkablePositions = ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "K" );
		
		// add the N-terminal residue, unless it's already been added
		if( !proteinSequence.startsWith( "K" ) )
			linkablePositions.add( 1 );

		// if the N-terminal protein is M, it may be cleaved leaving the 2nd residue as the N-terminal protein. 
		//if( proteinSequence.startsWith( "M" ) &&  ! proteinSequence.substring(1, 2).equals( "K" ) )
		//	linkablePositions.add( 2 );

		// add second-most N-terminal residue if it has not already been added
		if( !proteinSequence.substring(1, 2).equals( "K" ) )
			linkablePositions.add( 2 );
		
		return linkablePositions;		
	}
	
	@Override
	public Collection<Integer> getLinkablePositions( String querySequence, String subjectSequence, int subjectPosition ) throws Exception {
		
		if( !subjectSequence.substring( subjectPosition - 1, subjectPosition ).equals( "K" ) && subjectPosition != 1 && subjectPosition != 2 )
			return new HashSet<Integer>();
		
		return getLinkablePositions( querySequence );
	}

}
