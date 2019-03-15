package org.yeastrc.xlink.www.linkable_positions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

		if ( linkerAbbrSet == null || linkerAbbrSet.isEmpty() ) {
			throw new IllegalArgumentException( "linkerAbbrSet == null || linkerAbbrSet.isEmpty()" );
		}
		
		Set<Integer> linkablePositions = new HashSet<>();

		List<ILinker> linkerObjects = new ArrayList<>( linkerAbbrSet.size() );
		for( String linkerAbbr : linkerAbbrSet ) {
			
			ILinker linker = GetLinkerFactory.getLinkerForAbbr( linkerAbbr );
			
			//  linker == null is now a valid response that needs to be handled.
			
			if( linker == null ) {

				//  No ILinker linker for linkerAbbr so no Linkable positions will be computed
				
				return linkablePositions;  //  EARLY RETURN
				
//				throw new Exception( "Invalid linker: " + linkerAbbr );
			}
			
			if ( linker != null ) {
				linkerObjects.add( linker );
			}
		}
		
		for ( ILinker linkerObject : linkerObjects ) {
			
			Collection<Integer> linkablePositionsForLinker = linkerObject.getLinkablePositions( proteinSequence );
			
			linkablePositions.addAll( linkablePositionsForLinker );
		}

		return linkablePositions;
	}


}
