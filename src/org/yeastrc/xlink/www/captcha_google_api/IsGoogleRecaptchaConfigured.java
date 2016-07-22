package org.yeastrc.xlink.www.captcha_google_api;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;

/**
 * 
 *
 */
public class IsGoogleRecaptchaConfigured {

	private static final Logger log = Logger.getLogger(IsGoogleRecaptchaConfigured.class);


	private static final IsGoogleRecaptchaConfigured instance = new IsGoogleRecaptchaConfigured();

	private IsGoogleRecaptchaConfigured() { }
	public static IsGoogleRecaptchaConfigured getInstance() { return instance; }


	/**
	 * @return
	 */
	public boolean isGoogleRecaptchaConfigured() {

		try {

			String siteKey = ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.GOOGLE_RECAPTCHA_SITE_KEY_KEY );
			
			String secretKey = ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.GOOGLE_RECAPTCHA_SITE_KEY_KEY );
			
			if ( siteKey != null && secretKey != null ) {
				
				if ( StringUtils.isNotEmpty( siteKey.trim() ) && StringUtils.isNotEmpty( secretKey.trim() ) ) {
				
					return true;
				}
			}
			
			return false;

		} catch ( Exception e ) {
			
			String msg = "Exception getting configSystem value for getGoogleRecaptchaSiteCode()";
			log.error( msg, e );

			return false;
		}
	}
	
}
