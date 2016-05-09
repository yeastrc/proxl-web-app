package org.yeastrc.xlink.www.config_system_table;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;

/**
 * For placement in the Application context of the web app
 * for getting specific config_system configuration values
 * 
 */
public class AppContextConfigSystemValuesRetrieval {


	private static final Logger log = Logger.getLogger(AppContextConfigSystemValuesRetrieval.class);
	
	/**
	 * @return
	 */
	public String getFooterCenterOfPageHTML() {

		try {

			return ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.FOOTER_CENTER_OF_PAGE_HTML_KEY );

		} catch ( Exception e ) {
			
			String msg = "Exception getting configSystem value for getFooterCenterOfPageHTML()";
			log.error( msg, e );

			return("UNABLE_TO_RETRIEVE_VALUE");
		}
	}
}
