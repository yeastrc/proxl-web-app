package org.yeastrc.xlink.www.web_utils;


import javax.servlet.http.HttpServletRequest;

import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;


/**
 * This class is for putting data in the "request" scope for the page header 
 *
 * This is for Config Data for webservice for tooltips
 * 
 * This is a companion to createTooltipForProteinNames.js 
 *   and needs to be called on every page createTooltipForProteinNames.js is included on
 *
 */
public class GetProteinListingTooltipConfigData {
	
	
	private static final GetProteinListingTooltipConfigData instance = new GetProteinListingTooltipConfigData();

	private GetProteinListingTooltipConfigData() { }
	public static GetProteinListingTooltipConfigData getInstance() { return instance; }
	
		
	/**
	 * @param request
	 * @throws Exception 
	 */
	public void getProteinListingTooltipConfigData( HttpServletRequest request ) throws Exception {
		

		String protein_listing_webservice_base_url = 
				ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.PROTEIN_LISTING_WEBSERVICE_URL_KEY );
				
		request.setAttribute( "protein_listing_webservice_base_url", protein_listing_webservice_base_url );
		
		
		
//		<input type="hidden" id="protein_listing_webservice_base_url" value="<c:out value="${ protein_listing_webservice_base_url }"></c:out>"> 

		
		
	}
}
