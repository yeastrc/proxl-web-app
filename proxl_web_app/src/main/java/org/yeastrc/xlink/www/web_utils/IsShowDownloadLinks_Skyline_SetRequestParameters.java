package org.yeastrc.xlink.www.web_utils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.linker_data_processing_base.ILinker_Main;
import org.yeastrc.xlink.linker_data_processing_base.ILinkers_Main_ForSingleSearch;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.linkable_positions.ILinker_Main_Objects_ForSearchId_Cached;
import org.yeastrc.xlink.www.linkable_positions.ILinker_Main_Objects_ForSearchId_Cached.ILinker_Main_Objects_ForSearchId_Cached_Response;
import org.yeastrc.xlink.www.searcher.LinkerPerSearchCleavedCrosslinkMass_Searcher;

/**
 * Should the Download Links be shown for either Skyline format
 * 
 * Should the Download Link be shown for Skyline Shulman format
 * 
 * 
 *
 */
public class IsShowDownloadLinks_Skyline_SetRequestParameters {

	private static final Logger log = Logger.getLogger(IsShowDownloadLinks_Skyline_SetRequestParameters.class);
	private static final IsShowDownloadLinks_Skyline_SetRequestParameters instance = new IsShowDownloadLinks_Skyline_SetRequestParameters();
	private IsShowDownloadLinks_Skyline_SetRequestParameters() { }
	public static IsShowDownloadLinks_Skyline_SetRequestParameters getInstance() { return instance; }
	
	/**
	 * Should the Download Links be shown for either Skyline format
	 * 
	 * Should the Download Link be shown for Skyline Shulman format
	 * 
	 * request param will be set if param request is not null
	 * 
	 * @param searchIds
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	public void isShowDownloadLinks_Skyline_SetRequestParameters( Collection<Integer> searchIds, HttpServletRequest request ) throws Exception {
		
		if ( searchIds == null || searchIds.isEmpty() ) {
			throw new IllegalArgumentException( "searchIds == null || searchIds.isEmpty()" );
		}

		if ( request == null ) {
			throw new IllegalArgumentException( "request == null" );
		}
		
//		log.warn( "TEMP: Commented Out.  Functionality disabled.");
//		
//		// TODO TEMP Commented out
		
		//  Need to look at download code to determine what filtering required
		
		
		request.setAttribute( WebConstants.PARAMETER_SHOW_DOWNLOAD_LINKS_SKYLINE, true );
		
		isShowDownloadLinks_Skyline_ShulmanFormat( searchIds, request );
	}
	
	/**
	 * @param searchIds
	 * @param request
	 * @throws Exception 
	 */
	private void isShowDownloadLinks_Skyline_ShulmanFormat( Collection<Integer> searchIds, HttpServletRequest request ) throws Exception {
		
		
		//  Not supported when have LinkerPerSearchCleavedCrosslinkMassDTO records
		{
			boolean foundcleavedCrosslinkMass = false;

			for ( Integer searchId : searchIds ) {
				try {
					//  Get cleaved_crosslink_mass records for search id
					List<LinkerPerSearchCleavedCrosslinkMassDTO> cleavedCrosslinkMassList_FromDB = LinkerPerSearchCleavedCrosslinkMass_Searcher.getInstance().getForSearchId( searchId );
					if ( ! cleavedCrosslinkMassList_FromDB.isEmpty() ) {
						foundcleavedCrosslinkMass = true;
						break;
					}
				} catch (Exception e) {
					log.error( "Failed getting LinkerPerSearchCleavedCrosslinkMassDTO for search id: " + searchId, e );
					throw e;
				}
			}

			if ( foundcleavedCrosslinkMass ) {
				//  Not supported when have LinkerPerSearchCleavedCrosslinkMassDTO records

				return;  //  EARLY EXIT
			}
		}
		
		{
			//  All Linkers must have Crosslink Chemical Formula
			
			boolean foundCrosslinkerChemicalFormulasForAllSearchesAllLinkers = true;

			ILinker_Main_Objects_ForSearchId_Cached iLinker_Main_Objects_ForSearchId_Cached = ILinker_Main_Objects_ForSearchId_Cached.getInstance(); 
	
			for ( Integer searchId : searchIds ) {
	
				ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response =
						iLinker_Main_Objects_ForSearchId_Cached.getSearchLinkers_ForSearchId_Response( searchId );
				ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch = iLinker_Main_Objects_ForSearchId_Cached_Response.getiLinkers_Main_ForSingleSearch();
				
				List<ILinker_Main> iLinkers_Main_List = iLinkers_Main_ForSingleSearch.getLinker_MainList();
				if ( iLinkers_Main_List == null || iLinkers_Main_List.isEmpty() ) {
					String msg = "iLinkers_Main_List == null || iLinkers_Main_List.isEmpty(), searchId: " + searchId ;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				for ( ILinker_Main iLinker_Main : iLinkers_Main_List ) {
					Set<String> crosslinkFormulasSet = iLinker_Main.getCrosslinkFormulas();
					
					if ( crosslinkFormulasSet == null || crosslinkFormulasSet.isEmpty() ) {
						
						//  No Chemical Formula for this linker
						foundCrosslinkerChemicalFormulasForAllSearchesAllLinkers = false;
						break;  //  EARLY LOOP EXIT
					}
				}

				if ( ! foundCrosslinkerChemicalFormulasForAllSearchesAllLinkers ) {
					//     Exit since cannot create this download.  Check for flag is false next and exit
					break;  //  EARLY LOOP EXIT
				}
			}
			if ( ! foundCrosslinkerChemicalFormulasForAllSearchesAllLinkers ) {
				//  NOT All Linkers have Crosslink Chemical Formula so do NOT set Request Param for display download Skyline Shulman link
				
				return;  //  EARLY EXIT
			}
		}
	
		//  Set request attribute so  display download Skyline Shulman link
		
		request.setAttribute( WebConstants.PARAMETER_SHOW_DOWNLOAD_LINK_SKYLINE_SHULMAN, true );
		
	}
}
