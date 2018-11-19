package org.yeastrc.xlink.www.web_utils;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
/**
 * 
 *
 */
public class ProteinListingTooltipConfigUtil {
	
	private static final Logger log = Logger.getLogger(ProteinListingTooltipConfigUtil.class);
	private ProteinListingTooltipConfigUtil() { }
	public static ProteinListingTooltipConfigUtil getInstance() { return new ProteinListingTooltipConfigUtil(); }
	/**
	 * 
	 * 
	 * @param projectSearchIdsSet
	 * @param request
	 * @throws Exception 
	 */
	public void putProteinListingTooltipConfigForPage( Collection<Integer> projectSearchIdsSet, HttpServletRequest request ) throws Exception {
		try {
			request.setAttribute( "searchIdsForProteinListing", projectSearchIdsSet );
			String protein_listing_webservice_base_url = 
					ConfigSystemCaching.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.PROTEIN_LISTING_FROM_SEQUENCE_TAXONOMY_WEBSERVICE_URL_KEY );
			if ( protein_listing_webservice_base_url != null && ( ! protein_listing_webservice_base_url.isEmpty() ) ) {
				request.setAttribute( "protein_listing_webservice_base_url_set", "Y" );
			}
		} catch ( Exception e ) {
			String msg = "Error in putProteinListingTooltipConfigForPage(...)";
			log.error( msg, e );
			throw e;
		}
	}
}
