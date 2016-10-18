package org.yeastrc.xlink.www.web_utils;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;



/**
 * 
 *
 */
public class IsTermsOfServiceEnabled {

	private static final Logger log = Logger.getLogger(IsTermsOfServiceEnabled.class);



	private static final IsTermsOfServiceEnabled instance = new IsTermsOfServiceEnabled();

	private IsTermsOfServiceEnabled() { }
	public static IsTermsOfServiceEnabled getInstance() { return instance; }


	/**
	 * @return
	 */
	public boolean isTermsOfServiceEnabled() {
		
		
		try {

			//  Is terms of service enabled?
			String termsOfServiceEnabledString =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.TERMS_OF_SERVICE_ENABLED );

			boolean termsOfServiceEnabled = false;

			if ( ConfigSystemsValuesSharedConstants.TRUE.equals(termsOfServiceEnabledString) ) {

				termsOfServiceEnabled = true;
			}

			return termsOfServiceEnabled;

		} catch ( Exception e ) {
			
			String msg = "Exception getting configSystem value for isTermsOfServiceEnabled()";
			log.error( msg, e );

			return false;
		}
	}

}
