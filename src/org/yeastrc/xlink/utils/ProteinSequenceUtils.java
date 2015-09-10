package org.yeastrc.xlink.utils;


import java.util.Collection;
import java.util.HashSet;

public class ProteinSequenceUtils {
	
	
	/**
	 * Find and return all positions that correspond to the supplied residue (e.g. K for lysine)
	 * Counting starts at 1
	 * @param proteinId The protein id to search
	 * @param residue The residue to search for
	 * @return
	 * @throws Exception
	 */
	public static Collection<Integer> getPositionsOfResidueForProteinId( int proteinId, String residue ) throws Exception {
		
		String proteinSequence = YRC_NRSEQUtils.getSequence( proteinId );
		
		Collection<Integer> positions = getPositionsOfResidueForProteinSequence( proteinSequence, residue );
				
		return positions;		
	}
	
	/**
	 * Find and return all positions that correspond to the supplied residue (e.g. K for lysine)
	 * Counting starts at 1
	 * @param proteinSequence The protein sequence to search
	 * @param residue The residue to search for
	 * @return
	 * @throws Exception
	 */
	public static Collection<Integer> getPositionsOfResidueForProteinSequence( String proteinSequence, String residue ) throws Exception {

		Collection<Integer> positions = new HashSet<Integer>();
		
		int index = proteinSequence.indexOf( residue );
		while (index >= 0) {
		    positions.add( index + 1 );
		    index = proteinSequence.indexOf( residue, index + 1);
		}
		
		return positions;		
	}
	
	
	
}
