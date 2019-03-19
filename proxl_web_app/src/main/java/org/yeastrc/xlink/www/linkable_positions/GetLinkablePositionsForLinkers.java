package org.yeastrc.xlink.www.linkable_positions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.Get_BuiltIn_Linker_From_Abbreviation_Factory;
import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin.ILinker_Builtin_Linker;

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

		List<ILinker_Builtin_Linker> linkerObjects = new ArrayList<>( linkerAbbrSet.size() );
		for( String linkerAbbr : linkerAbbrSet ) {
			
			ILinker_Builtin_Linker linker = Get_BuiltIn_Linker_From_Abbreviation_Factory.getLinkerForAbbr( linkerAbbr );
			
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
		
		for ( ILinker_Builtin_Linker linkerObject : linkerObjects ) {
			
			Collection<Integer> linkablePositionsForLinker = linkerObject.getLinkablePositions( proteinSequence );
			
			linkablePositions.addAll( linkablePositionsForLinker );
		}

		return linkablePositions;
	}


}
