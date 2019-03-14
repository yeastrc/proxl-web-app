package org.yeastrc.xlink.www.web_utils;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.www.constants.WebConstants;
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
		
		boolean allLinkerAbbrHaveILinkerObjectsForSearch = true;
		
		for ( Integer searchId : searchIds ) {
			if ( ! AllLinkerAbbrHaveILinkerObjectsForSearchUtil.getInstance().is_allLinkerAbbrHaveILinkerObjectsForSearchUtil( searchId ) ) {
				allLinkerAbbrHaveILinkerObjectsForSearch = false;
				break; // EXIT LOOP
			}
		}
		
		if ( ! allLinkerAbbrHaveILinkerObjectsForSearch ) {
			//  Not All Linker Abbreviations have ILinker Objects so do NOT set Request Param for display download Skyline Links
			
			return;  //  EARLY EXIT
		}
		
		request.setAttribute( WebConstants.PARAMETER_SHOW_DOWNLOAD_LINKS_SKYLINE, true );
		
		isShowDownloadLinks_Skyline_ShulmanFormat( searchIds, request );
	}
	
	/**
	 * @param searchIds
	 * @param request
	 * @throws Exception 
	 */
	private void isShowDownloadLinks_Skyline_ShulmanFormat( Collection<Integer> searchIds, HttpServletRequest request ) throws Exception {
		
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
		
		if ( ! foundcleavedCrosslinkMass ) {
			
			request.setAttribute( WebConstants.PARAMETER_SHOW_DOWNLOAD_LINK_SKYLINE_SHULMAN, true );
		}
	}
}
