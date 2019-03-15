package org.yeastrc.xlink.www.web_utils;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.linkable_positions.Get_BuiltIn_Linker_From_Abbreviation_Factory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SearchLinker_ForSearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SearchLinker_ForSearchId_Response;

/**
 * 
 *
 */
public class AllLinkerAbbrHaveILinkerObjectsForSearchUtil {

	private static final Logger log = Logger.getLogger(AllLinkerAbbrHaveILinkerObjectsForSearchUtil.class);
	//  private constructor
	private AllLinkerAbbrHaveILinkerObjectsForSearchUtil() { }
	public static AllLinkerAbbrHaveILinkerObjectsForSearchUtil getInstance() { 
		return new AllLinkerAbbrHaveILinkerObjectsForSearchUtil(); 
	}

	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public boolean is_allLinkerAbbrHaveILinkerObjectsForSearchUtil( int searchId ) throws Exception {

		Cached_SearchLinker_ForSearchId cached_Linkers_ForSearchId = Cached_SearchLinker_ForSearchId.getInstance();
		SearchLinker_ForSearchId_Response linkers_ForSearchId_Response =
				cached_Linkers_ForSearchId.getSearchLinkers_ForSearchId_Response( searchId );
		List<String>  linkerAbbreviationList = linkers_ForSearchId_Response.getLinkerAbbreviationsForSearchIdList();
		if ( linkerAbbreviationList == null || linkerAbbreviationList.isEmpty() ) {
			String msg = "No linker abbreviations found for Search Id: " + searchId;
			log.error( msg );
			//			throw new Exception(msg);
		} else {
			for ( String linkerAbbreviation : linkerAbbreviationList ) {

				ILinker linker = Get_BuiltIn_Linker_From_Abbreviation_Factory.getLinkerForAbbr( linkerAbbreviation );
				
				if( linker == null ) {

					//  No ILinker linker for linkerAbbr 
					
					return false;  // EARLY EXIT
				}
			}
		}
		
		return true;
	}
}
