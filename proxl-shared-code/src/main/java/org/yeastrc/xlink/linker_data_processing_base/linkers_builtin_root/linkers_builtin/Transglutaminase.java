package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin;

import java.util.HashSet;
import java.util.Set;

import org.yeastrc.xlink.utils.ProteinSequenceUtils;

public class Transglutaminase implements ILinker_Builtin_Linker {

	@Override
	public String toString() {
		return "tg (transglutaminase)";
	}
	
	@Override
	public Set<Integer> getLinkablePositions(String proteinSequence) throws Exception {
		
		Set<Integer> linkablePositions = ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "K" );
		
		linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "Q" ) );
		
		return linkablePositions;		
	}

	@Override
	public Set<Integer> getLinkablePositions( String querySequence, String subjectSequence, int subjectPosition ) throws Exception {
		
		String subjectResidue = subjectSequence.substring( subjectPosition - 1, subjectPosition );
		Set<Integer> linkablePositions = new HashSet<Integer>();
		
		if( subjectResidue.equals( "K" ) ) {
			
			// lysines interact with E, D, and C-terminus
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "Q" ) );

		} else if( subjectResidue.equals( "Q" ) ) {
			
			// E and D interact with K and the two N-terminal residues
			linkablePositions.addAll( ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( querySequence, "K" ) );

		}
		
		return linkablePositions;
	}
	
	
	@Override
	public double getLinkerLength() {
		return 0;
	}
	
	@Override
	public Set<String> getCrosslinkFormulas() {
		
		Set<String> formulas = new HashSet<>();
		formulas.add( "-NH3" );
		
		return formulas;
	}
	
	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "-NH3";
	}

	@Override
	public boolean isCleavable() {
		return false;
	}
}
