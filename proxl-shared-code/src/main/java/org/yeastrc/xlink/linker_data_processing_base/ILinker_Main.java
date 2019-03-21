package org.yeastrc.xlink.linker_data_processing_base;

import java.util.List;
import java.util.Set;

import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;

/**
 * Interface for class for main Linker processing
 * 
 * Processing is likely deferred to builtin linker when data for linker for the search is not provided.
 *
 */
public interface ILinker_Main {

	
	/**
	 * @return
	 */
	public String getLinkerAbbreviation();

	/**
	 * @return
	 */
	public int getSearchId();
	
	/**
	 * Get all theoretically linkable positions (N-terminal residue is position 1)
	 * in the supplied protein sequence for this crosslinker
	 * 
	 * !!  Returns Null if not available for Search or for Linker in Search
	 * 
	 * @param proteinSequence
	 * @return Null if not available for Search or for Linker in Search: A collection of integers corresponding to linkable protein positions
	 * @throws Exception
	 */
	public Set<Integer> getLinkablePositions( String proteinSequence ) throws Exception;

	/**
	 * Get all theoretically linkable positions (N-terminal residue is position 1)
	 * in the querySequence given that one end is known to be the subjectPosition in
	 * the subjectSequence. If the residue at the subjectPosition in the subjectSequence
	 * is, itself, not a linkable position, an empty collection is returned.
	 * 
	 * !!  Returns Null if not available for Search or for Linker in Search
	 * 
	 * @param querySequence
	 * @param subjectSequence
	 * @param subjectPosition
	 * @return Null if not available for Search or for Linker in Search
	 * @throws Exception
	 */
	public Set<Integer> getLinkablePositions( String querySequence, String subjectSequence, int subjectPosition ) throws Exception;
	
	/**
	 * Get the length, in Angstroms of this crosslinker
	 * 
	 * @return Null if not available for Search or for Linker in Search
	 */
	public Double getLinkerLength();
	
	/**
	 * Get the formula of the cross-linker after it has linked. Essentially the formula of the spacer arm
	 * after the cross-link reaction.
	 * 
	 * @return Null if not available for Search or for Linker in Search
	 */
	public Set<String> getCrosslinkFormulas();
	
	/**
	 * Attempt to get the cross link formula for the given queryMass. For linkers with
	 * multiple cross-link masses, an attempt is made to find the correct formula
	 * for the supplied mass. If none is found, an exception is thrown.
	 * 
	 * For linkers with only one formula, that formula is always returned.
	 * 
	 * @param queryMass
	 * @return Null if not available for Search or for Linker in Search
	 * @throws Exception
	 */
	public String getCrosslinkFormula( double queryMass ) throws Exception;

	
	//  Commented out until determined if needed.  The Builtin Linkers have this method.
	/**
	 * Return true if this is a cleavable cross-linker, false if not. Cleavable cross-linkers
	 * will be treated differently by proxl in appropriate situations.
	 *
	 * @return
	 */
//	public boolean isCleavable();

	////////
	
	//  Getters for DB data
	
	public List<LinkerPerSearchMonolinkMassDTO> getLinkerPerSearchMonolinkMassDTOList();
	public List<LinkerPerSearchCrosslinkMassDTO> getLinkerPerSearchCrosslinkMassDTOList();
	public List<LinkerPerSearchCleavedCrosslinkMassDTO> getLinkerPerSearchCleavedCrosslinkMassDTOList();
}
