package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;
import java.util.HashSet;

import org.yeastrc.xlink.utils.ProteinSequenceUtils;

/**
 * Link "C" to "C"
 * 
 * No N or C terminus processing
 *
 */
public class BMOE implements ILinker {

	/**
	 * Get all theoretically linkable positions (N-terminal residue is position 1)
	 * in the supplied protein sequence for this crosslinker
	 * 
	 * @param proteinSequence
	 * @return A collection of integers corresponding to linkable protein positions
	 * @throws Exception
	 */
	@Override
	public Collection<Integer> getLinkablePositions(String proteinSequence) throws Exception {
		
		Collection<Integer> linkablePositions = ProteinSequenceUtils.getPositionsOfResidueForProteinSequence( proteinSequence, "C" );
				
		return linkablePositions;		
	}
	
	/**
	 * Get all theoretically linkable positions (N-terminal residue is position 1)
	 * in the querySequence given that one end is known to be the subjectPosition in
	 * the subjectSequence. If the residue at the subjectPosition in the subjectSequence
	 * is, itself, not a linkable position, an empty collection is returned.
	 * 
	 * @param querySequence
	 * @param subjectSequence
	 * @param subjectPosition
	 * @return
	 * @throws Exception
	 */
	@Override
	public Collection<Integer> getLinkablePositions( String querySequence, String subjectSequence, int subjectPosition ) throws Exception {

		if( !subjectSequence.substring( subjectPosition - 1, subjectPosition ).equals( "C" ) )
			return new HashSet<Integer>();
		
		return getLinkablePositions( querySequence );
	}

	/**
	 * Get the length, in Angstroms of this crosslinker
	 * 
	 * @return
	 */
	@Override
	public double getLinkerLength() {
		return 8;
	}

	@Override
	public Collection<String> getCrosslinkFormula() throws Exception {
		throw new Exception( "Undefined for this cross-linker." );
	}
}
