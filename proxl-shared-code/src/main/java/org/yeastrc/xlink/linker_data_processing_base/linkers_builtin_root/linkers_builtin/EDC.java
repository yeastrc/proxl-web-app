package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin;

import java.util.Collection;
import java.util.HashSet;

import org.yeastrc.xlink.utils.ProteinSequenceUtils;

public class EDC implements ILinker_Builtin_Linker {

	@Override
	public String toString() {
		return "EDC";
	}
	
	@Override
	public Collection<Integer> getLinkablePositions(String proteinSequence) throws Exception {
		
		Collection<Integer> linkablePositions = ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "K" );
		
		linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "E" ) );
		linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "D" ) );
		
		
		// add the N-terminal residue, unless it's already been added
		if( !proteinSequence.startsWith( "K" ) 
				&& !proteinSequence.startsWith( "E" ) 
				&& !proteinSequence.startsWith( "D" ) ) {
			
			linkablePositions.add( 1 );
		}

		// it may be cleaved leaving the 2nd residue as the N-terminal protein. 
		if( !proteinSequence.substring(1, 2).equals( "K" ) 
				&& !proteinSequence.substring(1, 2).equals( "E" ) 
				&& !proteinSequence.substring(1, 2).equals( "D" ) ) {
		
			linkablePositions.add( 2 );
		}
		
		// add the C-terminal residue
		if( !proteinSequence.endsWith( "K" ) && !proteinSequence.endsWith( "E" ) && !proteinSequence.endsWith( "D" ) )
			linkablePositions.add( proteinSequence.length() );
		
		return linkablePositions;		
	}

	@Override
	public Collection<Integer> getLinkablePositions( String querySequence, String subjectSequence, int subjectPosition ) throws Exception {

		/*
		if( !getLinkablePositions( subjectSequence ).contains( subjectPosition ) ) {
			throw new Exception( "Position " + subjectPosition + " is not a valid linkable position for " + subjectSequence );
		}
		*/
		
		String subjectResidue = subjectSequence.substring( subjectPosition - 1, subjectPosition );
		Collection<Integer> linkablePositions = new HashSet<Integer>();
		
		if( subjectResidue.equals( "K" ) ) {
			
			// lysines interact with E, D, and C-terminus
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "E" ) );
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "D" ) );
			linkablePositions.add( querySequence.length() );

		} else if( subjectResidue.equals( "E" ) || subjectResidue.equals( "D" ) ) {
			
			// E and D interact with K and the two N-terminal residues
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "K" ) );
			linkablePositions.add( 1 );
			linkablePositions.add( 2 );
		}
		
		// if we're at one of the two N-terminal residues, add in all links to E, D and C-terminus, even if the residue is an E or D
		if( subjectPosition == 1 || subjectPosition == 2 ) {
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "E" ) );
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "D" ) );
			linkablePositions.add( querySequence.length() );
		}
		
		// if we're at the C-terminus, add in all links to K and the N-terminal residues, even if this residue is a K
		if( subjectPosition == subjectSequence.length() ) {
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "K" ) );
			linkablePositions.add( 1 );
			linkablePositions.add( 2 );
		}
		
		
		
		return linkablePositions;
	}
	
	
	@Override
	public double getLinkerLength() {
		return 0;
	}
	
	@Override
	public Collection<String> getCrosslinkFormulas() {
		
		Collection<String> formulas = new HashSet<>();
		formulas.add( "-H2O" );
		
		return formulas;
	}
	
	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "-H2O";
	}

	@Override
	public boolean isCleavable() {
		return false;
	}

}
