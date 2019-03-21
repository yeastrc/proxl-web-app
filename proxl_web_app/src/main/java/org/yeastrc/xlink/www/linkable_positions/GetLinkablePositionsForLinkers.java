package org.yeastrc.xlink.www.linkable_positions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.linker_data_processing_base.ILinker_Main;
import org.yeastrc.xlink.linker_data_processing_base.ILinkers_Main_ForSingleSearch;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.linkable_positions.ILinker_Main_Objects_ForSearchId_Cached.ILinker_Main_Objects_ForSearchId_Cached_Response;

/**
 * 
 *
 */
public class GetLinkablePositionsForLinkers {

	private static final Logger log = Logger.getLogger( GetLinkablePositionsForLinkers.class );
	
	/**
	 * @param proteinSequence
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public static Set<Integer> getLinkablePositionsForProteinSequenceAndSearchId( String proteinSequence, int searchId ) throws Exception {

		ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response =
				ILinker_Main_Objects_ForSearchId_Cached.getInstance().getSearchLinkers_ForSearchId_Response( searchId );
		
		ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch =
				iLinker_Main_Objects_ForSearchId_Cached_Response.getiLinkers_Main_ForSingleSearch();
		
		return getLinkablePositionsForProteinSequenceAndSearchIdAndILinkers_Main_ForSingleSearch( proteinSequence , searchId, iLinkers_Main_ForSingleSearch );
	}
	
	/**
	 * @param proteinSequence
	 * @param searchId
	 * @param iLinkers_Main_ForSingleSearch
	 * @return
	 * @throws Exception
	 */
	public static Set<Integer> getLinkablePositionsForProteinSequenceAndSearchIdAndILinkers_Main_ForSingleSearch( 
			String proteinSequence, int searchId, ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch ) throws Exception {

		if ( ! iLinkers_Main_ForSingleSearch.isAllLinkersHave_LinkablePositions() ) {
			// Not all linkers have linkable positions so return empty set
			return new HashSet<>(); // EARLY EXIT
		}
		
		List<ILinker_Main> linker_MainList = iLinkers_Main_ForSingleSearch.getLinker_MainList();
		
		if ( linker_MainList == null || linker_MainList.isEmpty() ) {
			String msg = "linker_MainList == null || linker_MainList.isEmpty().  Should not get here. searchId: " + searchId;
			log.error(msg);
			throw new ProxlWebappInternalErrorException(msg);
		}

		Set<Integer> linkablePositions = null;
		
		for( ILinker_Main linker_Main : linker_MainList ) {
			
			Set<Integer> linkablePositionsLocal = linker_Main.getLinkablePositions( proteinSequence );
			
			if ( linkablePositions == null ) {
				linkablePositions = linkablePositionsLocal;
			} else {
				linkablePositions.addAll( linkablePositionsLocal );
			}
		}

		return linkablePositions;
	}


}
