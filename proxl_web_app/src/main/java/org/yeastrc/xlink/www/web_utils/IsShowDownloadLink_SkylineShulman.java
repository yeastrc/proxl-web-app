package org.yeastrc.xlink.www.web_utils;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.searcher.LinkerPerSearchCleavedCrosslinkMass_Searcher;

/**
 * Should the Download Link be shown for Skyline Shulman format
 * 
 * 
 *
 */
public class IsShowDownloadLink_SkylineShulman {

	private static final Logger log = Logger.getLogger(IsShowDownloadLink_SkylineShulman.class);
	private static final IsShowDownloadLink_SkylineShulman instance = new IsShowDownloadLink_SkylineShulman();
	private IsShowDownloadLink_SkylineShulman() { }
	public static IsShowDownloadLink_SkylineShulman getInstance() { return instance; }
	
	/**
	 * Should the Download Link be shown for Skyline Shulman format
	 * 
	 * request param will be set if param request is not null
	 * 
	 * @param searchIds
	 * @param request - Optional, request param will be set if this is not null
	 * @return
	 * @throws Exception 
	 */
	public boolean isShowDownloadLink_SkylineShulman( Collection<Integer> searchIds, HttpServletRequest request ) throws Exception {
		
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
			
			if ( request != null ) {
				request.setAttribute( WebConstants.PARAMETER_SHOW_DOWNLOAD_LINK_SKYLINE_SHULMAN, true );
			}
			return true;
		}
		
		return false;
		
	}
}
