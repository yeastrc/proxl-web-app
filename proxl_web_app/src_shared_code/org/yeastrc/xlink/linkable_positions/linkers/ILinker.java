package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;


public interface ILinker {

	/**
	 * Get all theoretically linkable positions (N-terminal residue is position 1)
	 * in the supplied protein sequence for this crosslinker
	 * 
	 * @param proteinSequence
	 * @return A collection of integers corresponding to linkable protein positions
	 * @throws Exception
	 */
	public Collection<Integer> getLinkablePositions( String proteinSequence ) throws Exception;

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
	public Collection<Integer> getLinkablePositions( String querySequence, String subjectSequence, int subjectPosition ) throws Exception;
	
	/**
	 * Get the length, in Angstroms of this crosslinker
	 * 
	 * @return
	 */
	public double getLinkerLength();
	
	/**
	 * Get the formula of the cross-linker after it has linked. Essentially the formula of the spacer arm
	 * after the cross-link reaction.
	 * 
	 * @return
	 */
	public Collection<String> getCrosslinkFormula() throws Exception;
	
}
