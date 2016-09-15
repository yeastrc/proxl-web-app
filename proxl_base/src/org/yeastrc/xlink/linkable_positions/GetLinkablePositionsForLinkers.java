package org.yeastrc.xlink.linkable_positions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;

public class GetLinkablePositionsForLinkers {


	/**
	 * @param proteinSequence
	 * @param linkerAbbrSet
	 * @return
	 * @throws Exception
	 */
	public static Set<Integer> getLinkablePositionsForProteinSequenceAndLinkerAbbrSet( String proteinSequence, Set<String> linkerAbbrSet ) throws Exception {

		Set<Integer> linkablePositions = new HashSet<>();

		for ( String linkerAbbr : linkerAbbrSet ) {

			ILinker linker = GetLinkerFactory.getLinkerForAbbr( linkerAbbr );
			
			Collection<Integer> linkablePositionsForLinker = linker.getLinkablePositions( proteinSequence );
			
			linkablePositions.addAll( linkablePositionsForLinker );
		}

		return linkablePositions;
	}


}
