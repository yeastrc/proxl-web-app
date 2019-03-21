package org.yeastrc.xlink.linker_data_processing_base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchRoot;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchSingleLinker;

public class Linker_Main_LinkersForSingleSearch_Factory {

	/**
	 * @param linkersDBDataSingleSearchPerLinker
	 * @return
	 * @throws ProxlBaseDataException 
	 */
	public static ILinkers_Main_ForSingleSearch getILinker_Main( LinkersDBDataSingleSearchRoot linkersDBDataSingleSearchRoot ) throws ProxlBaseDataException {
		
		List<LinkersDBDataSingleSearchSingleLinker> linkersDBDataSingleSearchPerLinkerList = linkersDBDataSingleSearchRoot.getLinkersDBDataSingleSearchPerLinkerList();
		
		List<Linker_Main> linker_MainList = new ArrayList<>( linkersDBDataSingleSearchPerLinkerList.size() );
		
		boolean allLinkersHave_LinkablePositions = true;
		
		for ( LinkersDBDataSingleSearchSingleLinker linkersDBDataSingleSearchSingleLinker : linkersDBDataSingleSearchPerLinkerList ) {
			
			Linker_Main linker_Main = Linker_Main_SingleLinker_Factory.getLinker_Main( linkersDBDataSingleSearchSingleLinker );
			linker_MainList.add( linker_Main );
			
			if ( ! linker_Main.isLinkablePositionsAvailable() ) {
				allLinkersHave_LinkablePositions = false;
			}
		}
		
		List<ILinker_Main> linker_MainList_Final =
				Collections.unmodifiableList( linker_MainList );
		
		Linkers_Main_ForSingleSearch linkers_Main_ForSingleSearch = new Linkers_Main_ForSingleSearch( linker_MainList_Final );
				
		linkers_Main_ForSingleSearch.setAllLinkersHave_LinkablePositions( allLinkersHave_LinkablePositions );
		
		return linkers_Main_ForSingleSearch;
	}
}
